# ECR

This file provides general information on setting up AWS ECR repository, and
pushing a containerized image.

## Prerequisites

- Must be able to sign in to the AWS console using an AWS "root" access account

1. Provision the "root" account in IAM Identity Center:
    - Enable IAM Identiry Center using "organization instances"
    - Set up an "AdministratorAccess" permission set
    - Create an "admin" group in the IAM Identity Center
    - Assign the "AdministratorAccess" permission set to the "admin' group
    - Assign the "admin" group and "AdministratorAccess" permission set to the
      "root management" account

2. Provision an administrator user account
    - Create a user account in IAM Identity Center
    - Ensure user account is in the "admin" group
    - Ensure the "admin" group has the "AdministratorAccess" permission set

## Set Up Tools: `aws cli`

- [Installing the latest version of the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)

## Create an ECR Repository

1. Sign in using sso login:

    ```shell
    # previously configured profile admin
    AWS_PROFILE="admin"
    aws sso login --profile ${AWS_PROFILE}
    # aws sts get-caller-identity  --profile ${AWS_PROFILE}
    ```

2. Create a repository in the ECR private registry:

    ```shell
    REPOSITORY="rubensgomes/helloworld-ms"
    AWS_REGION="us-east-1"
    AWS_PROFILE="admin"
    aws ecr create-repository \
      --repository-name "${REPOSITORY}"  \
      --region ${AWS_REGION} \
      --profile ${AWS_PROFILE}
    ```

3. Describe ECR repositories:

    ```shell
    AWS_REGION="us-east-1"
    AWS_PROFILE="admin"
    aws ecr describe-repositories \
      --region ${AWS_REGION} \
      --profile ${AWS_PROFILE}
    ```

4. Delete ECR registry repository:

    ```shell
    REPOSITORY="rubensgomes/helloworld-ms"
    # Note Rubens Account
    AWS_ACCOUNT="347851559421"
    AWS_REGION="us-east-1"
    AWS_PROFILE="admin"
    registry="${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com"
    aws ecr delete-repository \
      --repository-name ${REPOSITORY} \
      --force \
      --region ${AWS_REGION} \
      --profile ${AWS_PROFILE}
    ```

5. Sign in to AWS ECR registry account:

   ```shell
   # Note Rubens Account
   AWS_ACCOUNT="347851559421"
   AWS_REGION="us-east-1"
   AWS_PROFILE="admin"
   REGISTRY="${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com"
   aws ecr get-login-password \
      --region ${AWS_REGION} \
      --profile ${AWS_PROFILE} | docker login --username AWS --password-stdin ${REGISTRY}
   ```

## Push an Image to ECR Repository

1. Tag image to push to ECR repository:

   ```shell
   # extract version from app/build/libs/app-x.y.z-SNAPSHOT.jar (e.g., version=x.y.z)
   # VERSION="0.1.1-SNAPSHOT"
   VERSION="latest"
   REPOSITORY="rubensgomes/helloworld-ms"
   # Note Rubens Account
   AWS_ACCOUNT="347851559421"
   AWS_REGION="us-east-1"
   AWS_PROFILE="admin"
   REGISTRY="${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com"
   docker image tag ${REPOSITORY}:${VERSION} \
      ${REGISTRY}/${REPOSITORY}:${VERSION} || {
     printf "failed to tag image\n"
     sleep 5
   }
   ```

2. Push image to ECR repository:

    ```shell
    # extract version from app/build/libs/app-x.y.z-SNAPSHOT.jar (e.g., version=x.y.z)
    # VERSION="0.1.1-SNAPSHOT"
    VERSION="latest"
    REPOSITORY="rubensgomes/helloworld-ms"
    # Note Rubens Account
    AWS_ACCOUNT="347851559421"
    AWS_REGION="us-east-1"
    AWS_PROFILE="admin"
    REGISTRY="${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com"
    docker image push "${REGISTRY}/${REPOSITORY}:${VERSION}" || {
      printf "failed to push image\n" >&2
      sleep 5
    }
    ```

3. Pull image from ECR repository:

    ```shell
    # extract version from app/build/libs/app-x.y.z-SNAPSHOT.jar (e.g., version=x.y.z)
    # VERSION="0.1.1-SNAPSHOT"
    VERSION="latest"
    REPOSITORY="rubensgomes/helloworld-ms"
    # Note Rubens Account
    AWS_ACCOUNT="347851559421"
    AWS_REGION="us-east-1"
    AWS_PROFILE="admin"
    REGISTRY="${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com"
    docker image pull "${REGISTRY}/${REPOSITORY}:${VERSION}" || {
      printf "failed to pull image\n" >&2
      sleep 5
    }
    ```

4. Describe images in ECR repository:

    ```shell
    REPOSITORY="rubensgomes/helloworld-ms"
    AWS_REGION="us-east-1"
    AWS_PROFILE="admin"
    aws ecr describe-images \
      --repository-name "${REPOSITORY}"  \
      --region ${AWS_REGION} \
      --profile ${AWS_PROFILE}
    ```

5. Remove image from local registry:

    ```shell
    # extract version from app/build/libs/app-x.y.z-SNAPSHOT.jar (e.g., version=x.y.z)
    # VERSION="0.1.1-SNAPSHOT"
    VERSION="latest"
    REPOSITORY="rubensgomes/helloworld-ms"
    # Note Rubens Account
    AWS_ACCOUNT="347851559421"
    AWS_REGION="us-east-1"
    REGISTRY="${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com"
    docker image rm "${REGISTRY}/${REPOSITORY}:${VERSION}" || {
      printf "failed to remove image\n" >&2
      sleep 5
    }
    ```

6. Remove image from ECR private registry repository:

    ```shell
    # extract version from app/build/libs/app-x.y.z-SNAPSHOT.jar (e.g., version=x.y.z)
    # VERSION="0.1.1-SNAPSHOT"
    VERSION="latest"
    REPOSITORY="rubensgomes/helloworld-ms"
    AWS_REGION="us-east-1"
    AWS_PROFILE="admin"
    aws ecr batch-delete-image \
      --repository-name ${REPOSITORY} \
      --image-ids imageTag=${VERSION} \
      --region ${AWS_REGION} \
      --profile ${AWS_PROFILE}
    ```

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
