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

public class RoundRobinLoadBalancedListTest {
    private static final Logger logger = LoggerFactory.getLogger(RoundRobinLoadBalancedListTest.class);

    @Test
    public void getNextOneItem() throws Exception {
        RoundRobinLoadBalancedList<String> list = new RoundRobinLoadBalancedList<String>();

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
        RoundRobinLoadBalancedList<String> list = new RoundRobinLoadBalancedList<String>();

        // add just one item
        list.set("Item1", 2);
        list.set("Item2", 2);
        list.set("Item3", 2);

        Assert.assertEquals(3, list.getRemainingSize());

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals(1, list.getRemainingSize());
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


    @Test
    public void setLargerWeight() throws Exception {
        RoundRobinLoadBalancedList<String> list = new RoundRobinLoadBalancedList<String>();

        // add just one item
        list.set("Item1", 2);
        list.set("Item2", 2);
        list.set("Item3", 2);

        Assert.assertEquals(3, list.getRemainingSize());

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals(1, list.getRemainingSize());
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

        logger.debug(list.toString());
        logger.debug("Removing Item3");
        list.remove("Item3");
        logger.debug(list.toString());

        logger.debug("Resetting Item2 to weight of 3");
        list.set("Item2", 3);
        logger.debug(list.toString());

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        // should normally be at end of cycle now since Item2 was @ 2, but now its 3
        Assert.assertEquals("Item2", list.getNext());

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
    }


    @Test
    public void setSmallerWeight() throws Exception {
        RoundRobinLoadBalancedList<String> list = new RoundRobinLoadBalancedList<String>();

        // add just one item
        list.set("Item1", 2);
        list.set("Item2", 2);
        list.set("Item3", 2);

        Assert.assertEquals(3, list.getRemainingSize());

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        // normally item1, item2, item3 could all be reused again
        // reset Item2 to weight of 1, should cause it be taken out of remainingItems
        Assert.assertEquals(3, list.getRemainingSize());
        list.set("Item2", 1);

        Assert.assertEquals(2, list.getRemainingSize());
        
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        // now cycle should repeat fully...
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        // back to full cycle again...
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        // normally Item2 wouldn't show up again, let's reset its weight again
        list.set("Item2", 2);
        logger.debug(list.toString());

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item3", list.getNext());

        logger.debug(list.toString());
    }

    @Test
    public void setZeroWeightOnOnlyRemainingItemToTestRebuild() throws Exception {
        RoundRobinLoadBalancedList<String> list = new RoundRobinLoadBalancedList<String>();

        // add two items
        list.set("Item1", 1);
        list.set("Item2", 1);

        logger.debug(list.toString());

        Assert.assertEquals(2, list.getRemainingSize());

        // "use" up the first item
        Assert.assertEquals("Item1", list.getNext());

        // now we'll set what should have been the "next" item to a weight of
        // zero which will effectively remove it
        list.set("Item2", 0);

        logger.debug("AFTER REMOVAL: " + list.toString());

        // the list contains 1 item still and it should be the next item returned
        Assert.assertEquals(1, list.getSize());
        // item1 should be returned every single time
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
    }

    @Test
    public void setSmallerWeightOnOnlyRemainingItemToTestRebuild() throws Exception {
        RoundRobinLoadBalancedList<String> list = new RoundRobinLoadBalancedList<String>();

        // add two items
        list.set("Item1", 3);
        list.set("Item2", 1);

        logger.debug("Initial list: " + list.toString());

        Assert.assertEquals(2, list.getRemainingSize());

        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());

        // item2 should be "used" up, item1 should be the only thing returned now

        logger.debug("Updated list: " + list.toString());

        // reset weight back to 1 on item1 -- since it already has a count of 1
        // this should actually remove it from our "remaining" items
        list.set("Item1", 1);

        logger.debug("After Item1 Set to 1: " + list.toString());

        // the remaining items should be item1 and item2 again
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());
    }


    @Test
    public void clear() throws Exception {
        RoundRobinLoadBalancedList<String> list = new RoundRobinLoadBalancedList<String>();

        // add two items
        list.set("Item1", 1);
        list.set("Item2", 1);

        logger.debug("Initial list: " + list.toString());

        Assert.assertEquals(2, list.getRemainingSize());
        Assert.assertEquals("Item1", list.getNext());
        Assert.assertEquals("Item2", list.getNext());

        Assert.assertEquals(2, list.getSize());
        Assert.assertEquals(2, list.getRemainingSize());

        // clear it
        list.clear();

        Assert.assertEquals(0, list.getSize());
        Assert.assertEquals(0, list.getRemainingSize());
        Assert.assertEquals(null, list.getNext());
    }


    @Test
    public void contains() throws Exception {
        RoundRobinLoadBalancedList<String> list = new RoundRobinLoadBalancedList<String>();

        // add two items
        list.set("Item1", 1);
        list.set("Item2", 1);

        Assert.assertEquals(true, list.contains("Item1"));
        Assert.assertEquals(true, list.contains("Item2"));
        Assert.assertEquals(false, list.contains("Item3"));
    }
}
