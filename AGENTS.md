# AGENTS.md

## Project Overview
This is a Domain-Driven Design (DDD) Spring Boot application for order management (Pedidos) with Spanish domain language and English technical terms.

### Technology Stack
- **Java 17** - Modern Java with latest features
- **Spring Boot 3.1.5** - Latest stable Spring Boot version
- **Maven** - Build and dependency management
- **H2 Database** - In-memory database for development
- **Spring Data JPA** - ORM support (though currently using in-memory repository)
- **Jakarta Validation** - Bean validation framework
- **Lombok** - Code generation for boilerplate reduction
- **JUnit 5** - Testing framework (spring-boot-starter-test included)

### Architecture
The project follows clean DDD architecture with three main layers:

#### Domain Layer (`dominio.modelo` & `dominio.servicio`)
- **Entities**: Rich domain models with business logic (Pedido)
- **Value Objects**: Immutable records for domain concepts (Dinero, IdentificadorPedido)
- **Domain Services**: Domain-specific business logic (ServicioRealizacionPedido)
- **Repository Interfaces**: Contracts for persistence (PedidoRepository)

#### Application Layer (`aplicacion`)
- **Application Services**: Orchestration of domain logic (PedidoAplicacionService)
- **DTOs**: Data Transfer Objects for API communication (PedidoDTO, CrearPedidoRequest)
- **Exception Handling**: Custom exceptions for application errors (PedidoNoEncontradoException)

#### Infrastructure Layer (`infraestructura`)
- **Web Controllers**: REST API endpoints (PedidoController)
- **Persistence**: Repository implementations (PedidoRepositoryImpl with ConcurrentHashMap)

## Build & Development Commands

### Maven Commands
```bash
# Build the project
mvn clean compile

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClassName

# Run single test method
mvn test -Dtest=ClassName#methodName

# Package application
mvn clean package

# Run application (development)
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Development Server
- **Default Port**: 8080
- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:testdb
- **Database Browser**: Access via web console at above URL

### Database Access
```bash
# Connect to H2 console
open http://localhost:8080/h2-console

# JDBC Settings
# URL: jdbc:h2:mem:testdb
# Driver: org.h2.Driver
# Username: sa
# Password: (empty)
```

## DDD Architecture Patterns

### Domain Layer Best Practices
- **Entity Design**:
  - Rich domain models with behavior methods
  - Private constructors, public factory methods
  - Aggregate roots manage invariants
  - Defensive programming with Objects.requireNonNull()
  - Business logic encapsulated in entities
  
- **Value Object Pattern**:
  - Immutable records with compact constructors
  - Validation in record components
  - No-args constructors for safety
  - Factory methods for creation (ej: `IdentificadorPedido.nuevo()`)
  
- **Repository Pattern**:
  - Interfaces in domain layer
  - Implementations in infrastructure layer
  - Clear separation of concerns

### Application Layer Guidelines
- **Service Orchestration**:
  - Coordinate domain objects
  - Handle transaction boundaries (@Transactional)
  - Transform between DTOs and domain models
  - Apply business rules and validations

### Infrastructure Layer Standards
- **REST Controllers**:
  - Clean API endpoints with proper HTTP methods
  - ResponseStatusException for error handling
  - Jakarta validation on request DTOs
  - Consistent error response format

## Code Style Guidelines

### Naming Conventions
- **Domain Concepts**: Spanish (Pedido, Dirección, EstadoPedido)
- **Technical Terms**: English (Service, Repository, Controller)
- **Classes**: PascalCase (PedidoController, PedidoAplicacionService)
- **Methods**: camelCase (crearNuevoPedido, confirmarPedido)
- **Variables**: camelCase for local variables, snake_case for constants
- **Packages**: `com.ejemplo.ddd.{layer}.{subpackage}`

### Import Organization
```java
// Standard libraries first
import java.util.*;
import java.math.*;
import java.time.*;

// Jakarta validation
import jakarta.validation.*;
import jakarta.persistence.*;

// Spring framework
import org.springframework.*;
import org.springframework.web.*;

// Project packages
import com.ejemplo.ddd.dominio.*;
import com.ejemplo.ddd.aplicacion.*;
import com.ejemplo.ddd.infraestructura.*;
```

### Validation Patterns
- **Jakarta Validation**: Use annotations on DTOs
- **Custom Messages**: Spanish error messages for better UX
- **Null Checks**: Use Objects.requireNonNull() for early validation
- **Business Rules**: Enforce invariants in domain objects

### Error Handling
- **Custom Exceptions**: Extend RuntimeException for domain errors
- **HTTP Status**: Use ResponseStatusException for REST errors
- **Logging**: Proper error logging with context
- **Exception Propagation**: Don't catch and rethrow unnecessarily

## Development Patterns

### Factory Method Pattern
```java
// For value objects and entities
public static IdentificadorPedido nuevo() {
    return new IdentificadorPedido(UUID.randomUUID());
}

