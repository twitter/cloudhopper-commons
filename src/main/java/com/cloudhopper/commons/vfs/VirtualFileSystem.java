
package com.cloudhopper.commons.vfs;

// my imports
import com.cloudhopper.commons.util.URL;
import java.io.File;
import java.io.InputStream;

/**
 * Interface for virtual filesystem providers.
 *
 * @author joelauer
 */
public interface VirtualFileSystem {

    /**
     * Gets the URL bound to this virtual filesystem.
     * @return The URL bound to this virtual filesystem
     */
    public URL getURL();

    /**
     * Connect to the virtual filesystem.
     */
    public void connect() throws FileSystemException;

    /**
     * Disconnect from the virtual filesystem.
     */
    public void disconnect() throws FileSystemException;

    /**
     * Checks if a filename already exists on the virtual filesystem.
     * @param filename The filename to check
     * @return True if the filename already exists, otherwise false.
     * @throws FileSystemException Thrown only if there an error while checking
     *      if the file exists.  This will not be thrown if the file already
     *      exists -- only if there is an error with the underlying connection, etc.
     */
    public boolean exists(String filename) throws FileSystemException;

    /**
     * Copies the source file to the virtual filesystem. The filename on the
     * virtual filesystem will be named to match the source file.
     * @param srcFile The local file to copy
     * @throws FileSystemException Thrown if there is an error while copying or
     *      transferring the file to the virtual filesystem.  Also thrown if
     *      the filename already exists on the virtual filesystem.
     */
    public void copy(File srcFile) throws FileSystemException;

    /**
     * Copies the source file to the virtual filesystem. The filename on the
     * virtual filesystem will be named with the specified filename.
     * @param srcFile The local file to copy
     * @param filename The filename to use on the virtual filesystem
     * @throws FileSystemException Thrown if there is an error while copying or
     *      transferring the file to the virtual filesystem.  Also thrown if
     *      the filename already exists on the virtual filesystem.
     */
    public void copy(File srcFile, String filename) throws FileSystemException;

    /**
     * Copies the data read from the InputStream to the virtual filesystem. The
     * data will be copied into the specified filename.  The InputStream will
     * only be read to the end, it will not be closed.  The caller of this
     * method will need to close the InputStream.
     * @param in The input stream containing the data to copy
     * @param filename The filename to use on the virtual filesystem
     * @throws FileSystemException Thrown if there is an error while copying or
     *      transferring the input stream to the virtual filesystem.  Also thrown if
     *      the filename already exists on the virtual filesystem.
     */
    public void copy(InputStream in, String filename) throws FileSystemException;

    /**
     * Moves the source file to the virtual filesystem. The filename on the
     * virtual filesystem will be named to match the source file.  The file
     * will be deleted only after confirming its been successfully copied to
     * the virtual filesystem.
     * @param srcFile The local file to move
     * @throws FileSystemException Thrown if there is an error while copying or
     *      transferring the file to the virtual filesystem.  Also thrown if
     *      the filename already exists on the virtual filesystem.
     */
    public void move(File srcFile) throws FileSystemException;

    /**
     * Moves the source file to the virtual filesystem. The filename on the
     * virtual filesystem will be named with the specified filename.  The file
     * will be deleted only after confirming its been successfully copied to
     * the virtual filesystem.
     * @param srcFile The local file to move
     * @param filename The filename to use on the virtual filesystem
     * @throws FileSystemException Thrown if there is an error while copying or
     *      transferring the file to the virtual filesystem.  Also thrown if
     *      the filename already exists on the virtual filesystem.
     */
    public void move(File srcFile, String filename) throws FileSystemException;

}
