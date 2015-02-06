package com.cloudhopper.jetty;

/*
 * #%L
 * ch-jetty
 * %%
 * Copyright (C) 2012 - 2015 Cloudhopper by Twitter
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

import java.util.concurrent.ThreadPoolExecutor;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

/**
 * Jetty ThreadPool exposing the underlying ThreadPoolExecutor. Use with care.
 * This class wraps a ThreadPoolExecutor as a ThreadPool.
 * @author garth
 */
public class JettyExecutorThreadPool extends ExecutorThreadPool {
    private final ThreadPoolExecutor executor;

    public JettyExecutorThreadPool(ThreadPoolExecutor executor) {
	super(executor);
	this.executor = executor;
    }

    public ThreadPoolExecutor getExecutor() {
	return this.executor;
    }

}
