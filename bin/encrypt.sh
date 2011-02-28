#!/bin/sh

# cd to the top of the directory tree so that configs and logs will work properly
cd $(dirname $0)/..

# Build CLASSPATH
CLASSPATH=build/classes/:build/classes.demo/
for file in lib/*.jar
do
    CLASSPATH=$CLASSPATH:$file
done
echo $CLASSPATH
export CLASSPATH

# Default JAVA_HOME
[ -z "$JAVA_HOME" ] && JAVA_HOME=/usr/java/default

$JAVA_HOME/jre/bin/java net.cloudhopper.commons.util.EncryptUtilMain $@
