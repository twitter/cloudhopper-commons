package com.cloudhopper.httpclient.util;

/*
 * #%L
 * ch-httpclient-util
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

import java.io.IOException;

/**
 * Extends an IOException so that any HTTP status code other than what the caller
 * expects will generate a specific error.  Also, the error message will contain
 * a useful description of what ocurred.
 * 
 * @author joelauer
 */
public class UnexpectedHttpStatusCodeException extends IOException {
    private static final long serialVersionUID = 1L;

    int[] expected;
    int actual;
    String responseBody;

    public UnexpectedHttpStatusCodeException(int expectedStatusCode, String msg) {
        this(expectedStatusCode, -1, msg, null);
    }

    public UnexpectedHttpStatusCodeException(int expectedStatusCode, int actualStatusCode, String msg) {
        this(expectedStatusCode, actualStatusCode, msg, null);
    }

    public UnexpectedHttpStatusCodeException(int expectedStatusCode, int actualStatusCode, String msg, String responseBody) {
        this(new int[] { expectedStatusCode }, actualStatusCode, responseBody);
    }

    public UnexpectedHttpStatusCodeException(int[] expectedStatusCodes, String msg) {
        this(expectedStatusCodes, -1, msg, null);
    }

    public UnexpectedHttpStatusCodeException(int[] expectedStatusCodes, int actualStatusCode, String msg) {
        this(expectedStatusCodes, actualStatusCode, msg, null);
    }

    public UnexpectedHttpStatusCodeException(int[] expectedStatusCodes, int actualStatusCode, String msg, String responseBody) {
        super(msg);
        this.expected = expectedStatusCodes;
        this.actual = actualStatusCode;
        this.responseBody = responseBody;
    }

    /**
     * Gets the status code we expected.
     * @return The status code we expected.
     */
    public int[] getExpectedStatusCodes() {
        return this.expected;
    }

    /**
     * Gets the actual status code we got.  In the case where a status code
     * wasn't read, this will be -1;
     * @return -1 if no status code read or the actual status code.
     */
    public int getActualStatusCode() {
        return this.actual;
    }

    /**
     * Gets the body of the HTTP response if there was one.
     * @return The body of the HTTP response.
     */
    public String getResponseBody() {
        return this.responseBody;
    }

}
