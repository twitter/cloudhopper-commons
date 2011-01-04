package com.cloudhopper.commons.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.NoSuchAlgorithmException;

import com.cloudhopper.commons.util.Hasher;
import com.cloudhopper.commons.util.Hasher.Algorithm;

/**
 * A simple implementation of FileStore that uses NIO FileChannel direct ByteBuffer-ing, and
 * stores the file using a simple hash directory structure.
 * @author garth
 */
public class SimpleNIOFileStore 
    implements FileStore
{

    private static final int B16K = 16*1024;

    public SimpleNIOFileStore(IdGenerator idGen, String base) throws SecurityException {
	this.idGen = idGen;
	this.baseDir = new File(base);
	if (!baseDir.exists()) baseDir.mkdirs();
    }

    private IdGenerator idGen;
    private File baseDir;

    public Id write(InputStream is) throws FileStoreException
    {
	return write(Channels.newChannel(is));
    }

    public Id write(ReadableByteChannel channel) throws FileStoreException
    {
	Id id = idGen.newId();
	try {
	    File f = createFile(id.getName());
	    RandomAccessFile randomAccessFile = new RandomAccessFile(f, "rw");
	    FileChannel fileChannel = randomAccessFile.getChannel();
	    channelCopy(channel, fileChannel);
	} catch (IOException e) {
	    throw new FileStoreException(e);
	}
	return id;
    }

    public void transferToOutputStream(OutputStream os, Id id) throws FileStoreException
    {
	try {
	    channelCopy(getChannel(id), Channels.newChannel(os));
	} catch (IOException e) {
	    throw new FileStoreException(e);
	}
    }

    public void transferToChannel(WritableByteChannel channel, Id id) throws FileStoreException
    {
	try {
	    channelCopy(getChannel(id), channel);
	}  catch (IOException e) {
	    throw new FileStoreException(e);
	}
    }

    public ReadableByteChannel getChannel(Id id) throws FileStoreException
    {
	try {
	    File f = getFile(id.getName());
	    RandomAccessFile randomAccessFile = new RandomAccessFile(f, "rw");
	    FileChannel fileChannel = randomAccessFile.getChannel();
	    return fileChannel;
	} catch (IOException e) {
	    throw new FileStoreException(e);
	}
    }

    public void remove(Id id) throws FileStoreException
    {
	try {
	    File f = getFile(id.getName());
	    f.delete();
	} catch (IOException e) {
	    throw new FileStoreException(e);
	}
    }


    ///////
    // Utilities for file and path
    ///////
    private void channelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException
    {
	final ByteBuffer buffer = ByteBuffer.allocateDirect(B16K);
	while (src.read(buffer) != -1) {
	    buffer.flip();
	    dest.write(buffer);
	    buffer.compact();
	}
	buffer.flip();
	while (buffer.hasRemaining()) {
	    dest.write(buffer);
	}
    }
 
    private void channelCopyToFile(final ReadableByteChannel src, final FileChannel dest) throws IOException
    {
	long position = 0;
	long transferred = 0;
	do {
	    transferred = dest.transferFrom(src, position, B16K);
	    position += transferred;
	} while (transferred >= B16K);
    }

    private void channelCopyFromFile(final FileChannel src, final WritableByteChannel dest) throws IOException
    {
	long position = 0;
	long transferred = 0;
	do {
	    transferred = src.transferTo(position, B16K, dest);
	    position += transferred;
	} while (transferred >= B16K);
    }
 
   private File createFile(String name) throws SecurityException
    {
	File tmpDir = new File(this.baseDir, getHashPathForLevel(name, 2));
	if (!tmpDir.exists()) tmpDir.mkdirs();
	File tmpFile = new File(tmpDir, name);
	if (tmpFile.exists()) throw new SecurityException("This file already exists. Use getFile() to use it.");
	return tmpFile;
    }

    private File getFile(String name) throws IOException
    {
	File tmpDir = new File(this.baseDir, getHashPathForLevel(name, 2));
	if (!tmpDir.exists()) throw new FileNotFoundException();
	File tmpFile = new File(tmpDir, name);
	if (tmpFile.exists()) throw new FileNotFoundException();
	return tmpFile;
    }

    private String getHashPathForLevel(String name, int levels)
    {
	String path = "";
	if (levels != 0) {
	    try {
		Hasher hasher = new Hasher(Algorithm.MD5);
		String hash = hasher.toHashedHexString(name);
		path = "";
		for (int i = 1; i <= levels; i++) {
		    path += hash.substring( 0, i ) + "/";
		}
	    } catch (NoSuchAlgorithmException e) {}
	}
	return path;
    }

}