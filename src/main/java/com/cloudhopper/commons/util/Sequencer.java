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

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an extremely efficient, thread-safe way to generate a
 * simple incrementing sequence of Longs. This class safely resets the sequence
 * back to zero to prevent overflow. Internally uses the AtomicLong class.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class Sequencer {
    private static Logger logger = LoggerFactory.getLogger(Sequencer.class);

    private AtomicLong sequenceNumber;

    /**
     * Constructs a new instance of <code>Sequencer</code> a default starting
     * sequence number of 0.
     */
    public Sequencer() {
        sequenceNumber = new AtomicLong(0);
    }

    /**
     * Constructs a new instance of <code>Sequencer</code> with the specified
     * starting value.
     */
    public Sequencer(long startingValue) {
        sequenceNumber = new AtomicLong(startingValue);
    }

    public long next() {
        long seqNum = sequenceNumber.getAndIncrement();
        // check if this value is getting close to overflow?
        if (seqNum > Long.MAX_VALUE-1000000000) {
            sequenceNumber.set(0);
        }
        return seqNum;
    }
    
}
