#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
#### export KAFKA_OPTS="-Djavax.net.debug=all"
/home/lin/apps/kafka241/bin/kafka-server-start.sh /home/lin/apps/kafka241/config/server.properties
