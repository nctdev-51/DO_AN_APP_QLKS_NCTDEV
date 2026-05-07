# 🚀 NEXT STEPS - Hoàn tất Clean Architecture Project

## Phase 2: Database & Configuration Setup

### Step 1: Kiểm tra và cập nhật persistence.xml
**Location:** `src/main/resources/META-INF/persistence.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence 
             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">

    <persistence-unit name="qlkhachsanTATP" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernateJpaProvider</provider>
        
        <!-- List tất cả Entity classes -->
        <class>iuh.fit.core.entity.LoaiPhong</class>
        <class>iuh.fit.core.entity.NhanVien</class>
        <class>iuh.fit.core.entity.TaiKhoan</class>
        <class>iuh.fit.core.entity.KhachHang</class>
        <class>iuh.fit.core.entity.KhuyenMai</class>
        <class>iuh.fit.core.entity.Phong</class>
        <class>iuh.fit.core.entity.DichVu</class>
        <class>iuh.fit.core.entity.PhieuDatPhong</class>
        <class>iuh.fit.core.entity.HoaDon</class>
        <class>iuh.fit.core.entity.ChiTietPhieuDatPhong</class>
        <class>iuh.fit.core.entity.ChiTietHoaDon</class>
        
        <properties>
            <!-- MariaDB Configuration -->
            <property name="jakarta.persistence.jdbc.driver" value="org.mariadb.jdbc.Driver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://localhost:3306/qlkhachsanTATP_db"/>
            <property name="jakarta.persistence.jdbc.user" value="root"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>
            
            <!-- Hibernate Settings -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.MariaDBDialect"/>
            <property name="hibernate.hbm2ddl.auto" value="validate"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.use_sql_comments" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

### Step 2: Chạy SQL Script trên MariaDB
**Location:** `cypher/qlkhachsanTATP_db_MariaDB.sql`

```bash
# Mở HeidiSQL hoặc MySQL CLI
mysql -u root -p < cypher/qlkhachsanTATP_db_MariaDB.sql

# Hoặc copy-paste script vào HeidiSQL GUI
```

---

## Phase 3: Presentation Layer (JavaFX UI)

### Step 3: Tạo MainApp.java
**Location:** `src/main/java/iuh/fit/presentation/MainApp.java`

```java
package iuh.fit.presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Quản Lý Khách Sạn TATP");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

### Step 4: Tạo các FXML files
**Locations:** `src/main/resources/fxml/`

Ví dụ: `PhongManagement.fxml`, `KhachHangManagement.fxml`, `HoaDonManagement.fxml`

### Step 5: Tạo các JavaFX Controllers
**Location:** `src/main/java/iuh/fit/presentation/controller/`

Ví dụ: `PhongController.java`, `KhachHangController.java`, etc.

---

## Phase 4: Service Dependency Setup

### Step 6: Tạo ServiceFactory
**Location:** `src/main/java/iuh/fit/core/service/ServiceFactory.java`

```java
package iuh.fit.core.service;

import iuh.fit.core.service.impl.*;
import iuh.fit.core.repository.*;
import iuh.fit.infrastructure.persistence.*;

public class ServiceFactory {
    private static ServiceFactory instance;
    
    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    private final IHoaDonService hoaDonService;
    // ... các service khác
    
    private ServiceFactory() {
        // Khởi tạo Repositories
        IPhongRepository phongRepo = new PhongRepositoryImpl();
        IKhachHangRepository khachHangRepo = new KhachHangRepositoryImpl();
        IHoaDonRepository hoaDonRepo = new HoaDonRepositoryImpl();
        // ...
        
        // Khởi tạo Services
        this.phongService = new PhongServiceImpl(phongRepo);
        this.khachHangService = new KhachHangServiceImpl(khachHangRepo);
        this.hoaDonService = new HoaDonServiceImpl(hoaDonRepo);
        // ...
    }
    
    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }
    
    public IPhongService getPhongService() { return phongService; }
    public IKhachHangService getKhachHangService() { return khachHangService; }
    public IHoaDonService getHoaDonService() { return hoaDonService; }
    // ...
}
```

