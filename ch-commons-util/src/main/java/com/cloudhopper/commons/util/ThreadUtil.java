package com.cloudhopper.commons.util;

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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

/**
 * A utility class for working with threads in a JVM.
 *
 * Some of these methods were obtained here:
 * http://nadeausoftware.com/articles/2008/04/java_tip_how_list_and_find_threads_and_thread_groups#Gettingalistofallthreads
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ThreadUtil {

    /**
     * Gets the "root" thread group for the entire JVM.  Internally, first get
     * the current thread and its thread group. Then get its parent group, then
     * its parent group, and on up until you find a group with a null parent.
     * That's the root ThreadGroup.  Since the same root thread group is used
     * for the life of the JVM, you can safely cache it for faster future use.
     * @return The root thread group for the JVM
     */
    static public ThreadGroup getRootThreadGroup() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        ThreadGroup ptg;
        while ((ptg = tg.getParent()) != null)
            tg = ptg;
        return tg;
    }

    /**
     * Gets all threads in the JVM.  This is really a snapshot of all threads
     * at the time this method is called.
     * @return An array of all threads currently running in the JVM.
     */
    static public Thread[] getAllThreads() {
        final ThreadGroup root = getRootThreadGroup();
        final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
        int nAlloc = thbean.getThreadCount();
        int n = 0;
        Thread[] threads;
        do {
            nAlloc *= 2;
            threads = new Thread[nAlloc];
            n = root.enumerate(threads, true);
        } while (n == nAlloc);
        return java.util.Arrays.copyOf(threads, n);
    }

    /**
     * Gets a thread by its assigned ID.  Internally, this method calls
     * getAllThreads() so be careful to only call this method once and cache
     * your result.
     * @param id The thread ID
     * @return The thread with this ID or null if none were found
     */
    static public Thread getThread(final long id) {
        final Thread[] threads = getAllThreads();
        for (Thread thread : threads)
            if (thread.getId() == id)
                return thread;
        return null;
    }

    /**
     * Gets all threads if its name matches a regular expression.  For example,
     * using a regex of "main" will execute a case-sensitive match for threads
     * with the exact name of "main".  A regex of ".*main.*" will execute a
     * case sensitive match for threads with "main" anywhere in their name. A
     * regex of "(?i).*main.*" will execute a case insensitive match of any
     * thread that has "main" in its name.
     * @param regex The regular expression to use when matching a threads name.
     *      Same rules apply as String.matches() method.
     * @return An array (will not be null) of all matching threads.  An empty
     *      array will be returned if no threads match.
     */
    static public Thread[] getAllThreadsMatching(final String regex) {
        if (regex == null)
            throw new NullPointerException("Null thread name regex");
        final Thread[] threads = getAllThreads();
        ArrayList<Thread> matchingThreads = new ArrayList<Thread>();
        for (Thread thread : threads) {
            if (thread.getName().matches(regex)) {
                matchingThreads.add(thread);
            }
        }
        return matchingThreads.toArray(new Thread[0]);
    }

}

