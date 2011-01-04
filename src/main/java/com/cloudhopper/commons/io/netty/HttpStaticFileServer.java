package com.cloudhopper.commons.io.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.cloudhopper.commons.io.FileStore;

/**
 */
public class HttpStaticFileServer {

    public HttpStaticFileServer(FileStore store, int port) {
	this.store = store;
	this.port = port;
    }

    private int port;
    private FileStore store;
    private ServerBootstrap bootstrap;

    public void start() 
    {
        // Configure the server.
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
									  Executors.newCachedThreadPool()));
	
        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpStaticFileServerPipelineFactory(store));

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }

    public void stop()
    {
	bootstrap.releaseExternalResources();
    }
}
