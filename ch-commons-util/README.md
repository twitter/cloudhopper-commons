Cloudhopper by Twitter
============================

cloudhopper-commons-util
------------------------

Utility Java classes commonly used across all Cloudhopper-based projects.

Contributors
------------

Joe Lauer (Twitter: [@jjlauer](http://twitter.com/jjlauer))

Installation
------------

Library versions >= 6.0.0 are now published to the Maven Central Repository.
Just add the following dependency to your project maven pom.xml:

    <dependency>
      <groupId>com.cloudhopper</groupId>
      <artifactId>ch-commons-util</artifactId>
      <version>[6.0.0,)</version>
    </dependency>

Demo Code / Tutorials
---------------------

There are numerous examples of how to use various parts of this library:

    src/test/java/com/cloudhopper/commons/util/demo

To run some of the samples, there is a Makefile to simplify the syntax required
by Maven:

    make uptime
    make window

On Windows, you'd run:
 
    nmake uptime
    nmake window

License
-------

Copyright (C) 2010-2012 Twitter, Inc.

This work is licensed under the Apache License, Version 2.0. See LICENSE for details.