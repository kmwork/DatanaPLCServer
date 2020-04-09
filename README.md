# PLC Proxy Server и Client (шлюзы между контроллером и базой данных PostgeSQL)
сделано по тех заданию: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Server
или файл `NIOKR-PLCProxyServer-220320-1401-76` в папке `<this project>/doc-manual`
задача в JIRA: https://jira.dds.lanit.ru/browse/VACUM-23
## сборка Maven 3 + OpenJDK 13
```
mvn clean compile package spring-boot:repackage -P plcServer
mvn clean compile package spring-boot:repackage -P plcClient
```
## Как пользоваться для теста
##### сервер -- слушает REST запросы 
    1) читает из папки app.dir (системное свойство Ява-приложении)
    2) и ждет рест запросы
        GET - http://localhost:8080/rest/getVersion
        POST - http://localhost:8080/rest/getData   (в теле нужно передать JSON-запрос)
        примеры JSON можно copy-past в POSTMEN  -cм в папке `user-manual`
        
        если POST то читает по мере необходимости файл - `plc-meta-response-example.json` 
               если все ок то стучится к PLC контроллеру Ланита (Датана) с 
               по controller id =2 -- нормальные настройки а остальные ошибочные (так как 2 байта с контроллера можно  
               вычитывать в текущей версии  настроек S1200)
               и программа матерится на ошибки так как просят вычитать не доступные данные
        и JSON формируется в ответ
##### клиент - посылает запросы
    а) вызывает хранимки из 
    datana:
      database-options:
        postgresql-get-function: <тест sql - 1>
    б) читает переменные 
    datana:
      plc-server:
        # количество опросов сервера а потом завершить работу
        loop-count: 10
        # ожидание после каждого цикла в миллисекундах
        sleep-ms: 500    
        и как в примере 10 раз с задержкой 0.5 секунд стучится на сервер
    в) записывает через хранимку
        datana:
          database-options:
            postgresql-save-function: <тест sql - 2>
        
#### Для тестирования рекомендация
желательно использовать Open JDK/JRE 13
и для RESTfull запросов POSTMEN как плагин к Хрому - 
    postmen - расширение к хрому - https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop/related?hl=ru          
    
,
## техническая документация 
в папке <this project>/doc-manual

### Запуск программ
#### для Linux
```
run-client-on-linux.sh
run-server-on-linux.sh
```

#### для Windows
```
run_client_on_windows.bat
run_server_on_windows.bat
```