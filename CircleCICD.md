# CircleCI

This file describes steps to test and run [CircleCI](https://circleci.com/) jobs
locally on a developer's UNIX computer (e.g., macOS or Linux). In addition, it
describes how to set up CircleCI to run remote CICD builds.

## Local CircleCI Job Run

### Docker Engine

You must have a Docker engine (e.g., Docker Desktop) installed and running.

### CircleCI CLI Personal API Token

1. To run local CircleCI CLI commands, you need to have a `Personal API Token`
   created under your account `User Settings` page.
2. The `Personal API Token` must be configured in your UNIX shell environment
   along with other CircleCI environment variables. For example:

```shell
###############################################################################
# Rubens Gomes Personal CircleCI Account
declare -x CIRCLECI_GIT_USER_EMAIL="rubens.s.gomes@gmail.com"
declare -x CIRCLECI_GIT_USER_NAME="Rubens Gomes"
declare -x CIRCLECI_CLI_TOKEN='<copy and paste personal api token here>'
```

### Install and Configure The CircleCI Local CLI

You must install the CircleCI CLI tools following
these [Install and configure the CircleCI local CLI](https://circleci.com/docs/local-cli/).

### Validate a CircleCI Config

To validate your local `.circleci/config.yml` follow
these [Validate a CircleCI config](https://circleci.com/docs/how-to-use-the-circleci-local-cli/#validate-a-circleci-config).

```shell
circleci config validate .circleci/config.yml
```

### Processing a CircleCI Config

In addition to validating your CircleCI config, you may also display expanded
source configuration as
in [Processing a config](https://circleci.com/docs/how-to-use-the-circleci-local-cli/#processing-a-config).

```shell
circleci config process .circleci/config.yml
```

### Run a Circleci Job in a Container on Your Machine

Once `config.yml` is validated you may execute a job in the `config.yml` as
in [Run a job in a container on your machine](https://circleci.com/docs/how-to-use-the-circleci-local-cli/#run-a-job-in-a-container-on-your-machine).

```shell
# ensure your Docker Engine is running.
JOB_NAME="build-check-job"
circleci local execute "${JOB_NAME}"    \
  -e REPSY_USERNAME="${REPSY_USERNAME}" \
  -e REPSY_PASSWORD="${REPSY_PASSWORD}"
```

```shell
# ensure your Docker Engine is running.
JOB_NAME="sonar-scan-job"
circleci local execute "${JOB_NAME}"    \
  -e REPSY_USERNAME="${REPSY_USERNAME}" \
  -e REPSY_PASSWORD="${REPSY_PASSWORD}" \
  -e SONAR_TOKEN="${SONAR_TOKEN}"
```


## Set Up Remote CircleCI Environment

### Configure `sonarcloud` and `repsy` Contexts

1. Login to CircleCI
2. Select the Organization --> Organization Settings --> Context
3. Create "sonarcloud" SonarQube Sonar Cloud API token
    - Add environment variable "SONAR_TOKEN" and assign it to the generated
      SonarCloud Token
    - [SonarQube Cloud Orb](https://github.com/SonarSource/sonarcloud-circleci-orb/blob/master/README.md#sonarqube-cloud-orb)
4. Create "repsy" Maven repsy.io context
    - Add environment variable "REPSY_USERNAME" and assign it to the
      corresponding repsy.io username
    - Add environment variable "REPSY_PASSWORD" and assign it to the
      corresponding repsy.io password

## CircleCI CLI Commands

Check [circleci](https://circleci-public.github.io/circleci-cli/).

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
