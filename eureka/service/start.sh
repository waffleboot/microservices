#!/bin/sh
./stop.sh
docker run --network eureka --rm -d -p 8001:9000 --name eureka-service-us --env spring.profiles.active=united-states --hostname eureka-service-us eureka-service
docker run --network eureka --rm -d -p 8002:9000 --name eureka-service-fr --env spring.profiles.active=france        --hostname eureka-service-fr eureka-service
docker logs -f eureka-service-us
