Cloudhopper by Twitter
============================

cloudhopper-commons-util
------------------------

## 6.0.1 - 2012-10-30
 - Parent POM from to 1.3
 - Packaged jar now with OSGi

## 6.0.0 - 2012-10-26
 - No major code changes, mostly project layout changes in prep of release to
   Maven Central Repository
 - Maven artifact group changed from cloudhopper to com.cloudhopper
 - Readme and changelog switched to markdown
 - ch-sevenzip dependency removed along w/ support in CompressionUtil
 - Joda, SLF4J, and Logback dependencies switched to use Maven Central groupIds
 - Updated underlying maven license plugin - every java file had its
   license positioned after package declaration.
 - Deprecated bin directory

## 5.1.2 - 2011-10-26
 - Upgrade parent POM dependency from 1.5 to 1.6
 - Added support for OSGI in final jar

## 5.1.1 - 2011-08-18
 - Switched org.slf4j to slf4j maven group due to issue with upstream maven repo.
 - Switched ch.qos.logback to logback maven group due to issue with upstream maven repo.
 - Added floorToHalfHour, floorToQuarterHour, and floorToTenMinutes to DateTimeUtil

## 5.1.0 - 2011-07-26
 - Upgrade parent POM dependency from 1.1 to 1.5
 - Switched to new version format.
 - Added HexString utility class and fixed a few compiler warnings.
 - Added Version class to compile build info into jar

## 5.0 - 2011-05-09
 - Log4j dependency switched for SLF4J
 - WARN: Windowing package has been refactored. Source changes were significant
    enough (and unavoidable) to create a major backwards incompatibility with
    <= version 4.1.
 - Added UnwrappedWeakReference utility class for using the referenced object's
    equals() and hashCode() values rather than WeakReference's impl which uses
    the impl of Object.  Methods that rely on equals() such as CopyOnWriteArrayList
    and the addIfAbsent method work correctly with UnwrappedWeakReference.
 - Added support in Window for triggering an early timeout for threads blocked
    with a pending offer to be accepted (see Window.abortPendingOffers())
 - Removed old experimental support for expiring requests in a Window and
    replaced with a much more efficient and atomic process for expiring requests.
    See new alternative constructor for Window that accepts a ScheduledExecutorService.
 - Added support in Window for externally cancelling only expired requests.
 - Added support for setting an expiration time as well as new offer, accept,
    and done timestamps on all Window requests.
 - Large amount of cleanup of javadocs and comments.
 - Added NamingThreadFactory utility class for standardizing the two most common
    customizations for Executors: thread names and whether they are daemons.
 - Added CountingRejectedExecutionHandler utility class for counting the number
    of times an Executors rejectedExecution() method has been called.

## 4.1 - 2011-04-28
 - Upgrade joda-time dependency from 1.6 to 1.6.2
 - Added support for setting a "cause" when a request is canceled in a Window.
 - Fixed some missing annotations for @Overrides

## 4.0 - 2011-02-28
 - Migrated build system from Ant to Maven
 - Deleted all legacy Ant build files (build.xml, Dependency.txt)
 - Added Apache license to top of all source files
 - Fixed FileFinder to sort files by alphabetical order -- caused some tests to
    fail on XFS filesystem since the Java directory listing didn't match the
    original EXT3/4 test case.
 - Moved demo source code from src/demo/java into src/test/java directory and
    placed in separate "demo" package.
 - Added "Makefile" with targets that match previous demo ant tasks.
 - Fixed CompressionUtilTest and FileUtilTest to use ReleaseNotes.txt as a
    sample file.
 - Minor cleanup to source code to include annotations for overrides.

## 3.1 - 2010-08-23
 - EXPERIMENTAL: Added "expiredRequestReaper" feature to the Window class.
    Adds ability to automatically expire requests from the window by
    periodically checking if they have expired.  A listener is notified if the
    request has expired.

## 3.0 - 2010-06-05
 - AddressMap class has been replaced by DigitLookupMap class
 - Added SimpleMap interface to implement a subset of java.util.Map features
 - Added StringLookupMap class

## 2.15 - 2010-05-31
 - Fixed bug with ByteArrayUtil.toUnsignedByte not properly decoding unsigned
    byte.

## 2.14 - 2010-05-31
 - Added ByteArrayUtil for converting between byte arrays and numbers.

## 2.13 - 2010-03-11
 - Added new StringUtil.removeAllCharsExceptDigits method which is useful for
    cleaning up something such as a phone number.

## 2.12 - 2010-02-28
 - Added "ThreadUtil" class for internally working with threads.  For example,
    mostly useful for unit tests where a caller would like to verify a thread
    properly died.

