
Overview
--------------------------------------------------------------------------------
Provides a consistent interface to configure, create, and manage various
DataSource providers that may provide Connection pooling.  With no other
dependencies, this library provides a basic DataSource that offers no pooling.
A convenvient way to use connections in simple applications or unit tests. All
configuration is controlled by one DataSourceConfiguration object that can
be easily configured via the ch-commons-xbean project.  This library is not
dependent on that library, however.

JMX
--------------------------------------------------------------------------------
Every DataSource created by this library, provides an optional JMX registration
of a ManagedDataSource MBean.  The most reliable monitoring stat is
"IdleConnectionCount" which ideally should > 0.  If its 0, then that either means
the app is overloaded or the database is down.  Perhaps future versions of this
library can help figure out extra vars to monitor.

C3P0
--------------------------------------------------------------------------------
Seems to the most complete pooling implementation, offering high performance,
and numerous helpers such as a timeout value for getConnection() to complete
successfully.

Proxool
--------------------------------------------------------------------------------
Similar to C3P0, but getConnection() will timeout almost immediately if no
connections are available.  Especially if the maxConnections are already used.
C3P0 offers a timeout value to wait for another connection to be returned to
the pool before getConnection() timesout.

Both implementations, setting a lower idle test timeout helps to figure out if
the database is "down" much faster.

Proxool will only restore itself on a getConnection() call, while C3P0 will
asynchronously rebuild the pool if the database comes back up.  Seems to be a
smarter pooling impl.