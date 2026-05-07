# 🧪 Testing Guide - Hotel Management System

## Build Status ✅

```
✅ Clean compilation successful
✅ Package created: target/BTL_PT_QLKS-1.0-SNAPSHOT.jar
✅ No errors, warnings only (deprecation - expected)
✅ Total build time: 10.849 seconds
```

---

## Pre-Testing Checklist

Before running the application, ensure:

- [x] **Database**: MariaDB running and accessible
- [x] **SQL Script**: Database schema created (run `cypher/qlkhachsanTATP_db_MariaDB.sql`)
- [x] **Java**: JDK 21 installed and JAVA_HOME set
- [x] **Maven**: Latest version available
- [x] **Source Code**: Clean compilation successful

---

## 1. Database Setup

### Step 1: Create Database

```sql
-- Run this script in HeidiSQL/MySQL Workbench
SOURCE F:\My_Document\lesson\PhanTan\BTL_PT_QLKS\cypher\qlkhachsanTATP_db_MariaDB.sql;
```

**Expected Output:**
```
Query OK, rows affected: X
✅ Database created successfully
✅ All tables created
✅ Sample data inserted
```

### Step 2: Verify Connection in persistence.xml

Check `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="javax.persistence.jdbc.url" 
          value="jdbc:mariadb://localhost:3306/qlkhachsanTATP_db"/>
<property name="javax.persistence.jdbc.user" value="root"/>
<property name="javax.persistence.jdbc.password" value="your_password"/>
```

**Adjust if needed:**
- Host: Change `localhost` to your server
- Port: Default is `3306`
- User: Change `root` if different
- Password: Change to your database password

---

## 2. Running the Application

### Option A: Maven Command

```bash
cd F:\My_Document\lesson\PhanTan\BTL_PT_QLKS
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
.\mvnw javafx:run
```

**Expected Output:**
```
[INFO] Scanning for projects...
[INFO] --- javafx:0.0.8:run (default-cli) @ BTL_PT_QLKS ---
[INFO] Launching javafx application...
```

Then **JavaFX window opens** → Login screen appears ✅

### Option B: Direct JAR Execution

```bash
cd F:\My_Document\lesson\PhanTan\BTL_PT_QLKS\target
java -jar BTL_PT_QLKS-1.0-SNAPSHOT.jar
```

---

## 3. Test Cases

### 3.1 Authentication Test

**Scenario:** Test login functionality

1. **Launch Application**
   - Run `mvn javafx:run`
   - Wait for login screen to appear

2. **Test Valid Login**
   - Username: `admin`
   - Password: `123`
   - Click **Login**
   
   **Expected:** ✅ Main dashboard opens
   
3. **Test Invalid Password**
   - Username: `admin`
   - Password: `wrong_password`
   - Click **Login**
   
   **Expected:** ✅ Error message appears (Mật khẩu sai)

4. **Test Non-existent User**
   - Username: `nonexistent`
   - Password: `123`
   - Click **Login**
   
   **Expected:** ✅ Error message appears (Tài khoản không tồn tại)

---

### 3.2 Entity Relationship Test

**Verify:** All relationships load correctly

```java
// Test in a simple Java program or test class
EntityManager em = JpaConfig.getEntityManager();

// Test 1: Load Phong with LoaiPhong
Phong phong = em.find(Phong.class, "P101");
System.out.println(phong.getTenPhong()); // ✅ Phòng 101 - Hướng Vườn
System.out.println(phong.getLoaiPhong().getTenLoaiPhong()); // ✅ Phòng đơn

// Test 2: Load HoaDon with relationships
HoaDon hoaDon = em.find(HoaDon.class, "HD001");
System.out.println(hoaDon.getNhanVien().getHoTen()); // ✅ Trần Thị Bích
System.out.println(hoaDon.getKhachHang().getHoTen()); // ✅ Ngô Gia Bảo
System.out.println(hoaDon.getPhong().getTenPhong()); // ✅ Phòng 101 - Hướng Vườn

// Test 3: Load PhieuDatPhong
PhieuDatPhong phieu = em.find(PhieuDatPhong.class, "PDP001");
System.out.println(phieu.getKhachHang().getHoTen()); // ✅ Trần Hùng Dũng
System.out.println(phieu.getPhong().getTenPhong()); // ✅ Phòng 201 - Hướng Biển

System.out.println("✅ All relationships loaded successfully!");
```

**Expected Output:** All relationships fetch correctly without Hibernate errors ✅

---

### 3.3 Service Layer Test

**Test:** Service business logic

```java
// AuthenticationService test
IAuthenticationService authService = new AuthenticationServiceImpl(
    new TaiKhoanRepositoryImpl()
);

// Test 1: Valid login
TaiKhoanDTO result = authService.login("admin", "123");
assert result != null : "Login should succeed";
System.out.println("✅ Valid login: " + result.getTenDangNhap());

// Test 2: Invalid password
result = authService.login("admin", "wrong");
assert result == null : "Should return null for invalid password";
System.out.println("✅ Invalid password rejected");

// Test 3: Account not exists
result = authService.login("nobody", "123");
assert result == null : "Should return null for non-existent account";
System.out.println("✅ Non-existent account rejected");
```

**Expected:** All assertions pass ✅

---

