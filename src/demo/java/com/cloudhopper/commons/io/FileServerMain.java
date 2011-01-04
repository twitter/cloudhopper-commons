package com.cloudhopper.commons.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

import com.cloudhopper.commons.io.FileStore;
import com.cloudhopper.commons.io.Id;
import com.cloudhopper.commons.io.IdGenerator;
import com.cloudhopper.commons.io.SimpleNIOFileStore;
import com.cloudhopper.commons.io.UUIDIdGenerator;
import com.cloudhopper.commons.io.netty.HttpStaticFileServer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Simple demo that creates a FileStore, stores a file, and then fetches the file with HTTP.
 * @author garth
 */
public class FileServerMain
{

    public static void main(String[] argv)
    {
	BasicConfigurator.configure();
	Logger log = Logger.getLogger(FileServerMain.class);

	try {
	    IdGenerator idGen = new UUIDIdGenerator();
	    FileStore store = new SimpleNIOFileStore(idGen, "/tmp/fileStore/");
	    
	    HttpStaticFileServer server = new HttpStaticFileServer(store, 8080);
	    server.start();
	    
	    File f = new File("test.png");
	    RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r");
	    FileChannel fileChannel = randomAccessFile.getChannel();
	    
	    Id fileId = store.write(fileChannel);
	    
	    saveFileFromUrl(new URL(fileId.getHost()+"/"+fileId.getName()), "test2.png");
	    
	    server.stop();
	} catch (Exception e) {
	    log.error(e);
	}
    }

    private static void saveFileFromUrl(URL url, String path) throws Exception
    { 
	URLConnection urlc = url.openConnection();
	BufferedInputStream bis = new BufferedInputStream( urlc.getInputStream() );
	BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(new File(path)) );
	int i;
	while ((i = bis.read()) != -1) {
	    bos.write( i );
	}
	bis.close();
	bos.close();
    }

}