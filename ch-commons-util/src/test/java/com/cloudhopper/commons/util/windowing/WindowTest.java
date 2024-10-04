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

// third party imports
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// my imports
//import net.cloudhopper.commons.util.ByteBuffer;

/**
 * Tests Windowing class and packages.
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class WindowTest {
    private static final Logger logger = LoggerFactory.getLogger(WindowTest.class);

    @Test
    public void usage() throws Exception {
        Window<Integer,String,String> window = new Window<Integer,String,String>(1);
        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(0, window.getSize());

        WindowFuture<Integer,String,String> future0 = window.offer(0, "Request0", 100);

        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(1, window.getSize());
        Assert.assertEquals(0, window.getFreeSize());
        Assert.assertEquals(1, future0.getWindowSize());

        window.cancel(0);

        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(0, window.getSize());
        Assert.assertEquals(1, window.getFreeSize());

        WindowFuture<Integer,String,String> future1 = window.offer(1, "Request1", 100);

        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(1, window.getSize());
        Assert.assertEquals(0, window.getFreeSize());
        Assert.assertEquals(1, future1.getWindowSize());

        window.complete(1, "Response1");

        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(0, window.getSize());
        Assert.assertEquals(1, window.getFreeSize());
        
        WindowFuture<Integer,String,String> future2 = window.offer(2, "Request2", 100);
        
        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(1, window.getSize());
        Assert.assertEquals(0, window.getFreeSize());
        Assert.assertFalse(future2.isDone());
        Assert.assertFalse(future2.isCancelled());
        Assert.assertFalse(future2.isSuccess());
        Assert.assertNull(future2.getCause());
        Assert.assertEquals(1, future2.getWindowSize());
        
        // trigger this from the future now
        future2.fail(new Exception("Test Cause"));

        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(0, window.getSize());
        Assert.assertEquals(1, window.getFreeSize());
        Assert.assertTrue(future2.isDone());
        Assert.assertFalse(future2.isCancelled());
        Assert.assertFalse(future2.isSuccess());
        Assert.assertNotNull(future2.getCause());
        
        WindowFuture<Integer,String,String> future3 = window.offer(3, "Request3", 100);
        
        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(1, window.getSize());
        Assert.assertEquals(0, window.getFreeSize());
        Assert.assertFalse(future3.isDone());
        Assert.assertFalse(future3.isCancelled());
        Assert.assertFalse(future3.isSuccess());
        Assert.assertNull(future3.getCause());
        Assert.assertEquals(1, future3.getWindowSize());
        
        // trigger this from the future now
        long now = System.currentTimeMillis();
        future3.complete("Response3", now);

        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(0, window.getSize());
        Assert.assertEquals(1, window.getFreeSize());
        Assert.assertTrue(future3.isDone());
        Assert.assertFalse(future3.isCancelled());
        Assert.assertTrue(future3.isSuccess());
        Assert.assertNull(future3.getCause());
        Assert.assertEquals(now, future3.getDoneTimestamp());
        
        
        WindowFuture<Integer,String,String> future4 = window.offer(4, "Request4", 100);
        
        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(1, window.getSize());
        Assert.assertEquals(0, window.getFreeSize());
        Assert.assertFalse(future4.isDone());
        Assert.assertFalse(future4.isCancelled());
        Assert.assertFalse(future4.isSuccess());
        Assert.assertNull(future4.getCause());
        Assert.assertEquals(1, future4.getWindowSize());
        
        // trigger this from the future now
        future4.cancel(now);

        Assert.assertEquals(1, window.getMaxSize());
        Assert.assertEquals(0, window.getSize());
        Assert.assertEquals(1, window.getFreeSize());
        Assert.assertTrue(future4.isDone());
        Assert.assertTrue(future4.isCancelled());
        Assert.assertFalse(future4.isSuccess());
        Assert.assertNull(future4.getCause());
        Assert.assertEquals(now, future4.getDoneTimestamp());
    }

    
    @Test
    public void requestFutureAndWindowFuture() throws Exception {
        Window<Integer,String,String> window = new Window<Integer,String,String>(1);
        Integer i = new Integer(1);
        String request = "Request"+i;

        WindowFuture<Integer,String,String> requestFuture = window.offer(i, request, 100);
        Assert.assertEquals(new Integer(i), requestFuture.getKey());
        Assert.assertEquals(request, requestFuture.getRequest());
        Assert.assertEquals(true, requestFuture.getAcceptTimestamp() > 0);
        Assert.assertEquals(null, requestFuture.getResponse());
        Assert.assertFalse(requestFuture.hasDoneTimestamp());
        Assert.assertEquals(0, requestFuture.getDoneTimestamp());
        Assert.assertEquals(false, requestFuture.isCancelled());
        Assert.assertEquals(false, requestFuture.isDone());
        Assert.assertEquals(false, requestFuture.isSuccess());
        Assert.assertEquals(-1, requestFuture.getAcceptToDoneTime());

        // this should timeout waiting for a response
        Assert.assertFalse(requestFuture.await(100));

        // mimic a response is received
        String response = "Response"+i;
        WindowFuture<Integer,String,String> responseFuture = window.complete(i, response);
        Assert.assertEquals(new Integer(i), responseFuture.getKey());
        Assert.assertEquals(request, responseFuture.getRequest());
        Assert.assertEquals(true, responseFuture.getAcceptTimestamp() > 0);
        Assert.assertEquals(response, responseFuture.getResponse());
        Assert.assertEquals(true, responseFuture.getDoneTimestamp() > 0);
        Assert.assertEquals(false, requestFuture.isCancelled());
        Assert.assertEquals(true, requestFuture.isDone());
        Assert.assertEquals(true, requestFuture.isSuccess());
        Assert.assertEquals(true, requestFuture.getAcceptToDoneTime() > 0);

        // this should succeed now since a response was received
        Assert.assertTrue(requestFuture.await(100));
        String response0 = requestFuture.getResponse();
        Assert.assertEquals(response, response0);
        Assert.assertEquals(request, requestFuture.getRequest());
        Assert.assertEquals(true, requestFuture.getAcceptTimestamp() > 0);
        Assert.assertEquals(response, requestFuture.getResponse());
        Assert.assertEquals(true, requestFuture.getDoneTimestamp() > 0);
        Assert.assertEquals(false, requestFuture.isCancelled());
        Assert.assertEquals(true, requestFuture.isDone());
        Assert.assertEquals(true, requestFuture.isSuccess());
        Assert.assertEquals(true, requestFuture.getAcceptToDoneTime() > 0);
    }

    @Test
    public void filledWindowThrowsOfferTimeoutException() throws Exception {
        Window<Integer,String,String> window = new Window<Integer,String,String>(1);
        WindowFuture<Integer,String,String> requestFuture0 = window.offer(0, "Request0", 100);
        try {
            // this should timeout waiting for a slot
            WindowFuture<Integer,String,String> requestFuture1 = window.offer(1, "Request1", 100);
            Assert.fail();
        } catch (OfferTimeoutException e) {
            // correct behavior
        }
    }

    @Test
    public void awaitTimesout() throws Exception {
        Window<Integer,String,String> window = new Window<Integer,String,String>(1);
        WindowFuture<Integer,String,String> requestFuture0 = window.offer(0, "Request0", 100);
        // this should timeout waiting for a response
        Assert.assertFalse(requestFuture0.await(100));
    }

    @Test
    public void duplicateKeyThrowsDuplicateKeyException() throws Exception {
        Window<Integer,String,String> window = new Window<Integer,String,String>(1);
        WindowFuture<Integer,String,String> requestFuture0 = window.offer(0, "Request0", 100);
        try {
            WindowFuture<Integer,String,String> requestFuture1 = window.offer(0, "Request0", 100);;
            Assert.fail();
        } catch (DuplicateKeyException e) {
            // correct behavior
        }
    }

    @Test
    public void waitingFlagNotOriginallySetButAddedOnAwait() throws Exception {
        Window<Integer,String,String> window = new Window<Integer,String,String>(1);
        WindowFuture<Integer,String,String> requestFuture0 = window.offer(0, "Request0", 100);
        Assert.assertEquals(WindowFuture.CALLER_NOT_WAITING, requestFuture0.getCallerStateHint());
        Assert.assertFalse(requestFuture0.isCallerWaiting());

        // now the caller was mistaken that they weren't waiting, when they actually
        // start to wait, we'll set the waiting flag to true
        Assert.assertFalse(requestFuture0.await(30));
        
        Assert.assertEquals(WindowFuture.CALLER_WAITING_TIMEOUT, requestFuture0.getCallerStateHint());
    }

    @Test
    public void cancel() throws Exception {
        Window<Integer,String,String> window = new Window<Integer,String,String>(1);
        Integer i = new Integer(1);
        String request = "Request"+i;

        WindowFuture<Integer,String,String> requestFuture = window.offer(i, request, 100);
        
        // cancel it
        WindowFuture<Integer,String,String> responseFuture = window.cancel(i);
        Assert.assertEquals(new Integer(i), responseFuture.getKey());
        Assert.assertEquals(request, responseFuture.getRequest());
        Assert.assertEquals(true, responseFuture.getAcceptTimestamp() > 0);
        Assert.assertEquals(null, responseFuture.getResponse());
        Assert.assertEquals(true, responseFuture.getDoneTimestamp() > 0);
        Assert.assertEquals(true, requestFuture.isCancelled());
        Assert.assertEquals(true, requestFuture.isDone());
        Assert.assertEquals(false, requestFuture.isSuccess());
        Assert.assertTrue(requestFuture.getAcceptToDoneTime() >= 0);

        // this should not timeout waiting for a response
        Assert.assertTrue(requestFuture.await(50));
    }


    @Test
    public void cancelAll() throws Exception {
        int count = 5;
        Window<Integer,String,String> window = new Window<Integer,String,String>(count);
        String[] requests = new String[count];
        for (int i = 0; i < count; i++) {
            requests[i] = "Request" + i;
        }

        for (int i = 0; i < 3; i++) {
            window.offer(i, requests[i], 100);
        }

        Assert.assertEquals(3, window.getSize());

        // are there requests pending?
        List<WindowFuture<Integer,String,String>> cancelled = window.cancelAll();

        Assert.assertEquals(0, window.getSize());
        Assert.assertEquals(3, cancelled.size());

        for (int i = 0; i < 3; i++) {
            WindowFuture<Integer,String,String> value = cancelled.get(i);
            Assert.assertEquals("Request"+value.getKey(), value.getRequest());
            Assert.assertEquals(true, value.getAcceptTimestamp() > 0);
            Assert.assertEquals(null, value.getResponse());
            Assert.assertEquals(true, value.getDoneTimestamp() > 0);
            Assert.assertEquals(true, value.isCancelled());
            Assert.assertEquals(true, value.isDone());
            Assert.assertEquals(false, value.isSuccess());
        }
    }

    public static class RequestThread extends Thread {

        private Window<Integer,String,String> window;
        private BlockingQueue<Integer> requestQueue;
        public int id;
        public int requestsPerThread;
        public Throwable throwable;

        public RequestThread(Window<Integer,String,String> window, BlockingQueue<Integer> requestQueue, int id, int requestsPerThread) {
            this.window = window;
            this.requestQueue = requestQueue;
            this.id = id;
            this.requestsPerThread = requestsPerThread;
        }

        @Override
        public void run() {
            try {
                for (int x = 0; x < requestsPerThread; x++) {
                    //To get a distinct value of i, we multiply id by the total number of requests per thread and add x to it
                    Integer i = Integer.valueOf(""+id*requestsPerThread+""+x);
                    String request = "Request"+i;
 //                   logger.debug("adding request " + i);
                    WindowFuture<Integer,String,String> requestFuture = window.offer(i, request, 1000);
                    Assert.assertEquals(i, requestFuture.getKey());
                    Assert.assertEquals(request, requestFuture.getRequest());
                    Assert.assertEquals(true, requestFuture.getAcceptTimestamp() > 0);
                    Assert.assertEquals(null, requestFuture.getResponse());
                    Assert.assertEquals(0, requestFuture.getDoneTimestamp());
                    Assert.assertEquals(false, requestFuture.isCancelled());
                    Assert.assertEquals(false, requestFuture.isDone());
                    Assert.assertEquals(false, requestFuture.isSuccess());
                    Assert.assertEquals(-1, requestFuture.getAcceptToDoneTime());

                    requestQueue.add(i);
                    requestFuture.await(100);

                    Assert.assertEquals(request, requestFuture.getRequest());
                    Assert.assertEquals(true, requestFuture.getAcceptTimestamp() > 0);
                    Assert.assertEquals("Response"+i, requestFuture.getResponse());
                    //Assert.assertEquals(null, requestFuture.getResponse());
                    Assert.assertEquals(true, requestFuture.getDoneTimestamp() > 0);
                    Assert.assertEquals(false, requestFuture.isCancelled());
                    Assert.assertEquals(true, requestFuture.isDone());
                }
            } catch (Throwable t) {
                logger.error("", t);
                this.throwable = t;
                return;
            }
        }
    }
    
    public static class ResponseThread extends Thread {

        private Window<Integer,String,String> window;
        private BlockingQueue<Integer> requestQueue;
        public int total;
        public Throwable throwable;

        public ResponseThread(Window<Integer,String,String> window, BlockingQueue<Integer> requestQueue, int total) {
            this.window = window;
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
                    WindowFuture<Integer,String,String> responseFuture = window.complete(i, response);
                    Assert.assertEquals(new Integer(i), responseFuture.getKey());
                    Assert.assertEquals("Request"+i, responseFuture.getRequest());
                    Assert.assertEquals(true, responseFuture.getAcceptTimestamp() > 0);
                    Assert.assertEquals(response, responseFuture.getResponse());
                    Assert.assertEquals(true, responseFuture.getDoneTimestamp() > 0);
                    Assert.assertEquals(false, responseFuture.isCancelled());
                    Assert.assertEquals(true, responseFuture.isDone());
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
        final Window<Integer,String,String> window = new Window<Integer,String,String>(5);

        final int requestThreadCount = 8;
        final int requestsPerThread = 10000;
        final BlockingQueue<Integer> requestQueue = new LinkedBlockingQueue<Integer>();

        RequestThread[] requestThreads = new RequestThread[requestThreadCount];
        for (int i = 0; i < requestThreadCount; i++) {
            requestThreads[i] = new RequestThread(window, requestQueue, i, requestsPerThread);
        }

        ResponseThread responseThread = new ResponseThread(window, requestQueue, requestThreadCount*requestsPerThread);

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
            Assert.assertNull("RequestThread " + i + " throwable wasn't null: " + requestThreads[i].throwable, requestThreads[i].throwable);
        }

        if (responseThread.throwable != null) {
            logger.error("", responseThread.throwable);
        }
        Assert.assertNull("ResponseThread throwable wasn't null", responseThread.throwable);

        Assert.assertEquals(0, window.getSize());
    }
    
    @Test
    public void abortOffering() throws Exception {
        // test that terminating slot waiters early works as expected
        final Window<Integer,String,String> window = new Window<Integer,String,String>(2);
        
        // first two items should work
        Assert.assertFalse(window.abortPendingOffers());
        window.offer(1, "Request1", 100);
        Assert.assertFalse(window.abortPendingOffers());
        window.offer(2, "Request2", 100);
        Assert.assertFalse(window.abortPendingOffers());
        
        // third item should fail
        try {
            window.offer(3, "Request3", 20);
            Assert.fail();
        } catch (OfferTimeoutException e) {
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
                        window.offer(4+x, "Request" + (4+x), 5000);
                        Assert.fail();
                    } catch (PendingOfferAbortedException e) {
                        // correct behavior
                    } catch (Exception e) {
                        logger.error("", e);
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
                    Assert.assertEquals(4, window.getPendingOfferCount());
                    boolean hadWaiters = window.abortPendingOffers();
                    logger.debug("hadWaiters: " + hadWaiters);
                    Assert.assertTrue(hadWaiters);
                } catch (Exception e) {
                    logger.error("", e);
                    Assert.fail();
                }
            }
        };
        terminator.start();
        
        // now wait for a slot up to 5 seconds (the thread we spawned earlier
        // should definitely cause it to terminate early)
        try {
            window.offer(3, "Request3", 5000);
            Assert.fail();
        } catch (PendingOfferAbortedException e) {
            // correct behavior
        }
        
        // make sure everything is finished
        terminator.join();
        for (Thread t : waiters) {
            t.join();
        }
        
        // next call to terminate slot waiters shouldn't do anything
        Assert.assertEquals(0, window.getPendingOfferCount());
        Assert.assertFalse(window.abortPendingOffers());
        
        window.complete(1, "Response1");
        Assert.assertFalse(window.abortPendingOffers());
        window.offer(4, "Request4", 100);
        Assert.assertFalse(window.abortPendingOffers());
    }
    
    @Test
    public void abortOfferingCalledWithNoWaitingOfferers() throws Exception {
        final Window<Integer,String,String> window = new Window<Integer,String,String>(2);
        
        window.offer(0, "Request0", 100);
        
        window.abortPendingOffers();
        
        window.offer(1, "Request1", 100);
    }
    
    @Test
    public void invalidArguments() throws Exception {
        final Window<Integer,String,String> window = new Window<Integer,String,String>(1);
        
        try {
            window.offer(1, "test1", -1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
    }
}
