#!/bin/bash

set -e

cd docker
docker build . -t zeppelin-0.8.0:latest
docker run -d -p 8080:8080 -p 4040:4040 -p 4041:4041 --mount 'type=volume,src=yelp-spark,dst=/tmp/yelp-spark' zeppelin-0.8.0:latest
cd ..
