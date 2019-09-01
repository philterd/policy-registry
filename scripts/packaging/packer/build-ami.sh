#!/bin/bash
set -e

# Set variables.

WORKINGDIRECTORY="../../../distribution"
TIMESTAMP=`date "+%Y.%m.%d-%H.%M.%S"`
GIT_COMMIT=`git rev-parse HEAD`
BUILD_NUMBER=${1:-000}
BUILT_BY=${2:-local}

SOURCE_AMI="ami-035be7bafff33b6b6"
SOURCE_AMI_DESCRIPTION="Amazon Linux 2 AMI (HVM), SSD Volume Type"

VERSION=$3
if [ -z "$VERSION" ]
then
  echo "Getting the project and version from the pom.xml."
  VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`
fi

FULL_VERSION="$VERSION.$BUILD_NUMBER.$GIT_COMMIT"
PROJECT=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId -f ../../../pom.xml | egrep -v '^\[|Downloading:' | tr -d ' \n'`
FORMATTED_PROJECT="${PROJECT^}"

START_INSTANCE=${4:-false}

# Remove any prior manifest.json.
rm -f manifest.json

echo "Building $PROJECT $VERSION ($FULL_VERSION)"
PACKER_LOG=1 PACKER_LOG_PATH=packer.log packer build \
  -only=aws \
  -var "source_ami_description=$SOURCE_AMI_DESCRIPTION" \
  -var "source_ami=$SOURCE_AMI" \
  -var "version=$FULL_VERSION" \
  -var "commit=$GIT_COMMIT" \
  -var "ami_name=$PROJECT $FULL_VERSION $TIMESTAMP" \
  -var "working_directory=$WORKINGDIRECTORY" \
  -var "built_by=$BUILT_BY" \
  -var "application=$PROJECT" \
  -var "build_number=$BUILD_NUMBER" \
  -var "iam_instance_profile=build.mtnfog.com" \
  ./$PROJECT.json

# If manifest.json does not exist Packer failed.
if [ -f "manifest.json" ]; then

  NEW_AMI=`cat manifest.json | jq -r '.builds[-1].artifact_id' |  cut -d':' -f2`

  if [ "$START_INSTANCE" == "true" ]; then
    echo "Creating instance from AMI $NEW_AMI"
    aws ec2 run-instances --image-id $NEW_AMI --count 1 --instance-type t3.small --key-name mtnfog --security-group-ids sg-067151e722ec103fb --subnet-id subnet-1e9c8456
  fi

  # Notify me the AMI creation is done.
  #echo "Sending SNS completion notification..."
  #aws sns publish --topic-arn "arn:aws:sns:us-east-1:341239660749:packer-ami-creation-completion" --message file://./packer.log --subject "$FORMATTED_PROJECT AMI $VERSION done"

  exit 0

else
  exit 1
fi
