package com.cloudhopper.commons.io;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * An ID representing a file
 * @author garth
 */
public class Id
{

    public Id(String host, String name)
    {
	this.host = host;
	this.name = name;
    }

    private String host;
    private String name;
    private String originalName;
    private MimeType mimeType;

    public String getHost() { return this.host; }
    public String getName() { return this.name; }
    public String getOriginalName() { return this.originalName; }
    public void setOriginalName(String s) { this.originalName = s; }
    public MimeType getMimeType() { return this.mimeType; }
    public void setMimeType(MimeType m) { this.mimeType = m; }
    public void setMimeType(String s) throws MimeTypeParseException { this.mimeType = new MimeType(s); }

    public String toString()
    {
	StringBuilder o = new StringBuilder();
	o.append(host);
	o.append(":");
	o.append(name);
	o.append(" (");
	o.append(originalName);
	if (mimeType != null) o.append(":").append(mimeType.getBaseType());
	o.append(")");
	return o.toString();
    }
}