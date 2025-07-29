# Dynamic Email Generator

A Spring Boot application that dynamically generates email addresses based on user-defined inputs and a custom expression language. It includes API documentation, Docker support, HTTPS reverse proxying, and detailed usage examples.

---

## Features

* Dynamic expression parsing and evaluation
* Multiple expression support per request
* Input validation and error handling
* Dockerized with HTTPS via NGINX reverse proxy
* Swagger API documentation

---

## API Documentation

Swagger UI:

* Local: `http://localhost:8080/swagger-ui.html`
* Docker/NGINX: `https://localhost:9443/swagger-ui.html`

---

## Run Locally

### 1. Build the Application

```bash
./gradlew clean build
```

Ensure `build/libs/deg.jar` is created.

### 2. Start via Docker Compose

```bash
docker-compose up --build
```

---

### Spring Boot App

* Base image: `eclipse-temurin:21-jdk-alpine`
* Exposes port `8080`

### NGINX Reverse Proxy

* Routes HTTPS on port `9443` to the Spring Boot app
* Uses a self-signed certificate (`certs/`)

---

## SSL Setup

To generate a self-signed certificate:

```bash
mkdir certs
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout certs/server.key \
  -out certs/server.crt \
  -subj "/CN=localhost"
```

---

## Expression Language

The application supports a custom expression language for email generation.

### Basic Syntax

* Input variables: `{input1}`, `{input2}`, etc.
* Functions are chained with `|`
* Combine parts with `~`

Example:

```text
{input1|first:1|lower}~'.'~{input2|all|lower}~'@'~{input3|lower}~'.com'
```

### Supported Functions

| Function | Args | Description              |
| -------- | ---- | ------------------------ |
| `first`  | :n   | First n characters       |
| `last`   | :n   | Last n characters        |
| `all`    | —    | Full input               |
| `lower`  | —    | Lowercase transformation |
| `upper`  | —    | Uppercase transformation |

### Multiple Expressions

Send multiple expressions in one request to generate multiple outputs.

Example:

```json
{
  "inputs": {
    "input1": "Jane",
    "input2": "Doe"
  },
  "expressions": [
    "{input1|first:1|lower}~'.'~{input2|all|lower}~'@example.com'",
    "{input1|all|lower}~'.'~{input2|first:2|lower}~'@example.com'"
  ]
}
```

---

## API Endpoints

### POST `/api/v1/generate-email`

Generates email(s) using the given inputs and expressions.

**Request Body**:

```json
{
  "inputs": {
    "input1": "Jane",
    "input2": "Doe"
  },
  "expressions": [
    "{input1|first:1|lower}~'.'~{input2|all|lower}~'@example.com'"
  ]
}
```

**Response**:

```json
{
  "data": [
    {
      "id": "j.doe@example.com",
      "value": "j.doe@example.com"
    }
  ]
}
```

### GET `/api/v1/generate-email`

Generates email using query parameters.

**Query Parameters**:

- `expression` (required): The expression string to evaluate
- `inputN` (optional): Individual input parameters, e.g., `input1=Jane`
- `allParams` (optional): JSON string with inputs, used as a fallback

Example:

```url
/api/v1/generate-email?expression={input1|first:1|lower}~'.'~{input2|all|lower}~'@example.com'&input1=Jane&input2=Doe
```

---

## Errors

* Malformed expressions return 400 with descriptive error messages
* Missing inputs or invalid function names will be flagged accordingly

For full usage details and examples, see [USAGE.md](USAGE.md)

---

## CI Pipeline

GitHub Actions workflow:

* Location: `.github/workflows/ci.yml`
* Triggers on push and PR to `master`
* Steps: checkout, JDK setup, build, test, healthcheck

---

## Postman Collection

To test the API interactively:

1. Open Postman
2. Import the following:
   - `dynamic-email-generator.postman_collection.json`
   - `dev.postman_environment.json`
3. Switch to Dynamic Email Generator - Dev environment
4. In the "Auth - Login" request, click **Send** to generate a JWT token
5. Use the "Generate Email" request to test dynamic expression processing

Make sure your local server is running and accessible at `https://localhost:9443`.

---