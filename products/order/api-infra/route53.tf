data "aws_route53_zone" "test_domain_zone" {
  name = "kmhak.com"
}

## creating A record for domain:
resource "aws_route53_record" "website_url" {
  name    = var.aws_domain_name
  zone_id = data.aws_route53_zone.test_domain_zone.zone_id
  type    = "A"
  alias {
    name                   = aws_cloudfront_distribution.cf_dist.domain_name
    zone_id                = aws_cloudfront_distribution.cf_dist.hosted_zone_id
    evaluate_target_health = true
  }
}

resource "aws_route53_record" "test_alb" {
  name    = "testalb.kmhak.com"
  zone_id = data.aws_route53_zone.test_domain_zone.zone_id
  type    = "CNAME"
  ttl     = 60
  records = [aws_alb.frontend.dns_name]
}

resource "aws_route53_record" "test_cf" {
  name    = "testcf.kmhak.com"
  zone_id = data.aws_route53_zone.test_domain_zone.zone_id
  type    = "CNAME"
  ttl     = 60
  records = [aws_cloudfront_distribution.cf_dist.domain_name]
}

resource "aws_route53_record" "test_ecs_lb" {
  name    = "alb2ecs.kmhak.com"
  zone_id = data.aws_route53_zone.test_domain_zone.zone_id
  type    = "CNAME"
  ttl     = 60
  records = [aws_lb.to_ecs.dns_name]
}

resource "aws_route53_record" "cf_for_ecs" {
  name    = "cf2ecs.kmhak.com"
  zone_id = data.aws_route53_zone.test_domain_zone.zone_id
  type    = "CNAME"
  ttl     = 60
  records = [aws_cloudfront_distribution.cf_dist_ecs.domain_name]
}
