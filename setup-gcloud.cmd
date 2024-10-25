gcloud auth login

gcloud auth configure-docker europe-west4-docker.pkg.dev

gcloud config set project operationalexcellence-439615

gcloud container clusters get-credentials operational-excellence-cluster --zone europe-west4
