# Use lightweight Java 21 runtime
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/deg.jar app.jar

# Expose the application port internally
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
