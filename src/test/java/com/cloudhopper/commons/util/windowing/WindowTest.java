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

// third party imports
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.apache.log4j.Logger;

// my imports
//import net.cloudhopper.commons.util.ByteBuffer;

/**
 * Tests Windowing class and packages.
 * @author joelauer
 */
public class WindowTest {
    private static final Logger logger = Logger.getLogger(WindowTest.class);

    @Test
    public void sizes() throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(1);
        Assert.assertEquals(1, requestWindow.getWindowSize());
        Assert.assertEquals(0, requestWindow.getPendingSize());

        RequestFuture<Integer,String,String> requestFuture0 = requestWindow.addRequest(0, "Request0", 100);

        Assert.assertEquals(1, requestWindow.getWindowSize());
        Assert.assertEquals(1, requestWindow.getPendingSize());

        requestWindow.cancelRequest(0);

        Assert.assertEquals(1, requestWindow.getWindowSize());
        Assert.assertEquals(0, requestWindow.getPendingSize());

        RequestFuture<Integer,String,String> requestFuture1 = requestWindow.addRequest(1, "Request1", 100);

        Assert.assertEquals(1, requestWindow.getWindowSize());
        Assert.assertEquals(1, requestWindow.getPendingSize());

        requestWindow.addResponse(1, "Response1");

