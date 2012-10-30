Cloudhopper by Twitter
============================

ch-commons-gsm
------------------

Utility Java classes for working with GSM mobile technologies (usually SMS or MMS).
Here are just some of the utilities:

 - Parsing Data Coding Scheme values or splitting up an SMS
 - Splitting an SMS properly for concatenation
 - Parsing User Data Headers from byte arrays

Contributors
------------

Joe Lauer (Twitter: [@jjlauer](http://twitter.com/jjlauer))

Installation
------------

Library versions >= 3.0.0 are now published to the Maven Central Repository.
Just add the following dependency to your project maven pom.xml:

    <dependency>
      <groupId>com.cloudhopper</groupId>
      <artifactId>ch-commons-gsm</artifactId>
      <version>[3.0.0,)</version>
    </dependency>

Demo Code / Tutorials
---------------------

There are numerous examples of how to use various parts of this library:

    src/test/java/com/cloudhopper/commons/gsm/demo

To run some of the samples, there is a Makefile to simplify the syntax required
by Maven:

    make datacoding

On Windows, you can use `nmake` in place of `make`

License
-------

Copyright (C) 2010-2012 Twitter, Inc.

This work is licensed under the Apache License, Version 2.0. See LICENSE for details.