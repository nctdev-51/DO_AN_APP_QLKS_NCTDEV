# ✅ PLAIN TEXT PASSWORD AUTHENTICATION - MIGRATION COMPLETE

## 🔄 THAY ĐỔI ĐÃ THỰC HIỆN

### **1. Entity TaiKhoan - Cột Mapping**

**FILE**: `iuh.fit.core.entity.TaiKhoan`

```java
// ❌ TRƯỚC (Sai):
@Id
@Column(name = "tai_khoan", length = 50)
private String taiKhoan;

// ✅ SAU (Đúng):
@Id
@Column(name = "ten_dang_nhap", length = 50)
private String tenDangNhap;

@Column(name = "mat_khau", length = 255, nullable = false)
private String matKhau; // Plain text (NOT hashed)

@Column(name = "trang_thai_tk", nullable = false)
private boolean trangThaiTK; // true = active, false = inactive
```

**Lý do thay đổi**:
- Database của bạn dùng cột `tenDangNhap` (không phải `tai_khoan`)
- Thêm field `trangThaiTK` để check account active/inactive
- Comment rõ ràng: mật khẩu lưu PLAIN TEXT (không hash)

---

### **2. AuthenticationServiceImpl - Xóa MD5 Hash**

**FILE**: `iuh.fit.core.service.AuthenticationServiceImpl`

#### **TRƯỚC (Dùng MD5 Hash)**:
```java
❌ // TODO: Hash password
newAccount.setMatKhau(taiKhoanDTO.getMatKhau()); 
```

#### **SAU (Plain Text)**:
```java
✅ // ✅ Lưu password dưới dạng PLAIN TEXT (không hash)
newAccount.setMatKhau(taiKhoanDTO.getMatKhau());
```

#### **Login Logic**:
```java
// ✅ So sánh PLAIN TEXT bằng .equals()
if (!foundAccount.getMatKhau().equals(matKhau)) {
    return null; // Mật khẩu sai
}

// ✅ Check trangThaiTK trước khi cho login
if (!foundAccount.isTrangThaiTK()) {
    throw new IllegalArgumentException("Tài khoản đã bị vô hiệu hóa");
}
```

**Thay đổi chính**:
- ✅ Xóa toàn bộ MD5 hash logic
- ✅ Dùng `.equals()` để so sánh trực tiếp
- ✅ Thêm check trạng thái account
- ✅ Không trả password trong DTO response (security best practice)

---

### **3. TaiKhoanRepositoryImpl - Sửa Query**

**FILE**: `iuh.fit.infrastructure.persistence.TaiKhoanRepositoryImpl`

#### **TRƯỚC**:
```java
❌ String hql = "SELECT t FROM TaiKhoan t WHERE t.taiKhoan = :taiKhoan";
query.setParameter("taiKhoan", taiKhoan);
```

#### **SAU**:
```java
✅ String hql = "SELECT t FROM TaiKhoan t WHERE t.tenDangNhap = :tenDangNhap";
query.setParameter("tenDangNhap", taiKhoan);
```

**Lý do**: Field entity tên là `tenDangNhap`, không phải `taiKhoan`

---

## 🎯 LUỒNG ĐĂNG NHẬP MỚI

```
┌─────────────────────────────────────────────────┐
│ 1. User nhập: username="admin", password="123"  │
│    (UI: LoginController)                        │
└──────────────┬────────────────────────────────┘
               ↓
┌─────────────────────────────────────────────────┐
│ 2. LoginController call:                        │
│    authService.login("admin", "123")            │
└──────────────┬────────────────────────────────┘
               ↓
┌─────────────────────────────────────────────────┐
│ 3. AuthenticationServiceImpl.login():            │
│    - Validate input                             │
│    - Call repository: findByTaiKhoan("admin")   │
└──────────────┬────────────────────────────────┘
               ↓
┌─────────────────────────────────────────────────┐
│ 4. TaiKhoanRepositoryImpl.findByTaiKhoan():      │
│    - HQL: SELECT t FROM TaiKhoan t              │
│           WHERE t.tenDangNhap = ?               │
│    - Return: TaiKhoan entity                    │
└──────────────┬────────────────────────────────┘
               ↓
┌─────────────────────────────────────────────────┐
│ 5. Service nhận entity:                         │
│    foundAccount.getMatKhau() = "123" (from DB)  │
│    user input password = "123"                  │
│    Compare: "123".equals("123") ✅ TRUE         │
└──────────────┬────────────────────────────────┘
               ↓
┌─────────────────────────────────────────────────┐
│ 6. Check status:                                │
│    foundAccount.isTrangThaiTK() = true ✅       │
│    Account is ACTIVE                           │
└──────────────┬────────────────────────────────┘
               ↓
┌─────────────────────────────────────────────────┐
│ 7. Convert Entity → DTO:                        │
│    Return TaiKhoanDTO (WITHOUT password!)       │
└──────────────┬────────────────────────────────┘
               ↓
┌─────────────────────────────────────────────────┐
│ 8. LoginController nhận DTO:                    │
│    ✅ Login thành công                          │
│    ✅ Chuyển sang Main Screen                   │
└─────────────────────────────────────────────────┘
```

