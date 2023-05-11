data "aws_ecr_repository" "api" {
  name         = "day2"
}

resource "aws_ecr_repository" "repo" {
  name = "kmhak/service_${var.environment}" # 환경별로 이미지 따로가져가면 SSOT 위반
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = false
  }
}

resource "aws_ecr_lifecycle_policy" "repo-policy" {
  repository = aws_ecr_repository.repo.name

  policy = <<EOF
  {
    "rules": [
      {
        "rulePriority": 1,
        "description": "Keep image deployed with tag latest",
        "selection": {
          "tagStatus": "tagged",
          "tagPrefixList": ["latest"],
          "countType": "imageCountMoreThan",
          "countNumber": 1
        },
        "action": {
          "type": "expire"
        }
      },
      {
        "rulePriority": 2,
        "description": "Keep last 2 any images",
        "selection": {
          "tagStatus": "any",
          "countType": "imageCountMoreThan",
          "countNumber": 2
        },
        "action": {
          "type": "expire"
        }
      }
    ]
  }
  EOF
}