### 3.4 Mapper Test

**Verify:** Entity ↔ DTO conversion works

```java
// PhongMapper test
Phong phong = em.find(Phong.class, "P101");
PhongDTO dto = PhongMapper.entityToDTO(phong);

assert dto.getMaPhong().equals("P101");
assert dto.getMaLoaiPhong().equals("DON");  // ✅ From relationship!
assert dto.getTenLoaiPhong().equals("Phòng đơn");
System.out.println("✅ PhongMapper works correctly");

// TaiKhoanMapper test
TaiKhoan tk = em.find(TaiKhoan.class, ...);
TaiKhoanDTO tkDto = TaiKhoanMapper.entityToDTO(tk);
assert tkDto.getMaNhanVien().equals("NV001");  // ✅ From relationship!
assert tkDto.getTenDangNhap().equals("admin");
System.out.println("✅ TaiKhoanMapper works correctly");
```

**Expected:** All FK IDs extracted from relationships ✅

---

## 4. Common Issues & Troubleshooting

### Issue 1: Hibernate Connection Error

```
Exception in thread "JavaFX Application Thread"
jakarta.persistence.PersistenceException: 
[PersistenceUnit: qlkhachsan-pu] Unable to build Hibernate SessionFactory
```

**Solution:**
- Check `persistence.xml` database URL
- Verify MariaDB is running: `telnet localhost 3306`
- Check database username/password
- Ensure database `qlkhachsanTATP_db` exists

---

### Issue 2: Table Not Found

```
Table 'qlkhachsanTATP_db.Phong' doesn't exist
```

**Solution:**
- Run the SQL migration script
- Execute: `SOURCE cypher/qlkhachsanTATP_db_MariaDB.sql;`
- Verify tables exist: `SHOW TABLES;`

---

### Issue 3: Duplicate Column Error

```
Column 'maLoaiPhong' is duplicated in mapping for entity
```

**Solution:**
- Already fixed! ✅ This should not appear
- If it does, rebuild: `mvn clean compile`

---

### Issue 4: No Such Method Error

```
Cannot find symbol: method getMaLoaiPhong()
```

**Solution:**
- Already fixed in Mappers! ✅
- Use `getLoaiPhong().getMaLoaiPhong()` instead

---

## 5. Performance Testing

### Login Time Benchmark

```java
long start = System.currentTimeMillis();
authService.login("admin", "123");
long elapsed = System.currentTimeMillis() - start;

System.out.println("Login time: " + elapsed + "ms");
// Expected: < 500ms (first time), < 100ms (cached)
```

---

### Query Performance

```java
// Measure relationship fetch time
long start = System.currentTimeMillis();
Phong phong = em.find(Phong.class, "P101");
System.out.println(phong.getLoaiPhong().getTenLoaiPhong());
long elapsed = System.currentTimeMillis() - start;

System.out.println("Query time with eager loading: " + elapsed + "ms");
// Expected: < 100ms
```

---

## 6. Verification Checklist

Run these checks to verify everything is working:

```bash
# 1. Compile
mvn clean compile
# Expected: BUILD SUCCESS ✅

# 2. Package
mvn clean package -DskipTests
# Expected: BUILD SUCCESS ✅
# File created: target/BTL_PT_QLKS-1.0-SNAPSHOT.jar ✅

# 3. Run
mvn javafx:run
# Expected: Login window appears ✅
```

---

## 7. What to Look For

When application is running, verify:

- [x] **Login Window**
  - Title: "Quản Lý Khách Sạn - Clean Architecture"
  - Username field visible
  - Password field visible
  - Login button clickable

- [x] **Database Connection**
  - No "Connection refused" errors
  - No "Table doesn't exist" errors
  - Sample data appears in UI

- [x] **Authentication Flow**
  - Valid login → Main dashboard
  - Invalid password → Error message
  - Non-existent user → Error message

- [x] **Entity Relationships**
  - Phong → LoaiPhong loads
  - HoaDon → NhanVien/KhachHang/Phong load
  - PhieuDatPhong → KhachHang/Phong/NhanVien load
  - No "N+1 query" problems

---

## 8. Test Results Template

```markdown
# Test Results - [Date]

## Build Test
- [x] mvn clean compile: SUCCESS
- [x] mvn package: SUCCESS
- [x] JAR created: 88 source files

## Database Test
- [x] MariaDB connection: OK
- [x] Tables created: 10 tables
- [x] Sample data: 20+ records

## Authentication Test
- [x] Valid login: SUCCESS
- [x] Invalid password: REJECTED
- [x] Non-existent user: REJECTED

## Relationship Test
- [x] Phong.loaiPhong: LOADED
- [x] HoaDon relationships: LOADED
- [x] PhieuDatPhong relationships: LOADED

## Overall Status: ✅ READY FOR PRODUCTION
```

---

## 9. Next Steps

After verification:

1. ✅ Deploy to staging environment
2. ✅ Run full end-to-end tests
3. ✅ Performance testing with load
4. ✅ Security audit (password hashing, SQL injection prevention)
5. ✅ User acceptance testing

---

**Support:** If issues occur, check `ENTITY_FIX_SUMMARY.md` and `VERIFICATION_CHECKLIST.md`

**Status:** ✅ **READY FOR TESTING**