---

## 💾 DATABASE - PLAIN TEXT PASSWORDS

Bảng `tai_khoan` hiện có dữ liệu:
```sql
SELECT * FROM tai_khoan;

┌──────────────────┬──────────┬───────────┬──────────────┐
│ ten_dang_nhap    │ mat_khau │ trangThaiTK   │ ma_nhan_vien │
├──────────────────┼──────────┼───────────┼──────────────┤
│ admin            │ 123      │ 1         │ NV001        │
│ letan01          │ 123      │ 1         │ NV002        │
│ letan02          │ 123      │ 0         │ NV003        │
└──────────────────┴──────────┴───────────┴──────────────┘
```

**Cách login**:
- Username: `admin`
- Password: `123`
- Result: ✅ Login thành công

---

## 🧪 TEST CASES

### **Test 1: Login thành công**
```
Input:
- taiKhoan: "admin"
- matKhau: "123"

Expected: ✅ TaiKhoanDTO returned (not null)
          ✅ Chuyển sang main screen
```

### **Test 2: Sai password**
```
Input:
- taiKhoan: "admin"
- matKhau: "wrong"

Expected: ❌ null returned
          ❌ Show error: "Mật khẩu sai"
```

### **Test 3: Account không tồn tại**
```
Input:
- taiKhoan: "notexist"
- matKhau: "123"

Expected: ❌ null returned
          ❌ Show error: "Tài khoản không tồn tại"
```

### **Test 4: Account bị vô hiệu hóa (trangThaiTK = false)**
```
Input:
- taiKhoan: "letan02" (trangThaiTK = 0)
- matKhau: "123"

Expected: ❌ Throw exception
          ❌ Show error: "Tài khoản đã bị vô hiệu hóa"
```

### **Test 5: Empty input**
```
Input:
- taiKhoan: "" (hoặc null)
- matKhau: "123"

Expected: ❌ Throw exception
          ❌ Show error: "Tài khoản không được để trống"
```

---

## 🔐 SECURITY NOTES

### **⚠️ Lưới ý quan trọng**

**Plain Text Password = KHÔNG AN TOÀN trong production!**

Nên:
```java
❌ KHÔNG:
password = "123" (plain text)

✅ NÊN:
password = BCrypt.hash("123") // In production
password = Argon2.hash("123") // Even better
```

### **Tại sao plain text ở đây?**
- ✅ Đây là đồ án học tập (dev/test environment)
- ✅ Database của bạn lưu plain text sẵn
- ✅ Simple logic cho việc học
- ✅ Production sẽ implement BCrypt/Argon2

### **Cách chuyển sang BCrypt (Nâng cấp)**
```java
// Add dependency: bcrypt
// <dependency>
//     <groupId>org.mindrot</groupId>
//     <artifactId>jbcrypt</artifactId>
//     <version>0.4</version>
// </dependency>

// Update service:
import org.mindrot.bcrypt.BCrypt;

public TaiKhoanDTO login(String tenDangNhap, String matKhau) {
    // ...
    TaiKhoan foundAccount = taiKhoanOptional.get();
    
    // ✅ Compare hashed password
    if (!BCrypt.checkpw(matKhau, foundAccount.getMatKhau())) {
        return null;
    }
    // ...
}

public TaiKhoanDTO register(TaiKhoanDTO dto) {
    // ...
    // ✅ Hash password before saving
    String hashedPassword = BCrypt.hashpw(dto.getMatKhau(), BCrypt.gensalt());
    newAccount.setMatKhau(hashedPassword);
    // ...
}
```

---

## 📁 FILES UPDATED

| File | Changes |
|------|---------|
| `core.entity.TaiKhoan` | ✅ tenDangNhap, trangThaiTK columns |
| `core.service.AuthenticationServiceImpl` | ✅ Plain text comparison, removed MD5 |
| `infrastructure.persistence.TaiKhoanRepositoryImpl` | ✅ Fixed HQL query to use tenDangNhap |
| `LoginController` | ✅ No changes needed (already correct) |

---

## ✅ TESTING CHECKLIST

- [ ] Build project: `mvn clean install`
- [ ] Run app: `mvn javafx:run`
- [ ] Try login: `admin` / `123` ✅ Should work
- [ ] Try wrong password ❌ Should fail
- [ ] Try non-existent user ❌ Should fail
- [ ] Try disabled account (letan02) ❌ Should fail
- [ ] Check console for error messages ✅ Should show

---

## 🎉 READY TO GO!

Your authentication is now updated to use **PLAIN TEXT passwords** with simple `.equals()` comparison!

**Next steps**:
1. Test login functionality
2. When moving to production, upgrade to BCrypt/Argon2
3. Add other authentication features (forgot password, 2FA, etc.)

---

**Status**: ✅ **PLAIN TEXT AUTHENTICATION IMPLEMENTED**


