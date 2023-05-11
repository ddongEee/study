# - Bastion Host 혹은 SSH 터널링을 위한 instance 생성
resource "aws_key_pair" "web_admin" {
  key_name   = "web_admin"
  public_key = file("~/.ssh/web_admin.pub")
}

resource "aws_instance" "web" {
  count                  = length(var.aws_public_subnet_cidrs)
  ami                    = "ami-0a93a08544874b3b7" # amzn2-ami-hvm-2.0.20200207.1-x86_64-gp2
  instance_type          = "t2.micro"
  key_name               = aws_key_pair.web_admin.key_name
  subnet_id              = element(aws_subnet.public_subnets.*.id, count.index)
  vpc_security_group_ids = [
    aws_security_group.ec2.id,
    aws_security_group.allow_8080.id,
    aws_security_group.db_key.id, # db access 용 sg
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
    "Patch Group" = "AccountGuardian-PatchGroup-DO-NOT-DELETE"
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
