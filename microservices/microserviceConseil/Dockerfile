FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY microservices/microserviceConseil/pom.xml microservices/microserviceConseil/pom.xml
RUN mvn -q -B -DskipTests dependency:go-offline
COPY . .
RUN mvn -q -B -DskipTests -pl microservices/microserviceConseil -am package

FROM eclipse-temurin:17-jre
WORKDIR /opt/app
ARG JAR=microservices/microserviceConseil/target/*.jar
COPY --from=build /app/${JAR} app.jar
EXPOSE 8091
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
