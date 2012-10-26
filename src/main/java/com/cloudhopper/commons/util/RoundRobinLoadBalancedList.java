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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a load balanced list that returns items using a weighted, 
 * round-robin algorithm.  If "A" is added with weight 2 and "B" is added with
 * weight 1, then getNext() will return the items in this order:
 *
 *   A, B, A, A, B, A, A, B, A, ....
 *
 * NOTE: This class does not synchronize access.  Any concurrent access to this
 * object MUST be externally synchronized.
 *
 * NOTE: This class only works for small lists.  You need to assume every
 * call to get the next selection is O(n).
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class RoundRobinLoadBalancedList<E> implements LoadBalancedList<E> {
    private static final Logger logger = LoggerFactory.getLogger(RoundRobinLoadBalancedList.class);

    private final ArrayList<Node<E>> allItems;
    private final ArrayList<Node<E>> remainingItems;
    private int currentIndex;

    public RoundRobinLoadBalancedList() {
        this.allItems = new ArrayList<Node<E>>();
        this.remainingItems = new ArrayList<Node<E>>();
        this.currentIndex = 0;     // index of remaining item to return
    }

    @Override
    public List<Node<E>> getValues() {
        return this.allItems;
    }

    @Override
    public void clear() {
        // remove all items from allitems
        allItems.clear();
        remainingItems.clear();
        this.currentIndex = 0;
    }

    @Override
    public boolean contains(E item) {
        Node<E> newNode = new Node<E>(item);
        return this.allItems.contains(newNode);
    }

    @Override
    public void set(E item, int weight) {
        // if weight of zero, acts just like removing
        if (weight <= 0) {
            remove(item);
            return;
        }

        // create a new node we'll use for searching and/or adding
        Node<E> newNode = new Node<E>(item, weight);

        // try to find item in list 0(N)
        int i = allItems.indexOf(newNode);

        // this is a new item -- add it
        if (i < 0) {
            // add to all of our items
            this.allItems.add(newNode);
            // add to remainingItems as well
            this.remainingItems.add(newNode);
        // this is a current item -- update weight
        } else {
            // this is just an update of the weight (to something non-zero) - get the current node
            Node<E> currentNode = allItems.get(i);
            // update it's weight to its new value
            currentNode.setWeight(weight);
            // if the new weight >= count, remove it from remaining items
            if (currentNode.getCount() >= currentNode.getWeight()) {
                // remove this node from remaining items if it already exists...
                // MUST reset count back to zero (otherwise we'll never add it back in)
                currentNode.setCount(0);
                // remove from remaining items
                this.remainingItems.remove(currentNode);

                // bug fix: if the node just removed was the only node in our
                // *remaining* items list, then we have a problem.  we need to
                // check if we should rebuild our remaining items array
                if (this.remainingItems.isEmpty() && this.allItems.size() > 0) {
                    this.rebuildRemainingItems();
                }
            }
        }
    }

    @Override
    public void remove(E item) {
        // create a new node we'll use for searching and/or adding
        Node<E> newNode = new Node<E>(item);

        // try to find item in list 0(N)
        int i = allItems.indexOf(newNode);

        if (i < 0) {
            // item not in our list, don't do anything
            //logger.debug("Item is not in list");
        } else {
            // remove from all items and possible items
            this.allItems.remove(i);
            this.remainingItems.remove(newNode);
        }

        // safety check -- if there are no more remaining items, but we still
        // have items in our list, rebuild our "remaining items"
        if (this.remainingItems.isEmpty() && this.allItems.size() > 0) {
            this.rebuildRemainingItems();
        }
    }

    protected void rebuildRemainingItems() {
        // clear any previous items
        this.remainingItems.clear();
        // loop thru all items
        for (Node<E> node : allItems) {
            // if the nodes weight is greater than count add it
            if (node.getWeight() > node.getCount()) {
                this.remainingItems.add(node);
            }
        }
    }

    protected void resetCountsAndRebuildRemainingItems() {
        // reset all counts back to zero
        for (Node<E> node : allItems) {
            node.setCount(0);
        }
        // now rebuild the whole thing
        this.rebuildRemainingItems();
    }

    @Override
    public int getSize() {
        return this.allItems.size();
    }

    public int getRemainingSize() {
        return this.remainingItems.size();
    }

    @Override
    public E getNext() {
        // hmm... actually, this is a good safety check to do now -- if allitems
        // is zero, then there is no way we could have a next item
        if (allItems.isEmpty()) {
            return null;
        }

        // if there are items in "allItems", but remainingItems is still zero
        // then this means some sort of bug occurred since this should be impossible
        if (remainingItems.isEmpty()) {
            logger.error("Impossible bug occurred with RoundRobinLoadBalancedList where allItems > 0, but remainingItems == 0, going to completely internally rebuild everything");
            resetCountsAndRebuildRemainingItems();
        }

        // saftey check -- see if we need to reset the index back to zero
        if (this.currentIndex >= remainingItems.size()) {
            // reset back to zero
            this.currentIndex = 0;
        }

        // get the item at this index
        Node<E> node = this.remainingItems.get(this.currentIndex);

        // increment count
        node.incrementCount();

        // is the max count reached?
        if (node.getCount() >= node.getWeight()) {
            // reset count back to zero
            node.setCount(0);
            //logger.debug("Removing node " + node.getValue());
            // remove this node from possible items
            this.remainingItems.remove(this.currentIndex);
            // don't increment index since the current index actually would
            // be the next item we want to return
        } else {
            // keep the current node and increment the index
            // always increment currentIndex first
            this.currentIndex++;
        }

        // if there aren't any more remaining items, rebuid it
        if (remainingItems.isEmpty()) {
            // rebuild a new remainingItems array
            rebuildRemainingItems();
        }

        // see if we need to reset the index back to zero for next getNext call
        if (this.currentIndex >= remainingItems.size()) {
            // reset back to zero
            this.currentIndex = 0;
        }

        return node.getValue();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(200);

        buf.append("curIdx=");
        buf.append(this.currentIndex);
        buf.append(" [");

        int i = 0;
        for (Node<E> node : remainingItems) {
            if (i != 0) buf.append(", ");
            buf.append(node);
            i++;
        }

        buf.append("]");

        return buf.toString();
    }

}
