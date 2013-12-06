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

import com.cloudhopper.sxmp.util.ToStringUtil;

/**
 *
 * @author joelauer
 */
public abstract class Response extends Operation {

    private Integer errorCode;
    private String errorMessage;

    public Response(Operation.Type value) {
        super(value, false);
        // default error code and message are OK
        this.errorCode = SxmpErrorCode.OK.getIntValue();
        this.errorMessage = "OK";
    }

    public void setErrorCode(Integer value) {
        this.errorCode = value;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public void validate() throws SxmpErrorException {
        // just an error code is required
        if (getErrorCode() == null) {
            throw new SxmpErrorException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "A error code value is mandatory with a response");
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(100)
            .append(super.toString())
            .append(" (error code [")
            .append(ToStringUtil.nullSafe(this.errorCode))
            .append("] message [")
            .append(ToStringUtil.nullSafe(this.errorMessage))
            .append("])")
            .toString();
    }

}
