# WhatsApp Service

A Spring Boot application for managing WhatsApp messages, designed to work alongside the Identity service with JWT validation and event publishing capabilities.

## 🚀 Features

- **WhatsApp Management**: Create, read, update, and delete WhatsApp messages
- **Status Tracking**: Track WhatsApp message status changes with history
- **Assignment Management**: Assign and reassign WhatsApp messages to users
- **Items & Comments**: Manage WhatsApp message items and comments
- **JWT Validation**: Secure API endpoints with JWT token validation
- **Event Publishing**: Publish events for WhatsApp message operations
- **Multi-tenant Support**: Support for multiple tenants
- **RESTful APIs**: Clean, consistent API design

## 🗄️ Database Configuration

The WhatsApp project supports multiple database configurations using Spring profiles:

### Available Profiles

1. **H2 Profile** (`h2`) - In-memory database for development and testing
    - Fast startup and no external dependencies
    - Data is lost when application stops
    - Includes H2 console for database inspection

2. **PostgreSQL Profile** (`postgres`) - Production-ready database
    - Persistent data storage
    - Better performance for large datasets
    - Connection pooling and optimization

### Database Setup

#### H2 Database (Default)
```bash
# Run with H2 profile
./gradlew bootRun --args='--spring.profiles.active=h2'
```

#### PostgreSQL Setup with Docker
```bash
# Start PostgreSQL container
docker-compose up -d postgres

# Run with PostgreSQL profile
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

### Database Access

#### H2 Console (when using H2 profile)
- URL: `http://localhost:8083/h2-console`
- JDBC URL: `jdbc:h2:mem:whatsappdb`
- Username: `sa`
- Password: `password`

## 🏗️ Architecture

- **WhatsAppController**: REST endpoints for WhatsApp operations
- **WhatsAppService**: Business logic for WhatsApp management
- **Event Publishing**: Kafka-based event publishing for WhatsApp changes
- **JWT Validation**: Secure API endpoints with JWT token validation
- **SecurityConfig**: Spring Security configuration for JWT validation

### Technology Stack

- **Spring Boot 3.3.3**: Core framework
- **Spring Security**: JWT validation
- **Spring Data JPA**: Data persistence
- **Spring Kafka**: Event publishing
- **H2 Database**: In-memory database for development
- **PostgreSQL**: Production database
- **Liquibase**: Database migration
- **Gradle**: Build tool
- **Common Library**: Shared utilities and models

## 📋 Prerequisites

- Java 21 or higher
- Gradle 8.x
- Identity service (for JWT token validation)
- Kafka (for event publishing)
- curl (for API testing)

## 🛠️ Setup and Installation

### 1. Build the Project
```bash
cd whatsapp
./gradlew build
```

### 2. Run the Application

#### Option 1: H2 Database (Default)
```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

#### Option 2: PostgreSQL Database
```bash
# Start PostgreSQL
docker-compose up -d postgres

# Run the application
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

The service will start on `http://localhost:8083`

### 3. Verify Application Health
```bash
curl http://localhost:8083/api/whatsapp/health
```

Expected response: `"WhatsApp Service is running"`

## 🔌 API Endpoints

### Base URL
`http://localhost:8083/api/whatsapp`

### WhatsApp Endpoints

#### 1. Health Check
```http
GET /health
```


## 🔐 Security

### JWT Validation
- All endpoints (except health check) require JWT token validation
- JWT tokens are validated using the same secret as the Identity service
- Token must be included in the Authorization header: `Bearer <token>`

### Getting JWT Token
1. Use the Identity service to authenticate and get a JWT token
2. Include the token in the Authorization header for all WhatsApp API calls


## 🔄 Event Publishing

The service publishes events for:
- WhatsApp message creation
- WhatsApp message updates
- Status changes
- Assignment changes
- WhatsApp message deletion

Events are published to Kafka topics following the pattern: `whatsapp.whatsapp.events`


## 📁 Project Structure

```
whatsapp/
├── src/main/java/com/assetneuron/whatsapp/
│   ├── WhatsAppApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── WebConfig.java
│   ├── controller/
│   │   └── WhatsAppController.java
│   ├── dto/
│   │   ├── WhatsAppDto.java
│   │   ├── WhatsAppItemDto.java
│   │   ├── WhatsAppCommentDto.java
│   │   └── WhatsAppHistoryDto.java
│   ├── event/
│   │   ├── WhatsAppEvent.java
│   │   ├── WhatsAppStatusEvent.java
│   │   └── WhatsAppAssignmentEvent.java
│   ├── model/
│   │   ├── WhatsApp.java
│   │   ├── WhatsAppStatus.java
│   │   ├── Priority.java
│   │   ├── WhatsAppItem.java
│   │   ├── WhatsAppComment.java
│   │   └── WhatsAppHistory.java
│   ├── repository/
│   │   ├── WhatsAppRepository.java
│   │   ├── WhatsAppItemRepository.java
│   │   ├── WhatsAppCommentRepository.java
│   │   └── WhatsAppHistoryRepository.java
│   └── service/
│       └── WhatsAppService.java
└── src/main/resources/
    ├── application.properties
    ├── application-h2.properties
    ├── application-postgres.properties
    └── db/changelog/
        ├── db.changelog-master.xml
        ├── 001-create-whatsapp-schema.xml
        └── 002-insert-sample-data.xml
```

## 🔗 Integration

### With Identity Service
- Uses JWT tokens from Identity service for authentication
- Validates tokens using the same secret key

### With Common Library
- Extends BaseEntity for audit fields
- Uses common event publishing and utilities
- Leverages shared security components

### With Kafka
- Publishes WhatsApp message events for other services to consume
- Follows event-driven architecture patterns
