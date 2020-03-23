#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
keytool -exportcert -alias datana_client_alias_key -keystore keystore-client.jks -storepass 12345678 -file client.cer

