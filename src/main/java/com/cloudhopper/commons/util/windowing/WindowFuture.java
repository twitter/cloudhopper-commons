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

/**
 * A {@link WindowFuture} is either <em>uncompleted</em> or <em>completed</em>.
 * When an operation begins, a new future object is created.  The new future
 * is uncompleted initially - it is neither succeeded, failed, nor cancelled
 * because the operation is not finished yet.  If the operation is
 * finished either successfully, with failure, or by cancellation, the future is
 * marked as completed with more specific information, such as the cause of the
 * failure.  Please note that even failure and cancellation belong to the
 * completed state.
 * <pre>
 *                                      +---------------------------+
 *                                      | Completed successfully    |
 *                                      +---------------------------+
 *                                 +---->      isDone() = <b>true</b>      |
 * +--------------------------+    |    |   isSuccess() = <b>true</b>      |
 * |        Uncompleted       |    |    +===========================+
 * +--------------------------+    |    | Completed with failure    |
 * |      isDone() = false    |    |    +---------------------------+
 * |   isSuccess() = false    |----+---->   isDone() = <b>true</b>         |
 * | isCancelled() = false    |    |    | getCause() = <b>non-null</b>     |
 * |    getCause() = null     |    |    +===========================+
 * +--------------------------+    |    | Completed by cancellation |
 *                                 |    +---------------------------+
 *                                 +---->      isDone() = <b>true</b>      |
 *                                      | isCancelled() = <b>true</b>      |
 *                                      +---------------------------+
 * </pre>
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public interface WindowFuture<K,R,P> {

    /** The caller is not waiting on this entry */
    static public final int CALLER_NOT_WAITING = 0;
    /** The caller is waiting on this entry */
    static public final int CALLER_WAITING = 1;
    /** The caller was waiting, but gave up (timeout) */
    static public final int CALLER_WAITING_TIMEOUT = 2;

    /**
     * Gets the key of the window entry.
     * @return The key of the window entry.
     */
    public K getKey();

    /**
     * Gets the request contained in the window entry.
     * @return The request contained in the window entry.
     */
    public R getRequest();
    
    /**
     * Gets the response associated with the window entry.
     * @return The response associated with he request or null if no response
     *      has been received.
     */
    public P getResponse();
    
    /**
     * Returns {@code true} if and only if this future is
     * complete, regardless of whether the operation was successful, failed,
     * or cancelled.
     */
    public boolean isDone();

    /**
     * Returns {@code true} if and only if the operation was completed
     * successfully.
     */
    public boolean isSuccess();
    
    /**
     * Returns the cause of the failed operation if the operation has
     * failed.
     *
     * @return the cause of the failure.
     *         {@code null} if succeeded or this future is not
     *         completed yet.
     */
    public Throwable getCause();
    
    /**
     * Returns {@code true} if and only if this future was cancelled by a 
     * cancel() method.
     */
    public boolean isCancelled();

    /**
     * Gets a hint of the caller state such as whether the caller is waiting, not
     * waiting, or timed out while waiting for completion.
     * @return The hint of the state of the caller
     */
    public int getCallerStateHint();
    
    /**
     * Returns {@code true} if and only if the caller hinted at "WAITING" for
     * completion.  Returns {@code false} if the caller either did not hint
     * at planning on "WAITING" or did wait but timed out and gave up. Please
     * note that even if this returns true, it does not mean the caller is
     * actively waiting since this merely represents a hint.
     */
    public boolean isCallerWaiting();
    
    /**
     * Returns the size of the window (number of requests in it) after this
     * request was added.  Useful for calculating an estimated response time
     * just for this request.
     * @return The size of the window after this request was added.
     */
    public int getWindowSize();
    
    /**
     * Returns true if an expire timestamp value exists (&gt; 0).
     * @return True if an expire timestamp exists
     */
    public boolean hasExpireTimestamp();
    
    /**
     * Gets the expire timestamp in milliseconds.  The expire timestamp is when
     * the request expires unless this optional field was not set.
     * @return The expire timestamp or &lt;= 0 if it doesn't exist.
     * @see #hasExpireTimestamp() 
     */
    public long getExpireTimestamp();
    
    /**
     * Gets the offer timestamp in milliseconds.  The offer timestamp is when
     * the request was offered for acceptance to the window.
     * @return The offer timestamp
     */
    public long getOfferTimestamp();
    
    /**
     * Gets the accept timestamp in milliseconds. The accept timestamp is when
     * the request was accepted by the window.
     * @return The accept timestamp
     */
    public long getAcceptTimestamp();
    
    /**
     * Gets the amount of time (in ms) from offer to accept.
     * @return The amount of time from offer to accept
     */
    public long getOfferToAcceptTime();
    
    /**
     * Returns true if a done timestamp value exists (&gt; 0).
     * @return True if a done timestamp exists
     */
    public boolean hasDoneTimestamp();
    
    /**
     * Gets the done timestamp in milliseconds. The done timestamp is when
     * the request has been completed.
     * @return The done timestamp
     */
    public long getDoneTimestamp();
    
    /**
     * Gets the amount of time (in ms) from offer to done or -1 if a done
     * timestamp does not yet exist.
     * @return The amount of time from offer to done
     */
    public long getOfferToDoneTime();
    
    /**
     * Gets the amount of time (in ms) from accept to done or -1 if a done
     * timestamp does not yet exist.
     * @return The amount of time from accept to done
     */
    public long getAcceptToDoneTime();
    
    /**
     * Completes (as a success) a request by setting the response. This method
     * will set the done timestamp to System.currentTimeMillis().
     * @param response The response for the associated request
     */
    public void complete(P response);
    
    /**
     * Completes (as a success) a request by setting the response.
     * @param response The response for the associated request
     * @param doneTimestamp The timestamp when the request completed
     */
    public void complete(P response, long doneTimestamp);
    
    /**
     * Completes (as a failure) a request by setting a throwable as the cause
     * of failure. This method will set the done timestamp to System.currentTimeMillis()
     * @param t The throwable as the cause of failure
     */
    public void fail(Throwable t);
    
    /**
     * Completes (as a failure) a request by setting a throwable as the cause
     * of failure.
     * @param t The throwable as the cause of failure
     * @param doneTimestamp The timestamp when the request failed
     */
    public void fail(Throwable t, long doneTimestamp);
    
    /**
     * Completes (as a cancel) a request. This method will set the done timestamp
     * to System.currentTimeMillis().
     */
    public void cancel();
    
    /**
     * Completes (as a cancel) a request.
     * @param doneTimestamp The timestamp when the request was cancelled
     */
    public void cancel(long doneTimestamp);
    
    /**
     * Waits for this future to be completed within the amount of time remaining
     * from the original offerTimeoutMillis minus the amount of time it took
     * for the Window to accept the offer. For example, if Window.offer was
     * called with an offerTimeoutMillis of 5000 milliseconds and it took 
     * 1000 milliseconds for the Window to accept the offer, then this method
     * would wait for 4000 milliseconds.
     * @return True if and only if the future was completed within the specified
     *      time limit 
     * @throws InterruptedException Thrown if the current thread was interrupted
     */
    public boolean await() throws InterruptedException;
    
    /**
     * Waits for this future to be completed within the specified time limit.
     * @param timeoutMillis The amount of milliseconds to wait
     * @return True if and only if the future was completed within the specified
     *      time limit 
     * @throws InterruptedException Thrown if the current thread was interrupted
     */
    public boolean await(long timeoutMillis) throws InterruptedException;
}