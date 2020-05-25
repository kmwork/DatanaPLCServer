# !!!! ТЕСТОВОЕ ПО !!!! - PLC Proxy Server и Client 
****(шлюзы по Apache ActiveMQ JMS-брокер, при этом сервер и клиент работают**** 
****в холостом режиме - для проверки нагрузки на транспорт)****
---

### для начала - нужно поднять и настроить Apache ActiveMQ
по ссылке https://activemq.apache.org/components/classic/download/

### Введение
это тесты для транспорта JMS под проект по тех заданию: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Server
или файл `NIOKR-PLCProxyServer-220320-1401-76` в папке `<this project>/doc-manual`
задача в JIRA: https://jira.dds.lanit.ru/browse/VACUM-23
## сборка Maven 3 + OpenJDK 13
```
mvn clean compile package spring-boot:repackage -P plcServer
mvn clean compile package spring-boot:repackage -P plcClient
```
##### клиент - посылает запросы
* читает конфиги ***application-dev_client.yaml*** и ***plc-meta-request-example.json*** из папки app.dir (системное свойство Ява-приложении)
* читает переменные
``` 
   datana:
      plc-server:
        # количество опросов сервера а потом завершить работу
        loop-count: 10
        # ожидание после каждого цикла в миллисекундах
        sleep-ms: 500
```    
    и как в примере 10 раз с задержкой 0.5 секунд стучится на сервер

        
#### Для тестирования рекомендация
желательно использовать Open JDK/JRE 13
    
## техническая документация 
в папке <this project>/doc-manual

### Запуск программ
#### для Linux
```
run-client-local-on-linux.sh
run-server-on-linux.sh
```

#### для Windows
```
run_client_local_on_windows.bat
run_server_on_windows.bat
```

#### Demon for Linux
инфа тут 
https://computingforgeeks.com/how-to-run-java-jar-application-with-systemd-on-linux/

подложите файл datana7-plc-server.service (путь и юзера нужно изменить) в папку (нужны рут права)
в /etc/systemd/system
