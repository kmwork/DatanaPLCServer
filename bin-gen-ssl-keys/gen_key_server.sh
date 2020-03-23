#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
keytool -genkey -keyalg RSA -alias datana_server_alias_key -keystore keystore-server.jks -storepass 12345678 -validity 360 -keysize 2048

