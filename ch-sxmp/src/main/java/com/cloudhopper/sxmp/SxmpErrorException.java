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

/**
 * Exception thrown when the XML was parsed correctly enough to produce a valid
 * XML error response.
 * 
 * @author joelauer
 */
public class SxmpErrorException extends Exception {
    private static final long serialVersionUID = 0L;

    private SxmpErrorCode errorCode;
    private String errorMessage;
    private Operation.Type operationType;
    
    public SxmpErrorException(SxmpErrorCode errorCode, String msg) {
        this(errorCode, msg, (Operation.Type)null);
    }

    public SxmpErrorException(SxmpErrorCode errorCode, String msg, Operation.Type operationType) {
        super("SXMP error (code [" + errorCode.getIntValue() + "] message [" + msg + "]");
        this.errorCode = errorCode;
        this.errorMessage = msg;
	this.operationType = operationType;
    }

    public SxmpErrorException(SxmpErrorCode errorCode, String msg, Throwable cause) {
        this(errorCode, msg, (Operation.Type)null, cause);
    }

    public SxmpErrorException(SxmpErrorCode errorCode, String msg, Operation.Type operationType, Throwable cause) {
        super("SXMP error (code [" + errorCode.getIntValue() + "] message [" + msg + "]", cause);
        this.errorCode = errorCode;
        this.errorMessage = msg;
	this.operationType = operationType;
    }

    public SxmpErrorCode getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Operation.Type getOperationType() {
	return this.operationType;
    }
    
}
