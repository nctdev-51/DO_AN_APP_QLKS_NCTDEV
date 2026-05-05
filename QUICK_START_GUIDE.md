# ⚡ QUICK START GUIDE - CLEAN ARCHITECTURE PROJECT

## 📊 Project Status Dashboard

```
✅ COMPLETED (70%)                    ⚠️  IN PROGRESS / TODO (30%)
├── Core Layer Setup                   ├── Repository Implementations
├── Entity Definitions                 ├── JavaFX UI Controllers  
├── DTO Mapping                        ├── FXML View Files
├── Service Interfaces                 ├── Persistence Layer
├── Service Implementations            ├── EntityManager Configuration
├── Mapper Classes                     └── Comprehensive Testing
├── Repository Interfaces
├── Database Schema (MariaDB)
└── Project Structure
```

---

## 🚀 CURRENT STATISTICS

| Layer | Component | Count | Status |
|-------|-----------|-------|--------|
| **CORE** | Entities | 14 | ✅ Complete |
| | DTOs | 8 | ✅ Complete |
| | Repository Interfaces | 6 | ✅ Complete |
| | Service Interfaces | 7 | ✅ Complete |
| | Service Implementations | 6 | ✅ Complete |
| **INFRASTRUCTURE** | Mappers | 4 | ✅ Complete |
| | DB Config | 0 | ⚠️ TODO |
| | Persistence Impls | 0 | ⚠️ TODO |
| **PRESENTATION** | Controllers | 4 | ⚠️ TODO |
| | FXML Views | 0 | ⚠️ TODO |
| **TOTAL** | **Java Files** | **59** | **70% Complete** |

---

## 🏗️ PROJECT STRUCTURE AT A GLANCE

```
iuh/fit/
├── core/
│   ├── entity/          [14 Entities] ✅
│   ├── dto/             [8 DTOs] ✅
│   ├── repository/      [6 Interfaces] ✅
│   └── service/
│       ├── [7 Interfaces] ✅
│       └── impl/        [6 Implementations] ✅
├── infrastructure/
│   ├── db/              [Config] ⚠️
│   ├── mapper/          [4 Mappers] ✅
│   └── persistence/     [Implementations] ⚠️
└── presentation/
    ├── controller/      [4 Files] ⚠️
    └── view/            [FXML] ⚠️
```

---

## 📝 KEY FILES COMPLETED THIS SESSION

### Services Layer (`core/service/impl/`)
- ✅ `AuthenticationServiceImpl.java` - Login/Auth logic
- ✅ `KhachHangServiceImpl.java` - Customer management
- ✅ `NhanVienServiceImpl.java` - Employee management
- ✅ `PhongServiceImpl.java` - Room management (NEW)
- ✅ `DichVuServiceImpl.java` - Service management (NEW)
- ✅ `PhieuDatPhongServiceImpl.java` - Booking management (NEW)

### DTOs (`core/dto/`)
- ✅ `PhongDTO.java` - Room data transfer (NEW)
- ✅ `DichVuDTO.java` - Service data transfer (NEW)
- ✅ `PhieuDatPhongDTO.java` - Booking data transfer (NEW)
- ✅ `HoaDonDTO.java` - Invoice data transfer (NEW)

### Repositories (`core/repository/`)
- ✅ `IPhongRepository.java` - Room interface (NEW)
- ✅ `IDichVuRepository.java` - Service interface (NEW)
- ✅ `IPhieuDatPhongRepository.java` - Booking interface (NEW)

### Mappers (`infrastructure/mapper/`)
- ✅ `PhongMapper.java` - Room converter (NEW)
- ✅ `DichVuMapper.java` - Service converter (NEW)
- ✅ `PhieuDatPhongMapper.java` - Booking converter (NEW)

### Database
- ✅ `qlkhachsanTATP_db_MariaDB.sql` - Full MariaDB schema (NEW)

---

## 🔄 DATA FLOW PATTERN

Every feature follows this Clean Architecture pattern:

