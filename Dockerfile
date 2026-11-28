# Stage 1: Build Spring Boot application
FROM docker.io/library/eclipse-temurin:21-jdk AS builder

#RUN invalid-command-for-demo

WORKDIR /app

COPY . .

RUN chmod +x gradlew && \
    ./gradlew clean bootJar --no-daemon

# Stage 2: Runtime image
FROM docker.io/library/eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]