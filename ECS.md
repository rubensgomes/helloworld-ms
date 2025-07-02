# ECS

This file describes how to set up an ECS to have tasks running docker containers.

## Prerequisites

1. Ensure you have a default VPC and subnets setup
    - VPC ID: vpc-045218fadf49af3a0
2. Ensure you have a default Security Group
3. [Local computer public IP](https://checkip.amazonaws.com/)
4. Create a key-pair in the us-east-1 region
5. Create an ECS cluster -- I had to use the root user!!!
6. Create an ECS task definition
7. Use Spring Boot SERVER_PORT to define the container port to use

   ```shell
   SERVER_PORT="5000"
   declare -rx SERVER_PORT   
   ```
   
8. [Learn how to create an Amazon ECS Linux task for the Fargate launch type](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/getting-started-fargate.html)
9. [Creating Amazon ECS resources using the AWS CDK](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/tutorial-ecs-web-server-cdk.html)
10. [Set up to use Amazon ECS](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html)
11. [Amazon ECS task definition parameters for the Fargate launch type](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definition_parameters.html#container_definition_portmappings?icmpid=docs_ecs_hp-task-definition)

## Miscellaneous Notes

### Security Group - Inbound Rule

1. EC2 -> Security Groups
2. Select the default security group
3. Select Actions -> Inbound Rules
4. Determine the IP address of local machine
   - https://checkip.amazonaws.com/
   - assume: 35.146.45.12
5. Create following inbound rule:
   - Custom TCP / TCP / 8888 / 35.146.45.12/32

### VPC - Default VPC

1. VPC -> Your VPCs
2. Ensure a "default" VPC is created and available

### Cluster - Create Cluster

1. ECS -> Cluster
2. Create cluster

### ECS Service - Create Service

1. ECS -> Cluster -> (select created cluster)
2. Select Services tab
3. Create
4. Enter following data:
- Select task definition family
- Enter the latest definition
- Enter service name (or leave default)

### Push Image to Private ECR

- ECR Repository - Create repository

   ```shell
   repository="rubensgomes/helloworld-ms"
   aws ecr create-repository \
   --repository-name "${repository}"  \
   --region us-east-1 \
   --profile admin
   ```

- ECR Repositoy - Login to Repository

   ```shell
   aws sso login --profile admin
   # Rubens' personal account id: 347851559421
   registry="347851559421.dkr.ecr.us-east-1.amazonaws.com"
   aws ecr get-login-password --region us-east-1 --profile admin \
   | docker login --username AWS --password-stdin ${registry}
   ```

- ECR Repositoy - Tag Image

   ```shell
   version="x.y.z"
   registry="347851559421.dkr.ecr.us-east-1.amazonaws.com"
   repository="rubensgomes/helloworld-ms"
   docker image tag ${repository}:${version} \
   ${registry}/${repository}:${version}
   ```

- ECR Repository - Push Image to ECR

   ```shell
   version="x.y.z"
   registry="347851559421.dkr.ecr.us-east-1.amazonaws.com"
   repository="rubensgomes/helloworld-ms"
   docker image push "${registry}/${repository}:${version}"
   ```

### Exercise REST API

Once the service is up and running:

   ```text
   http://34.224.71.19:8888/api/v1/helloworld
   prints {"message":"Hello World!"}
   ```

