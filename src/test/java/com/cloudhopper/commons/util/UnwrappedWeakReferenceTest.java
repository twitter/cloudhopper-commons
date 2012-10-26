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

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class UnwrappedWeakReferenceTest {
    
    @Test
    public void usage() throws Exception {
        Object o = new Object();
        
        // verify WeakReference does not work correctly with CopyOnWriteArrayList
        WeakReference ref0 = new WeakReference(o);
        WeakReference ref1 = new WeakReference(o);
        CopyOnWriteArrayList<WeakReference> cowal = new CopyOnWriteArrayList<WeakReference>();
        cowal.addIfAbsent(ref0);
        cowal.addIfAbsent(ref1);
        
        // if it didn't work, size is 2
        Assert.assertEquals(2, cowal.size());
        
        // use our impl now
        ref0 = new UnwrappedWeakReference(o);
        ref1 = new UnwrappedWeakReference(o);
        cowal = new CopyOnWriteArrayList<WeakReference>();
        cowal.addIfAbsent(ref0);
        cowal.addIfAbsent(ref1);
        
        Assert.assertEquals(1, cowal.size());
        
        Assert.assertTrue(cowal.contains(ref0));
        
        cowal.remove(ref1);
        
        Assert.assertEquals(0, cowal.size());
    }
    
    @Test
    @SuppressWarnings("IncompatibleEquals")
    public void equals() throws Exception {
        String o = new String();
        
        // verify WeakReference does not work correctly with CopyOnWriteArrayList
        WeakReference ref0 = new UnwrappedWeakReference(o);
        WeakReference ref1 = new UnwrappedWeakReference(o);
        
        Assert.assertTrue(ref0.equals(ref1));
        // also test non-unwrapping
        Assert.assertTrue(ref0.equals(o));
        Assert.assertTrue(ref1.equals(o));
    }
    
}
