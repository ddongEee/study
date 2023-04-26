
aws_domain_name          = "kmhak.com"
aws_vpc_name             = "poc"
aws_vpc_cidr             = "10.0.0.0/16"
aws_public_subnet_cidrs  = ["10.0.1.0/24", "10.0.2.0/24"]
aws_private_subnet_cidrs = ["10.0.4.0/24", "10.0.5.0/24"]
aws_availability_zones   = ["ap-northeast-2a", "ap-northeast-2c"]
app_name                 = "nginx"
host_port                = 8080
container_port           = 8080
