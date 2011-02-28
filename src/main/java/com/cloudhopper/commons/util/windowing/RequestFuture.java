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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Wraps a request and response entry used in a Window.  Useful for waiting
 * for a response to a request.
 * 
 * @author joelauer
 */
public class RequestFuture<K,R,P> extends WindowEntryWrapper<K,R,P> {

    private final long waitTime;          // original "waitTime" included with the original request
    private final ReentrantLock windowLock;
    private final Condition responseReceivedCondition;

    protected RequestFuture(DefaultWindowEntry<K,R,P> entry, long waitTime, ReentrantLock windowLock, Condition responseReceivedCondition) {
        super(entry);
        this.waitTime = waitTime;
        // references to the lock and condition we wait on
        this.windowLock = windowLock;
        this.responseReceivedCondition = responseReceivedCondition;
    }

    /**
     * Interruptibly waits for a response to be received for this request.
     * Will wait for the remaining amount of time determined by the original
     * Window.addRequest() waitTime parameter that generated this future.
     * Since waiting for an open slot in a "window" is counted against this
     * total wait time, this method will only be able to wait for remaining
     * amount of time.
     * @return The response received and matched to this request.
     * @throws ResponseTimeoutException Thrown if no response is received
     *      within the permitted timeout.
     * @throws RequestCancelledException Thrown if the request is cancelled
     *      either before or while we are waiting for a response.
     * @throws InterruptedException Thrown if the calling thread is interrupted
     *      while we are attempting to acquire the lock inside the "window".
     */
    public P await() throws ResponseTimeoutException, RequestCancelledException, InterruptedException {
        // k, if someone actually calls this method -- make sure to set the flag
        // this may have already been set earlier, but if not its safe to set here
        this.entry.setCallerStatus(CALLER_WAITING);

        // check if its finished first
        if (isFinished()) {
            if (isCancelled()) {
                throw new RequestCancelledException("Request was cancelled");
            } else {
                return getResponse();
            }
        }
        
        // synchronize against same lock in this object
        windowLock.lockInterruptibly();
        try {
            // wait until we're finished
            while (!isFinished()) {
                // current "waitTime" is (now-requestTime)
                long currentWaitTime = System.currentTimeMillis() - entry.getRequestTime();
                if (currentWaitTime >= waitTime) {
                    // if we get here, then the caller intended on waiting, but
                    // we timed out while waiting for a response
                    this.entry.setCallerStatus(CALLER_WAITING_TIMEOUT);
                    throw new ResponseTimeoutException("Unable to get a response to this request within [" + waitTime + "] ms");
                }
                // calculate the amount of timeout remaining
                long remainingWaitTime = waitTime - currentWaitTime;
                // await for a signal that a response was received
                // NOTE: this signal may be sent multiple times and not apply to us necessarily
                responseReceivedCondition.await(remainingWaitTime, TimeUnit.MILLISECONDS);
            }

            // if we got here then we're finished
            if (isCancelled()) {
                throw new RequestCancelledException("Request was cancelled");
            } else {
                return getResponse();
            }
        } finally {
            windowLock.unlock();
        }
    }

}
