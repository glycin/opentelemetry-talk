@echo off
set DEPLOY_VERSION=0.0.1-SNAPSHOT

docker build -t europe-west4-docker.pkg.dev/operationalexcellence-439615/operational-excellence/otel-collector-fe:%DEPLOY_VERSION% .
docker push europe-west4-docker.pkg.dev/operationalexcellence-439615/operational-excellence/otel-collector-fe:%DEPLOY_VERSION%

kubectl apply -f k8s

kubectl rollout restart deployment/otel-collector-fe
