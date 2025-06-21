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


## Clean, lint, test, and assemble

```shell
./gradlew --info clean
```

```shell
./gradlew :app:spotlessApply
```

```shell
./gradlew --info check
```

```shell
./gradlew --info bootJar
```

### Start and stop using bootRun

- Start using "bootRun":

  ```shell
  ./gradlew --info bootRun
  ```

- Stop "bootRun"

  ```shell
  ./gradlew --stop
  ```

- To render the `Hello World!` message:

  ```shell
  curl --verbose "http://localhost:8080/api/v1/helloworld"
  ```

### To create a release

```shell
# only Rubens can release
./gradlew --info release
```

## Public API in AWS ECS

- Ensure cluster is set up
- Ensure task definition is set up (SERVER_PORT=8888)
- Ensure service is up and running
- Ensure security group allows proper inbound traffic to port 8888
- http://<ECS-TASK-PUBLIC_IP>:8888/api/v1/helloworld

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
