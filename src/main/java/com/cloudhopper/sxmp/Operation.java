package com.cloudhopper.sxmp;

/*
 * #%L
 * ch-sxmp
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
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

import com.cloudhopper.commons.util.StringUtil;
import com.cloudhopper.sxmp.util.ToStringUtil;

/**
 *
 * @author joelauer
 */
public abstract class Operation {

    public enum Type {
        SUBMIT("submit"),
        DELIVER("deliver"),
        DELIVERY_REPORT("deliveryReport");

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        public String getValue() {
            return this.type;
        }

        public static Type parse(final String type) {
            for (Type e : Type.values()) {
                if (e.type.equals(type)) {
                    return e;
                }
            }
            return null;
        }
    };

    private final Type type;
    private final boolean isRequest;
    private String referenceId;
    private String ticketId;
    protected String version = SxmpParser.VERSION_1_0;  // default version

    public Operation(Type value, boolean isRequest) {
        this.type = value;
        this.isRequest = isRequest;
    }

    public Type getType() {
        return this.type;
    }

    public boolean isRequest() {
        return this.isRequest;
    }

    public boolean isResponse() {
        return !this.isRequest;
    }

    public String getVersion() {
        return this.version;
    }

   public void setReferenceId(String value) throws SxmpErrorException {
        // only check if its a valid reference id here
        if (value != null) {
            // validate the value contains only safe chars
            if (!StringUtil.isSafeString(value)) {
                throw new SxmpErrorException(SxmpErrorCode.INVALID_REFERENCE_ID, "One or more characters in the referenceId value are not allowed");
            }
        }
        this.referenceId = value;
    }

    public String getReferenceId() {
        return this.referenceId;
    }


    public void setTicketId(String value) {
        this.ticketId = value;
    }

    public String getTicketId() {
        return this.ticketId;
    }

    /**
     * Validates the operation whether it contains all required properties
     * per the SXMP protocol specifications.
     * @throws SxmpErrorException Thrown if any required elements are missing.
     */
    public abstract void validate() throws SxmpErrorException;

    @Override
    public String toString() {
        return new StringBuilder(50)
            .append(this.getClass().getSimpleName())
            .append(" (referenceId [")
            .append(ToStringUtil.nullSafe(this.referenceId))
            .append("] ticketId [")
            .append(ToStringUtil.nullSafe(this.ticketId))
            .append("])")
            .toString();
    }

}
