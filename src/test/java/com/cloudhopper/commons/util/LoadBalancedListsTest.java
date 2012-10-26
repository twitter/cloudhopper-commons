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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;

public class LoadBalancedListsTest {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancedListsTest.class);

    @Test
    public void getNext() throws Exception {
        LoadBalancedList<String> list = LoadBalancedLists.synchronizedList(new RoundRobinLoadBalancedList<String>());

        // add just one item
        list.set("Item1", 1);
        for (int i = 0; i < 50; i++) {
            Assert.assertTrue("Failed on i=" + i, "Item1".equals(list.getNext()));
        }

        // remove it
        list.remove("Item1");
        for (int i = 0; i < 50; i++) {
            Assert.assertTrue("Failed on i=" + i, list.getNext() == null);
        }

        // add another item
        list.set("Item2", 1);
        for (int i = 0; i < 50; i++) {
            Assert.assertTrue("Failed on i=" + i, "Item2".equals(list.getNext()));
        }

        // reset weight to zero (should remove it)
        list.set("Item2", 0);
        for (int i = 0; i < 50; i++) {
            Assert.assertTrue("Failed on i=" + i, list.getNext() == null);
        }
    }

    @Test
    public void getNextEqualWeight() throws Exception {
        LoadBalancedList<String> list = LoadBalancedLists.synchronizedList(new RoundRobinLoadBalancedList<String>());

        // add just one item
        list.set("Item1", 2);
        list.set("Item2", 2);
        list.set("Item3", 2);

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        // k, now we repeat and every item is back in list of possible choices
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        // used up all of them once, item1 would normally have 1 more use left
        // let's reset its weight to 1 (which should remove it from possible list)
        list.set("Item1", 1);

        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        // reset weight of item1 back to two
        list.set("Item1", 2);

        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());
        // cycle should repeat now...
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
    }

}
