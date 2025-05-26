# ─── Build stage ──────────────────────────────────────────────────────────────
FROM openjdk:21-jdk-slim AS builder

WORKDIR /builder
COPY . .
RUN ./gradlew build --stacktrace


# ─── Final runtime stage ──────────────────────────────────────────────────────
FROM openjdk:21-jdk-slim

# 1) Install the OS CA bundle (including ISRG Root X1)…
# 2) …and the ca-certificates-java helper so it populates Java's cacerts from it.
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      ca-certificates \
      ca-certificates-java \
 && update-ca-certificates \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /builder/build/libs/music-release-notifier-core-0.0.1-SNAPSHOT.jar ./server.jar

EXPOSE 80
CMD ["java", "-jar", "server.jar"]
