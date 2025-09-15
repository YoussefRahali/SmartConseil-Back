# syntax=docker/dockerfile:1.6
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1) Amorce Maven avec les POM nécessaires (couche cacheable)
COPY pom.xml .
COPY microservices/microserviceRectification/pom.xml microservices/microserviceRectification/pom.xml

# 2) Pré-télécharger les deps du module uniquement (cache Maven BuildKit)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -pl microservices/microserviceRectification -am dependency:go-offline

# 3) Copier le code du module (pas tout le repo) puis builder ce module
COPY microservices/microserviceRectification microservices/microserviceRectification
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests -pl microservices/microserviceRectification -am package

# 4) Image d'exécution minimale
FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY --from=build /app/microservices/microserviceRectification/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
