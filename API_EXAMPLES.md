# WhatsApp Management System API Examples

This document provides comprehensive examples for the WhatsApp Management System APIs. The system is built with Spring Boot and provides RESTful APIs for managing WhatsApp messages, and related entities.

## Base URL
```
http://localhost:8080
```

## Authentication
All API endpoints require authentication. Include the following headers:
- `X-Tenant-Id`: Required for multi-tenant support
- `Authorization`: Bearer token for authentication

## Common Response Format
All API responses follow this standard format:
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully",
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "pageSize": 20
}
```

## WhatsApp APIs

### 1. Health Check
**GET** `/api/v1/whatsapp/health`

```bash
curl -X GET "http://localhost:8080/api/v1/whatsapp/health"
```

**Response:**
```
WhatsApp Service is running
```

## Multi-tenancy

All endpoints require the `X-Tenant-Id` header for proper data isolation. The tenant ID is automatically injected from the header and overrides any tenant ID in the request body for security purposes.

## Rate Limiting

The API implements rate limiting to prevent abuse. Default limits:
- 1000 requests per hour per tenant
- 100 requests per minute per user

## API Versioning

The current API version is v1. All endpoints are prefixed with `/api/v1/`.

## Support

For API support and questions, please contact the development team or refer to the internal documentation.