package com.cloudhopper.commons.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cloudhopper.commons.io.FileStore;
import com.cloudhopper.commons.io.Id;
import com.cloudhopper.commons.io.IdGenerator;
import com.cloudhopper.commons.io.SimpleNIOFileStore;
import com.cloudhopper.commons.io.UUIDIdGenerator;
import com.cloudhopper.commons.io.netty.HttpStaticFileServer;

import org.apache.commons.io.FileUtils;
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
	loadFilesFromDir(argv[0], Integer.parseInt(argv[1]));

// 	BasicConfigurator.configure();
// 	Logger log = Logger.getLogger(FileServerMain.class);

// 	try {
// 	    IdGenerator idGen = new UUIDIdGenerator();
// 	    FileStore store = new SimpleNIOFileStore(idGen, "/tmp/fileStore/");
	    
// 	    HttpStaticFileServer server = new HttpStaticFileServer(store, 8080);
// 	    server.start();
	    
// 	    File f = new File("test.png");
// 	    RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r");
// 	    FileChannel fileChannel = randomAccessFile.getChannel();
	    
// 	    Id fileId = store.write(fileChannel);
	    
// 	    saveFileFromUrl(new URL("http://"+fileId.getHost()+":8080/"+fileId.getName()), "test2.png");
	    
// 	    //server.stop();
// 	} catch (Exception e) {
// 	    log.error(e);
// 	}
    }

    public static void loadFilesFromDir(String dir, int threads)
    {
	ThreadPoolExecutor ex =  new ThreadPoolExecutor(threads, threads, 5000l, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	ex.prestartAllCoreThreads();

	IdGenerator idGen = new UUIDIdGenerator();
	final FileStore store = new SimpleNIOFileStore(idGen, "/tmp/fileStore/");

	final long start = System.currentTimeMillis();
	int count = 0;

	Iterator<File> it = FileUtils.iterateFiles(new File(dir), null, true);
	while (it.hasNext()) {
	    final File f = it.next();
	    final int num = count++;
	    Runnable job = new Runnable() {
		    public void run() {
			try {
			    RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r");
			    FileChannel fileChannel = randomAccessFile.getChannel();
			    Id id = store.write(fileChannel);
			    System.out.println("("+num+") Stored "+f.getPath()+" as "+id.getName()+" after "+(System.currentTimeMillis()-start)+"ms");
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		};
	    ex.execute(job);
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