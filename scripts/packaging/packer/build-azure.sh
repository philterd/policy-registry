#!/bin/bash
set -e

# Builds using the jeffrey@mtnfog.com Azure Pay-as-you-go subscription.
# The default VM username for packer on Azure is "packer".

# https://docs.microsoft.com/en-us/azure/virtual-machines/linux/build-image-with-packer

echo "Getting info from the pom.xml."
PROJECT=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`
BUILD_NUMBER=${1:-000}

VERSION=$2
if [ -z "$VERSION" ]
then
  VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`
fi

FULL_VERSION=`echo "$VERSION.$BUILD_NUMBER" | tr '[:upper:]' '[:lower:]'`
FORMATTED_VERSION=`echo $FULL_VERSION | sed 's/\./-/g'`

# The container name has to satisfy the regex: ^[a-z0-9][a-z0-9\\-]{2,62}$
CONTAINER_NAME="$PROJECT-$FORMATTED_VERSION"

# Get the Azure client-secret from AWS Parameter Store.
AZURE_CLIENT_SECRET=`aws ssm get-parameter --name azure_client_secret | jq -r .Parameter.Value`

echo "Container name: $CONTAINER_NAME"

PACKER_LOG=1 PACKER_LOG_PATH=packer.log packer build \
  -only=azure \
  -var "capture_container_name=$CONTAINER_NAME" \
  -var "ssh_username=packer" \
  -var "working_directory=../../../distribution" \
  -var "azure_client_secret=$AZURE_CLIENT_SECRET" \
  ./philter.json
