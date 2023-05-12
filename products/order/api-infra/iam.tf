# Role
resource "aws_iam_role" "ecs_task_execution_role" {
  name               = "ecs-staging-execution-role-${var.environment}"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_execution_role.json # Trust relationships 에 추가됨. 해당 롤을 수임할수 있는(맡을수 있는) 조건 정책?
}

resource "aws_iam_role" "ecs_task_role" {
  name               = "ecs-task-role-${var.environment}"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_execution_role.json
}

# Policy
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

resource "aws_iam_role_policy" "ssm_agent" {
  name   = "local-ssm-permissions"
  role   = aws_iam_role.ecs_task_execution_role.id
  policy = data.aws_iam_policy_document.ssm_task_permissions.json
}

resource "aws_iam_role_policy" "access_dev_order_api_secretsmanager" {
  name   = "access_dev_order_api_secretsmanager"
  role   = aws_iam_role.ecs_task_role.id
  policy = data.aws_iam_policy_document.access_order_api_secretsmanager.json
}

# Task logging privileges & ssm
data "aws_iam_policy_document" "ssm_task_permissions" {
  statement {
    effect    = "Allow"
    resources = ["*"]
    actions = [
      "ssmmessages:CreateControlChannel",
      "ssmmessages:CreateDataChannel",
      "ssmmessages:OpenControlChannel",
      "ssmmessages:OpenDataChannel"
    ]
  }
}

data "aws_secretsmanager_secret" "dev_order_api" {
  name = "/dev/order-api"
}

data "aws_iam_policy_document" "access_order_api_secretsmanager" {
  statement {
    effect = "Allow"
    actions = [
      "secretsmanager:GetResourcePolicy",
      "secretsmanager:GetSecretValue",
      "secretsmanager:DescribeSecret",
      "secretsmanager:ListSecretVersionIds"
    ]
    resources = [data.aws_secretsmanager_secret.dev_order_api.arn]
  }
}

# Role - Policy attachment, attach to policy as entity
resource "aws_iam_role_policy_attachment" "ecs_task_execution_role" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_task_role" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonECS_FullAccess" # todo : 권한축소하기
}

resource aws_iam_role_policy_attachment secret_access {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = aws_iam_policy.secrets_access.arn
}

resource aws_iam_role_policy_attachment secret_access4task {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = aws_iam_policy.secrets_access.arn
}

# ------------ About RDS (Credential)
data "aws_iam_policy_document" "my_password_policy" {
  statement {
    effect = "Allow"
    principals {
      identifiers = [aws_iam_role.ecs_task_execution_role.arn, aws_iam_role.ecs_task_role.arn]
      type        = "AWS"
    }
    actions = [
      "secretsmanager:GetSecret",
      "secretsmanager:GetSecretValue"
    ]
    resources = ["*"]
  }
}

resource "aws_iam_policy" "secrets_access" { # todo : rename : access_rds_secretsmanager
  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetResourcePolicy",
        "secretsmanager:GetSecretValue",
        "secretsmanager:DescribeSecret",
        "secretsmanager:ListSecretVersionIds"
      ],
      "Resource": [
        "${aws_secretsmanager_secret.rds_credentials.arn}"
      ]
    }
  ]
}
POLICY
}


# ------------ Policy About S3 bucket
data "aws_iam_policy_document" "allow-lb" { # TODO : check
  statement {
    principals {
      type        = "Service"
      identifiers = ["logdelivery.elb.amazonaws.com"]
    }

    actions = ["s3:PutObject"]

    resources = [
      "arn:aws:s3:::${aws_s3_bucket.log_storage.bucket}/frontend-alb/AWSLogs/${var.aws_account_id}/*"
    ]

    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"

      values = [
        "bucket-owner-full-control"
      ]
    }
  }
  statement {
    principals {
      type        = "Service"
      identifiers = ["logdelivery.elasticloadbalancing.amazonaws.com"]
    }

    actions = ["s3:PutObject"]

    resources = [
      "arn:aws:s3:::${aws_s3_bucket.log_storage.bucket}/frontend-alb/AWSLogs/${var.aws_account_id}/*"
    ]

    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"

      values = [
        "bucket-owner-full-control"
      ]
    }
  }
  statement {
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.aws_elb_account_id_seoul}:root"]
    }

    actions = ["s3:PutObject"]

    resources = [
      "arn:aws:s3:::${aws_s3_bucket.log_storage.bucket}/frontend-alb/AWSLogs/${var.aws_account_id}/*"
    ]

    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"

      values = [
        "bucket-owner-full-control"
      ]
    }
  }
}
