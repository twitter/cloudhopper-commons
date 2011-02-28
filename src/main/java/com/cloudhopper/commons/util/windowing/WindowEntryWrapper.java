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
public class WindowEntryWrapper<K,R,P> implements WindowEntry<K,R,P> {

    protected final DefaultWindowEntry<K,R,P> entry;

    protected WindowEntryWrapper(DefaultWindowEntry<K,R,P> entry) {
        this.entry = entry;
    }

    public int getCallerStatus() {
        return this.entry.getCallerStatus();
    }

    public K getKey() {
        return this.entry.getKey();
    }

    public R getRequest() {
        return this.entry.getRequest();
    }

    public P getResponse() {
        return this.entry.getResponse();
    }

    public long getRequestTime() {
        return this.entry.getRequestTime();
    }

    public long getResponseTime() {
        return this.entry.getResponseTime();
    }

    public boolean isFinished() {
        return this.entry.isFinished();
    }

    public long getProcessingTime() {
        return this.entry.getProcessingTime();
    }

    public boolean isSuccess() {
        return this.entry.isSuccess();
    }

    public boolean isCancelled() {
        return this.entry.isCancelled();
    }

}