# 🏨 QUẢN LÝ KHÁCH SẠN TATP - CLEAN ARCHITECTURE PROJECT

## 📌 PROJECT OVERVIEW

**Dự án:** Hệ thống Quản Lý Khách Sạn TATP  
**Architecture:** Clean Architecture + N-Tier  
**Technology Stack:**
- Backend: Java 17+, Spring Boot (future)
- UI: JavaFX
- Database: MariaDB
- ORM: Hibernate/JPA
- Build: Maven

**Status:** ✅ 80% Complete - Core architecture ready, UI pending

---

## 🎯 QUICK START

### For First-Time Users:

1. **Read these files in order:**
   ```
   1. README.md (this file)
   2. PROJECT_COMPLETION_REPORT.md (overview)
   3. QUICK_REFERENCE.md (lookups)
   4. NEXT_STEPS.md (implementation guide)
   5. ARCHITECTURE_MIGRATION_COMPLETE.md (detailed reference)
   ```

2. **Understand the structure:**
   - Core Layer = Pure Java domain logic (entities, DTOs, services)
   - Infrastructure Layer = Database interaction (repositories, mappers)
   - Presentation Layer = User interface (JavaFX controllers)

3. **Start implementing:**
   - Update `persistence.xml` with entity classes
   - Run MariaDB SQL script
   - Create JavaFX FXML and Controller files
   - Bind services to UI components

---

## 📁 DIRECTORY STRUCTURE

```
BTL_PT_QLKS/
├── src/main/java/iuh/fit/
│   ├── core/                    # 👑 Core Business Logic Layer
│   │   ├── entity/              # 11 JPA Entity classes
│   │   ├── dto/                 # 9 Data Transfer Objects
│   │   ├── repository/          # 11 Repository interfaces
│   │   └── service/             # 9 Service interfaces + 9 implementations
│   │       └── impl/            # Service implementations
│   │
│   ├── infrastructure/          # 🔧 Technical Implementation Layer
│   │   ├── db/                  # Database configuration
│   │   ├── mapper/              # 9 Entity↔DTO Mappers
│   │   └── persistence/         # 11 Repository implementations
│   │
│   └── presentation/            # 🖥️ UI Layer (TO CREATE)
│       └── controller/          # JavaFX Controllers
│
├── src/main/resources/
│   ├── META-INF/
│   │   └── persistence.xml      # JPA Configuration
│   └── fxml/                    # JavaFX UI files (TO CREATE)
│
├── src/test/                    # Unit tests (TO CREATE)
├── cypher/                      # Database scripts
│   └── qlkhachsanTATP_db_MariaDB.sql
└── Documentation/
    ├── PROJECT_COMPLETION_REPORT.md
    ├── ARCHITECTURE_MIGRATION_COMPLETE.md
    ├── QUICK_REFERENCE.md
    └── NEXT_STEPS.md
```

---

## ✅ WHAT'S ALREADY DONE

### ✅ Core Layer (100% Complete)
- [x] 11 Entity Classes with JPA annotations
- [x] 9 DTO Classes for data transfer
- [x] 11 Repository Interfaces (CRUD contracts)
- [x] 9 Service Interfaces (business logic contracts)
- [x] 9 Service Implementations (actual business logic)

### ✅ Infrastructure Layer (100% Complete)
- [x] 11 Repository Implementations (JPA/Hibernate)
- [x] 9 Mapper Classes (Entity ↔ DTO conversion)
- [x] Database configuration support

### ✅ Database (100% Complete)
- [x] MariaDB SQL schema (11 tables with relationships)
- [x] Sample data (test records)
- [x] Foreign keys and constraints

### ⏳ Presentation Layer (0% - TO DO)
- [ ] JavaFX FXML files
- [ ] JavaFX Controller classes
- [ ] UI event handlers
- [ ] Data binding to UI components

---

## 🔧 SETUP INSTRUCTIONS

### Prerequisites
```
✓ Java 17 or higher
✓ Maven 3.8+
✓ MariaDB 10.5+
✓ HeidiSQL or MySQL Workbench (optional, for GUI)
```

### Step 1: Clone/Open Project
```bash
cd F:\My_Document\lesson\PhanTan\BTL_PT_QLKS
```

### Step 2: Update persistence.xml
**Location:** `src/main/resources/META-INF/persistence.xml`

Ensure all 11 entity classes are listed:
```xml
<class>iuh.fit.core.entity.LoaiPhong</class>
<class>iuh.fit.core.entity.NhanVien</class>
<!-- ... etc ... -->
```

