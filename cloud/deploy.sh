#!/bin/bash
set -euxo pipefail

for template_file in *.yaml.template
do
  cat $template_file | sed -e 's/PROJECT_NAME/'"$PROJECT_NAME"'/g' > "${template_file/.template/}"
done

kubectl apply -f postgres.yaml
sleep 10s
kubectl apply -f database-schema.yaml
kubectl apply -f server.yaml
