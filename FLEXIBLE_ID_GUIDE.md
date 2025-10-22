# Flexible ID Configuration Guide

This library provides **maximum flexibility** for configuring entity ID types and audit field types. You can use generics to specify exactly what you want, without being locked into specific classes.

## The Problem We Solved

❌ **Old Approach** - Separate classes for each ID type:
```java
@Entity
public class ProductA extends BaseEntity { }         // Long ID only

@Entity
public class ProductB extends UuidBaseEntity { }     // UUID ID only

// What if I want String ID? Integer ID? Custom type?
```

✅ **New Approach** - Generic base entities:
```java
@Entity
public class Product extends GenericBaseEntity<UUID, UUID> { }  // UUID ID with UUID audit

@Entity
public class Order extends GenericBaseEntity<Long, Long> { }    // Long ID with Long audit

@Entity
public class Customer extends GenericBaseEntity<String, Long> { } // String ID with Long audit

// ANY Serializable type works!
```

## All Available Options

### Option 1: Use Generic Base Entities (Maximum Flexibility)

The `GenericBaseEntity<ID, AUDIT>` and `GenericSoftDeleteEntity<ID, AUDIT>` give you complete control:

```java
import pro.thinhha.core.entity.GenericBaseEntity;
import pro.thinhha.core.entity.GenericSoftDeleteEntity;

// UUID ID with UUID audit fields
@Entity
public class Product extends GenericBaseEntity<UUID, UUID> {
    private String name;
    private BigDecimal price;
}

// Long ID with Long audit fields
@Entity
public class Order extends GenericBaseEntity<Long, Long> {
    private String orderNumber;
}

// String ID with Long audit fields (mixed types!)
@Entity
public class Customer extends GenericBaseEntity<String, Long> {
    private String name;
}

// With soft delete support
@Entity
public class Invoice extends GenericSoftDeleteEntity<UUID, UUID> {
    private BigDecimal amount;
}
```

### Option 2: Use Convenience Classes (Simple and Clean)

For the most common cases, we provide convenience classes:

```java
import pro.thinhha.core.entity.BaseEntity;
import pro.thinhha.core.entity.SoftDeleteEntity;
import pro.thinhha.core.entity.UuidBaseEntity;
import pro.thinhha.core.entity.UuidSoftDeleteEntity;

// Long ID (extends GenericBaseEntity<Long, Long>)
@Entity
public class Product extends BaseEntity {
    private String name;
}

// Long ID with soft delete
@Entity
public class Order extends SoftDeleteEntity {
    private String orderNumber;
}

// UUID ID (extends GenericBaseEntity<UUID, UUID>)
@Entity
public class Customer extends UuidBaseEntity {
    private String name;
}

// UUID ID with soft delete
@Entity
public class Invoice extends UuidSoftDeleteEntity {
    private BigDecimal amount;
}
```

### Option 3: Mix and Match ID and Audit Types

You can use different types for entity ID vs audit fields:

```java
// UUID entity ID, but track users by Long ID
@Entity
public class Document extends GenericBaseEntity<UUID, Long> {
    private String title;
    // id: UUID
    // createdBy, updatedBy: Long
}

// Integer entity ID, tracked by UUID users
@Entity
public class LegacyRecord extends GenericBaseEntity<Integer, UUID> {
    private String data;
    // id: Integer
    // createdBy, updatedBy: UUID
}
```

## Complete Examples

### Example 1: E-Commerce Application with UUID

```java
// Products with UUID
@Entity
@Table(name = "products")
public class Product extends GenericSoftDeleteEntity<UUID, UUID> {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private Integer stock;

    // Automatically gets:
    // - UUID id (primary key)
    // - UUID createdBy, updatedBy, deletedBy
    // - LocalDateTime createdAt, updatedAt, deletedAt
    // - boolean deleted
    // - Long version (optimistic locking)
}

// Orders with UUID
@Entity
@Table(name = "orders")
public class Order extends GenericSoftDeleteEntity<UUID, UUID> {

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}

// Repository
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByDeletedFalse();

    @Query("SELECT p FROM Product p WHERE p.createdBy = :userId")
    List<Product> findByCreator(@Param("userId") UUID userId);
}
```

### Example 2: Traditional Application with Long IDs

```java
// Simple approach using convenience classes
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    // id, createdBy, updatedBy are all Long
}

@Entity
@Table(name = "posts")
public class Post extends SoftDeleteEntity {

    private String title;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}
```

