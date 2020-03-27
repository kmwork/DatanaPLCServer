# PLC Proxy Server (шлюз между котроллером и кафкой)
сделано по тех заданию: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Server
или файл `NIOKR-PLCProxyServer-220320-1401-76` в папке `<this project>/doc-manual`

задача в JIRE: https://jira.dds.lanit.ru/browse/VACUM-23

## cборка Maven 3 + JDK 13
`mvn clean compile package`

## Как пользоваться для теста (пока загрузка и работа с файлами так как нет PLC Client еще)
в папке test команый файл run-datana.sh
в нем указать пути к файлам json и явы и запускать в линуксе

### примерный синтаксис команды
`java -Dapp.dir=<путь к папке config> -cp datana-kafka-Datata-PLC-Server-alfa-1.jar ru.datana.steel.plc.util.JsonParserUtil`

## техническая документация 
в папке <this project>/doc-manual