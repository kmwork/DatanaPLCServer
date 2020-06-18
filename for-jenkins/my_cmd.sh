Jenkins-Crumb:64521cf407749f4bdddc4a045bfc484099f772c6b82cd69133330890a43a2541

curl --user kostya:1 -H "Jenkins-Crumb:64521cf407749f4bdddc4a045bfc484099f772c6b82cd69133330890a43a2541" -d "script=print(\"1\")" $SERVER/script

curl -I -X POST http://kostya:111bb7577e5b442b59ee810106103bde12@localhost:8080/job/k2/build -H "Jenkins-Crumb:64521cf407749f4bdddc4a045bfc484099f772c6b82cd69133330890a43a2541"
