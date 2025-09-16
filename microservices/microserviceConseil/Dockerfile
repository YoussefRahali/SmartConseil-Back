# builder
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the POMs needed to resolve deps
COPY pom.xml .
COPY microservices/microserviceConseil/pom.xml microservices/microserviceConseil/pom.xml

# Prime the Maven cache for this module only
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -f microservices/microserviceConseil/pom.xml dependency:go-offline

# Now copy sources for this module and package
COPY microservices/microserviceConseil/src microservices/microserviceConseil/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -f microservices/microserviceConseil/pom.xml package

# runtime
FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY --from=build /app/microservices/microserviceConseil/target/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
