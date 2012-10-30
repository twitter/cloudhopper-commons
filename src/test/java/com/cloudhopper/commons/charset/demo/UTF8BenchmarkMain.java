package com.cloudhopper.commons.charset.demo;

/*
 * #%L
 * ch-commons-charset
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

import com.cloudhopper.commons.charset.CharSequenceAccessor;
import com.cloudhopper.commons.charset.CharSequenceAccessor.CharArrayWrapper;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.charset.ModifiedUTF8Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class UTF8BenchmarkMain {
    private static final Logger logger = LoggerFactory.getLogger(UTF8BenchmarkMain.class);
    
    static public void main(String[] args) throws Exception {
        // strings to decode/encode to/from UTF-8
        // various types of strings are harder to decode
        // ASCII-only (bytes 32-126)
        String controlCharsString = createStringWithCharRange('\u0000', 0x20);
        String asciiOnlyString = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        String iso88591CharsString = createStringWithCharRange('\u0080', 128);
        String remainingCharsString = createStringWithCharRange('\u0100', 65279);
        String twoKCharsString = createStringWithCharRange('\u0100', 2000);
        String latin300CharString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent commodo vestibulum tellus at rutrum. Ut in ipsum augue, eget posuere nulla. Quisque elementum ante ut leo euismod nec hendrerit lectus lobortis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia posuere.";
        
        int count = 1000000;
        
        String[] strings = new String[] { twoKCharsString };
        byte[][] byteArrays = createUTF8ByteArrays(strings);
        
        EncodeBenchmark[] encodeBenchmarks = new EncodeBenchmark[] {
            new EncodeBenchmark("JVM String.getBytes()", new EncodeStringGetBytes(), strings),
            new EncodeBenchmark("CharsetUtil.encode() w/ UTF-8 Charset", new EncodeCharsetUtil(), strings),
            new EncodeBenchmark("CharsetUtil.encode() w/ Modified UTF-8 Charset", new EncodeModifiedUTF8Charset(), strings),
            new EncodeBenchmark("CharsetUtil.encode() w/ Modified UTF-8 Charset (mocking a buffer already allocated)", new EncodeModifiedUTF8CharsetNoByteLengthCheck(), strings)
        };
        
        DecodeBenchmark[] decodeBenchmarks = new DecodeBenchmark[] {
            new DecodeBenchmark("JVM new String()", new DecodeNewString(), byteArrays),
            new DecodeBenchmark("CharsetUtil.decode() w/ UTF-8 Charset", new DecodeCharsetUtil(), byteArrays),
            new DecodeBenchmark("CharsetUtil.decode() w/ Modified UTF-8 Charset", new DecodeModifiedUTF8Charset(), byteArrays)
        };
        
        // warmup each benchmark first
        for (EncodeBenchmark eb : encodeBenchmarks) {
            eb.warmup(1);
        }
        
        // warmup each benchmark first
        for (DecodeBenchmark db : decodeBenchmarks) {
            db.warmup(1);
        }
        
        // run each benchmark
        for (EncodeBenchmark eb : encodeBenchmarks) {
            System.gc();
            eb.run(count);
        }
        
        // run each benchmark
        for (DecodeBenchmark db : decodeBenchmarks) {
            System.gc();
            db.run(count);
        }
    }

    static public String createStringWithCharRange(char start, int length) {
        StringBuilder buf = new StringBuilder(length);
        int end = start+length;
        for (int i = start; i < end; i++) {
            buf.append((char)i);
        }
        return buf.toString();
    }
    
    static public byte[][] createUTF8ByteArrays(String[] strings) throws Exception {
        byte[][] byteArrays = new byte[strings.length][];
        for (int i = 0; i < strings.length; i++) {
            byteArrays[i] = strings[i].getBytes("UTF-8");
        }
        return byteArrays;
    }
    
    static public class EncodeBenchmark {
        public String name;
        public EncodeTest test;
        public String[] strings;

        public EncodeBenchmark(String name, EncodeTest test, String[] strings) {
            this.name = name;
            this.test = test;
            this.strings = strings;
        }
        
        public void warmup(int count) throws Exception {
            this.test.encode(strings, count);
        }
        
        public void run(int count) throws Exception {
            long startMillis = System.currentTimeMillis();
            this.test.encode(strings, count);
            long stopMillis = System.currentTimeMillis();
            System.out.println("Encode Benchmark: " + name);
            System.out.println(" count: " + count);
            System.out.println("  time: " + (stopMillis-startMillis) + " ms");
        }
    }
    
    static public class DecodeBenchmark {
        public String name;
        public DecodeTest test;
        public byte[][] byteArrays;

        public DecodeBenchmark(String name, DecodeTest test, byte[][] byteArrays) {
            this.name = name;
            this.test = test;
            this.byteArrays = byteArrays;
        }
        
        public void warmup(int count) throws Exception {
            this.test.decode(byteArrays, count);
        }
        
        public void run(int count) throws Exception {
            long startMillis = System.currentTimeMillis();
            this.test.decode(byteArrays, count);
            long stopMillis = System.currentTimeMillis();
            System.out.println("Decode Benchmark: " + name);
            System.out.println(" count: " + count);
            System.out.println("  time: " + (stopMillis-startMillis) + " ms");
        }
    }
    
    static public interface EncodeTest {
        public void encode(String[] strings, int count) throws Exception;
    }
    
    static public interface DecodeTest {
        public void decode(byte[][] byteArrays, int count) throws Exception;
    }
    
    static public class EncodeStringGetBytes implements EncodeTest {
        @Override
        public void encode(String[] strings, int count) throws Exception {
            for (int i = 0; i < count; i++) {
                for (String s : strings) {
                    byte[] b = s.getBytes("UTF-8");
                }
            }
        }
    }
    
    static public class EncodeCharsetUtil implements EncodeTest {
        @Override
        public void encode(String[] strings, int count) throws Exception {
            for (int i = 0; i < count; i++) {
                for (String s : strings) {
                    byte[] b = CharsetUtil.encode(s, CharsetUtil.CHARSET_UTF_8);
                }
            }
        }
    }
    
    static public class EncodeModifiedUTF8Charset implements EncodeTest {
        ModifiedUTF8Charset charset = new ModifiedUTF8Charset();
        @Override
        public void encode(String[] strings, int count) throws Exception {
            for (int i = 0; i < count; i++) {
                for (String s : strings) {
                    byte[] b = charset.encode(s);
                }
            }
        }
    }
    
    static public class EncodeModifiedUTF8CharsetNoByteLengthCheck implements EncodeTest {
        @Override
        public void encode(String[] strings, int count) throws Exception {
            // mimic use case where we'd be serializing a string to a byte array
            // that was already allocated -- the only other method would be to
            // usually do a String.getBytes() and then a byte copy
            byte[] buf = new byte[6000];
            for (int i = 0; i < count; i++) {
                for (String s : strings) {
                    CharArrayWrapper wrapper = CharSequenceAccessor.access(s);
                    ModifiedUTF8Charset.encodeToByteArray(null, wrapper.value, wrapper.offset, wrapper.length, buf, 0);
                }
            }
        }
    }
    
    static public class DecodeNewString implements DecodeTest {
        @Override
        public void decode(byte[][] byteArrays, int count) throws Exception {
            for (int i = 0; i < count; i++) {
                for (byte[] b : byteArrays) {
                    String s = new String(b, "UTF-8");
                }
            }
        }
    }
    
    static public class DecodeCharsetUtil implements DecodeTest {
        @Override
        public void decode(byte[][] byteArrays, int count) throws Exception {
            for (int i = 0; i < count; i++) {
                for (byte[] b : byteArrays) {
                    String s = CharsetUtil.decode(b, CharsetUtil.CHARSET_UTF_8);
                }
            }
        }
    }
    
    static public class DecodeModifiedUTF8Charset implements DecodeTest {
        ModifiedUTF8Charset charset = new ModifiedUTF8Charset();
        @Override
        public void decode(byte[][] byteArrays, int count) throws Exception {
            for (int i = 0; i < count; i++) {
                for (byte[] b : byteArrays) {
                    String s = charset.decode(b);
                }
            }
        }
    }
    
}
