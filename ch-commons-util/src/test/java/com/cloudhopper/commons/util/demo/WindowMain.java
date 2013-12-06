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

import com.cloudhopper.commons.util.windowing.Window;
import com.cloudhopper.commons.util.windowing.WindowFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple demo of window utility class.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class WindowMain {
    private static final Logger logger = LoggerFactory.getLogger(WindowMain.class);

    static public void main(String[] args) throws Exception {
        Window<Integer,String,String> window = new Window<Integer,String,String>(1);

        WindowFuture<Integer,String,String> future = window.offer(0, "Request0", 1000);
        logger.info("Request0 worked");
        //requestFuture.await();

        // add a response
        WindowFuture<Integer,String,String> future0 = window.complete(0, "Response0");
        logger.info(future0.getRequest());
        logger.info(future0.getResponse());

        // this should timeout dude...
        WindowFuture<Integer,String,String> future1 = window.offer(1, "Request1", 1000);
        logger.info("Request1 worked");

        WindowFuture<Integer,String,String> future2 = window.complete(1, "Response1");
        logger.info(future2.getRequest());
        logger.info(future2.getResponse());

        // cancel a request with an exception cause
        WindowFuture<Integer,String,String> future3 = window.offer(2, "Request2", 1000);
        Thread.sleep(100);
        WindowFuture<Integer,String,String> future4 = window.fail(2, new Exception("Test Cause"));
        logger.info("Request2 Done? " + future4.isDone());
        logger.info("Request2 Success? " + future4.isSuccess());
        logger.info("Request2 Cancelled? " + future4.isCancelled());
        logger.info("Request2 Cause: ", future4.getCause());
        logger.info("Request2 OfferToAccept: " + future4.getOfferToAcceptTime());
        logger.info("Request2 OfferToDone: " + future4.getOfferToDoneTime());
        logger.info("Request2 AcceptToDone: " + future4.getAcceptToDoneTime());
        
        
        System.out.println("Press any key to exit...");
        System.in.read();
    }
    
}