### Example 3: Migration Scenario (Mixed Types)

```java
// Legacy system uses Integer IDs, new system tracks by UUID
@Entity
@Table(name = "legacy_customers")
public class LegacyCustomer extends GenericBaseEntity<Integer, UUID> {

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return super.getId();
    }

    private String customerCode;
    private String name;

    // id: Integer (legacy)
    // createdBy, updatedBy: UUID (new audit system)
}
```

### Example 4: Custom ID Type

```java
// Using String IDs (like MongoDB-style IDs)
@Entity
@Table(name = "documents")
public class Document extends GenericBaseEntity<String, Long> {

    @Override
    @Id
    @Column(length = 24)
    private String id;

    @PrePersist
    public void generateId() {
        if (id == null) {
            id = generateMongoStyleId();
        }
    }

    private String title;
    private String content;

    private String generateMongoStyleId() {
        // Your custom ID generation logic
        return /* generated ID */;
    }
}
```

## Field Details

### What You Get Automatically

When extending `GenericBaseEntity<ID, AUDIT>`:

| Field | Type | Description |
|-------|------|-------------|
| `id` | `ID` (generic) | Primary key - type you specify |
| `createdAt` | `LocalDateTime` | When record was created |
| `updatedAt` | `LocalDateTime` | When record was last updated |
| `createdBy` | `AUDIT` (generic) | Who created (user ID) |
| `updatedBy` | `AUDIT` (generic) | Who last updated (user ID) |
| `version` | `Long` | Optimistic locking version |

When extending `GenericSoftDeleteEntity<ID, AUDIT>` (includes all above plus):

| Field | Type | Description |
|-------|------|-------------|
| `deleted` | `boolean` | Soft delete flag |
| `deletedAt` | `LocalDateTime` | When record was deleted |
| `deletedBy` | `AUDIT` (generic) | Who deleted (user ID) |

## Automatic Audit Tracking

The audit fields are **automatically populated** by the library:

```java
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(String name, BigDecimal price) {
        // User logged in as UUID: 123e4567-e89b-12d3-a456-426614174000

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        productRepository.save(product);

        // Automatically set by the library:
        // product.getCreatedAt() = 2025-01-15T10:30:00
        // product.getUpdatedAt() = 2025-01-15T10:30:00
        // product.getCreatedBy() = 123e4567-e89b-12d3-a456-426614174000
        // product.getUpdatedBy() = 123e4567-e89b-12d3-a456-426614174000

        return product;
    }

    public void updateProduct(UUID productId, BigDecimal newPrice) {
        // User logged in as UUID: 999e4567-e89b-12d3-a456-426614174111

        Product product = productRepository.findById(productId).orElseThrow();
        product.setPrice(newPrice);

        productRepository.save(product);

        // Automatically updated:
        // product.getUpdatedAt() = 2025-01-15T11:45:00
        // product.getUpdatedBy() = 999e4567-e89b-12d3-a456-426614174111
        // product.getCreatedBy() = still 123e4567-... (unchanged)
    }

    public void deleteProduct(UUID productId) {
        // User logged in as UUID: 999e4567-e89b-12d3-a456-426614174111

        Product product = productRepository.findById(productId).orElseThrow();
        product.delete();  // Soft delete

        productRepository.save(product);

        // Automatically set:
        // product.isDeleted() = true
        // product.getDeletedAt() = 2025-01-15T12:00:00
        // product.getDeletedBy() = 999e4567-e89b-12d3-a456-426614174111
    }
}
```

## Configuration

### No Configuration Needed!

The library automatically detects your ID type from the entity definition. Just extend the appropriate base class:

```java
// This is ALL you need!
@Entity
public class Product extends GenericBaseEntity<UUID, UUID> {
    private String name;
}
```

### Optional Configuration

If you want to disable auditing entirely:

```properties
# Disable automatic auditing
app.auditing.enabled=false
```

If you want to use the old property-based configuration:

```properties
# Explicitly use Long-based auditing
app.auditing.id-type=long

# Or UUID-based auditing
app.auditing.id-type=uuid
```

But with the new generic approach, **you don't need this anymore!**

## Working with Repositories

Repositories work seamlessly with any ID type:

```java
// UUID-based repository
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByName(String name);
    List<Product> findByCreatedBy(UUID userId);
}

// Long-based repository
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCreatedBy(Long userId);
}

// String-based repository
@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    List<Document> findByTitleContaining(String keyword);
}
```

