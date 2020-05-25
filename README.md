# !!!! ТЕСТОВОЕ ПО !!!! - PLC Proxy Server и Client 
****(шлюзы по Apache ActiveMQ JMS-брокер, при этом сервер и клиент работают**** 
****в холостом режиме - для проверки нагрузки на транспорт)****
---

### для начала - нужно поднять и настроить Apache ActiveMQ
по ссылке https://activemq.apache.org/components/classic/download/

## сборка Maven 3 + OpenJDK 13
```
mvn clean compile package spring-boot:repackage 
```
##### клиент - посылает запросы
* читает конфиги ***application-dev_client.yaml*** из папки app.dir (системное свойство Ява-приложении)
* читает переменные
* и генерирует сообщения в jms
        
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
