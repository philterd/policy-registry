#!/bin/bash
set -e

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

echo "Building docker image for $PROJECT $FULL_VERSION"

rm -rf ./files/
mkdir -p files

cp ../../../distribution/filter-profile-registry.jar ./files/
cp ../../../distribution/application.properties ./files/
cp ../../../distribution/filter-profile-registry.conf ./files/
cp ../../../distribution/README.txt ./files/
cp ../../../distribution/LICENSE.txt ./files/
cp ../../../distribution/NOTICE.txt ./files/

echo "Building image mtnfog/$PROJECT:$FULL_VERSION"

docker build -t mtnfog/$PROJECT:$FULL_VERSION .

rm -rf ./files/
