# PLC Proxy Server под Кафку
сделано по тех заданию: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Server
или файл `NIOKR-PLCProxyServer-220320-1401-76` в папке `<this project>/doc-manual`
задача в JIRA: https://jira.dds.lanit.ru/browse/VACUM-23 и https://jira.dds.lanit.ru/browse/NKR-364
## сборка Maven 3 + OpenJDK 11
```
mvn clean compile package spring-boot:repackage 
```
## Как пользоваться для теста
##### сервер -- слушает REST запросы 
    1) читает настройки из файлов из папки app.dir (системное свойство Ява-приложении)
    в) и кидает в kafka сообщения
    
#### Описание настроек
    application.yaml  -- настройки
    plc-meta-request-example.json -- для описания какие датчикм нужны
    plc-meta-config.json -- настройки контроллеров

        
#### Для тестирования рекомендация
желательно использовать Open JDK/JRE 11 (или выше 11ой явы)
желательно завернуть в докеры       
    
## техническая документация 
в папке <this project>/doc-manual

### Запуск программ
#### для Linux
```
run-for-camel-on-linux.sh
```

