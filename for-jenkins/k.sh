#!/bin/bash -xe
docker ps | grep "datana" | awk '{print $1}' | xargs docker stop
docker images | grep "datana" | awk '{print $3}' | xargs docker rmi -f