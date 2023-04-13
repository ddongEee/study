resource "aws_key_pair" "web_admin" {
  key_name = "web_admin"
  public_key = file("~/.ssh/web_admin.pub")
}

resource "aws_security_group" "ssh" {
  name = "allow_ssh_from_all"
  description = "Allow SSH port from all"
  ingress {
    from_port  = 22
    to_port    = 22
    protocol   = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "aws_security_group" "default" {
  name = "default"
}

resource "aws_instance" "web" {
  ami = "ami-0a93a08544874b3b7" # amzn2-ami-hvm-2.0.20200207.1-x86_64-gp2
  instance_type = "t2.micro"
  key_name = aws_key_pair.web_admin.key_name
  vpc_security_group_ids = [
    aws_security_group.ssh.id,
    data.aws_security_group.default.id
  ]
  tags = {
    Name = "web"
  }
}

resource "aws_db_instance" "web_db" {
  allocated_storage = 8
  engine = "mysql"
  engine_version = "5.7.33"
  instance_class = "db.t2.micro"
  username = "admin"
  password = "xxxxxxxx"
  skip_final_snapshot = true # terraform 에서 삭제하기 쉽게 하기 위해..
}
