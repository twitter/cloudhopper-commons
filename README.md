Cloudhopper by Twitter
============================

cloudhopper-commons-charset
---------------------------

Utility Java classes for converting between charsets (mostly "mobile" in nature)
such as Unicode to GSM and vice versa. This library attempts to contain the fastest
and most efficient methods for converting charsets to/from byte arrays.  There
are also some useful charset specific static methods (e.g. GSMCharset.canRepresent()).

Based on experience at Twitter during our integrations with several hundred
mobile operators around the world, the following charsets are supported:

 - ISO-8859-1 (Latin-1)
 - ISO-8859-15 (Latin-15)
 - GSM (Default GSM alphabet including extended chars via 0x1B escapes)
 - Packed GSM (GSM alphabet 8-bit values are packed into 7-bit alignment) 
 - UCS-2
 - UTF-8
 - Modified UTF-8 (Useful for fast Java-only serialization; see info below)
 - Airwide IA5/GSM (Similar to GSM alphabet; customizations for Airwide SMSCs)
 - Vodafone D2 (Similar to GSM alphabet; customizations for Vodafone D2 SMSCs)
 - Vodafone Turkey (Similar to GSM alphabet; customizations for Vodafone Turkey SMSCs)
 - T-Mobile NL (Similar to GSM alphabet; customizations for T-Mobile Netherlands)

Contributors
------------

Joe Lauer (Twitter: [@jjlauer](http://twitter.com/jjlauer))

Installation
------------

Library versions >= 3.0.0 are now published to the Maven Central Repository.
Just add the following dependency to your project maven pom.xml:

    <dependency>
      <groupId>com.cloudhopper</groupId>
      <artifactId>ch-commons-charset</artifactId>
      <version>[3.0.0,)</version>
    </dependency>

Demo Code / Tutorials
---------------------

There are numerous examples of how to use various parts of this library:

    src/test/java/com/cloudhopper/commons/charset/demo

To run some of the samples, there is a Makefile to simplify the syntax required
by Maven:

    make charset1
    make gsm-benchmark

On Windows, you can use `nmake` in place of `make`

License
-------

Copyright (C) 2010-2012 Twitter, Inc.

This work is licensed under the Apache License, Version 2.0. See LICENSE for details.

Modified UTF-8 Caution and Performance
--------------------------------------

This library contains a charset called "Modified UTF-8".  Most of the
implementation is based on Java's DataOutputStream.writeUTF() and
DataInputStream.readUTF() methods.  Modified UTF-8 is not actually valid UTF-8
for Java characters > 0x7FFF, but as long as you read/write the produced byte
arrays for something like serialization, you'll be guaranteed to always get
the same Java character values.  You'll also benefit from much faster decoding
performance than Java's String class can provide and sometimes faster encoding.
Also, if you already have a pre-allocated byte array, this charset is generally
much faster for direct encoding vs. String.getBytes() and copying that again
into your target byte buffer.