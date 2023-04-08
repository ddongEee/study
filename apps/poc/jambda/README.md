# Jambda
> > About Blog posting : https://ddongeee.github.io/2022/01/03/jambda/
# 준비
1. aws cli 설치 및 개인계정 configuration 구성
    - https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html
    - 터미널에서 "aws sts get-caller-identity" 명령어에 대한 json 값이 잘 return 된다면 완료
2. jq 설치
    - https://stedolan.github.io/jq/

# 설정
- git clone를 한 directory 로 이동 하여 아래 명령어로 람다 실행.
- 최초에 한번 아래 설정을 하면됨.
```bash
# 1. jambda 계정 생성
aws iam create-role --role-name hello-jambda-role \
--assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'
# 2. 기본 람다 실행 rolePolicy 추가
aws iam attach-role-policy --role-name hello-jambda-role \
--policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
# 3. ACCOUNT_ID 초기화
ACCOUNT_ID=$(aws whoami | jq '.Account | tonumber')
# 4. empty.zip 경로 초기화
EMPTY_FILE_PATH=$(pwd)'/empty.zip'
# 5. file 없이 람다 생성
aws lambda create-function --function-name hello-jambda \
--zip-file fileb://${EMPTY_FILE_PATH} \
--handler io.github.ddongeee.jambda.Handler::handleRequest \
--runtime java11 \
--role arn:aws:iam::${ACCOUNT_ID}:role/hello-jambda-role
```

# 실행
```bash
# lambda 호출 및 context 확인 
aws lambda invoke --function-name hello-jambda out --log-type Tail --query 'LogResult' --output text | base64 -d
# lambda 호출 및 response 확인 
aws lambda invoke --function-name hello-jambda out | jq >/dev/null && jq . out
```

# 개발후 배포
```bash
# 해당 source 가 위치한 root directory 에서, 아래 실행
sh save_ur_time.sh
```
