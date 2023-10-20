ARG IMAGE_NAME=wallet_service
ARG IMAGE_VERSION=1.0

FROM openjdk:17-jdk

RUN mkdir /app
RUN curl -o /app/postgresql-connector-java-42.6.0.jar https://jdbc.postgresql.org/download/postgresql-42.6.0.jar

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} /app/walletService.jar

ENTRYPOINT ["java", "-cp", "/app/walletService.jar:/app/postgresql-connector-java-42.6.0.jar", "walletService.Main"]
