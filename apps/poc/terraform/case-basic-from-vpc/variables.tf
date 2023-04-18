# 참고 : https://developer.hashicorp.com/terraform/tutorials/configuration-language/variables
# 참고 : https://upcloud.com/resources/tutorials/terraform-variables
# 참고 : https://terraform101.inflearn.devopsart.dev/advanced/variables/
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-northeast-2"
}

variable "aws_domain_name" {
  type        = string
  description = "The domain name to use"
  default     = "test.kmhak.com"
}

variable "aws_vpc_name" {
  type    = string
  default = "poc"
}

variable "aws_vpc_cidr" {
  description = "AWS VPC cidr block"
  default     = "10.0.0.0/16"
}

variable "aws_public_subnet_cidrs" {
  type        = list(string)
  description = "Public Subnet CIDR values"
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "aws_private_subnet_cidrs" {
  type        = list(string)
  description = "Private Subnet CIDR values"
  default     = ["10.0.4.0/24", "10.0.5.0/24", "10.0.6.0/24"]
}

variable "aws_availability_zones" {
  description = "AWS availability zone names"
  type        = list(string)
  default     = ["ap-northeast-2a", "ap-northeast-2b", "ap-northeast-2c"]
}

#variable "aws_ami_id_maps" {
#  description = ""
#  type = map
#  default = {}
#}

