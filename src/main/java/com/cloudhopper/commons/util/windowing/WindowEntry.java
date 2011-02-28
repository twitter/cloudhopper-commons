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
 *
 * @author joelauer
 */
public interface WindowEntry<K,R,P> {

    /** The caller is not waiting on this entry */
    static public final int CALLER_NO_WAIT = 0;
    /** The caller is waiting on this entry */
    static public final int CALLER_WAITING = 1;
    /** The caller was waiting, but gave up (timeout) */
    static public final int CALLER_WAITING_TIMEOUT = 2;

    public K getKey();

    public R getRequest();

    public long getRequestTime();

    public P getResponse();

    public long getResponseTime();

    /**
     * Gets the "caller" status such as whether the caller is not waiting,
     * waiting, or timed out while waiting for a response.
     * @return The status of the caller
     */
    public int getCallerStatus();

    /**
     * Gets the total amount of time it took to finish processing this request.
     * This is the ResponseTime - RequestTime.  If no response has been
     * received yet, this will be 0.
     * @return
     */
    public long getProcessingTime();

    /**
     * Checks if the request is finished processing.  This may be true if
     * the request was a success or cancelled.
     * @return True if the request is finished.  This can either mean it was
     *      a success or was cancelled.
     */
    public boolean isFinished();

    /**
     * Checks if the request was a success.  A request is successful if its
     * finished processing AND a response was received.
     * @return True if the request is finished and a response was received.
     */
    public boolean isSuccess();

    /**
     * Checks if the request was cancelled.  A request is cancelled if its
     * finished processing AND a response was not received.
     * @return True if the request is finished and a response was NOT received.
     */
    public boolean isCancelled();

}