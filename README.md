# 🏦 Loan Banking System

A full-stack backend system for managing loan operations in a banking environment. Built with **Spring Boot**, this system provides secure REST APIs, role-based access control, a loan eligibility rules engine, and robust exception handling — all containerized with Docker.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Loan Eligibility Rules Engine](#loan-eligibility-rules-engine)
- [Role-Based Authentication](#role-based-authentication)
- [Exception Handling](#exception-handling)
- [Docker Setup](#docker-setup)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)

---

## 📖 Overview

The **Loan Banking System** is a backend service designed to handle end-to-end loan lifecycle management — from customer registration and loan application to eligibility checks, approval workflows, and repayment tracking. The system is secured with JWT-based role authentication and backed by a SQL Server database with an optimized schema.

---

## 🛠 Tech Stack

| Technology      | Purpose                          |
|-----------------|----------------------------------|
| Java 17+        | Core language                    |
| Spring Boot 3.x | Application framework            |
| Spring Security | Authentication & Authorization   |
| JWT             | Token-based auth                 |
| REST APIs       | Service communication layer      |
| SQL Server      | Relational database              |
| Hibernate / JPA | ORM layer                        |
| Docker          | Containerization                 |
| Maven           | Build & dependency management    |
| JUnit 5         | Unit & integration testing       |

---

## ✨ Features

- 🔐 **Role-Based Authentication** — Admin, Loan Officer, and Customer roles with JWT
- 📋 **Loan Eligibility Rules Engine** — Configurable rules for credit score, income, and debt ratio
- 🧾 **Loan Application Management** — Apply, review, approve, or reject loans
- 🏗 **Optimized Database Schema** — Normalized tables for customers, loans, repayments, and audit logs
- ✅ **Backend Validations** — Field-level and business-level validations on all inputs
- ⚠️ **Global Exception Handling** — Centralized error responses with meaningful messages
- 🐳 **Dockerized** — Fully containerized for easy deployment
- 📄 **Audit Logging** — Track all state changes with timestamps

---

## 📁 Project Structure

```
loan-banking-system/
├── src/
│   ├── main/
│   │   ├── java/com/banking/loan/
│   │   │   ├── config/               # Security, JWT, and app configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   └── AppConfig.java
│   │   │   ├── controller/           # REST API controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CustomerController.java
│   │   │   │   ├── LoanController.java
│   │   │   │   └── AdminController.java
│   │   │   ├── service/              # Business logic layer
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── CustomerService.java
│   │   │   │   ├── LoanService.java
│   │   │   │   └── EligibilityService.java
│   │   │   ├── repository/           # JPA repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   └── LoanRepository.java
│   │   │   ├── model/                # JPA entity classes
│   │   │   │   ├── User.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Loan.java
│   │   │   │   ├── LoanRepayment.java
│   │   │   │   └── AuditLog.java
│   │   │   ├── dto/                  # Request & Response DTOs
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── exception/            # Custom exceptions & global handler
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── LoanNotFoundException.java
│   │   │   │   ├── EligibilityException.java
│   │   │   │   └── UnauthorizedException.java
│   │   │   ├── rules/                # Loan eligibility rules engine
│   │   │   │   ├── EligibilityRule.java
│   │   │   │   ├── CreditScoreRule.java
│   │   │   │   ├── IncomeRule.java
│   │   │   │   └── DebtToIncomeRule.java
│   │   │   └── LoanBankingApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
├   ├       ├── application-stage.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/com/banking/loan/
│           ├── service/
│           ├── controller/
│           └── rules/
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── scripts/
│   └── init-db.sql                   # Initial DB schema & seed data
├── .env.example
├── pom.xml
└── README.md
```

---

## 🗄 Database Schema

### Core Tables

```sql
-- Users (Authentication)
CREATE TABLE users (
    id           BIGINT PRIMARY KEY IDENTITY,
    username     VARCHAR(100) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(50)  NOT NULL,   -- ADMIN | LOAN_OFFICER | CUSTOMER
    is_active    BIT DEFAULT 1,
    created_at   DATETIME DEFAULT GETDATE()
);

-- Customers
CREATE TABLE customers (
    id               BIGINT PRIMARY KEY IDENTITY,
    user_id          BIGINT FOREIGN KEY REFERENCES users(id),
    full_name        VARCHAR(200) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    phone            VARCHAR(20),
    date_of_birth    DATE,
    annual_income    DECIMAL(18,2),
    credit_score     INT,
    employment_type  VARCHAR(50),   -- SALARIED | SELF_EMPLOYED | UNEMPLOYED
    created_at       DATETIME DEFAULT GETDATE()
);

-- Loans
CREATE TABLE loans (
    id               BIGINT PRIMARY KEY IDENTITY,
    customer_id      BIGINT FOREIGN KEY REFERENCES customers(id),
    loan_type        VARCHAR(50)    NOT NULL,  -- HOME | PERSONAL | VEHICLE | EDUCATION
    principal_amount DECIMAL(18,2)  NOT NULL,
    interest_rate    DECIMAL(5,2)   NOT NULL,
    tenure_months    INT            NOT NULL,
    status           VARCHAR(50)    NOT NULL,  -- PENDING | APPROVED | REJECTED | DISBURSED | CLOSED
    applied_at       DATETIME DEFAULT GETDATE(),
    approved_by      BIGINT FOREIGN KEY REFERENCES users(id),
    approved_at      DATETIME,
    remarks          VARCHAR(500)
);

-- Loan Repayments
CREATE TABLE loan_repayments (
    id              BIGINT PRIMARY KEY IDENTITY,
    loan_id         BIGINT FOREIGN KEY REFERENCES loans(id),
    emi_amount      DECIMAL(18,2) NOT NULL,
    due_date        DATE NOT NULL,
    paid_date       DATE,
    status          VARCHAR(30) NOT NULL,   -- PENDING | PAID | OVERDUE
    payment_method  VARCHAR(50)
);

-- Audit Logs
CREATE TABLE audit_logs (
    id           BIGINT PRIMARY KEY IDENTITY,
    entity_type  VARCHAR(100),
    entity_id    BIGINT,
    action       VARCHAR(50),
    performed_by BIGINT FOREIGN KEY REFERENCES users(id),
    old_value    NVARCHAR(MAX),
    new_value    NVARCHAR(MAX),
    timestamp    DATETIME DEFAULT GETDATE()
);
```

---

## 🌐 API Endpoints

### Auth APIs

| Method | Endpoint               | Access     | Description              |
|--------|------------------------|------------|--------------------------|
| POST   | `/api/auth/register`   | Public     | Register a new user      |
| POST   | `/api/auth/login`      | Public     | Login and get JWT token  |
| POST   | `/api/auth/refresh`    | Authenticated | Refresh JWT token     |

### Customer APIs

| Method | Endpoint                    | Access              | Description                 |
|--------|-----------------------------|---------------------|-----------------------------|
| GET    | `/api/customers`            | ADMIN, LOAN_OFFICER | Get all customers           |
| GET    | `/api/customers/{id}`       | ADMIN, LOAN_OFFICER | Get customer by ID          |
| POST   | `/api/customers`            | ADMIN               | Create customer profile     |
| PUT    | `/api/customers/{id}`       | ADMIN               | Update customer details     |
| DELETE | `/api/customers/{id}`       | ADMIN               | Deactivate a customer       |

### Loan APIs

| Method | Endpoint                         | Access              | Description                       |
|--------|----------------------------------|---------------------|-----------------------------------|
| POST   | `/api/loans/apply`               | CUSTOMER            | Apply for a loan                  |
| GET    | `/api/loans/{id}`                | ALL                 | Get loan details                  |
| GET    | `/api/loans/customer/{customerId}` | ALL               | Get all loans for a customer      |
| PUT    | `/api/loans/{id}/approve`        | LOAN_OFFICER, ADMIN | Approve a loan                    |
| PUT    | `/api/loans/{id}/reject`         | LOAN_OFFICER, ADMIN | Reject a loan                     |
| GET    | `/api/loans/{id}/eligibility`    | CUSTOMER            | Check loan eligibility            |
| GET    | `/api/loans/{id}/repayments`     | ALL                 | Get repayment schedule            |

### Admin APIs

| Method | Endpoint                    | Access | Description              |
|--------|-----------------------------|--------|--------------------------|
| GET    | `/api/admin/users`          | ADMIN  | Get all users            |
| PUT    | `/api/admin/users/{id}/role`| ADMIN  | Update user role         |
| GET    | `/api/admin/audit-logs`     | ADMIN  | View all audit logs      |

---

## ⚙️ Loan Eligibility Rules Engine

The rules engine evaluates loan applications using a **chain of responsibility** pattern. Each rule is independently configurable and returns a pass/fail decision with a reason.

### Default Rules

| Rule                  | Criteria                                                    |
|-----------------------|-------------------------------------------------------------|
| **Credit Score Rule** | Credit score must be ≥ 650                                  |
| **Income Rule**       | Annual income must meet minimum threshold per loan type     |
| **Debt-to-Income Rule** | Existing debt obligations must be < 40% of monthly income |
| **Employment Rule**   | Applicant must be employed (salaried or self-employed)      |
| **Age Rule**          | Applicant must be between 21 and 65 years of age            |

### Adding a Custom Rule

Implement the `EligibilityRule` interface:

```java
public interface EligibilityRule {
    EligibilityResult evaluate(Customer customer, LoanApplication application);
}

// Example
@Component
public class CreditScoreRule implements EligibilityRule {

    @Override
    public EligibilityResult evaluate(Customer customer, LoanApplication application) {
        if (customer.getCreditScore() < 650) {
            return EligibilityResult.fail("Credit score below minimum threshold of 650.");
        }
        return EligibilityResult.pass();
    }
}
```

Register it in `EligibilityService` and it is automatically included in the evaluation chain.

---

## 🔐 Role-Based Authentication

The system uses **JWT tokens** with Spring Security to protect all endpoints.

### Roles

| Role           | Permissions                                              |
|----------------|----------------------------------------------------------|
| `ADMIN`        | Full access — manage users, loans, customers, audit logs |
| `LOAN_OFFICER` | Review and approve/reject loan applications              |
| `CUSTOMER`     | Apply for loans, view own loan status and repayments     |

### JWT Flow

```
1. Client sends POST /api/auth/login with credentials
2. Server validates credentials and returns { accessToken, refreshToken }
3. Client sends Authorization: Bearer <accessToken> on every subsequent request
4. Server validates token and extracts role from claims
5. Spring Security enforces role-based access on each endpoint
```

---

## ⚠️ Exception Handling

All exceptions are handled globally via `GlobalExceptionHandler` using `@RestControllerAdvice`.

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Annual income does not meet the minimum requirement for this loan type.",
  "path": "/api/loans/apply"
}
```

### Custom Exceptions

| Exception                  | HTTP Status | Trigger                                |
|----------------------------|-------------|----------------------------------------|
| `LoanNotFoundException`    | 404         | Loan ID does not exist                 |
| `EligibilityException`     | 422         | Loan eligibility check fails           |
| `UnauthorizedException`    | 403         | Role not permitted to perform action   |
| `CustomerNotFoundException`| 404         | Customer ID does not exist             |
| `ValidationException`      | 400         | Input field validation failure         |
| `DuplicateResourceException` | 409       | Duplicate email or username            |

---

## 🐳 Docker Setup

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/loan-banking-system-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:sqlserver://sqlserver:1433;databaseName=loandb
      - SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - sqlserver

  sqlserver:
    image: mcr.microsoft.com/mssql/server:2022-latest
    environment:
      SA_PASSWORD: ${DB_PASSWORD}
      ACCEPT_EULA: Y
    ports:
      - "1433:1433"
    volumes:
      - sqlserver_data:/var/opt/mssql

volumes:
  sqlserver_data:
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- SQL Server (or use Docker)

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/loan-banking-system.git
cd loan-banking-system
```

### 2. Configure Environment

```bash
cp .env.example .env
# Edit .env with your DB credentials and JWT secret
```

### 3. Build the Application

```bash
mvn clean package -DskipTests
```

### 4. Run with Docker Compose

```bash
docker-compose up --build
```

### 5. Run Locally (without Docker)

```bash
# Start SQL Server separately, then:
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at `http://localhost:8080`.

---

## 🔧 Environment Variables

| Variable                     | Description                        | Example                          |
|------------------------------|------------------------------------|----------------------------------|
| `DB_URL`                     | SQL Server JDBC URL                | `jdbc:sqlserver://localhost:1433`|
| `DB_NAME`                    | Database name                      | `loandb`                         |
| `DB_USERNAME`                | Database username                  | `sa`                             |
| `DB_PASSWORD`                | Database password                  | `StrongPass@123`                 |
| `JWT_SECRET`                 | JWT signing secret (min 256-bit)   | `your-secret-key`                |
| `JWT_EXPIRY_MS`              | Access token expiry in ms          | `86400000` (24 hours)            |
| `JWT_REFRESH_EXPIRY_MS`      | Refresh token expiry in ms         | `604800000` (7 days)             |
| `SERVER_PORT`                | App server port                    | `8080`                           |

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest="*ServiceTest,*RuleTest"

# Run with coverage report
mvn verify
# Report available at: target/site/jacoco/index.html
```

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/loan-prepayment`
3. Commit your changes: `git commit -m 'Add loan prepayment feature'`
4. Push to the branch: `git push origin feature/loan-prepayment`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

> Built using Spring Boot · REST APIs · SQL Server · Docker