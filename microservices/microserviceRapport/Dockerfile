# 1) Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY microservices/microserviceRapport/pom.xml microservices/microserviceRapport/pom.xml
RUN mvn -q -B -DskipTests dependency:go-offline
COPY . .
RUN mvn -q -B -DskipTests -pl microservices/microserviceRapport -am package

# 2) Runtime
FROM eclipse-temurin:17-jre
WORKDIR /opt/app
ARG JAR=microservices/microserviceRapport/target/*.jar
COPY --from=build /app/${JAR} app.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
