# 📚 COMPREHENSIVE REFERENCE - Composite Key Mapping in Hibernate/JPA

**Last Updated:** 2026-05-07  
**Status:** ✅ Verified & Tested  
**Project:** Hotel Management System (Clean Architecture)

---

## Table of Contents

1. [The Problem We Solved](#problem)
2. [Complete Solution Pattern](#solution)
3. [Step-by-Step Implementation](#implementation)
4. [Common Pitfalls](#pitfalls)
5. [Testing & Verification](#testing)
6. [Real-World Examples](#examples)

---

## <a name="problem"></a> The Problem We Solved

### Error Message
```
org.hibernate.AnnotationException: Property 'ChiTietHoaDon.maDichVu' 
belongs to an '@IdClass' but has no matching property in entity class 
'iuh.fit.core.entity.ChiTietHoaDon'
```

### What Causes This?

When you have a table with a composite primary key where the PK columns are also foreign keys:

```
Database Table: ChiTietHoaDon
├── maHoaDon (PK, FK to HoaDon)
├── maDichVu (PK, FK to DichVu)
├── soLuong
└── ghiChu
```

Hibernate needs to map this correctly OR you get the AnnotationException.

### The Wrong Approach (What We Had)

```java
@Entity
@IdClass(ChiTietHoaDonId.class)
public class ChiTietHoaDon {
    @Id
    @ManyToOne
    @JoinColumn(name = "maHoaDon")
    private HoaDon hoaDon;  // ❌ Property name is 'hoaDon'
    
    @Id
    @ManyToOne
    @JoinColumn(name = "maDichVu")
    private DichVu dichVu;  // ❌ Property name is 'dichVu'
}

public class ChiTietHoaDonId implements Serializable {
    public String maHoaDon;  // ❌ Hibernate looks for property 'maHoaDon'
    public String maDichVu;  // ❌ Hibernate looks for property 'maDichVu'
}
// ERROR: Property names don't match!
```

---

## <a name="solution"></a> Complete Solution Pattern

### The Correct Approach (What We Fixed)

```java
@Entity
@Table(name = "ChiTietHoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChiTietHoaDonId.class)
public class ChiTietHoaDon {
    
    // ✅ PART 1: FK values as simple @Id properties
    // These MUST match the names in @IdClass
    @Id
    @Column(name = "maHoaDon", length = 10)
    private String maHoaDon;  // Name matches @IdClass field ✅

    @Id
    @Column(name = "maDichVu", length = 20)
    private String maDichVu;  // Name matches @IdClass field ✅

    // ✅ PART 2: Regular column
    @Column(name = "soLuong")
    private int soLuong;

    // ✅ PART 3: Relationships (read-only)
    // These map the same columns but allow lazy loading
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maHoaDon", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maDichVu", insertable = false, updatable = false)
    private DichVu dichVu;
}

// ✅ PART 4: @IdClass must have matching property names
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietHoaDonId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String maHoaDon;  // ✅ Exactly matches entity @Id property name
    public String maDichVu;  // ✅ Exactly matches entity @Id property name
}
```

---

## <a name="implementation"></a> Step-by-Step Implementation

### Step 1: Create @IdClass

```java
package iuh.fit.core.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode  // ✅ IMPORTANT: Must have equals/hashCode
public class ChiTietHoaDonId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String maHoaDon;
    public String maDichVu;
}
```

### Step 2: Create Entity with Correct Mapping

```java
package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ChiTietHoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChiTietHoaDonId.class)  // ✅ Reference ID class
public class ChiTietHoaDon {
    
    // ✅ Declare @Id properties EXACTLY as in @IdClass
    @Id
    @Column(name = "maHoaDon", length = 10)
    private String maHoaDon;

    @Id
    @Column(name = "maDichVu", length = 20)
    private String maDichVu;

    @Column(name = "soLuong", nullable = false)
    private int soLuong;

    // ✅ Add relationships with insertable=false, updatable=false
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maHoaDon", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maDichVu", insertable = false, updatable = false)
    private DichVu dichVu;
}
```

### Step 3: Update Mapper (If Used)

```java
package iuh.fit.infrastructure.mapper;

public class ChiTietHoaDonMapper {
    
    public static ChiTietHoaDonDTO entityToDTO(ChiTietHoaDon entity) {
        if (entity == null) return null;
        
        ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
        
        // ✅ Use the String @Id fields
        dto.setMaHoaDon(entity.getMaHoaDon());
        dto.setMaDichVu(entity.getMaDichVu());
        dto.setSoLuong(entity.getSoLuong());
        
        // Optional: Extract info from relationships
        if (entity.getHoaDon() != null) {
            dto.setTenHoaDon(entity.getHoaDon().getTenHoaDon());
        }
        
        return dto;
    }

    public static ChiTietHoaDon dtoToEntity(ChiTietHoaDonDTO dto) {
        if (dto == null) return null;
        
        ChiTietHoaDon entity = new ChiTietHoaDon();
        
        // ✅ Set the String @Id fields
        entity.setMaHoaDon(dto.getMaHoaDon());
        entity.setMaDichVu(dto.getMaDichVu());
        entity.setSoLuong(dto.getSoLuong());
        
        // ❌ Don't set relationships - Hibernate loads them via @JoinColumn
        
        return entity;
    }
}
```

### Step 4: Use in Repository

```java
public class ChiTietHoaDonRepositoryImpl implements IChiTietHoaDonRepository {
    
    @Override
    public ChiTietHoaDon findById(String maHoaDon, String maDichVu) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            // Create composite key
            ChiTietHoaDonId id = new ChiTietHoaDonId(maHoaDon, maDichVu);
            
            // Find by composite key
            ChiTietHoaDon detail = em.find(ChiTietHoaDon.class, id);
            
            return detail;  // ✅ hoaDon and dichVu already loaded
        } finally {
            em.close();
        }
    }

    @Override
    public void save(ChiTietHoaDon detail) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(detail);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
```

---

## <a name="pitfalls"></a> Common Pitfalls to Avoid

### ❌ PITFALL 1: Names Don't Match

```java
// WRONG
public class CompositeId {
    public String fk1Id;  // Property name
}

@Entity
@IdClass(CompositeId.class)
public class Entity {
    @Id private String fk1;  // ❌ Different name!
}
```

**FIX:**
```java
// CORRECT
@Id private String fk1Id;  // ✅ Exact match
```

### ❌ PITFALL 2: Missing insertable=false

```java
// WRONG
@Id @Column(name = "fk") private String fk;
@ManyToOne @JoinColumn(name = "fk") private Related entity;
// ❌ ERROR: Column 'fk' is duplicated!
```

**FIX:**
```java
// CORRECT
@Id @Column(name = "fk") private String fk;
@ManyToOne @JoinColumn(name = "fk", insertable=false, updatable=false)
private Related entity;
```

### ❌ PITFALL 3: Using Entity Objects in @IdClass

```java
// WRONG
public class CompositeId {
    public HoaDon hoaDon;  // ❌ Can't use entity objects
    public DichVu dichVu;
}
```

**FIX:**
```java
// CORRECT
public class CompositeId {
    public String maHoaDon;  // ✅ Simple types only
    public String maDichVu;
}
```

### ❌ PITFALL 4: No equals/hashCode in @IdClass

```java
// WRONG
public class CompositeId implements Serializable {
    public String id1;
    public String id2;
    // ❌ Missing equals() and hashCode()
}
```

**FIX:**
```java
// CORRECT
@EqualsAndHashCode
public class CompositeId implements Serializable {
    public String id1;
    public String id2;
    // ✅ Lombok generates equals() and hashCode()
}
```

---

## <a name="testing"></a> Testing & Verification

### Test 1: Compilation

```bash
mvn clean compile
```

**Expected:**
```
✅ BUILD SUCCESS
✅ 0 errors
```

### Test 2: Runtime

```bash
mvn javafx:run
```

**Expected:**
```
✅ Application launches
✅ No Hibernate AnnotationException
✅ Login screen appears
```

### Test 3: Entity Loading

```java
@Test
public void testCompositeKeyLoading() {
    EntityManager em = JpaConfig.getEntityManager();
    
    // Create composite key
    ChiTietHoaDonId id = new ChiTietHoaDonId("HD001", "DV001");
    
    // Load entity
    ChiTietHoaDon detail = em.find(ChiTietHoaDon.class, id);
    
    // Verify
    assert detail != null;
    assert detail.getMaHoaDon().equals("HD001");
    assert detail.getSoLuong() > 0;
    
    // Verify relationships loaded (FetchType.EAGER)
    assert detail.getHoaDon() != null;
    assert detail.getDichVu() != null;
    
    System.out.println("✅ Composite key test passed!");
}
```

### Test 4: Insertion

```java
@Test
public void testInsertion() {
    ChiTietHoaDon detail = new ChiTietHoaDon();
    detail.setMaHoaDon("HD002");
    detail.setMaDichVu("DV002");
    detail.setSoLuong(3);
    
    repository.save(detail);
    
    // Verify
    ChiTietHoaDon loaded = repository.findById("HD002", "DV002");
    assert loaded != null;
    assert loaded.getSoLuong() == 3;
    
    System.out.println("✅ Insertion test passed!");
}
```

---

## <a name="examples"></a> Real-World Examples

### Example 1: Hotel Chain (Like Our Project)

```
Table: ChiTietHoaDon (Bill Details)
├── maHoaDon (PK, FK)
├── maDichVu (PK, FK)
└── soLuong

// Solution:
@Id private String maHoaDon;
@Id private String maDichVu;

@ManyToOne @JoinColumn(name = "maHoaDon", insertable=false, updatable=false)
private HoaDon hoaDon;

@ManyToOne @JoinColumn(name = "maDichVu", insertable=false, updatable=false)
private DichVu dichVu;
```

### Example 2: E-Commerce (Order Line Items)

```
Table: OrderLineItem
├── orderId (PK, FK)
├── itemId (PK, FK)
├── quantity
└── price

// Solution:
@Id private String orderId;
@Id private String itemId;

@ManyToOne @JoinColumn(name = "orderId", insertable=false, updatable=false)
private Order order;

@ManyToOne @JoinColumn(name = "itemId", insertable=false, updatable=false)
private Item item;
```

### Example 3: Many-to-Many with Extra Data

```
Table: StudentCourse (Join table with payload)
├── studentId (PK, FK)
├── courseId (PK, FK)
├── grade
└── attendance

// Solution:
@Id private String studentId;
@Id private String courseId;

@ManyToOne @JoinColumn(name = "studentId", insertable=false, updatable=false)
private Student student;

@ManyToOne @JoinColumn(name = "courseId", insertable=false, updatable=false)
private Course course;
```

---

## Key Takeaways

| Aspect | Rule |
|--------|------|
| **@IdClass** | Must have properties matching entity @Id fields |
| **@Id properties** | Use simple types (String, Integer, Long, etc.) |
| **Property names** | Must match exactly between entity and @IdClass |
| **Relationships** | Add `insertable=false, updatable=false` |
| **Composite Key** | Must implement Serializable, have equals/hashCode |
| **Serializable** | Should add serialVersionUID for stability |

---

## Documentation Files in This Project

- `COMPOSITE_KEY_IDCLASS_FIX.md` - Focused fix documentation
- `IDCLASS_FIX_COMPLETE.md` - Complete fix summary
- `SESSION_COMPLETE_IDCLASS_FIX.md` - Session completion report
- `DEVELOPER_QUICK_REFERENCE.md` - Developer reference
- `This file` - Comprehensive reference guide

---

**Created:** 2026-05-07  
**Status:** ✅ Production Ready  
**Quality:** ⭐⭐⭐⭐⭐

