#!/bin/sh

sbt app-version

if [ $? -ne 0 ];
then
    echo "Could not store current_version file."
    exit 1
else
    BASE_DIR=$(pwd -P)
    git add ${BASE_DIR}/
    exit 0
fi
