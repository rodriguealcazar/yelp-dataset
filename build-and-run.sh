#!/bin/bash

set -e

SPARK_ROOT=/usr/spark-2.1.3
DOCKER_CONTAINER_ID=`docker ps | grep zeppelin-0.8.0 | cut -d' ' -f1`

if [[ -z  ${DOCKER_CONTAINER_ID// } ]]
then
    echo "Container for zeppelin-0.8.0 image not running. Please start a container first using:"
    echo "    docker run -d -p 8080:8080 -p 4040:4040 -p 4041:4041 zeppelin-0.8.0:latest"
    exit 1
fi

# Build jar
./gradlew clean assemble

# Copy jar to Docker container
docker cp build/libs/yelp-dataset-1.0-SNAPSHOT.jar ${DOCKER_CONTAINER_ID}:/tmp/yelp-dataset.jar

# Copy logging config
docker cp conf/log4j.properties ${DOCKER_CONTAINER_ID}:${SPARK_ROOT}/conf/

# Submit Spark job
docker exec -i -t ${DOCKER_CONTAINER_ID} ${SPARK_ROOT}/bin/spark-submit \
    --class org.rodriguealcazar.yelp.DatasetChallenge \
    --master local[2] \
    /tmp/yelp-dataset.jar

VOLUME_PATH=`docker volume ls -f name=yelp-spark --format "{{.Mountpoint}}"`
echo "*****"
echo "You can find the output at: "
echo "  ${VOLUME_PATH}/business_opening_hours_percentiles/"
echo "  ${VOLUME_PATH}/business_closing_hours_percentiles/"
echo "*****"
