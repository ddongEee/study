# virginia 리전에 ACM 생성하고, aws에 등록한 domain name 에 대해서 SSL 적용
## generate ACM cert for domain on us-east-1 :
resource "aws_acm_certificate" "cert" {
  provider                  = aws.virginia
  domain_name               = var.aws_domain_name
  subject_alternative_names = ["*.${var.aws_domain_name}"]
  validation_method         = "DNS"
  tags = {
    Name = "${var.aws_vpc_name}-ACM"
  }
}

## validate cert:
resource "aws_route53_record" "cert_validation" {
  for_each = {
    for d in aws_acm_certificate.cert.domain_validation_options : d.domain_name => {
      name   = d.resource_record_name
      record = d.resource_record_value
      type   = d.resource_record_type
    }
  }
  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = data.aws_route53_zone.test_domain_zone.zone_id
}

resource "aws_acm_certificate_validation" "cert_validation" {
  provider = aws.virginia
  certificate_arn         = aws_acm_certificate.cert.arn // 검증중인 인증서의 ARN.
  validation_record_fqdns = [ for r in aws_route53_record.cert_validation : r.fqdn ] // 유효성 검사를 구현하는 FQDN 목록
}
