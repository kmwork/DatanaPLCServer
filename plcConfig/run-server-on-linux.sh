#!/bin/sh
##### PLC SERVER #####
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
java -Dapp.dir="/home/lin/apps/plc-bin" -Dfile.encoding=UTF8 -jar plc-server.jar