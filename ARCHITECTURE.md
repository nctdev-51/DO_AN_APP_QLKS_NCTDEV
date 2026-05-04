# 🏨 Quản Lý Khách Sạn - Clean Architecture + N-Tier

Dự án này được tái cấu trúc thành một **Single Maven Project** theo mô hình **Clean Architecture** kết hợp với **N-Tier Architecture**.

## 📋 Cấu trúc Project

```
src/main/java/iuh/fit/
├── app/                          # Application Bootstrap Layer
│   └── MainApp.java              # Entry point, setup Dependency Injection
│
├── core/                         # CORE LAYER (Domain Layer - không phụ thuộc bất kỳ framework)
│   ├── entity/                   # Domain Models (JPA Entities)
│   │   ├── TaiKhoan.java
│   │   ├── NhanVien.java
│   │   ├── KhachHang.java
│   │   ├── Phong.java
│   │   ├── PhieuDatPhong.java
│   │   ├── HoaDon.java
│   │   ├── DichVu.java
│   │   ├── KhuyenMai.java
│   │   ├── CaLamViec.java
│   │   ├── PhanCongCaLamViec.java
│   │   └── [Enums: LoaiNhanVien, LoaiKhachHang, TinhTrangPhong]
│   │
│   ├── dto/                      # Data Transfer Objects (không expose Entity)
│   │   ├── TaiKhoanDTO.java
│   │   ├── NhanVienDTO.java
│   │   └── KhachHangDTO.java
│   │
│   ├── repository/               # Repository Interfaces (Ports)
│   │   ├── ITaiKhoanRepository.java
│   │   ├── INhanVienRepository.java
│   │   └── IKhachHangRepository.java
│   │
│   └── service/                  # Business Logic Services
│       ├── IAuthenticationService.java
│       ├── AuthenticationServiceImpl.java
│       ├── IKhachHangService.java
│       └── KhachHangServiceImpl.java
│
├── infrastructure/               # INFRASTRUCTURE LAYER (Kỹ thuật & CSDL)
│   ├── db/
│   │   └── JpaConfig.java        # EntityManagerFactory & JPA Configuration
│   │
│   ├── persistence/              # Repository Implementations (Adapters)
│   │   ├── TaiKhoanRepositoryImpl.java
│   │   └── KhachHangRepositoryImpl.java
│   │
│   └── mapper/                   # Entity ↔ DTO Mappers
│       ├── TaiKhoanMapper.java
│       └── KhachHangMapper.java
│
└── presentation/                 # PRESENTATION LAYER (JavaFX UI)
    └── controller/               # JavaFX Controllers
        ├── LoginController.java
        └── QuanLyKhachHangController.java

resources/
└── META-INF/
    └── persistence.xml           # JPA Configuration
```

## 🏗️ Mô hình Clean Architecture + N-Tier

### Các Tầng (Layers):

#### 1️⃣ **CORE Layer** (Tầng Lõi - Domain)
- **Trách nhiệm**: Định nghĩa business logic, entities, DTOs, interfaces
- **Độc lập**: KHÔNG phụ thuộc vào framework bên ngoài (chỉ Java cơ bản)
- **Package**:
  - `core.entity`: JPA Entities (database models)
  - `core.dto`: Data Transfer Objects (API models)
  - `core.repository`: Repository interfaces (database contracts)
  - `core.service`: Business logic interfaces & implementations

#### 2️⃣ **INFRASTRUCTURE Layer** (Tầng Hạ Tầng - Kỹ Thuật)
- **Trách nhiệm**: Implement repositories, kết nối database, mapper
- **Phụ thuộc vào**: CORE layer, JPA/Hibernate, MariaDB driver
- **Package**:
  - `infrastructure.db`: JPA configuration (JpaConfig.java)
  - `infrastructure.persistence`: Repository implementations (adapters)
  - `infrastructure.mapper`: Entity ↔ DTO converters

#### 3️⃣ **PRESENTATION Layer** (Tầng Giao Diện)
- **Trách nhiệm**: Hiển thị UI, nhận input user, gọi services
- **Phụ thuộc vào**: CORE layer services (interfaces), JavaFX
- **Package**:
  - `presentation.controller`: JavaFX controllers

