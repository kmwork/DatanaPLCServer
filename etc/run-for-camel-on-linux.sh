#!/bin/sh
##### PLC SERVER #####
export JAVA_HOME=/home/lin/apps/jdk14
export PATH=$JAVA_HOME/bin:$PATH
java -Dapp.dir="/home/lin/apps/DatanaCamel" -Dfile.encoding=UTF8 -jar plc-camel.jar