#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
java -Dapp.dir="/home/lin/apps/Datana7" -Dapp.config.file="application-remote_client.yaml" -Dfile.encoding=UTF8 -jar plc-client.jar