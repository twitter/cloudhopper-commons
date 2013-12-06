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

import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a capacity constrained ByteBuffer using a circular array
 * of bytes. A circular array of bytes minimizes memory allocations when adding
 * or removing bytes from the buffer.</br></br>
 *
 * NOTE: This class is NOT thread-safe since its intended mainly for use inside
 * a single thread -- such as reading from a socket or other connection.</br></br>
 *
 * NOTE: This class does not auto grow the underlying buffer.
 *
 * All add() methods are the only way to copy information to this buffer. However,
 * there are various ways to delete or remove data from this buffer.  The delete()
 * methods will erase data from the buffer without returning the removed bytes.
 * This avoids allocating memory if you simply want to free up space in this buffer.
 * The remove() methods will erase the data AND return the erased data as the
 * return value. Use these methods when you need to get the data and delete it.
 *
 * The size of the underlying byte[] buffer is always capacity + 1. If you
 * initialize this buffer requesting a capacity of 1024, please be aware that this
 * class will actually allocate 1025 bytes. This is required to be able to internally
 * track where the data ends inside the buffer. However, externally, it will
 * look like only 1024 bytes are free, capacity, size, etc.  At no time would
 * 1025 be made available in a public method.</br></br>
 *
 * This class is originally derived from a CircularByteBuffer obtained from
 * http://awr.free.fr/ which was distributed with the Lesser GNU Public License.
 * That class was written by Arnaud Witschger. This class has been heavily
 * modified for a different usage.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ByteBuffer {
    private final static Logger logger = LoggerFactory.getLogger(ByteBuffer.class);

    /** Default buffer capacity. */
    private static final int DEFAULT_BUFFER_CAPACITY = 1023;

    /** Maximum for integer numbers. */
    private static final int MAXIMUM_INTEGER = 2147483647;

    /** The circular buffer is a byte array. */
    private byte[] buffer;

    /** The current buffer reading position. */
    private int currentReadPosition;

    /** The current buffer writing position. */
    private int currentWritePosition;

    /** The current buffer real size. */
    private int currentBufferSize;
    

    /**
     * Constructs a new instance of <code>ByteBuffer</code> with an
     * initial default capacity of 1024 bytes (DEFAULT_BUFFER_CAPACITY)
     */
    public ByteBuffer() {
        try {
            this.circularByteBufferInitializer(DEFAULT_BUFFER_CAPACITY+1, 0, 0, 0);
        } catch (IllegalArgumentException e) {
            logger.error("Impossible case reached constructing ByteBuffer");
        }
    }

    
    /**
     * Constructs a new instance of <code>ByteBuffer</code> with the specified
     * capacity.
     * @param capacity The buffer capacity. Must be >= 1.
     */
    public ByteBuffer(int capacity) throws IllegalArgumentException {
        // since we'll always allocate capacity+1, the user only needs to request
        // a size of at least 1
        if (capacity+1 < 2) {
            throw new IllegalArgumentException("Buffer capacity must be >= 1");
        }
        this.circularByteBufferInitializer(capacity+1, 0, 0, 0);
    }

    /**
     * Constructs a new instance of <code>ByteBuffer</code> with a capacity
     * only large enough to hold they byte array.  Buffer is initialized with
     * the byte[] bytes starting from offset 0 and adding the entire byte aray.
     * @param bytes The byte array to initialize buffer with
     */
    public ByteBuffer(byte[] bytes) {
        this((bytes.length==0 ? 1 : bytes.length));
        try {
            this.add(bytes);
        } catch (BufferSizeException e) {
            logger.error("Impossible case reached constructing ByteBuffer");
        }
    }

    /**
     * Constructs a new instance of <code>ByteBuffer</code> with the specified
     * capacity.  Buffer is initialized with the byte[] bytes starting from
     * offset 0 and adding the entire byte aray. Capacity must be >= the length
     * of the byte array.
     * @param bytes The byte array to initialize buffer with
     * @param capacity The buffer capacity. Must be >= 1.
     * @throws IllegalArgumentException Thrown if the capacity is too small
     */
    public ByteBuffer(byte[] bytes, int capacity)
            throws IllegalArgumentException {
        this(bytes, 0, bytes.length, capacity);
    }


    /**
     * Constructs a new instance of <code>ByteBuffer</code> with the specified
     * capacity.  Buffer is initialized with the byte[] bytes starting from
     * <code>offset</code> and added up to <code>length</code> bytes.
     * @param bytes The byte array to initialize buffer with
     * @param offset The offset in the byte array to start from
     * @param length The length starting from offset within the byte array
     * @throws IllegalArgumentException Thrown if offset or length is negative,
     *      capacity is too small, or if the offset+length would cause a read
     *      past the length of the byte array.
     */
    public ByteBuffer(byte[] bytes, int offset, int length)
            throws IllegalArgumentException {
        this(bytes, offset, length, length);
    }


    /**
     * Constructs a new instance of <code>ByteBuffer</code> with the specified
     * capacity.  Buffer is initialized with the byte[] bytes starting from
     * <code>offset</code> and added up to <code>length</code> bytes. Capacity
     * must be large enough to store the length.
     * @param bytes The byte array to initialize buffer with
     * @param offset The offset in the byte array to start from
     * @param length The length starting from offset within the byte array
     * @param capacity The buffer capacity. Must be >= 1.
     * @throws IllegalArgumentException Thrown if offset or length is negative,
     *      capacity is too small, or if the offset+length would cause a read
     *      past the length of the byte array.
     */
    public ByteBuffer(byte[] bytes, int offset, int length, int capacity)
            throws IllegalArgumentException {
        this(capacity);                 // will check for valid capacity
        checkOffsetLength(bytes.length, offset, length);
        // user should have requested >= the length of the bytes they passed in
        if (capacity < length) {
            throw new IllegalArgumentException("Buffer capacity (" + capacity + ") must be >= the byte[] length (" + bytes.length + ")");
        }
        // at this point, guaranteed to have enough capacity to add this
        // entire byte array to this buffer -- we can at least prevent
        // any BufferSizeException
        try {
            this.add(bytes, offset, length);
        } catch (BufferSizeException e) {
            logger.error("Impossible case of BufferSizeException in ByteBuffer constructor", e);
        }
    }


    /**
     * Constructs a new instance of <code>ByteBuffer</code>.
     * Buffer is initialized with the String converted to bytes using the
     * ISO-8859-1 character set.
     * @param string0 The string to initialize our buffer with
     * @throws java.lang.IllegalArgumentException If capacity is too small to store
     *      the bytes from the String
     */
    public ByteBuffer(String string0) throws IllegalArgumentException {
        this(string0, string0.length());
    }


    /**
     * Constructs a new instance of <code>ByteBuffer</code> with the specified
     * capacity.  Buffer is initialized with the String converted to bytes using the
     * ISO-8859-1 character set. Capacity must be large enough to store the
     * bytes obtained after conversion.  If only 8-bit data is stored in the String,
     * the capacity required would be the length() of the String.
     * @param string0 The string to initialize our buffer with
     * @param capacity The capacity of the buffer. Must be >= byte length of string
     * @throws java.lang.IllegalArgumentException If capacity is too small to store
     *      the bytes from the String
     */
    public ByteBuffer(String string0, int capacity) throws IllegalArgumentException {
        this(capacity);
        byte[] bytes = null;
        
        try {
            // convert string to bytes via ISO-8859-1
            bytes = string0.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            logger.error("Impossible case in BtyeBuffer(String) constructor since ISO-8859-1 should be supported encoding", e);
            throw new IllegalArgumentException("Unsupported encoding exception thrown, should never happen", e);
        }

        // is the capacity large enough
        if (capacity < bytes.length) {
            throw new IllegalArgumentException("Buffer capacity (" + capacity + ") must be >= string0 length (" + string0.length() + "). ISO-8859-1 conversion to bytes > string length?");
        }

        try {
            this.add(bytes);
        } catch (BufferSizeException e) {
            logger.error("Impossible case in BtyeBuffer(String) constructor since capacity should have been ensured", e);
            throw new IllegalArgumentException("BufferSizeException exception thrown, should never happen", e);
        }
    }


    /**
     * Intializes the new CircularByteBuffer with all parameters that characterize a CircularByteBuffer.
     * @param bufferCapacity the buffer capacity. Must be >= 2.
     * @param bufferSize the buffer initial size. Must be in [0, bufferCapacity].
     * @param readPosition the buffer initial read position. Must be in [0, bufferSize]
     * @param writePosition the buffer initial write position. Must be in [0, bufferSize]
     */
    private void circularByteBufferInitializer(int bufferCapacity, int bufferSize, int readPosition, int writePosition) {
        if (bufferCapacity < 2) {
            throw new IllegalArgumentException("Buffer capacity must be greater than 2 !");
        }
        if ((bufferSize < 0) || (bufferSize > bufferCapacity)) {
            throw new IllegalArgumentException("Buffer size must be a value between 0 and "+bufferCapacity+" !");
        }
        if ((readPosition < 0) || (readPosition > bufferSize)) {
            throw new IllegalArgumentException("Buffer read position must be a value between 0 and "+bufferSize+" !");
        }
        if ((writePosition < 0) || (writePosition > bufferSize)) {
            throw new IllegalArgumentException("Buffer write position must be a value between 0 and "+bufferSize+" !");
        }
        this.buffer = new byte[bufferCapacity];
        this.currentBufferSize = bufferSize;
        this.currentReadPosition = readPosition;
        this.currentWritePosition = writePosition;
    }


    /**
     * Gets the modular exponentiation, i.e. result of  a^b mod n. Use to calculate hashcode.
     * @param a A number.
     * @param b An exponent.
     * @param n A modulus.
     * @return Result of modular exponentiation, i.e. result of  a^b mod n.
     */
    private int modularExponentation(int a, int b, int n) throws IllegalArgumentException {
        int result = 1;
        int counter;
        int maxBinarySize = 32;
        boolean[] b2Binary = new boolean[maxBinarySize];
        for (counter = 0; b > 0; counter++) {
            if (counter >= maxBinarySize){
                throw new IllegalArgumentException("Exponent "+b+" is too big !");
            }
            b2Binary[counter] = (b % 2 != 0);
            b = b / 2;
        }
        for (int k = counter - 1; k >= 0; k--) {
            result = (result * result) % n;
            if (b2Binary[k]){
                result = (result * a) % n;
            }
        }
        return result;
    }


    /**
     * Gets the current buffer size.
     * @return The current buffer size.
     */
    public int size() {
        return this.currentBufferSize;
    }

    /**
     * Gets the total buffer allocated capacity, not the remaining capacity.
     * Using capacity()-size() would calculate how much room is remaining in
     * this buffer.
     * @return The buffer capacity.
     * @see #free()
     */
    public int capacity() {
        // this is always 1 less than the actual byte[]
        return this.buffer.length-1;
    }

    /**
     * Gets the number of free bytes this buffer has remaining for writing.
     * @return The number of bytes available for writing in this buffer.
     */
    public int free() {
        return capacity()-size();
    }

    /**
     * Tests if the buffer has enough free space to store N bytes. Same
     * as free() >= N.
     * @return True if the buffer has enough free space to store N bytes,
     *      otherwise false.
     */
    public boolean isFree(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        return (free() >= count);
    }

    /**
     * Tests if the buffer is empty.
     * @return True if the buffer is empty, otherwise false.
     */
    public boolean isEmpty() {
        return (size() == 0);
    }

    /**
     * Tests if the buffer is full.
     * @return True if the buffer is full, otherwise false.
     */
    public boolean isFull() {
        return (size() == capacity());
    }

    /**
     * Clears the buffer and resets it. Does not reallocate a new array.
     */
    public void clear() {
        this.currentReadPosition = 0;
        this.currentWritePosition = 0;
        this.currentBufferSize = 0;
    }


    /**
     * Adds one byte to the buffer and throws an exception if the buffer is
     * full.
     * @param b Byte to add to the buffer.
     * @throws BufferIsFullException If the buffer is full and the byte cannot
     *      be stored.
     */
    public void add(byte b) throws BufferIsFullException {
        if (isFull()) {
            throw new BufferIsFullException("Buffer is full and has reached maximum capacity (" + capacity() + ")");
        }

        // buffer is not full
        this.buffer[this.currentWritePosition] = b;
        this.currentWritePosition = (this.currentWritePosition + 1) % this.buffer.length;
        this.currentBufferSize += 1;
    }


    /**
     * Adds a byte array to this buffer. If the free space remaining in the buffer
     * is not large enough, this method will throw a BufferSizeException. A byte
     * array consisting of a length of 0 will immediately return.
     * @param bytes A byte array to add to this buffer. Its size must less than or equal to this buffer's free space
     * @throws BufferSizeException If this buffer's free space is not large enough to store add the byte array
     */
    public void add(byte[] bytes) throws BufferSizeException {
        this.add(bytes, 0, bytes.length);
    }

    
    /**
     * Helper method for validating if an offset and length are valid for a given
     * byte array. Checks if the offset or length is negative or if the offset+length
     * would cause a read past the end of the byte array.
     * @param bytesLength The length of the byte array to validate against
     * @param offset The offset within the byte array
     * @param length The length to read starting from the offset
     * @throws java.lang.IllegalArgumentException If any of the above conditions
     *      are violated.
     */
    static protected void checkOffsetLength(int bytesLength, int offset, int length)
            throws IllegalArgumentException {
        // offset cannot be negative
        if (offset < 0) {
            throw new IllegalArgumentException("The byte[] offset parameter cannot be negative");
        }
        // length cannot be negative either
        if (length < 0) {
            throw new IllegalArgumentException("The byte[] length parameter cannot be negative");
        }
        // is it a valid offset?  Must be < bytes.length if non-zero
        // if its zero, then the check below will validate if it would cause
        // a read past the length of the byte array
        if (offset != 0 && offset >= bytesLength) {
            throw new IllegalArgumentException("The byte[] offset (" + offset + ") must be < the length of the byte[] length (" + bytesLength + ")");
        }
        if (offset+length > bytesLength) {
            throw new IllegalArgumentException("The offset+length (" + (offset+length) + ") would read past the end of the byte[] (length=" + bytesLength + ")");
        }
    }

    /**
     * Adds a byte array to this buffer starting from the offset up to the
     * length requested. If the free space remaining in the buffer
     * is not large enough, this method will throw a BufferSizeException.
     * @param bytes A byte array to add to this buffer.
     * @param offset The offset within the byte array to begin to add
     * @param length The length starting from offset to begin to add
     * @throws BufferSizeException If this buffer's free space is not large enough to store add the byte array
     */
    public void add(byte[] bytes, int offset, int length)
            throws IllegalArgumentException, BufferSizeException {
        // validate the bytes, offset, length
        checkOffsetLength(bytes.length, offset, length);
        // is there enough free space in this buffer to add the entire array
        if (length > free()) {
            throw new BufferSizeException("Buffer does not have enough free space (" + free() + " bytes) to add " + length + " bytes of data");
        }
        
        // add each byte to this array
        for (int i = 0; i < length; i++) {
            try {
                this.add(bytes[i+offset]);
            } catch (BufferIsFullException e) {
                // this should be an impossible case since we checked the size() above
                logger.error("Buffer is full even though this method checked its size() ahead of time", e);
                throw new BufferSizeException(e.getMessage());
            }
        }
    }


    /**
     * Deletes the first N bytes of the buffer.  Avoids allocating any memory
     * since there is no return value for this method.  If you want to delete
     * and also see the deleted data as a return value, please use the remove()
     * methods.
     */
    public void delete(int count) throws BufferSizeException {
        if ((count < 0) || (count > capacity())) {
            throw new IllegalArgumentException("Can only delete between 0 and " + capacity() + " bytes from buffer, you passed in=" + count);
        }
        if (count > size()) {
            throw new BufferSizeException("Buffer size (" + size() + ") not large enough to delete (" + count + ") bytes");
        }
        this.currentReadPosition = (this.currentReadPosition + count) % this.buffer.length;
        this.currentBufferSize -= count;
    }


    /**
     * Removes the first (oldest/head) byte in the buffer. If the buffer is empty,
     * this method will throw a BufferIsEmptyException
     * @return The first (oldest/head) byte in the buffer
     * @throws BufferIsEmptyException If the buffer is empty and a byte cannot be
     *      removed.
     */
    public byte remove() throws BufferIsEmptyException {
        if (size() == 0) {
            throw new BufferIsEmptyException("Buffer is empty and no bytes available to remove");
        }
        byte b = this.buffer[this.currentReadPosition];
        this.currentReadPosition = (this.currentReadPosition + 1) % this.buffer.length;
        this.currentBufferSize -= 1;
        return b;
    }
    

    /**
     * Removes a byte array of the first N bytes in the buffer. The parameter
     * count must be a value between 1 and the capacity of the buffer, otherwise
     * a runtime IllegalArgumentException will be thrown.  If the N bytes
     * requested is larger than the buffer size, then a <code>BufferSizeException</code>
     * will be thrown.
     * @param count The number of bytes to remove (a value between 1 and the buffer capacity).
     * @return The first N bytes of the buffer.
     * @throws BufferSizeException If the buffer is not large enough to fufill the request
     *      (if N is > size)
     */
    public byte[] remove(int count) throws BufferSizeException {
        if ((count < 0) || (count > capacity())) {
            throw new IllegalArgumentException("Tried to remove " + count + " bytes from buffer. The count must be a value between 0 and " + capacity());
        }
        if (count > size()) {
            throw new BufferSizeException("Buffer size (" + size() + ") not large enough to remove (" + count + ") bytes");
        }

        // allocate the new array we'll fill
        byte[] removedBuffer  = new byte[count];

        int currentPos = 0;
        // remove each byte :
        for (int i = 0; i < count; i++, currentPos++) {
            try {
                removedBuffer[currentPos] = this.remove();
            } catch (BufferIsEmptyException e) {
                // this should be an impossible case since we checked the size() above
                logger.error("Buffer is empty even though this method checked its size() ahead of time", e);
                throw new BufferSizeException(e.getMessage());
            }
        }
        
        return removedBuffer;
    }

    
    /**
     * Counts the number of occurrences of the byte in this <code>ByteBuffer</code>.
     * This method will not overlap any bytes during its search. For example,
     * if you're search for bytes of "AA" in a buffer containing "AAA", this
     * method will only return a value of 1.
     * @param b The byte to search for
     * @return -1 if byte is not found, otherwise the number of occurrences.
     */
    public int occurrences(byte b) {
        byte[] bytes = { b };
        return occurrences(bytes);
    }


    /**
     * Counts the number of occurrences of the byte array in this
     * <code>ByteBuffer</code>. This method will not overlap any bytes during its
     * search. For example, if you're search for bytes of "AA" in a buffer containing "AAA", this
     * method will only return a value of 1.
     * @param bytes The byte[] to search for
     * @return -1 if bytes is a length of zero, otherwise the number of occurrences.
     */
    public int occurrences(byte[] bytes) {
        if (bytes.length == 0) {
            return -1;
        }
        int occurrences = 0;
        int currentPos = -1;
        int offset = 0;
        while (offset < size()) {
            // search for the delimiter
            currentPos = indexOf(bytes, offset);
            if (currentPos < 0) {
                break;
            }
            occurrences++;
            offset = currentPos + bytes.length;
        }
        return occurrences;
    }

    /**
    public ByteBuffer copy() {
        return copy(0, size());
    }
     */


    /**
     * Most efficient copy of this <code>ByteBuffer</code>.  The internal buffer
     * is copied to the new ByteBuffer using either 1 or 2 calls to System.arraycopy().
     * @return The new ByteBuffer with copied data
     */
    public ByteBuffer copy() {
        return copy(0, size(), size());
    }


    /**
     * Most efficient copy of this <code>ByteBuffer</code>.  The internal buffer
     * is copied to the new ByteBuffer using either 1 or 2 calls to System.arraycopy().
     * @param offset The offset in the buffer to start from
     * @param length The length from the offset
     * @return The new ByteBuffer with copied data
     */
    public ByteBuffer copy(int offset, int length) {
        return copy(offset, length, length);
    }
    

    /**
     * Most efficient copy of this <code>ByteBuffer</code>.  The internal buffer
     * is copied to the new ByteBuffer using either 1 or 2 calls to System.arraycopy().
     * @param offset The offset in the buffer to start from
     * @param length The length from the offset
     * @param capacity The capacity of the new ByteBuffer. Must be >= this ByteBuffer's size()
     * @return The new ByteBuffer with copied data
     */
    public ByteBuffer copy(int offset, int length, int capacity) {

        // if not copying any data, just return an empty buffer
        if (length == 0) {
            return new ByteBuffer(1);
        }

        // validate offset, length ok
        ByteBuffer.checkOffsetLength(size(), offset, length);

        // is the capacity large enough?
        if (capacity < length) {
            throw new IllegalArgumentException("Capacity must be large enough to hold copied data of size=" + length);
        }

        // create a new ByteBuffer with enough capacity
        ByteBuffer copyBuffer = new ByteBuffer(capacity);

        // initialize everything now
        // copy this buffer's data into the target buffer -- already allocated memory
        this.toArray(offset, length, copyBuffer.buffer, 0);
        // setup other variables to be correct position now
        copyBuffer.currentBufferSize = length;
        copyBuffer.currentReadPosition = 0;
        copyBuffer.currentWritePosition = length;

        return copyBuffer;
    }
    


    /**
     * Splits a <code>ByteBuffer</code> into a ByteBuffer array where each
     * ByteBuffer in the array contains the sequence of bytes split along the
     * byte delimiter.  The underlying byte[] in this ByteBuffer is not affected.
     * If the delimiter wasn't found, then the returned ByteBuffer will essentially
     * be a copy of the current ByteBuffer.
     * @param delimiter The byte[] that will be treated as the delimiter
     * @return A new array of ByteBuffers or null if the delimiter was not found
     */
    /**
    public ByteBuffer[] split(byte[] delimiter) {
        // is the delimiter ok?
        if (delimiter.length == 0) {
            throw new IllegalArgumentException("Delimiter byte[] must have a length > 0");
        }

        // find number of occurrences of this delimiter
        int occurrences = occurrences(delimiter);

        // safety check
        if (occurrences < 0) {
            throw new IllegalArgumentException("Impossible case of -1 occurrences of the delimiter");
        }

        // otherwise, we know how many ByteBuffer's we'll create - we'll always
        // end up return occurrences+1 buffers
        int tokens = occurrences+1;
        ByteBuffer[] buffers = new ByteBuffer[tokens];

        int i = 0;
        int offset = 0;
        int delimiterPos = -1;

        // keep looping until the offset is larger than we want
        while (offset < size()) {
            logger.debug("in split loop for i=" + i + ",delimiterPos=" + delimiterPos + ",offset=" + offset + ",tokens=" + tokens + ",size=" + this.size());
            // find the next delimiter occurrence
            delimiterPos = indexOf(delimiter, offset);
            logger.debug("found next delimiter at pos=" + delimiterPos);
            // no occurrence found, copy rest of buffer
            if (delimiterPos < 0) {
                // last token, copy rest of this buffer
                buffers[i] = this.copy(offset, size()-offset);
            } else {
                buffers[i] = this.copy(offset, delimiterPos-offset);
                offset = delimiterPos + delimiter.length;
            }
            i++;
        }

        return buffers;
    }
     */


    /**
     * Internal unchecked version of get(). Will only throw a Runtime exception
     * which should happen as long as internally, you're careful of how you
     * access the buffer.
     */
    private byte getUnchecked(int index) {
        return this.buffer[(this.currentReadPosition + index) % this.buffer.length];
    }


    /**
     * Gets the byte at the given index relative to the beginning the circular
     * buffer. The index must be a value between 0 and the buffer capacity.
     * @param index The index of the byte relative to the beginning the buffer
     * (a value between 0 and the the current size).
     * @return The byte at the given position relative to the beginning the buffer.
     * @throws BufferSizeException If the index is >= size()
     */
    public byte get(int index) throws IllegalArgumentException, BufferSizeException {
        if ((index < 0) || (index  >= capacity())) {
            throw new IllegalArgumentException("The buffer index must be a value between 0 and " + capacity() + "!");
        }
        if (index >= size()) {
            throw new BufferSizeException("Index " + index + " is >= buffer size of " + size());
        }
        return this.buffer[(this.currentReadPosition + index) % this.buffer.length];
    }


    /**
     * Replaces the byte at the given position relative to the beginning the circular buffer by the given value.
     * The position must be a value between 0 and the current buffer size - 1.
     * @param position a position relative to the beginning the circular buffer (a value between 0 and the
     * the current buffer size - 1).
     * @param value a byte value to put instead of the previous value.
     */
    /** NOT NEEDED YET
    public void replace(int position, byte value) throws IllegalArgumentException {
        if ((position < 0) || (position  >= this.buffer.length)) {
            throw new IllegalArgumentException("The position must be a value between 0 and "+this.buffer.length+" - 1 = "+(this.buffer.length - 1)+" !");
        }
        this.buffer[(this.currentReadPosition + position) % this.buffer.length] = value;
    }
     */

    

    /**
     * Gets a copy of the current buffer as byte array. Method will allocate
     * only enough memory to hold a copy of the current buffer data.
     * @return A byte array. Could be empty if this buffer's size() is zero.
     */
    public byte[] toArray() {
        return toArray(0, size());
    }


    /**
     * Gets a copy of the current buffer as byte array, but only copies data
     * starting from an offset and length.  Method will allocate
     * only enough memory to hold a copy of the slice of the current buffer data.
     * @param offset The offset to start from
     * @param length The length from the offset
     * @return A byte array. Could be empty.
     * @throws IllegalArgumentException If capacity isn't large enough enough
     *      to hold the new byte[]
     */
    public byte[] toArray(int offset, int length) {
        return toArray(offset, length, length);
    }
    

    /**
     * Gets a copy of the current buffer as byte array, but the new byte[]
     * has the specified capacity.  Useful if you need to store additional bytes
     * in the returned byte[] and dont' want to do an additional System.arraycopy()
     * afterwards. Method will allocate memory to hold a copy of the current array
     * and return it.
     * @param offset The offset to start from
     * @param length The length from the offset
     * @param capacity The size of the new byte[]. Must be >= this buffer's size()
     * @return A byte array. Could be empty.
     * @throws IllegalArgumentException If capacity isn't large enough enough
     *      to hold the new byte[]
     */
    public byte[] toArray(int offset, int length, int capacity) {
        // validate the offset, length are ok
        ByteBuffer.checkOffsetLength(size(), offset, length);
        
        // will we have a large enough byte[] allocated?
        if (capacity < length) {
            throw new IllegalArgumentException("Capacity must be large enough to hold a byte[] of at least a size=" + length);
        }

        byte[] arrayCopy = new byte[capacity];
        this.toArray(offset, length, arrayCopy, 0);
        return arrayCopy;
    }


    /**
     * Will copy data from this ByteBuffer's buffer into the targetBuffer.  This
     * method requires the targetBuffer to already be allocated with enough capacity
     * to hold this ByteBuffer's data.
     * @param offset The offset within the ByteBuffer to start copy from
     * @param length The length from the offset to copy
     * @param targetBuffer The target byte array we'll copy data into. Must already
     *      be allocated with enough capacity.
     * @param targetOffset The offset within the target byte array to start from
     * @throws IllegalArgumentException If the offset and length are invalid
     *      for this ByteBuffer, if the targetOffset and targetLength are invalid
     *      for the targetBuffer, or if if the targetBuffer's capacity is not
     *      large enough to hold the copied data.
     */
    public void toArray(int offset, int length, byte[] targetBuffer, int targetOffset) {

        // validate the offset, length are ok
        ByteBuffer.checkOffsetLength(size(), offset, length);

        // validate the offset, length are ok
        ByteBuffer.checkOffsetLength(targetBuffer.length, targetOffset, length);

        // will we have a large enough byte[] allocated?
        if (targetBuffer.length < length) {
            throw new IllegalArgumentException("TargetBuffer size must be large enough to hold a byte[] of at least a size=" + length);
        }

        // are we actually copying any data?
        if (length > 0) {

            // create adjusted versions of read and write positions based
            // on the offset and length passed into this method
            int offsetReadPosition = (this.currentReadPosition + offset) % this.buffer.length;
            int offsetWritePosition = (this.currentReadPosition + offset + length) % this.buffer.length;

            if (offsetReadPosition >= offsetWritePosition) {
                System.arraycopy(
                    this.buffer,
                    offsetReadPosition,
                    targetBuffer,
                    targetOffset,
                    this.buffer.length - offsetReadPosition);
                System.arraycopy(
                    this.buffer,
                    0,
                    targetBuffer,
                    targetOffset + this.buffer.length - offsetReadPosition,
                    offsetWritePosition);
            } else {
                System.arraycopy(
                    this.buffer,
                    offsetReadPosition,
                    targetBuffer,
                    targetOffset,
                    offsetWritePosition - offsetReadPosition);
            }
        }
    }

    
    /**
     * Tests if the buffer starts with the byte array prefix. The byte array
     * prefix must have a size >= this buffer's size.
     * @return True if the buffer starts with the bytes array, otherwise false.
     */
    public boolean startsWith(byte[] prefix) {
        if ((prefix.length == 0) || (prefix.length > size())) {
            // no match would be possible
            return false;
        }
        boolean match = true;
        int i = 0;
        int j = this.currentReadPosition;
        while (match && (i < prefix.length)) {
            if (this.buffer[j] != prefix[i]) {
                match = false;
            }
            i++;
            j = (j + 1) % this.buffer.length;
        }
        return match;
    }


    /**
     * Tests if the buffer ends with the bytes array prefix. The byte array
     * prefix must have a size >= this buffer's size.
     * @return True if the buffer ends with the bytes array, otherwise false.
     */
    public boolean endsWith(byte[] prefix) {
        //logger.debug("Inside endsWith()");
        if ((prefix.length == 0) || (prefix.length > size())) {
            //It could not match :
            return false;
        }
        boolean match = true;
        int i = prefix.length - 1;
        int j = (this.currentWritePosition - 1 + this.buffer.length) % this.buffer.length;
        while (match && (i >= 0)) {
            //logger.debug("endsWith() this.buffer["+j+"]=" + this.buffer[j] + "='" + (char)this.buffer[j] + "', prefix["+i+"]=" + prefix[i] + "='" + (char)prefix[i] + "'");
            if (this.buffer[j] != prefix[i]) {
                match = false;
            }
            i--;
            j = (j - 1 + this.buffer.length) % this.buffer.length;
        }
        return match;
    }


    /**
     * Returns the index within this buffer of the first occurrence of the
     * specified bytes. The bytes to search for must have a size lower than
     * or equal to the current buffer size. This method will return -1 if the
     * bytes are not contained within this byte buffer.
     * @param bytes The byte array to search for
     * @return The index where the bytes start to match within the buffer.
     */
    public int indexOf(byte[] bytes) {
        return indexOf(bytes, 0);
    }

    /**
     * Returns the index within this buffer of the first occurrence of the
     * specified bytes after the offset. The bytes to search for must have a
     * size lower than or equal to the current buffer size. This method will
     * return -1 if the bytes are not contained within this byte buffer.
     * @param bytes The byte array to search for
     * @param offset The offset within the buffer to search from
     * @return The index where the bytes start to match within the buffer.
     */
    public int indexOf(byte[] bytes, int offset) {
        // make sure we're not checking against any byte arrays of length 0
        if (bytes.length == 0 || size() == 0) {
            // a match is not possible
            return -1;
        }
        
        // make sure the offset won't cause us to read past the end of the byte[]
        if (offset < 0 || offset >= size()) {
            throw new IllegalArgumentException("Offset must be a value between 0 and " + size() + " (current buffer size)");
        }

        int length = size()-bytes.length;
        for (int i = offset; i <= length; i++) {
            int j = 0;
            // continue search loop while the next bytes match
            while (j < bytes.length && getUnchecked(i+j) == bytes[j]) {
                j++;
            }
            // if we found it, then j will equal the length of the search bytes
            if (j == bytes.length) {
                return i;
            }
        }

        // if we get here then we didn't find it
        return -1;
    }


    /**
     * Gets a hash code for this ByteBuffer object based on actual sequence
     * of bytes stored in this buffer.  That means this object is safe to
     * store in Hashtables or Hashmaps since the same sequence of bytes in
     * different instances will generate the same hashCode, regardless of
     * capacity, internal read position, write position, etc.
     * <br>
     * This method is implemented using logic from Jakarta Commons Lang
     * library in the HashCodeBuilder class.
     * @return A hash code for this ByteBuffer object for the sequence of bytes
     *      stored in this buffer.
     */
    @Override
    public int hashCode() {
        // use two hard-coded constant -- any value would do
        final int hashingConstant = 37;
        int hashCode = 17 * hashingConstant;
        int size = size();
        // adding entire array of bytes in sequence
        for (int i = 0; i < size; i++) {
            hashCode = hashCode * hashingConstant + getUnchecked(i);
        }
        return hashCode;
    }
    

    /**
     * Tests if the current ByteBuffer and the parameter are both ByteBuffers
     * and whether it contains the same sequence of bytes.
     * @return True if the two objects are equal and contain the same sequence
     *      of bytes, otherwise false.
     */
    @Override
    public boolean equals(Object obj)  {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof ByteBuffer)) {
            return false;
        }
        ByteBuffer toTest = (ByteBuffer)obj;

        // can't be equal if size()'s don't match
        if (toTest.size() != this.size())
            return false;

        // start comparison
        int length = this.size();
        for (int i = 0; i < length; i++) {
            try {
                if (this.get(i) != toTest.get(i)) {
                    return false;
                }
            } catch (Exception e) {
                logger.error("Impossible case should never happen", e);
                return false;
            }
        }
        
        return true;
    }


    /**
     * Tests if the current ByteBuffer and the byte array have the same
     * sequence of bytes.  Nearly the same as calling startsWith(), but if
     * the two byte[]'s are a length of zero, this method will return true.
     * @return True if the two byte arrays are equal and contain the same sequence
     *      of bytes, otherwise false.
     */
    public boolean equals(byte[] bytes)  {
        // cannot be equal if the bytes is null, since this ByteBuffer is
        // not definitely not null
        if (bytes == null) {
            return false;
        }
        // are both buffer's zero length, this is the case that startsWith misses
        if (bytes.length == 0 && size() == 0) {
            return true;
        }
        // otherwise, the impl is the same as startsWith
        return this.startsWith(bytes);
    }

    
    /**
     * Clones this ByteBuffer. Copies the buffer, data in the buffer, and keeps
     * the original capacity value intact.
     * @return A clone of this CircularByteBuffer object.
     */
    /**
    public Object clone() throws CloneNotSupportedException {
        ByteBuffer clonedBuffer = null;
        try {
            clonedBuffer = new CircularByteBuffer(
                this.buffer,
                this.currentBufferSize,
                this.currentReadPosition,
                this.currentWritePosition);
        } catch (IllegalArgumentException ue) {
            //Nothing to do : impossible case !
        }
        return cbb;
    }
     */

    
    /**
     * Returns a string representation of the current buffer.  This converts
     * any internal stored bytes directly to chars and appends them to a String.
     * @return A byte-to-char string representation of the current buffer.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append((char)getUnchecked(i));
        }
        return sb.toString();
    }


    /**
     * Return a hexidecimal String representation of the current buffer with each byte
     * displayed in a 2 character hexidecimal format.  Useful for debugging.
     * Convert a ByteBuffer to a String with a hexidecimal format.
     * @return The string in hex representation
     */
    public String toHexString() {
        return toHexString(0, size());
    }


    /**
     * Return a hexidecimal String representation of the current buffer with each byte
     * displayed in a 2 character hexidecimal format.  Useful for debugging.
     * Convert a ByteBuffer to a String with a hexidecimal format.
     * @param offset
     * @param length
     * @return The string in hex representation
     */
    public String toHexString(int offset, int length) {
        // is offset, length valid for the current size() of our internal byte[]
        checkOffsetLength(size(), offset, length);

        // if length is 0, return an empty string
        if (length == 0 || size() == 0) {
            return "";
        }

        StringBuilder s = new StringBuilder(length * 2);
        int end = offset + length;
        for (int i = offset; i < end; i++) {
            HexUtil.appendHexString(s, getUnchecked(i));
        }
        
        return s.toString();
    }
}
