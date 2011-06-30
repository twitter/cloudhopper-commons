package com.cloudhopper.commons.io;

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

    public long getSize() { return this.size; }
    public void setSize(long size) { this.size = size; }
    public String getName() { return this.name; }
    public String getOriginalName() { return this.originalName; }
    public void setOriginalName(String s) { this.originalName = s; }
    public MimeType getMimeType() { return this.mimeType; }
    public void setMimeType(MimeType m) { this.mimeType = m; }
    public void setMimeType(String s) throws MimeTypeParseException { this.mimeType = new MimeType(s); }

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