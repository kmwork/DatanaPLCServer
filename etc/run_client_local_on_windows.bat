echo on
rem *********************************************************************
rem *****              Command file for Windows OS                  *****
rem ***** PLC Client with Postgres, installed on localhost DATABASE *****
rem *********************************************************************
c:\apps\OpenJDK_13\bin\java.exe -Dapp.dir="c:\apps\Datana7" -Dapp.config.file="application-dev_client.yaml" -Dfile.encoding=CP866 -jar plc-client.jar
pause