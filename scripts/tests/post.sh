#!/bin/bash
FILE=${1:-profile.json}
curl -vvvv http://localhost:8080/api/profiles -d @$FILE -H "Content-Type: application/json"

