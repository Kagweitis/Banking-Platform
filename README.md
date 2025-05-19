# 💳 Banking Platform Microservices

A Java Spring Boot-based microservices architecture for managing banking customer data, accounts, and card services. This project is a simplified banking platform designed to demonstrate modular service design, RESTful APIs, and secure data handling for card details.

## 🧩 Microservices Overview

The system consists of three core services:

### 1. Customer Service
Manages customer biodata.

- `customerId` (UUID): Unique customer identifier
- `firstName` (String): Required
- `lastName` (String): Required
- `otherName` (String): Optional

### 2. Account Service
Handles account management and links accounts to customers.

- `accountId` (UUID): Unique account identifier
- `iban` (String)
- `bicSwift` (String)
- `customerId` (UUID): Foreign key reference to a Customer

> 🧠 Each customer can have multiple accounts. Accounts are exclusive to one customer.

### 3. Card Service
Handles cards tied to accounts.

- `cardId` (UUID): Unique card identifier
- `cardAlias` (String): Editable
- `accountId` (UUID): Account the card is linked to
- `cardType` (Enum): `VIRTUAL` or `PHYSICAL`
- `pan` (String): Primary Account Number (masked by default)
- `cvv` (String): 3-digit security code (masked by default)

> 🔒 Each account can have a maximum of **2 cards**, only one of each type (virtual/physical).

---

## 🛠️ Features

- 🔧 Full CRUD for Customers, Accounts, and Cards
- 🔍 Filterable and paginated search APIs:
    - **Customer Service:** filter by name (full-text), created date range
    - **Account Service:** filter by IBAN, BICSwift, and card alias
    - **Card Service:** filter by card alias, type, and PAN
- 🛡️ Sensitive data masking for PAN and CVV by default
    - Use `?overrideMasking=true` to override
- ✅ Unit testing with **JUnit 5**
- 🗄️ PostgreSQL with isolated DB users per microservice

- 📦 Buildable using **Maven**

---

## ⚙️ Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Maven
- JUnit 5 + Mockito
- Lombok
- SLF4J + Logback for logging

---

## 🚀 Running the Application

### Prerequisites

- Java 21+
- Maven 3.6+
- Docker (optional for PostgreSQL setup)

## 🔐 Database Configuration Per Microservice

Each microservice connects to the **same PostgreSQL database instance** but uses a separate schema and a dedicated database user for security and isolation.

| Microservice      | Database User      | Schema Name |
|-------------------|--------------------|-------------|
| `CustomerService` | `customer_service` | `public`    |
| `AccountService`  | `account_service`  | `public`    |
| `CardService`     | `card_service`     | `public`    |

Each service has its own set of tables within its schema and is granted access only to that schema.

### 🧪 Sample SQL Setup

```sql
-- Create database
CREATE DATABASE bankdb;

-- Create users and schemas
CREATE USER customer_user WITH PASSWORD 'customer_pass';
CREATE USER account_user WITH PASSWORD 'account_pass';
CREATE USER card_user WITH PASSWORD 'card_pass';


-- Connect to the individual databases and Grant privileges
GRANT USAGE ON SCHEMA public TO customer_user;
GRANT USAGE ON SCHEMA public TO account_user;
GRANT USAGE ON SCHEMA public TO card_user;
```

## 🚀 Build & Run

### Build the project
#### Navigate to each project's root directory and run the command below
```bash
mvn clean install
```
### Run each microservice
Navigate into each microservice directory and run:
```bash
cd <Service Folder>
mvn spring-boot:run
```
