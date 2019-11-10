#!/bin/bash
PROFILE=${1:-default}
curl -s http://localhost:8080/api/profiles/$PROFILE | jq

