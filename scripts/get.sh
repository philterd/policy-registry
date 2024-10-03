#!/bin/bash -e

POLICY=${1:-default}

curl -s http://localhost:8080/api/policies/${POLICY} | jq