public static Pedido crearNuevoPedido(String idCliente, Direccion direccion, Currency moneda) {
    return new Pedido(IdentificadorPedido.nuevo(), idCliente, direccion, moneda);
}
```

### Transaction Management
- Use `@Transactional` on service methods that modify data
- Propagation: Default (REQUIRED)
- Rollback: Automatic on runtime exceptions

## Testing Guidelines

### Testing Framework
- **JUnit 5**: Use annotations from spring-boot-starter-test
- **Test Structure**: Mirror main package structure in src/test
- **Naming**: `ClassNameTest` for unit tests, `ClassNameIntegrationTest` for integration tests

### Test Categories
- **Unit Tests**: Test individual components in isolation
  - Domain entities and value objects
  - Services with mocked dependencies
  - Repository implementations
- **Integration Tests**: Test component interactions
  - REST controllers with MockMvc
  - End-to-end request flows

### Test Execution
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=PedidoTest

# Run with coverage (if JaCoCo added)
mvn test jacoco:report
```

### Mock Strategy
- Use @MockBean for Spring beans
- Mock external dependencies in unit tests
- Use @TestConfiguration for integration test setup
- Avoid mocking domain objects in unit tests

## Performance Considerations

### Current Limitations
- **In-Memory Repository**: Uses ConcurrentHashMap, suitable for development only
- **No Connection Pooling**: Consider H2 connection limits
- **Large Data Sets**: Monitor memory usage with current persistence approach
- **Caching**: No caching layer implemented

### Recommendations
- **Database Migration**: Consider JPA entities for production
- **Connection Pool**: Configure HikariCP for better performance
- **Caching**: Add Redis or Ehcache for frequently accessed data
- **Monitoring**: Add Micrometer metrics for production readiness

## Security Considerations

### Current Security
- **No Authentication**: No security implementation found
- **Input Validation**: Jakarta validation on all endpoints
- **SQL Injection**: JPA provides protection
- **HTTPS**: Not configured (development only)

### Security Recommendations
- **Authentication**: Add Spring Security or JWT
- **Authorization**: Implement role-based access control
- **HTTPS**: Enable SSL in production
- **CORS**: Configure for web API access
- **Input Sanitization**: Continue using validation annotations
- **Security Headers**: Add security headers for production APIs

## Code Quality Standards

### Code Quality Tools
- **IDE**: Configure IDE with Java 17+ support
- **Build**: Maven with proper compiler settings
- **Code Coverage**: Consider JaCoCoCo for coverage tracking

### Development Workflow
1. **Code Changes**: Ensure tests pass before committing
2. **Local Testing**: Run full test suite frequently
3. **Code Review**: Follow style guidelines consistently
4. **Documentation**: Update AGENTS.md with architectural decisions

### Troubleshooting
- **H2 Console Access**: http://localhost:8080/h2-console
- **Database Logs**: Enable DEBUG logging for H2 queries
- **Spring Boot Actuator**: Add `/actuator` endpoints for health checks
- **Test Failures**: Check application.properties and dependencies for configuration issues

## API Documentation

### REST Endpoints
- **Base URL**: `http://localhost:8080/api/pedidos`
- **Content-Type**: `application/json`
- **HTTP Methods**: 
  - POST `/api/pedidos` - Create new pedido
  - GET `/api/pedidos/{id}` - Get pedido by ID
  - PUT `/api/pedidos/{id}/direccion` - Update shipping address
  - POST `/api/pedidos/{id}/lineas` - Add line to pedido
  - DELETE `/api/pedidos/{id}/lineas/{idProducto}` - Remove line
  - PUT `/api/pedidos/{id}/lineas/{idProducto}/cantidad` - Update line quantity
  - POST `/api/pedidos/{id}/confirmar` - Confirm pedido
  - POST `/api/pedidos/{id}/enviar` - Mark as sent
  - POST `/api/pedidos/{id}/entregar` - Mark as delivered
  - POST `/api/pedidos/{id}/cancelar` - Cancel pedido
  - DELETE `/api/pedidos/{id}` - Delete pedido

### Request/Response Examples
See application startup console output for sample JSON payloads when running `mvn spring-boot:run`.