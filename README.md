# PLC Proxy Server (шлюз между котроллером и кафкой)
сделано по тех заданию: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Server
или файл `NIOKR-PLCProxyServer-220320-1401-76` в папке `<this project>/doc-manual`

задача в JIRE: https://jira.dds.lanit.ru/browse/VACUM-23

## cборка Maven 3 + JDK 13
`mvn clean compile package spring-boot:repackage`

## Как пользоваться для теста (пока загрузка и работа с файлами так как нет PLC Client еще)
что программа делает:
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
        
#### Для тестирования рекомендация
желательно использовать Open JDK/JRE 13
и для RESTfull запросов POSTMEN как плагин к Хрому - 
    postmen - расшрение к хрому - https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop/related?hl=ru          
    
#### Примеры работы программы (тестовой)
    пример лога "1" в <this project>/test/example-log-not-vpn.txt (это без vpn) - проказываю когда нет связи
    пример лога "2"в <this project>/test/example-log-with-vpn-good-link.txt (есть связь с контроллером) - проказываю когда нет связи


### примерный синтаксис команды
java -Dapp.dir=<путь к json файлам> -Dfile.encoding=UTF-8 -jar Datata-PLC-Server-alfa-1-jar-with-dependencies.jar

## техническая документация 
в папке <this project>/doc-manual