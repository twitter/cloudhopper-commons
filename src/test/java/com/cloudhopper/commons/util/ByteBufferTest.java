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

// third party imports
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// my imports
//import net.cloudhopper.commons.util.ByteBuffer;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ByteBufferTest {
    private static final Logger logger = LoggerFactory.getLogger(ByteBufferTest.class);

    private static final byte[] TEST_BYTES1 = {'H','E','L','L','O',' ','W','O','R','L','D'};
    private static final byte[] TEST_HELLO = {'H','E','L','L','O'};
    private static final byte[] TEST_H = {'H'};
    private static final byte[] TEST_WORLD = {'W','O','R','L','D'};
    private static final byte[] TEST_W = {'W'};
    private static final byte[] TEST_D = {'D'};
    private static final byte[] TEST_L = {'L'};
    private static final byte[] TEST_LL = {'L','L'};
    private static final byte[] TEST_SPACE = {' '};
    private static final byte[] TEST_SPACEH = {' ','H'};
    private static final byte[] TEST_SPACEW = {' ','W'};
    private static final byte[] TEST_BYTES2 = {'C','A','A','A','A','A','A','A','B','A','A','A','B','B','B','B','B','B'};
    private static final byte[] TEST_AA = {'A','A'};
    private static final byte[] TEST_AB = {'A','B'};
    private static final byte[] TEST_BB = {'B','B'};
    private static final byte[] TEST_C = {'C'};
    private static final byte[] COMMA = {','};

    /**
     * Helper method for creating a buffer that probably is trashed -- circled
     * at least 3 times just to make sure we're testing for the most difficult
     * scenario in any method.
     *
     * @param capacity Capacity of ByteBuffer to create
     * @return The new ByteBuffer with set capacity
     */
    private static ByteBuffer createCircledByteBuffer(int capacity) {
        ByteBuffer buf = new ByteBuffer(capacity);
        try {
            for (int j = 0; j < 3; j++) {
                // add almost enough bytes
                for (int i = 0; i < capacity-1; i++)
                    buf.add((byte)0);
                for (int i = 0; i < capacity-1; i++)
                    buf.remove();
            }
        } catch (BufferException e) {
            // should not happen
            logger.error("Impossible case should not happen", e);
        }
        return buf;
    }

    private static String createDebugString(byte[] bytes) {
        try {
            return new String(bytes, "ISO-8859-1");
        } catch (Exception e) {
            logger.error("Impossible case should not happen", e);
            throw new IllegalArgumentException("Impossible case should not happen");
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidCapacityInConstructor1() {
        ByteBuffer buffer = new ByteBuffer(0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidCapacityInConstructor2() {
        ByteBuffer buffer = new ByteBuffer(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void longConstructorNegativeOffset() {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, -1, 2, TEST_BYTES1.length);
    }

    @Test(expected=IllegalArgumentException.class)
    public void longConstructorNegativeLength() {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, 0, -1, TEST_BYTES1.length);
    }

    @Test(expected=IllegalArgumentException.class)
    public void longConstructorWithLargeOffset() {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, TEST_BYTES1.length, 0, TEST_BYTES1.length);
    }

    @Test
    public void longConstructorWithZeroOffsetLength() {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, 0, 0, TEST_BYTES1.length);
    }

    @Test(expected=IllegalArgumentException.class)
    public void longConstructorWithTooSmallCapacity() {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, 0, TEST_BYTES1.length, TEST_BYTES1.length-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void longConstructorWithNegativeCapacity() {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, 0, 0, -1);
    }

    @Test
    public void constructorWithByteArray() {
        // normal case
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        Assert.assertEquals(true, buffer.equals(TEST_BYTES1));
        Assert.assertEquals(TEST_BYTES1.length, buffer.size());
        Assert.assertEquals(TEST_BYTES1.length, buffer.capacity());
        Assert.assertEquals(0, buffer.free());
        // difficult case - zero length buffer - should be okay even though
        // it doesn't make much sense
        buffer = new ByteBuffer(new byte[0]);
        Assert.assertEquals(false, buffer.equals(TEST_BYTES1));
        Assert.assertEquals(true, buffer.equals(new byte[0]));
        Assert.assertEquals(0, buffer.size());
        Assert.assertEquals(1, buffer.capacity());
        Assert.assertEquals(1, buffer.free());
    }

    @Test
    public void constructorWithByteArrayOffsetLength() {
        // normal case - entire bytes
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, 0, TEST_BYTES1.length);
        Assert.assertEquals(true, buffer.equals(TEST_BYTES1));
        Assert.assertEquals(TEST_BYTES1.length, buffer.size());
        Assert.assertEquals(TEST_BYTES1.length, buffer.capacity());
        Assert.assertEquals(0, buffer.free());
        // normal case - with offset included
        buffer = new ByteBuffer(TEST_BYTES1, 6, TEST_BYTES1.length-6);
        Assert.assertEquals(false, buffer.equals(TEST_BYTES1));
        Assert.assertEquals(true, buffer.equals(TEST_WORLD));
        Assert.assertEquals(TEST_BYTES1.length-6, buffer.size());
        Assert.assertEquals(TEST_BYTES1.length-6, buffer.capacity());
        Assert.assertEquals(0, buffer.free());
        // difficult case - zero length
        buffer = new ByteBuffer(TEST_BYTES1, 6, 0, TEST_BYTES1.length);
        Assert.assertEquals(false, buffer.equals(TEST_BYTES1));
        Assert.assertEquals(true, buffer.equals(new byte[0]));
        Assert.assertEquals(0, buffer.size());
        Assert.assertEquals(TEST_BYTES1.length, buffer.capacity());
        Assert.assertEquals(TEST_BYTES1.length, buffer.free());
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructorWithByteArrayOffsetLengthCapacityToCauseIllegalArgumentException() {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, 0, TEST_BYTES1.length, -1);     
    }

    @Test
    public void constructorWithByteArrayOffsetLengthCapacity() {
        // normal case - entire bytes
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, 0, TEST_BYTES1.length, TEST_BYTES1.length);
        Assert.assertEquals(true, buffer.equals(TEST_BYTES1));
        Assert.assertEquals(TEST_BYTES1.length, buffer.size());
        Assert.assertEquals(TEST_BYTES1.length, buffer.capacity());
        Assert.assertEquals(0, buffer.free());
        // normal case - with offset included
        buffer = new ByteBuffer(TEST_BYTES1, 6, TEST_BYTES1.length-6, TEST_BYTES1.length-6);
        Assert.assertEquals(false, buffer.equals(TEST_BYTES1));
        Assert.assertEquals(true, buffer.equals(TEST_WORLD));
        Assert.assertEquals(TEST_BYTES1.length-6, buffer.size());
        Assert.assertEquals(TEST_BYTES1.length-6, buffer.capacity());
        Assert.assertEquals(0, buffer.free());
        // difficult case - zero length
        buffer = new ByteBuffer(TEST_BYTES1, 6, 0, TEST_BYTES1.length);
        Assert.assertEquals(false, buffer.equals(TEST_BYTES1));
        Assert.assertEquals(true, buffer.equals(new byte[0]));
        Assert.assertEquals(0, buffer.size());
        Assert.assertEquals(TEST_BYTES1.length, buffer.capacity());
        Assert.assertEquals(TEST_BYTES1.length, buffer.free());
    }

    @Test
    public void size() throws BufferException {
        ByteBuffer buffer = new ByteBuffer();
        Assert.assertEquals(0, buffer.size());
        buffer.add((byte)10);
        Assert.assertEquals(1, buffer.size());
        for (int i = 0; i < 10; i++)
            buffer.add((byte)10);
        Assert.assertEquals(11, buffer.size());
    }

    @Test
    public void capacity() throws BufferException {
        int expCapacity = 100;
        ByteBuffer buffer = new ByteBuffer(expCapacity);
        int actCapacity = buffer.capacity();
        Assert.assertEquals(expCapacity, actCapacity);
    }

    @Test
    public void isFree() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(1);
        Assert.assertTrue(buffer.isFree(0));
        Assert.assertTrue(buffer.isFree(1));
        buffer.add((byte)10);
        Assert.assertFalse(buffer.isFree(1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void isFreeToCauseIllegalArgumentException() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(1);
        buffer.isFree(-1);
    }

    @Test
    public void free() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(10);
        Assert.assertEquals(10, buffer.free());
        buffer.add((byte)0);
        buffer.add((byte)0);
        buffer.add((byte)0);
        Assert.assertEquals(7, buffer.free());
    }

    @Test
    public void isEmpty() throws BufferException {
        ByteBuffer buffer = new ByteBuffer();
        Assert.assertTrue(buffer.isEmpty());
        // fill it with some data
        buffer.add((byte)10);
        Assert.assertFalse(buffer.isEmpty());
    }

    @Test
    public void isFull() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(1);
        Assert.assertTrue(buffer.isEmpty());
        // fill it with some data
        Assert.assertFalse(buffer.isFull());
        buffer.add((byte)10);
        Assert.assertTrue(buffer.isFull());
    }


    @Test(expected=BufferIsFullException.class)
    public void addToCauseBufferIsFullException() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        // append 5 bytes
        for (int i = 0; i < 5; i++)
            buffer.add((byte)10);
        // this should trigger an exception
        buffer.add((byte)11);
    }

    @Test
    public void addWithArray() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        byte[] tmpbuf = buffer.remove(TEST_BYTES1.length);
        Assert.assertArrayEquals(TEST_BYTES1, tmpbuf);
    }

    @Test(expected=BufferSizeException.class)
    public void addWithArrayToCauseBufferSizeException() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1.length-1);
        buffer.add(TEST_BYTES1);
    }

    @Test
    public void addWithArrayEmptyArrayNoException() throws BufferException {
        byte[] data = new byte[0];      // create zero length buffer
        ByteBuffer buffer = new ByteBuffer(1);
        buffer.add(data);           // this should succeed with no exception
    }

    @Test
    public void addWithArrayOffset() throws BufferException {
        // create a shorted buffer from our sample
        int offset = 5;
        byte[] tmpbuf2 = new byte[TEST_BYTES1.length-offset];
        // this should contain a copy of the correct offset array
        System.arraycopy(TEST_BYTES1, offset, tmpbuf2, 0, TEST_BYTES1.length-offset);
        // now create our buffer
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1, offset, TEST_BYTES1.length-offset);
        byte[] tmpbuf = buffer.remove(TEST_BYTES1.length-offset);
        Assert.assertArrayEquals(tmpbuf2, tmpbuf);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addWithArrayNegativeOffset() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(100);
        buffer.add(TEST_BYTES1, -1, TEST_BYTES1.length);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addWithArrayNegativeLength() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(100);
        buffer.add(TEST_BYTES1, 0, -1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addWithArrayWithTooLargeLength() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(100);
        buffer.add(TEST_BYTES1, 0, TEST_BYTES1.length+1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addWithArrayWithOffsetAndTooLargeLength() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(6);
        buffer.add(TEST_BYTES1, 5, TEST_BYTES1.length-4);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addWithArrayWithLargeOffset() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        // most difficult case where the offset is off by 1
        buffer.add(TEST_BYTES1, TEST_BYTES1.length, 0);
    }


    @Test
    public void addAndRemove() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        // append 5 bytes (0-4)
        for (int i = 0; i < 5; i++)
            buffer.add((byte)i);
        // check that each byte back matches what we would expect
        for (int i = 0; i < 5; i++) {
            byte b = buffer.remove();
            Assert.assertEquals((byte)i, b);
        }
    }

    @Test(expected=BufferIsEmptyException.class)
    public void removeToCauseBufferIsEmptyException() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        buffer.remove();
    }

    @Test(expected=BufferIsEmptyException.class)
    public void removeToCauseBufferIsEmptyException2() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        buffer.add((byte)1);
        buffer.add((byte)1);
        buffer.add((byte)1);
        buffer.remove();
        buffer.remove();
        buffer.remove();
        buffer.remove();
    }

    // does not test 'circular' feature
    @Test
    public void addAndRemoveWithByteArrayNoCircular() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1.length);
        // add it with single method
        for (int i = 0; i < TEST_BYTES1.length; i++) {
            buffer.add(TEST_BYTES1[i]);
        }
        // remove it and test for equality
        byte[] tmp = buffer.remove(TEST_BYTES1.length);
        Assert.assertArrayEquals(TEST_BYTES1, tmp);
    }

    // does test adding/removing with circular functionality
    @Test
    public void addAndRemoveWithByteArrayWithCircular() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1.length);
        // add and delete several bytes to cause a single circle to occur
        buffer.add((byte)0);
        buffer.add((byte)0);
        buffer.remove();
        buffer.remove();
        // the buffer should not effectively 'circle' around to store the last
        // two bytes in the first 2 byte positions of the underlying byte[]

        // add it with single method
        for (int i = 0; i < TEST_BYTES1.length; i++) {
            buffer.add(TEST_BYTES1[i]);
        }
        
        // remove it and test for equality
        byte[] tmp = buffer.remove(TEST_BYTES1.length);
        Assert.assertArrayEquals(TEST_BYTES1, tmp);
    }

    @Test
    public void removeByteArrayZeroCount() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        buffer.add((byte)1);
        // removing zero bytes should be ok
        byte[] tmp = buffer.remove(0);
        Assert.assertArrayEquals(new byte[0], tmp);
    }

    @Test(expected=BufferSizeException.class)
    public void removeWithByteArrayToCauseBufferSizeException() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        buffer.add((byte)1);
        // there should only be 1 byte in this buffer
        byte[] tmp = buffer.remove(2);
    }

    @Test
    public void get() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        buffer.add((byte)0);
        byte b = buffer.get(0);
        Assert.assertEquals((byte)0, b);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getToCauseIllegalArgumentException() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        buffer.add((byte)0);
        byte b = buffer.get(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getToCauseIllegalArgumentException2() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        buffer.add((byte)0);
        // buffer capacity is 5, and index of 4 is the maximum, so 5 should fail
        byte b = buffer.get(5);
    }

    @Test
    public void hashCodeZeroSized() throws BufferException {
        // hashCode's should be the same -- we'll test by making sure the other
        // values in the instance don't match such as readPosition, etc.
        ByteBuffer buffer = new ByteBuffer(100);
        int bufferHashCode = buffer.hashCode();
        ByteBuffer buffer2 = createCircledByteBuffer(300);
        int buffer2HashCode = buffer2.hashCode();
        // both hashCode's should be the same
        Assert.assertEquals(true, buffer.equals(buffer2));
        Assert.assertEquals(bufferHashCode, buffer2HashCode);
    }

    @Test
    public void hashCodeSameValuesDifferentInstances() throws BufferException {
        // hashCode's should be the same -- we'll test by making sure the other
        // values in the instance don't match such as readPosition, etc.
        byte[] text = StringUtil.getAsciiBytes("Thisisareallylongstringwithnospaces");
        ByteBuffer buffer = new ByteBuffer(text);
        int bufferHashCode = buffer.hashCode();
        ByteBuffer buffer2 = createCircledByteBuffer(300);
        buffer2.add(text);
        int buffer2HashCode = buffer2.hashCode();
        // both hashCode's should be the same
        Assert.assertEquals(true, buffer.equals(buffer2));
        Assert.assertEquals(bufferHashCode, buffer2HashCode);
    }

    @Test(expected=BufferSizeException.class)
    public void getToCauseBufferSizeException() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        // buffer is only size 0 now
        // this should fail
        byte b = buffer.get(0);
    }

    @Test(expected=BufferSizeException.class)
    public void getToCauseBufferSizeException2() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(5);
        buffer.add((byte)0);
        // buffer is only size 1 now
        // this should fail
        byte b = buffer.get(1);
    }

    // does not test 'circular' feature
    @Test
    public void removeNoCircular() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1.length);
        // add it with single method
        for (int i = 0; i < TEST_BYTES1.length; i++) {
            buffer.add(TEST_BYTES1[i]);
        }
        buffer.delete(TEST_BYTES1.length);
        Assert.assertEquals(0, buffer.size());
        Assert.assertEquals(TEST_BYTES1.length, buffer.capacity());

        // let's add data back to make sure everything is still ok
        buffer.add((byte)0);
        byte b = buffer.remove();
        Assert.assertEquals((byte)0, b);
    }

    // does test deleting with circular functionality
    @Test
    public void removeWithCircular() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        // remove it and test for equality
        byte[] tmp = buffer.remove(TEST_BYTES1.length);
        Assert.assertArrayEquals(TEST_BYTES1, tmp);
    }

    @Test
    public void deleteCountZero() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        // deleting zero is fine
        buffer.delete(0);
        // add bytes to this buffer
        buffer.add(TEST_BYTES1);
        // delete 'em all
        buffer.delete(TEST_BYTES1.length);
        Assert.assertEquals(0, buffer.size());
    }

    @Test
    public void toArray() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        byte[] copyArray = buffer.toArray();
        Assert.assertArrayEquals(TEST_BYTES1, copyArray);
    }

    @Test
    public void toArrayWithExtraCapacity() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1, TEST_BYTES1.length+20);
        byte[] copyArray = buffer.toArray();
        Assert.assertArrayEquals(TEST_BYTES1, copyArray);
        // validate our returned buffer isn't larger capacity
        Assert.assertEquals(TEST_BYTES1.length, copyArray.length);
    }
    
    @Test
    public void toArrayWithCircledBuffer() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length+20);
        buffer.add(TEST_BYTES1);
        byte[] copyArray = buffer.toArray();
        Assert.assertArrayEquals(TEST_BYTES1, copyArray);
    }
    
    @Test
    public void toArrayZeroLength() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length+20);
        //buffer.add(TEST_BYTES1);
        byte[] zeroArray = new byte[0];
        byte[] copyArray = buffer.toArray();
        Assert.assertArrayEquals(zeroArray, copyArray);
    }

    

    @Test(expected=IllegalArgumentException.class)
    public void toArrayWithOffsetLengthSmallCapacity() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        byte[] copyArray = buffer.toArray(5, 2, 1);
    }

    @Test
    public void toArrayWithOffsetLengthCapacity() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        byte[] copyArray = buffer.toArray(3, 1, 1);
        Assert.assertArrayEquals(this.TEST_L, copyArray);
        copyArray = buffer.toArray(5, 2, 2);
        Assert.assertArrayEquals(this.TEST_SPACEW, copyArray);
        copyArray = buffer.toArray(10, 1, 4);
        byte[] tmp = new byte[4];
        tmp[0] = (byte)'D';
        Assert.assertArrayEquals(tmp, copyArray);
        Assert.assertEquals(4, copyArray.length);
    }

    @Test
    public void toArrayWithOffsetLengthCapacityWithCircledBuffer() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        byte[] copyArray = buffer.toArray(3, 1, 1);
        Assert.assertArrayEquals(this.TEST_L, copyArray);
        copyArray = buffer.toArray(5, 2, 2);
        Assert.assertArrayEquals(this.TEST_SPACEW, copyArray);
        copyArray = buffer.toArray(10, 1, 4);
        byte[] tmp = new byte[4];
        tmp[0] = (byte)'D';
        Assert.assertArrayEquals(tmp, copyArray);
        Assert.assertEquals(4, copyArray.length);
    }

    @Test
    public void toArrayWithTargetBuffer() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        // create our new targetBuffer
        byte[] targetBuffer = new byte[TEST_BYTES1.length];
        buffer.toArray(0, buffer.size(), targetBuffer, 0);
        Assert.assertEquals(targetBuffer.length, TEST_BYTES1.length);
        Assert.assertArrayEquals(TEST_BYTES1, targetBuffer);
    }

    @Test
    public void toArrayWithTargetBufferOffset() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        // create our new targetBuffer
        byte[] targetBuffer = new byte[5];
        buffer.toArray(6, 5, targetBuffer, 0);
        Assert.assertArrayEquals(TEST_WORLD, targetBuffer);
    }

    @Test(expected=IllegalArgumentException.class)
    public void toArrayWithTargetSmallCapacity() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        // create our new targetBuffer
        byte[] targetBuffer = new byte[5];
        buffer.toArray(5, 6, targetBuffer, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void toArrayWithTargetBadTargetOffset() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        // create our new targetBuffer
        byte[] targetBuffer = new byte[6];
        buffer.toArray(0, 6, targetBuffer, 1);
    }

    @Test
    public void startsWith() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        Assert.assertEquals(true, buffer.startsWith(TEST_H));
        Assert.assertEquals(true, buffer.startsWith(TEST_HELLO));
        Assert.assertEquals(false, buffer.startsWith(TEST_WORLD));
        Assert.assertEquals(false, buffer.startsWith(TEST_W));
        buffer.clear();
        buffer.add(TEST_WORLD);
        Assert.assertEquals(false, buffer.startsWith(TEST_H));
        Assert.assertEquals(false, buffer.startsWith(TEST_HELLO));
        Assert.assertEquals(true, buffer.startsWith(TEST_WORLD));
        Assert.assertEquals(true, buffer.startsWith(TEST_W));
        buffer.clear();
        buffer.add(TEST_HELLO);
        Assert.assertEquals(true, buffer.startsWith(TEST_H));
        Assert.assertEquals(true, buffer.startsWith(TEST_HELLO));
        // test a longer buffer that does start with the same buffer
        Assert.assertEquals(false, buffer.startsWith(TEST_BYTES1));
    }

    @Test
    public void endsWith() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        //byte[] tmpbuf = buffer.toArray();
        //String tmpstr = createDebugString(tmpbuf);
        //logger.debug("buffer currently contains=" + tmpstr);
        Assert.assertEquals(false, buffer.endsWith(TEST_H));
        Assert.assertEquals(false, buffer.endsWith(TEST_HELLO));
        Assert.assertEquals(true, buffer.endsWith(TEST_WORLD));
        buffer.clear();
        buffer.add(TEST_WORLD);
        //byte[] tmpbuf2 = buffer.toArray();
        //String tmpstr2 = createDebugString(tmpbuf2);
        //logger.debug("buffer currently contains=" + tmpstr2);
        Assert.assertEquals(true, buffer.endsWith(TEST_D));
        Assert.assertEquals(false, buffer.endsWith(TEST_HELLO));
        Assert.assertEquals(true, buffer.endsWith(TEST_WORLD));
        Assert.assertEquals(false, buffer.endsWith(TEST_W));
        buffer.clear();
        buffer.add(TEST_HELLO);
        Assert.assertEquals(false, buffer.endsWith(TEST_H));
        Assert.assertEquals(true, buffer.endsWith(TEST_HELLO));
        // test a longer buffer that does start with the same buffer
        Assert.assertEquals(false, buffer.endsWith(TEST_BYTES1));
    }

    @Test
    public void indexOf() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        // test normal case
        Assert.assertEquals(0, buffer.indexOf(TEST_H));
        Assert.assertEquals(0, buffer.indexOf(TEST_HELLO));
        Assert.assertEquals(0, buffer.indexOf(TEST_BYTES1));
        // test too long now
        buffer.remove();
        Assert.assertEquals(-1, buffer.indexOf(TEST_BYTES1));
        // test end -- buffer should be 1 less now such as ELLO WORLD
        Assert.assertEquals(9, buffer.indexOf(TEST_D));
        // test middle
        Assert.assertEquals(-1, buffer.indexOf(TEST_SPACEH));
        Assert.assertEquals(4, buffer.indexOf(TEST_SPACEW));
        Assert.assertEquals(5, buffer.indexOf(TEST_WORLD));
        buffer.clear();
        Assert.assertEquals(-1, buffer.indexOf(TEST_SPACEH));
    }

    @Test(expected=IllegalArgumentException.class)
    public void indexOfWithNegativeOffset() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        buffer.indexOf(TEST_H, -1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void indexOfWithTooLargeOffset() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        buffer.indexOf(TEST_H, TEST_BYTES1.length);
    }

    @Test
    public void indexOfWithOffset() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        // test normal case
        Assert.assertEquals(0, buffer.indexOf(TEST_H, 0));
        Assert.assertEquals(-1, buffer.indexOf(TEST_H, 1));
        // empty data is always -1
        Assert.assertEquals(-1, buffer.indexOf(new byte[0], 0));
        Assert.assertEquals(10, buffer.indexOf(TEST_D, 0));
        Assert.assertEquals(10, buffer.indexOf(TEST_D, 10));
        Assert.assertEquals(10, buffer.indexOf(TEST_D, 3));
        Assert.assertEquals(2, buffer.indexOf(TEST_LL, 0));
        Assert.assertEquals(2, buffer.indexOf(TEST_LL, 2));
        Assert.assertEquals(6, buffer.indexOf(TEST_WORLD));
        Assert.assertEquals(5, buffer.indexOf(TEST_SPACEW));
        
    }
    
    @Test
    public void indexOfWithOffset2() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES2.length);
        buffer.add(TEST_BYTES2);
        // test normal case
        Assert.assertEquals(-1, buffer.indexOf(TEST_AA, 11));
    }

    @Test
    public void occurrences() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        Assert.assertEquals(-1, buffer.occurrences(new byte[0]));
        Assert.assertEquals(3, buffer.occurrences(TEST_L));
        Assert.assertEquals(1, buffer.occurrences(TEST_LL));
        Assert.assertEquals(1, buffer.occurrences(TEST_H));
        byte[] tmp = {'A'};
        Assert.assertEquals(0, buffer.occurrences(tmp));
    }

    @Test
    public void occurrencesWithByte() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        Assert.assertEquals(3, buffer.occurrences((byte)'L'));
        Assert.assertEquals(1, buffer.occurrences((byte)'H'));
        Assert.assertEquals(0, buffer.occurrences((byte)'A'));
    }

    @Test
    public void occurrencesWithOverlap() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES2);
        Assert.assertEquals(-1, buffer.occurrences(new byte[0]));
        Assert.assertEquals(1, buffer.occurrences(TEST_C));
        Assert.assertEquals(4, buffer.occurrences(TEST_AA));
        Assert.assertEquals(2, buffer.occurrences(TEST_AB));
        Assert.assertEquals(3, buffer.occurrences(TEST_BB));
    }

    @Test
    public void equals() throws BufferException {
        ByteBuffer buffer = createCircledByteBuffer(TEST_BYTES1.length);
        buffer.add(TEST_BYTES1);
        ByteBuffer buffer2 = createCircledByteBuffer(TEST_BYTES1.length);
        buffer2.add(TEST_BYTES1);
        // test normal case
        Assert.assertEquals(false, buffer.equals((Object)TEST_BYTES1));
        Assert.assertEquals(true, buffer.equals(buffer2));
        buffer2.remove();
        Assert.assertEquals(false, buffer.equals(buffer2));
        // test override for byte[]
        Assert.assertEquals(true, buffer.equals(TEST_BYTES1));
    }

    public void equalsWithZeroArrays() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(1);
        buffer.add(new byte[0]);
        Assert.assertEquals(true, buffer.equals(new byte[0]));
        ByteBuffer buffer2 = new ByteBuffer(1);
        Assert.assertEquals(true, buffer.equals(buffer2));
    }

    @Test
    public void testToString() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        //System.out.println("String: '" + buffer + "'");
        Assert.assertEquals(true, buffer.toString().equals("HELLO WORLD"));
    }

    @Test
    public void toHexString() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        //System.out.println("Hex String: '" + buffer.toHexString() + "'");
        Assert.assertEquals(true, buffer.toHexString().equals("48454c4c4f20574f524c44".toUpperCase()));
        Assert.assertEquals(true, buffer.toHexString(2, 2).equals("4c4c".toUpperCase()));
    }


    @Test
    public void copyWithOffsetLengthCapacity() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        // copy only a small portion - WORLD
        ByteBuffer copyBuffer = buffer.copy(6, 5, 5);
        Assert.assertArrayEquals(TEST_WORLD, copyBuffer.toArray());
        // make sure the new copyBuffer really works now
        copyBuffer.delete(4);
        byte b = copyBuffer.remove();
        Assert.assertEquals((byte)'D', b);
        copyBuffer.add(TEST_WORLD);
        Assert.assertArrayEquals(TEST_WORLD, copyBuffer.toArray());
        // let's make a small copy -- should be quick
        ByteBuffer copyBuffer2 = copyBuffer.copy(0, 1, 1);
        Assert.assertArrayEquals(TEST_W, copyBuffer2.toArray());
        // a copy of zero bytes should be allowed...
        ByteBuffer copyBuffer3 = copyBuffer2.copy(0, 0, 0);
        Assert.assertArrayEquals(new byte[0], copyBuffer3.toArray());
    }

    @Test
    public void copyWithOffsetLengthCapacityInvalidOffset() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        ByteBuffer copyBuffer = buffer.copy(11, 0, 1);
        Assert.assertEquals(0, copyBuffer.size());
    }

    @Test(expected=IllegalArgumentException.class)
    public void copyWithOffsetLengthCapacityInvalidLength() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        ByteBuffer copyBuffer = buffer.copy(10, 2, 2);
    }

    @Test(expected=IllegalArgumentException.class)
    public void copyWithOffsetLengthCapacityInvalidCapacity() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        ByteBuffer copyBuffer = buffer.copy(10, 1, 0);
    }

    /**
    @Test
    public void split() throws BufferException {
        ByteBuffer buffer = new ByteBuffer(TEST_BYTES1);
        // no delimiter found
        ByteBuffer[] buffers = buffer.split(this.TEST_C);
        Assert.assertEquals(buffer, buffers[0]);
        // break up on space
        buffers = buffer.split(this.TEST_SPACE);
        Assert.assertEquals(2, buffers.length);
        Assert.assertArrayEquals(this.TEST_HELLO, buffers[0].toArray());
        Assert.assertArrayEquals(this.TEST_WORLD, buffers[1].toArray());
        // break up on L
        buffers = buffer.split(this.TEST_L);
        Assert.assertEquals(4, buffers.length);
        Assert.assertArrayEquals(new byte[0], buffers[1].toArray());
        Assert.assertArrayEquals(this.TEST_D, buffers[3].toArray());

        // this example caused some null tokens, instead of an empty array
        ByteBuffer buffer2 = new ByteBuffer("11,\"AE\",,\"7D\",,\"EA\",");
        int pos = buffer2.indexOf(COMMA, 15);
        Assert.assertEquals(19, pos);
        buffers = buffer2.split(COMMA);
        Assert.assertEquals(7, buffers.length);
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i] == null) {
                Assert.fail("buffers[" + i + "] was null, this should never happen, should be empty array");
            }
        }
    }

    @Test
    public void splitZeroDataInBuffer() throws BufferException {
        ByteBuffer buffer = new ByteBuffer();
        ByteBuffer[] buffers = buffer.split(TEST_C);
        Assert.assertEquals(0, buffers[0].size());
        Assert.assertEquals(buffer, buffers[0]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void splitWithBadDelimiter() throws BufferException {
        ByteBuffer buffer = new ByteBuffer();
        ByteBuffer[] buffers = buffer.split(new byte[0]);
    }
     */

}
