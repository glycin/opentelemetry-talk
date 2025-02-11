call gcloud auth login

call gcloud auth configure-docker europe-west4-docker.pkg.dev

call gcloud config set project operationalexcellence-439615

call gcloud container clusters get-credentials operational-excellence-cluster --zone europe-west4
