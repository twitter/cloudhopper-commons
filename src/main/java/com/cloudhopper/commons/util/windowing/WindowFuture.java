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
 * @author joelauer
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
    
    
    public boolean hasExpireTimestamp();
    
    public long getExpireTimestamp();
    
    public long getOfferTimestamp();
    
    public long getAcceptTimestamp();
    
    public long getOfferToAcceptTime();
    
    public boolean hasDoneTimestamp();
    
    public long getDoneTimestamp();
    
    public long getOfferToDoneTime();
    
    public long getAcceptToDoneTime();
    
    public void complete(P response);
    
    public void complete(P response, long doneTimestamp);
    
    public void fail(Throwable t);
    
    public void fail(Throwable t, long doneTimestamp);
    
    public void cancel();
    
    public void cancel(long doneTimestamp);
    
    /**
     * Waits for this future to be completed within the specified time limit.
     * @param timeoutMillis The amount of milliseconds to wait
     * @return True if and only if the future was completed within the specified
     *      time limit 
     * @throws InterruptedException Thrown if the current thread was interrupted
     */
    public boolean await(long timeoutMillis) throws InterruptedException;
}