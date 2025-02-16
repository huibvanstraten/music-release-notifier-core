# Build stage
FROM openjdk:23-jdk-slim as builder

WORKDIR /builder
COPY . .

RUN ./gradlew build --stacktrace


# # Final runtime stage
FROM openjdk:24-ea-21-slim

WORKDIR /app
COPY --from=builder /builder/build/libs/music-release-notifier-core-0.0.1-SNAPSHOT.jar ./server.jar
CMD ["java", "-jar", "server.jar"]

EXPOSE 80
