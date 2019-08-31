#!/bin/bash
set -e

# This script pushes a docker image to AWS ECS ECR.

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

echo "Pushing docker image to AWS ECR: $PROJECT $FULL_VERSION"

eval $(aws ecr get-login --region us-east-1 --no-include-email)

docker tag mtnfog/$PROJECT:$FULL_VERSION 341239660749.dkr.ecr.us-east-1.amazonaws.com/mtnfog/$PROJECT:$FULL_VERSION
docker push 341239660749.dkr.ecr.us-east-1.amazonaws.com/mtnfog/$PROJECT:$FULL_VERSION

docker tag mtnfog/$PROJECT:$FULL_VERSION 341239660749.dkr.ecr.us-east-1.amazonaws.com/mtnfog/$PROJECT:latest
docker push 341239660749.dkr.ecr.us-east-1.amazonaws.com/mtnfog/$PROJECT:latest

docker logout
