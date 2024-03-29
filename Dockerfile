FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:21-jdk-slim

EXPOSE 8080

COPY --from=build /target/app-rdp-tg-bot-0.0.1-SNAPSHOT.jar app.jar
COPY users.json .
COPY servers.json .
ENTRYPOINT [ "java", "-jar", "app.jar" ]
