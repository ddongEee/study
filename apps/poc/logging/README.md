# PoC:logging
***
## Goal

## Task
### CW 관련 SDK 사용 예제 구현
> why : 관련 PoC를 통해 CW SDK 에 대한 개념 및 구현이해. 이를 통해 customize 서비스 제공가능   
> how : 1. PoC 하려는 CW 기능 선정. (putMetric, )    
> what : ...


##### 참고
- [AWS SDK for Java 1.x CloudWatch code sample](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-cloudwatch.html)
- [Amazon CloudWatch code examples for the SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/cloudwatch)

## More
- log format 을 json 으로 하는 case 많은가?
- log 에 log format versionning?

## References
- MDC
  - [Slf4j-Mapped Diagnostic Context (MDC) support](https://www.slf4j.org/manual.html#mdc)
  - [Logback-Chapter 8: Mapped Diagnostic Context](https://logback.qos.ch/manual/mdc.html)
    - > "only log4j and logback offer MDC functionality"
  - [로깅 시스템 #4 - Correlation id & MDC](https://bcho.tistory.com/1316)
- [DevCustomAppender](https://docs.developers.optimizely.com/full-stack-experimentation/docs/customize-logger-java)
- [Creating a Custom Logback Appender](https://www.baeldung.com/custom-logback-appender)
- [Log. Color-coded Output](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging.console-output.color-coded)
  - [Logback 출력 로그 색상 변경하기 (with clr)](https://oingdaddy.tistory.com/257)
  - >blue, cyan, faint, green, magenta, red, yellow
- [spring-cloud-sleuth](https://spring.io/projects/spring-cloud-sleuth)
