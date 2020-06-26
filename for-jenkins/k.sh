#!/bin/sh -xe
if ["$(docker images -q datana:2 2> /dev/null)" == ""]; then
    echo "1111111111111"
fi