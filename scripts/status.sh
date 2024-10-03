#!/bin/bash -e

curl -s http://localhost:8080/api/status | jq
