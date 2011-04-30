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

package com.cloudhopper.commons.util.demo;

import com.cloudhopper.commons.util.windowing.RequestFuture;
import com.cloudhopper.commons.util.windowing.ResponseFuture;
import com.cloudhopper.commons.util.windowing.Window;
import org.apache.log4j.Logger;

/**
 * Simple demo of window utility class.
 * 
 * @author joelauer
 */
public class WindowMain {
    private static final Logger logger = Logger.getLogger(WindowMain.class);

    static public void main(String[] args) throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(1);

        RequestFuture<Integer,String,String> requestFuture0 = requestWindow.addRequest(0, "Request0", 1000);
        logger.info("Request0 worked");
        //requestFuture.await();

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

        // cancel a request with an exception cause
        RequestFuture<Integer,String,String> requestFuture2 = requestWindow.addRequest(2, "Request2", 1000);
        ResponseFuture<Integer,String,String> responseFuture2 = requestWindow.cancelRequest(2, new Exception("Test Cause"));
        logger.info("Request2 Processing Time: " + responseFuture2.getProcessingTime() + " ms");
        logger.info("Request2 Canceled? " + responseFuture2.isCancelled());
        logger.info("Request2 Cause: ", responseFuture2.getCause());
        
        
        System.out.println("Press any key to exit...");
        System.in.read();
    }
    
}
