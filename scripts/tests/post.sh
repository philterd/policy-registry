#!/bin/bash
FILE=$1
curl -vvvv -X POST http://localhost:8080/api/profiles -d @$FILE -H "Content-Type: application/json"