## 2.11 - 2010-02-19
 - Added ability for callers to instruct a "window" that they plan or do not
    plan on "waiting" for a response.  This doesn't change any of the internal
    behavior or logic, but it does let callers with responses figure out if
    another thread is waiting or planning on waiting for the response.  This is
    particularly useful for SMPP where we want to route a response back to a
    thread waiting for a response vs. passing the response upstream.

## 2.10 - 2010-02-17
 - Added "windowing" package to simplify the request/response synchroniziation
    with windowed protocols like SMPP or UCP.
 - Fixed compiler warnings for unchecked and redundant casts in various classes
 - Fixed unit tests that counted on lower case hex chars which changed with
    the HexUtil class.  This impacted ByteBufferTest and HasherTest.
 - Fixed runtime issue with sample compression app

## 2.9 - 2010-02-12
 - Added new HexUtil class with optimized and better documented methods for
    encoding and decoding Strings containing hexidecimal characters.  This is
    intended as a replacement for any previous methods available in StringUtil.
 - All "hex" related methods in StringUtil and ByteUtil are marked @deprecated
    and will be removed in a future release.
 - Added "toStringWithNullAsXXXXX" methods to help safely call toString() on
    objects and gracefully handle the situation where they are null.

## 2.8 - 2010-02-09
 - Added a helper method to TimedStateBoolean to return the duration of how
    long the current value has been set (in milliseconds).  Formally, only
    a Joda Duration object was available.  This method will return just a long
    representation of the number of milliseconds.

## 2.7 - 2010-02-09
 - Fixed bug with PeriodFormatterUtil where Periods greater than 7 days resulted
    in a "0 days" value in a String since a Period automatically included a
    "weeks" field that 7 days rolled up into.  Turns out that Periods need to be
    normalized with a PeriodStyle that excludes years, months, or weeks.
 - Removed "millis" from all uptime styles since its not usually required to
    track the uptime or downtime of a long running process.
 - Added helper methods to just take a duration (in millis) and return the
    String with the uptime already converted.  Saves extra typing and ensures
    a correct, normalized Period is used to create the underlying String.
 - Changed the name of the styles to reflect that they are purely for "Uptime"
    printouts of Periods.  NOTE: Will break backwards compatibability.

## 2.6 - 2010-01-31
 - Added several new methods to AddressMap.

## 2.5 - 2010-01-03 
 - Added "getQueryProperties" for URL.
 - Added CircularIndex for calculating an index that loops around once it
    reaches an upper bound.
 - Added LoadBalancedList interface and 2 related classes:
      RoundRobinLoadBalancedList and LoadBalancedLists class for creating
      synchronzied versions.
 - Added support for equals and hashCode properties on a URL.

## 2.4 - 2009-11-02
 - Added readToString() to read an entire InputStream into a String and return
 - Added StringUtil.escapeXml method to escape the entities in a String so
    it can be directly added to an XML document.

## 2.3 - 2009-10-27
 - Added AddressMap class - allows specific and wildcard for routing address
    based on digits.
 - Fixed bug with DecimalUtil.toString() - incorrect formatting when a double
    value had an exponent value such as 1.20E-5.  The bug was that it would
    return "1.20" instead of "0.00" since the exponent moved the decimal over.
    Underlying implementation changed.
 - Added URL query string parsing for extracting name/value pairs in a query
    string.
 - Added EnvironmentUtil class for creating common system properties such as
    the host FQDN, name, and domain.

## 2.2 - 2009-07-10
 - Added com.cloudhopper.commons.util.time package - which is useful for
    modeling periods of time such as a Year, Month, Day, Hour, etc.  Created
    specifically for running reports and grouping information by periods of
    time.
 - Added StringUtil.isEquals() which safely handles the case of both Strings
    being null.
 - Added floor functions for DateTime objects such as floorToHour() or
    floorToFiveMinutes().

## 2.1 - 2009-06-29
 - Added DateTimeUtil.toYearMonthDayUTC method to convert DateTime values to
    midnight while keeping their year, month, and day values the same regardless
    of the timezone used for the parameter.
 - Added StringUtil.isEmpty which mimics the behavior in Jakarta commons-lang.
 - Added CompressionUtil from LogFileManager application.
 - Added FileUtil utility class with code mostly extracted from the LogFileManager
    application.  parseFileNameToDateTime was modified to support a wider range
    of possible DateTime values with various formats.
 - Added three default FileFilter implementations.
 - Added two new FileFilters for startsWith and endsWith.
 - Added FileNameDateTimeComparator class to FileUtil to assist with sorting
    an array of Files by their embedded DateTime values.

