package com.cloudhopper.commons.io;

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

    public String getHost() { return this.host; }
    public String getName() { return this.name; }

}