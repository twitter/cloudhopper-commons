package com.cloudhopper.commons.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * A simple interface for writing/reading files (or arbitrary byte arrays and streams)
 * to a underlying persistence mechanism.
 * @author garth
 */
public interface FileStore
{

    public Id write(InputStream is) throws FileStoreException;

    public Id write(ReadableByteChannel channel) throws FileStoreException;

    public void transferToOutputStream(OutputStream os, Id id) throws FileStoreException;

    public void transferToChannel(WritableByteChannel channel, Id id) throws FileStoreException;

    public ReadableByteChannel getChannel(Id id) throws FileStoreException;

    public void remove(Id id) throws FileStoreException;

}