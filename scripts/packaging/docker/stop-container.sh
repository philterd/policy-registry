#!/bin/bash

BUILD_NUMBER=$1
VERSION=$2

GIT_COMMIT=`git rev-parse --short HEAD`
PROJECT="filter-profile-registry"

if [ -z "$VERSION" ]
then
  echo "Getting version from the pom.xml"
  VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`
fi

FULL_VERSION="$VERSION.$BUILD_NUMBER.$GIT_COMMIT"

echo "Stopping container mtnfog/$PROJECT:$FULL_VERSION"

docker stop "$PROJECT-$FULL_VERSION"
