package com.cloudhopper.commons.io;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * ID generator that produces a UUID.
 * @author garth
 */
public class UUIDIdGenerator
    implements IdGenerator
{

    public UUIDIdGenerator() {
	try {
	    InetAddress addr = InetAddress.getLocalHost();
	    this.hostname = addr.getHostName();
	} catch (UnknownHostException e) {}
    }

    private String hostname;

    public Id newId() { return new Id(hostname, UUID.randomUUID().toString()); }

}