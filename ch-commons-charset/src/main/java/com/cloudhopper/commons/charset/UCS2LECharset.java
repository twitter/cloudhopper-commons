package com.cloudhopper.commons.charset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

/**
 * Charset for UCS2LE (ISO-10646-UCS-2 in Java converted to Little Endian).
 *
 * @author David Wilkie <dwilkie@gmail.com>
 */
public class UCS2LECharset extends UCS2Charset {
    @Override
    public byte[] encode(CharSequence str0) {
      return getLittleEndianBytes(super.encode(str0));
    }

    @Override
    public void decode(byte[] bytes, StringBuilder buffer) {
      super.decode(getLittleEndianBytes(bytes), buffer);
    }

    @Override
    public String decode(byte[] bytes) {
      return super.decode(getLittleEndianBytes(bytes));
    }

    private byte[] getLittleEndianBytes(byte [] bytes) {
      if (bytes == null) {
          return null;
      }
      ByteBuffer sourceByteBuffer = ByteBuffer.wrap(bytes);
      ByteBuffer destByteBuffer = ByteBuffer.allocate(bytes.length);

      destByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      while(sourceByteBuffer.hasRemaining()) {
        destByteBuffer.putShort(sourceByteBuffer.getShort());
      }

      return destByteBuffer.array();
    }
}
