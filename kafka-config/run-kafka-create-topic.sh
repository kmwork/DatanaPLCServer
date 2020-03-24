#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
#### export KAFKA_OPTS="-Djavax.net.debug=all"
export _JAVA_OPTIONS="-Djavax.net.debug=all -Dcom.sun.jndi.ldap.object.disableEndpointIdentification=true"
#### export KAFKA_OPTS="-Dcom.sun.jndi.ldap.object.disableEndpointIdentification=true"
export KAFKA_HEAP_OPTS="-Xmx1024M -Xms512m"
bin/kafka-topics.sh --create --bootstrap-server "SSL://localhost:9093" -command-config ./config/ssl-user-config.properties --replication-factor 1 --partitions 1 --topic datana_topic_kafka
### bin/kafka-topics.sh --list --bootstrap-server "SSL://127.0.0.1:9093" --command-config ./config/ssl-user-config.properties
