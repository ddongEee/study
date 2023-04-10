#!/bin/bash

./gradlew clean && \
./gradlew build && \
project_path=$(pwd) && \
file_path=$project_path'/build/distributions/jambda.zip' && \
aws lambda update-function-code --function-name test-java-exam --zip-file fileb://$file_path
