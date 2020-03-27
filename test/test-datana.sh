#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
java -Dapp.dir="/home/lin/work-lanit/Datata-Kafla-Gateway-K7/test" -cp datana-kafka-Datata-PLC-Server-alfa-1.jar ru.datana.steel.plc.util.JsonParserUtil
