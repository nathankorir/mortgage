# 🧪 Mortgage Service

This microservice handles applications for the mortgage platform. It allows creating, retrieving and deciding on application records with pagination and filtering support.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Mapstruct
- Swagger (OpenAPI)
- Docker / Kubernetes (optional)
- JUnit 5 + MockMvc for testing

---

## ✅ Prerequisites

Ensure the following are installed and properly configured on your system:

- Java 17
- Maven
- Docker
  > Make sure Docker is installed and **running** before starting the application.

---

## 🚀 How to Run the Project

Follow these steps to run the application locally:

### 1. Clone the project
```bash
https://github.com/nathankorir/mortgage
```

### 2. Install dependencies
```bash
mvn clean install
```

### 3. Start the postgres database, kafka and spring boot application
```bash
docker-compose up
```

### 4. Run the application
```bash
./mvnw spring-boot:run  
```

## Documentation

### Swagger UI
```bash
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON Spec
```bash
http://localhost:8080/v3/api-docs
```

### Download openapi.yaml
```bash
http://localhost:8080/v3/api-docs.yaml
```

## 🔐 Authentication
To access secured endpoints, you need to obtain a JWT token.

### Login Endpoint
```bash
POST http://localhost:8080/auth/login
```
Use this endpoint to get a token.

### Default user credentials
#### Applicant
Username: nathan_applicant
Password: password

#### Officer
Username: nathan_officer
Password: admin

### Architecture diagram
https://drive.google.com/file/d/1OA9In7zbA5pJh7adfL5zOqNM5pslNOl-/view?usp=drive_link

### CI Badge
https://codecov.io/gh/nathankorir/mortgage/branch/main/graph/badge.svg

### Environment json
{
"JAVA_OPTS": "-Xmx512m",
"SPRING_ACTIVE_PROFILE": "dev",
"SPRING_DATASOURCE_URL": "jdbc:postgresql://mortgage-postgres:5432/mortgage",
"SPRING_DATASOURCE_USERNAME": "postgres",
"SPRING_DATASOURCE_PASSWORD": "postgres",
"KAFKA_BOOTSTRAP_SERVERS": "kafka:9092",
"KAFKA_TOPIC": "loan.applications"
}

### Kafka topic schema
{
"applicationId": "550e8400-e29b-41d4-a716-446655440000",
"amount": 250000.00,
"purpose": "Home renovation",
"status": "PENDING",
"timestamp": "2025-08-08T14:32:45",
"traceId": "123e4567-e89b-12d3-a456-426614174000",
"version": "1.0"
}
