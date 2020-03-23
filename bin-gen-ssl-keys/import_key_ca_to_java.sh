#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
### keytool -importcert -alias datana_client_cer_javaca -file client.cer -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit
keytool -importcert -alias datana_server_cer_javaca -file server.cer -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit