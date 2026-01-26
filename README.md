# Distributed Order Processing System

This project is a microservices-based order processing system built with Spring Boot and Vue.

It demonstrates:

- JWT-based authentication
- Role-based authorization
- Service-to-service authentication
- Background recovery with retry logic
- Eventual consistency
- SPA frontend integration

---

## Architecture

The system consists of four independent components:

- **Auth Service** (Port 8081) – Handles login and JWT issuance
- **Order Service** (Port 8082) – Manages orders and business logic
- **Recovery Service** (Port 8083) – Retries failed orders in the background
- **Frontend (Vue SPA)** (Port 5173) – User interface

---

## Prerequisites

- Java 17+
- Maven
- Node.js (v18+ recommended)
- PostgreSQL

---

## Database Setup

Create the following databases in PostgreSQL:

auth_db,
order_db,

Ensure your `application.yml` files match your local PostgreSQL credentials.

---

## Start Backend Services

Run each service in a separate terminal.

### Auth Service

cd auth-service
mvn spring-boot:run

Runs on: 
http://localhost:8081

---

### Order Service

cd order-service
mvn spring-boot:run

Runs on:
http://localhost:8082

---

### Recovery Service

cd recovery-service
mvn spring-boot:run

Runs on:
http://localhost:8083

The recovery service runs in the background and automatically retries failed orders.

---

## Start Frontend

cd frontend
npm install
npm run dev


Open in browser:

http://localhost:5173


---

## Testing the System

1. Login via frontend
2. Create several orders
3. Some orders may fail
4. Wait for recovery service to retry
5. Refresh the order list to observe status updates

---

This project focuses on demonstrating distributed system concepts such as stateless authentication, service isolation, and background recovery.


