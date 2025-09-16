# build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY microservices/microserviceRapport/pom.xml microservices/microserviceRapport/pom.xml

# Prime dependencies for THIS module only
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -f microservices/microserviceRapport/pom.xml dependency:go-offline

# Copy sources for THIS module only
COPY microservices/microserviceRapport/src microservices/microserviceRapport/src

# Package THIS module only
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -f microservices/microserviceRapport/pom.xml package

# runtime image
FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY --from=build /app/microservices/microserviceRapport/target/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
