#!/bin/bash
PROFILE=$1
curl -vvvv -X DELETE http://localhost:8080/api/profiles/$1

