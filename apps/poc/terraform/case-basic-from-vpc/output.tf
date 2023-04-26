output "private_ip" {
  value = zipmap(aws_instance.web.*.tags.Name, aws_instance.web.*.private_ip)
}

output "public_ip" {
  value = zipmap(aws_instance.web.*.tags.Name, aws_eip.demo-eips.*.public_ip)
}

output "public_dns" {
  value = zipmap(aws_instance.web.*.tags.Name, aws_eip.demo-eips.*.public_dns)
}

output "private_dns" {
  value = zipmap(aws_instance.web.*.tags.Name, aws_instance.web.*.private_dns)
}

output "alb_id" {
  value = aws_alb.frontend.dns_name
}

output "alb_to_ecs" {
  value = aws_lb.to_ecs.dns_name
}

output "cloudfront_domain_name" {
  value = aws_cloudfront_distribution.cf_dist.domain_name
}
