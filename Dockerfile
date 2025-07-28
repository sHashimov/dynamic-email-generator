# Use lightweight Java 21 runtime
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the jar file from the host machine into the container
COPY build/libs/deg.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
