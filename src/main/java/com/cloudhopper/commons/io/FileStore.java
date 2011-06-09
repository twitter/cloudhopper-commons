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

    /**
     * Write an InputStream's bytes to the FileStore.
     * @param is The input stream (e.g. from a ServletInputStream)
     * @return The file's Id.
     * @throws FileStoreException if the underlying FileStore cannot write the file.
     */
    public Id write(InputStream is) throws FileStoreException;

    /**
     * Write a ReadableByteChannel's bytes to the FileStore. This will use zero-copy if available.
     * @param channel The channel to read from.
     * @return The file's Id.
     * @throws FileStoreException if the underlying FileStore cannot write the file.
     */
    public Id write(ReadableByteChannel channel) throws FileStoreException;

    /**
     * Read an file's bytes to an InputStream.
     * @return The Id of the file you want to read. 
     * @return The input stream
     * @throws FileStoreException if the underlying FileStore cannot read the file.
     */
    public InputStream readStream(Id id) throws FileStoreException;

    /**
     * Read an file's bytes to a ReadableByteChannel.
     * @return The Id of the file you want to read. 
     * @return The readable byte channel
     * @throws FileStoreException if the underlying FileStore cannot read the file.
     */
    public ReadableByteChannel readChannel(Id id) throws FileStoreException;

    /**
     * Transfer a file's contents to a given OutputStream.
     * @param os The OutputStream to write to.
     * @param id The Id of the file you want to read.
     * @throws FileStoreException if the underlying FileStore cannot read the file.
     */
    public void transferToOutputStream(OutputStream os, Id id) throws FileStoreException;

    /**
     * Transfer a file's contents to a given WritableByteChannel. This will use zero-copy if available.
     * @param os The WritableByteChannel to write to.
     * @param id The Id of the file you want to read.
     * @throws FileStoreException if the underlying FileStore cannot read the file.
     */
    public void transferToChannel(WritableByteChannel channel, Id id) throws FileStoreException;

    /**
     * Get the file's FileChannel. This is useful for doing zero-copy reads from a file.
     * @param id The Id of the file you want to read.
     * @throws FileStoreException if the underlying FileStore cannot read the file.
     */
    public FileChannel getFileChannel(Id id) throws FileStoreException;

    /**
     * Remove the file specified. This is a hint to the FileStore, and does not guarantee synchronous deletion.
     * @param id The Id of the file you want to delete.
     * @throws FileStoreException if the underlying FileStore cannot delete the file.
     */
    public void remove(Id id) throws FileStoreException;

}