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
public abstract class Request<E extends Response> extends Operation {

    private Account account;
    private Application application;

    public Request(Operation.Type value) {
        super(value, true);
    }

    public void setAccount(Account value) {
        this.account = value;
    }

    public Account getAccount() {
        return this.account;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public abstract E createResponse() throws SxmpErrorException;

    @Override
    public void validate() throws SxmpErrorException {
        if (getAccount() == null) {
            throw new SxmpErrorException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "An account value is mandatory with a request");
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(80)
            .append(super.toString())
            .append(" (account [")
            .append(ToStringUtil.nullSafe(this.account))
            .append("] application [")
            .append(ToStringUtil.nullSafe(this.application))
            .append("])")
            .toString();
    }
    
}
