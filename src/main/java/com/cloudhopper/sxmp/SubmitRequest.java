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

/**
 *
 * @author joelauer
 */
public class SubmitRequest extends MessageRequest<SubmitResponse> {

    private Boolean deliveryReport;

    public SubmitRequest() {
        super(Operation.Type.SUBMIT);
        this.deliveryReport = Boolean.FALSE;
    }

    public SubmitRequest(String version) {
        super(Operation.Type.SUBMIT);
        this.deliveryReport = Boolean.FALSE;
        this.version = version;
    }

    public SubmitResponse createResponse() throws SxmpErrorException {
        SubmitResponse submitResp = new SubmitResponse();
        submitResp.setReferenceId(this.getReferenceId());
        return submitResp;
    }

    public void setDeliveryReport(Boolean value) {
        this.deliveryReport = value;
    }

    public Boolean getDeliveryReport() {
        return this.deliveryReport;
    }

    @Override
    public void validate() throws SxmpErrorException {
        super.validate();
    }
}
