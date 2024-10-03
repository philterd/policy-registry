#!/bin/bash -e

POLICY=${1:-default}

curl -X DELETE http://localhost:8080/api/policies/${POLICY}
