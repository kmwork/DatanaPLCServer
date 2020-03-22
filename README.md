# PLC Proxy Server (шлюз между котроллером и кафкой)
сделано по тех заданию: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Server
или файл `NIOKR-PLCProxyServer-220320-1401-76` в папке `<this project>/doc-manual`

задача в JIRE: https://jira.dds.lanit.ru/browse/VACUM-23

## cборка Maven 3 + JDK 8
`mvn clean compile assembly:single`

## Как пользоваться
после сборки мавеном, из папки <this project>/etc для bat и sh командые файлы

### примерный синтаксис команды
`java -Dapp.dir=<путь к папке config> -jar target/Siemens-K4-1.0-SNAPSHOT-jar-with-dependencies.jar`

## настройка
файл `datana_siemens.properties` c настройками

## техническая документация 
в папке <this project>/doc-manual