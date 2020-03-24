#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
#### export KAFKA_OPTS="-Djavax.net.debug=all"
export KAFKA_OPTS="-Djavax.net.debug=all -Dcom.sun.jndi.ldap.object.disableEndpointIdentification=true"
#### export KAFKA_OPTS="-Dcom.sun.jndi.ldap.object.disableEndpointIdentification=true"
export KAFKA_HEAP_OPTS="-Xmx1024M -Xms512m"
bin/kafka-server-start.sh config/server.properties
