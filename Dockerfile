# Stage 1: Build the application
FROM gradle:7.5.1-jdk17 as builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle

RUN chmod +x gradlew

COPY build.gradle .
COPY settings.gradle .

COPY src src

# Copy the desired yml file
COPY src/main/resources/application-docker.yml src/main/resources/application.yml

# Build the application
RUN ./gradlew build --no-daemon -x test

# Stage 2: Run the application
#FROM openjdk:17-jdk-slim
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/be-0.0.1-SNAPSHOT.jar /app/fete-be.jar
COPY --from=builder /app/src/main/resources/application-docker.yml /app/application-docker.yml

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "/app/fete-be.jar"]
