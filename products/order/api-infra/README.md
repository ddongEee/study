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
# -var="aws_account_id=$(cat "${POC_PROPERTY_DIR}/input.json" | jq -r .ACCOUNT_ID)" \
export TAG_VERSION=1.0.18 && \
source ~/.tf/poc/loadInput2Env.sh && \
 export EXEC_CMD=plan && \
#export EXEC_CMD=apply && \
#export AUTO_APPROVE=-auto-approve && \
terraform -chdir=${TERRAFORM_DIR} ${EXEC_CMD} \
		  -var="aws_account_id=${ACCOUNT_ID}" \
		  -var="aws_db_name=${AWS_DB_NAME}" \
		  -var="aws_db_username=${AWS_DB_USERNAME}" \
		  -var="aws_db_password=${AWS_DB_PASSWORD}" \
		  -var="ecs_task_image_tag=${TAG_VERSION}" \
#		  ${AUTO_APPROVE}


source ~/.tf/poc/loadInput2Env.sh && \
terraform -chdir=${TERRAFORM_DIR} output \
-json > ~/.tf/poc/output.json


source ~/.tf/poc/loadInput2Env.sh && \
ssh -i ~/.ssh/web_admin ec2-user@${BASTION_IP}

ssh -i ~/.ssh/web_admin -L -H <local-port>:<aws-rds-endpoint>:<aws-rds-port> ec2-user@3.39.20.180

mysql --user=kmhakpostgres --password=kmhakpostgres! --host=aurora-cluster-demo-v2-instance-0.cjr6vr4c518w.ap-northeast-2.rds.amazonaws.com --port=5432
mysql --user=user1  --host=127.16.38.1 --port=25060 -p

# EC2 에서 RDS 로 접근가능 확인
# -var="aws_account_id=$(cat "${POC_PROPERTY_DIR}/input.json" | jq -r .ACCOUNT_ID)" \
echo "curl -v telnet://"$(cat ~/.tf/poc/output.json | jq -r .cluster.value)":5432"
curl -v telnet://aurora-cluster-demo-v2.cluster-cjr6vr4c518w.ap-northeast-2.rds.amazonaws.com:5432

curl -v telnet://aurora-cluster-demo-v2-instance-0.cjr6vr4c518w.ap-northeast-2.rds.amazonaws.com:3306

# sudo yum install postgresql
# psql: SCRAM authentication requires libpq version 10 or above 에러로 아래 추가 설치
# sudo yum install -y amazon-linux-extras
# sudo amazon-linux-extras install postgresql10
# sudo pip3 install --force-reinstall psycopg2==2.9.3

psql \
   --host=aurora-cluster-demo-v2-instance-0.cjr6vr4c518w.ap-northeast-2.rds.amazonaws.com \
   --port=5432 \
   --username=kmhakpostgres \
   --password \
   --dbname=orderdb
#  < [SQL 파일명]   하면 실행됨
# 명령어s
# \list, \c orderdb, \dt      
kmhakpostgres!
```

## 기타 
- default tag 에 project 이름 넣기? 
