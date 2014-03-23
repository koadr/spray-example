#!/bin/sh

sbt app-version

if [ $? -ne 0 ];
then
    echo "Could not store current_version file."
    exit 1
else
    exit 0
fi