#### 4️⃣ **APP Layer** (Tầng Khởi Động)
- **Trách nhiệ**: Bootstrap ứng dụng, setup Dependency Injection
- **Package**: `app.MainApp`

### Luồng Dữ Liệu (Data Flow):

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│                    (JavaFX Controller)                      │
│                                                             │
│  User Input → LoginController.handleLogin()               │
└────────────────────────┬────────────────────────────────────┘
                         │ gọi IAuthenticationService
                         ↓
┌─────────────────────────────────────────────────────────────┐
│                      CORE LAYER                             │
│                   (Service/Business Logic)                  │
│                                                             │
│  AuthenticationServiceImpl.login()                          │
│    ├─ Validate input                                       │
│    ├─ Call ITaiKhoanRepository.findByTaiKhoan()           │
│    ├─ Check password                                       │
│    └─ Return TaiKhoanDTO                                   │
└────────────────────────┬────────────────────────────────────┘
                         │ gọi ITaiKhoanRepository
                         ↓
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                        │
│            (Repository Implementation/Adapter)             │
│                                                             │
│  TaiKhoanRepositoryImpl.findByTaiKhoan()                    │
│    ├─ Get EntityManager                                    │
│    ├─ Execute HQL Query                                    │
│    ├─ Map Entity → DTO (using Mapper)                     │
│    └─ Return Result                                        │
└────────────────────────┬────────────────────────────────────┘
                         │ gọi JPA/Hibernate
                         ↓
┌─────────────────────────────────────────────────────────────┐
│              DATABASE (MariaDB)                             │
│                                                             │
│  SELECT * FROM tai_khoan WHERE tai_khoan = 'admin'        │
└─────────────────────────────────────────────────────────────┘
```

## 🔄 Dependency Injection Pattern

Ứng dụng không sử dụng Spring Framework, thay vào đó sử dụng **Constructor-based Dependency Injection**:

```java
// Service layer
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final ITaiKhoanRepository repository;
    
    // Inject repository qua constructor
    public AuthenticationServiceImpl(ITaiKhoanRepository repository) {
        this.repository = repository;
    }
}

// Presentation layer
public class LoginController {
    private final IAuthenticationService authService;
    
    // Inject service qua constructor
    public LoginController(IAuthenticationService authService) {
        this.authService = authService;
    }
}

// App layer (Bootstrap)
public class MainApp {
    private void setupDependencyInjection() {
        // Tạo repository instance
        var repository = new TaiKhoanRepositoryImpl();
        
        // Tạo service instance với repository
        var service = new AuthenticationServiceImpl(repository);
        
        // Tạo controller instance với service
        var controller = new LoginController(service);
    }
}
```

## 🚀 Chạy Ứng Dụng

### Prerequisites:
- Java 21+
- Maven 3.8+
- MariaDB 10.5+

### Bước 1: Clone hoặc extract project
```bash
cd BTL_PT_QLKS
```

### Bước 2: Cấu hình Database
Chỉnh sửa `src/main/resources/META-INF/persistence.xml`:
```xml
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mariadb://localhost:3306/qlkhachsan_db"/>
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="your_password"/>
```

### Bước 3: Build & Run
```bash
# Build
mvn clean install

# Run
mvn javafx:run

# Hoặc chạy directly
java -jar target/BTL_PT_QLKS-1.0-SNAPSHOT.jar
```

## 📚 Ví Dụ: Luồng Quản Lý Khách Hàng

### 1. Entity (CORE - Domain Model)
```java
@Entity
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private LocalDate ngaySinh;
    @Enumerated(EnumType.STRING)
    private LoaiKhachHang loaiKhachHang;
}
```

### 2. DTO (CORE - Transfer Object)
```java
@Data
public class KhachHangDTO {
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String loaiKhachHang;
}
```

### 3. Repository Interface (CORE - Port)
```java
public interface IKhachHangRepository {
    Optional<KhachHang> findById(String maKhachHang);
    List<KhachHang> findAll();
    KhachHang save(KhachHang khachHang);
    KhachHang update(KhachHang khachHang);
    void deleteById(String maKhachHang);
}
```

### 4. Service (CORE - Business Logic)
```java
public class KhachHangServiceImpl implements IKhachHangService {
    private final IKhachHangRepository repository;
    
