# Dynamic Email Generator

A flexible, secure Spring Boot service for generating dynamic email addresses using custom expressions based on user inputs.

---

## 🚀 Features

- ✅ **Custom Expression Language** for building emails dynamically
- 🔐 **JWT Authentication** and **Role-Based Authorization** (Admin/User)
- 🌐 **REST API** documented with Swagger (OpenAPI 3)
- 🐳 **Dockerized Deployment** with NGINX reverse proxy & HTTPS support
- 🔧 **Environment Profiles** for dev and prod
- 📦 Includes Postman collection and test suite

---

## 🛠 Project Structure

```bash
.
├── src/main/java/com/emailgen
│   ├── config          # App configs, JWT, security
│   ├── controller      # REST controllers (auth, email, health)
│   ├── dto             # Request/response DTOs
│   ├── exception       # Custom exceptions & handlers
│   ├── security        # JWT utils, roles, constants
│   ├── service         # Business logic for email/auth
│   └── util            # Expression evaluation engine
├── src/test            # Unit & integration tests
├── docker              # Dockerfile & docker-compose.yml
├── nginx               # SSL certs & nginx.conf
├── postman             # Collections & environment
└── resources           # YAML configs for profiles
```

---

## ⚙️ Setup & Deployment

### 1. 🔧 Generate SSL Certificates

```bash
mkdir -p nginx/certs
openssl req -x509 -nodes -days 365 \
  -newkey rsa:2048 \
  -keyout nginx/certs/selfsigned.key \
  -out nginx/certs/selfsigned.crt \
  -subj "/CN=localhost"
```

### 2. 🐳 Run with Docker Compose

```bash
docker-compose -f docker/docker-compose.yml up --build
```

NGINX exposes HTTPS on: [https://localhost:9443](https://localhost:9443)

Swagger UI: [https://localhost:9443/swagger-ui/index.html](https://localhost:9443/swagger-ui/index.html)

Use `-k` flag with `curl` to ignore self-signed certificate:

```bash
curl -k https://localhost:9443/actuator/health
```

---

## 🔐 Authentication & Authorization

### JWT Token Login

`POST /api/v1/auth/login`

```json
{
  "username": "degadmin",
  "password": "degadmin123"
}
```

Returns:

```json
{
  "token": "<JWT-TOKEN>"
}
```

- Add `Authorization: Bearer <TOKEN>` to secured API requests
- Supports roles: `ADMIN`, `USER`, `GUEST`

Example decoded token payload:

```json
{
  "sub": "degadmin",
  "roles": ["ADMIN"],
  "exp": 1753957317
}
```

---

## 🧪 Testing with Postman

Use included files in `postman/`:

- ✅ `dynamic-email-generator.postman_collection.json`
- 🌐 `dev.postman_environment.json`

---

## 📬 Email Expression Examples

Use `GET /api/v1/generate-email?input1=Jean&input2=Mignard&...&expression=...`

Expression:

```bash
{input1|first:1|lower}~'.'~{input2|last:3|lower}~'@'~{input3|all|lower}~'.'~{input4|all|lower}~'.'~{input5|all|lower}
```

Result:

```json
{
  "data": [
    {
      "id": "j.ard@external.peoplespheres.fr",
      "value": "j.ard@external.peoplespheres.fr"
    }
  ]
}
```

For full expression language details, see [`USAGE.md`](USAGE.md)

---

## 🧱 Git & Branching Strategy

- Follows `feature/DEG-#_description`, `techdebt/DEG-#`, etc.
- Commits use ticket reference, e.g., `DEG-7 Fix expression chaining`
- Uses GitHub Flow with short-lived branches + PRs

---

## 📌 Environment Config

### application.yml

```yaml
spring:
  profiles:
    active: dev
server:
  address: 0.0.0.0
```

### application-dev.yml

```yaml
swagger:
  enabled: true
logging:
  level:
    org.springframework.web: DEBUG
```

### application-prod.yml

```yaml
swagger:
  enabled: false
logging:
  level:
    org.springdoc: ERROR
```

---

## 🧯 Security Best Practices

- ✅ Spring Security with CSRF protection
- ✅ Input validation and error handling
- ✅ JWT-based access control
- ✅ HTTPS enforced via NGINX reverse proxy

---


