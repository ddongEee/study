# ALB

## Host header, Path, Http header, Http request method, Query string, Source IP 등으로 rule 생성 가능
## AWS ALB Listener rule (then)
### forward : tg로 라우팅
### redirect: HTTP status code로 301 or 302 반환. HTTPS로 요청을 리다이렉트 시킬때 가장많이 사용
### returnFixedResponse : 특정 status code 리턴
### Authenticate : AWS Cognito or OIDC 이용한 인증 거치도록함. OIDC 는 OAuth2.0

## Internet facing 용 alb 는 public subnet 에 Load Balancer Node 를 생성하기 때문에 public subnet 필요
resource "aws_alb" "frontend" {
  name               = "alb-example"
  subnets            = aws_subnet.public_subnets.*.id
  load_balancer_type = "application"
  security_groups    = [aws_security_group.sg.id]
  internal           = false

  access_logs {
    bucket  = aws_s3_bucket.log_storage.id
    prefix  = "frontend-alb"
    enabled = true
  }

  lifecycle {
    # ALB 재생성시, 새로운 ALB 생성후 기존 ALB 제거 -> downtime 제거
    create_before_destroy = true
  }

  tags = {
    Name = "ALB"
  }
}

# certificate_arn 속성을 설정하여 SSH 적용가능
resource "aws_alb_listener" "test" {
  load_balancer_arn = aws_alb.frontend.arn
  port              = 80
  protocol          = "HTTP"

  # targetGroup 에 request 를 forward 만 할수 있음
  default_action {
    type = "forward"
    target_group_arn = aws_alb_target_group.frontend.arn
  }
}

resource "aws_alb_target_group" "frontend" {
  name     = "frontend-target-group"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id
  health_check {
    interval = 30
    path = "/"
    healthy_threshold = 3
    unhealthy_threshold = 3
  }

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name = "Frontend target group"
  }
}

# Provides the ability to register instances and containers with an Application Load Balancer (ALB) or
# Network Load Balancer (NLB) target group. For attaching resources with Elastic Load Balancer (ELB)
# 생성한 target group 에 instance 를 등록 register 함
resource "aws_alb_target_group_attachment" "frontend" {
  count            = length(aws_instance.web)
  target_group_arn = aws_alb_target_group.frontend.arn
  target_id        = element(aws_instance.web.*.id, count.index)
  port             = 80
}

### ecs 관련
resource "aws_lb" "to_ecs" {
  name               = "my-personal-web-lb-tf"
  internal           = false
  load_balancer_type = "application"
  security_groups = [aws_security_group.sg.id,]
  subnets         = aws_subnet.public_subnets.*.id
  tags = {
    env = "dev"
  }
}

resource "aws_lb_listener" "to_ecs" {
  load_balancer_arn = aws_lb.to_ecs.arn
  protocol          = "HTTP"
  port              = "80"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ecs.arn
  }
}

resource "aws_lb_target_group" "ecs" {
  name        = "tf-my-personal-web-lb-tg"
  port        = 8080
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.main.id

  health_check {
    interval = 120 # todo : 적당하게 줄이거나 늘릴필요 있음
    path = "/hello"
    port = 8080 # 따로 지정 필요햇음..
    healthy_threshold = 3
    unhealthy_threshold = 6
  }
}
