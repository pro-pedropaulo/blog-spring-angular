FROM ubuntu:latest
LABEL authors="Pedro"

RUN apt-get update
RUN apt-get openjdk-17-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=0 /target/*.jar /app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]

ENTRYPOINT ["top", "-b"]