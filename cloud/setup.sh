#!/bin/bash
set -euxo pipefail

cluster=$(gcloud container clusters list --format="json" | jq '.[] | select(.name == "healthcheck")')

if [ -z "$cluster" ];
then
  echo "No clusters found, creating"
  gcloud container clusters create healthcheck --zone europe-west1-b
fi;