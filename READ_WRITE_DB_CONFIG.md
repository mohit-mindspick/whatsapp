# Read/Write Database Configuration

## Overview

The Asset Service now supports read/write database configuration similar to the Location Service, enabling database scaling and load distribution.

## Configuration Files Added

### 1. **ConnectionPoolConfig.java**
- Configures separate HikariCP connection pools for read and write databases
- Write pool: 20 max connections, 5 min idle
- Read pool: 15 max connections, 3 min idle
- Named pools for better monitoring

### 2. **ReadWriteRoutingDataSource.java**
- Extends `AbstractRoutingDataSource` to route queries based on transaction type
- Read-only transactions → read database
- Write transactions → write database

### 3. **ReadWriteDataSourceConfig.java**
- Creates the routing data source with read/write mapping
- Wraps routing data source with `LazyConnectionDataSourceProxy` for better performance
- Sets write database as default fallback

### 4. **Updated JpaConfig.java**
- Modified to use custom `entityManagerFactory` and `transactionManager`
- Configured with proper bean qualifiers for dependency injection
- Supports the routing data source architecture

## Application Properties

### Main Configuration (`application.properties`)
```properties
# Read/Write Database Configuration
spring.datasource.write.url=jdbc:postgresql://localhost:5432/assetneuron_asset
spring.datasource.write.username=postgres
spring.datasource.write.password=postgres

spring.datasource.read.url=jdbc:postgresql://localhost:5432/assetneuron_asset
spring.datasource.read.username=postgres
spring.datasource.read.password=postgres
```

### PostgreSQL Profile (`application-postgres.properties`)
```properties
# Same configuration but pointing to assetneuron database
spring.datasource.write.url=jdbc:postgresql://localhost:5432/assetneuron
spring.datasource.read.url=jdbc:postgresql://localhost:5432/assetneuron
```

## Transaction Routing

### Read Operations (Read Database)
- All methods in `AssetService` are marked with `@Transactional(readOnly = true)`
- Automatically routed to read database
- Includes: `getAssetById`, `getAssetsByParent`, `searchAssets`, etc.

### Write Operations (Write Database)
- Write methods override with `@Transactional` (without readOnly)
- Automatically routed to write database
- Includes: `generateUploadUrl`, `confirmUpload`, `deleteAsset`, `archiveAsset`, etc.

## Benefits

1. **Scalability**: Read and write operations can be scaled independently
2. **Performance**: Read replicas can handle heavy query load
3. **High Availability**: Write database can be replicated to read databases
4. **Load Distribution**: Query load is distributed across multiple databases
5. **Monitoring**: Separate connection pools for better observability

## Usage Examples

### Single Database Setup (Current)
```properties
# Same database for both read and write
spring.datasource.write.url=jdbc:postgresql://localhost:5432/assetneuron
spring.datasource.read.url=jdbc:postgresql://localhost:5432/assetneuron
```

### Separate Read Replica Setup
```properties
# Different databases for read and write
spring.datasource.write.url=jdbc:postgresql://master-db:5432/assetneuron
spring.datasource.read.url=jdbc:postgresql://replica-db:5432/assetneuron
```

### Multiple Read Replicas (Advanced)
```properties
# Primary write database
spring.datasource.write.url=jdbc:postgresql://master:5432/assetneuron

# Read replica 1
spring.datasource.read.url=jdbc:postgresql://replica1:5432/assetneuron
```

## Connection Pool Monitoring

The configuration includes named connection pools for monitoring:
- `AssetWritePool`: For write operations
- `AssetReadPool`: For read operations

Monitor these pools using:
- HikariCP JMX metrics
- Spring Boot Actuator health checks
- Application performance monitoring tools

## Testing

The configuration maintains backward compatibility:
- Test profile continues to use H2 in-memory database
- Postgres profile uses the new read/write configuration
- All existing functionality remains unchanged

## Next Steps

1. **Production Setup**: Configure separate read replicas in production
2. **Monitoring**: Set up connection pool monitoring and alerts
3. **Load Testing**: Test read/write distribution under load
4. **Failover**: Implement read database failover strategies

The Asset Service now has enterprise-grade database configuration ready for production scaling!
