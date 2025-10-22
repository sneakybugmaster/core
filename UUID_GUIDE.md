# UUID Support Guide

This library supports both **Long** (auto-increment) and **UUID** as primary key types for entities and audit fields.

## Why Use UUID?

### Benefits of UUID
✅ **Security** - Non-sequential, harder to guess or enumerate
✅ **Distributed Systems** - No collision risk when generating IDs across multiple servers
✅ **Privacy** - Don't expose the total number of records
✅ **Merging Data** - Easy to merge databases without ID conflicts
✅ **API Design** - Better for public APIs (RESTful resource identifiers)

### When to Use Long vs UUID

**Use Long (Auto-increment) when:**
- Simple single-server applications
- Performance is critical (slightly faster than UUID)
- Sequential ordering is beneficial
- Database size is a concern (Long = 8 bytes, UUID = 16 bytes)

**Use UUID when:**
- Building distributed systems or microservices
- Security/privacy is important (don't expose record counts)
- Merging data from multiple sources
- Building public APIs
- Need client-side ID generation

## Quick Start with UUID

### Option 1: Using UUID Entities

The library provides UUID-based entity classes ready to use:

```java
// 1. Use UUID base entities
@Entity
public class Product extends UuidBaseEntity {
    private String name;
    private BigDecimal price;
}

// 2. Or with soft delete support
@Entity
public class Order extends UuidSoftDeleteEntity {
    private String orderNumber;
    private BigDecimal total;
}
```

### Option 2: Using UUID Authentication

The library includes UUID-based User and Role entities:

```java
// Import UUID-based auth entities
import pro.thinhha.core.auth.uuid.entity.UuidUser;
import pro.thinhha.core.auth.uuid.entity.UuidRole;

// Use them in your application
@Service
public class MyService {
    @Autowired
    private JpaRepository<UuidUser, UUID> userRepository;

    public UuidUser createUser(String username) {
        UuidUser user = UuidUser.builder()
            .username(username)
            .email(username + "@example.com")
            .build();
        return userRepository.save(user);
    }
}
```

## Configuration

### 1. Enable UUID Auditing

Add to your `application.properties`:

```properties
# Use UUID for audit fields (createdBy, updatedBy, deletedBy)
app.auditing.id-type=uuid

# Default is Long - to explicitly set Long:
# app.auditing.id-type=long
```

### 2. Database Configuration

#### PostgreSQL (Recommended)
PostgreSQL has native UUID support:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

#### MySQL 8.0+
MySQL can store UUIDs as BINARY(16):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

#### H2 (Testing)
H2 supports UUID:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
```

## Entity Examples

### Basic UUID Entity

```java
@Entity
@Table(name = "products")
public class Product extends UuidBaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    // Automatically gets:
    // - UUID id (primary key)
    // - LocalDateTime createdAt, updatedAt
    // - UUID createdBy, updatedBy
    // - Long version (optimistic locking)
}
```

### UUID Entity with Soft Delete

```java
@Entity
@Table(name = "orders")
public class Order extends UuidSoftDeleteEntity {

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private BigDecimal total;

    // Automatically gets all BaseEntity fields plus:
    // - boolean deleted
    // - LocalDateTime deletedAt
    // - UUID deletedBy

    // Soft delete methods:
    // - delete()
    // - delete(UUID deletedBy)
    // - restore()
}
```

### UUID User Entity

```java
import pro.thinhha.core.auth.uuid.entity.UuidUser;
import pro.thinhha.core.auth.uuid.entity.UuidRole;

@Service
public class UserService {

    @Autowired
    private JpaRepository<UuidUser, UUID> userRepository;

    @Autowired
    private JpaRepository<UuidRole, UUID> roleRepository;

    public UuidUser registerUser(String username, String email, String password) {
        UuidUser user = UuidUser.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .status(Status.ACTIVE)
            .enabled(true)
            .build();

        // Add default role
        UuidRole userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow();
        user.addRole(userRole);

        return userRepository.save(user);
    }
}
```

## Repositories

### UUID Repositories

```java
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByName(String name);

    List<Product> findByPriceGreaterThan(BigDecimal price);

    @Query("SELECT p FROM Product p WHERE p.createdBy = :userId")
    List<Product> findByCreatedBy(@Param("userId") UUID userId);
}
```

## Controllers with UUID

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product instanceof UuidSoftDeleteEntity) {
            // Soft delete - will automatically set deletedBy to current user
            ((UuidSoftDeleteEntity) product).delete();
            productRepository.save(product);
        } else {
            // Hard delete
            productRepository.delete(product);
        }

        return ResponseEntity.noContent().build();
    }
}
```

## Audit Tracking with UUID

The library automatically tracks who created, updated, or deleted records:

```java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void demonstrateAuditing() {
        // When a logged-in user creates an order
        Order order = new Order();
        order.setOrderNumber("ORD-001");
        order.setTotal(new BigDecimal("99.99"));

        orderRepository.save(order);
        // Automatically sets:
        // - createdAt = now
        // - updatedAt = now
        // - createdBy = current user's UUID
        // - updatedBy = current user's UUID

        // When updating
        order.setTotal(new BigDecimal("149.99"));
        orderRepository.save(order);
        // Automatically updates:
        // - updatedAt = now
        // - updatedBy = current user's UUID
        // - createdAt and createdBy remain unchanged

        // Soft delete
        order.delete();  // Sets deletedBy to current user's UUID automatically
        orderRepository.save(order);
    }
}
```

