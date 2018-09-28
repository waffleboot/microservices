#!/bin/sh
./stop.sh
docker run --network eureka --rm -d -p 9001:9000 --name eureka-us --env spring.profiles.active=united-states --hostname eureka-us eureka
docker run --network eureka --rm -d -p 9002:9000 --name eureka-fr --env spring.profiles.active=france        --hostname eureka-fr eureka
docker run --network eureka --rm -d -p 9003:9000 --name eureka-vn --env spring.profiles.active=vietnam       --hostname eureka-vn eureka
docker logs -f eureka-us
