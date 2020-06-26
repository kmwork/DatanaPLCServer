#!/bin/sh -xe
docker images | grep "kmtemp\/datana\:2" | awk '{print \$1}' | xargs docker rmi