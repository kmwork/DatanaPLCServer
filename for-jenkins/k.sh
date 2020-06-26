#!/bin/sh -xe
if ["$(docker images -q datana:2 2> /dev/null)" == ""]; then
    echo "empty"
  else
    echo "exits"
fi