/*
 * Copyright 2011 Twitter, Inc..
 *
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
 */
package com.cloudhopper.commons.util.windowing;

import com.cloudhopper.commons.util.UnwrappedWeakReference;
import java.lang.ref.WeakReference;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Internal utility class to monitor the window and send events upstream
   to listeners.
 * @author joelauer
 */
public class WindowMonitor<K,R,P> implements Runnable {
    private static final Logger logger = Logger.getLogger(Window.class);
    
    private final WeakReference<Window<K,R,P>> windowRef;

    public WindowMonitor(Window<K,R,P> window) {
        this.windowRef = new WeakReference<Window<K,R,P>>(window);
    }

    @Override
    public void run() {
        Window<K,R,P> window = windowRef.get();
        
        // check if the window using this monitor was GC'ed
        if (window == null) {
            logger.error("The parent Window was garbage collected in this WindowMonitor(): missing call to Window.reset() to stop this monitoring thread (will throw exception to cancel this recurring execution!)");
            throw new IllegalStateException("Parent Window was garbage collected (missing call to Window.reset() somewhere in code)");
        }
        
        logger.debug("WindowMonitor running, current size=" + window.getSize());
        
        List<WindowFuture<K,R,P>> expired = window.cancelAllExpired();
        if (expired != null && expired.size() > 0) {
            logger.debug("Found " + expired.size() + " requests that expired");
            // process each expired request and pass up the chain to handlers
            for (WindowFuture<K,R,P> future : expired) {
                for (UnwrappedWeakReference<WindowListener<K,R,P>> listenerRef : window.getListeners()) {
                    WindowListener<K,R,P> listener = listenerRef.get();
                    if (listener == null) {
                        // remove this reference from our array (no good anymore)
                        window.removeListener(listener);
                    } else {
                        try {
                            listener.expired(future);
                        } catch (Throwable t) {
                            logger.error("Ignoring uncaught exception thrown in listener: ", t);
                        }
                    }
                }
            }
        }
    }
}
