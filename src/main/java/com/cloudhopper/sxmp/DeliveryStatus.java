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
import org.apache.log4j.Logger;

// my imports

/**
 *
 * @author joelauer
 */
public class DeliveryStatus {
    private static final Logger logger = Logger.getLogger(DeliveryStatus.class);

    private final Integer code;
    private final String message;

    public DeliveryStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return new StringBuilder(20)
            .append("[")
            .append(code)
            .append(", ")
            .append(message)
            .append("]")
            .toString();
    }
}