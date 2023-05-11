# Role
resource "aws_iam_role" "ecs_task_execution_role" {
  name               = "ecs-staging-execution-role-${var.environment}"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_execution_role.json
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

# https://github.com/telia-oss/terraform-aws-ecs-fargate/blob/master/policies.tf
# Task role assume policy
#data "aws_iam_policy_document" "task_assume" {
#  statement {
#    effect  = "Allow"
#    actions = ["sts:AssumeRole"]
#
#    principals {
#      type        = "Service"
#      identifiers = ["ecs-tasks.amazonaws.com"]
#    }
#  }
#}


# ------------------------------------------------------------------------------
# IAM - Task role, basic. Users of the module will append policies to this role
# when they use the module. S3, Dynamo permissions etc etc.
# ------------------------------------------------------------------------------
#resource "aws_iam_role" "task" {
#  name                 = "${var.name_prefix}${var.aws_iam_role_task_suffix}"
#  assume_role_policy   = data.aws_iam_policy_document.task_assume.json
#  permissions_boundary = var.task_role_permissions_boundary_arn
#}

resource "aws_iam_role_policy" "ssm_agent" {
  name   = "local-ssm-permissions"
  role   = aws_iam_role.ecs_task_execution_role.id
  policy = data.aws_iam_policy_document.ssm_task_permissions.json
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

# Role - Policy attachment
resource "aws_iam_role_policy_attachment" "ecs_task_execution_role" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource aws_iam_role_policy_attachment secret_access {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = aws_iam_policy.secrets_access.arn
}

