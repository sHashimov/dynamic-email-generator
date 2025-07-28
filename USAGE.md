# Dynamic Email Expression Language Guide

This document explains the custom expression language used to dynamically generate email addresses based on user-defined inputs. It covers the syntax, supported functions, complex use cases, and practical application patterns.

---

## Expression Syntax

Expressions are enclosed in curly braces (`{}`) and use **pipe (`|`) operators** to chain transformation functions:

```text
{input1|first:2|lower}
```

You can **combine multiple expressions and literals** using the `~` operator:

```text
{input1|first:1|lower}~'.'~{input2|all|lower}~'@'~{input3|all|lower}~'.com'
```

---

## Available Functions

| Function | Arguments | Description                    | Example   |                 |
| -------- | --------- | ------------------------------ | --------- | --------------- |
| `first`  | `:n`      | Takes the first `n` characters | \`{input1 | first:2}`→`Jo\` |
| `last`   | `:n`      | Takes the last `n` characters  | \`{input2 | last:3}`→`son\` |
| `all`    | none      | Returns the full value         | \`{input3 | all}`→`Galaxy\` |
| `lower`  | none      | Converts to lowercase          | \`{input1 | lower}`→`john\` |
| `upper`  | none      | Converts to uppercase          | \`{input1 | upper}`→`JOHN\` |

---

## Handling Multiple Emails

You can define multiple expressions in one request to generate **several emails**:

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

**Output:**

```json
[
  {"id": "j.doe@example.com", "value": "j.doe@example.com"},
  {"id": "jane.do@example.com", "value": "jane.do@example.com"}
]
```

---

## Practical Examples

### 🔹 Basic Example: First initial and last name

```json
Expression: {input1|first:1|lower}~'.'~{input2|all|lower}~'@company.com'
Inputs: {"input1": "Luke", "input2": "Skywalker"}
Output: l.skywalker@company.com
```

### 🔹 Uppercased version

```json
Expression: {input1|upper}~'.'~{input2|upper}~'@COMPANY.COM'
Output: LUKE.SKYWALKER@COMPANY.COM
```

### 🔹 Dynamic Domain

```json
Expression: {input1|lower}~'.'~{input2|lower}~'@'~{input3|lower}~'.com'
Inputs: input3 = "galaxy"
Output: luke.skywalker@galaxy.com
```

### 🔹 Custom Prefix and Suffix

```json
Expression: 'prefix-'~{input1|first:2|lower}~'-suffix'
Output: prefix-lu-suffix
```

---

## ⚙️ Operational Guidance

### 1. Define your dynamic inputs

Inputs must be named `input1`, `input2`, etc.

```json
{
  "input1": "John",
  "input2": "Smith",
  "input3": "galaxy"
}
```

### 2. Write expressions using syntax

Use `{}` for input references and `|` for function chains. Combine parts with `~`.

```json
"{input1|first:1|lower}~'.'~{input2|all|lower}~'@'~{input3|lower}~'.com'"
```

### 3. Submit the request via API

```http
POST /api/v1/generate-email
Content-Type: application/json

{
  "inputs": { ... },
  "expressions": [ ... ]
}
```

### 4. Receive structured email output

Each expression returns an entry with `id` and `value` fields.

---

## Validation Rules

* Input keys must start with `input` (e.g., `input1`)
* Functions must match supported syntax
* All function arguments must be numeric if required (e.g., `first:2`)
* Malformed or unknown functions will result in a 400 error with a descriptive message

---

## 🧪 Testing Edge Cases

| Scenario           | Expression  | Expected Behavior |                                          |
| ------------------ | ----------- | ----------------- | ---------------------------------------- |
| Missing input      | \`{input999 | all}\`            | Error: "Missing input for key: input999" |
| Malformed function | \`{input1   | first:}\`         | Error: "Malformed function"              |
| Unknown function   | \`{input1   | reverse}\`        | Error: "Unknown no-arg function"         |
| Invalid argument   | \`{input1   | first\:x}\`       | Error: "Invalid argument"                |

---

