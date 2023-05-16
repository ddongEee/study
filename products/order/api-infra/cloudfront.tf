# cloudfront

## for OAC (Origin Access Control)
resource "aws_cloudfront_origin_access_control" "oac" {
#  name                              = local.origin_id # TODO(Umut)
#  name = "cloudfront-s3"
  name                              = aws_s3_bucket.react.id
  description                       = "OAC for foo" # TODO(umut)
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

# creating Cloudfront distribution: 생성되는데 시간 오래 걸림.. 4분
# change resource name.... ecs s3
resource "aws_cloudfront_distribution" "cf_dist_ecs" {
  enabled = true
  aliases = ["cf2ecs.kmhak.com"]
  origin {
    domain_name = aws_lb.to_ecs.dns_name
    # A unique identifier for this origin configuration.
    # Since you can setup multiple origins, used to link caching behavior to origin configurations.
    # dynamic 값이 변함에 따라 해당 cf 리소스도 변경될수 잇으니,
    # 그냥 local 파일로 s3, apigw 선언하고 참조해서 사용하는것이 좋을듯
    origin_id   = aws_lb.to_ecs.dns_name
    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "http-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  origin {
    domain_name = aws_s3_bucket.react.bucket_regional_domain_name
    # dynamic 값이 변함에 따라 해당 cf 리소스도 변경될수 잇으니,
    # 그냥 local 파일로 s3, apigw 선언하고 참조해서 사용하는것이 좋을듯
    origin_id   = aws_s3_bucket.react.id
#    origin_id = "cloudfront-s3" # TODO(umut) what's this
    origin_access_control_id = aws_cloudfront_origin_access_control.oac.id

#    s3_origin_config {
#      origin_access_identity = aws_cloudfront_origin_access_control.test.
#      origin_access_control_id = aws_cloudfront_origin_access_control.oac.id
#    }
  }

  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods  = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods   = ["GET", "HEAD"]
    # dynamic 값이 변함에 따라 해당 cf 리소스도 변경될수 잇으니,
    # 그냥 local 파일로 s3, apigw 선언하고 참조해서 사용하는것이 좋을듯
    target_origin_id = aws_s3_bucket.react.id

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
  }

  ordered_cache_behavior {
    path_pattern     = "/api/*"
    allowed_methods  = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods   = ["GET", "HEAD"]
    # dynamic 값이 변함에 따라 해당 cf 리소스도 변경될수 잇으니,
    # 그냥 local 파일로 s3, apigw 선언하고 참조해서 사용하는것이 좋을듯
    target_origin_id = aws_lb.to_ecs.dns_name

    default_ttl = 0
    min_ttl     = 0
    max_ttl     = 0

#    forwarded_values {
#      query_string = true
#      cookies {
#        forward = "all"
#      }
#    }

    cache_policy_id = aws_cloudfront_cache_policy.auth_header.id
#    use_forwarded_values = false
    viewer_protocol_policy = "redirect-to-https"
  }

  ordered_cache_behavior {
    path_pattern     = "/auth/*"
    allowed_methods  = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods   = ["GET", "HEAD"]
    # dynamic 값이 변함에 따라 해당 cf 리소스도 변경될수 잇으니,
    # 그냥 local 파일로 s3, apigw 선언하고 참조해서 사용하는것이 좋을듯
    target_origin_id = aws_lb.to_ecs.dns_name

    default_ttl = 0
    min_ttl     = 0
    max_ttl     = 0

#    forwarded_values {
#      query_string = true
#      cookies {
#        forward = "all"
#      }
#    }

    cache_policy_id = aws_cloudfront_cache_policy.auth_header.id
#    use_forwarded_values = false
    viewer_protocol_policy = "redirect-to-https"
  }

  ordered_cache_behavior {
    path_pattern     = "/public/*"
    allowed_methods  = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods   = ["GET", "HEAD"]
    # dynamic 값이 변함에 따라 해당 cf 리소스도 변경될수 잇으니,
    # 그냥 local 파일로 s3, apigw 선언하고 참조해서 사용하는것이 좋을듯
    target_origin_id = aws_lb.to_ecs.dns_name

    default_ttl = 0
    min_ttl     = 0
    max_ttl     = 0

#    forwarded_values {
#      query_string = true
#      cookies {
#        forward = "all"
#      }
#    }

    cache_policy_id = aws_cloudfront_cache_policy.auth_header.id
#    use_forwarded_values = false
    viewer_protocol_policy = "redirect-to-https"
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }
  viewer_certificate {
    // if you want viewers to use HTTPS to request your objects
    // and you're using the CloudFront domain name for your distribution
    #    cloudfront_default_certificate = true
    acm_certificate_arn      = aws_acm_certificate.cert.arn
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1.2_2018"
  }

  tags = {
    Name = "${var.aws_vpc_name}-CF-DIST"
  }
}

# 참고 : https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/cloudfront_cache_policy
resource "aws_cloudfront_cache_policy" "auth_header" {
  name = "cloudfront-auth-cache-policy"
  parameters_in_cache_key_and_forwarded_to_origin {
    cookies_config {
      cookie_behavior = "all"
    }

    query_strings_config {
      query_string_behavior = "all"
    }

    headers_config {
      header_behavior = "whitelist"
      headers {
        items = ["Authorization"]
      }
    }
  }
}

## for Bastion Host
resource "aws_cloudfront_distribution" "cf_dist" {
  enabled = true

  origin {
    domain_name = aws_alb.frontend.dns_name
    origin_id   = aws_alb.frontend.dns_name
    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "http-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }
  default_cache_behavior {
    allowed_methods        = ["GET","HEAD","OPTIONS","PUT","POST","PATCH","DELETE"]
    cached_methods         = ["GET","HEAD","OPTIONS"]
    target_origin_id       = aws_alb.frontend.dns_name
    viewer_protocol_policy = "allow-all" // cert 적용후, "redirect-to-https"
    forwarded_values {
      headers = []
      query_string = true
      cookies {
        forward = "all"
      }
    }
  }
  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }
  viewer_certificate {
    // if you want viewers to use HTTPS to request your objects
    // and you're using the CloudFront domain name for your distribution
    cloudfront_default_certificate = true
    #    acm_certificate_arn      = aws_acm_certificate.cert.arn
    #    ssl_support_method       = "sni-only"
    #    minimum_protocol_version = "TLSv1.2_2018"
  }

  tags = {
    Name = "${var.aws_vpc_name}-CF-DIST"
  }
}

# todo
# 정리 : [How to route to multiple origins with CloudFront] : https://advancedweb.hu/how-to-route-to-multiple-origins-with-cloudfront/
# https://billmdevs.medium.com/terraform-aws-routing-with-cloudfront-dc52016ab1e9