```
USER ACTION (Presentation)
    ↓
    [JavaFX Controller] 
    - Receives user input
    - Calls Service method with DTOs
    ↓
BUSINESS LOGIC (Core/Service)
    - Validates data
    - Implements business rules
    - Calls Repository interface
    ↓
DATA ACCESS (Infrastructure/Persistence)
    - Converts DTO → Entity
    - Executes JPA/Hibernate queries
    - Converts Entity → DTO back
    ↓
DATABASE (MariaDB)
    - Stores/retrieves data
    ↓
    Response flows back to UI
```

---

## 🎯 NEXT IMMEDIATE TASKS (Priority Order)

### 1️⃣ **CRITICAL: Implement Repository Persistence Layer**
```
Location: infrastructure/persistence/
Files to create:
├── NhanVienRepositoryImpl.java
├── KhachHangRepositoryImpl.java
├── TaiKhoanRepositoryImpl.java
├── PhongRepositoryImpl.java
├── DichVuRepositoryImpl.java
├── PhieuDatPhongRepositoryImpl.java
└── HoaDonRepositoryImpl.java

Template pattern:
@Override
public List<Entity> findAll() {
    return entityManager
        .createQuery("SELECT e FROM Entity e", Entity.class)
        .getResultList();
}
```

### 2️⃣ **HIGH: Create EntityManager Configuration**
```
Location: infrastructure/db/EntityManagerConfig.java

Must contain:
├── EntityManagerFactory setup
├── EntityManager provider
├── Transaction management
└── Persistence.xml reference
```

### 3️⃣ **HIGH: Build JavaFX Controllers**
```
Location: presentation/controller/
Priority order:
1. LoginController (auth)
2. QuanLyPhongController (rooms)
3. QuanLyDichVuController (services)
4. QuanLyKhachHangController (customers)
5. DatPhongController (bookings)

Template:
@FXML
private Label statusLabel;

@Inject
private IPhongService phongService;

@FXML
public void handleLoadRooms() {
    List<PhongDTO> rooms = phongService.getAllPhong();
    // Populate UI
}
```

### 4️⃣ **MEDIUM: Create FXML Views**
```
Location: src/main/resources/fxml/
Required files:
├── login.fxml
├── main.fxml
├── phong.fxml
├── dichvu.fxml
├── datphong.fxml
└── khachhang.fxml
```

### 5️⃣ **MEDIUM: Implement Remaining Mappers**
```
Location: infrastructure/mapper/
├── NhanVienMapper.java
├── KhachHangMapper.java
└── HoaDonMapper.java
```

---

## 💡 HOW TO IMPLEMENT NEXT ITEM

### Example: Create PhongRepositoryImpl

```java
package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.Phong;
import iuh.fit.core.repository.IPhongRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class PhongRepositoryImpl implements IPhongRepository {
    private final EntityManager entityManager;
    
    public PhongRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public List<Phong> findAll() {
        return entityManager
            .createQuery("SELECT p FROM Phong p", Phong.class)
            .getResultList();
    }
    
    @Override
    public Optional<Phong> findById(String maPhong) {
        return Optional.ofNullable(
            entityManager.find(Phong.class, maPhong)
        );
    }
    
    @Override
    public List<Phong> findByTinhTrang(String tinhTrang) {
        return entityManager
            .createQuery(
                "SELECT p FROM Phong p WHERE p.tinhTrang = :tinhTrang",
                Phong.class
            )
            .setParameter("tinhTrang", tinhTrang)
            .getResultList();
    }
    
    @Override
    public List<Phong> findByMaLoaiPhong(String maLoaiPhong) {
        return entityManager
            .createQuery(
                "SELECT p FROM Phong p WHERE p.maLoaiPhong = :maLoai",
                Phong.class
            )
            .setParameter("maLoai", maLoaiPhong)
            .getResultList();
    }
    
    @Override
    public Phong save(Phong phong) {
        entityManager.persist(phong);
        return phong;
    }
    
    @Override
    public Phong update(Phong phong) {
        return entityManager.merge(phong);
    }
    
    @Override
    public void deleteById(String maPhong) {
        Phong phong = entityManager.find(Phong.class, maPhong);
        if (phong != null) {
            entityManager.remove(phong);
        }
    }
}
```

