#!/bin/bash
rm -f ./distribution/*.jar
cp ./filter-profile-registry-app/target/filter-profile-registry.jar ./distribution/
cp ./target/generated-sources/license/THIRD-PARTY.txt ./distribution/NOTICE.txt

