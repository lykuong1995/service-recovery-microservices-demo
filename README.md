# Distributed Order Processing System

This project is a microservices-based distributed order processing system built with Spring Boot, Spring Cloud, and Vue.

It demonstrates:

- JWT-based authentication (Access + Refresh Token)
- Role-based authorization
- Service-to-service authentication (SYSTEM role)
- API Gateway routing
- Service Discovery (Eureka)
- Resilience (Resilience4j circuit breakers/retries) for internal flows
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
  Manages orders, enrichment, and resilience logic

- **Frontend (Vue SPA)** – Port 5173  
  User interface

---

## High-Level Flow

Frontend → Gateway → (via Service Discovery) → Auth / Order Services  

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

### 4️⃣ Start API Gateway

cd gateway  
mvn spring-boot:run

Gateway runs on:
http://localhost:8080

---

### 5️⃣ Start Frontend

cd frontend  
npm install  
npm run dev

Open in browser:
http://localhost:5173

Frontend should point to:

VITE_API_URL=http://localhost:8080

All requests go through the Gateway.

---

## Running Multiple Order Instances

By default the order service uses `server.port=0`, so each process binds to an available port. `EurekaPortConfig` copies the actual port into the Eureka metadata after startup, preventing every instance from appearing on port `0`. Start multiple copies with the same build:

```
cd order
mvn spring-boot:run
```

or `java -jar target/order-0.0.1-SNAPSHOT.jar --server.port=0` repeatedly. Eureka will show distinct entries (same `ORDER-SERVICE` name, different ports), and the gateway can route to them via `lb://ORDER-SERVICE`.

---

## Testing the System

1. Register or login via frontend
2. Create several orders
3. Some orders may fail (simulated processing failure)
4. Refresh order list to observe how Resilience4j-enhanced internal retries affect status transitions:
    - PROCESSING
    - FAILED_TEMP
    - COMPLETED
    - FAILED_FINAL

---

## Security Design

- Access tokens are short-lived
- Refresh tokens issue new access tokens
- Gateway routes authenticated requests
- Order service protects `/internal/**` endpoints with role-based security
- Order service adds Resilience4j circuit breakers/retries around critical internal calls

---

## Distributed System Concepts Demonstrated

- Stateless JWT authentication
- Role-based authorization
- Service-to-service authentication
- API Gateway pattern
- Service Discovery (Eureka)
- Load-balanced service calls
- Runtime resilience via Resilience4j circuit breakers and retries
- Eventual consistency pattern

---

## Notes

- All services register with Eureka automatically.
- No hardcoded service URLs are used internally.
- Gateway routes requests using `lb://SERVICE-NAME`.
- Order service publishes its actual random port (`server.port=0`) back to Eureka via `EurekaPortConfig`, so each instance shows a unique entry.

---

This project demonstrates practical distributed system architecture using Spring Boot and Spring Cloud while keeping the implementation lightweight and understandable.

---

## Running with Docker Compose

1. Build all artifacts using a JDK 17+ runtime (the Maven command below sets `JAVA_HOME` so the compiler runs with the same version as the services expect):

   ```
   JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home mvn -DskipTests -B package
   ```

2. Start the stack:

   ```
   docker compose up --build
   ```

   The Compose file now defines a Postgres container that creates `auth_db` and `order_db` for you (`postgres-init/init.sql`), so the auth and order services can run Flyway migrations without additional setup.

3. Service-to-service wiring is handled via the environment variables that Compose injects, so Auth/Order connect to Postgres using `DB_USERNAME=lykuong` and `DB_PASSWORD=password`, and the Gateway binds to host `8080` through `SERVER_PORT=8080`.

4. Tear the stack down when you are done:

   ```
   docker compose down
   ```
