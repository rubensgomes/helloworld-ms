# helloworld-ms

A basic Kotlin Spring Boot microservice.

- Requires Java LTS 21 or greater.

## Update `gradlew` to latest gradle version

```shell
# update version when needed
./gradlew wrapper --gradle-version 8.14.2
```

## Display Java tools installed

```shell
./gradlew -q javaToolchains
```

## Install SonarQube for IDE

- This steps assumes you are using `JetBrains IntelliJIDEA`
- [SonarQube Plugin Installation](https://docs.sonarsource.com/sonarqube-for-ide/intellij/getting-started/installation/)

## Clean Docker Containers, Images and System

```shell
# remove all containers
docker container rm -vf $(docker ps -aq)
docker image rm -f $(docker images -aq)
docker system prune -a --volumes
```

## Clean, lint, test, assemble, push

```shell
# clean the build
./gradlew clean -i
```

```shell
# lint and unit tests
./gradlew check -i
```

```shell
# create a layered bootjar
./gradlew bootJar -i
```

```shell
# build and push bootjar to dockerhub
./gradlew bootBuildImage -i
```

```shell
# only Rubens can release
# TODO: release not working. It is not updating the release branch.
./gradlew release -i
```

## Start and stop using docker compose

- Start docker container:

  ```shell
  cd app
  docker compose up --detach --no-recreate --remove-orphans || {
    printf "failed to stop container.\n" >&2
    sleep 5   
  }
  ```

- Stop docker container:

  ```shell
  cd app
  docker compose down --remove-orphans || {
    printf "failed to stop container.\n" >&2
    sleep 5
  }
  ```

- To render the `Hello World!` messasge:

  ```shell
  curl --verbose "http://localhost:8080/api/v1/helloworld"
  ```



### Start and stop using bootRun

```shell
# start bootjar
./gradlew bootRun -i
```

```shell
# stop running bootjar
./gradlew --stop
```

```shell
# render the Hello World! message:
curl --verbose "http://localhost:8080/api/v1/helloworld"
```

## Public API in AWS ECS

- Ensure cluster is set up
- Ensure task definition is set up (SERVER_PORT=8888)
- Ensure service is up and running
- Ensure security group allows proper inbound traffic to port 8888
- http://<ECS-TASK-PUBLIC_IP>:8888/api/v1/helloworld

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