    public KhachHangServiceImpl(IKhachHangRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public KhachHangDTO addKhachHang(KhachHangDTO dto) {
        // Validate
        validateKhachHang(dto);
        
        // Check duplicate phone
        if (repository.findBySoDienThoai(dto.getSoDienThoai()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
        
        // Convert DTO → Entity
        KhachHang entity = convertToEntity(dto);
        
        // Save to repository
        KhachHang saved = repository.save(entity);
        
        // Convert Entity → DTO
        return convertToDTO(saved);
    }
}
```

### 5. Repository Implementation (INFRASTRUCTURE - Adapter)
```java
public class KhachHangRepositoryImpl implements IKhachHangRepository {
    
    @Override
    public KhachHang save(KhachHang khachHang) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(khachHang);
            tx.commit();
            return khachHang;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<KhachHang> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createQuery("SELECT k FROM KhachHang k", KhachHang.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
```

### 6. JavaFX Controller (PRESENTATION - UI Layer)
```java
public class QuanLyKhachHangController {
    private final IKhachHangService service;
    private TableView<KhachHangDTO> tableView;
    
    public QuanLyKhachHangController(IKhachHangService service) {
        this.service = service;
    }
    
    private void handleThemKhachHang() {
        try {
            KhachHangDTO dto = new KhachHangDTO();
            dto.setHoTen(tenTextField.getText());
            dto.setSoDienThoai(sdtTextField.getText());
            
            KhachHangDTO created = service.addKhachHang(dto);
            
            loadKhachHangData();
            showSuccess("Thêm khách hàng thành công");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }
    
    private void loadKhachHangData() {
        List<KhachHangDTO> list = service.getAllKhachHang();
        tableView.setItems(FXCollections.observableArrayList(list));
    }
}
```

## ✅ Các Nguyên Tắc Clean Architecture

1. **Dependency Rule**: Tầng cao không phụ thuộc vào tầng thấp
   - ✅ CORE không biết INFRASTRUCTURE
   - ✅ PRESENTATION chỉ gọi SERVICE interfaces
   - ❌ INFRASTRUCTURE không gọi ngược lên CORE

2. **Separation of Concerns**: Mỗi tầng có một trách nhiệm
   - CORE: Business logic
   - INFRASTRUCTURE: Database, external systems
   - PRESENTATION: UI, user interaction
   - APP: Bootstrap, DI setup

3. **Testability**: Dễ viết unit test
   - Services dùng interfaces → Có thể mock
   - Không phụ thuộc vào framework → Test offline
   - DTO tách biệt Entity → Flexible testing

4. **Scalability**: Dễ mở rộng
   - Thêm feature mới: thêm Entity + DTO + Repository + Service + Controller
   - Thay đổi database: chỉ sửa repository implementation
   - Thay đổi UI: chỉ sửa controller, service không đổi

## 📦 Dependencies

```xml
<!-- JPA/Hibernate -->
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.4.4.Final</version>
</dependency>

<!-- Database Driver -->
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>3.3.3</version>
</dependency>

<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.3</version>
</dependency>

<!-- Utility -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.32</version>
</dependency>
```

## 🎯 Next Steps

1. **Tiếp tục phát triển features**:
   - Tạo thêm Service classes cho Phòng, Hóa Đơn, v.v.
   - Tạo thêm Repository implementations
   - Tạo thêm JavaFX Controllers

2. **Cải thiện bảo mật**:
   - Hash password (BCrypt, Argon2)
   - Thêm JWT token authentication
   - Validate input ở presentation layer

3. **Logging & Monitoring**:
   - Thêm SLF4J + Logback
   - Log tất cả operations
   - Monitor performance

4. **Unit Testing**:
   - Test Service layer với Mockito
   - Test Repository layer với embedded database
   - Test Controller logic

5. **Deployment**:
   - Tạo JAR executable
   - Deploy lên server
   - Cấu hình production database

---

**Tác giả**: Clean Architecture Expert  
**Ngày tạo**: May 4, 2026  
**Version**: 1.0

