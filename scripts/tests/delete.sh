#!/bin/bash
PROFILE=${1:-prof}
curl -X DELETE http://localhost:8080/api/profiles/$1

