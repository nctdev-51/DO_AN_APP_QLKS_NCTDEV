# 📐 Clean Architecture Pattern - Chi Tiết Thiết Kế

## 🎯 Mục Đích của Clean Architecture

Clean Architecture nhằm:
1. ✅ **Độc lập với Framework**: Logic nghiệp vụ không phụ thuộc JPA, Spring, JavaFX
2. ✅ **Testable**: Dễ viết unit test cho mỗi layer
3. ✅ **Maintainable**: Code rõ ràng, dễ bảo trì
4. ✅ **Scalable**: Dễ thêm feature mới mà không ảnh hưởng layer khác
5. ✅ **Flexible**: Có thể thay đổi database, UI mà không sửa business logic

---

## 🏗️ Quy Tắc Phụ Thuộc (Dependency Rule)

### ⬅️ Dependency Direction (Hướng Phụ Thuộc)

```
┌─────────────────────────────┐
│   PRESENTATION LAYER        │ ← Có thể thay đổi (JavaFX, Swing, Web)
│  (Controllers/Views)        │
└──────────────┬──────────────┘
               ↓ phụ thuộc vào
┌─────────────────────────────┐
│      CORE LAYER             │ ← Lõi cứng (Business Logic)
│  (Services, Entities, DTOs) │   Không phụ thuộc layer khác
└──────────────┬──────────────┘
               ↓ phụ thuộc vào
┌─────────────────────────────┐
│   INFRASTRUCTURE LAYER      │ ← Implement chi tiết (DB, API, etc.)
│  (Repositories, Mappers)    │
└─────────────────────────────┘
               ↓
┌─────────────────────────────┐
│    EXTERNAL SYSTEMS         │ ← Database, APIs, Libraries
│  (JPA, Hibernate, MariaDB)  │
└─────────────────────────────┘
```

### ❌ KHÔNG ĐƯỢC (Violates Dependency Rule)
```
PRESENTATION → INFRASTRUCTURE (❌)
PRESENTATION → DATABASE directly (❌)
CORE → PRESENTATION (❌)
CORE → INFRASTRUCTURE (❌)
```

### ✅ ĐƯỢC (Follows Dependency Rule)
```
PRESENTATION → CORE (✅)
CORE → INTERFACES only (✅)
INFRASTRUCTURE → CORE (✅)
INFRASTRUCTURE → EXTERNAL SYSTEMS (✅)
```

---

## 📦 Chi Tiết từng Layer

### 1️⃣ CORE Layer (Tầng Lõi)

**Trách nhiệm**:
- Định nghĩa business rules
- Entity: Database models
- DTO: Transfer objects
- Repository Interfaces: Data access contracts
- Service: Business logic implementations

**Đặc điểm**:
- ❌ KHÔNG import JPA, Hibernate, Spring
- ❌ KHÔNG import JavaFX, Swing
- ❌ KHÔNG có @Repository, @Service annotations
- ✅ Chỉ import Java standard library + lombok (tùy chọn)

**Ví dụ**:
```java
// ✅ OK - Chỉ Java cơ bản
import java.time.LocalDate;
import lombok.Data;

// ❌ KHÔNG - Database framework
// import jakarta.persistence.*;
// import org.hibernate.*;

@Data
public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private LocalDate ngaySinh;
}
```

### 2️⃣ INFRASTRUCTURE Layer (Tầng Hạ Tầng)

**Trách nhiệm**:
- Implement Repository interfaces từ CORE
- Cấu hình JPA/Hibernate
- Mapper: Entity ↔ DTO conversion
- External API integration

**Đặc điểm**:
- ✅ CÓ thể import JPA, Hibernate
- ✅ Implement interfaces từ CORE
- ✅ Chi tiết về database operations
- ❌ KHÔNG implement presentation layer

**Ví dụ**:
```java
// ✅ OK - Implement repository interface
public class KhachHangRepositoryImpl implements IKhachHangRepository {
    
    // ✅ OK - JPA imports ở đây
    @Override
    public KhachHang save(KhachHang khachHang) {
        EntityManager em = JpaConfig.getEntityManager();
        // JPA/Hibernate operations
    }
}
```

### 3️⃣ PRESENTATION Layer (Tầng Giao Diện)

**Trách nhiệm**:
- JavaFX Controllers
- UI logic (event handlers, validations)
- Bind data to UI

**Đặc điểm**:
- ✅ CÓ JavaFX imports
- ✅ Call Services từ CORE (thông qua interfaces)
- ❌ KHÔNG directly access repositories
- ❌ KHÔNG directly manipulate entities

**Ví dụ**:
```java
// ✅ OK - Call service interface
public class LoginController {
    private final IAuthenticationService authService;
    
    public LoginController(IAuthenticationService authService) {
        this.authService = authService; // Dependency injection
    }
    
    private void handleLogin() {
        TaiKhoanDTO user = authService.login(username, password);
        // Handle UI with DTO
    }
}

// ❌ KHÔNG - Direct repository access
// private final ITaiKhoanRepository repository;
```

