package com.cloudhopper.commons.gsm;

/*
 * #%L
 * ch-commons-gsm
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
 * Enum class that represents a Type of Number (TON) in the GSM world.
 *
 * @author joelauer
 */
public enum Ton {

    UNKNOWN (GsmConstants.TON_UNKNOWN),
    INTERNATIONAL (GsmConstants.TON_INTERNATIONAL),
    NATIONAL (GsmConstants.TON_NATIONAL),
    NETWORK (GsmConstants.TON_NETWORK),
    SUBSCRIBER (GsmConstants.TON_SUBSCRIBER),
    ALPHANUMERIC (GsmConstants.TON_ALPHANUMERIC),
    ABBREVIATED (GsmConstants.TON_ABBREVIATED);

    private  final int ton;

    Ton(final int ton) {
        this.ton = ton;
    }

    public int toInt() {
        return this.ton;
    }

    public static Ton fromInt(final int ton) {
        for (Ton e : Ton.values()) {
            if (e.ton == ton) {
                return e;
            }
        }
        return null;
    }

}
