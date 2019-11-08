#!/bin/bash
rm -f ./distribution/*.jar
cp ./philter-profile-registry-app/target/philter-profile-registry.jar ./distribution/
cp ./target/generated-sources/license/THIRD-PARTY.txt ./distribution/NOTICE.txt

