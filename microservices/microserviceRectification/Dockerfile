FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# parent POM is still needed because the module likely has <parent> .. </parent>
COPY pom.xml .
COPY microservices/microserviceRectification/pom.xml microservices/microserviceRectification/pom.xml

# Prime the cache only for this module
RUN mvn -q -B -DskipTests \
    -f microservices/microserviceRectification/pom.xml \
    dependency:go-offline

# Now add sources and build the JAR
COPY microservices/microserviceRectification/src microservices/microserviceRectification/src
RUN mvn -q -B -DskipTests \
    -f microservices/microserviceRectification/pom.xml \
    package

FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY --from=build /app/microservices/microserviceRectification/target/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
