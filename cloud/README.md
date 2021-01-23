This project contains the automation that sets up the service stack in GCP.

# Requirements
1. `gcloud` is installed (installation can be done
  via `curl https://sdk.cloud.google.com | bash`)
1. To configure a project run `gcloud config set project`
1. `gcloud` is initialized (if it's not then run `gcloud init` and proceed
  with the instructions to log in and setup the project)
1. `gcloud services enable container.googleapis.com`
1. `gcloud auth configure-docker`

# How to deploy changes
1. `export PROJECT_NAME=YOUR_PROJ_NAME`
1. `./setup.sh` to setup the cluster
1. `./push_images.sh` to build the application images and push them to the GCR
1. `./deploy.sh` to create kubernetes jobs/services to run the app
