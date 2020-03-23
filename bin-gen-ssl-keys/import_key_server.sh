#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
keytool -importcert -alias datana_server_client_cer -keystore keystore-server.jks -storepass 12345678  -trustcacerts -file client.cer

