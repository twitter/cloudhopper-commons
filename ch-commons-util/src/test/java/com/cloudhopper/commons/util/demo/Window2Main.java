package com.cloudhopper.commons.util.demo;

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

import com.cloudhopper.commons.util.windowing.WindowListener;
import com.cloudhopper.commons.util.windowing.Window;
import com.cloudhopper.commons.util.windowing.WindowFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class Window2Main {
    private static final Logger logger = LoggerFactory.getLogger(Window2Main.class);

    static public void main(String[] args) throws Exception {
        
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        WindowListener listener = new WindowListener<Integer,String,String>() {
            @Override
            public void expired(WindowFuture<Integer, String, String> entry) {
                logger.debug("The key=" + entry.getKey() + ", request=" + entry.getRequest() + " expired");
            }
        };
        
        Window<Integer,String,String> window = new Window<Integer,String,String>(2, executor, 5000, listener);
        Window<Integer,String,String> window2 = new Window<Integer,String,String>(2, executor, 5000, listener, "window2monitor");
        
        WindowFuture<Integer,String,String> future0 = window.offer(0, "Request0", 1000, 4000);
        logger.info("Request0 offered at " + future0.getOfferTimestamp() + " and expires at " + future0.getExpireTimestamp());
        //requestFuture.await();

        System.out.println("Press any key to add another request...");
        System.in.read();

        WindowFuture<Integer,String,String> future1 = window2.offer(1, "Request1", 1000, 4000);
        logger.info("Request1 offered at " + future1.getOfferTimestamp() + " and expires at " + future1.getExpireTimestamp());

        System.out.println("Press any key to add response...");
        System.in.read();

        logger.info("Adding Response1...");
        WindowFuture<Integer,String,String> responseFuture1 = window.complete(1, "Response1");
        if (responseFuture1 == null) {
            logger.info("Request1 was not present in window");
        } else {
            logger.info(responseFuture1.getRequest());
            logger.info(responseFuture1.getResponse());
        }

        /**
        // add a response
        ResponseFuture<Integer,String,String> responseFuture0 = requestWindow.addResponse(0, "Response0");
        logger.info(responseFuture0.getRequest());
        logger.info(responseFuture0.getResponse());
        logger.info("Processing Time: " + responseFuture0.getProcessingTime() + " ms");

        // this should timeout dude...
        RequestFuture<Integer,String,String> requestFuture1 = requestWindow.addRequest(1, "Request1", 1000);
        logger.info("Request1 worked");

        ResponseFuture<Integer,String,String> responseFuture1 = requestWindow.addResponse(1, "Response1");
        logger.info(responseFuture1.getRequest());
        logger.info(responseFuture1.getResponse());
        logger.info("Processing Time: " + responseFuture1.getProcessingTime() + " ms");
         */
        
        System.out.println("Press any key to get rid of our reference to Window");
        System.in.read();
        
        window.destroy();
        window2.destroy();
        window = null;
        window2 = null;
        System.gc();
        
        System.out.println("Press any key to exit...");
        System.in.read();
        
        executor.shutdown();
    }
    
}
