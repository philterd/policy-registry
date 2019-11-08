#!/bin/bash
set -e

# This script pushes a docker image to DockerHub.

BUILD_NUMBER=$1
VERSION=$2

GIT_COMMIT=`git rev-parse --short HEAD`
PROJECT="philter-profile-registry"

if [ -z "$VERSION" ]
then
  echo "Getting version from the pom.xml"
  VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`
fi

FULL_VERSION="$VERSION.$BUILD_NUMBER.$GIT_COMMIT"

echo "Pushing docker image to DockerHub: $PROJECT $FULL_VERSION"

DOCKERHUB_PASSWORD=`aws ssm get-parameter --region us-east-1 --name dockerhub_password | jq -r .Parameter.Value`
echo $DOCKERHUB_PASSWORD | docker login --username jzemerick --password-stdin

docker tag mtnfog/$PROJECT:$FULL_VERSION mtnfog/$PROJECT:$FULL_VERSION
docker push mtnfog/$PROJECT:$FULL_VERSION
docker logout
