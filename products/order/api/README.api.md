# APP : Order-api
## 작업
```bash
#export ACCOUNT_ID=$(aws sts get-caller-identity --output json | jq ".Account" | tr -d '"')
export ACCOUNT_ID=$(cat ~/.aws/poc.secret.json | jq -r .ACCOUNT_ID) && \
docker build -t day2 .
docker run -d -p 8008:8080 --name day2app day2:latest
## create repository
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com
docker build -t day2 .
docker tag day2:latest ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/day2:latest
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/day2:latest
```
```bash
export TAG_VERSION=1.0.12 && \
docker rm $(docker ps --all --no-trunc --format='{{json .}}' | jq -c 'select( .Image == "order-api:'${TAG_VERSION}'")' | jq -r .ID)
docker run -d --env SPRING_PROFILES_ACTIVE='local' -p 8080:8080 order-api:${TAG_VERSION} && \
docker logs -f $(docker ps --all --no-trunc --format='{{json .}}' | jq -c 'select( .Image == "order-api:'${TAG_VERSION}'")' | jq -r .ID)

# clean & build 하여 boot.jar 새롭게 만들고 docker image 를 ecr 에다가 upload, 그리고 terraform apply & deploy

export TAG_VERSION=1.0.18 && \
export SPRING_PROFILES_ACTIVE='local-container' && \
source ~/.tf/poc/loadInput2Env.sh && \
${GRADLEW_DIR}/gradlew -p ${GRADLEW_DIR} :products:order:api:clean && \
${GRADLEW_DIR}/gradlew -p ${GRADLEW_DIR} :products:order:api:build && \
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com && \
docker build --tag order-api:${TAG_VERSION} ${APP_PROJECT_DIR} && \
#docker rm $(docker ps --all --no-trunc --format='{{json .}}' | jq -c 'select( .Image == "order-api:'${TAG_VERSION}'")' | jq -r .ID) && \
docker run -d --network poc-network -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} -e AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} -e AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} -p 8080:8080 order-api:${TAG_VERSION} && \
docker logs -f $(docker ps --all --no-trunc --format='{{json .}}' | jq -c 'select( .Image == "order-api:'${TAG_VERSION}'")' | jq -r .ID)


# Deploy ONLY : 4분30 초 소요 예상
# todo : task-definition 에 sg + subnet 하드코딩값 변경 > dynamic
export TAG_VERSION=1.0.26 && \
export SPRING_PROFILES_ACTIVE='local-container' && \
source ~/.tf/poc/loadInput2Env.sh && \
${GRADLEW_DIR}/gradlew -p ${GRADLEW_DIR} :products:order:api:clean && \
${GRADLEW_DIR}/gradlew -p ${GRADLEW_DIR} :products:order:api:build && \
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com && \
docker build --tag order-api:${TAG_VERSION} ${APP_PROJECT_DIR} && \
docker tag order-api:${TAG_VERSION} ${ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/kmhak/service_local:${TAG_VERSION} && \
docker push ${ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/kmhak/service_local:${TAG_VERSION} && \
export EXEC_CMD=apply && \
terraform -chdir=${TERRAFORM_DIR} ${EXEC_CMD} \
      -var="aws_account_id=${ACCOUNT_ID}" \
      -var="aws_db_name=${AWS_DB_NAME}" \
      -var="aws_db_username=${AWS_DB_USERNAME}" \
      -var="aws_db_password=${AWS_DB_PASSWORD}" \
      -var="ecs_task_image_tag=${TAG_VERSION}" \
      -auto-approve && \
export LATEST_TD_REVISIONI=$(aws ecs list-task-definitions | jq -r ".taskDefinitionArns[0]" | cut -d ":" -f 7) && \
echo $LATEST_TD_REVISIONI && \
aws ecs run-task  --cluster cluster --task-definition service:${LATEST_TD_REVISIONI} --launch-type="FARGATE" --network-configuration '{ "awsvpcConfiguration": { "assignPublicIp":"ENABLED", "securityGroups": ["sg-07e80a6f5c321074c","sg-09de35a799829a32f","sg-08d55ddd64dfd2a45","sg-08aa334a7d4cfc5b0"], "subnets": ["subnet-0a87454ca9341b2d6"]}}' | jq . && \
aws ecs update-service --cluster cluster --service service --task-definition service:${LATEST_TD_REVISIONI} | jq .


# run container
# local docker test & print log (delete already stopped container before start)
export TAG_VERSION=1.0.20 && \
export SPRING_PROFILES_ACTIVE='local-container' && \
source ~/.tf/poc/loadInput2Env.sh && \
docker run -d --network poc-network -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} -e AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} -e AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} -p 8080:8080 order-api:${TAG_VERSION} && \
docker logs -f $(docker ps --all --no-trunc --format='{{json .}}' | jq -c 'select( .Image == "order-api:'${TAG_VERSION}'")' | jq -r .ID)
```

## References

## Troubleshooting
- task 실행에러
- > Caused by: java.lang.NoClassDefFoundError: software/amazon/awssdk/auth/credentials/AwsCredentialsProvider
  - 관련 의존성 따로 추가해야함 
- ecs executor 안되서 참고 : https://github.com/aws-containers/amazon-ecs-exec-checker
- local 환경에서 spring container 와 db(docker-compose로 띄운)연결에서 connection refused
  - docker-compose 에서 defaultNetwork를 "poc-network" 지정
  - spring app docker run 시에 --network poc-network 설정
  - db url 에서 "jdbc:postgresql://postgres:5432/orderdb"" 와 같이 설정
