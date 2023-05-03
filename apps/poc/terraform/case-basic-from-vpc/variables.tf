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
}

variable "aws_vpc_name" {
  type    = string
#  default = "poc"
}

variable "aws_vpc_cidr" {
  description = "AWS VPC cidr block"
#  default     = "10.0.0.0/16"
}

variable "aws_public_subnet_cidrs" {
  type        = list(string)
  description = "Public Subnet CIDR values"
#  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "aws_private_subnet_cidrs" {
  type        = list(string)
  description = "Private Subnet CIDR values"
#  default     = ["10.0.4.0/24", "10.0.5.0/24", "10.0.6.0/24"]
}

variable "aws_availability_zones" {
  description = "AWS availability zone names"
  type        = list(string)
#  default     = ["ap-northeast-2a", "ap-northeast-2b", "ap-northeast-2c"]
}

variable "app_name" {
  type        = string
  description = "ecs task application name"
}

variable "host_port" {
  type        = number
  description = "host port"
  default     = 80
}
variable "container_port" {
  type        = number
  description = "container port"
  default     = 80
}

variable "environment" {
  type = string
  description = "running environment"
  default = "local"
}

variable "aws_account_id" {
  type = number
  description = "aws account id"
}

variable "aws_elb_account_id_seoul" {
  type = number
  description = "elb-account-id(Principal) : 600734575887(서울, ap-northeast-2)"
  default = 600734575887
}

variable "scaling_max_capacity" {
  type        = number
  description = "ecs asg scaling_max_capacity"
  default     = 3
}

variable "scaling_min_capacity" {
  type        = number
  description = "ecs asg scaling_min_capacity"
  default     = 1
}

variable "cpu_or_memory_limit" {
  type = number
  description = "ecs asg cpu_or_memory_limit"
  default = 70
}

variable "ecs_task_image_tag" {
  type = string
}

#variable "aws_ami_id_maps" {
#  description = ""
#  type = map
#  default = {}
#}

