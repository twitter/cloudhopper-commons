/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cloudhopper.commons.util.windowing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/**
 * A utility class to support "windowed" protocols that permit requests to be
 * sent asynchronously and the responses to be processed at a later time.
 * <BR>
 * Windowed protocols generally provide high throughput over high latency
 * links such as TCP/IP connections since they allow requests one after each
 * other without waiting for a response back right away.
 * <BR>
 * The "window" is the amount of unacknowledged requests that are permitted to
 * be outstanding/unacknowledged at any given time.  This implementation
 * allows a "windowSize" to be defined during construction.  This represents
 * the number of open "slots".  When a response is received, it's up to the
 * user of this class to make sure that response is added so that any threads
 * waiting for a response are properly notified.
 * <BR>
 * This class is also good to use to fetch the original request when a response
 * is asynchronously received in a different thread.
 * 
 * @author joelauer
 */
public class Window<K,R,P> {
    private static final Logger logger = Logger.getLogger(Window.class);

    private final ConcurrentHashMap<K,DefaultWindowEntry<K,R,P>> pendingRequests;
    private final int windowSize;
    private final ReentrantLock windowLock;
    private final Condition responseReceivedCondition;
    // number of threads waiting for a slot
    private AtomicInteger slotWaitingSize;
    private AtomicBoolean slotWaitingCanceled;
    // for scheduling tasks (such as expiring requests)
    private final ScheduledExecutorService executor;
    private final ScheduledFuture<?> executorHandle;
    private final WindowMonitor monitor;
    private final long period;
    private final CopyOnWriteArrayList<WindowListener<K,R,P>> listeners;

    /**
     * Creates a new window with the specified max window size.  This
     * constructor disables any automatic recurring tasks from being executed.
     * @param windowSize The maximum number of pending requests permitted to
     *      be outstanding (unacknowledged) at a given time.  Must be > 0.
     */
    public Window(int windowSize) {
        this(windowSize, null, 0, null);
    }
    
