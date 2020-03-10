#!/bin/bash

# Deletes the locally built image.

set -e

BUILD_NUMBER=$1
VERSION=$2

GIT_COMMIT=`git rev-parse --short HEAD`
PROJECT="philter-profile-registry"

if [ -z "$VERSION" ]
then
  VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`
fi

FULL_VERSION="$VERSION.$BUILD_NUMBER.$GIT_COMMIT"

docker rmi -f mtnfog/$PROJECT:$FULL_VERSION
docker rmi -f mtnfog/$PROJECT:latest
