# ecs
resource "aws_ecs_cluster" "cluster" {
  name = "cluster"
  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}

data "template_file" "service" {
  template = file("templates/service.config.json.tpl")

  vars = {
    region             = var.aws_region
    aws_ecr_repository = aws_ecr_repository.repo.repository_url
    tag                = var.ecs_task_image_tag
    container_port     = var.container_port
    host_port          = var.host_port
    app_name           = var.app_name
    env_suffix         = var.environment
    rds_credentials = aws_secretsmanager_secret.rds_credentials.arn
  }
}

resource "aws_ecs_task_definition" "task" {
  family                   = "service"
  network_mode             = "awsvpc"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = ""
  cpu                      = 256
  memory                   = 512
  requires_compatibilities = ["FARGATE", "EC2"] # todo : ec2 지워보기
  container_definitions    = data.template_file.service.rendered

  tags = {
    Application = var.app_name
  }
}

resource "aws_ecs_service" "service" {
  name             = "service"
  cluster          = aws_ecs_cluster.cluster.id
  task_definition  = aws_ecs_task_definition.task.id
  desired_count    = 1 # todo : 아래껄로 변경해보기
#  desired_count    = length(var.aws_public_subnet_cidrs)
  force_new_deployment = true # todo : 무엇?
  launch_type      = "FARGATE"
  platform_version = "LATEST"

  network_configuration {
    security_groups  = [aws_security_group.ecs_tasks.id, aws_security_group.db_key.id]
    subnets          = aws_subnet.public_subnets.*.id # todo : private 으로 바꿔보기
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.ecs.arn # todo : 변경
    container_name   = var.app_name # todo : 변경
    container_port   = var.container_port # todo : 변경
  }

  depends_on = [
#    aws_lb_listener.https_forward,
#    aws_lb_listener.http_forward,
    aws_lb_listener.to_ecs,
    aws_iam_role_policy_attachment.ecs_task_execution_role
  ]

  lifecycle {
    ignore_changes = [task_definition] # todo : 무엇?
  }

  tags = {
    Application = var.app_name
  }
}
