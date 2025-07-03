# FinSight Core - Financial Trading Backend API

A comprehensive REST API for managing financial trades, portfolios, and user authentication. Built with Spring Boot, PostgreSQL, and JWT authentication.

## 🚀 Features

### Core Trading Features
- **Trade Management**: Create, read, update, and delete trades
- **Portfolio Analytics**: View portfolio breakdown by stock symbol
- **Performance Tracking**: Calculate unrealized P&L and return percentages
- **Trade Reconciliation**: Compare internal vs external trade data
- **User Authentication**: JWT-based secure authentication

### Technical Features
- **Spring Boot 3.5** with Java 17
- **PostgreSQL** database with Docker support
- **JWT Authentication** with Spring Security
- **Input Validation** with comprehensive error handling
- **DTO Pattern** for clean API contracts
- **Integration Testing** with TestRestTemplate
- **RESTful API Design** following best practices

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose**
- **Git**
- **PostgreSQL client** (optional, for database inspection)

## 🛠️ Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/finsight-core.git
cd finsight-core
```

### 2. Start PostgreSQL with Docker

```bash
# Start PostgreSQL container
docker run --name finsight-postgres \
  -e POSTGRES_USER=danish \
  -e POSTGRES_PASSWORD=danish \
  -e POSTGRES_DB=finsightdb \
  -p 5432:5432 \
  -d postgres:14

# Verify container is running
docker ps
```

### 3. Configure Application

The application is pre-configured to connect to the Docker PostgreSQL instance. Configuration is in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finsightdb
    username: danish
    password: danish
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
server:
  port: 8081
```

### 4. Build and Run

```bash
# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8081`

### 5. Verify Setup

Check if the application is running:

```bash
curl http://localhost:8081/trades
```

You should get a `401 Unauthorized` response, which is correct since authentication is required.

## 🔐 Authentication Setup

### Create a Test User

Connect to your PostgreSQL database and create a test user:

```bash
# Connect to PostgreSQL in Docker
docker exec -it finsight-postgres psql -U danish -d finsightdb

# Create a test user (password is BCrypt encoded "password123")
INSERT INTO users (username, password) VALUES ('testuser', '$2a$10$3YW2EHzkLGlKHzGYgwEuQeEPF6Lj5JbWQGJyPwMvJKrGYKCkzEFdm');
```

### Login to Get JWT Token

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 📖 API Documentation

### Authentication Endpoints

#### POST /auth/login
Login with username and password to get JWT token.

**Request:**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Trade Management Endpoints

> **Note**: All endpoints below require JWT authentication. Include the token in the Authorization header:
> ```
> Authorization: Bearer <your-jwt-token>
> ```

#### GET /trades
Get all trades for the authenticated user.

**Response:**
```json
[
  {
    "id": 1,
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 100,
    "price": 150.25,
    "tradeDate": "2025-01-15"
  }
]
```

#### POST /trades
Create a new trade.

**Request:**
```json
{
  "symbol": "AAPL",
  "type": "BUY",
  "quantity": 100,
  "price": 150.25,
  "tradeDate": "2025-01-15"
}
```

**Response:**
```json
{
  "id": 1,
  "symbol": "AAPL",
  "type": "BUY",
  "quantity": 100,
  "price": 150.25,
  "tradeDate": "2025-01-15"
}
```

#### GET /trades/portfolio/{userId}
Get portfolio summary grouped by stock symbol.

**Response:**
```json
[
  {
    "symbol": "AAPL",
    "totalQuantity": 100,
    "averageBuyPrice": 150.25,
    "currentValue": 18000.0
  }
]
```

#### GET /trades/performance?userId={userId}
Get performance metrics with unrealized P&L.

**Response:**
```json
[
  {
    "symbol": "AAPL",
    "averageBuyPrice": 150.25,
    "currentPrice": 180.0,
    "totalQuantity": 100,
    "unrealizedPL": 2975.0,
    "returnPercentage": 19.80
  }
]
```

#### POST /trades/reconcile
Compare internal trades with external trade data.

**Request:**
```json
[
  {
    "id": 1,
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 100,
    "price": 150.0,
    "tradeDate": "2025-01-15"
  }
]
```

**Response:**
```json
[
  "Mismatch on trade ID: 1"
]
```

## 🧪 Testing

### Run Unit and Integration Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=TradeIntegrationTest
```

### Manual Testing with curl

#### Create a Trade
```bash
curl -X POST http://localhost:8081/trades \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 100,
    "price": 150.25,
    "tradeDate": "2025-01-15"
  }'
```

#### Get All Trades
```bash
curl -X GET http://localhost:8081/trades \
  -H "Authorization: Bearer <your-jwt-token>"
```

#### Get Portfolio
```bash
curl -X GET http://localhost:8081/trades/portfolio/1 \
  -H "Authorization: Bearer <your-jwt-token>"
```

#### Get Performance
```bash
curl -X GET "http://localhost:8081/trades/performance?userId=1" \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/finsight/backend/
│   │   ├── controller/          # REST controllers
│   │   │   ├── AuthController.java
│   │   │   └── TradeController.java
│   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── AuthRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── ExternalTradeDTO.java
│   │   │   ├── PerformanceDTO.java
│   │   │   ├── PortfolioDTO.java
│   │   │   └── TradeDTO.java
│   │   ├── exception/           # Exception handling
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── model/               # JPA entities
│   │   │   ├── Trade.java
│   │   │   ├── TradeType.java
│   │   │   └── User.java
│   │   ├── repository/          # Data access layer
│   │   │   ├── TradeRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/            # Security configuration
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtUtil.java
│   │   │   └── SecurityConfig.java
│   │   ├── service/             # Business logic
│   │   │   └── TradeService.java
│   │   └── BackendApplication.java
│   └── resources/
│       └── application.yml      # Application configuration
└── test/
    └── java/com/finsight/backend/
        └── TradeIntegrationTest.java
```

## 🔧 Configuration

### Database Configuration

The application uses PostgreSQL. Update `application.yml` if you need different database settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finsightdb
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
```

### JWT Configuration

JWT settings are configured in `JwtUtil.java`. For production, use environment variables:

```java
private final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
```

### Server Port

Change the server port in `application.yml`:

```yaml
server:
  port: 8081  # Change to desired port
```

## 🐳 Docker Deployment

### Using Docker Compose

Create a `docker-compose.yml`:

```yaml
version: '3.8'
services:
  db:
    image: postgres:14
    environment:
      POSTGRES_DB: finsightdb
      POSTGRES_USER: danish
      POSTGRES_PASSWORD: danish
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build: .
    ports:
      - "8081:8081"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/finsightdb
      SPRING_DATASOURCE_USERNAME: danish
      SPRING_DATASOURCE_PASSWORD: danish

volumes:
  postgres_data:
```

Run with:
```bash
docker-compose up -d
```

## 🚨 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```
   Solution: Ensure PostgreSQL Docker container is running
   Command: docker ps
   ```

2. **Port Already in Use**
   ```
   Solution: Change port in application.yml or stop conflicting service
   Command: lsof -i :8081
   ```

3. **JWT Token Expired**
   ```
   Solution: Login again to get a new token
   Token expires after 10 hours
   ```

4. **Authentication Required**
   ```
   Solution: Include JWT token in Authorization header
   Format: Authorization: Bearer <token>
   ```

### Database Inspection

Connect to PostgreSQL to inspect data:

```bash
# Connect to database
docker exec -it finsight-postgres psql -U danish -d finsightdb

# List tables
\dt

# Check trades
SELECT * FROM trades;

# Check users
SELECT * FROM users;
```
