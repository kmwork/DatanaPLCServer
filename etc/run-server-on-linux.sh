#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
java -Dapp.dir="/home/lin/work-lanit/Datata-Kafla-Gateway-K7/etc/" -Dfile.encoding=UTF8 -jar Datata-PLC-Server-alfa-1-jar-with-dependencies.jar