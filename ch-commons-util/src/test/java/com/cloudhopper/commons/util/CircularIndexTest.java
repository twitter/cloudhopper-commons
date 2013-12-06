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

import org.junit.*;

public class CircularIndexTest {

    @Test
    public void calculateNextIndex() throws Exception {
        CircularIndex index = new CircularIndex(4);

        Assert.assertEquals(1, index.calculateNewIndex(0, 1));
        Assert.assertEquals(2, index.calculateNewIndex(1, 1));
        Assert.assertEquals(3, index.calculateNewIndex(2, 1));
        Assert.assertEquals(0, index.calculateNewIndex(3, 1));
        // handle wraparound -- one previous from zero is the end
        Assert.assertEquals(3, index.calculateNewIndex(0, -1));
        Assert.assertEquals(2, index.calculateNewIndex(0, -2));
    }

    @Test
    public void constructor1() throws Exception {
        // initialize the index with other parameters
        CircularIndex index = new CircularIndex(4, 0, 0);

        //
        // initial states of everything
        //
        Assert.assertEquals(3, index.getMaxSize());
        Assert.assertEquals(4, index.getCapacity());
        Assert.assertEquals(0, index.getSize());
        Assert.assertEquals(true, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(-1, index.getFirst());
        Assert.assertEquals(-1, index.getLast());
        Assert.assertEquals(0, index.getNextFirst());
        Assert.assertEquals(0, index.getNextLast());
        Assert.assertEquals(false, index.removeFirst());
        // check internals
        Assert.assertEquals(0, index.getInternalFirst());
        Assert.assertEquals(0, index.getInternalNext());

        try {
            index = new CircularIndex(4, 0, 4);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        // initialize the index with other parameters
        index = new CircularIndex(4, 1, 3);

        //
        // initial states of everything
        //
        Assert.assertEquals(3, index.getMaxSize());
        Assert.assertEquals(4, index.getCapacity());
        Assert.assertEquals(3, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(true, index.isFull());
        Assert.assertEquals(1, index.getFirst());
        Assert.assertEquals(3, index.getLast());
        Assert.assertEquals(-1, index.getNextFirst());
        Assert.assertEquals(-1, index.getNextLast());
        // check internals
        Assert.assertEquals(1, index.getInternalFirst());
        Assert.assertEquals(0, index.getInternalNext());


        index = new CircularIndex(4, 1, 2);

        //
        // initial states of everything
        //
        Assert.assertEquals(3, index.getMaxSize());
        Assert.assertEquals(4, index.getCapacity());
        Assert.assertEquals(2, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(1, index.getFirst());
        Assert.assertEquals(2, index.getLast());
        Assert.assertEquals(0, index.getNextFirst());
        Assert.assertEquals(3, index.getNextLast());
        // check internals
        Assert.assertEquals(1, index.getInternalFirst());
        Assert.assertEquals(3, index.getInternalNext());
    }

    @Test
    public void addAndRemoveCorrectness() throws Exception {
        CircularIndex index = new CircularIndex(4);

        //
        // initial states of everything
        //
        Assert.assertEquals(3, index.getMaxSize());
        Assert.assertEquals(4, index.getCapacity());
        Assert.assertEquals(0, index.getSize());
        Assert.assertEquals(true, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(-1, index.getFirst());
        Assert.assertEquals(-1, index.getLast());
        Assert.assertEquals(0, index.getNextFirst());
        Assert.assertEquals(0, index.getNextLast());
        Assert.assertEquals(false, index.removeFirst());
        // check internals
        Assert.assertEquals(0, index.getInternalFirst());
        Assert.assertEquals(0, index.getInternalNext());

        //
        // add a new item
        //
        Assert.assertEquals(true, index.addLast());

        Assert.assertEquals(1, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(0, index.getFirst());
        Assert.assertEquals(0, index.getLast());
        Assert.assertEquals(3, index.getNextFirst());
        Assert.assertEquals(1, index.getNextLast());
        // check internals
        Assert.assertEquals(0, index.getInternalFirst());
        Assert.assertEquals(1, index.getInternalNext());

        //
        // remove first item
        //
        Assert.assertEquals(true, index.removeFirst());

        Assert.assertEquals(0, index.getSize());
        Assert.assertEquals(true, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(-1, index.getFirst());
        Assert.assertEquals(-1, index.getLast());
        Assert.assertEquals(1, index.getNextFirst());
        Assert.assertEquals(1, index.getNextLast());
        Assert.assertEquals(false, index.removeFirst());
        // check internals
        Assert.assertEquals(1, index.getInternalFirst());
        Assert.assertEquals(1, index.getInternalNext());


        //
        // add a new item
        //
        Assert.assertEquals(true, index.addLast());

        Assert.assertEquals(1, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(1, index.getFirst());
        Assert.assertEquals(1, index.getLast());
        Assert.assertEquals(0, index.getNextFirst());
        Assert.assertEquals(2, index.getNextLast());
        // check internals
        Assert.assertEquals(1, index.getInternalFirst());
        Assert.assertEquals(2, index.getInternalNext());


        //
        // add a new item
        //
        Assert.assertEquals(true, index.addLast());

        Assert.assertEquals(2, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(1, index.getFirst());
        Assert.assertEquals(2, index.getLast());
        Assert.assertEquals(0, index.getNextFirst());
        Assert.assertEquals(3, index.getNextLast());
        // check internals (last would have hit capacity and looped back to zero)
        Assert.assertEquals(1, index.getInternalFirst());
        Assert.assertEquals(3, index.getInternalNext());


        //
        // add a new item -- this should be the last we can insert
        //
        Assert.assertEquals(true, index.addLast());

        Assert.assertEquals(3, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(true, index.isFull());
        Assert.assertEquals(1, index.getFirst());
        // since an item was added above, then removed, then only empty space
        // open in the index is at position zero -- already looped around!
        Assert.assertEquals(3, index.getLast());
        // at capacity, so no new elements!
        Assert.assertEquals(-1, index.getNextFirst());
        Assert.assertEquals(-1, index.getNextLast());
        // check internals
        Assert.assertEquals(1, index.getInternalFirst());
        Assert.assertEquals(0, index.getInternalNext());

        // since we're full these should fail
        Assert.assertEquals(false, index.addLast());
        Assert.assertEquals(false, index.addFirst());


        //
        // let's remove 2 items
        //
        Assert.assertEquals(true, index.removeFirst());
        Assert.assertEquals(true, index.removeFirst());

        Assert.assertEquals(1, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(3, index.getFirst());
        Assert.assertEquals(3, index.getLast());
        Assert.assertEquals(2, index.getNextFirst());
        Assert.assertEquals(0, index.getNextLast());
        // check internals
        Assert.assertEquals(3, index.getInternalFirst());
        Assert.assertEquals(0, index.getInternalNext());


        //
        // add 2 items onto front
        //
        Assert.assertEquals(true, index.addFirst());
        Assert.assertEquals(true, index.addFirst());

        Assert.assertEquals(3, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(true, index.isFull());
        Assert.assertEquals(1, index.getFirst());
        Assert.assertEquals(3, index.getLast());
        // at capacity, so no new elements!
        Assert.assertEquals(-1, index.getNextFirst());
        Assert.assertEquals(-1, index.getNextLast());
        // check internals
        Assert.assertEquals(1, index.getInternalFirst());
        Assert.assertEquals(0, index.getInternalNext());

        // since we're full these should fail
        Assert.assertEquals(false, index.addLast());
        Assert.assertEquals(false, index.addFirst());

        //
        // remove first 2 items
        //
        Assert.assertEquals(true, index.removeFirst());
        Assert.assertEquals(true, index.removeFirst());

        //
        // add onto back, will loop around
        //
        Assert.assertEquals(true, index.addLast());

        Assert.assertEquals(2, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(3, index.getFirst());
        Assert.assertEquals(0, index.getLast());
        Assert.assertEquals(2, index.getNextFirst());
        Assert.assertEquals(1, index.getNextLast());
        // check internals
        Assert.assertEquals(3, index.getInternalFirst());
        Assert.assertEquals(1, index.getInternalNext());


        //
        // remove first
        //
        Assert.assertEquals(true, index.removeFirst());

        Assert.assertEquals(1, index.getSize());
        Assert.assertEquals(false, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(0, index.getFirst());
        Assert.assertEquals(0, index.getLast());
        Assert.assertEquals(3, index.getNextFirst());
        Assert.assertEquals(1, index.getNextLast());
        // check internals
        Assert.assertEquals(0, index.getInternalFirst());
        Assert.assertEquals(1, index.getInternalNext());
    }

    @Test
    public void resetOnZero() throws Exception {
        // initialize with automatic reset back to zero
        CircularIndex index = new CircularIndex(4, true);

        //
        // initial states of everything
        //
        Assert.assertEquals(3, index.getMaxSize());
        Assert.assertEquals(4, index.getCapacity());
        Assert.assertEquals(0, index.getSize());
        Assert.assertEquals(true, index.isEmpty());
        Assert.assertEquals(false, index.isFull());
        Assert.assertEquals(-1, index.getFirst());
        Assert.assertEquals(-1, index.getLast());
        Assert.assertEquals(0, index.getNextFirst());
        Assert.assertEquals(0, index.getNextLast());
        Assert.assertEquals(false, index.removeFirst());
        // check internals
        Assert.assertEquals(0, index.getInternalFirst());
        Assert.assertEquals(0, index.getInternalNext());

        // add 2 items
        Assert.assertEquals(true, index.addLast());
        Assert.assertEquals(true, index.addLast());
        Assert.assertEquals(2, index.getSize());
        Assert.assertEquals(0, index.getInternalFirst());
        Assert.assertEquals(2, index.getInternalNext());

        // remove them
        Assert.assertEquals(true, index.removeFirst());
        Assert.assertEquals(true, index.removeFirst());

        // with auto reset back to zero, this should have reset the internal first and next
        Assert.assertEquals(0, index.getSize());
        Assert.assertEquals(0, index.getInternalFirst());
        Assert.assertEquals(0, index.getInternalNext());

        // add another item, this should now be item zero
        Assert.assertEquals(true, index.addLast());
        Assert.assertEquals(1, index.getSize());
        Assert.assertEquals(0, index.getFirst());
        Assert.assertEquals(0, index.getLast());
        Assert.assertEquals(0, index.getInternalFirst());
        Assert.assertEquals(1, index.getInternalNext());

    }

}
