chcp 1251
echo Программ для теста контролера Сименс
pause
set JAVA_HOME=f:\apps\jdk13
set PATH=%JAVA_HOME%\bin;%PATH%
java -Dapp.dir="f:/apps/datana_server_plc" -Dfile.encoding="CP1251" cp datana-kafka-Datata-PLC-Server-alfa-1.jar ru.datana.steel.plc.util.JsonParserUtil
