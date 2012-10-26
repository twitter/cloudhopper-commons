package com.cloudhopper.commons.util.windowing;

/*
 * #%L
 * ch-commons-util
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Default implementation of a WindowFuture.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DefaultWindowFuture<K,R,P> implements WindowFuture<K,R,P> {

    private final WeakReference<Window> window;
    private final ReentrantLock windowLock;
    private final Condition completedCondition;
    private final K key;
    private final R request;
    private final AtomicReference<P> response;
    private final AtomicReference<Throwable> cause;
    private final AtomicInteger callerStateHint;
    private final AtomicBoolean done;
    private final long originalOfferTimeoutMillis;
    private final int windowSize;
    private final long offerTimestamp;
    private final long acceptTimestamp;
    private final long expireTimestamp;
    private final AtomicLong doneTimestamp;

    /**
     * Creates a new DefaultWindowFuture.
     * @param window The window that created this future.  Saved as a weak
     *      reference to prevent circular references.
     * @param windowLock The shared lock from the window
     * @param completedCondition The shared condition to wait on
     * @param key The key of the future
     * @param request The request of the future
     * @param callerStateHint The initial state of the caller hint
     * @param originalOfferTimeoutMillis
     * @param windowSize Size of the window after this request was added. Useful
     *      for calculating an estimated response time for this request rather
     *      than all requests ahead of it in the window.
     * @param offerTimestamp The timestamp when the request was offered
     * @param acceptTimestamp The timestamp when the request was accepted
     * @param expireTimestamp The timestamp when the request will expire or -1
     *      if no expiration is set
     */
    protected DefaultWindowFuture(Window window, ReentrantLock windowLock, Condition completedCondition, K key, R request, int callerStateHint, long originalOfferTimeoutMillis, int windowSize, long offerTimestamp, long acceptTimestamp, long expireTimestamp) {
        this.window = new WeakReference<Window>(window);
        this.windowLock = windowLock;
        this.completedCondition = completedCondition;
        this.key = key;
        this.request = request;
        this.response = new AtomicReference<P>();
        this.cause = new AtomicReference<Throwable>();
        this.callerStateHint = new AtomicInteger(callerStateHint);
        this.done = new AtomicBoolean(false);
        this.originalOfferTimeoutMillis = originalOfferTimeoutMillis;
        this.windowSize = windowSize;
        this.offerTimestamp = offerTimestamp;
        this.acceptTimestamp = acceptTimestamp;
        this.expireTimestamp = expireTimestamp;
        this.doneTimestamp = new AtomicLong(0);    
    }
    
    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public R getRequest() {
        return this.request;
    }

    @Override
    public P getResponse() {
        return this.response.get();
    }

    @Override
    public Throwable getCause() {
        return this.cause.get();
    }
    
    @Override
    public int getCallerStateHint() {
        return this.callerStateHint.get();
    }

    public void setCallerStateHint(int callerState) {
        this.callerStateHint.set(callerState);
    }
    
    @Override
    public boolean isCallerWaiting() {
        return (this.callerStateHint.get() == CALLER_WAITING);
    }
    
    @Override
    public int getWindowSize() {
        return this.windowSize;
    }

    @Override
    public boolean hasExpireTimestamp() {
        return (this.expireTimestamp > 0);
    }
    
    @Override
    public long getExpireTimestamp() {
        return this.expireTimestamp;
    }

    @Override
    public long getOfferTimestamp() {
        return this.offerTimestamp;
    }

    @Override
    public long getAcceptTimestamp() {
        return this.acceptTimestamp;
    }
    
    @Override
    public boolean hasDoneTimestamp() {
        return (this.doneTimestamp.get() > 0);
    }

    @Override
    public long getDoneTimestamp() {
        return this.doneTimestamp.get();
    }
    
    @Override
    public long getOfferToAcceptTime() {
        return (this.acceptTimestamp - this.offerTimestamp);
    }

    @Override
    public long getOfferToDoneTime() {
        if (this.done.get()) {
            return (this.doneTimestamp.get() - this.offerTimestamp);
        } else {
            return -1;
        }
    }

    @Override
    public long getAcceptToDoneTime() {
        if (this.done.get()) {
            return (this.doneTimestamp.get() - this.acceptTimestamp);
        } else {
            return -1;
        }
    }
    
    @Override
    public boolean isDone() {
        return this.done.get();
    }
    
    private void lockAndSignalAll() {
        // notify any waiters that we're done
        windowLock.lock();
        try {
            completedCondition.signalAll();
        } finally {
            windowLock.unlock();
        }
    }
    
    @Override
    public boolean isSuccess() {
        return (this.done.get() && this.response.get() != null);
    }
    
    @Override
    public void complete(P response) {
        complete(response, System.currentTimeMillis());
    }
    
    @Override
    public void complete(P response, long doneTimestamp) {
        completeHelper(response, doneTimestamp);
        safelyRemoveRequestInWindow();
        lockAndSignalAll();
    }
    
    private void safelyRemoveRequestInWindow() {
        Window window0 = this.window.get();
        if (window0 == null) {
            // hmm.. this means the window was garbage collected (uh oh)
        } else {
            window0.removeHelper(key);
        }
    }
    
    void completeHelper(P response, long doneTimestamp) {
        if (response == null) {
            throw new IllegalArgumentException("A response cannot be null if trying to complete()");
        }
        if (doneTimestamp <= 0) {
            throw new IllegalArgumentException("A valid doneTime must be > 0 if trying to complete()");
        }
        // set to done, but don't handle duplicate calls
        if (this.done.compareAndSet(false, true)) {
            this.response.set(response);
            this.doneTimestamp.set(doneTimestamp);
        }
    }
    
    @Override
    public void fail(Throwable t) {
        fail(t, System.currentTimeMillis());
    }
    
    @Override
    public void fail(Throwable t, long doneTimestamp) {
        failedHelper(t, doneTimestamp);
        safelyRemoveRequestInWindow();
        lockAndSignalAll();
    }
    
    void failedHelper(Throwable t, long doneTimestamp) {
        if (t == null) {
            throw new IllegalArgumentException("A response cannot be null if trying to failed()");
        }
        if (doneTimestamp <= 0) {
            throw new IllegalArgumentException("A valid doneTimestamp must be > 0 if trying to failed()");
        }
        // set to done, but don't handle duplicate calls
        if (this.done.compareAndSet(false, true)) {
            this.cause.set(t);
            this.doneTimestamp.set(doneTimestamp);
        }
    }
    
    @Override
    public boolean isCancelled() {
        return (this.done.get() && this.response.get() == null && this.cause.get() == null);
    }
    
    @Override
    public void cancel() {
        cancel(System.currentTimeMillis());
    }
    
    @Override
    public void cancel(long doneTimestamp) {
        cancelHelper(doneTimestamp);
        safelyRemoveRequestInWindow();
        lockAndSignalAll();
    }
    
    void cancelHelper(long doneTimestamp) {
        if (doneTimestamp <= 0) {
            throw new IllegalArgumentException("A valid doneTimestamp must be > 0 if trying to cancel()");
        }
        // set to done, but don't handle duplicate calls
        if (this.done.compareAndSet(false, true)) {
            this.doneTimestamp.set(doneTimestamp);
        }
    }
    
    @Override
    public boolean await() throws InterruptedException {
        // wait for only offerTimeoutMillis - offerToAcceptTime
        long remainingTimeoutMillis = this.originalOfferTimeoutMillis - this.getOfferToAcceptTime();
        return this.await(remainingTimeoutMillis);
        
    }
    
    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        // k, if someone actually calls this method -- make sure to set the flag
        // this may have already been set earlier, but if not its safe to set here
        this.setCallerStateHint(CALLER_WAITING);
        
        // if already done, return immediately
        if (isDone()) {
            return true;
        }
        
        long startTime = System.currentTimeMillis();
        // try to acquire lock within given amount of time
        if (!windowLock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
            this.setCallerStateHint(CALLER_WAITING_TIMEOUT);
            return false;
        }
        
        try {
            // keep waiting until we're done
            while (!isDone()) {
                // current "waitTime" is ("now" - startTime)
                long waitingTime = System.currentTimeMillis() - startTime;
                if (waitingTime >= timeoutMillis) {
                    // caller intended on waiting, but timed out while waiting for a response
                    this.setCallerStateHint(CALLER_WAITING_TIMEOUT);
                    return false;
                }
                // calculate the amount of timeout remaining
                long remainingWaitTime = timeoutMillis - waitingTime;
                // await for a signal that a response was received
                // NOTE: this signal may be sent multiple times and not apply to us necessarily
                completedCondition.await(remainingWaitTime, TimeUnit.MILLISECONDS);
            }
        } finally {
            windowLock.unlock();
        }
        
        return true;
    }
}