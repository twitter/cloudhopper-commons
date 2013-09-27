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

import com.cloudhopper.stratus.type.OptionalParamMap;
import com.cloudhopper.sxmp.util.ToStringUtil;
import java.util.Map;

/**
 *
 * @author joelauer
 */
public abstract class MessageRequest<E extends Response> extends Request<E> {

    // common attributes in any message (submit or deliver)
    private Integer operatorId;
    private MobileAddress sourceAddress;
    private MobileAddress destinationAddress;
    // for "text" messages
    private TextEncoding textEncoding;
    private String text;
    private OptionalParamMap optionalParams;
    // for "data" messages ->  <data udh="true|false" coding="00">ffffff</data>
    //private Boolean hasUserDataHeader;
    //private Byte dataCoding;
    //private byte[] data;
    private Priority priority;

    public MessageRequest(Operation.Type type) {
        super(type);
        // default charset is UTF-8
        this.textEncoding = TextEncoding.UTF_8;
	// default priority is NORMAL
	//this.priority = Priority.NORMAL;
    }

    public void setOperatorId(Integer value) throws SxmpErrorException {
        if (value != null && value < 0) {
            throw new SxmpErrorException(SxmpErrorCode.INVALID_VALUE, "The [operatorId] must be greater than or equal to 0");
        }
        this.operatorId = value;
    }

    public Integer getOperatorId() {
        return this.operatorId;
    }

    public void setSourceAddress(MobileAddress value) {
        this.sourceAddress = value;
    }

    public MobileAddress getSourceAddress() {
        return this.sourceAddress;
    }

    public void setDestinationAddress(MobileAddress value) {
        this.destinationAddress = value;
    }

    public MobileAddress getDestinationAddress() {
        return this.destinationAddress;
    }

    public void setTextEncoding(TextEncoding value) {
        this.textEncoding = value;
    }

    public TextEncoding getTextEncoding() {
        return this.textEncoding;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setText(String text, TextEncoding textEncoding) {
        setTextEncoding(textEncoding);
        setText(text);
    }

    public String getText() {
        return this.text;
    }

    public void setPriority(Priority priority) {
	this.priority = priority;
    }

    public Priority getPriority() {
	return this.priority;
    }

    public void setOptionalParams(OptionalParamMap optionalParams) {
        this.optionalParams = optionalParams;
    }

    public OptionalParamMap getOptionalParams() {
        return optionalParams;
    }

    @Override
    public void validate() throws SxmpErrorException {
        super.validate();

        // destination address and text must be set
        if (getDestinationAddress() == null) {
            throw new SxmpErrorException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "A destinationAddress value is mandatory with a request");
        }

        if (getText() == null) {
            throw new SxmpErrorException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "A text value is mandatory with a request");
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(250)
            .append(super.toString())
            .append(" (operator [")
            .append(ToStringUtil.nullSafe(this.operatorId))
            .append("] srcAddr [")
            .append(ToStringUtil.nullSafe(this.sourceAddress))
            .append("] destAddr [")
            .append(ToStringUtil.nullSafe(this.destinationAddress))
            .append("] encoding [")
            .append(ToStringUtil.nullSafe(this.textEncoding))
            .append("] text [")
            .append(ToStringUtil.nullSafe(this.text))
            .append("] optParams [")
            .append(ToStringUtil.nullSafe(this.optionalParams))
            .append("] priority [")
            .append(ToStringUtil.nullSafe(this.priority))
            .append("])")
            .toString();
    }
}
