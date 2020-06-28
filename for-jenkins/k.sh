#!/bin/sh -xe
docker images | grep "datana" | awk '{print $3}' | xargs docker rmi -f