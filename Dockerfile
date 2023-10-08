ARG IMAGE_NAME=wallet_service

ARG IMAGE_VERSION=1.0

FROM openjdk:17-jdk

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE}  walletService.jar

ENTRYPOINT ["java","-jar","walletService.jar"]
