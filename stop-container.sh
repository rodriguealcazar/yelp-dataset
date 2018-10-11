#!/bin/bash

set -e

DOCKER_CONTAINER_ID=`docker ps | grep zeppelin-0.8.0 | cut -d' ' -f1`

echo "Stopping container"
docker container stop ${DOCKER_CONTAINER_ID}

echo "Deleting volume"
docker volume rm yelp-spark
