set JAVA_HOME=c:\apps\OpenJDK_13
set PATH=%JAVA_HOME%\bin;%PATH%
java -Dapp.dir="c:\apps\plc-datana-sever-v2" -Dfile.encoding=CP866 -jar Datata-PLC-Server-alfa-1-jar-with-dependencies.jar
pause