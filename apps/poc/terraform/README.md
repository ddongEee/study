# Terraform
## 정리
### 선행조건
```bash
export AWS_PROFILE=lab-admin
export AWS_ACCOUNT_ID=xxx
export AWS_IAM_USER_NAME=devops
# aws 에 해당 작업만을 위한 role 생성. 초안..
aws iam create-user --user-name ${AWS_IAM_USER_NAME}
aws iam list-users
# policy 설정
# TODO : 좀더 구조적으로 변경하기(ex.group 사용), policyName 도 변경
# ec2:keyPair 의 경우, 수정이나, 생성이후 삭제 없으면, "ec2:ImportKeyPair", "ec2:DescribeKeyPairs" 만 있으면 되나, 
# terraform destroy 을 위해, "ec2:DeleteKeyPair" 까지 추가..
aws iam create-policy --policy-name ec2-keypair-policy --policy-document file://aws-stuff/ec2-KeyPair.policy.json
aws iam attach-user-policy --user-name ${AWS_IAM_USER_NAME} --policy-arn arn:aws:iam::${AWS_ACCOUNT_ID}:policy/ec2-keypair-policy
# securityGroup policy 생성
aws iam create-policy --policy-name ec2-securitygroup-policy --policy-document file://aws-stuff/ec2-SecurityGroup.policy.json
aws iam attach-user-policy --user-name ${AWS_IAM_USER_NAME} --policy-arn arn:aws:iam::${AWS_ACCOUNT_ID}:policy/ec2-securitygroup-policy
# instance policy 생성
# reading EC2 Instance launch template 관련 error 는 tag 관련 policy 또한 필요햇엇음. https://docs.aws.amazon.com/autoscaling/ec2/userguide/ts-as-launch-template.html#ts-launch-template-unauthorized-error
# reading EC2 Instance (***): reading block devices: UnauthorizedOperation 관련, ec2:DescribeVolumes policy 추가
aws iam create-policy --policy-name ec2-instance-policy --policy-document file://aws-stuff/ec2-Instance.policy.json
aws iam attach-user-policy --user-name ${AWS_IAM_USER_NAME} --policy-arn arn:aws:iam::${AWS_ACCOUNT_ID}:policy/ec2-instance-policy
# RDS - db instance policy 생성
# [Issue.1] Verify that you have permission to create service linked role. Otherwise wait and try again later
# [Solution.1] iam:CreateServiceLinkedRole >> IAM 관련 policy
aws iam create-policy --policy-name rds-dbinstance-policy --policy-document file://aws-stuff/rds-dbInstance.policy.json
aws iam attach-user-policy --user-name ${AWS_IAM_USER_NAME} --policy-arn arn:aws:iam::${AWS_ACCOUNT_ID}:policy/rds-dbinstance-policy
# IAM policy 생성
aws iam create-policy --policy-name iam-policy --policy-document file://aws-stuff/iam.policy.json
aws iam attach-user-policy --user-name ${AWS_IAM_USER_NAME} --policy-arn arn:aws:iam::${AWS_ACCOUNT_ID}:policy/iam-policy

#aws iam attach-role-policy --role-name {{role_name}} --policy-arn arn:aws:iam::xxx:policy/ec2-keypair-policy
aws iam list-attached-user-policies --user-name ${AWS_IAM_USER_NAME} | jq .

# access key 생성
aws iam create-access-key --user-name ${AWS_IAM_USER_NAME}

# Local 에 profile 생성
aws configure --profile lab-devops
# AWS Access Key ID [None]: *****
# AWS Secret Access Key [None]: *****
# Default region name [None]: ap-northeast-2
# Default output format [None]: json
# 추후 위의 profile 명을 provider 블록안에 넣어준다.
```
### 개념
- IaC 로써 인프라스트럭쳐 관리 도구
- 프로비저닝 도구
- 선언적 코드로 작성해 관리

#### 용어
 - 프로비저닝
 - 프로바이더
 - 리소스
 - Plan
 - Apply
 - 
### 설치
```bash
#brew install terraform
#terraform version
brew update
brew install tfenv
tfenv list-remote
tfenv install 1.4.4
tfenv list
tfenv use 1.4.4
#echo 1.4.4 > .terraform-version
```
### 개발
```bash
# provider.tf, variables.tf 생성후
terraform init
# key-pair 생성
ssh-keygen -t rsa -b 4096 -C "twoday.me@gmail.com" -f "$HOME/.ssh/web_admin" -N ""

terraform plan
terraform apply

terraform console
> aws_instance.web.public_ip
> aws_db_instance.web_db.enpoint
```

## 리소스 제거..
- aws iam : admin, devops
  - command : ?

## Reference
- 적용필요
  - [Implement these best practices to run Terraform with AWS](https://www.techtarget.com/searchaws/tip/Implement-these-best-practices-to-run-Terraform-with-AWS)
  - [AWS Provider credential Authentication](https://iamjjanga.tistory.com/20)
- 기초
  - [테라폼(Terraform) 기초 튜토리얼](https://www.44bits.io/ko/post/terraform_introduction_infrastrucute_as_code)
- [Youtube-Terraform 소개](https://www.youtube.com/watch?v=R6XxYKqB8EY)
  - [관련사이트](https://great-stone.github.io/hashicorp/terraform/TerraformIntroduction/)
- [Youtube-Terraform 기본설명!](https://www.youtube.com/watch?v=3qSpwqckvXQ)
- [테라폼(Terraform) 기초 튜토리얼](https://www.44bits.io/ko/post/terraform_introduction_infrastrucute_as_code)
- [Hashicorp official - Terraform](https://developer.hashicorp.com/terraform/intro)
- 실습위주
  - [Terraform IaC 도구를 활용한 AWS 웹콘솔 클릭 노가다 해방기](https://saramin.github.io/2022-10-21-terraform/)
  - [테라폼으로 AWS EC2 인스턴스 생성,삭제하기](https://newdeal123.tistory.com/75)
