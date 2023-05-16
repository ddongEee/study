# Terraform - order-api
***
### Script
```bash
export AWS_PROFILE=poc-admin
export ACCOUNT_ID=$(aws sts get-caller-identity --output json | jq ".Account" | tr -d '"')
export AWS_IAM_USER_NAME=devops
# aws 에 해당 작업만을 위한 role 생성. 초안..
aws iam create-user --user-name ${AWS_IAM_USER_NAME}
aws iam list-users
# access key 생성 및 local profile 에 setting 할것.. as "poc-devops"
aws iam create-access-key --user-name ${AWS_IAM_USER_NAME}

aws iam create-policy --policy-name depopsec2-policy --policy-document file://aws-stuff/ec2-SecurityGroup.policy.json
aws iam attach-user-policy --user-name ${AWS_IAM_USER_NAME} --policy-arn arn:aws:iam::${AWS_ACCOUNT_ID}:policy/ec2-securitygroup-policy

#arn:aws:iam::aws:policy/AmazonVPCCrossAccountNetworkInterfaceOperations
```
### EC2 에 docker container 로 boot app run
```bash
export ACCOUNT_ID=$(aws sts get-caller-identity --output json | jq ".Account" | tr -d '"')
sudo yum install -y docker-20.10.23-1.amzn2.0.1
systemctl start docker
sudo dockerd &> dockerd-logfile &
sudo docker ps
# 관련 권한 있어야함 "ecr:GetDownloadUrlForLayer", "ecr:BatchGetImage", "ecr:GetAuthorizationToken", "ecr:DescribeRepositories"
aws ecr describe-repositories --region ap-northeast-2
aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com
sudo docker pull ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/day2:latest
sudo docker images 
sudo docker run -d -p 8008:8080 --name day2app ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/day2:latest
```
### 이전 설정
```bash
sudo yum install -y docker-20.10.23-1.amzn2.0.1
sudo dockerd &> dockerd-logfile &
aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com
sudo docker pull ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/day2:latest
sudo docker run -d -p 8008:8080 --name day2app ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/day2:latest
```

### 실행
```bash
export TAG_VERSION=1.0.26 && \
source ~/.tf/poc/loadInput2Env.sh && \
# export EXEC_CMD=plan && \
export EXEC_CMD=apply && \
export AUTO_APPROVE=-auto-approve && \
terraform -chdir=${TERRAFORM_DIR} ${EXEC_CMD} \
		  -var="aws_account_id=${ACCOUNT_ID}" \
		  -var="aws_db_name=${AWS_DB_NAME}" \
		  -var="aws_db_username=${AWS_DB_USERNAME}" \
		  -var="aws_db_password=${AWS_DB_PASSWORD}" \
		  -var="ecs_task_image_tag=${TAG_VERSION}" \
		  ${AUTO_APPROVE} && \
terraform -chdir=${TERRAFORM_DIR} output \
-json > ~/.tf/poc/output.json


# output 을 json 으로 export
source ~/.tf/poc/loadInput2Env.sh && \
terraform -chdir=${TERRAFORM_DIR} output \
-json > ~/.tf/poc/output.json
# subl ~/.tf/poc/output.json

# bastion 으로 db 접근
source ~/.tf/poc/loadInput2Env.sh && \
ssh -i ~/.ssh/web_admin ec2-user@${BASTION_IP}

ssh -i ~/.ssh/web_admin -L -H <local-port>:<aws-rds-endpoint>:<aws-rds-port> ec2-user@3.39.20.180


# EC2 에서 RDS 로 접근가능 확인
# -var="aws_account_id=$(cat "${POC_PROPERTY_DIR}/input.json" | jq -r .ACCOUNT_ID)" \
echo "curl -v telnet://"$(cat ~/.tf/poc/output.json | jq -r .cluster.value)":5432" | pbcopy

# sudo yum install postgresql
# psql: SCRAM authentication requires libpq version 10 or above 에러로 아래 추가 설치
# sudo yum install -y amazon-linux-extras
# sudo amazon-linux-extras install postgresql10
# sudo pip3 install --force-reinstall psycopg2==2.9.3

echo "curl -v telnet://"$(cat ~/.tf/poc/output.json | jq -r .cluster.value)":5432" | pbcopy

# psql 접속 command 생성
source ~/.tf/poc/loadInput2Env.sh && \
echo "psql --host=$(cat ~/.tf/poc/output.json | jq -r .cluster.value) --port=5432 --username=${AWS_DB_USERNAME} --password --dbname=${AWS_DB_NAME}" | \
pbcopy

#  < [SQL 파일명]   하면 실행됨
# 명령어s
# \list, \c orderdb, \dt

```

## 기타 
- default tag 에 project 이름 넣기?

## History
- 2023-05-12
  - cloud front 에 s3와 alb endpoint 추가
    - 
    - cache policy 를 통해 Auth header 를 추가해 줘야함..
- 2023-05-12
  - ecs task 에 taskRole 추가
    - devOrderApi Secret을 위한 SecretManager 관련 policy 추가 및 일단 AmazonECSFullAccess 추가
    - rds credential 쪽에도 taskRole 관련 arn 을 
  - ecs 에 app 배포 및 alb 연결 완료
    - targetGroup 에 healthCheck 이슈 해소
  - ecs task appName 및 ecs 전반적으로 cpu, memory 설정 변경

## Troubleshooting
- ALB 의 targetGroup healthCheck 실패
  - healthCheck port 를 8080으로 overwrite 하지 않음. -> 8080 으로 지정!
  - ecs fargate 의 스펙에 맞지않게, healthCheck의 interval 이 30sec 이었음 -> 120sec 으로 변경
  - securityGroup 에서 8080 allow
- ecs task 의 appName 변경시도시, The container does not exist in the task definition 이슈발생
  - 기본적으로 ecs task definition의 container definition 의 name 과 ecs_service 의 load_balancer 의 container_name 과 일치해야함.
  - 위는 rootCause 아니었고, terraform 설정에서 ecs_service resource 의 lifecycle 속성에 "ignore_changes = [task_definition]" 를 지정해놓음.
  - 결과적으로 task_definition 의 appName이 변경되더라도 ecs_service 가 업데이트 안되서 이슈 발생. 
  - 일단 해당부분 주석하고 재배포 성공
- Terraform 에서 cloudfront 의 cahce_behavior 에서 forward_value 와 cache_policy 동시사용 이슈
  - cache_policy_id 를 사용할땐, forwarded_values {} value를 제거한다.
  - "You have to disable the forwarded section if applying a custom cache_policy and origin_request_policy."
