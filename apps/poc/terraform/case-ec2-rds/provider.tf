terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
  required_version = "1.4.4"
}

provider "aws" {
  region = var.aws_region
  shared_config_files = [ "~/.aws/config" ]
  shared_credentials_files = [ "~/.aws/credentials" ]
  profile = "lab-devops"

  default_tags {
    tags = {
      CreatedBy = "Terraform"
    }
  }
}
