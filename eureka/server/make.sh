#!/bin/sh
./stop.sh
mvn package -DskipTests=true
docker build -t eureka .

