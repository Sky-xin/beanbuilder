#! /usr/bin/sh
# Shell script to run the Bean Builder example
#
# Usage: JAVA_HOME=/usr/local/jdk1.3/ runnit.sh

${JAVA_HOME}/bin/java -cp beanbuilder.jar:${JAVA_HOME}/lib/dt.jar beantest.BeanTest
