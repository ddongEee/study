resource "aws_s3_bucket" "log_storage" {
  bucket = "ecs-access-logs-kemist-${var.environment}"
  force_destroy = true
}

# todo : 아래 동작 방식 체크
resource "aws_s3_bucket_ownership_controls" "example" {
  bucket = aws_s3_bucket.log_storage.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_acl" "lb-logs-acl" {
  bucket = aws_s3_bucket.log_storage.id
  depends_on = [aws_s3_bucket_ownership_controls.example]
  # `private`, `public-read`, `public-read-write`, `aws-exec-read`, `authenticated-read`, `log-delivery-write` 중 하나 선택.
  # 기본값은 `private`.
  # `grant`와 대비되는 속성
  acl    = "private"
}

resource "aws_cloudwatch_log_group" "service" {
  name = "awslogs-service-staging-${var.environment}"

  tags = {
    Application = var.app_name
  }
}

# TODO : check
data "aws_iam_policy_document" "allow-lb" {
  statement {
    principals {
      type        = "Service"
      identifiers = ["logdelivery.elb.amazonaws.com"]
    }

    actions = ["s3:PutObject"]

    resources = [
      "arn:aws:s3:::${aws_s3_bucket.log_storage.bucket}/frontend-alb/AWSLogs/${var.aws_account_id}/*"
    ]

    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"

      values = [
        "bucket-owner-full-control"
      ]
    }
  }
  statement {
    principals {
      type        = "Service"
      identifiers = ["logdelivery.elasticloadbalancing.amazonaws.com"]
    }

    actions = ["s3:PutObject"]

    resources = [
      "arn:aws:s3:::${aws_s3_bucket.log_storage.bucket}/frontend-alb/AWSLogs/${var.aws_account_id}/*"
    ]

    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"

      values = [
        "bucket-owner-full-control"
      ]
    }
  }
  statement {
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.aws_elb_account_id_seoul}:root"]
    }

    actions = ["s3:PutObject"]

    resources = [
      "arn:aws:s3:::${aws_s3_bucket.log_storage.bucket}/frontend-alb/AWSLogs/${var.aws_account_id}/*"
    ]

    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"

      values = [
        "bucket-owner-full-control"
      ]
    }
  }
}

resource "aws_s3_bucket_policy" "allow-lb" {
  bucket = aws_s3_bucket.log_storage.id
  policy = data.aws_iam_policy_document.allow-lb.json
}

resource "aws_s3_bucket_lifecycle_configuration" "lifecycle" {
  bucket = aws_s3_bucket.log_storage.id

  rule {
    id      = "log_lifecycle_${var.environment}"
    status  = "Enabled"

    expiration {
      days = 10
    }
  }
}

resource "aws_s3_bucket" "react" {
  bucket = "day3-app"
  force_destroy = true
}

resource "aws_s3_bucket_ownership_controls" "react" {
  bucket = aws_s3_bucket.react.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_acl" "react" {
  bucket = aws_s3_bucket.react.id
  depends_on = [aws_s3_bucket_ownership_controls.react]
  acl    = "private"
}

resource "aws_s3_bucket_public_access_block" "pab" {
  bucket                  = aws_s3_bucket.react.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

