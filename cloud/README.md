This project contains the automation that sets up the service stack in GCP.

# Requirements
1. `gcloud` is installed (installation can be done
  via `curl https://sdk.cloud.google.com | bash`)
1. `gcloud` is initialized (if it's not then run `gcloud init` and proceed
  with the instructions to log in and setup the project)
1. Following APIs are enabled (if they are not, you can enable them
  from the gcloud web ui): `container.googleapis.com`
1. Ensure that you can push images to the GCP container registry
  https://cloud.google.com/container-registry/docs/advanced-authentication

# How to deploy changes
1. `export PROJECT_NAME=YOUR_PROJ_NAME`
1. `./setup.sh` to setup the cluster
1. `./push_images.sh` to build the application images and push them to the GCR
1. `./deploy.sh` to create kubernetes jobs/services to run the app
