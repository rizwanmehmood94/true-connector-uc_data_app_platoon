#!/bin/bash

echo "Creating Docker Image from UC repo..."
sudo docker build -f Dockerfile -t rdlabengpa/ids_uc_data_app_platoon:develop .
cd ..
echo "UC is ready"
echo "Starting deployment to Docker Hub"
sudo docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}
sudo docker push rdlabengpa/ids_uc_data_app_platoon:develop
echo "UC deployed to Docker Hub"