# Spring Dockerization > https://github.com/spring-guides/top-spring-boot-docker
# Base Image > https://hub.docker.com/layers/adoptopenjdk/openjdk11/jre-11.0.16.1_1-alpine/images/sha256-5454cc66dfdb61e8292051d6d9f4ef2c220b468171dc7808c9689acceba3f2f9?context=explore
ARG REGISTRY_URL="docker.io/library"
ARG BASE_IMG_NAME="amazoncorretto"
ARG BASE_IMG_VERS="17.0.0-alpine"
FROM ${REGISTRY_URL}/${BASE_IMG_NAME}:${BASE_IMG_VERS}

ARG COPY_HOME="."
ARG APP_NAME="api"
ARG HASH_TAG="NO_INCLUDED"
LABEL "com.kmhak.poc"="${APP_NAME}:${HASH_TAG}"

WORKDIR /poc
COPY ${COPY_HOME}/build/libs/${APP_NAME}-*.jar boot.jar
RUN chmod +x boot.jar

ENTRYPOINT exec java ${JAVA_OPTS} -jar /poc/boot.jar
