#!/bin/bash

BUILD_NUMBER=$1
VERSION=$2
GIT_COMMIT=`git rev-parse --short HEAD`

if [ -z "$VERSION" ]
then
  VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | egrep -v '^\[|Downloading:' | tr -d ' \n'`
fi

FULL_VERSION="$VERSION.$BUILD_NUMBER.$GIT_COMMIT"

sed -i "s/{{{version}}}/$FULL_VERSION/g" ./distribution/README.txt
