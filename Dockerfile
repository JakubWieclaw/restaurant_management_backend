# Use a base image with Java
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application.properties file
COPY src/main/resources/application.properties /app/application.properties

# Copy the jar file
COPY target/restaurant_management_backend-0.0.1-SNAPSHOT.jar /app/my-spring-boot-app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application with the correct log configuration
ENTRYPOINT ["java", "-jar", "/app/my-spring-boot-app.jar", "--spring.config.location=/app/application.properties"]