---

## 🔄 Data Flow Example: Đăng Nhập

### Scenario: User nhấn "Đăng Nhập"

```
┌─────────────────────────────────────────────────┐
│ PRESENTATION: LoginController                   │
│                                                 │
│ @handleLogin()                                  │
│   username = usernameField.getText()            │
│   password = passwordField.getText()            │
│   user = authService.login(username, password)  │ ◄─── Call SERVICE
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│ CORE: AuthenticationServiceImpl                  │
│                                                 │
│ @login(username, password)                      │
│   ① Validate input                              │
│   ② account = repository.findByTaiKhoan(u)     │ ◄─── Call REPO INTERFACE
│   ③ Check password match                        │
│   ④ Return TaiKhoanDTO                          │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│ INFRASTRUCTURE: TaiKhoanRepositoryImpl           │
│                                                 │
│ @findByTaiKhoan(username)                       │
│   ① Get EntityManager                           │
│   ② Execute HQL: SELECT FROM TaiKhoan          │
│   ③ Map Entity → DTO                            │
│   ④ Return result                               │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│ EXTERNAL: JPA/Hibernate/MariaDB                 │
│                                                 │
│ Database Query:                                 │
│ SELECT * FROM tai_khoan WHERE tai_khoan = 'u1' │
└─────────────────────────────────────────────────┘
```

### Return Value Flow:

```
Database → Entity → DTO → Controller → UI
```

---

## 🧵 Dependency Injection Pattern

### Tại sao Cần DI?

```java
// ❌ Tightly Coupled (Khó test)
public class AuthenticationServiceImpl {
    private ITaiKhoanRepository repo = new TaiKhoanRepositoryImpl();
    // Khó mock, phụ thuộc cụ thể implementation
}

// ✅ Loosely Coupled (Dễ test)
public class AuthenticationServiceImpl {
    private final ITaiKhoanRepository repo;
    
    public AuthenticationServiceImpl(ITaiKhoanRepository repo) {
        this.repo = repo;
    }
    // Có thể inject mock, phụ thuộc interface
}
```

### Cách DI trong Ứng Dụng:

```java
// APP Layer - Bootstrap
public class MainApp {
    private void setupDependencyInjection() {
        // 1. Tạo Repository Implementation
        ITaiKhoanRepository repository = new TaiKhoanRepositoryImpl();
        
        // 2. Tạo Service, inject Repository
        IAuthenticationService service = new AuthenticationServiceImpl(repository);
        
        // 3. Tạo Controller, inject Service
        LoginController controller = new LoginController(service);
        
        // Giờ controller có thể dùng service
        // service có thể dùng repository
    }
}
```

### Benefits:

1. **Testability**: Có thể mock repository trong unit test
2. **Flexibility**: Có thể thay đổi implementation mà không sửa code
3. **Loose Coupling**: Mỗi class độc lập, dễ maintain

---

## 🧪 Unit Test Example

```java
public class AuthenticationServiceTest {
    
    // Mock repository
    private ITaiKhoanRepository mockRepository;
    private IAuthenticationService authService;
    
    @Before
    public void setUp() {
        // Create mock
        mockRepository = mock(ITaiKhoanRepository.class);
        
        // Inject mock
        authService = new AuthenticationServiceImpl(mockRepository);
    }
    
    @Test
    public void testLoginSuccessful() {
        // Arrange
        TaiKhoan expected = new TaiKhoan("admin", "pass123", null);
        when(mockRepository.findByTaiKhoan("admin"))
            .thenReturn(Optional.of(expected));
        
        // Act
        TaiKhoanDTO result = authService.login("admin", "pass123");
        
        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getTaiKhoan());
    }
    
    @Test
    public void testLoginFailed() {
        // Arrange
        when(mockRepository.findByTaiKhoan("invalid"))
            .thenReturn(Optional.empty());
        
        // Act
        TaiKhoanDTO result = authService.login("invalid", "pass");
        
        // Assert
        assertNull(result);
    }
}
```

---

## 📐 Entity vs DTO

### Entity (CORE Layer)
```java
@Entity // JPA annotation - Maps to DB table
@Table(name = "tai_khoan")
public class TaiKhoan {
    @Id
    private String taiKhoan;
    
    @Column(name = "mat_khau")
    private String matKhau;
    
    @ManyToOne
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;
}
```

### DTO (CORE Layer)
```java
public class TaiKhoanDTO {
    private String taiKhoan;
    private String matKhau;
    private String maNhanVien; // ID only, not object
    private String hoTenNhanVien; // For display
}
```

### Lý Do Tách Biệt:

| Aspect | Entity | DTO |
|--------|--------|-----|
| **Mục đích** | Map to DB table | Transfer data |
| **Relationships** | Full object graph | ID + minimal info |
| **Annotations** | @Entity, @Column | None |
| **Expose ra UI** | ❌ No | ✅ Yes |
| **Lazy Loading** | Có thể xảy ra | Không có |

