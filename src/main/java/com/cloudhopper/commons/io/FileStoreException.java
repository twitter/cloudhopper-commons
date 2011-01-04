package com.cloudhopper.commons.io;

import java.io.IOException;

/**
 * FileStore exception.
 * @author garth
 */
public class FileStoreException
    extends IOException
{

    public FileStoreException() {
	super();
    }
	
    public FileStoreException(String message) {
	super(message);
    }
	
    public FileStoreException(String message, Throwable cause) {
	super(message, cause);
    }
    
    public FileStoreException(Throwable cause) {
	super(cause);
    }

}
