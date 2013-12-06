Cloudhopper by Twitter
============================

cloudhopper-commons-gsm
---------------------------

## 3.0.0 - 2012-10-30
 - No major code changes, mostly project layout changes in prep of release to
   Maven Central Repository
 - Maven artifact group changed from cloudhopper to com.cloudhopper
 - Readme and changelog switched to markdown
 - Log4J dependency switched to SLF4J
 - Updated underlying maven license plugin - every java file had its
   license positioned after package declaration.

## 2.1.1 - 2011-10-26
 - Upgrade parent POM dependency from 1.5 to 1.6
 - Added support for OSGI in final jar

## 2.1.0 - 2011-07-26
 - Upgrade parent POM dependency from 1.0 to 1.5
 - Switched to new version format.
 - Added Version class to compile build info into jar

## 2.0 - 2010-03-14
 - Migrated build system from Ant to Maven
 - Deleted all legacy Ant build files (build.xml, Dependency.txt)
 - Added Apache license to top of all source files
 - Moved demo source code from src/demo/java into src/test/java directory and
    placed in separate "demo" package.
 - Added "Makefile" with targets that match previous demo ant tasks.

## 1.5 - 2010-12-16 
 - Added GsmUtil.createConcatenatedBinaryShortMessages() to help split up
    a binary short message into multiple short messages with the correct
    GSM User Data Header included.

## 1.4 - 2010-05-23
 - Added some additional documentation
 - Renamed GsmUtil.removeUserDataHeader to getShortMessageUserData()
 - Added GsmUtil.getShortMessageUserDataHeader()
 - Moved all EsmClass methods over to ch-smpp project (since they are not
    actually specific to GSM, but rather SMPP)

## 1.3 - 2010-02-28
 - Added DataCoding class for parsing and handling GSM 3.38 DCS values.

## 1.2 - 2010-02-25
 - Added GSM utility methods for verify the type of message present in an
    "esm_class" such as delivery receipt, etc.

## 1.1 - 2010-02-22
 - Added user data header utilities

## 1.0 - 2009-04-01
 - Moved everything to com.cloudhopper.commons.gsm package
 - Moved log4j to 'provided' maven scope

## 0.9 - 2009-02-16
 - Initial release extracted from ch-commons-util
