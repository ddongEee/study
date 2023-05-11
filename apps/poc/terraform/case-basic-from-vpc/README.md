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

## 기타 
- default tag 에 project 이름 넣기? 
