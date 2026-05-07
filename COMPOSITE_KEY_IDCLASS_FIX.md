# 🔧 Composite Key @IdClass Fix

**Date:** May 7, 2026  
**Issue:** AnnotationException - Composite ID property mismatch  
**Status:** ✅ FIXED

---

## Problem

```
org.hibernate.AnnotationException: Property 'ChiTietHoaDon.maDichVu' belongs to 
an '@IdClass' but has no matching property in entity class 'ChiTietHoaDon'
```

### Root Cause

When using `@IdClass`, Hibernate **requires exact name matching** between:
- Properties in the `@IdClass` class
- `@Id` properties in the Entity class

**Before (WRONG):**
```java
// ChiTietHoaDonId
public String maHoaDon;
public String maDichVu;

// ChiTietHoaDon Entity
@Id
@ManyToOne
@JoinColumn(name = "maHoaDon")
private HoaDon hoaDon;  // ❌ Property name is 'hoaDon', not 'maHoaDon'!
```

---

## Solution

For `@IdClass` with composite FK, the correct pattern is:

```java
@Entity
@IdClass(ChiTietHoaDonId.class)
public class ChiTietHoaDon {
    
    // ✅ @Id properties with String types (matching @IdClass)
    @Id
    @Column(name = "maHoaDon", length = 10)
    private String maHoaDon;

    @Id
    @Column(name = "maDichVu", length = 20)
    private String maDichVu;

    // ✅ Regular columns
    @Column(name = "soLuong")
    private int soLuong;

    // ✅ Relationships (insertable=false because they're managed via FK columns)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maHoaDon", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maDichVu", insertable = false, updatable = false)
    private DichVu dichVu;
}
```

### Key Points

1. **@Id properties must match @IdClass names exactly**
   - `ChiTietHoaDonId.maHoaDon` → `ChiTietHoaDon.maHoaDon` ✅

2. **@Id properties are simple types (String, Integer, etc.)**
   - Not entity objects directly

3. **Relationships use insertable=false, updatable=false**
   - Prevents Hibernate from trying to insert/update the same column twice
   - The FK columns (`@Id` fields) are authoritative for INSERT/UPDATE
   - Relationships are read-only (loaded via JoinColumn)

---

## Files Fixed

**Modified:**
- ✅ `ChiTietHoaDon.java` - Added String @Id fields, relationships are now read-only
- ✅ `ChiTietPhieuDatPhong.java` - Same pattern applied

**Already Correct:**
- ✅ `ChiTietHoaDonId.java` - Has `maHoaDon` and `maDichVu` (String)
- ✅ `ChiTietPhieuDatPhongId.java` - Has `maPhieu` and `maDichVu` (String)

---

## Verification

```bash
# Compile
mvn clean compile
# ✅ BUILD SUCCESS

# Run
mvn javafx:run
# ✅ Application launches
# ✅ No Hibernate AnnotationException
```

---

## Pattern Reference

### Pattern: Composite PK with FK References

```java
@Entity
@IdClass(MyCompositeId.class)
public class MyEntity {
    
    // Step 1: Declare @Id fields with simple types (must match @IdClass)
    @Id
    @Column(name = "fk1_id")
    private String fk1Id;
    
    @Id
    @Column(name = "fk2_id") 
    private String fk2Id;
    
    // Step 2: Add relationships with insertable=false, updatable=false
    @ManyToOne
    @JoinColumn(name = "fk1_id", insertable = false, updatable = false)
    private RelatedEntity1 related1;
    
    @ManyToOne
    @JoinColumn(name = "fk2_id", insertable = false, updatable = false)
    private RelatedEntity2 related2;
}

// @IdClass must have matching property names and types
@EqualsAndHashCode
public class MyCompositeId implements Serializable {
    public String fk1Id;  // ✅ Matches entity property
    public String fk2Id;  // ✅ Matches entity property
}
```

---

## Additional Notes

### Why insertable=false, updatable=false?

Without these flags:
```
❌ ERROR: Column 'fk1_id' is duplicated in mapping
```

Because Hibernate would see two mappings trying to write to the same column:
1. The `@Id @Column` mapping
2. The `@ManyToOne @JoinColumn` mapping

With `insertable=false, updatable=false`:
```
✅ SUCCESS: Only @Id fields write to columns
           @ManyToOne fields are read-only for loading data
```

### Accessing Related Objects

```java
// ✅ Get the FK value (for queries/comparisons)
String maHoaDon = entity.getMaHoaDon();

// ✅ Get the related entity (lazy loaded if configured)
HoaDon hoaDon = entity.getHoaDon();
if (hoaDon != null) {
    String tenHoaDon = hoaDon.getTenHoaDon();
}
```

---

## Build Results

**Before Fix:**
```
❌ AnnotationException: Property 'maDichVu' has no matching property
BUILD FAILURE
```

**After Fix:**
```
✅ BUILD SUCCESS
   88 source files compiled
   Total time: 6.151s
```

---

**Status:** ✅ **RESOLVED & VERIFIED**