## Controllers with Generic IDs

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // UUID as path variable
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable UUID id) {
        return productRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());

        // Audit fields set automatically!
        Product saved = productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
```

## Querying Audit Fields

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Find products created by specific user
    List<Product> findByCreatedBy(UUID userId);

    // Find products updated after date
    @Query("SELECT p FROM Product p WHERE p.updatedAt > :since")
    List<Product> findUpdatedSince(@Param("since") LocalDateTime since);

    // Find active (non-deleted) products
    List<Product> findByDeletedFalse();

    // Complex query with audit fields
    @Query("""
        SELECT p FROM Product p
        WHERE p.createdBy = :userId
        AND p.deleted = false
        AND p.createdAt BETWEEN :start AND :end
        ORDER BY p.createdAt DESC
        """)
    List<Product> findUserProductsInDateRange(
        @Param("userId") UUID userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
}
```

## Migration Guide

### From Specific Classes to Generic

**Before:**
```java
@Entity
public class Product extends UuidBaseEntity {
    private String name;
}
```

**After (Option 1 - Explicit):**
```java
@Entity
public class Product extends GenericBaseEntity<UUID, UUID> {
    private String name;
}
```

**After (Option 2 - Convenience):**
```java
// No change needed! UuidBaseEntity now extends GenericBaseEntity<UUID, UUID>
@Entity
public class Product extends UuidBaseEntity {
    private String name;
}
```

Both work the same way! Choose based on your preference.

## Best Practices

### 1. Be Consistent Within Your Application

```java
// Good - consistent UUID usage
@Entity
public class Product extends GenericBaseEntity<UUID, UUID> { }

@Entity
public class Order extends GenericBaseEntity<UUID, UUID> { }

@Entity
public class Customer extends GenericBaseEntity<UUID, UUID> { }
```

### 2. Use the Same Type for ID and Audit (Usually)

```java
// Good - same types
@Entity
public class Product extends GenericBaseEntity<UUID, UUID> { }

// Avoid unless you have a specific reason
@Entity
public class Product extends GenericBaseEntity<UUID, Long> { }
```

### 3. Add @IdType Annotation for Documentation

```java
@Entity
@IdType(UUID.class)  // Optional but helpful for documentation
public class Product extends GenericBaseEntity<UUID, UUID> {
    private String name;
}
```

### 4. Override getId() for Custom Generation Strategies

```java
@Entity
public class Product extends GenericBaseEntity<UUID, UUID> {

    @Override
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    public UUID getId() {
        return super.getId();
    }

    private String name;
}
```

## Troubleshooting

### Issue: "Type mismatch" errors

**Problem:**
```java
@Entity
public class Product extends GenericBaseEntity<UUID, Long> {
    // Error: createdBy is Long but user ID is UUID
}
```

**Solution:** Match your audit type to your user entity ID type:
```java
@Entity
public class Product extends GenericBaseEntity<UUID, UUID> {
    // Now createdBy matches user UUID
}
```

### Issue: Audit fields are null

**Problem:** Audit fields (createdBy, updatedBy) are not being populated.

**Solution:**
1. Ensure Spring Security is set up with authenticated users
2. Check that your User entity has `getId()` method
3. Verify auditing is enabled (default):
```properties
app.auditing.enabled=true
```

### Issue: GenerationType not working

**Problem:** `@GeneratedValue(strategy = GenerationType.IDENTITY)` doesn't work with UUID.

**Solution:** Use the correct strategy for your ID type:
```java
// For UUID
@Override
@Id
@GeneratedValue(strategy = GenerationType.UUID)
public UUID getId() {
    return super.getId();
}

// For Long
@Override
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public Long getId() {
    return super.getId();
}
```

## Summary

✅ **Maximum Flexibility** - Use any Serializable type for IDs
✅ **Type-Safe** - Compile-time type checking
✅ **Clean Syntax** - `GenericBaseEntity<UUID, UUID>` is clear and concise
✅ **Backward Compatible** - Old convenience classes still work
✅ **Automatic Auditing** - Detects type automatically
✅ **No Configuration** - Just extend and go!

Choose the approach that fits your needs:
- **Simple apps:** Use `BaseEntity` or `UuidBaseEntity`
- **Complex apps:** Use `GenericBaseEntity<ID, AUDIT>` for full control
- **Mixed requirements:** Mix and match as needed!
