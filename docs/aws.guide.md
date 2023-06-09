# AWS

## 서비스 
### Subnet
- public subnet : 인터넷 연결이 가능함으로, 공인 ip 를 갖을수 있다.
- private subnet : 다른 vpc 영역에서 접근이 불가능하다. 동일 vpc 대역이라면 접근가능
  - nat gw 를 타고 EIP로 변환후 igw통해 인터넷으로 나감
- 정리
  - ELB 만 public 에 놓고, 실제 was 와 db는 private subnet 에 위치시키는것이 좋은선택
### Amazon CloudWatch
- 모니터링 및 관찰 시스템, AWS 서비스 혹은 다른 애플리케이션 모니터링
- Public 서비스 (인터넷 혹은 Interface Endpoint 사용)
- 로그, 지표, 이벤트 수집 및 시각화
- 기능
  - metric 수집
    - cloudWatchAgent를 통해 EC2 메모리 정보 커스텀 수집가능
  - alert
    - 기준값으로 알림생성 가능. 여러 채널 가능
  - 로그수집
    - 수집및 다른 서비스/계정으로 전달가능
    - 자체쿼리가능
  - 대시보드
- 심화
  - metric
    - 시간순. dataPoint로 구성
    - AWS 서비스는 대부분 지원
    - 커스텀 데이포인트를 생성가능
    - 리전단위
    - 15개월 이후 사라짐
    - 구성
      - namespsce : cloudwatch 지표의 컨테이너, 논리적 묶음단위, AWS/EC2, 필수
      - DataPoints : 시간-값, 초단위까지. UTC권장
        - Resolution : 얼마나자주수집? 기본60s, HighResolution(HR):1s, (1,5,10,30,60..)
        - Period : 얼마 기준으로 묶여서 보여지는지 개념, (1,5,10,30,60...1일), 60밑은 HR설정필요
          - 보관 : 60s 3시간, 60s:15시간, 300s:63일, 1시간:15개월
          - 큰단위로 계속 merge됨
          - (주의) 2주이상 업데이트 없으면 console에서 안보이고, CLI로만 확인가능
        - Dimension : 태그/카테고리. metric 구분시 사용, 키벨류구성, 최대30개, 조합가능
        - 기타 : Unit 단위
  - Alarm(경보)
    - threshold 기준 알림생성
    - 3가지 상태 (OK, ALARM, INSUFFICIENT_DATA)
    - 대응가능 : sns+lambda, 이메일,, 500에러 일정수치 이상일때
    - Resolution 에 따라 평가 주기 변동. HR 아니면 60초 이상기준
- 사용
  - AWS system manager command 를 통해 cloudWatchAgent 설치 및 실행
    - agent 관련 config는 parameterStore 활용
  - 로그그룹 검색 -> 로그스트림 : log format 이 json이라서 filter 가 쉬움..
    - 작업>지표필터> 패턴필터링 > { $.status="404"}
  - 모든지표. (Custom,AWS service namespace 중에) CWAgent
    - 로그스트림 name 혹은 여러 dimension 조합으로 검색
  - 모든경보 -> 지표선택 -> 합계,1분,보다크거나같음 -> SNS 설정
    - email confirm 필요
### AWS route53

### Application Load Balancer
- 기능
  - 트래픽 분산, health check, https 지원 
- 구성
  - listener : 구성 protocol 과 port 로  
#### 다른곳에서 구입한 도메인을 사용하기 위해선
- 사용하려고 하는 aws 계정의 route53에 "Hosting" 영역 생성하고
  - 생성된 ns을, 이전에 구입한 곳의 도메인에 연결된 ns 에 overwrite 해준다. 
```bash
생성후 확인
nslookup -type=ns woocra.com
```
        
### AWS CLI
```bash
# iam 에서 role 생성 및 access key 생성후 
# aws configure --profile {{accountName-role}}
aws configure --profile lab-admin
# AWS Access Key ID [None]: ****
# AWS Secret Access Key [None]: ****
# Default region name [None]: ap-northeast-2
# Default output format [None]: json
```

### AWS Session manager on local
- 참고 : https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html
  - https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html#install-plugin-macos-signed
```bash
aws ssm start-session --target {{instanceId}}
$ cd ~
```

#### Amazon ECS Exec Checker
```bash
bash <( curl -Ls https://raw.githubusercontent.com/aws-containers/amazon-ecs-exec-checker/main/check-ecs-exec.sh ) <YOUR_ECS_CLUSTER_NAME> <YOUR_ECS_TASK_ID>
```
- 참고 : https://github.com/aws-containers/amazon-ecs-exec-checker

### AWS IAM 
- 참고 : https://jonnung.dev/posts/2021-01-28-aws-iam-role/#gsc.tab=0
- 어떻게 role, policy, attach 되는가?
- sts:AssumeRole
  - sts : security token service. (보안토큰생성). iam 사용자나 외부 자격증명을 사용하여 액세스 권한 부여 가능
  - AssumeRole, AWS IAM 지원기능, IAM 혹은 외부자격증명으로 다른 계정 혹은 리소스 접근 가능
  - 순서 : 1. sts 로 일시 자격증명 생성, 2. 해당 자격증명으로 assumeRole 호출, 3. 해당 리소스에 대한 액세스 권한 부여받음
  - aws iam role 에 trust relationship 에 특정 서비스 혹은 accountId 등을 지정하여, 해당 entity 만 role 을 assume 할수 있도록 한다.  

## 아키택쳐
### SSH Tunneling
 -


## Reference
- [AWS Documentation](https://docs.aws.amazon.com/)
- ["Github" aws-lambda-developer-guide ](https://github.com/awsdocs/aws-lambda-developer-guide)
- 강의
    - [AWS 강의실](https://www.youtube.com/@AWSClassroom)
