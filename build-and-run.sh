#!/bin/bash

set -e

SPARK_ROOT=/usr/spark-2.1.3

# Build jar
./gradlew clean assemble

# Copy jar to Docker container
docker cp build/libs/yelp-dataset-1.0-SNAPSHOT.jar yelp-spark:/tmp/yelp-dataset.jar

# Copy logging config
docker cp conf/log4j.properties yelp-spark:${SPARK_ROOT}/conf/

# Submit Spark job
docker exec -i -t yelp-spark ${SPARK_ROOT}/bin/spark-submit \
    --class org.rodriguealcazar.yelp.DatasetChallenge \
    --master local[2] \
    /tmp/yelp-dataset.jar
