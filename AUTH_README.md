# Authentication Module

This core library includes a comprehensive authentication and authorization module with JWT-based security.

## Features

- User registration and login
- JWT token-based authentication
- Role-based access control (RBAC)
- Password encryption with BCrypt
- User management (CRUD operations)
- Soft delete support for users
- Automatic audit tracking (createdBy, updatedBy)
- RESTful API endpoints
- Token refresh mechanism

## Quick Start

### 1. Add Dependency

Add this library to your Spring Boot project:

```xml
<dependency>
    <groupId>pro.thinhha</groupId>
    <artifactId>core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Configure Application Properties

Add the following to your `application.properties` or `application.yml`:

```properties
# Database Configuration (required)
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
# IMPORTANT: Change this secret in production!
app.jwt.secret=your-super-secret-key-change-this-in-production-must-be-at-least-256-bits
app.jwt.expiration=86400000
app.jwt.refresh-expiration=604800000

# Security (optional - enabled by default)
app.security.enabled=true
```

### 3. Enable Component Scanning

Make sure your main application class enables component scanning for the core package:

```java
@SpringBootApplication
@EntityScan(basePackages = {"pro.thinhha.core", "your.package"})
@EnableJpaRepositories(basePackages = {"pro.thinhha.core", "your.package"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 4. Run Your Application

That's it! The authentication endpoints are now available:

## Available Endpoints

### Authentication Endpoints

#### 1. Register New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe",
    "status": "ACTIVE",
    "roles": ["ROLE_USER"]
  }
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "john_doe",
  "password": "securePassword123"
}
```

**Response:** Same as registration response with JWT token.

#### 3. Logout
```http
POST /api/auth/logout
Authorization: Bearer {token}
```

**Response:**
```json
{
  "message": "Logged out successfully"
}
```

#### 4. Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

### User Management Endpoints

#### 1. Get Current User Profile
```http
GET /api/users/me
Authorization: Bearer {token}
```

#### 2. Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer {token}
```

#### 3. Get All Users (Admin Only)
```http
GET /api/users?page=0&size=20&sort=createdAt,desc
Authorization: Bearer {token}
```

#### 4. Update User
```http
PUT /api/users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Doe",
  "phoneNumber": "+9876543210"
}
```

#### 5. Change Password
```http
PUT /api/users/{id}/password
Authorization: Bearer {token}
Content-Type: application/json

{
  "oldPassword": "oldPassword123",
  "newPassword": "newSecurePassword456"
}
```

#### 6. Delete User (Admin Only)
```http
DELETE /api/users/{id}
Authorization: Bearer {token}
```

#### 7. Assign Roles (Admin Only)
```http
PUT /api/users/{id}/roles
Authorization: Bearer {token}
Content-Type: application/json

["ROLE_USER", "ROLE_ADMIN"]
```

## Database Schema

The authentication module creates the following tables:

- `users` - User information with authentication fields
- `roles` - Available roles in the system
- `user_roles` - Many-to-many relationship between users and roles

### User Entity Fields

- `id` - Primary key (auto-generated)
- `username` - Unique username (required)
- `email` - Unique email (required)
- `password` - Encrypted password (required)
- `firstName` - User's first name
- `lastName` - User's last name
- `phoneNumber` - Contact number
- `status` - User status (ACTIVE, INACTIVE, etc.)
- `enabled` - Account enabled flag
- `accountNonExpired` - Account expiration flag
- `accountNonLocked` - Account lock flag
- `credentialsNonExpired` - Credentials expiration flag
- `deleted` - Soft delete flag
- `createdAt`, `updatedAt` - Audit timestamps
- `createdBy`, `updatedBy` - Audit user tracking
- `version` - Optimistic locking version

## Security Features

### JWT Token Authentication

- Tokens are signed using HMAC-SHA256
- Tokens include user roles for authorization
- Configurable token expiration
- Refresh token support

### Password Security

- Passwords are encrypted using BCrypt
- Minimum password length: 6 characters
- Passwords are never returned in API responses

### Role-Based Access Control

Default roles:
- `ROLE_USER` - Standard user (assigned by default on registration)
- `ROLE_ADMIN` - Administrator with full access

You can create additional roles as needed.

### Method-Level Security

Use Spring Security annotations in your controllers:

```java
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<?> adminOnlyEndpoint() {
    // Admin only logic
}

@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
public ResponseEntity<?> userOrAdminEndpoint() {
    // User or admin logic
}

@PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_ADMIN')")
public ResponseEntity<?> ownerOrAdminEndpoint(@PathVariable Long id) {
    // Owner or admin logic
}
```

## Customization

### Custom Security Configuration

You can override the default security configuration by creating your own `SecurityFilterChain` bean:

```java
@Configuration
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### Disable Authentication

To disable authentication in development or testing:

```properties
app.security.enabled=false
```

### Add Custom Roles

Create roles programmatically:

```java
@Service
public class RoleInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        if (!roleRepository.existsByName("ROLE_MODERATOR")) {
            roleRepository.save(new Role("ROLE_MODERATOR", "Moderator role"));
        }
    }
}
```

## Best Practices

1. **Change JWT Secret**: Always use a strong, random secret in production (minimum 256 bits)
2. **Use HTTPS**: Always use HTTPS in production to prevent token interception
3. **Token Storage**: Store JWT tokens securely on the client (avoid localStorage for sensitive apps)
4. **Token Expiration**: Use short-lived access tokens and refresh tokens for better security
5. **Password Policy**: Enforce strong password requirements in your application
6. **Rate Limiting**: Implement rate limiting on authentication endpoints
7. **Audit Logging**: Monitor authentication attempts and failures

## Troubleshooting

### Common Issues

1. **"Invalid JWT signature"**: Check that your JWT secret is consistent across restarts
2. **"User not found"**: Ensure the database tables are created (check `spring.jpa.hibernate.ddl-auto`)
3. **401 Unauthorized**: Verify the token is included in the Authorization header as "Bearer {token}"
4. **403 Forbidden**: User doesn't have the required role for the endpoint

## Examples

### Complete Registration and Login Flow

```bash
# 1. Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# 2. Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }'

# 3. Use the token to access protected endpoints
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Support

For issues, questions, or contributions, please refer to the main project repository.
