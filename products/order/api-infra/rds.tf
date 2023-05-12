resource "random_password" "master_password" {
  length  = 16
  special = false
}

# 가능 버전 확인
# aws rds describe-db-engine-versions --engine aurora-postgresql --query '*[].[EngineVersion]' --output text --region ap-northeast-2
resource "aws_rds_cluster" "c_v2" {
  engine                  = "aurora-postgresql"
  engine_mode             = "provisioned"
  engine_version          = "15.2"
  cluster_identifier      = "aurora-cluster-demo-v2"
  master_username         = var.aws_db_username
  master_password         = var.aws_db_password
#  master_password         = random_password.master_password.result

  db_subnet_group_name    = aws_db_subnet_group.db_subnet_group.name
  vpc_security_group_ids  = [aws_security_group.db_lock.id]

  availability_zones      = var.aws_availability_zones
  database_name           = var.aws_db_name
  skip_final_snapshot     = true

  # Nasty little bug
  # See: https://github.com/hashicorp/terraform-provider-aws/issues/25480
  lifecycle {
    ignore_changes = [availability_zones]
  }
}

resource "aws_rds_cluster_instance" "v2i" {
  count              = 2
  identifier         = "aurora-cluster-demo-v2-instance-${count.index}"
  cluster_identifier = aws_rds_cluster.c_v2.id
  instance_class     = "db.t3.medium"
  engine             = aws_rds_cluster.c_v2.engine
  engine_version     = aws_rds_cluster.c_v2.engine_version
  publicly_accessible = false
}

resource "aws_db_subnet_group" "db_subnet_group" {
  name       = "aurora-cluster-demo-v2-db-subnet-group"
  subnet_ids = aws_subnet.private_subnets.*.id #  DB용 private subnet 만들필요?
}

resource "aws_secretsmanager_secret" "rds_credentials" {
  name   = "credentials"
  policy = data.aws_iam_policy_document.my_password_policy.json
}

resource "aws_secretsmanager_secret_version" "rds_credentials" {
  secret_id     = aws_secretsmanager_secret.rds_credentials.id
  secret_string = <<EOF
{
  "username": "${aws_rds_cluster.c_v2.master_username}",
  "password": "${var.aws_db_password}",
  "engine": "${aws_rds_cluster.c_v2.engine}",
  "host": "${aws_rds_cluster.c_v2.endpoint}",
  "port": ${var.aws_rds_cluster_port_5432},
  "dbClusterIdentifier": "${aws_rds_cluster.c_v2.cluster_identifier}"
}
EOF
}

output "instance" {
  value = aws_rds_cluster_instance.v2i.*.endpoint
}

output "cluster" {
  value = aws_rds_cluster.c_v2.endpoint
}

output "cluster-r" {
  value = aws_rds_cluster.c_v2.reader_endpoint
}
