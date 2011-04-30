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
     * Returns the cause of the failed operation if the request has failed.
     * @return The cause of the failure. null if succeeded or this future has
     *      not completed yet.
     */
    public Throwable getCause();

    /**
     * Gets the "caller" status such as whether the caller is not waiting,
     * waiting, or timed out while waiting for a response.
     * @return The status of the caller
     */
    public int getCallerStatus();
    
    /**
     * Checks if the request is finished processing.  This may be true if
     * the request was a success or canceled.
     * @return True if the request is finished.  This can either mean it was
     *      a success or was canceled.
     */
    public boolean isFinished();

    /**
     * Checks if the request was a success.  A request is successful if its
     * finished processing AND a response was received.
     * @return True if the request is finished and a response was received.
     */
    public boolean isSuccess();

    /**
     * Returns true if and only if this was cancelled by a cancel() method. 
     * 
     * Checks if the request was canceled.  A request is canceled if its
     * finished processing AND a response was not received.
     * @return True if the request is finished and a response was NOT received.
     */
    public boolean isCancelled();
    
    
    

    /**
     * Gets the absolute Java datetime for the time the request was made in
     * milliseconds. Usually this is a value created by System.currentTimeMillis()
     * @return The absolute Java datetime of the request
     */
    public long getRequestTime();
    
    /**
     * Gets the absolute Java datetime for the time the request will expire in
     * milliseconds. Usually this is a value created by adding some expiry time
     * onto the requestTime.
     * @return The absolute Java datetime when the request expires.  If less than
     *      1, then an expiration is not set.
     */
    public long getExpiryTime();
    
    /**
     * Returns true if this entry has an expiry time, or false if it doesn't.
     * @return True if this entry has an expiry time, otherwise false.
     */
    public boolean hasExpiryTime();

    

    /**
     * Gets the absolute Java datetime for the time the response was created
     * for this request in milliseconds.  Usually this is a value created by
     * calling System.currentTimeMillis().  This value is used in conjunction
     * with requestTime to calculate how long the entire request & response
     * round trip took.
     * @return The absolute Java datetime of the response
     */
    public long getResponseTime();
    
    

    /**
     * Gets the total amount of time it took to finish processing this request.
     * This is the ResponseTime - RequestTime.  If no response has been
     * received yet, this will be 0.
     * @return
     */
    public long getProcessingTime();

    

}