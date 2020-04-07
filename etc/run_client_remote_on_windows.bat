echo on
rem ******************************************************************
rem *****             Command file for WINDOWS OS                *****
rem ***** PLC Client with Postgres, installed on REMOVE DATABASE *****
rem ******************************************************************
c:\apps\OpenJDK_13\bin\java.exe -Dapp.dir="c:\apps\Datana7" -Dapp.config.file="application-remote_client.yaml" -Dfile.encoding=CP866 -jar plc-client.jar
pause