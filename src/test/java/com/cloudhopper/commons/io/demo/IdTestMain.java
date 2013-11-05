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
 * Simple demo that creates a FileStore, stores a file, and then fetches the file with HTTP.
 * @author garth
 */
public class IdTestMain {
    private static final Logger logger = LoggerFactory.getLogger(IdTestMain.class);

    public static void main(String[] argv) throws Exception {
        // figure out "path" from a filename
        // sfd-flamingo-001.cloudhopper.lan:5ede0dd4-7a8d-4b7d-9696-7d2fdcacb5a4
        
        
        IdGenerator idGen = new UUIDIdGenerator();
        SimpleNIOFileStore fs = new SimpleNIOFileStore(idGen, "/tmp/filestore/");
        
        //Id id = new Id("sfd-flamingo-001.cloudhopper.lan", "5ede0dd4-7a8d-4b7d-9696-7d2fdcacb5a4");
        //Id id = new Id("sfd-flamingo-001.cloudhopper.lan", "56247bc2-dca2-4bc5-a7d1-cd5c36cd0c48");
        Id id = new Id("fe5146c6-cff7-4986-be2d-56e582103b0d");
        FileChannel fc = fs.getFileChannel(id);
        
    }
}