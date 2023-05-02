# Product : Day2
## 작업
```bash
export ACCOUNT_ID=$(aws sts get-caller-identity --output json | jq ".Account" | tr -d '"')
docker build -t day2 .
docker run -d -p 8008:8080 --name day2app day2:latest
## create repository
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com
docker build -t day2 .
docker tag day2:latest ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/day2:latest
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/day2:latest


```


## References
