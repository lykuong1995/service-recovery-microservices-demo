# Distributed Order Processing System

This project is a microservices-based distributed order processing system built with Spring Boot, Spring Cloud, and Vue.

It demonstrates:

- JWT-based authentication (Access + Refresh Token)
- Role-based authorization
- Service-to-service authentication (SYSTEM role)
- API Gateway routing
- Service Discovery (Eureka)
- Background recovery with retry logic
- Eventual consistency
- SPA frontend integration

---

## Architecture Overview

The system consists of the following components:

- **Discovery Service (Eureka Server)** – Port 8761  
  Service registry for all microservices

- **API Gateway** – Port 8080  
  Single entry point for all frontend requests

- **Auth Service** – Registers with Eureka  
  Handles authentication, JWT issuance, and refresh

- **Order Service** – Registers with Eureka  
  Manages orders and business logic

- **Recovery Service** – Registers with Eureka  
  Background job that retries temporarily failed orders

- **Frontend (Vue SPA)** – Port 5173  
  User interface

---

## High-Level Flow

Frontend → Gateway → (via Service Discovery) → Auth / Order Services  
Recovery Service → Order Service (internal endpoint with SYSTEM role)

Service Discovery removes hardcoded URLs and allows dynamic service resolution.

---

## Prerequisites

- Java 17+
- Maven
- Node.js (v18+ recommended)
- PostgreSQL

---

## Database Setup

Create the following databases in PostgreSQL:

- auth_db
- order_db

Ensure each service's `application.yml` matches your local PostgreSQL credentials.

---

## Start the System (Recommended Order)

### 1️⃣ Start Discovery Service

cd discovery-service  
mvn spring-boot:run

Dashboard:
http://localhost:8761

---

### 2️⃣ Start Auth Service

cd auth-service  
mvn spring-boot:run

---

### 3️⃣ Start Order Service

cd order-service  
mvn spring-boot:run

---

### 4️⃣ Start Recovery Service

cd recovery-service  
mvn spring-boot:run

---

### 5️⃣ Start API Gateway

cd gateway  
mvn spring-boot:run

Gateway runs on:
http://localhost:8080

---

### 6️⃣ Start Frontend

cd frontend  
npm install  
npm run dev

Open in browser:
http://localhost:5173

Frontend should point to:

VITE_API_URL=http://localhost:8080

All requests go through the Gateway.

---

## Testing the System

1. Register or login via frontend
2. Create several orders
3. Some orders may fail (simulated processing failure)
4. Recovery service runs periodically and retries failed orders
5. Refresh order list to observe status transitions:
    - PROCESSING
    - FAILED_TEMP
    - COMPLETED
    - FAILED_FINAL

---

## Security Design

- Access tokens are short-lived
- Refresh tokens issue new access tokens
- Gateway routes authenticated requests
- Recovery service uses SYSTEM role to access internal endpoints
- Order service protects `/internal/**` endpoints with role-based security

---

## Distributed System Concepts Demonstrated

- Stateless JWT authentication
- Role-based authorization
- Service-to-service authentication
- API Gateway pattern
- Service Discovery (Eureka)
- Load-balanced service calls
- Background retry mechanism
- Eventual consistency pattern

---

## Notes

- All services register with Eureka automatically.
- No hardcoded service URLs are used internally.
- Gateway routes requests using `lb://SERVICE-NAME`.
- Recovery service uses `@LoadBalanced RestTemplate` to call Order Service.

---

This project demonstrates practical distributed system architecture using Spring Boot and Spring Cloud while keeping the implementation lightweight and understandable.