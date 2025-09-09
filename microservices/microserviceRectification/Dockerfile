FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY microservices/rectification/pom.xml microservices/rectification/pom.xml
RUN mvn -q -B -DskipTests dependency:go-offline
COPY . .
RUN mvn -q -B -DskipTests -pl microservices/rectification -am package

FROM eclipse-temurin:17-jre
WORKDIR /opt/app
ARG JAR=microservices/rectification/target/*.jar
COPY --from=build /app/${JAR} app.jar
EXPOSE 8089
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
