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

package com.cloudhopper.commons.gsm;

/**
 * Class that represents a Type of Address in the GSM mobile world. A type of
 * address contains both a TON (type of number) and NPI (numbering plan indicator).
 * 
 * @author joelauer
 */
public class TypeOfAddress {

    private Ton ton;
    private Npi npi;

    public TypeOfAddress(Ton ton, Npi npi) {
        this.ton = ton;
        this.npi = npi;
    }

    public Ton getTon() {
        return ton;
    }

    public void setTon(Ton ton) {
        this.ton = ton;
    }

    public Npi getNpi() {
        return npi;
    }

    public void setNpi(Npi npi) {
        this.npi = npi;
    }

    /**
     * To a GSM-encoded type of address byte.
     * @return The byte representing a type of address
     */
    public byte toByte() {
        return toByte(this);
    }

    /**
     * To a GSM-encoded type of address byte.
     * @return The byte representing a type of address
     */
    public static byte toByte(TypeOfAddress toa) {
        byte b = 0;
        if (toa.getTon() != null) {
            b |= ( toa.getTon().toInt() << 0 );
        }
        if (toa.getNpi() != null) {
            b |= ( toa.getNpi().toInt() << 4 );
        }
        b |= ( 1 << 7 );
        return b;
    }

    /**
     * Creates a TypeOfAddress from a GSM-encoded type of address byte.
     * @return
     */
    public static TypeOfAddress valueOf(byte b) {
        // bits 0-3 are the ton (shift over 0, then only the first 4 bits)
        int ton = (b & 0x0F);
        // bits 4-7 are the npi (shift over 4, then only the first 3 bits)
        int npi = ((b >> 4) & 0x07);
        // create our type of address now
        return new TypeOfAddress(Ton.fromInt(ton), Npi.fromInt(npi));
    }

    @Override
    public String toString() {
        return "ton=" + ton + ", npi=" + npi;
    }
}
