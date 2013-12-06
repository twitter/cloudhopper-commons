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
 * Interface for listening to events triggered by a window.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public interface WindowListener<K,R,P> {

    /**
     * Called when a future has been automatically expired by a window in its
     * internal monitoring task.  Since the thread that will call this method
     * is potentially shared by any window in the JVM, it's best to handle this
     * event as quickly as possible.
     * @param future The future that expired based on its ExpireTimestamp
     */
    public void expired(WindowFuture<K,R,P> future);

}
