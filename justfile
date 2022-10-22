#!/usr/bin/env just --justfile
## Licensed under the terms of http://www.apache.org/licenses/LICENSE-2.0

# NOTE: The just recipes defined below assume sdkman is installed and used for java and maven selection.
#       Recipes that utilize docker containers assume the existence of the specific docker image existing locally

export JAVA_VER_DISTRO_11 := "11.0.17-zulu"
export JAVA_VER_DISTRO_17 := "17.0.5-zulu"
export JAVA_VER_DISTRO_19 := "19.0.1-zulu"
export DOCKER_CMD := "docker container run --rm -it"
export VOL_NAME := "apache-jena"
export M2_REPO := "/root/.m2/repository"
export BLD_DIR := "/usr/src/build"
export IMG := "llsem-ubuntu-maven"

default:
  @echo "Invoke just --list to see a list of possible recipes to run"

clean: clean-11

clean-11:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_11}
  mvn clean

clean-17:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_17}
  mvn clean

clean-19:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_19}
  mvn clean

clean-install: clean-install-11

clean-install-11: clean-11
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_11}
  mvn install

clean-install-17: clean-17
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_17}
  mvn install

clean-install-19: clean-19
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_19}
  mvn install

verify: verify-11

verify-11:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_11}
  mvn verify

verify-17:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_17}
  mvn verify

verify-19:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_19}
  mvn verify

dev-verify: dev-verify-11

dev-verify-11:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_11}
  mvn -Pdev verify

dev-verify-17:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_17}
  mvn -Pdev verify

dev-verify-19:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_19}
  mvn -Pdev verify

docker-clean: docker-clean-11

docker-clean-11:
  ${DOCKER_CMD} -v ${VOL_NAME}-11:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:11" mvn clean

docker-clean-17:
  ${DOCKER_CMD} -v ${VOL_NAME}-17:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:17" mvn clean

docker-clean-19:
  ${DOCKER_CMD} -v ${VOL_NAME}-19:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:19" mvn clean

docker-clean-install: docker-clean-install-11

docker-clean-install-11: docker-clean-11
  ${DOCKER_CMD} -v ${VOL_NAME}-11:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:11" mvn install

docker-clean-install-17: docker-clean-17
  ${DOCKER_CMD} -v ${VOL_NAME}-17:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:17" mvn install

docker-clean-install-19: docker-clean-19
  ${DOCKER_CMD} -v ${VOL_NAME}-19:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:19" mvn install

docker-verify: docker-verify-11

docker-verify-11:
  ${DOCKER_CMD} -v ${VOL_NAME}-11:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:11" mvn verify

docker-verify-17:
  ${DOCKER_CMD} -v ${VOL_NAME}-17:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:17" mvn verify

docker-verify-19:
  ${DOCKER_CMD} -v ${VOL_NAME}-19:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:19" mvn verify

docker-dev-verify: docker-dev-verify-11

docker-dev-verify-11:
  ${DOCKER_CMD} -v ${VOL_NAME}-11:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:11" mvn -Pdev verify

docker-dev-verify-17:
  ${DOCKER_CMD} -v ${VOL_NAME}-17:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:17" mvn -Pdev verify

docker-dev-verify-19:
  ${DOCKER_CMD} -v ${VOL_NAME}-19:"${M2_REPO}" -v "$(pwd):${BLD_DIR}" -w ${BLD_DIR} "${IMG}:19" mvn -Pdev verify

dependencies:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_11}
  mvn dependency:tree -Dscope=compile | tee dependencies.txt

updates:
  #!/usr/bin/env bash -l
  sdk use java ${JAVA_VER_DISTRO_11}
  mvn versions:display-dependency-updates | tee updates.txt
