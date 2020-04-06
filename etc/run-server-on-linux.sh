#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
java -Dapp.profile=dev_postgres -Dapp.dir="/home/lin/apps/Danata7" -Dfile.encoding=UTF8 -jar Datata-PLC-Server-alfa-1-spring-boot.jar