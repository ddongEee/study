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

// 고가용성을 위해서 multi nat gw를 public-subnet 마다 생성.. 성능이슈는 거의 없음
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
