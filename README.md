# PLC Proxy Server (шлюз между котроллером и кафкой)
сделано по тех заданию: https://conf.dds.lanit.ru/display/NIOKR/PLC+Proxy+Server
или файл `NIOKR-PLCProxyServer-220320-1401-76` в папке `<this project>/doc-manual`

задача в JIRE: https://jira.dds.lanit.ru/browse/VACUM-23

## cборка Maven 3 + JDK 13
`mvn clean compile assembly:single`

## Как пользоваться для теста (пока загрузка и работа с файлами так как нет PLC Client еще)
в папке test командный файл run-datana.sh
в нем указать пути к файлам json и явы и запускать в линуксе

что программа делает:
    1) читает из папки app.dir (системное свойство Ява-приложении)
    2) файлы читаются и парсятся - `plc-meta-response-example.json` и `request-example.json`
       если все ок то стучится к PLC контроллеру Ланита (Датана) с 
       по controller id =2 -- нормальные настройки а остальные ошибочные (так как 2 байта с контроллера можно  
       вычитывать в текущей версии  настроек S1200)
       и программа матерится на ошибки так как просят вычитать не доступные данные
    3) правильные данные читает и формирует в консоли JSON и ошибки что были тоже пишет в JSON
    
#### Примеры работы программы (тестовой)
    пример лога "1" в <this project>/test/example-log-not-vpn.txt (это без vpn) - проказываю когда нет связи
    пример лога "2"в <this project>/test/example-log-with-vpn-good-link.txt (есть связь с контроллером) - проказываю когда нет связи


### примерный синтаксис команды
java -Dapp.dir=<путь к json файлам> -Dfile.encoding=UTF-8 -jar Datata-PLC-Server-alfa-1-jar-with-dependencies.jar

## техническая документация 
в папке <this project>/doc-manual