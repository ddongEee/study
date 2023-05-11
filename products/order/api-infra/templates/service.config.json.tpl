[
  {
    "name": "${app_name}",
    "image": "${aws_ecr_repository}:${tag}",
    "essential": true,
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-region": "${region}",
        "awslogs-stream-prefix": "staging-service",
        "awslogs-group": "awslogs-service-staging-${env_suffix}"
      }
    },
    "portMappings": [
      {
        "containerPort": ${container_port},
        "hostPort": ${host_port},
        "protocol": "tcp"
      }
    ],
    "cpu": 2,
    "environment": [
      {
        "name": "PORT",
        "value": "${host_port}"
      },
      {
        "name": "SPRING_PROFILES_ACTIVE",
        "value": "dev"
      }
    ],
    "secrets": [
          {"name":"username","valueFrom":"${rds_credentials}"},
          {"name":"password","valueFrom":"${rds_credentials}"},
          {"name":"engine","valueFrom":"${rds_credentials}"},
          {"name":"host","valueFrom":"${rds_credentials}"},
          {"name":"port","valueFrom":"${rds_credentials}"},
          {"name":"dbClusterIdentifier","valueFrom":"${rds_credentials}"}
    ],
    "ulimits": [
      {
        "name": "nofile",
        "softLimit": 65536,
        "hardLimit": 65536
      }
    ],
    "mountPoints": [],
    "memory": 512,
    "volumesFrom": []
  }
]
