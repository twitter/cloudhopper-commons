package com.cloudhopper.commons.io;

/*
 * #%L
 * ch-commons-io
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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * An ID representing a file
 * @author garth
 */
public class Id
{

    public Id(String name)
    {
	this.name = name;
    }

    private long size;
    private String name;
    private String originalName;
    private MimeType mimeType;
    private String metaData;

    public long getSize() { return this.size; }
    public void setSize(long size) { this.size = size; }
    public String getName() { return this.name; }
    public String getOriginalName() { return this.originalName; }
    public void setOriginalName(String s) { this.originalName = s; }
    public MimeType getMimeType() { return this.mimeType; }
    public void setMimeType(MimeType m) { this.mimeType = m; }
    public void setMimeType(String s) throws MimeTypeParseException { this.mimeType = new MimeType(s); }
    public String getMetaData() { return this.metaData; }
    public void setMetaData(String metaData) { this.metaData = metaData; }

    @Override
    public String toString()
    {
	StringBuilder o = new StringBuilder();
	o.append(name);
	o.append(" (");
	o.append(originalName);
	if (mimeType != null) o.append(":").append(mimeType.getBaseType());
	if (size > 0) o.append(":[").append(mimeType.getBaseType()).append(" bytes]");
	o.append(")");
	return o.toString();
    }
}