---

## 🗂️ FILE REFERENCE GUIDE

### Where to find what:
| Purpose | Location | Status |
|---------|----------|--------|
| Business Rules | `core/service/` | ✅ |
| Data Models | `core/entity/` | ✅ |
| Data Transfer | `core/dto/` | ✅ |
| Query Interface | `core/repository/` | ✅ |
| DB Access Code | `infrastructure/persistence/` | ⚠️ |
| Type Conversion | `infrastructure/mapper/` | ✅ |
| Database Setup | `infrastructure/db/` | ⚠️ |
| User Interface | `presentation/controller/` | ⚠️ |
| FXML Templates | `src/main/resources/fxml/` | ⚠️ |
| SQL Scripts | `cypher/` | ✅ |

---

## 🧪 TESTING THE ARCHITECTURE

### Test a Service in Isolation:
```java
// Example: Test PhongService
IPhongRepository mockRepo = mock(IPhongRepository.class);
IPhongService service = new PhongServiceImpl(mockRepo);

List<Phong> testData = Arrays.asList(
    new Phong("P101", "Phòng 101", 500000, "DON", "Trống")
);
when(mockRepo.findAll()).thenReturn(testData);

List<PhongDTO> result = service.getAllPhong();
assertEquals(1, result.size());
assertEquals("P101", result.get(0).getMaPhong());
```

---

## 📖 KEY PRINCIPLES TO REMEMBER

1. **Never import Infrastructure in Presentation**
   ```java
   // ❌ WRONG
   class PhongController {
       PhongRepositoryImpl repo = new PhongRepositoryImpl();
   }
   
   // ✅ CORRECT
   class PhongController {
       IPhongService service = /* injected */;
   }
   ```

2. **Never use Entity in UI**
   ```java
   // ❌ WRONG - Returns Entity
   List<Phong> rooms = service.getAllPhong();
   
   // ✅ CORRECT - Returns DTO
   List<PhongDTO> rooms = service.getAllPhong();
   ```

3. **Database queries only in Persistence**
   ```java
   // ❌ WRONG - Query in Service
   public List<Phong> findAll() {
       return em.createQuery(...);
   }
   
   // ✅ CORRECT - Query in Repository
   // Service only calls repository
   ```

---

## 🎓 LEARNING RESOURCES IN CODE

- Architecture explanation: `ARCHITECTURE.md`
- Database guide: `DATABASE_SETUP.md`
- Refactoring summary: `PROJECT_REFACTORING_SUMMARY.md`
- Service example: `core/service/impl/AuthenticationServiceImpl.java`
- Mapper example: `infrastructure/mapper/PhongMapper.java`

---

## ⚙️ DEVELOPMENT COMMANDS

```bash
# Clean build
mvn clean install

# Compile only
mvn compile

# Run tests
mvn test

# Create executable JAR
mvn package

# Skip tests during build
mvn clean install -DskipTests

# View dependencies
mvn dependency:tree
```

---

## 📞 QUICK TROUBLESHOOTING

| Problem | Solution |
|---------|----------|
| "Cannot find EntityManager" | Implement `EntityManagerConfig` in `infrastructure/db/` |
| "Repository methods not found" | Create `RepositoryImpl` in `infrastructure/persistence/` |
| "Service returns Entity not DTO" | Use Mapper classes to convert |
| "FXML not loading" | Ensure FXML files in `src/main/resources/fxml/` |
| "MariaDB connection fails" | Check `persistence.xml` database URL & credentials |

---

## 🎯 COMPLETION TARGET

```
Current Progress:  ████████░░░░░░░░░░░ 70%

Remaining:
├── Repository Implementations     [10%]
├── EntityManager Configuration    [5%]
├── JavaFX Controllers & UI       [10%]
└── Testing & Documentation       [5%]

Estimated completion: 2-3 hours with this template
```

---

**Remember**: Follow the pattern established in completed files!
Each new component should mirror the structure of existing implementations.

Last updated: May 5, 2026

