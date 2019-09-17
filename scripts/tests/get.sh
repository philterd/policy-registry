#!/bin/bash
PROFILE=$1
curl -vvvv http://localhost:8080/api/profiles/$PROFILE

