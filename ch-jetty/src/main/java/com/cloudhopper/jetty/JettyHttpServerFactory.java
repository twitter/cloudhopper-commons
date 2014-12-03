package com.cloudhopper.jetty;

/*
 * #%L
 * ch-jetty
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

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class JettyHttpServerFactory {
    private static final Logger logger = LoggerFactory.getLogger(JettyHttpServerFactory.class);
    
    static public JettyHttpServer create(HttpServerConfiguration configuration) throws Exception {
        // validate the arguments
        if (configuration == null) {
            throw new NullPointerException("configuration cannot be null");
        }

        // are there any connectors configured?
        if (!configuration.hasAtLeastOneConnector()) {
            throw new Exception("At least one connector or sslConnector must be configured for an HttpServer");
        }

	// create thread pool for max control
        logger.info("Creating threadPool with minThreads [{}] and maxThreads [{}]...", configuration.getMinThreads(), configuration.getMaxThreads());
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(configuration.getMinThreads().intValue());
        threadPool.setMaxThreads(configuration.getMaxThreads().intValue());
        // make sure this is set to be a "daemon"
        threadPool.setDaemon(true);

        // create a new jetty server instance
        Server server = new Server(threadPool);

        // enable jmx?
        String domain = "com.cloudhopper.jetty." + configuration.safeGetName();
        logger.info("Creating jmx for domain [{}]...", domain);
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        mBeanContainer.setDomain(domain);
        server.addBean(mBeanContainer);

	// create a scheduler? always necessary?
	server.addBean(new ScheduledExecutorScheduler());
	
        // add cleartext connectors
        for (HttpConnectorConfiguration connConfig : configuration.getConnectors()) {
            logger.info("Creating NIO connector on port [{}]...", connConfig.getPort());
	    
	    // use config defaults
	    HttpConfiguration config = new HttpConfiguration();

	    ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(config));
            if (connConfig.getHost() != null) {
                connector.setHost(connConfig.getHost());
            }
	    connector.setPort(connConfig.getPort().intValue());
	    connector.setIdleTimeout(connConfig.getMaxIdleTime());
	    //statsOn?
	    connector.setReuseAddress(connConfig.isReuseAddress());
	    server.addConnector(connector);
	}

        // add SSL connectors
        for (HttpSslConnectorConfiguration connConfig : configuration.getSslConnectors()) {
            logger.trace("Creating NIO SSL connector on port [{}]...", connConfig.getPort());
            
            // NIO-based SSL connector requires a factory at constructor time
            SslContextFactory factory = new SslContextFactory();
            
            // the keystore file MUST be set
            if (connConfig.getKeystoreFile() == null) {
                throw new Exception("An HTTP SSL connector must have its keystoreFile set");
            }

            logger.info("Configuring NIO SSL connector on port [{}] with keystoreFile [{}]", connConfig.getPort(), connConfig.getKeystoreFile());
            factory.setKeyStorePath(connConfig.getKeystoreFile());
            factory.setKeyStorePassword(connConfig.getKeystorePassword());
            factory.setKeyManagerPassword(connConfig.getKeystorePassword());

            // the truststore is either specific or the same as keystore
            if (connConfig.getTruststoreFile() == null) {
                factory.setTrustStorePath(factory.getKeyStorePath());
            } else {
                factory.setTrustStorePath(connConfig.getTruststoreFile());
            }
            if (connConfig.getTruststorePassword() == null) {
                factory.setTrustStorePassword(connConfig.getKeystorePassword());
            } else {
                factory.setTrustStorePassword(connConfig.getTruststorePassword());
            }
            
	    // ? from example http://www.eclipse.org/jetty/documentation/current/embedding-jetty.html
	    factory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
					   "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
					   "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
					   "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
					   "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
					   "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
	    
	    // SSL HTTP Configuration. Use config defaults.
	    HttpConfiguration config = new HttpConfiguration();
	    config.addCustomizer(new SecureRequestCustomizer());

	    // SSL Connector
	    ServerConnector connector = new ServerConnector(server,
							    new SslConnectionFactory(factory, HttpVersion.HTTP_1_1.asString()),
							    new HttpConnectionFactory(config));
            if (connConfig.getHost() != null) {
                connector.setHost(connConfig.getHost());
            }
	    connector.setPort(connConfig.getPort().intValue());
	    connector.setIdleTimeout(connConfig.getMaxIdleTime());
	    //statsOn?
	    connector.setReuseAddress(connConfig.isReuseAddress());
	    server.addConnector(connector);
	}
        
        // prep server to handle multiple contexts, potentially with sessions
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(contexts);
        server.setHandler(handlers);
        
        // at this point, servlets will be added to "contexts" and resources
        // such as files will be added as a resource handler
        ServletContextHandler rootServletContext = new ServletContextHandler(contexts, "/", (configuration.isSessionsEnabled().booleanValue() ? ServletContextHandler.SESSIONS : ServletContextHandler.NO_SESSIONS));
        rootServletContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        
        // in order to add a servlet, it's pretty easy
        // ServletHolder servletHolder = new ServletHolder(servlet);
        // rootContext.addServlet(servletHolder, uriMapping);

        // add a statistics handler -- responsible for generating statistics
        //StatisticsHandler statsHandler = new StatisticsHandler();
        //rootContext.addHandler(statsHandler);
        
        JettyHttpServer httpd = new JettyHttpServer(configuration, server, handlers, contexts, rootServletContext);
        
        // any post-configs
        if (configuration.getResourceBaseDirectory() != null) {
            httpd.addResourceBase(configuration.getResourceBaseDirectory());
        }
        
        return httpd;
    }

}
