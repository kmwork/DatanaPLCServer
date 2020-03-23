#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
keytool -exportcert -alias datana_server_alias_key -keystore keystore-server.jks -storepass 12345678 -file server.cer