        Assert.assertEquals(1, requestWindow.getWindowSize());
        Assert.assertEquals(0, requestWindow.getPendingSize());
    }

    @Test
    public void requestFutureAndResponseFuture() throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(1);
        Integer i = new Integer(1);
        String request = "Request"+i;

        RequestFuture<Integer,String,String> requestFuture = requestWindow.addRequest(i, request, 100);
        Assert.assertEquals(new Integer(i), requestFuture.getKey());
        Assert.assertEquals(request, requestFuture.getRequest());
        Assert.assertEquals(true, requestFuture.getRequestTime() > 0);
        Assert.assertEquals(null, requestFuture.getResponse());
        Assert.assertEquals(0, requestFuture.getResponseTime());
        Assert.assertEquals(false, requestFuture.isCancelled());
        Assert.assertEquals(false, requestFuture.isFinished());
        Assert.assertEquals(false, requestFuture.isSuccess());
        Assert.assertEquals(0, requestFuture.getProcessingTime());

        try {
            // this should timeout waiting for a response
            requestFuture.await();
            Assert.fail();
        } catch (ResponseTimeoutException e) {
            // correct behavior
        }

        // mimic a response is received
        String response = "Response"+i;
        ResponseFuture<Integer,String,String> responseFuture = requestWindow.addResponse(i, response);
        Assert.assertEquals(new Integer(i), responseFuture.getKey());
        Assert.assertEquals(request, responseFuture.getRequest());
        Assert.assertEquals(true, responseFuture.getRequestTime() > 0);
        Assert.assertEquals(response, responseFuture.getResponse());
        Assert.assertEquals(true, responseFuture.getResponseTime() > 0);
        Assert.assertEquals(false, requestFuture.isCancelled());
        Assert.assertEquals(true, requestFuture.isFinished());
        Assert.assertEquals(true, requestFuture.isSuccess());
        Assert.assertEquals(true, requestFuture.getProcessingTime() > 0);

        // this should succeed now since a response was received
        String response0 = requestFuture.await();
        Assert.assertEquals(response, response0);
        Assert.assertEquals(request, requestFuture.getRequest());
        Assert.assertEquals(true, requestFuture.getRequestTime() > 0);
        Assert.assertEquals(response, requestFuture.getResponse());
        Assert.assertEquals(true, requestFuture.getResponseTime() > 0);
        Assert.assertEquals(false, requestFuture.isCancelled());
        Assert.assertEquals(true, requestFuture.isFinished());
        Assert.assertEquals(true, requestFuture.isSuccess());
        Assert.assertEquals(true, requestFuture.getProcessingTime() > 0);
    }

    @Test
    public void maxWindowSizeTimeoutException() throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(1);
        RequestFuture<Integer,String,String> requestFuture0 = requestWindow.addRequest(0, "Request0", 100);
        try {
            // this should timeout waiting for a slot
            RequestFuture<Integer,String,String> requestFuture1 = requestWindow.addRequest(1, "Request1", 100);
            Assert.fail();
        } catch (MaxWindowSizeTimeoutException e) {
            // correct behavior
        }
    }

    @Test
    public void responseTimeoutException() throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(1);
        RequestFuture<Integer,String,String> requestFuture0 = requestWindow.addRequest(0, "Request0", 100);
        try {
            // this should timeout waiting for a response
            requestFuture0.await();
            Assert.fail();
        } catch (ResponseTimeoutException e) {
            // correct behavior
        }
    }

    @Test
    public void requestAlreadyExistsException() throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(1);
        RequestFuture<Integer,String,String> requestFuture0 = requestWindow.addRequest(0, "Request0", 100);
        try {
            RequestFuture<Integer,String,String> requestFuture1 = requestWindow.addRequest(0, "Request0", 100);;
            Assert.fail();
        } catch (RequestAlreadyExistsException e) {
            // correct behavior
        }
    }


    @Test
    public void waitingFlagNotOriginallySetButAddedOnAwait() throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(1);
        RequestFuture<Integer,String,String> requestFuture0 = requestWindow.addRequest(0, "Request0", 100);
        Assert.assertEquals(WindowEntry.CALLER_NO_WAIT, requestFuture0.getCallerStatus());

        // now the caller was mistaken that they weren't waiting, when they actually
        // start to wait, we'll set the waiting flag to true
        try {
            requestFuture0.await();
            Assert.fail();
        } catch (Exception e) {
            // correct behavior
        }

        Assert.assertEquals(WindowEntry.CALLER_WAITING_TIMEOUT, requestFuture0.getCallerStatus());
    }



    @Test
    public void cancelRequest() throws Exception {
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(1);
        Integer i = new Integer(1);
        String request = "Request"+i;

        RequestFuture<Integer,String,String> requestFuture = requestWindow.addRequest(i, request, 100);
        
        // cancel it
        ResponseFuture<Integer,String,String> responseFuture = requestWindow.cancelRequest(i);
        Assert.assertEquals(new Integer(i), responseFuture.getKey());
        Assert.assertEquals(request, responseFuture.getRequest());
        Assert.assertEquals(true, responseFuture.getRequestTime() > 0);
        Assert.assertEquals(null, responseFuture.getResponse());
        Assert.assertEquals(true, responseFuture.getResponseTime() > 0);
        Assert.assertEquals(true, requestFuture.isCancelled());
        Assert.assertEquals(true, requestFuture.isFinished());
        Assert.assertEquals(false, requestFuture.isSuccess());
        Assert.assertEquals(0, requestFuture.getProcessingTime());

        try {
            // this should timeout waiting for a response
            requestFuture.await();
            Assert.fail();
        } catch (RequestCanceledException e) {
            // correct behavior
        }
    }


    @Test
    public void cancelAllRequests() throws Exception {
        int count = 5;
        Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(count);
        String[] requests = new String[count];
        for (int i = 0; i < count; i++) {
            requests[i] = "Request" + i;
        }

        for (int i = 0; i < 3; i++) {
            requestWindow.addRequest(i, requests[i], 100);
        }

        Assert.assertEquals(3, requestWindow.getPendingSize());

        // are there requests pending?
        List<WindowEntry<Integer,String,String>> cancelledRequests = requestWindow.cancelAllRequests();

        Assert.assertEquals(0, requestWindow.getPendingSize());
        Assert.assertEquals(3, cancelledRequests.size());

        for (int i = 0; i < 3; i++) {
            WindowEntry<Integer,String,String> value = cancelledRequests.get(i);
            Assert.assertEquals("Request"+value.getKey(), value.getRequest());
            Assert.assertEquals(true, value.getRequestTime() > 0);
            Assert.assertEquals(null, value.getResponse());
            Assert.assertEquals(true, value.getResponseTime() > 0);
            Assert.assertEquals(true, value.isCancelled());
            Assert.assertEquals(true, value.isFinished());
            Assert.assertEquals(false, value.isSuccess());
        }
    }

    public static class RequestThread extends Thread {

        private Window<Integer,String,String> requestWindow;
        private BlockingQueue<Integer> requestQueue;
        public int id;
        public int requestsPerThread;
        public Throwable throwable;

        public RequestThread(Window<Integer,String,String> requestWindow, BlockingQueue<Integer> requestQueue, int id, int requestsPerThread) {
            this.requestWindow = requestWindow;
            this.requestQueue = requestQueue;
            this.id = id;
            this.requestsPerThread = requestsPerThread;
        }

        @Override
        public void run() {
            try {
                for (int x = 0; x < requestsPerThread; x++) {
                    Integer i = Integer.valueOf(""+id+""+x);
                    String request = "Request"+i;
 //                   logger.debug("adding request " + i);
                    RequestFuture<Integer,String,String> requestFuture = requestWindow.addRequest(i, request, 100);
                    Assert.assertEquals(i, requestFuture.getKey());
                    Assert.assertEquals(request, requestFuture.getRequest());
                    Assert.assertEquals(true, requestFuture.getRequestTime() > 0);
                    Assert.assertEquals(null, requestFuture.getResponse());
                    Assert.assertEquals(0, requestFuture.getResponseTime());
                    Assert.assertEquals(false, requestFuture.isCancelled());
                    Assert.assertEquals(false, requestFuture.isFinished());
                    Assert.assertEquals(false, requestFuture.isSuccess());
                    Assert.assertEquals(0, requestFuture.getProcessingTime());

                    requestQueue.add(i);
                    requestFuture.await();

                    Assert.assertEquals(request, requestFuture.getRequest());
                    Assert.assertEquals(true, requestFuture.getRequestTime() > 0);
                    Assert.assertEquals("Response"+i, requestFuture.getResponse());
                    //Assert.assertEquals(null, requestFuture.getResponse());
                    Assert.assertEquals(true, requestFuture.getResponseTime() > 0);
                    Assert.assertEquals(false, requestFuture.isCancelled());
                    Assert.assertEquals(true, requestFuture.isFinished());
                }
            } catch (Throwable t) {
                logger.error("", t);
                this.throwable = t;
                return;
            }
        }
    }
    
    public static class ResponseThread extends Thread {

        private Window<Integer,String,String> requestWindow;
        private BlockingQueue<Integer> requestQueue;
        public int total;
        public Throwable throwable;

        public ResponseThread(Window<Integer,String,String> requestWindow, BlockingQueue<Integer> requestQueue, int total) {
            this.requestWindow = requestWindow;
            this.requestQueue = requestQueue;
            this.total = total;
        }

        @Override
        public void run() {
            try {
                for (int x = 0; x < total; x++) {
                    //Integer i = new Integer(x);
                    Integer i = requestQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (i == null) {
                        throw new Exception("Integer was null in ResponseThread");
                    }
                    String response = "Response"+i;
 //                   logger.debug("adding response " + i);
                    ResponseFuture<Integer,String,String> responseFuture = requestWindow.addResponse(i, response);
                    Assert.assertEquals(new Integer(i), responseFuture.getKey());
                    Assert.assertEquals("Request"+i, responseFuture.getRequest());
                    Assert.assertEquals(true, responseFuture.getRequestTime() > 0);
                    Assert.assertEquals(response, responseFuture.getResponse());
                    Assert.assertEquals(true, responseFuture.getResponseTime() > 0);
                    Assert.assertEquals(false, responseFuture.isCancelled());
                    Assert.assertEquals(true, responseFuture.isFinished());
                    Assert.assertEquals(true, responseFuture.isSuccess());
                }
            } catch (Throwable t) {
                logger.error("", t);
                this.throwable = t;
                return;
            }
        }
    }


    @Test
    public void simulatedMultithreadedProcessing() throws Exception {
        final Window<Integer,String,String> requestWindow = new Window<Integer,String,String>(5);

        final int requestThreadCount = 8;
        final int requestsPerThread = 10000;
        final BlockingQueue<Integer> requestQueue = new LinkedBlockingQueue<Integer>();

        RequestThread[] requestThreads = new RequestThread[requestThreadCount];
        for (int i = 0; i < requestThreadCount; i++) {
            requestThreads[i] = new RequestThread(requestWindow, requestQueue, i, requestsPerThread);
        }

        ResponseThread responseThread = new ResponseThread(requestWindow, requestQueue, requestThreadCount*requestsPerThread);

        // start 'em
        for (RequestThread requestThread : requestThreads) {
            requestThread.start();
        }
        responseThread.start();

        // wait for them to finish
        for (RequestThread requestThread : requestThreads) {
            requestThread.join();
        }
        responseThread.join();

        
        // make sure everything was successful
        for (int i = 0; i < requestThreadCount; i++) {
            if (requestThreads[i].throwable != null) {
                logger.error("", requestThreads[i].throwable);
            }
            Assert.assertNull("RequestThread " + i + " throwable wasn't null", requestThreads[i].throwable);
        }

        if (responseThread.throwable != null) {
            logger.error("", responseThread.throwable);
        }
        Assert.assertNull("ResponseThread throwable wasn't null", responseThread.throwable);

        Assert.assertEquals(0, requestWindow.getPendingSize());
    }
    
    @Test
    public void terminateSlotWaiters() throws Exception {
        // test that terminating slot waiters early works as expected
        final Window<Integer,String,String> window = new Window<Integer,String,String>(2);
        
        // first two items should work
        Assert.assertFalse(window.terminateSlotWaiters());
        window.addRequest(1, "Request1", 100);
        Assert.assertFalse(window.terminateSlotWaiters());
        window.addRequest(2, "Request2", 100);
        Assert.assertFalse(window.terminateSlotWaiters());
        
        // third item should fail
        try {
            window.addRequest(3, "Request3", 20);
            Assert.fail();
        } catch (MaxWindowSizeTimeoutException e) {
            // correct behavior
        }
        
        // start up 3 other threads that will all be waiting too
        Thread[] waiters = new Thread[3];
        for (int i = 0; i < waiters.length; i++) {
            final int x = i;
            waiters[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        window.addRequest(4+x, "Request" + (4+x), 5000);
                        Assert.fail();
                    } catch (WaitingTerminatedEarlyException e) {
                        // correct behavior
                    } catch (Exception e) {
                        logger.error(e);
                        Assert.fail();
                    }
                }
            };
            waiters[i].start();
        }
        
        // start up a thread that will call "terminateSlotWaiters" in 300 ms
        Thread terminator = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (Exception e) { }
                try {
                    Assert.assertEquals(4, window.getSlotWaitingSize());
                    boolean hadWaiters = window.terminateSlotWaiters();
                    logger.debug("hadWaiters: " + hadWaiters);
                    Assert.assertTrue(hadWaiters);
                } catch (Exception e) {
                    logger.error(e);
                    Assert.fail();
                }
            }
        };
        terminator.start();
        
        // now wait for a slot up to 5 seconds (the thread we spawned earlier
        // should definitely cause it to terminate early)
        try {
            window.addRequest(3, "Request3", 5000);
            Assert.fail();
        } catch (WaitingTerminatedEarlyException e) {
            // correct behavior
        }
        
        // make sure everything is finished
        terminator.join();
        for (Thread t : waiters) {
            t.join();
        }
        
        // next call to terminate slot waiters shouldn't do anything
        Assert.assertEquals(0, window.getSlotWaitingSize());
        Assert.assertFalse(window.terminateSlotWaiters());
        
        window.addResponse(1, "Response1");
        Assert.assertFalse(window.terminateSlotWaiters());
        window.addRequest(4, "Request4", 100);
        Assert.assertFalse(window.terminateSlotWaiters());
    }
}
