# Dynamic Email Generator

A Spring Boot application that dynamically generates email content based on input values and expression logic. Designed to demonstrate REST API capabilities, input validation, Dockerization, reverse proxy integration with NGINX, and API documentation using Swagger.

---

## Tech Stack

* Java 21
* Spring Boot 3.4.5
* Spring Web
* Lombok
* Springdoc OpenAPI 2.8.6
* JUnit 5
* Docker & Docker Compose
* NGINX (Alpine)

---

## Features

* Generate dynamic email content based on user input and expressions
* Validates required query parameters
* Graceful error handling with custom exception responses
* Fully documented API via Swagger UI
* Dockerized backend app with reverse proxy via NGINX

---

## Building the Application

Set custom JAR name in `build.gradle`:

```groovy
bootJar {
    archiveFileName = 'deg.jar'
}
```

Then build the app:

```bash
./gradlew clean build
```

---

## Running with Docker

Build the Docker image:

```bash
docker build -t dynamic-email-generator .
```

Run the container:

```bash
docker run -p 8080:8080 dynamic-email-generator
```

---

## Running with Docker Compose and NGINX

Start all services:

```bash
docker-compose up --build
```

Access:

* API: `http://localhost/api/v1/generate-email`
* Swagger UI: `http://localhost/swagger-ui.html`

---

## API Documentation

* Swagger UI: `http://localhost/swagger-ui.html`
* OpenAPI Spec: `http://localhost/v3/api-docs`

---

## Project Structure

```
src/
├── main/
│   ├── java/com/emailgen/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── service/
│   │   ├── util/
│   │   └── config/
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/emailgen/...
```

---

## Running Tests

To run all tests:

```bash
./gradlew test
```

Tests cover:

* Controller logic
* Expression evaluation
* Input validation
* Exception handling
