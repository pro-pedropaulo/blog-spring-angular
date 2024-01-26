FROM ubuntu:latest AS build
LABEL authors="Pedro"

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
RUN apt-get install maven -y
COPY api/blog /app

WORKDIR /app

RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=0 /app/target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]