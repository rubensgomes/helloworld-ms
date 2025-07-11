# eks

This file provides general information on setting up AWS EKS cluster, and 
pushing and running a containerized application.

## Set Up Tools: `kubectl` `eksctl`

[Set up to use Amazon EKS](https://docs.aws.amazon.com/eks/latest/userguide/setting-up.html)
[Set up kubectl and eksctl](https://docs.aws.amazon.com/eks/latest/userguide/install-kubectl.html#kubectl-install-update)

## Create an EKS Auto Mode Cluster

- [Configure the cluster](https://docs.aws.amazon.com/eks/latest/userguide/quickstart.html#_configure_the_cluster)

1. Sign in using sso login:

    ```shell
    # previously configured profile admin
    AWS_PROFILE="admin"
    aws sso login --profile ${AWS_PROFILE}
    # aws sts get-caller-identity  --profile admin
    ```

2. Create EKS cluster:

    ```shell
    AWS_PROFILE="admin"
    eksctl create cluster \
      --profile ${AWS_PROFILE} \
      -f cluster.yaml
    ```

3. Get all the EKS clusters in a region:

    ```shell
    AWS_PROFILE="admin"
    AWS_REGION="us-east-1"
    eksctl get cluster \
      --profile ${AWS_PROFILE} \
      --region ${AWS_REGION}
    ```

4. Get details on a given EKS cluster:

    ```shell
    CLUSTER="playground-cluster"
    AWS_PROFILE="admin"
    AWS_REGION="us-east-1"
    eksctl get cluster \
      --name ${CLUSTER} \
      --profile ${AWS_PROFILE} \
      --region ${AWS_REGION}
    ```

   - Name: playground-cluster
   - VPC: vpc-01e10d7400ddf9602
     - VPC Name: eksctl-playground-cluster-cluster/VPC
     - CIDR: 192.168.0.0/16
     - 4(four) Subnets
   - Subnet: subnet-026187a58036c02e7,subnet-03d3e09319bc839e4
   - Security Group: sg-0cd9ca946056fbd50
     - Security Group Name: eksctl-playground-cluster-cluster-ControlPlaneSecurityGroup-fRoPD2mMyUsu
     - VPC Id; vpc-01e10d7400ddf9602

5. Delete cluster:

    ```shell
    AWS_PROFILE="admin"
    eksctl delete cluster \
      --profile ${AWS_PROFILE} \
      -f cluster.yaml
    ```

6. Enable cloudwatch logging:

    ```shell
    AWS_PROFILE="admin"
    CLUSTER="playground-cluster"
    eksctl utils update-cluster-logging \
      --enable-types all \
      --cluster ${CLUSTER} \
      --profile ${AWS_PROFILE} \
      --approve
    ```

## Retrieve Information from the EKS Cluster

1. Get Nodes:

   ```shell
   AWS_PROFILE="admin"
   AWS_REGION="us-east-1"
   CLUSTER="playground-cluster"
   eksctl get nodegroup \
      --cluster ${CLUSTER} \
      --profile ${AWS_PROFILE} \
      --region ${AWS_REGION}
   ```

## Apply Kubernetes `IngressClass` to Cluster
   
1. Apply ingress class to cluster:

    ```shell
    kubectl apply \
      -f ingressclass.yaml \
      --v=8
    ```

## Create Kubernetes Namespace

1. Create a Kubernetes namespace:

    ```shell
    kubectl create namespace ]
      -f namespace.yaml \
      --save-config --v=8
    ```
   
2. Create a Kubernetes secret to access an ECR private registry:

   ```shell
    # Note Rubens Account
    AWS_ACCOUNT="347851559421"
    AWS_REGION="us-east-1"
    AWS_PROFILE="admin"
    NAMESPACE="kube-playground"
    SECRET_NAME="rubens-aws-secret"
    # NOTE: the docker-ussername for ECR authentication is always AWS
    kubectl create secret docker-registry ${SECRET_NAME} \
      --docker-server=${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com \
      --docker-username=AWS \
      --docker-password="$(aws ecr get-login-password --profile ${AWS_PROFILE} --region ${AWS_REGION})" \
      --namespace="${NAMESPACE}"
   ```

## Apply Kubernetes Manifests to Cluster

1. Apply Kubernetes `deployment` manifest to cluster:

    ```shell
    NAMESPACE="kube-playground"
    kubectl apply \
      --namespace=${NAMESPACE} \
      -f deployment.yaml \
      --v=8
    ```

2. Apply Kubernetes `service` manifest to cluster:

    ```shell
    NAMESPACE="kube-playground"
    kubectl apply \
      --namespace=${NAMESPACE} \
      -f service.yaml \
      --v=8
    ```

3. Apply Kubernetes `ingress` manifest to cluster:

    ```shell
    NAMESPACE="kube-playground"
    kubectl apply \
      --namespace=${NAMESPACE} \
      -f ingress.yaml \
      --v=8
    ```

4. Get `ingress` resource for namespace:

   ```shell
   NAMESPACE="kube-playground"
   kubectl get ingress -n ${NAMESPACE}
   ```
   
5. Get all Kubernetes resources used in the `namespace`:

   ```shell
   NAMESPACE="kube-playground"
   kubectl get all -n ${NAMESPACE}
   ```

6. Get details of `service`:

   ```shell
   NAMESPACE="kube-playground"
   SERVICE="service-helloworld-ms"
   kubectl -n  ${NAMESPACE} describe service ${SERVICE}
   ```

7. Get details of `pod`:

   ```shell
   NAMESPACE="kube-playground"
   POD="deployment-helloworld-ms-8598fd76f4-5kn2j"
   kubectl -n  ${NAMESPACE} describe pod ${POD}
   ```

8. Send curl GET request to service:

   ```shell
   ADDRESS="k8s-rubenspl-ingressh-c19bda9fa4-1425542515.us-east-1.elb.amazonaws.com"
   curl --verbose "http://${ADDRESS}/api/v1/helloworld"
   ```

9. Shell into the pod:

   ```shell
   NAMESPACE="kube-playground"
   POD="deployment-helloworld-ms-8598fd76f4-5kn2j"
   kubectl exec -it ${POD} -n ${NAMESPACE} -- /bin/bash
   ```

10. Delete the entire namesapce:

    ```shell
    NAMESPACE="kube-playground"
    kubectl delete namespace ${NAMESPACE} --v=8
    ```
