@echo off
set DEPLOY_VERSION=0.0.1-SNAPSHOT

cd kotlin-backend
call gradlew -PdeployVersion=%DEPLOY_VERSION% bootBuildImage
docker push europe-west4-docker.pkg.dev/operationalexcellence-439615/operational-excellence/kotlin-backend:%DEPLOY_VERSION%

cd ..\persistence-service
call gradlew -PdeployVersion=%DEPLOY_VERSION% bootBuildImage
docker push europe-west4-docker.pkg.dev/operationalexcellence-439615/operational-excellence/persistence-service:%DEPLOY_VERSION%

cd ..\dotnet-backend
docker build -t europe-west4-docker.pkg.dev/operationalexcellence-439615/operational-excellence/dotnet-backend:%DEPLOY_VERSION% .
docker push europe-west4-docker.pkg.dev/operationalexcellence-439615/operational-excellence/dotnet-backend:%DEPLOY_VERSION%

cd ..
kubectl apply -f k8s
