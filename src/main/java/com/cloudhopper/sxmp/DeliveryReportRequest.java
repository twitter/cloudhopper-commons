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

import com.cloudhopper.sxmp.util.ToStringUtil;
import org.joda.time.DateTime;

/**
 *
 * @author joelauer
 */
public class DeliveryReportRequest extends Request<DeliveryReportResponse> {

    private DeliveryStatus status;
    private DateTime createDate;
    private DateTime finalDate;
    private Integer messageErrorCode;

    public DeliveryReportRequest() {
        super(Operation.Type.DELIVERY_REPORT);
    }

    public DeliveryReportResponse createResponse() throws SxmpErrorException {
        DeliveryReportResponse reportResp = new DeliveryReportResponse();
        reportResp.setReferenceId(this.getReferenceId());
        return reportResp;
    }

    public void setStatus(DeliveryStatus value) {
        this.status = value;
    }

    public DeliveryStatus getStatus() {
        return this.status;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    public DateTime getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(DateTime finalDate) {
        this.finalDate = finalDate;
    }

    public Integer getMessageErrorCode() {
        return messageErrorCode;
    }

    public void setMessageErrorCode(Integer messageErrorCode) {
        this.messageErrorCode = messageErrorCode;
    }

    @Override
    public void validate() throws SxmpErrorException {
        super.validate();
        
        if (getTicketId() == null) {
            throw new SxmpErrorException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "A ticketId value is mandatory with a deliveryReportRequest");
        }

        if (getStatus() == null) {
            throw new SxmpErrorException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "A status value is mandatory with a deliveryReportRequest");
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(250)
            .append(super.toString())
            .append(" (status [")
            .append(ToStringUtil.nullSafe(this.status))
            .append("] messageErrorCode [")
            .append(ToStringUtil.nullSafe(this.messageErrorCode))
            .append("] createDate [")
            .append(ToStringUtil.nullSafe(this.createDate))
            .append("] finalDate [")
            .append(ToStringUtil.nullSafe(this.finalDate))
            .append("])")
            .toString();
    }
}
