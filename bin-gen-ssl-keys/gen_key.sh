#!/bin/sh
export JAVA_HOME=/home/lin/apps/jdk13
export PATH=$JAVA_HOME/bin;$PATH
keytool -genkey -keyalg RSA -alias danata_client_alia_key -keystore keystore.jks -storepass 12345678 -validity 360 -keysize 2048
###echo "step-3: Сгенерировать csr-запрос на сертификат"
##keytool -certreq -alias danata_server_alia_key -file danata_server_x509.csr -keystore keystore.jks

