# 📘 USAGE.md — Dynamic Email Generator

This document explains how to use the **Dynamic Email Generator** API, especially the **expression language** that transforms user-defined inputs into email addresses.

---

## 📥 API Endpoints

### 🔐 Authenticate and Get JWT

`POST /api/v1/auth/login`

**Request:**

```json
{
  "username": "degadmin",
  "password": "degadmin123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Add this token to future requests:

```
Authorization: Bearer <JWT-TOKEN>
```

---

### ✉️ Generate Email via GET

`GET /api/v1/generate-email?input1=...&input2=...&expression=...`

### ✉️ Generate Email via POST

`POST /api/v1/generate-email`

**Request body:**

```json
{
  "inputs": {
    "input1": "Jean",
    "input2": "Mignard",
    "input3": "external",
    "input4": "peoplespheres",
    "input5": "fr"
  },
  "expression": "{input1|first:1|lower}~'.'~{input2|last:3|lower}~'@'~{input3|all|lower}~'.'~{input4|all|lower}~'.'~{input5|all|lower}"
}
```

**Response:**

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

---

## 🧮 Expression Syntax Guide

### 🌤️ Input Reference

Use inputs dynamically with `{inputN}`, where N is any positive number.

### 🔧 Supported Functions

| Function  | Syntax    | Description |                      |
| --------- | --------- | ----------- | -------------------- |
| `first:N` | \`{input1 | first:2}\`  | First N characters   |
| `last:N`  | \`{input2 | last:3}\`   | Last N characters    |
| `all`     | \`{input3 | all}\`      | Entire string        |
| `lower`   | \`{input4 | lower}\`    | Convert to lowercase |
| `upper`   | \`{input5 | upper}\`    | Convert to uppercase |

### 🔗 Concatenation

Use `~` as a **concatenation operator**. You can include literal strings in single quotes:

```bash
{input1|first:1|lower}~'.'~{input2|last:3|lower}~'@'~{input3|all|lower}~'.'~{input4|all|lower}
```

---

## ✅ More Examples

### Basic initials:

```bash
{input1|first:1|lower}~{input2|first:1|lower}~'@domain.com'
```

**Inputs:**

- input1: Jean
- input2: Mignard

**Result:** `jm@domain.com`

---

### Handle long domain names:

```bash
{input1|lower}~'@'~{input4|all|lower}~'.com'
```

**Inputs:**

- input1: support
- input4: peoplespheres

**Result:** `support@peoplespheres.com`

---

### Full Dynamic Example

```bash
{input1|first:1|lower}~'.'~{input9|last:3|lower}~'@'~{input7|all|lower}~'.'~{input4|all|lower}~'.'~{input11|all|lower}
```

**Inputs:**

- input1: Han
- input9: Solo
- input7: internal
- input4: peoplespheres
- input11: io

**Result:** `h.olo@internal.peoplespheres.io`

---

## 🤔 Notes

- If a required input (like `input1`) is missing, you'll get a validation error.
- The order and naming of input keys (`input1`, `input2`, etc.) matter.
- Expressions must follow the defined syntax strictly.

---

## 📖 Related Resources

- [Swagger UI](https://localhost:9443/swagger-ui/index.html)
- [README.md](README.md)
- Postman Collection: `postman/dynamic-email-generator.postman_collection.json`
- Environment File: `postman/dev.postman_environment.json`

