# cloudfront
# creating Cloudfront distribution: 생성되는데 시간 오래 걸림.. 4분
resource "aws_cloudfront_distribution" "cf_dist_ecs" {
  enabled = true
  aliases = ["cf2ecs.kmhak.com"]
  origin {
    domain_name = aws_lb.to_ecs.dns_name
    origin_id   = aws_lb.to_ecs.dns_name
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
    target_origin_id       = aws_lb.to_ecs.dns_name
    viewer_protocol_policy = "redirect-to-https" // cert 적용후
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
    #    cloudfront_default_certificate = true
    acm_certificate_arn      = aws_acm_certificate.cert.arn
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1.2_2018"
  }

  tags = {
    Name = "${var.aws_vpc_name}-CF-DIST"
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