---

## 🔐 Best Practices

### 1. Validation

```java
// CORE Layer - Service
public class KhachHangServiceImpl implements IKhachHangService {
    
    @Override
    public KhachHangDTO addKhachHang(KhachHangDTO dto) {
        // Validate input
        if (dto.getHoTen() == null || dto.getHoTen().isEmpty()) {
            throw new IllegalArgumentException("Tên không được trống");
        }
        
        if (!dto.getSoDienThoai().matches("\\d{10}")) {
            throw new IllegalArgumentException("SĐT phải 10 chữ số");
        }
        
        // Validate business rules
        if (repository.findBySoDienThoai(dto.getSoDienThoai()).isPresent()) {
            throw new IllegalArgumentException("SĐT đã tồn tại");
        }
        
        // Save...
    }
}
```

### 2. Error Handling

```java
// PRESENTATION Layer - Controller
private void handleLogin() {
    try {
        TaiKhoanDTO user = authService.login(username, password);
        if (user != null) {
            showMainScreen();
        } else {
            showError("Tài khoản hoặc mật khẩu sai");
        }
    } catch (IllegalArgumentException e) {
        showError("Lỗi validation: " + e.getMessage());
    } catch (Exception e) {
        showError("Lỗi hệ thống: " + e.getMessage());
        logger.severe(e);
    }
}
```

### 3. Transaction Management

```java
// INFRASTRUCTURE Layer - Repository
@Override
public KhachHang save(KhachHang khachHang) {
    EntityManager em = JpaConfig.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();          // ① Bắt đầu transaction
        em.persist(khachHang); // ② Thực hiện operation
        tx.commit();         // ③ Commit (save changes)
        return khachHang;
    } catch (Exception e) {
        if (tx.isActive()) {
            tx.rollback();   // ④ Rollback nếu có lỗi
        }
        throw new RuntimeException(e);
    } finally {
        em.close();          // ⑤ Đóng EntityManager
    }
}
```

---

## 📈 Quy Trình Thêm Feature Mới

### Example: Thêm Quản Lý Phòng

#### Step 1: Entity (CORE)
```java
@Entity
@Table(name = "phong")
public class Phong {
    @Id
    private String maPhong;
    private String tenPhong;
    private double giaPhong;
    @Enumerated
    private TinhTrangPhong tinhTrang;
}
```

#### Step 2: DTO (CORE)
```java
public class PhongDTO {
    private String maPhong;
    private String tenPhong;
    private double giaPhong;
    private String tinhTrang;
}
```

#### Step 3: Repository Interface (CORE)
```java
public interface IPhongRepository {
    Optional<Phong> findById(String maPhong);
    List<Phong> findByTinhTrang(TinhTrangPhong tinhTrang);
    Phong save(Phong phong);
}
```

#### Step 4: Service (CORE)
```java
public interface IPhongService {
    List<PhongDTO> getAllPhong();
    List<PhongDTO> getPhongTrong();
    PhongDTO addPhong(PhongDTO dto);
}

public class PhongServiceImpl implements IPhongService {
    // Implement...
}
```

#### Step 5: Repository Impl (INFRASTRUCTURE)
```java
public class PhongRepositoryImpl implements IPhongRepository {
    // Implement with JPA...
}
```

#### Step 6: Mapper (INFRASTRUCTURE)
```java
public class PhongMapper {
    public static PhongDTO entityToDTO(Phong entity) { }
    public static Phong dtoToEntity(PhongDTO dto) { }
}
```

#### Step 7: Controller (PRESENTATION)
```java
public class QuanLyPhongController {
    private final IPhongService phongService;
    
    public QuanLyPhongController(IPhongService phongService) {
        this.phongService = phongService;
    }
    
    public Scene createQuanLyPhongScene() {
        // Create UI...
        loadPhongData();
    }
    
    private void loadPhongData() {
        List<PhongDTO> list = phongService.getAllPhong();
        tableView.setItems(FXCollections.observableArrayList(list));
    }
}
```

#### Step 8: Bootstrap (APP)
```java
public class MainApp {
    private void showPhongScreen() {
        var repository = new PhongRepositoryImpl();
        var service = new PhongServiceImpl(repository);
        var controller = new QuanLyPhongController(service);
        
        Scene scene = controller.createQuanLyPhongScene();
        primaryStage.setScene(scene);
    }
}
```

---

## ✨ Kết Luận

Clean Architecture + N-Tier:
- ✅ Tách biệt concerns (mỗi layer có trách nhiệm cụ thể)
- ✅ Dễ test (có thể mock dependencies)
- ✅ Dễ maintain (code rõ ràng, có structure)
- ✅ Dễ scale (thêm feature mà không ảnh hưởng code cũ)
- ✅ Flexible (có thể thay đổi implementation)

**Quy tắc vàng**: Dependency luôn từ ngoài → vào (outer → inner), KHÔNG bao giờ ngược lại!


