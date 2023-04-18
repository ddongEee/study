resource "aws_vpc" "main" {
  cidr_block           = var.aws_vpc_cidr
  enable_dns_hostnames = true

  tags = {
    Name = "${var.aws_vpc_name}-VPC"
  }
}

resource "aws_subnet" "public_subnets" {
  count                   = length(var.aws_public_subnet_cidrs)
  vpc_id                  = aws_vpc.main.id
  cidr_block              = element(var.aws_public_subnet_cidrs, count.index)
  availability_zone       = element(var.aws_availability_zones, count.index)
  map_public_ip_on_launch = true # 설정시에 ec2에 publicIp 부여

  tags = {
    Name = "[${var.aws_vpc_name}] PublicSubnet-${count.index + 1}"
  }
}

resource "aws_subnet" "private_subnets" {
  count             = length(var.aws_private_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = element(var.aws_private_subnet_cidrs, count.index)
  availability_zone = element(var.aws_availability_zones, count.index)

  tags = {
    Name = "[${var.aws_vpc_name}] PrivateSubnet-${count.index + 1}"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "[${var.aws_vpc_name}] IGW"
  }
}

# TODO : 질문 : public rt 의 경우 zone 별로 만들 필요 있는가?
resource "aws_route_table" "public_subnet" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "[${var.aws_vpc_name}] PublicSubnetRouteTable"
  }
}

resource "aws_route_table_association" "public_subnet_assos" {
  count          = length(var.aws_public_subnet_cidrs)
  subnet_id      = element(aws_subnet.public_subnets.*.id, count.index)
  route_table_id = aws_route_table.public_subnet.id
}

resource "aws_eip" "nat_gw" {
  count = length(aws_subnet.private_subnets)
  vpc   = true
  lifecycle {
    create_before_destroy = true
  }
  tags = {
    Name = "[${var.aws_vpc_name}] EIP4NatGW-${count.index + 1}"
  }
}

resource "aws_nat_gateway" "nat_gateways" {
  count         = length(aws_subnet.private_subnets)
  allocation_id = element(aws_eip.nat_gw.*.id, count.index)
  subnet_id     = element(aws_subnet.public_subnets.*.id, count.index) #  public subnet 에 생성

  tags = {
    Name = "[${var.aws_vpc_name}] NAT-GW-${count.index + 1}"
  }
}

resource "aws_route_table" "private_subnets" {
  count  = length(aws_nat_gateway.nat_gateways)
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = element(aws_nat_gateway.nat_gateways.*.id, count.index)
  }

  tags = {
    Name = "[${var.aws_vpc_name}] Private subnet route table"
  }
}

resource "aws_route_table_association" "private_subnet_assos" {
  count          = length(aws_subnet.private_subnets)
  subnet_id      = element(aws_subnet.private_subnets.*.id, count.index)
  route_table_id = element(aws_route_table.private_subnets.*.id, count.index)
}

## START Instance
resource "aws_key_pair" "web_admin" {
  key_name   = "web_admin"
  public_key = file("~/.ssh/web_admin.pub")
}

resource "aws_security_group" "webserver-sg" {
  name = "allow 22, 80"
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

  tags = {
    Name = "[${var.aws_vpc_name}]-allow-22-80"
  }
}

data "aws_security_group" "default" {
  name = "default"
  vpc_id = aws_vpc.main.id
}

resource "aws_instance" "web" {
  count                  = length(var.aws_public_subnet_cidrs)
  ami                    = "ami-0a93a08544874b3b7" # amzn2-ami-hvm-2.0.20200207.1-x86_64-gp2
  instance_type          = "t2.micro"
  key_name               = aws_key_pair.web_admin.key_name
  subnet_id              = element(aws_subnet.public_subnets.*.id, count.index)
  vpc_security_group_ids = [
    aws_security_group.webserver-sg.id,
    data.aws_security_group.default.id
  ]
  user_data              = <<-EOF
                           #!/bin/bash
                           sudo yum install -y httpd
                           echo "Crayon's WebServer" > /var/www/html/index.html
                           sudo systemctl start httpd
                           sudo systemctl enable httpd
                           EOF

  tags = {
    Name = "[${var.aws_vpc_name}] web-${count.index + 1}"
  }

  depends_on = [aws_internet_gateway.igw]
}

resource "aws_eip" "demo-eips"{
  count    = length(aws_instance.web)
  instance = element(aws_instance.web.*.id, count.index)
  vpc = true
  tags = {
    Name = "[${var.aws_vpc_name}] EIP4Ec2-${count.index}"
  }
}

### ALB 관련
locals {
  ingress_rules = [{
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
    }]

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

resource "aws_alb" "frontend" {
  name            = "alb-example"
  internal        = false
  security_groups = [aws_security_group.sg.id,]
  subnets         = aws_subnet.public_subnets.*.id

  tags = {
    Name = "ALB"
  }

  lifecycle {
    create_before_destroy = true
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

  tags = {
    Name = "Frontend target group"
  }
}

resource "aws_alb_listener" "test" {
  load_balancer_arn = aws_alb.frontend.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    type = "forward"
    target_group_arn = aws_alb_target_group.frontend.arn
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

output "private_ip" {
  value = zipmap(aws_instance.web.*.tags.Name, aws_instance.web.*.private_ip)
}

output "public_ip" {
  value = zipmap(aws_instance.web.*.tags.Name, aws_eip.demo-eips.*.public_ip)
}

output "public_dns" {
  value = zipmap(aws_instance.web.*.tags.Name, aws_eip.demo-eips.*.public_dns)
}

output "private_dns" {
  value = zipmap(aws_instance.web.*.tags.Name, aws_instance.web.*.private_dns)
}

output "alb_id" {
  value = aws_alb.frontend.dns_name
}

# TODO : ASG 반영하기
# TODO : VPC Flow log 적용
