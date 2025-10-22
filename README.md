# Spring Boot Core Library

A comprehensive, production-ready reusable library for Spring Boot projects. This library provides common utilities, exception handling, base entities, and configurations that can be shared across multiple Spring Boot applications.

## Features

### Exception Handling
- **Base Exception Framework**: Hierarchical exception classes with HTTP status mapping
- **Global Exception Handler**: Centralized exception handling with standardized error responses
- **Custom Exceptions**:
  - `BusinessException`: For business logic errors
  - `ResourceNotFoundException`: For missing resources
  - `UnauthorizedException`: For authentication failures
  - `ForbiddenException`: For authorization failures

### Response DTOs
- **ErrorResponse**: Standardized error response structure with validation support

### Base Entities
- **BaseEntity**: Abstract entity with automatic audit fields:
  - `id`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `version`
- **SoftDeleteEntity**: Extends BaseEntity with soft delete functionality
- **JPA Auditing**: Automatic population of audit fields

### Utility Classes
- **DateTimeUtil**: Date and time operations and formatting
- **StringUtil**: String manipulation, validation, and transformation
- **JsonUtil**: JSON serialization/deserialization with Jackson
- **ValidationUtil**: Programmatic validation utilities

### Validation
- **Custom Validators**:
  - `@PhoneNumber`: Phone number validation
- **ValidationUtil**: Programmatic validation with exception handling

### Configuration
- **JpaAuditingConfig**: Automatic JPA auditing configuration
- **JacksonConfig**: JSON serialization/deserialization configuration
- **WebMvcConfig**: Web MVC and CORS configuration

### Aspect-Oriented Programming
- **LoggingAspect**:
  - `@LogExecutionTime`: Automatic method execution time logging
  - Controller method logging with arguments and results

### Constants & Enums
- **ApiConstants**: API versioning, headers, pagination defaults
- **ErrorMessages**: Standardized error messages
- **Status**: Common entity status enum
- **SortDirection**: Sort direction enum with Spring Data integration

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Spring Boot 3.x

### Installation

#### Option 1: Local Maven Install

Build and install to your local Maven repository:

```bash
mvn clean install
```

Then add as a dependency in your Spring Boot project:

```xml
<dependency>
    <groupId>pro.thinhha</groupId>
    <artifactId>core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

#### Option 2: Maven Repository

Once published to Maven Central or your organization's repository, add the dependency:

```xml
<dependency>
    <groupId>pro.thinhha</groupId>
    <artifactId>core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Important Notes

**This is a library, not a standalone application.** It provides reusable components that you import into your Spring Boot projects. Most dependencies are marked as `optional`, so you only need to include the specific Spring Boot starters your application requires.

### Required Dependencies in Your Application

Depending on which features you use, add the corresponding Spring Boot starters to your application's `pom.xml`:

```xml
<!-- For REST controllers and web features -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- For JPA entities and repositories -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- For validation annotations -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- For AOP features (logging aspects) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### Usage Examples

#### Using Base Entity

```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String username;
    private String email;
    // ... other fields
}
```

#### Using Custom Exceptions

```java
@Service
public class UserService {

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    public void createUser(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("User with this email already exists", "USER_ALREADY_EXISTS");
        }
        // ... create user
    }
}
```

#### Using Logging Aspect

```java
@Service
public class UserService {

    @LogExecutionTime
    public User findById(Long id) {
        // This method's execution time will be automatically logged
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }
}
```

#### Using Utility Classes

```java
// String utilities
String email = "test@example.com";
if (StringUtil.isValidEmail(email)) {
    // Process email
}

// JSON utilities
User user = new User();
String json = JsonUtil.toJson(user);
User deserializedUser = JsonUtil.fromJson(json, User.class);

// Date utilities
String formattedDate = DateTimeUtil.format(LocalDateTime.now(), DateTimeUtil.CUSTOM_DATE_TIME);

// Validation utilities
ValidationUtil.validateAndThrow(userDto);
```

#### Using Custom Validators

```java
public class UserDto {

    @NotBlank
    @Email
    private String email;

    @PhoneNumber
    private String phoneNumber;

    // ... other fields
}
```

## Project Structure

```
core/
├── src/main/java/pro/thinhha/core/
│   ├── annotation/          # Custom annotations
│   │   └── LogExecutionTime.java
│   ├── aspect/              # AOP aspects
│   │   └── LoggingAspect.java
│   ├── config/              # Configuration classes
│   │   ├── JacksonConfig.java
│   │   ├── JpaAuditingConfig.java
│   │   └── WebMvcConfig.java
│   ├── constant/            # Constants
│   │   ├── ApiConstants.java
│   │   └── ErrorMessages.java
│   ├── dto/                 # Data Transfer Objects
│   │   └── ErrorResponse.java
│   ├── entity/              # Base entity classes
│   │   ├── BaseEntity.java
│   │   └── SoftDeleteEntity.java
│   ├── enums/               # Enumerations
│   │   ├── SortDirection.java
│   │   └── Status.java
│   ├── exception/           # Exception classes
│   │   ├── BaseException.java
│   │   ├── BusinessException.java
│   │   ├── ForbiddenException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── UnauthorizedException.java
│   ├── handler/             # Exception handlers
│   │   └── GlobalExceptionHandler.java
│   ├── util/                # Utility classes
│   │   ├── DateTimeUtil.java
│   │   ├── JsonUtil.java
│   │   └── StringUtil.java
│   └── validator/           # Custom validators
│       ├── PhoneNumber.java
│       ├── PhoneNumberValidator.java
│       └── ValidationUtil.java
└── pom.xml
```

## Dependencies

Key dependencies included:
- Spring Boot 3.5.6
- Spring Data JPA
- Spring Validation
- Spring AOP
- Lombok
- Jackson (JSON processing)
- Apache Commons Lang3
- Apache Commons Collections4

## Configuration

### Enable Component Scanning

In your main application, make sure to scan the core package:

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.yourcompany", "pro.thinhha.core"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### Customize Auditing

Override the default `AuditorAware` implementation to provide actual user information:

```java
@Configuration
public class CustomAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // Get current user from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system");
            }
            return Optional.of(authentication.getName());
        };
    }
}
```

## Best Practices

1. **Extend Base Entities**: Use `BaseEntity` or `SoftDeleteEntity` for automatic audit tracking
2. **Throw Custom Exceptions**: Use the provided exception hierarchy for consistent error handling
3. **Apply Logging Annotations**: Use `@LogExecutionTime` for performance monitoring
4. **Leverage Utilities**: Use the utility classes for common operations

## Contributing

This is a core library meant to be reused across multiple projects. When adding new features:
1. Ensure they are generic and reusable
2. Follow existing patterns and conventions
3. Add comprehensive documentation
4. Include usage examples

## License

This project is licensed under the MIT License.

## Authors

- Initial work - Core Team

## Acknowledgments

- Spring Boot team for the excellent framework
- Project Lombok for reducing boilerplate
- Apache Commons for utility libraries
