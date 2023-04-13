# 참고 : https://developer.hashicorp.com/terraform/tutorials/configuration-language/variables
# 참고 : https://upcloud.com/resources/tutorials/terraform-variables
# 참고 : https://terraform101.inflearn.devopsart.dev/advanced/variables/
variable "aws_region" {
  description = "AWS region"
  type = string
  default = "ap-northeast-2"
}

variable "aws_availability_zones" {
  description = "AWS availability zone names"
  type = list(string)
  default = ["ap-northeast-2a", "ap-northeast-2b", "ap-northeast-2c"]
}

#variable "aws_ami_id_maps" {
#  description = ""
#  type = map
#  default = {}
#}
