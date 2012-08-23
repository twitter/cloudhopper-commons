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

import org.xml.sax.SAXException;

/**
 * Exception thrown when the XML could not be parsed and recognized as a valid
 * SXMP operation.  Will contain an error code, message, and if possible, it
 * will contain the Operation object that was at least partially parsed.
 *
 * The partially parsed Operation object is essential to correctly returning
 * the operation type in the XML response.  If the operation is null, then
 * a generic error will be returned to the caller (e.g. an HTTP non-200 error
 * code vs. a 200 error code).
 * 
 * @author joelauer
 */
public class SxmpParsingException extends SAXException {
    private static final long serialVersionUID = 0L;

    private SxmpErrorCode errorCode;
    private String errorMessage;
    private Operation operation;

    public SxmpParsingException(SxmpErrorCode errorCode, String msg, Operation operation) {
        super("SXMP error (code [" + errorCode.getIntValue() + "] message [" + msg + "]");
        this.errorCode = errorCode;
        this.errorMessage = msg;
        this.operation = operation;
    }

    public SxmpErrorCode getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Gets the partially parsed Operation if it exists.  If this is null,
     * then a valid SXMP error response could not be returned and a generic
     * operation error should be returned.
     * @return The operation at least partially parsed or null if it doesn't
     *      exist.
     */
    public Operation getOperation() {
        return this.operation;
    }
}
