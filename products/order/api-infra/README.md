# Terraform - Case. Basic from vpc
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

## 기타 
- default tag 에 project 이름 넣기? 