    /**
     * Creates a new window with the specified max window size along with
     * specifications for an executor to perform periodic maintenance such
     * as expiring requests.
     * @param windowSize The maximum number of pending requests permitted to
     *      be outstanding (unacknowledged) at a given time.  Must be > 0.
     * @param executor The scheduled executor service to use to schedule
     *      recurring tasks such as checking if requests have expired.
     * @param period The number of milliseconds between periodic maintenance
     *      tasks to be executed.
     * @param listener An listener to send window events to (such as when
     *      requests expire).
     */
    public Window(int windowSize, ScheduledExecutorService executor, long period, WindowListener<K,R,P> listener) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be > 0");
        }
        this.pendingRequests = new ConcurrentHashMap<K,DefaultWindowEntry<K,R,P>>(windowSize*2);
        this.windowSize = windowSize;
        this.windowLock = new ReentrantLock();
        this.responseReceivedCondition = this.windowLock.newCondition();
        this.slotWaitingSize = new AtomicInteger(0);
        this.slotWaitingCanceled = new AtomicBoolean(false);
        this.executor = executor;
        this.period = period;
        this.listeners = new CopyOnWriteArrayList<WindowListener<K,R,P>>();
        if (listener != null) {
            this.listeners.add(listener);
        }
        if (this.executor != null) {
            this.monitor = new WindowMonitor();
            this.executorHandle = this.executor.scheduleWithFixedDelay(this.monitor, this.period, this.period, TimeUnit.MILLISECONDS);
        } else {
            this.monitor = null;
            this.executorHandle = null;
        }
    }
    
    private class WindowMonitor implements Runnable {
        @Override
        public void run() {
            logger.debug("WindowMonitor running, current pendingSize=" + getPendingSize());
            List<WindowEntry<K,R,P>> expiredRequests = cancelAllExpiredRequests();
            if (expiredRequests != null && expiredRequests.size() > 0) {
                logger.debug("Found " + expiredRequests.size() + " that expired");
                // process each expired request and pass up the chain to handlers
                for (WindowEntry<K,R,P> entry : expiredRequests) {
                    for (WindowListener<K,R,P> listener : listeners) {
                        try {
                            listener.requestExpired(entry);
                        } catch (Throwable t) {
                            logger.error("Uncaught exception thrown in listener: ", t);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the window size.  The window size is the max number of requests
     * that can be pending (unacknowledged/do not have responses yet).
     * @return The window size
     */
    public int getWindowSize() {
        return this.windowSize;
    }

    /**
     * Gets the current number of requests that do not yet have a response.
     * @return The current number of pending requests
     */
    public int getPendingSize() {
        return this.pendingRequests.size();
    }
    
    /**
     * Gets the current number of callers/threads that are waiting for a slot
     * to free up in the window.
     */
    public int getSlotWaitingSize() {
        return this.slotWaitingSize.get();
    }
    
    private void beginSlotWaiting() {
        this.slotWaitingSize.incrementAndGet();
    }
    
    /**
     * Indicates slot waiting is finished. If "slotWaitingCanceled" was set to
     * true, this method takes care of resetting that flag back to false once
     * all waiters have called this method.
     * @return True if waiting should be terminated early or false if it is ok
     *      to continue waiting.
     */
    private boolean endSlotWaiting() {
        int newValue = this.slotWaitingSize.decrementAndGet();
        // if newValue reaches zero, make sure to always reset "slotWaitersCanceled"
        if (newValue == 0) {
            // if slotWaitingCanceled was true, then reset it back to false, and
            // return true to make sure the caller knows to cancel waiting
            return this.slotWaitingCanceled.compareAndSet(true, false);
        } else {
            // if slotWaitingCanceled is true, then return true
            return this.slotWaitingCanceled.get();
        }
    }

    /**
     * Checks if a pending request exists by its key.
     * @param key The key for the request (such as a sequence number)
     * @return True if the pending request exists or false if it doesn't
     */
    public boolean containsRequest(K key) {
        return this.pendingRequests.containsKey(key);
    }

    /**
     * Creates an ordered map view of all pending requests.  The entries will
     * be sorted by its key in ascending order.  A new underlying map is created
     * by calling this method, so call it once.
     * @return A new map instance representing all pending requests sorted by
     *      the ascending order of its key.
     */
    public Map<K,WindowEntry<K,R,P>> getPendingRequests() {
        Map<K,WindowEntry<K,R,P>> requests = new TreeMap<K,WindowEntry<K,R,P>>();
        for (DefaultWindowEntry<K,R,P> value : this.pendingRequests.values()) {
            requests.put(value.getKey(), value);
        }
        return requests;
    }
    
    /**
     * Adds a request to this "window" if a slot exists.  If the current max
     * window size has already been reached, then this method will wait for
     * a period of time for a slot to open up.  If a slot is open or opens up
     * within the specified period of time, then this method will add that
     * request to the internal pending "map" in an atomic fashion.  A
     * RequestFuture object will be returned so that the caller can optionally
     * wait for a response to be received.  Its safe to discard this "Future"
     * if you don't need to wait for a response.
     * @param key The key for the request (the protocol's sequence number is
     *      a good choice)
     * @param request The request to add to the "window"
     * @param waitTime The amount of time (in milliseconds) to wait for
     *      a slot to open up in this "window".
     * @param expireTime The relative amount of time (in milliseconds) to expire this
     *      request if a response isn't received.  If &lt; 1 then no expiration
     *      will be set.
     * @return An optional "future" object to synchronize against if the caller
     *      wants to "wait" for a response to be received.
     * @throws RequestAlreadyExistsException Thrown as a very serious error if
     *      the key already exists in our "pending" request map.
     * @throws MaxPendingTimeoutException Thrown if there were no open "slots"
     *      and a timeout occurred while waiting for a slot to open up.
     * @throws InterruptedException Thrown if the calling thread is interrupted
     *      and we're currently waiting to acquire the internal "windowLock".
     */
    public RequestFuture addRequest(K key, R request, long waitTime) throws RequestAlreadyExistsException, MaxWindowSizeTimeoutException, InterruptedException {
        return this.addRequest(key, request, waitTime, false, -1);
    }

    /**
     * Adds a request to this "window" if a slot exists.  If the current max
     * window size has already been reached, then this method will wait for
     * a period of time for a slot to open up.  If a slot is open or opens up
     * within the specified period of time, then this method will add that
     * request to the internal pending "map" in an atomic fashion.  A
     * RequestFuture object will be returned so that the caller can optionally
     * wait for a response to be received.  Its safe to discard this "Future"
     * if you don't need to wait for a response.
     * @param key The key for the request (the protocol's sequence number is
     *      a good choice)
     * @param request The request to add to the "window"
     * @param waitTime The amount of time (in milliseconds) to wait for
     *      a slot to open up in this "window".
     * @param expireTime The relative amount of time (in milliseconds) to expire this
     *      request if a response isn't received.  If &lt; 1 then no expiration
     *      will be set.
     * @return An optional "future" object to synchronize against if the caller
     *      wants to "wait" for a response to be received.
     * @throws RequestAlreadyExistsException Thrown as a very serious error if
     *      the key already exists in our "pending" request map.
     * @throws MaxPendingTimeoutException Thrown if there were no open "slots"
     *      and a timeout occurred while waiting for a slot to open up.
     * @throws InterruptedException Thrown if the calling thread is interrupted
     *      and we're currently waiting to acquire the internal "windowLock".
     */
    public RequestFuture addRequest(K key, R request, long waitTime, long expireTime) throws RequestAlreadyExistsException, MaxWindowSizeTimeoutException, InterruptedException {
        return this.addRequest(key, request, waitTime, false, expireTime);
    }

    /**
     * Adds a request to this "window" if a slot exists.  If the current max
     * window size has already been reached, then this method will wait for
     * a period of time for a slot to open up.  If a slot is open or opens up
     * within the specified period of time, then this method will add that
     * request to the internal pending "map" in an atomic fashion.  A
     * RequestFuture object will be returned so that the caller can optionally
     * wait for a response to be received.  Its safe to discard this "Future"
     * if you don't need to wait for a response.
     * @param key The key for the request (the protocol's sequence number is
     *      a good choice)
     * @param request The request to add to the "window"
     * @param waitTime The amount of time (in milliseconds) to wait for
     *      a slot to open up in this "window".
     * @param callerPlanningOnWaiting If true, the "callerStatus" flag of the entry
     *      will be set to "CALLER_WAITING".  If false, the "callerStratus" flag
     *      will be set to "CALLER_NO_WAIT".  Not internally important, but users
     *      of this class can use this status flag to determine whether the caller
     *      is going to wait for a response or if it really is using this more
     *      asynchronously (not waiting).
     * @param expireTime The relative amount of time (in milliseconds) to expire this
     *      request if a response isn't received.  If &lt; 1 then no expiration
     *      will be set.
     * @return An optional "future" object to synchronize against if the caller
     *      wants to "wait" for a response to be received.
     * @throws RequestAlreadyExistsException Thrown as a very serious error if
     *      the key already exists in our "pending" request map.
     * @throws MaxPendingTimeoutException Thrown if there were no open "slots"
     *      and a timeout occurred while waiting for a slot to open up.
     * @throws InterruptedException Thrown if the calling thread is interrupted
     *      and we're currently waiting to acquire the internal "windowLock".
     */
    public RequestFuture addRequest(K key, R request, long waitTime, boolean callerPlanningOnWaiting, long expireTime) throws RequestAlreadyExistsException, MaxWindowSizeTimeoutException, InterruptedException {
        // does this key already exist (MAJOR error)
        if (this.pendingRequests.containsKey(key)) {
            throw new RequestAlreadyExistsException("The key [" + key + "] already exists in our internal map of pending requests");
        }
        
        // is all slot waiting currently canceled? (if so, immediately terminate)
        if (this.slotWaitingCanceled.get()) {
            throw new WaitingTerminatedEarlyException("Slot waiting is currently canceled until all original waiters are terminated");
        }

        // create a timestamp of when this request was "added"
        long requestTime = System.currentTimeMillis();
        long expiryTime = (expireTime > 0 ? (requestTime + expireTime) : -1);

        this.windowLock.lockInterruptibly();
        try {
            // does enough room exist in the "window" for another pending request?
            // NOTE: wait for room up to the waitTime
            // NOTE: multiple signals may be received that need to be ignored
            while (this.pendingRequests.size() >= this.windowSize) {
                // check if there time remaining to wait
                long currentWaitTime = System.currentTimeMillis() - requestTime;
                if (currentWaitTime >= waitTime) {
                    throw new MaxWindowSizeTimeoutException("Max of [" + this.windowSize + "] pending requests reached and unable acquire a free slot within [" + waitTime + "] ms");
                }
                // check if slow waiting was canceled (terminate early)
                if (this.slotWaitingCanceled.get()) {
                    throw new WaitingTerminatedEarlyException("Slot waiting was canceled");
                }
                // calculate the amount of timeout remaining
                long remainingWaitTime = waitTime - currentWaitTime;
                try {
                    // await for a new signal for this max amount of time
                    this.beginSlotWaiting();
                    this.responseReceivedCondition.await(remainingWaitTime, TimeUnit.MILLISECONDS);
                } finally {
                    boolean terminateEarly = this.endSlotWaiting();
                    if (terminateEarly) {
                        throw new WaitingTerminatedEarlyException("Slot waiting was canceled (terminated early)");
                    }
                }
            }

            // if we got here, then there is a slot for our request
            DefaultWindowEntry<K,R,P> value = new DefaultWindowEntry<K,R,P>(key, request, requestTime, (callerPlanningOnWaiting ? WindowEntry.CALLER_WAITING : WindowEntry.CALLER_NO_WAIT), expiryTime);
            this.pendingRequests.put(key, value);
            return new RequestFuture<K,R,P>(value, waitTime, this.windowLock, this.responseReceivedCondition);
        } finally {
            this.windowLock.unlock();
        }
    }
    
    
    /**
     * Terminates all current callers/threads waiting for a slot in the window.
     * @return True if there were currently callers/waiters, otherwise false.
     * @throws InterruptedException Thrown if the calling thread was interrupted
     *      while waiting to obtain the window lock.
     */
    public boolean terminateSlotWaiters() throws InterruptedException {
        this.windowLock.lockInterruptibly();
        try {
            if (this.slotWaitingSize.get() > 0) {
                this.slotWaitingCanceled.set(true);
                this.responseReceivedCondition.signalAll();
                return true;
            } else {
                return false;
            }
        } finally {
            this.windowLock.unlock();
        }
    }
    

    /**
     * Adds a response to this "window" if a corresponding request exists.
     * If no request matches this response (by its key), then this method will
     * return null.  If a request is matched, it is returned wrapped inside
     * a ResponseFuture object to be used by the caller to help finish processing.
     * <BR>
     * If a request is matched, any threads waiting for a response are signaled
     * so that they can continue processing.
     * @param key The key for the response which should match its originating
     *      request (the protocol's sequence number is a good choice)
     * @param response The response to set and match to the original request.
     *      Using null as a response has the effect of "canceling" the request.
     * @return An optional "future" object which includes the original request
     *      that this response matches.  If no request is matched, this method
     *      will return null.  In that case, either the original request either
     *      timed out, was canceled, or this really is a totally unexpected response.
     * @throws InterruptedException Thrown if the calling thread is interrupted
     *      and we're currently waiting to acquire the internal "windowLock".
     */
    public ResponseFuture addResponse(K key, P response) throws InterruptedException {
        return addResponse(key, response, null);
    }
    
    private ResponseFuture addResponse(K key, P response, Throwable cause) throws InterruptedException {
        // does this key even exist?
        if (!this.pendingRequests.containsKey(key)) {
            return null;
        }

        // create a timestamp of when this response was "added"
        long responseTime = System.currentTimeMillis();

        this.windowLock.lockInterruptibly();
        try {
            // remove the request from our "pending" map
            DefaultWindowEntry<K,R,P> value = this.pendingRequests.remove(key);

            // if the entry was null, then somehow it got removed between our
            // initial check at the start of this method and acquiring the lock
            if (value == null) {
                return null;
            }

            // this means a reply was specifically received for the original request
            value.setResponse(response);
            value.setResponseTime(responseTime);
            if (cause != null) {
                value.setCause(cause);
            }
            value.finished();

            // let all "waiters" know a response was received and processed
            this.responseReceivedCondition.signalAll();

            // return the answer
            return new ResponseFuture<K,R,P>(value);
        } finally {
            this.windowLock.unlock();
        }
    }

    /**
     * Cancels and removes a request from this window.  If any thread is waiting
     * on this request, they'll be signaled and see that the response was
     * canceled.  This effectively sets the request as finished, but has the
     * response set as a null object.
     * @param key The key for the request (the protocol's sequence number is
     *      a good choice)
     * @return The ResponseFuture object that is a wrapper around the original
     *      request.  This can be safely discard and ignored if not needed.
     * @throws InterruptedException Thrown if the calling thread is interrupted
     *      and we're currently waiting to acquire the internal "windowLock".
     */
    public ResponseFuture cancelRequest(K key) throws InterruptedException {
        // cancelling a request is as easy as adding a null response
        return this.cancelRequest(key, null);
    }
    
    /**
     * Cancels and removes a request from this window.  If any thread is waiting
     * on this request, they'll be signaled and see that the response was
     * canceled.  This effectively sets the request as finished, but has the
     * response set as a null object.
     * @param key The key for the request (the protocol's sequence number is
     *      a good choice)
     * @param cause If an exception is the reason a request is canceled, the
     *      underlying cause can be set here.
     * @return The ResponseFuture object that is a wrapper around the original
     *      request.  This can be safely discard and ignored if not needed.
     * @throws InterruptedException Thrown if the calling thread is interrupted
     *      and we're currently waiting to acquire the internal "windowLock".
     */
    public ResponseFuture cancelRequest(K key, Throwable cause) throws InterruptedException {
        // cancelling a request is as easy as adding a null response
        return this.addResponse(key, null, cause);
    }

    /**
     * Cancels all pending requests, removes
     * @return
     */
    public List<WindowEntry<K,R,P>> cancelAllRequests() {
        // we'll create an array list of requests we cancelled
        List<WindowEntry<K,R,P>> cancelledRequests = new ArrayList<WindowEntry<K,R,P>>(this.pendingRequests.size());

        // create a timestamp of when this response was "added"
        long responseTime = System.currentTimeMillis();

        this.windowLock.lock();
        try {
            // update every pending request and "cancel" it
            for (DefaultWindowEntry<K,R,P> value : this.pendingRequests.values()) {
                // this means a reply was specifically received for the original request
                value.setResponse(null);
                value.setResponseTime(responseTime);
                value.finished();
                // add this value to our list of cancelled requests
                cancelledRequests.add(value);
            }

            // clear everything
            this.pendingRequests.clear();

            // let all "waiters" know that requests were cancelled
            this.responseReceivedCondition.signalAll();
        } finally {
            this.windowLock.unlock();
        }
        
        return cancelledRequests;
    }
    
    
    public List<WindowEntry<K,R,P>> cancelAllExpiredRequests() {
        // if there aren't any pending requests, nothing to expire
        if (this.pendingRequests.size() <= 0) {
            return null;
        }
        
        logger.debug("cancelAllExpiredRequests called...");
        
        List<WindowEntry<K,R,P>> expiredRequests = new ArrayList<WindowEntry<K,R,P>>();
        long now = System.currentTimeMillis();
        this.windowLock.lock();
        try {
            // check every request this window contains and see if it's expired
            for (DefaultWindowEntry<K,R,P> value : this.pendingRequests.values()) {
                if (value.hasExpiryTime() && now >= value.getExpiryTime()) {
                    expiredRequests.add(value);
                    // expire it
                    value.setResponse(null);
                    value.setResponseTime(now);
                    value.finished();
                }
            }
            
            // do any requests need expired?
            if (expiredRequests.size() > 0) {
                // take all expired requests and remove them from the pendingRequests
                for (WindowEntry<K,R,P> entry : expiredRequests) {
                    this.pendingRequests.remove(entry.getKey());
                }
                // let all "waiters" know that their request may have been expired
                this.responseReceivedCondition.signalAll();
            }
        } finally {
            this.windowLock.unlock();
        }
        return expiredRequests;
    }
}
