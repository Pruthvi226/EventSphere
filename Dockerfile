# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp dependency:go-offline

COPY src ./src
ARG SKIP_TESTS=true
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -DskipTests=${SKIP_TESTS} package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN groupadd --system eventsphere \
    && useradd --system --gid eventsphere --home-dir /app eventsphere \
    && mkdir -p /app/uploads \
    && chown -R eventsphere:eventsphere /app

COPY --from=build --chown=eventsphere:eventsphere /workspace/target/eventsphere-1.0.0.jar /app/app.jar

USER eventsphere
EXPOSE 8080
VOLUME ["/app/uploads"]

# Healthcheck to ensure the app is running with or without a servlet context path.
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 CMD sh -c 'curl -fsS "http://localhost:8080${SERVER_SERVLET_CONTEXT_PATH:-/eventsphere}/" || curl -fsS http://localhost:8080/ || exit 1'

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
