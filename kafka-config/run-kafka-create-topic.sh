#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
#### export KAFKA_OPTS="-Djavax.net.debug=all"
export KAFKA_OPTS="-Djavax.net.debug=all -Dcom.sun.jndi.ldap.object.disableEndpointIdentification=true"
export KAFKA_HEAP_OPTS="-Xmx1024M -Xms512m"
bin/kafka-topics.sh --create --bootstrap-server localhost:9093 --replication-factor 1 --partitions 1 --topic test
