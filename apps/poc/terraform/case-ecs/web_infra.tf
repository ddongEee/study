locals {
  subnet_domain = "ecs.${var.registered_domain}"
}

## route53
data "aws_route53_zone" "front" {
  name         = var.registered_domain
  private_zone = false
}

resource "aws_route53_record" "front" {
  zone_id = data.aws_route53_zone.front.zone_id
  name    = local.subnet_domain
  type    = "A"

  alias {
    name                   = aws_lb.staging.dns_name
    zone_id                = aws_lb.staging.zone_id
    evaluate_target_health = true
  }
}

resource "aws_route53_record" "cert_validation" {
  name    = tolist(aws_acm_certificate.cert.domain_validation_options)[0].resource_record_name
  type    = tolist(aws_acm_certificate.cert.domain_validation_options)[0].resource_record_type
  zone_id = data.aws_route53_zone.front.zone_id
  records = [tolist(aws_acm_certificate.cert.domain_validation_options)[0].resource_record_value]
  ttl     = 60
}

## acm
resource "aws_acm_certificate" "cert" {
  provider = aws.virginia
  domain_name       = var.registered_domain
  validation_method = "DNS"
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "cert" {
  provider = aws.virginia
  certificate_arn         = aws_acm_certificate.cert.arn
  validation_record_fqdns = [aws_route53_record.cert_validation.fqdn]
}

## auto-scaling
resource "aws_appautoscaling_target" "ecs_target" {
  max_capacity       = 3
  min_capacity       = 1
  resource_id        = "service/${aws_ecs_cluster.staging.name}/${aws_ecs_service.staging.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

resource "aws_appautoscaling_policy" "ecs_policy_memory" {
  name               = "memory-autoscaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs_target.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }

    target_value = 80
  }
}

resource "aws_appautoscaling_policy" "ecs_policy_cpu" {
  name               = "cpu-autoscaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs_target.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }

    target_value = 60
  }
}

## ecr
resource "aws_ecr_repository" "repo" {
  name = "day2repo"
}


resource "aws_ecr_lifecycle_policy" "repo-policy" {
  repository = aws_ecr_repository.repo.name

  policy = <<EOF
{
  "rules": [
    {
      "rulePriority": 1,
      "description": "Keep image deployed with tag latest",
      "selection": {
        "tagStatus": "tagged",
        "tagPrefixList": ["latest"],
        "countType": "imageCountMoreThan",
        "countNumber": 1
      },
      "action": {
        "type": "expire"
      }
    },
    {
      "rulePriority": 2,
      "description": "Keep last 2 any images",
      "selection": {
        "tagStatus": "any",
        "countType": "imageCountMoreThan",
        "countNumber": 2
      },
      "action": {
        "type": "expire"
      }
    }
  ]
}
EOF
}

## ecs
data "aws_iam_policy_document" "ecs_task_execution_role" {
  version = "2012-10-17"
  statement {
    sid     = ""
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name               = "ecs-staging-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_execution_role.json
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}


data "template_file" "service" {
  template = file(var.tpl_path)
  vars = {
    region             = var.aws_region
    aws_ecr_repository = aws_ecr_repository.repo.repository_url
    tag                = "latest"
    container_port     = var.container_port
    host_port          = var.host_port
    app_name           = var.app_name
  }
}

resource "aws_ecs_task_definition" "service" {
  family                   = "service-staging"
  network_mode             = "awsvpc"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  cpu                      = 256
  memory                   = 512
  requires_compatibilities = ["FARGATE"]
  container_definitions    = data.template_file.service.rendered
  tags = {
    Environment = "staging"
    Application = var.app_name
  }
}

resource "aws_ecs_cluster" "staging" {
  name = "service-ecs-cluster"
}

resource "aws_ecs_service" "staging" {
  name            = "staging"
  cluster         = aws_ecs_cluster.staging.id
  task_definition = aws_ecs_task_definition.service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    security_groups  = [aws_security_group.ecs_tasks.id]
    subnets          = aws_subnet.cluster[*].id
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.staging.arn
    container_name   = var.app_name
    container_port   = var.container_port
  }

  depends_on = [aws_lb_listener.https_forward, aws_lb_listener.http_forward, aws_iam_role_policy_attachment.ecs_task_execution_role]

  tags = {
    Environment = "staging"
    Application = var.app_name
  }
}

## lb
resource "aws_lb" "staging" {
  name               = "alb"
  subnets            = aws_subnet.cluster[*].id
  load_balancer_type = "application"
  security_groups    = [aws_security_group.lb.id]

  access_logs {
    bucket  = aws_s3_bucket.log_storage.id
    prefix  = "frontend-alb"
    enabled = true
  }

  tags = {
    Environment = "staging"
    Application = var.app_name
  }
}

resource "aws_lb_listener" "https_forward" {
  load_balancer_arn = aws_lb.staging.arn
  port              = 443
  protocol          = "HTTPS"
  certificate_arn   = aws_acm_certificate.cert.arn
  ssl_policy        = "ELBSecurityPolicy-2016-08"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.staging.arn
  }
}


resource "aws_lb_listener" "http_forward" {
  load_balancer_arn = aws_lb.staging.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_target_group" "staging" {
  name        = "service-alb-tg"
  port        = 80
  protocol    = "HTTP"
  vpc_id      = aws_vpc.cluster_vpc.id
  target_type = "ip"

  health_check {
    enabled             = true
    interval            = 300
    path                = "/"
    timeout             = 60
    matcher             = "200"
    healthy_threshold   = 5
    unhealthy_threshold = 5
  }

  lifecycle {
    create_before_destroy = true
  }
}

## logs
resource "aws_s3_bucket" "log_storage" {
  bucket = var.aws_bucket_name
  force_destroy = true
}


resource "aws_cloudwatch_log_group" "service" {
  name = "awslogs-service-staging"

  tags = {
    Environment = "staging"
    Application = var.app_name
  }
}

## network
resource "aws_vpc" "cluster_vpc" {
  tags = {
    Name = "ecs-vpc"
  }
  cidr_block = var.aws_vpc_cidr
}


resource "aws_subnet" "cluster" {
  vpc_id            = aws_vpc.cluster_vpc.id
  count             = length(var.aws_availability_zones)
  cidr_block        = element(var.aws_public_subnet_cidrs, count.index)
  availability_zone = element(var.aws_availability_zones, count.index)
  tags = {
    Name = "ecs-subnet"
  }
}


resource "aws_internet_gateway" "cluster_igw" {
  vpc_id = aws_vpc.cluster_vpc.id

  tags = {
    Name = "ecs-igw"
  }
}

resource "aws_route_table" "public_route" {
  vpc_id = aws_vpc.cluster_vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.cluster_igw.id
  }

  tags = {
    Name = "ecs-route-table"
  }
}

resource "aws_route_table_association" "to-public" {
  count          = length(aws_subnet.cluster)
  subnet_id      = aws_subnet.cluster[count.index].id
  route_table_id = aws_route_table.public_route.id
}

## sg
resource "aws_security_group" "lb" {
  vpc_id = aws_vpc.cluster_vpc.id
  name   = "lb-sg"
  ingress {
    protocol    = "tcp"
    from_port   = 80
    to_port     = 80
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol    = "tcp"
    from_port   = 443
    to_port     = 443
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs_tasks" {
  vpc_id = aws_vpc.cluster_vpc.id
  name   = "ecs-tasks-sg"

  ingress {
    protocol        = "tcp"
    from_port       = var.host_port
    to_port         = var.container_port
    cidr_blocks     = ["0.0.0.0/0"]
    security_groups = [aws_security_group.lb.id]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}
