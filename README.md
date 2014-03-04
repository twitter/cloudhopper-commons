Cloudhopper by Twitter [![Build Status](https://secure.travis-ci.org/twitter/cloudhopper-commons.png?branch=master)](http://travis-ci.org/twitter/cloudhopper-commons)
======================

Common Java libraries used by the Cloudhopper family of mobile messaging applications at Twitter.

cloudhopper-commons-charset
---------------------------
Utility Java classes for converting between charsets (mostly "mobile" in nature) such as Unicode to GSM and vice versa. This library attempts to contain the fastest and most efficient methods for converting charsets to/from byte arrays.

cloudhopper-commons-gsm
-----------------------
Utility Java classes for working with [GSM](http://en.wikipedia.org/wiki/GSM "GSM") mobile technologies (usually SMS or MMS).

cloudhopper-commons-io
----------------------
I/O utilities. Currently useful for file watching, reading and writing.

cloudhopper-commons-locale
--------------------------
Localization utilities for Cloudhopper projects. Country, country code and timezone utilities.

cloudhopper-commons-rfs
-----------------------
Cloudhopper Commons RFS supports copying and moving files to remote filesystems. Various protocols are supported such as FTP, SSL/TLS FTP, Secure FTP, and possibly more in the future.  Each is configured with a simple URL-based syntax. Authentication is supported for each protocol.

cloudhopper-commons-sql
-----------------------
Provides a consistent interface to configure, create, and manage (via JMX) various DataSource providers that may provide Connection pooling. Currently supports C3P0 and Proxool.

cloudhopper-commons-util
------------------------
Utility Java classes commonly used across all Cloudhopper-based projects.

cloudhopper-commons-xbean
-------------------------
The Xbean Java library is a set of utility classes for creating or configuring a Java object from XML. The library is a simple alternative to other XML-to-Java frameworks such as Spring. The library will only map in a single direction - XML -> Java. This limited scope helps keep this library small, fast, and very good at what it was mainly intended for -- application configuration files.

cloudhopper-httpclient-util
---------------------------
Apache Jakarta HttpClient 4.x utility library.

cloudhopper-jetty
-----------------
Wrapper around Jetty HTTP Server that makes configuration easy and possible from a simple few configuration objects.

cloudhopper-sxmp
----------------
Library implementing the SXMP protocol. SXMP is like SMPP, but as readable XML.

cloudhopper-commons-ssl
-----------------------

Utility Java classes for working with SSL in Cloudhopper-based projects. Originally part of cloudhopper-smpp.


Installation
------------

Library versions are now published to the Maven Central Repository.
Just add the following dependency to your project maven pom.xml:

    <dependency>
      <groupId>com.cloudhopper</groupId>
      <artifactId>ch-artifact-name</artifactId>
      <version>version.number</version>
    </dependency>

Demo Code / Tutorials
---------------------

Most of the modules contain examples of how to use various parts of the libraries in the demo package of the test sources. Additionally, some of the modules have a simple Makefile that simplify running demos.

License
-------

Copyright (C) 2010-2014 Twitter, Inc.

This work is licensed under the Apache License, Version 2.0. See LICENSE for details.
