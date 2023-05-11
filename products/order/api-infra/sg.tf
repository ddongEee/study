data "aws_security_group" "default" {
  name = "default"
  vpc_id = aws_vpc.main.id
}

resource "aws_security_group" "allow_8080" {
  name = "allow 8080"
  description = "[For WAS] Allow SSH port from all"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all outbound traffic.
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ec2" {
  name = "allow 22, 80 for ec2"
  description = "Allow SSH port from all"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all outbound traffic.
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "[${var.aws_vpc_name}]-allow-22-80"
  }
}


locals {
  ingress_rules = [
    {
      name        = "HTTPS"
      port        = 443
      description = "Ingress rules for port 443"
    },
    {
      name        = "HTTP"
      port        = 80
      description = "Ingress rules for port 80"
    },
    {
      name        = "SSH"
      port        = 22
      description = "Ingress rules for port 22"
    }
  ]
}

resource "aws_security_group" "sg" {
  name        = "CustomSG"
  description = "Allow TLS inbound traffic"
  vpc_id      = aws_vpc.main.id
  egress = [
    {
      description      = "for all outgoing traffics"
      from_port        = 0
      to_port          = 0
      protocol         = "-1"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = ["::/0"]
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    }
  ]

  dynamic "ingress" {
    for_each = local.ingress_rules

    content {
      description = ingress.value.description
      from_port   = ingress.value.port
      to_port     = ingress.value.port
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }
  }
  tags = {
    Name = "[${var.aws_vpc_name}] AWS security group dynamic block"
  }
}

# lb 의 경우 80, 443 포트 만 허용
resource "aws_security_group" "lb" {
  vpc_id = aws_vpc.main.id
  name = "lb-sg-${var.environment}"

  ingress {
    from_port         = var.host_port
    protocol          = "tcp"
    to_port           = var.host_port
    cidr_blocks       = ["0.0.0.0/0"]
    ipv6_cidr_blocks  = ["::/0"]
  }

  ingress {
    from_port         = 443
    protocol          = "tcp"
    to_port           = 443
    cidr_blocks       = ["0.0.0.0/0"]
    ipv6_cidr_blocks  = ["::/0"]
  }

  egress {
    from_port = 0
    protocol  = "-1"
    to_port   = 0
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
}

# ecs 는 80 포트만 허용
resource "aws_security_group" "ecs_tasks" {
  vpc_id = aws_vpc.main.id
  name = "ecs-tasks-sg-${var.environment}"

  ingress {
    from_port       = var.host_port
    protocol        = "tcp"
    to_port         = var.container_port
    cidr_blocks     = ["0.0.0.0/0"]
  }

  egress {
    from_port     = 0
    protocol      = "-1"
    to_port       = 0
    cidr_blocks   = ["0.0.0.0/0"]
  }
}

# [RDS] db를 사용하는 ec2 혹은 ecr 등등에서 사용
resource "aws_security_group" "db_key" {
  name   = "db_key"
  vpc_id = aws_vpc.main.id
  tags = {
    Name = "db-key"
  }
}

# [RDS] Database Security Group
resource "aws_security_group" "db_lock" {
  name        = "db_lock"
  description = "DB security group"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port = var.aws_rds_cluster_port_5432
    to_port   = var.aws_rds_cluster_port_5432
    protocol  = "tcp"

    security_groups = [aws_security_group.db_key.id]
  }

  tags = {
    Name = "db-lock"
  }
}