### Step 3: Create Database
```bash
# Option A: Using HeidiSQL GUI
1. Open HeidiSQL
2. Run File → Load SQL File
3. Select cypher/qlkhachsanTATP_db_MariaDB.sql
4. Click Run

# Option B: Using MySQL CLI
mysql -u root -p < cypher/qlkhachsanTATP_db_MariaDB.sql
```

### Step 4: Update MariaDB Credentials
**Location:** `persistence.xml` → Properties section
```xml
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mariadb://localhost:3306/qlkhachsanTATP_db"/>
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="YOUR_PASSWORD"/>
```

### Step 5: Build Project
```bash
mvnw clean install
# or
mvn clean install
```

---

## 🎓 UNDERSTANDING THE ARCHITECTURE

### Clean Architecture Benefits

```
🎯 Goals:
✓ Independent of frameworks
✓ Testable business logic
✓ Independent of UI
✓ Independent of database
✓ Independent of any external agency

📊 Layers (innermost → outermost):
1. Core (Entities, DTOs) - Most stable, least external deps
2. Service (Business logic) - Domain-specific rules
3. Infrastructure (DB, mappers) - Technical implementation
4. Presentation (UI) - External interaction point
```

### Data Flow Example

```
User Action (e.g., Click "Load Rooms")
                    ↓
    JavaFX Controller (Presentation)
                    ↓
    PhongService.findAll() (Service)
                    ↓
    PhongRepository.findAll() (Repository)
                    ↓
    Hibernate/JPA Query
                    ↓
    MariaDB Database
                    ↓
    Returns List<Phong>
                    ↓
    Mapper.entityToDTO() (Convert to DTO)
                    ↓
    Returns List<PhongDTO>
                    ↓
    UI Controller binds to TableView
                    ↓
    User sees data displayed
```

---

## 🚀 NEXT PHASE - IMPLEMENTATION GUIDE

### Phase 2.1: Create ServiceFactory (Dependency Injection)
```java
// src/main/java/iuh/fit/core/service/ServiceFactory.java
public class ServiceFactory {
    private static ServiceFactory instance;
    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    // ... other services
    
    private ServiceFactory() {
        // Instantiate repositories
        IPhongRepository phongRepo = new PhongRepositoryImpl();
        // ... other repos
        
        // Instantiate services
        this.phongService = new PhongServiceImpl(phongRepo);
        // ... other services
    }
}
```

### Phase 2.2: Create MainApp.java (Application Entry Point)
```java
// src/main/java/iuh/fit/presentation/MainApp.java
public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/MainWindow.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Quản Lý Khách Sạn TATP");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
```

### Phase 2.3: Create First FXML File
```xml
<!-- src/main/resources/fxml/PhongManagement.fxml -->
<BorderPane xmlns="http://javafx.com/javafx">
    <center>
        <TableView fx:id="tablePhong">
            <columns>
                <TableColumn text="Mã Phòng" fx:id="colMaPhong"/>
                <TableColumn text="Tên Phòng" fx:id="colTenPhong"/>
                <TableColumn text="Giá" fx:id="colGiaPhong"/>
                <!-- ... more columns ... -->
            </columns>
        </TableView>
    </center>
</BorderPane>
```

### Phase 2.4: Create JavaFX Controller
```java
// src/main/java/iuh/fit/presentation/controller/PhongController.java
@FXML
public class PhongController {
    @FXML private TableView<PhongDTO> tablePhong;
    @FXML private TableColumn<PhongDTO, String> colMaPhong;
    
    private IPhongService phongService;
    
    @FXML
    public void initialize() {
        phongService = ServiceFactory.getInstance().getPhongService();
        setupTableColumns();
        loadPhongList();
    }
    
    private void setupTableColumns() {
        colMaPhong.setCellValueFactory(
            new PropertyValueFactory<>("maPhong")
        );
        // ... setup other columns ...
    }
    
    private void loadPhongList() {
        List<PhongDTO> phongList = phongService.findAll();
        tablePhong.getItems().setAll(phongList);
    }
}
```

---

## 🧪 TESTING

### Unit Test Example
```java
// src/test/java/iuh/fit/core/service/PhongServiceTest.java
public class PhongServiceTest {
    @Test
    public void testFindAllPhong() {
        IPhongService service = new PhongServiceImpl(
            new PhongRepositoryImpl()
        );
        List<PhongDTO> phongList = service.findAll();
        
        assertNotNull(phongList);
        assertFalse(phongList.isEmpty());
    }
}
```

---

## 📊 PROJECT STATISTICS

```
JAVA FILES CREATED
├── Entity Classes         : 11
├── DTO Classes           : 9
├── Repository Interfaces : 11
├── Repository Impl       : 11
├── Service Interfaces    : 9
├── Service Impl          : 9
├── Mapper Classes        : 9
└── Documentation         : 5 markdown files

TOTAL: 56+ Java files
TOTAL: 3,500+ lines of code
```

