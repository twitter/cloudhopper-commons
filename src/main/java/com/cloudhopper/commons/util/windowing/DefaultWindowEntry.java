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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Default implemeentation of a WindowEntry.
 * 
 * @author joelauer
 */
public class DefaultWindowEntry<K,R,P> implements WindowEntry<K,R,P> {

    private final K key;
    private final R request;
    private final long requestTime;
    private final AtomicReference<P> response;
    private final AtomicLong responseTime;
    private final AtomicBoolean finished;
    private final AtomicInteger callerStatus;

    protected DefaultWindowEntry(K key, R request, long requestTime, int callerStatus) {
        this.key = key;
        this.request = request;
        this.requestTime = requestTime;
        this.response = new AtomicReference<P>();
        this.responseTime = new AtomicLong(0);
        this.finished = new AtomicBoolean(false);
        this.callerStatus = new AtomicInteger(callerStatus);
    }

    public int getCallerStatus() {
        return this.callerStatus.get();
    }

    public void setCallerStatus(int callerStatus) {
        this.callerStatus.set(callerStatus);
    }

    public K getKey() {
        return this.key;
    }

    public R getRequest() {
        return this.request;
    }

    public long getRequestTime() {
        return this.requestTime;
    }

    public void setResponse(P response) {
        this.response.set(response);
    }

    public P getResponse() {
        return this.response.get();
    }

    public void setResponseTime(long responseTime) {
        this.responseTime.set(responseTime);
    }

    public long getResponseTime() {
        return this.responseTime.get();
    }

    public long getProcessingTime() {
        if (this.getResponse() == null) {
            return 0;
        }
        return (this.responseTime.get() - this.requestTime);
    }

    public boolean isCancelled() {
        return (isFinished() && getResponse() == null);
    }

    protected void finished() {
        this.finished.set(true);
    }

    public boolean isFinished() {
        return this.finished.get();
    }

    public boolean isSuccess() {
        return (isFinished() && getResponse() != null);
    }


}