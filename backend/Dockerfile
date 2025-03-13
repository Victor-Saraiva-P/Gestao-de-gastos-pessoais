FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y
COPY . .

RUN apt-get update && apt-get install -y maven
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk

EXPOSE 8080

COPY --from=build /target/gestorfinanceiro-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]