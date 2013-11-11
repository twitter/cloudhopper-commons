/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cloudhopper.commons.sql.c3p0;

/*
 * #%L
 * ch-commons-sql
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

// java imports
import java.lang.management.*;
import javax.management.*;

// third party imports
import com.mchange.v2.log.*;
import com.mchange.v2.c3p0.*;
import com.mchange.v2.c3p0.management.*;

/**
 * Class copied directly from c3p0 source code from the ActiveManagementCoordinator.java
 * file.  This is an implementation to allow customization of how JMX is used
 * within the c3p0 library.  Those customizations include:
 * <ul>
 *      <li>Custom JMX domain</li>
 *      <li>Per-DataSource JMX on/off registry</li>
 * </ul>
 * @author joelauer
 */
public class C3P0CustomManagementCoordinator implements ManagementCoordinator {

    // static defaults -- these will work precisely the first time
    // and then they'll be able to change while the JVM is running
    private static String C3P0_JMX_DOMAIN = "com.cloudhopper";

    /**
     * Sets the JMX domain to use for registering the C3P0 registery.  This will
     * work exactly once.  It must be set prior to starting any C3P0 instance.
     * @param domain Such as "com.cloudhopper"
     */
    public static void setJmxDomainOnce(String domain) {
        C3P0_JMX_DOMAIN = domain;
    }

    // MT: thread-safe
    final static MLogger logger = MLog.getLogger(C3P0CustomManagementCoordinator.class);

    MBeanServer mbs;
    String jmxDomain;

    /**
     * Creates a new instance of C3P0CustomManagementCoordinator and saves
     * a copy of what the JMX domain was statically set to -- this is then used
     * for the lifetime of this object.
     */
    public C3P0CustomManagementCoordinator() throws Exception {
        this.mbs = ManagementFactory.getPlatformMBeanServer();
        this.jmxDomain = C3P0_JMX_DOMAIN;
    }

    public void attemptManageC3P0Registry()
    {
        try
        {
            // create objectname for C3P0 registry
            ObjectName name = new ObjectName(jmxDomain + ":type=C3P0,name=C3P0Registry");
            C3P0RegistryManager mbean = new C3P0RegistryManager();

            if (mbs.isRegistered(name))
            {
                if (logger.isLoggable(MLevel.WARNING))
                {
                    logger.warning("A C3P0Registry mbean is already registered. " +
                                    "This probably means that an application using c3p0 was undeployed, " +
                                    "but not all PooledDataSources were closed prior to undeployment. " +
                                    "This may lead to resource leaks over time. Please take care to close " +
                                    "all PooledDataSources.");
                }
                mbs.unregisterMBean(name);
            }
            mbs.registerMBean(mbean, name);
        }
        catch (Exception e)
        {
            if ( logger.isLoggable( MLevel.WARNING ) )
                logger.log( MLevel.WARNING,
                        "Failed to set up C3P0RegistryManager mBean. " +
                        "[c3p0 will still function normally, but management via JMX may not be possible.]",
                        e);
        }
    }

    public void attemptUnmanageC3P0Registry()
    {
        try
        {
            ObjectName name = new ObjectName(jmxDomain + ":type=C3P0,name=C3P0Registry");
            if (mbs.isRegistered(name))
            {
                mbs.unregisterMBean(name);
                if (logger.isLoggable(MLevel.FINER))
                    logger.log(MLevel.FINER, "C3P0Registry mbean unregistered.");
            }
            else if (logger.isLoggable(MLevel.FINE))
                logger.fine("The C3P0Registry mbean was not found in the registry, so could not be unregistered.");
        }
        catch (Exception e)
        {
            if ( logger.isLoggable( MLevel.WARNING ) )
                logger.log( MLevel.WARNING,
                        "An Exception occurred while trying to unregister the C3P0RegistryManager mBean." +
                        e);
        }
    }

    public void attemptManagePooledDataSource(PooledDataSource pds)
    {
        String name = getPdsObjectNameStr( pds );
        try
        {
            //PooledDataSourceManager mbean = new PooledDataSourceManager( pds );
            //mbs.registerMBean(mbean, ObjectName.getInstance(name));
            //if (logger.isLoggable(MLevel.FINER))
            //    logger.log(MLevel.FINER, "MBean: " + name + " registered.");

            // DynamicPooledDataSourceManagerMBean registers itself on construction (and logs its own registration)
            DynamicPooledDataSourceManagerMBean mbean = new DynamicPooledDataSourceManagerMBean( pds, name, mbs );
        }
        catch (Exception e)
        {
            if ( logger.isLoggable( MLevel.WARNING ) )
                logger.log( MLevel.WARNING,
                        "Failed to set up a PooledDataSourceManager mBean. [" + name + "] " +
                        "[c3p0 will still functioning normally, but management via JMX may not be possible.]",
                        e);
        }
    }


    public void attemptUnmanagePooledDataSource(PooledDataSource pds)
    {
        String nameStr = getPdsObjectNameStr( pds );
        try
        {
            ObjectName name = new ObjectName( nameStr );
            if (mbs.isRegistered(name))
            {
                mbs.unregisterMBean(name);
                if (logger.isLoggable(MLevel.FINER))
                    logger.log(MLevel.FINER, "MBean: " + nameStr + " unregistered.");
            }
            else
                if (logger.isLoggable(MLevel.FINE))
                    logger.fine("The mbean " + nameStr + " was not found in the registry, so could not be unregistered.");
        }
        catch (Exception e)
        {
            if ( logger.isLoggable( MLevel.WARNING ) )
                logger.log( MLevel.WARNING,
                        "An Exception occurred while unregistering mBean. [" + nameStr + "] " +
                        e);
        }
    }

    private String getPdsObjectNameStr(PooledDataSource pds) {
        return jmxDomain + ":type=C3P0,name=PooledDataSource[" + pds.getDataSourceName() + "]";
    }
}
