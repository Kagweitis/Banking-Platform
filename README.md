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

## 🧾 API Interaction Guide

### Swagger Docs
- Once the services are running access swagger docs 
  [here](http://localhost:8080/webjars/swagger-ui/index.html)


# Bank Platform API Interaction Guide

The Bank Platform API allows for the management of customer information, bank accounts, and associated cards. This guide outlines how to interact with the API using the provided endpoints for common operations like creating, updating, retrieving, and deleting records.

**Base URL:** `{{base_url}}`  (You will need to configure this variable in your environment in Postman. Its the Gateway url http://localhost:8080)

## 1. Customer Endpoints

These endpoints manage customer records.

### 1.1 Create Customer
* **Endpoint:** `POST /customers/api/v1/create/customer`
* **Description:** Creates a new customer record.
* **Request Body (JSON):**

    ```json
    {
        "firstName": "George",
        "lastName": "Bezs"
    }
    ```
* **Example Response:** (Successful creation)

    ```json
    {
        "message": "Customer created successfully",
        "status": "CREATED"
    }
    ```

### 1.2 Update Customer
* **Endpoint:** `PATCH /customers/api/v1/update/customer`
* **Description:** Updates an existing customer record.
* **Request Body (JSON):**

    ```json
    {
        "customerId": "57b8a84a-fc0a-414e-b8c0-6a61b2b0cff0",
        "firstName": "James",
        "lastName": "Bezos",
        "otherName": "AwS"
    }
    ```
* **Example Response:** (Successful update)

    ```json
    {
        "message": "Customer updated successfully",
        "status": "OK"
    }
    ```

### 1.3 Get Customer By Id
* **Endpoint:** `GET /customers/api/v1/get/customer/{customerId}`
* **Description:** Retrieves a customer record by their ID.
* **Example Request:** `GET /customers/api/v1/get/customer/57b8a84a-fc0a-414e-b8c0-6a61b2b0cff0`
* **Example Response:** (Successful retrieval)

    ```json
    {
        "customerId": "57b8a84a-fc0a-414e-b8c0-6a61b2b0cff0",
        "firstName": "James",
        "lastName": "Bezos",
        "otherName": "AwS",
        "createdAt": "2025-05-17T10:00:00Z"
    }
    ```

### 1.4 Delete Customer By Id
* **Endpoint:** `PATCH /customers/api/v1/delete/customer/{customerId}`
* **Description:** Marks a customer record as deleted (soft delete).
* **Example Request:** `PATCH /customers/api/v1/delete/customer/57b8a84a-fc0a-414e-b8c0-6a61b2b0cff0`
* **Example Response:** (Successful deletion)

    ```json
    {
        "message": "Customer deleted successfully",
        "status": "OK"
    }
    ```

### 1.5 Get Customers By Keyword and Date Range
* **Endpoint:** `GET /customers/api/v1/get/customers?name={keyword}&startDate={startDate}&endDate={endDate}`
* **Description:** Searches for customer records by a keyword in their name and within a specified date range.
* **Example Request:** `GET /customers/api/v1/get/customers?name=J&startDate=2025-05-17T00:00:00&endDate=2025-05-18T00:00:00`
* **Example Response:**

    ```json
    [
        {
            "customerId": "some-customer-id-1",
            "firstName": "John",
            "lastName": "Doe",
            "createdAt": "2025-05-17T12:00:00Z"
        },
        {
            "customerId": "some-customer-id-2",
            "firstName": "Jane",
            "lastName": "Smith",
            "createdAt": "2025-05-17T15:30:00Z"
        }
    ]
    ```

## 2. Account Endpoints

These endpoints manage bank accounts associated with customers.

### 2.1 Create New Account
* **Endpoint:** `POST /account/api/v1/create/account`
* **Description:** Creates a new bank account for a customer.
* **Request Body (JSON):**

    ```json
    {
      "customerId": "4bbc5153-34fe-40eb-8044-6957f7ba0036",
      "iban": "DE89370400440532013000",
      "bicSwift": "DEUTDEFF"
    }
    ```
* **Example Response:** (Successful creation)

    ```json
    {
        "message": "Account created successfully",
        "status": "OK"
    }
    ```

### 2.2 Update Account
* **Endpoint:** `PATCH /account/api/v1/update/account`
* **Description:** Updates an existing bank account.
* **Request Body (JSON):**

    ```json
    {
      "accountId": "b80bdb61-2ef1-4865-84e3-36bce5ba2c12",
      "iban": "DE89370400440532013000",
      "bicSwift": "DEUTTTDEFF"
    }
    ```
* **Example Response:** (Successful update)

    ```json
    {
        "message": "Account updated successfully",
        "status": "OK"
    }
    ```

### 2.3 Delete an Account
* **Endpoint:** `PATCH /account/api/v1/delete/account/{accountId}`
* **Description:** Marks a bank account as deleted (soft delete).
* **Example Request:** `PATCH /account/api/v1/delete/account/b80bdb61-2ef1-4865-84e3-36bce5ba2c12`
* **Example Response:** (Successful deletion)

    ```json
    {
        "message": "Account deleted successfully",
        "status": "OK"
    }
    ```

### 2.4 Get Account By ID
* **Endpoint:** `GET /account/api/v1/get/account/{accountId}`
* **Description:** Retrieves a bank account record by its ID.
* **Example Request:** `GET /account/api/v1/get/account/b80bdb61-2ef1-4865-84e3-36bce5ba2c12`
* **Example Response:**

    ```json
    {
        "accountId": "b80bdb61-2ef1-4865-84e3-36bce5ba2c12",
        "customerId": "4bbc5153-34fe-40eb-8044-6957f7ba0036",
        "iban": "DE89370400440532013000",
        "bicSwift": "DEUTTTDEFF",
        "createdAt": "2025-05-17T11:00:00Z"
    }
    ```

### 2.5 Get Accounts Based on Params
* **Endpoint:** `GET /account/api/v1/filter/accounts?cardAlias={cardAlias}`
* **Description:** Filters bank accounts based on provided parameters, such as `cardAlias`.
* **Example Request:** `GET /account/api/v1/filter/accounts?cardAlias=My`

## 3. Cards Endpoints

These endpoints manage cards associated with bank accounts.

### 3.1 Create Card request
* **Endpoint:** `POST /card/api/v1/create/card`
* **Description:** Creates a new card.
* **Request Body (JSON):**

    ```json
    {
      "cardAlias": "My Travel Card",
      "accountId": "fb007624-ba28-43c4-9a83-9e3987f6a80c",
      "cvv": "123",
      "pan": "1234567812345678",
      "cardType": "PHYSICAL"
    }
    ```
* **Example Response:** (Successful creation)
    ```json
    {
        "message": "Card created successfully",
        "status": "OK"
    }
    ```

### 3.2  Update Card Request
* **Endpoint:** `PATCH /card/api/v1/update/card`
* **Description:** Updates an existing card.
* **Request Body (JSON):**

    ```json
    {
      "cardAlias": "Me Travel Card",
      "cardId": "986d17b4-7f1c-4ab4-af41-0bd3d935ceb1"
    }
    ```
* **Example Response:** (Successful update)
    ```json
    {
        "message": "Card updated successfully",
        "status": "OK"
    }
    ```

### 3.3 Delete Card Request
* **Endpoint:** `PATCH /card/api/v1/update/card`
* **Description:** Marks a card as deleted.
* **Request Body (JSON):**

    ```json
    {
      "cardAlias": "Me Travel Card",
      "cardId": "986d17b4-7f1c-4ab4-af41-0bd3d935ceb1"
    }
    ```
* **Example Response:** (Successful deletion)
    ```json
    {
        "message": "Card deleted successfully",
        "status": "OK"
    }
    ```

### 3.4 Get card by id
* **Endpoint:** `GET /card/api/v1/get/card/{cardId}`
* **Description:** Retrieves a card by its ID.
* **Example Request:** `GET /card/api/v1/get/card/68917bd6-f061-4800-8dec-9f8891b0d636`
* **Example Response:**
    ```json
    {
        "cardId": "68917bd6-f061-4800-8dec-9f8891b0d636",
        "accountId": "fb007624-ba28-43c4-9a83-9e3987f6a80c",
        "cardAlias": "My Travel Card",
        "pan": "1234567812345678",
        "cvv": "123",
        "cardType": "PHYSICAL",
        "createdAt": "2025-05-17T14:00:00Z"
    }
    ```

### 3.5 Search cards
* **Endpoint:** `GET /card/api/v1/search/cards?cardAlias={cardAlias}&overideMasking={overideMasking}`
* **Description:** Searches for cards based on provided parameters.
* **Example Request:** `GET /card/api/v1/search/cards?cardAlias=Travel&overideMasking=true`
* **Example Response:**
    ```json
    [
      {
        "cardId": "68917bd6-f061-4800-8dec-9f8891b0d636",
        "accountId": "fb007624-ba28-43c4-9a83-9e3987f6a80c",
        "cardAlias": "My Travel Card",
        "pan": "1234567812345678",
        "cvv": "123",
        "cardType": "PHYSICAL",
        "createdAt": "2025-05-17T14:00:00Z"
      }
    ]
    ```
