# ECS

This file provides general information on setting up AWS ECS, and have tasks
running containerized image applications.

## Prerequisites

1. Ensure you have a default VPC and subnets setup
    - VPC ID: vpc-045218fadf49af3a0
2. Ensure you have a default Security Group
3. [Local computer public IP](https://checkip.amazonaws.com/)
4. Create a key-pair in the us-east-1 region
5. Create an ECS cluster -- I had to use the root user!!!
6. Create an ECS task definition
7. Using Environment in Task Definitions:
   - You may SERVER_PORT to define the Spring Boot container port to use

      ```shell
      # use this only if you don't want to use Spring Boot default "8080" port
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
   - Custom TCP / TCP / 8080 / 35.146.45.12/32

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
- Ensure to select "Lanuch Type" --> FARGATE

### Exercise REST API

Once the service is up and running:

   ```text
   http://34.224.71.19/api/v1/helloworld
   prints {"message":"Hello World!"}
   ```


## Create Custom ECS Cluster

- Note: image "347851559421.dkr.ecr.us-east-1.amazonaws.com/rubensgomes/helloworld-ms:latest"

1. Sign in using sso login:

    ```shell
    # previously configured profile admin
    AWS_PROFILE="admin"
    aws sso login --profile ${AWS_PROFILE}
    # aws sts get-caller-identity  --profile ${AWS_PROFILE}
    ```

2. Create custom ECS cluster:

   ```shell
   CLUSTER="rubens-fargate-cluster"
   AWS_REGION="us-east-1"
   AWS_PROFILE="admin"
   aws ecs create-cluster \
     --cluster-name ${CLUSTER} \
     --region ${AWS_REGION} \
     --profile ${AWS_PROFILE}
   ```
   
3. Retrieve ECS service:

   ```shell
   aws ecs describe-services \
     --services helloworld-task-service-btl0eelf \
     --profile admin \
     --region us-east-1 \
     --cluster rubens-cluster
   ```
