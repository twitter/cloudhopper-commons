/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.cloudhopper.sxmp;

// third party imports
import com.cloudhopper.sxmp.util.MobileAddressUtil;
import com.cloudhopper.sxmp.util.ToStringUtil;
import org.apache.log4j.Logger;

// my imports

/**
 *
 * @author joelauer
 */
public class MobileAddress {
    private static final Logger logger = Logger.getLogger(MobileAddress.class);

    public enum Type {
        NETWORK,
        INTERNATIONAL,
        NATIONAL,
        ALPHANUMERIC
    };

    private Type type;
    private String address;

    public MobileAddress() {
        // do nothing
    }

    public MobileAddress(Type type, String address) throws SxmpErrorException {
        setAddress(type, address);
    }

    public Type getType() {
        return this.type;
    }

    public void setAddress(Type type, String address) throws SxmpErrorException {
        // make sure the type isn't null
        if (type == null) {
            throw new SxmpErrorException(SxmpErrorCode.EMPTY_VALUE, "The address type cannot be null");
        }
        this.type = type;

        // test for correctly formatted address
        if (type == Type.NETWORK) {
            MobileAddressUtil.validateNetworkAddress(address);
        } else if (type == Type.NATIONAL) {
            // same rules apply to national as network
            MobileAddressUtil.validateNetworkAddress(address);
        } else if (type == Type.ALPHANUMERIC) {
            // do nothing special to validate
        } else if (type == Type.INTERNATIONAL) {
            MobileAddressUtil.validateInternationalAddress(address);
        } else {
            throw new SxmpErrorException(SxmpErrorCode.UNSUPPORTED_ADDRESS_TYPE, "Unsupported address type [" + type + "] found");
        }
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(40);
        //buf.append("type: ");
        buf.append(ToStringUtil.nullSafe(getType()));
        buf.append(" ");
        buf.append(ToStringUtil.nullSafe(getAddress()));
        return buf.toString();
    }
}