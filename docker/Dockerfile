# docker/Dockerfile

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy JAR from build context root (which is project root)
COPY build/libs/deg.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
