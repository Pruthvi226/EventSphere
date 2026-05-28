# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp dependency:go-offline

COPY src ./src
ARG SKIP_TESTS=true
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -DskipTests=${SKIP_TESTS} package \
    && cp target/eventsphere-*.jar app.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

ENV SERVER_PORT=8080 \
    SERVER_SERVLET_CONTEXT_PATH=/ \
    JAVA_OPTS="" \
    TZ=UTC

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN groupadd --system eventsphere \
    && useradd --system --gid eventsphere --home-dir /app eventsphere \
    && mkdir -p /app/uploads \
    && chown -R eventsphere:eventsphere /app

COPY --from=build --chown=eventsphere:eventsphere /workspace/app.jar /app/app.jar

USER eventsphere
EXPOSE 8080
VOLUME ["/app/uploads"]

# Healthcheck follows the configured servlet context path and port.
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 CMD ctx="${SERVER_SERVLET_CONTEXT_PATH:-/}"; port="${SERVER_PORT:-8080}"; if [ "$ctx" = "/" ]; then path="/"; else path="$ctx/"; fi; curl -fsS "http://localhost:${port}${path}" || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
