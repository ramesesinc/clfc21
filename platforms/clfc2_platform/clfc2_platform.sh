#!/bin/sh
# This will be the run directory
RUN_DIR=`pwd`
# This will the base directory
BASE_DIR=`pwd`

# set the java home if neccessary
# JAVA_HOME=

# set java options
JAVA_OPTS="-Xmx512m" 

# run java
if [ "x$JAVA_HOME" = "x" ]; then
   JAVA="java"
else
   JAVA="$JAVA_HOME/bin/java"
fi

echo ""
echo "========================================================================"
echo ""
echo "  Platform    : Osiris3 Client Platform"
echo "  Application : CLFC 2.0"
echo ""
echo "  JAVA_HOME   : $JAVA_HOME"
echo "  JAVA_OPTS   : $JAVA_OPTS"
echo "  JAVA        : $JAVA"
echo ""
echo "========================================================================"
echo ""

$JAVA $JAVA_OPTS -classpath lib/*:. com.rameses.osiris3.platform.OSBootStrap

