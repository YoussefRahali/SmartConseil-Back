# syntax=docker/dockerfile:1.6
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY microservices/microserviceConseil/pom.xml microservices/microserviceConseil/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -pl microservices/microserviceConseil -am dependency:go-offline

COPY microservices/microserviceConseil microservices/microserviceConseil
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -pl microservices/microserviceConseil -am package

FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY --from=build /app/microservices/microserviceConseil/target/*.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
