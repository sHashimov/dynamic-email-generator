# Dynamic Email Generator

A Spring Boot application that dynamically generates email strings based on user-provided inputs and expression syntax. This repository demonstrates:

* Expression parsing and transformation
* Swagger API documentation
* Input validation
* Dockerization using Eclipse Temurin
* Reverse proxying with NGINX and HTTPS support

---

## 📦 Features

* API to evaluate dynamic email expressions
* Expression chaining and input key referencing
* Detailed validation and error responses
* Fully containerized with Docker and Docker Compose
* Exposed via HTTPS using self-signed certificate

---

## 🧪 API Documentation

Swagger UI is available at:

* Local: `http://localhost:8080/swagger-ui.html`
* Docker/NGINX: `https://localhost:9443/swagger-ui.html`

---

## 🐳 Dockerized Architecture

This project includes a `docker-compose.yaml` file that orchestrates two services:

### 1. **Spring Boot App (Eclipse Temurin)**

* **Base image**: `eclipse-temurin:21-jdk-alpine`
* Runs the application JAR (`deg.jar`)
* Exposes port `8080` internally

### 2. **NGINX Server**

* **Image**: `nginx:latest`
* Routes incoming HTTPS (port 9443) traffic to the Spring Boot app
* SSL termination using self-signed certificate
* Includes error handling via `custom_502.html`

---

## 🔒 SSL Certificate

A self-signed certificate is generated for secure HTTPS access:

* Located in `certs/` directory
* Mounted into the NGINX container
* Instructions to generate:

```bash
mkdir certs
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout certs/server.key \
  -out certs/server.crt \
  -subj "/CN=localhost"
```

---

## 🛠️ Run Locally

### 1. **Build the Application**

```bash
./gradlew clean build
```

Ensure `build/libs/deg.jar` is created.

### 2. **Start via Docker Compose**

```bash
docker-compose up --build
```

Access the API at `https://localhost:9443`

---

## 📁 Project Structure

```
.
├── Dockerfile
├── docker-compose.yml
├── nginx.conf
├── certs/
│   ├── server.crt
│   └── server.key
└── build/libs/deg.jar
```

---

## ✅ Covered Technical Requirements

| Requirement                               | Status |
| ----------------------------------------- | ------ |
| Temurin-based Docker container            | ✅      |
| NGINX reverse proxy container             | ✅      |
| HTTPS on port 9443                        | ✅      |
| Self-signed SSL setup                     | ✅      |
| Docker Compose orchestration              | ✅      |
| NGINX error handling (e.g., 502 fallback) | ✅      |

---

## 📌 Notes

* Swagger annotations like `@Operation` are used for endpoint documentation
* Inputs must be named `input1`, `input2`, ... and passed as query params
* `expression` is a required parameter (e.g., `input1.firstChars(3)~"_test"`)

---

## 📬 Contact

Created as part of a technical challenge. For questions, open an issue.