## 2.0 - 2009-06-25
 - WARN: Renamed "net.cloudhopper.commons.util" package "com.cloudhopper.commons.util"
 - WARN: Removed all non-util packages into their own projects since we now
    have a common build framework.  A large number of dependencies are now gone
    which should make all projects that use this library smaller.  Only classes
    in the net.cloudhopper.util package exist in this library.
 - Added StringUtil.capitalize() and uncapitalize() method -- removes need for
    WordUtils from Apache commons lang library in some other libraries.
 - Added BeanUtil class and findBeanProperty() method -- obtains properties that follow
    the bean naming convention and allows simple access for getting, setting, or
    adding property values either via the setter/getter/adder methods or by falling
    back to the underlying field value.
 - Added ClassUtil.findEnumConstant with both case sensitive and non case
    sensitive searches for enum constants.
 - Added ManagementUtil to start a collection of common JMX utility functions.
 - Moved log4j to 'provided' maven scope vs. compile scope.
 - Added substituteWithProperties and substituteWithEnvironment methods to
    StringUtil to make substitutions from within configuration files easy for
    their values to be replaced with system or environment variables.
 - Added new RandomUtil with a simple implementation of generating simple
    random strings useful for creating (not very secure) passwords, verification
    codes, etc.
 - Added URL and URLParser classes
 - Added codec subpackage with very fast and efficient classes for encoding
    and decoding URL escaping.
 - Added Base64Codec to remove transitive dependency on Jakarta commons-codec
    library.
 - All ByteBuffer exceptions are now runtime exceptions vs. checked exceptions
    since they technically should not occur just like an ArrayIndexOutOfBoundsException

## 1.6 - 2009-02-13
 - Added EncryptUtil class for simple 2-way encryption.
 - Added Country and CountryUtil class
 - Added DateTimeUtil
 - Removed bean, dao packages: moved to ch-cortex project.
 - Moved DataSourceFactory to new sql package
 - Added StringUtil.isSafeString() method to check for strings that have a
    very limited set of characters permitted.  Useful for checking passwords
    etc.
 - Added TimeZone and TimeZoneUtil class for displaying and manipulating time
    zones.
 - Rewrote build system to use ant-common-build as well maven for dependency
    management.

## 1.5 - 2009-01-03
 - Added Hasher class to make hashing bytes or Strings easier. Useful for
    hashing passwords, credit card numbers, etc. and storing them in a database.
 - Added DataSourceFactory and DatabaseUtil in new db package.
 - Added TimedStateAtomicBoolean to track how long an AtomicBoolean has retained
    its current value.
 - Added PeriodFormatterUtil for creating a few handy formatters of a
    DateTime.
 - Added NameValue that supports generics.
 - Added MetaField annotation and Utility class.
 - Added DecimalUtil for formatting double values in a thread-safe way.
 - Added RunningTotal and RunningAverage classes for an easy way of tracking
    a moving total or average with a set number of last values.
 - Start of, but unfinished generic Dao library.

## 1.4 - 2008-12-19
 - Added ByteBuffer.hashCode() to make it safe to put in HashMaps or
    Hashtables.
 - Fixed bug with ByteBuffer.delete() which did not accept a value of 0
    for the count of bytes.
 - Changed behavior of ByteBuffer.remove(0) which threw an exception so
    that it now returns a zero length byte array. 
 - Added StringUtil.getAsciiString(byte[])
 - Fixed bug with ByteBuffer.indexOf() method that missed correctly finding
    an occurrence that was in a circular part of the buffer. Added extra test
    coverage for this case.
 - Added ByteBuffer.occurrences() method to search for the number of
    times a sequence of bytes occur in a non-overlapping manner.
 - Added additional toArray() methods, new copy() methods, and a split()
    method to ByteBuffer class.
 - Added gsm package of utility classes.
 - Added Sequencer class to generate overflow-safe sequence numbers in
    a thread-safe manner.
 - Added stripQuotes to StringUtil.

## 1.3 - 2008-12-11
 - Moved net.cloudhopper.commons.os package to ch-commons-firewall project.
    Removed all source code from this project.

## 1.2 - 2008-12-11
  - Added source and javadoc .zip files in distribution.

## 1.1 - 2008-12-11
  - Added util package - includes ByteBuffer, StackTraceUtil, StringUtil
  - Added src/test/java and new build.xml file to support building and running
     any tests generated in that library.

## 1.0 - 2008-11-08
  - Initial release