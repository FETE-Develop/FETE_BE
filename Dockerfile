# Stage 1: Build the application
FROM gradle:7.5.1-jdk17 as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the gradle wrapper and settings files
COPY gradlew .
COPY gradle gradle

# Grant execute permission to the gradlew script
RUN chmod +x gradlew

# Copy the build files
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src src

# Build the application
RUN ./gradlew build --no-daemon

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar file from the builder stage
COPY --from=builder /app/build/libs/be-0.0.1-SNAPSHOT.jar /app/fete-be.jar

# Expose the port that the application will run on
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/fete-be.jar"]
