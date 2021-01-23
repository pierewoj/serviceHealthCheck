#!/bin/bash
set -euxo pipefail

# extend app list when more apps are supported
for app in server database-schema
do
(
  cd ../"$app"
  docker build -t gcr.io/"$PROJECT_NAME"/"$app" .
  docker push gcr.io/"$PROJECT_NAME"/"$app"
)
done
