# syntax=docker/dockerfile:1

# ---- build stage -----------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS build

ARG MODULE
WORKDIR /workspace

COPY . .
RUN mvn -B -q -pl "${MODULE}" -am package -DskipTests

# ---- runtime stage ---------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

ARG MODULE
ENV JAVA_OPTS=""
WORKDIR /app

COPY --from=build "/workspace/${MODULE}/target/${MODULE}-0.0.1-SNAPSHOT.jar" app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
