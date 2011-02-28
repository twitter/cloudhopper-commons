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

import com.cloudhopper.commons.util.windowing.ExpiredRequestListener;
import com.cloudhopper.commons.util.windowing.RequestFuture;
import com.cloudhopper.commons.util.windowing.ResponseFuture;
import com.cloudhopper.commons.util.windowing.Window;
import com.cloudhopper.commons.util.windowing.WindowEntry;
import org.apache.log4j.Logger;

/**
 *
 * @author joelauer
 */
public class Window2Main {
    private static final Logger logger = Logger.getLogger(Window2Main.class);

    static public void main(String[] args) throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(2);

        // try out the expired request reaper...
        requestWindow.enableExpiredRequestReaper(new ExpiredRequestListener<Integer,String,String>() {
            @Override
            public void requestExpired(WindowEntry<Integer, String, String> entry) {
                logger.debug("The key=" + entry.getKey() + ", request=" + entry.getRequest() + " expired");
            }
        }, 5000);

        RequestFuture<Integer,String,String> requestFuture0 = requestWindow.addRequest(0, "Request0", 1000);
        logger.info("Request0 added");
        //requestFuture.await();

        System.out.println("Press any key to add another request...");
        System.in.read();

        RequestFuture<Integer,String,String> requestFuture1 = requestWindow.addRequest(1, "Request1", 1000);
        logger.info("Request1 added");

        System.out.println("Press any key to add response...");
        System.in.read();

        ResponseFuture<Integer,String,String> responseFuture1 = requestWindow.addResponse(1, "Response1");
        logger.info(responseFuture1.getRequest());
        logger.info(responseFuture1.getResponse());
        logger.info("Processing Time: " + responseFuture1.getProcessingTime() + " ms");


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

        System.out.println("Press any key to exit...");
        System.in.read();
    }
    
}
