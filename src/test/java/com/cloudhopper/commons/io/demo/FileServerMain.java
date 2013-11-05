package com.cloudhopper.commons.io.demo;

/*
 * #%L
 * ch-commons-io
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.cloudhopper.commons.io.FileStore;
import com.cloudhopper.commons.io.Id;
import com.cloudhopper.commons.io.IdGenerator;
import com.cloudhopper.commons.io.SimpleNIOFileStore;
import com.cloudhopper.commons.io.UUIDIdGenerator;
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
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple demo that creates a FileStore, stores a file.
 * @author garth
 */
public class FileServerMain {
    private static final Logger logger = LoggerFactory.getLogger(FileServerMain.class);

    public static void main(String[] argv) {
        loadFilesFromDir(argv[0], Integer.parseInt(argv[1]));
    }

    public static void loadFilesFromDir(String dir, int threads) {
        ThreadPoolExecutor ex = new ThreadPoolExecutor(threads, threads, 5000l, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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

                @Override
                public void run() {
                    try {
                        RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r");
                        FileChannel fileChannel = randomAccessFile.getChannel();
                        Id id = store.write(fileChannel);
                        System.out.println("(" + num + ") Stored " + f.getPath() + " as " + id.getName() + " after " + (System.currentTimeMillis() - start) + "ms");
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            };
            ex.execute(job);
        }
    }

    private static void saveFileFromUrl(URL url, String path) throws Exception {
        URLConnection urlc = url.openConnection();
        BufferedInputStream bis = new BufferedInputStream(urlc.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(path)));
        int i;
        while ((i = bis.read()) != -1) {
            bos.write(i);
        }
        bis.close();
        bos.close();
    }
}