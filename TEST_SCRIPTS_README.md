# Asset Service Test Scripts

This directory contains comprehensive test scripts for the Asset Service, following the same patterns as the Location Service test scripts.

## Test Scripts Overview

### 1. `test-asset-startup.sh`
**Purpose**: Quick startup test to verify the service starts correctly  
**Prerequisites**: Java 21+, Gradle  
**Usage**: `./test-asset-startup.sh`

**What it tests**:
- Java installation verification
- Gradle wrapper availability
- Application build
- Service startup with H2 database
- Health endpoint availability
- Basic security (unauthorized access blocking)
- Optional basic functionality test

**Output**: Service startup status, PID, and log file location

### 2. `test-asset-basic.sh`
**Purpose**: Basic functionality test without authentication  
**Prerequisites**: Asset service running on port 8086  
**Usage**: `./test-asset-basic.sh`

**What it tests**:
- Service health check
- Unauthorized access security
- Storage configuration accessibility
- Upload URL generation security (should fail without auth)

**Output**: Basic service status and security verification

### 3. `test-asset-api.sh`
**Purpose**: Comprehensive API test with full authentication  
**Prerequisites**: 
- Identity service running on port 8082
- Asset service running on port 8086
- `jq` and `curl` installed

**Usage**: `./test-asset-api.sh`

**What it tests**:
- Service health checks (Identity + Asset)
- JWT authentication
- Upload URL generation
- Asset metadata creation
- Asset retrieval by ID
- Asset retrieval by parent
- Asset search with filters
- Download URL generation
- Asset archiving/restoration
- Asset statistics and counting
- Multi-tenant functionality
- Error handling
- Performance testing

**Output**: Comprehensive test results with detailed API interaction

### 4. `test-asset-storage.sh`
**Purpose**: Storage provider specific testing  
**Prerequisites**: 
- Identity service running on port 8082
- Asset service running on port 8086
- MinIO server running on port 9000 (optional)
- `jq` and `curl` installed

**Usage**: `./test-asset-storage.sh`

**What it tests**:
- Service health checks
- JWT authentication
- Storage configuration retrieval
- MinIO health check (if using MinIO)
- Upload URL generation with different file types/sizes
- Upload URL validation (invalid sizes, MIME types)
- Complete asset lifecycle (upload → create → download)
- Storage provider switching
- Error handling for storage operations
- Performance testing for storage operations

**Output**: Storage provider functionality verification

## Prerequisites

### Required Tools
- **Java 21+**: Required for running the service
- **curl**: For making HTTP requests
- **jq**: For JSON parsing and manipulation

### Installation Commands
```bash
# macOS
brew install jq

# Ubuntu/Debian
sudo apt-get install jq curl

# CentOS/RHEL
sudo yum install jq curl
```

### Required Services
- **Identity Service**: Port 8082 (for authentication)
- **Asset Service**: Port 8086 (main service)
- **MinIO** (optional): Port 9000 (for MinIO storage provider)

## Quick Start Guide

### 1. Start Required Services
```bash
# Start Identity Service (in separate terminal)
cd /Users/mohit/git/identity
./gradlew bootRun --args="--spring.profiles.active=h2"

# Start Asset Service (in separate terminal)
cd /Users/mohit/git/asset
./gradlew bootRun --args="--spring.profiles.active=h2"

# Start MinIO (optional, for storage testing)
docker run -p 9000:9000 -p 9001:9001 minio/minio server /data --console-address ":9001"
```

### 2. Run Tests
```bash
# Quick startup test
./test-asset-startup.sh

# Basic functionality test
./test-asset-basic.sh

# Comprehensive API test
./test-asset-api.sh

# Storage provider test
./test-asset-storage.sh
```

## Test Data

### Default Tenant IDs
- **Default Tenant**: `550e8400-e29b-41d4-a716-446655440010`
- **ACME Tenant**: `550e8400-e29b-41d4-a716-446655440011`

### Test Credentials
- **Username**: `superadmin`
- **Password**: `password`

### Test File Types
- PDF assets (`application/pdf`)
- JPEG images (`image/jpeg`)
- Word assets (`application/vnd.openxmlformats-officeasset.wordprocessingml.asset`)
- Plain text (`text/plain`)

## Configuration

### Storage Providers
The Asset Service supports multiple storage providers:
- **MINIO**: Local MinIO server (default for testing)
- **S3**: AWS S3 (requires AWS credentials)
- **AZURE_BLOB**: Azure Blob Storage
- **GCP_STORAGE**: Google Cloud Storage

### File Size Limits
- **Default max size**: 100MB (104857600 bytes)
- **Test files**: 1KB - 1MB range

### URL Expiry Times
- **Upload URLs**: 15 minutes (configurable)
- **Download URLs**: 60 minutes (configurable)

## Troubleshooting

### Common Issues

1. **Service not starting**
   - Check Java version (requires 21+)
   - Verify port 8086 is not in use
   - Check app.log for error details

2. **Authentication failures**
   - Ensure Identity service is running on port 8082
   - Verify credentials (superadmin/password)
   - Check JWT token expiration

3. **Storage provider issues**
   - For MinIO: Ensure server is running on port 9000
   - For S3: Configure AWS credentials
   - Check storage configuration endpoint

4. **Permission errors**
   - Make scripts executable: `chmod +x test-asset-*.sh`
   - Ensure proper file permissions

### Debug Mode
To run tests with verbose output:
```bash
# Enable debug output
export DEBUG=true
./test-asset-api.sh
```

### Log Files
- **Service logs**: `app.log`
- **Test output**: Console output with colored status indicators

## Test Results Interpretation

### Status Indicators
- ✓ **Green**: Test passed
- ✗ **Red**: Test failed
- ⚠️ **Yellow**: Warning or info message

### HTTP Status Codes
- **200**: Success
- **401**: Unauthorized (expected for security tests)
- **403**: Forbidden (expected for security tests)
- **404**: Not found
- **500**: Internal server error

## Integration with CI/CD

These test scripts can be integrated into CI/CD pipelines:

```bash
#!/bin/bash
# CI/CD Integration Example

# Start services
./gradlew bootRun --args="--spring.profiles.active=h2" &
SERVICE_PID=$!

# Wait for startup
sleep 30

# Run tests
./test-asset-basic.sh
TEST_RESULT=$?

# Cleanup
kill $SERVICE_PID

# Exit with test result
exit $TEST_RESULT
```

## Contributing

When adding new test cases:
1. Follow the existing naming conventions
2. Include proper error handling
3. Add colored output for better readability
4. Update this README with new test descriptions
5. Ensure tests are idempotent (can be run multiple times)

## Related Assetation

- [Asset Service API Assetation](API_EXAMPLES.md)
- [Storage Provider Configuration](README.md)
- [Location Service Test Scripts](../location/)
- [Identity Service Assetation](../identity/)
