## for static s3 file
#resource "aws_cloudfront_distribution" "to_s3" {
#  enabled = true
#  aliases = ["static.kmhak.com"] # todo : domain 발급받으면 사용?
#
##  default_root_object = "index.html" # react 용?
#  origin {
#    domain_name = aws_s3_bucket.react.bucket_regional_domain_name
#    origin_id   = aws_s3_bucket.react.id
#    origin_access_control_id = aws_cloudfront_origin_access_control.oac.id
#    custom_origin_config {
#      http_port              = 80
#      https_port             = 443
#      origin_protocol_policy = "http-only"
#      origin_ssl_protocols   = ["TLSv1.2"]
#    }
#  }
#  default_cache_behavior {
#    allowed_methods        = ["GET", "HEAD"]
#    cached_methods         = ["GET", "HEAD"]
#    target_origin_id       = aws_s3_bucket.react.id
#    viewer_protocol_policy = "redirect-to-https" // cert 적용후
#    forwarded_values {
#      headers = []
#      query_string = true
#      cookies {
#        forward = "all"
#      }
#    }
#  }
#  restrictions {
#    geo_restriction {
#      restriction_type = "none"
#    }
#  }
#  viewer_certificate {
#    // if you want viewers to use HTTPS to request your objects
#    // and you're using the CloudFront domain name for your distribution
#    #    cloudfront_default_certificate = true
#    acm_certificate_arn      = aws_acm_certificate.cert.arn
#    ssl_support_method       = "sni-only"
#    minimum_protocol_version = "TLSv1.2_2018"
#  }
#
#  tags = {
#    Name = "${var.aws_vpc_name}-CF-DIST"
#  }
#}
#
#resource "aws_s3_bucket_policy" "oac" {
#  bucket = aws_s3_bucket.react.id
#  policy = data.aws_iam_policy_document.cloudfront_oac_access.json
#}
#
## for cloudfront s3 OAC
#data "aws_iam_policy_document" "cloudfront_oac_access" {
#  statement {
#    actions = ["s3:GetObject"]
#    resources = [aws_s3_bucket.react.arn, "${aws_s3_bucket.react.arn}/*"]
#
#    principals {
#      type        = "Service"
#      identifiers = ["cloudfront.amazonaws.com"]
#    }
#
#    condition {
#      test     = "StringEquals"
#      variable = "AWS:SourceArn"
#      values   = [aws_cloudfront_distribution.to_s3.arn]
#    }
#  }
#}
#
##resource "aws_route53_record" "static" {
##  name    = "static.kmhak.com"
##  zone_id = data.aws_route53_zone.test_domain_zone.zone_id
##  type    = "CNAME"
##  ttl     = 60
##  records = [aws_cloudfront_distribution.to_s3.domain_name]
##}
