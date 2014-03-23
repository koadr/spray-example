#!/bin/sh

sbt app_version

if [ $? -eq 0 ];
then
    echo "Could not store current_version file."
    exit 1
else
    exit 0
fi
