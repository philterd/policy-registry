#!/bin/bash -e

FILE=${1:-policy.json}

curl -vvvv http://localhost:8080/api/policies -d @${FILE} -H "Content-Type: application/json"
