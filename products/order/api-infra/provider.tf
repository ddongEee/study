terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 4.0"
    }
    docker = {
      source = "kreuzwerker/docker"
    }
  }
  required_version = "1.4.4"
}

# default provider
provider "aws" {
  region = var.aws_region
  shared_config_files = [ "~/.aws/config" ]
  shared_credentials_files = [ "~/.aws/credentials" ]
  profile = "poc-admin"

  default_tags {
    tags = {
      CreatedBy   = "Terraform"
      Environment = var.environment
    }
  }
}

# CloudFront 에서 HTTPS 설정을위해 사용하는 ACM 인증서는, us-east-1에 생성해야되서..
provider "aws" {
  alias  = "virginia"
  region = "us-east-1"
}

provider "docker" {}
