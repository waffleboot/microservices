#!/bin/sh
mvn package -DskipTests=true
docker build -t eureka-service .
