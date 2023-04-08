FROM xxx.dkr.ecr.ap-northeast-2.amazonaws.com/java-with-aws-cli:latest
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#EXPOSE 8080
