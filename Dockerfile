# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system eventsphere \
    && useradd --system --gid eventsphere --home-dir /app eventsphere \
    && mkdir -p /app/uploads \
    && chown -R eventsphere:eventsphere /app

COPY --from=build --chown=eventsphere:eventsphere /workspace/target/eventsphere-1.0.0.jar /app/app.jar

USER eventsphere
EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
