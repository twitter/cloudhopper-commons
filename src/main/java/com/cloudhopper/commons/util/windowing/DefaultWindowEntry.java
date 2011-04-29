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
 * Default implementation of a WindowEntry.
 * 
 * @author joelauer
 */
public class DefaultWindowEntry<K,R,P> implements WindowEntry<K,R,P> {

    private final K key;
    private final R request;
    private final long requestTime;
    private final AtomicReference<P> response;
    private final AtomicReference<Throwable> cause;
    private final AtomicLong responseTime;
    private final AtomicBoolean finished;
    private final AtomicInteger callerStatus;

    protected DefaultWindowEntry(K key, R request, long requestTime, int callerStatus) {
        this.key = key;
        this.request = request;
        this.requestTime = requestTime;
        this.response = new AtomicReference<P>();
        this.cause = new AtomicReference<Throwable>();
        this.responseTime = new AtomicLong(0);
        this.finished = new AtomicBoolean(false);
        this.callerStatus = new AtomicInteger(callerStatus);
    }

    @Override
    public int getCallerStatus() {
        return this.callerStatus.get();
    }

    public void setCallerStatus(int callerStatus) {
        this.callerStatus.set(callerStatus);
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
    public long getRequestTime() {
        return this.requestTime;
    }

    public void setResponse(P response) {
        this.response.set(response);
    }

    @Override
    public P getResponse() {
        return this.response.get();
    }

    public void setResponseTime(long responseTime) {
        this.responseTime.set(responseTime);
    }

    @Override
    public long getResponseTime() {
        return this.responseTime.get();
    }

    @Override
    public long getProcessingTime() {
        if (this.getResponse() == null) {
            return 0;
        }
        return (this.responseTime.get() - this.requestTime);
    }

    @Override
    public boolean isCancelled() {
        return (isFinished() && getResponse() == null);
    }

    protected void finished() {
        this.finished.set(true);
    }

    @Override
    public boolean isFinished() {
        return this.finished.get();
    }

    @Override
    public boolean isSuccess() {
        return (isFinished() && getResponse() != null);
    }

    public void setCause(Throwable cause) {
        this.cause.set(cause);
    }

    @Override
    public Throwable getCause() {
        return this.cause.get();
    }

}