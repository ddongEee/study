resource "aws_vpc" "main" {
  cidr_block = var.aws_vpc_cidr
  tags = {
    Name = "${var.aws_vpc_name}-VPC"
  }
}

resource "aws_subnet" "public_subnets" {
  count = length(var.aws_public_subnet_cidrs)
  vpc_id = aws_vpc.main.id
  cidr_block = element(var.aws_public_subnet_cidrs, count.index)
  availability_zone = element(var.aws_availability_zones, count.index)

  tags = {
    Name = "[${var.aws_vpc_name}] PublicSubnet-${count.index + 1}"
  }
}

resource "aws_subnet" "private_subnets" {
  count = length(var.aws_private_subnet_cidrs)
  vpc_id = aws_vpc.main.id
  cidr_block = element(var.aws_private_subnet_cidrs, count.index)
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

resource "aws_route_table" "second_rt" {
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
  count = length(var.aws_public_subnet_cidrs)
  subnet_id = element(aws_subnet.public_subnets, count.index).id
  route_table_id = aws_route_table.second_rt.id
}

resource "aws_eip" "for_nat_gateways" {
  count = length(aws_subnet.private_subnets)
  vpc   = true
  lifecycle {
    create_before_destroy = true
  }
  tags = {
    Name = "[${var.aws_vpc_name}] EIP-${count.index + 1}"
  }
}

resource "aws_nat_gateway" "nat_gateways" {
  count = length(aws_subnet.private_subnets)
  allocation_id = element(aws_eip.for_nat_gateways, count.index).id
  subnet_id = element(aws_subnet.public_subnets, count.index).id #  public subnet 에 생성

  tags = {
    Name = "[${var.aws_vpc_name}] NAT-GW-${count.index + 1}"
  }
}

resource "aws_route_table" "private_subnets" {
  count = length(aws_nat_gateway.nat_gateways)
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = element(aws_nat_gateway.nat_gateways, count.index).id
  }

  tags = {
    Name = "[${var.aws_vpc_name}] Private subnet route table"
  }
}

resource "aws_route_table_association" "private_subnet_assos" {
  count = length(aws_subnet.private_subnets)
  subnet_id = element(aws_subnet.private_subnets, count.index).id
  route_table_id = element(aws_route_table.private_subnets, count.index).id
}
