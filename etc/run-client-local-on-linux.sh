#!/bin/sh
# ************************************************************************
# *****               Command file for LINUX OS                      *****
# ***** PLC Client with Postgres, installed on LOCALHOST DATABASE!!! *****
# ************************************************************************
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin:$PATH
java -Dapp.dir="/home/datana/tools/etc" -Dapp.config.file="application-dev_client.yaml" -Dfile.encoding=UTF8 -jar plc-alfa-1.jar