### Step 7: Sử dụng Services trong Controller

```java
public class PhongController {
    private final IPhongService phongService;
    
    public PhongController() {
        this.phongService = ServiceFactory.getInstance().getPhongService();
    }
    
    public void loadPhongList() {
        List<PhongDTO> phongList = phongService.findAll();
        tablePhong.getItems().setAll(phongList);
    }
}
```

---

## Phase 5: Testing & Validation

### Step 8: Unit Test Examples

```java
// src/test/java/iuh/fit/core/service/impl/PhongServiceTest.java

@Test
public void testFindAllPhong() {
    IPhongService service = new PhongServiceImpl(new PhongRepositoryImpl());
    List<PhongDTO> phongList = service.findAll();
    assertNotNull(phongList);
    assertFalse(phongList.isEmpty());
}

@Test
public void testCreatePhong() {
    PhongDTO dto = new PhongDTO();
    dto.setMaPhong("P999");
    dto.setTenPhong("Test Room");
    dto.setGiaPhong(500000);
    dto.setMaLoaiPhong("DON");
    dto.setTinhTrang("Trống");
    
    IPhongService service = new PhongServiceImpl(new PhongRepositoryImpl());
    PhongDTO saved = service.create(dto);
    assertNotNull(saved);
    assertEquals("P999", saved.getMaPhong());
}
```

---

## 📋 Checklist Hoàn Tất Project

### Core/Service Layer ✅
- [x] Entities (11 classes)
- [x] DTOs (9 classes)
- [x] Repository Interfaces (11 interfaces)
- [x] Repository Implementations (11 classes)
- [x] Service Interfaces (9 interfaces)
- [x] Service Implementations (6 classes)
- [x] Mapper Classes (9 classes)

### Infrastructure Layer ✅
- [x] Database Configuration (persistence.xml)
- [x] JpaConfig (EntityManagerFactory)
- [x] Mapper utilities

### Presentation Layer 🔄
- [ ] MainApp.java
- [ ] FXML files
- [ ] Controllers (Phòng, Khách hàng, Hóa đơn, etc.)
- [ ] UI binding & event handlers

### Database & Testing 🔄
- [ ] Run SQL migration script
- [ ] Unit tests for Services
- [ ] Integration tests
- [ ] UI testing

### Optimization & Documentation ⏳
- [ ] Exception handling
- [ ] Logging configuration
- [ ] API documentation
- [ ] Code cleanup & refactoring

---

## 🔥 Quick Start Commands

```bash
# 1. Clone/Setup project
cd F:\My_Document\lesson\PhanTan\BTL_PT_QLKS

# 2. Build with Maven
mvnw clean install

# 3. Run SQL migration
# - Open HeidiSQL
# - Execute cypher/qlkhachsanTATP_db_MariaDB.sql

# 4. Run application
# - Right-click MainApp.java → Run

# 5. Test sample flow
# - Login with admin/123
# - View Phòng list
# - Create new Khách hàng
# - Create Phiếu đặt phòng
# - Generate Hóa đơn
```

---

## 🎓 Learning Outcomes

Sau khi hoàn tất project này, bạn sẽ hiểu rõ về:

✅ **Clean Architecture** - Tách biệt concerns thành các tầng rõ ràng
✅ **N-Tier Architecture** - Presentation → Core → Infrastructure
✅ **JPA/Hibernate** - ORM mapping entity ↔ database
✅ **Design Patterns** - Repository, Mapper, Factory, DAO
✅ **JavaFX** - Modern desktop UI framework
✅ **Maven** - Build tool & dependency management
✅ **Database** - MariaDB, SQL scripting, normalization

---

## 📞 Support

Nếu gặp vấn đề:
1. Kiểm tra lại Entity relationships (@JoinColumn)
2. Đảm bảo persistence.xml có đầy đủ Entity classes
3. Verify MariaDB connection properties
4. Check logs để xem error messages
5. Review database schema vs Entity mapping

---

**🎉 Happy Coding! 🎉**