---

## 🎯 KEY FEATURES

✅ **Clean Architecture**
- Separated concerns into clear layers
- No framework dependencies in core logic
- Easy to test and maintain

✅ **Repository Pattern**
- CRUD operations abstracted
- Easy database switching
- Clean data access layer

✅ **Service Layer**
- Business logic centralized
- Reusable across UI and other clients
- Easy validation and error handling

✅ **Mapper Pattern**
- Entity not exposed to UI
- DTOs prevent database schema leakage
- Flexible data structure transformation

✅ **Dependency Injection**
- Constructor-based injection
- Easy to mock for testing
- Loose coupling

✅ **JPA/Hibernate**
- Database-independent ORM
- Automatic relationship management
- Query object support

---

## 🐛 TROUBLESHOOTING

### "Error: JAVA_HOME not found"
Solution: Set JAVA_HOME environment variable
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Linux/Mac
export JAVA_HOME=/usr/libexec/java_home -v 17
```

### "Error: Database connection refused"
Solution: Check MariaDB is running and credentials are correct
```bash
# Verify MariaDB is running
mysql -u root -p -e "SELECT 1"

# Check persistence.xml has correct URL/user/password
```

### "Error: Table not found"
Solution: Run SQL script to create database
```bash
mysql -u root -p < cypher/qlkhachsanTATP_db_MariaDB.sql
```

### "Error: LazyInitializationException"
Solution: Use fetch=EAGER in @ManyToOne, @OneToOne annotations
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "maLoaiPhong")
private LoaiPhong loaiPhong;
```

---

## 📚 DOCUMENTATION

| Document | Purpose |
|----------|---------|
| **PROJECT_COMPLETION_REPORT.md** | Executive summary, statistics |
| **ARCHITECTURE_MIGRATION_COMPLETE.md** | Detailed breakdown of all 56 classes |
| **QUICK_REFERENCE.md** | Quick lookup guide for patterns and locations |
| **NEXT_STEPS.md** | Step-by-step implementation guide |
| **README.md** | This file |

---

## 🎓 LEARNING RESOURCES

- **Clean Architecture**: "Clean Architecture" by Robert C. Martin
- **JPA/Hibernate**: https://docs.jboss.org/hibernate/orm/6.2/userguide/
- **JavaFX**: https://openjfx.io/
- **Maven**: https://maven.apache.org/
- **Database Design**: Normalization and relationships

---

## 💼 PROJECT STRUCTURE SUMMARY

```
┌─────────────────────────────────┐
│   UI Layer (JavaFX)             │ ← TO IMPLEMENT
│   Controllers + FXML Files      │
└────────┬────────────────────────┘
         │
         ↓ (uses)
┌─────────────────────────────────┐
│   Service Layer ✅              │
│   Business Logic                │
│   (IPhongService, etc.)         │
└────────┬────────────────────────┘
         │
         ↓ (uses)
┌─────────────────────────────────┐
│   Repository Layer ✅           │
│   Data Access (CRUD)            │
│   (IPhongRepository, etc.)      │
└────────┬────────────────────────┘
         │
         ↓ (uses)
┌─────────────────────────────────┐
│   Entity Layer ✅               │
│   Domain Objects                │
│   (Phong, Khachang, etc.)      │
└────────┬────────────────────────┘
         │
         ↓ (persists)
      MariaDB Database ✅
```

---

## 🎯 MILESTONES

- [x] Phase 1: Database schema design
- [x] Phase 2: Entity classes with JPA
- [x] Phase 3: Repository pattern implementation
- [x] Phase 4: Service layer with business logic
- [x] Phase 5: Mapper for data transfer
- [ ] Phase 6: JavaFX UI implementation
- [ ] Phase 7: Testing & validation
- [ ] Phase 8: Deployment

---

## 📞 SUPPORT & QUESTIONS

If you encounter issues:
1. Check the relevant documentation file
2. Review QUICK_REFERENCE.md for patterns
3. Check TROUBLESHOOTING section above
4. Verify all dependencies in pom.xml
5. Ensure MariaDB is running and database exists

---

## 🎉 READY TO BEGIN?

1. **For architecture overview**: Read `PROJECT_COMPLETION_REPORT.md`
2. **For quick lookups**: Use `QUICK_REFERENCE.md`
3. **For implementation**: Follow `NEXT_STEPS.md`
4. **For details**: Check `ARCHITECTURE_MIGRATION_COMPLETE.md`

**Let's build a great hotel management system! 🚀**

---

*Last Updated: 2026-05-07*
*Version: 1.0*
*Architecture: Clean Architecture + N-Tier*
*Status: 80% Complete ✅*