## Querying Audit Fields

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Find orders created by a specific user
    List<Order> findByCreatedBy(UUID userId);

    // Find orders updated by a specific user
    List<Order> findByUpdatedBy(UUID userId);

    // Find non-deleted orders
    List<Order> findByDeletedFalse();

    // Find orders deleted by a specific user
    List<Order> findByDeletedByAndDeletedTrue(UUID userId);

    // Complex query
    @Query("""
        SELECT o FROM Order o
        WHERE o.createdBy = :userId
        AND o.deleted = false
        AND o.createdAt >= :since
        """)
    List<Order> findActiveOrdersByUserSince(
        @Param("userId") UUID userId,
        @Param("since") LocalDateTime since
    );
}
```

## Security Utilities

The library provides utilities to work with the current authenticated user:

```java
import pro.thinhha.core.util.SecurityUtil;

@Service
public class MyService {

    public void demonstrateSecurityUtil() {
        // Get current user's UUID
        Optional<UUID> userId = SecurityUtil.getCurrentUserId();

        // Get current username
        Optional<String> username = SecurityUtil.getCurrentUsername();

        // Get current user object
        Optional<UuidUser> user = SecurityUtil.getCurrentUser();

        // Check if authenticated
        boolean isAuthenticated = SecurityUtil.isAuthenticated();

        // Use in your logic
        userId.ifPresent(id -> {
            System.out.println("Current user ID: " + id);
        });
    }
}
```

## Migration from Long to UUID

If you're migrating an existing application from Long to UUID:

### 1. Database Migration

```sql
-- PostgreSQL example
ALTER TABLE products DROP CONSTRAINT products_pkey;
ALTER TABLE products ALTER COLUMN id TYPE UUID USING uuid_generate_v4();
ALTER TABLE products ADD PRIMARY KEY (id);

ALTER TABLE products ALTER COLUMN created_by TYPE UUID USING NULL;
ALTER TABLE products ALTER COLUMN updated_by TYPE UUID USING NULL;
```

### 2. Entity Migration

```java
// Before
@Entity
public class Product extends BaseEntity {
    // Long id, Long createdBy, Long updatedBy
}

// After
@Entity
public class Product extends UuidBaseEntity {
    // UUID id, UUID createdBy, UUID updatedBy
}
```

### 3. Repository Migration

```java
// Before
public interface ProductRepository extends JpaRepository<Product, Long> { }

// After
public interface ProductRepository extends JpaRepository<Product, UUID> { }
```

### 4. Update Configuration

```properties
# Change auditing type
app.auditing.id-type=uuid
```

## Best Practices

### 1. Use UUID v4 (Random)
The library uses `GenerationType.UUID` which generates UUID v4 (random) by default.

### 2. Index UUID Columns
```java
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_created_by", columnList = "created_by"),
    @Index(name = "idx_updated_by", columnList = "updated_by")
})
public class Product extends UuidBaseEntity {
    // ...
}
```

### 3. Use Binary Storage in MySQL
For MySQL, store UUIDs as BINARY(16) for better performance:

```java
@Column(name = "id", columnDefinition = "BINARY(16)")
private UUID id;
```

### 4. Format UUIDs Consistently in APIs
```java
@JsonFormat(shape = JsonFormat.Shape.STRING)
private UUID id;
```

### 5. Validate UUID in Controllers
```java
@GetMapping("/{id}")
public ResponseEntity<Product> getProduct(@PathVariable String id) {
    try {
        UUID uuid = UUID.fromString(id);
        // Process...
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}
```

## Available Classes

### Base Entities
- `BaseEntity` - Long ID with audit fields
- `UuidBaseEntity` - UUID ID with audit fields
- `SoftDeleteEntity` - Long ID with soft delete
- `UuidSoftDeleteEntity` - UUID ID with soft delete

### Auth Entities
- `User`, `Role` - Long ID based authentication
- `UuidUser`, `UuidRole` - UUID ID based authentication

### Configurations
- `JpaAuditingConfig` - Long-based auditing (default)
- `UuidJpaAuditingConfig` - UUID-based auditing

### Utilities
- `SecurityUtil` - Extract current user info (works with both Long and UUID)

## Performance Considerations

| Aspect | Long | UUID |
|--------|------|------|
| Storage Size | 8 bytes | 16 bytes |
| Index Size | Smaller | Larger |
| Insert Performance | Faster (sequential) | Slightly slower (random) |
| Clustering | Better (sequential) | Worse (random) |
| Security | Predictable | Non-guessable |
| Distribution | Conflicts possible | No conflicts |

**Recommendation:** Use UUID for distributed systems, APIs, and security-sensitive applications. Use Long for simple applications where performance is critical.

## Troubleshooting

### Issue: "No AuditorAware bean found"
**Solution:** Make sure you've set `app.auditing.id-type` in application.properties

### Issue: UUID stored as String in database
**Solution:** Use proper dialect for your database (PostgreSQL, MySQL 8+)

### Issue: Can't find UuidUser class
**Solution:** Import from `pro.thinhha.core.auth.uuid.entity.UuidUser`

### Issue: Audit fields are null
**Solution:** Ensure Spring Security authentication is set up correctly

## Examples Repository

For complete working examples, see the examples directory (coming soon).

## Support

For issues or questions about UUID support, please open an issue on GitHub.
