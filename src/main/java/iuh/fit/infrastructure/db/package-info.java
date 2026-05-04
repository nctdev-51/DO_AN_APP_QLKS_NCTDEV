/**
 * Package: iuh.fit.infrastructure.db
 * 
 * TẦNG: INFRASTRUCTURE - Database Configuration
 * 
 * TRÁCH NHIỆM:
 * - Cấu hình JPA/Hibernate
 * - Quản lý EntityManagerFactory (tạo, đóng)
 * - Cung cấp EntityManager cho repositories
 * - Handle connection pooling (HikariCP)
 * 
 * NGUYÊN TẮC:
 * ✅ Singleton pattern (chỉ một EMF cho toàn ứng dụng)
 * ✅ Lazy initialization (tạo khi cần)
 * ✅ Centralized configuration
 * ✅ Proper resource management (close connections)
 * ❌ KHÔNG có business logic
 * ❌ KHÔNG phụ thuộc vào CORE service
 * 
 * CẤU TRÚC:
 * 
 * Application Startup:
 *   ↓
 * MainApp.start()
 *   ↓
 * JpaConfig.getEntityManagerFactory()
 *   ↓
 * Persistence.createEntityManagerFactory("qlkhachsan-pu")
 *   ↓
 * persistence.xml cấu hình
 *   ↓
 * Connection to MariaDB
 * 
 * Repository Usage:
 * 
 * RepositoryImpl.save(entity):
 *   ↓
 * EntityManager em = JpaConfig.getEntityManager()
 *   ↓
 * em.persist(entity) / em.merge(entity)
 *   ↓
 * em.close()
 * 
 * CÁC FILES:
 * - JpaConfig.java: Singleton quản lý EntityManagerFactory
 *   ├─ getEntityManagerFactory(): Lấy EMF (singleton)
 *   ├─ getEntityManager(): Tạo EntityManager mới
 *   ├─ closeEntityManagerFactory(): Cleanup
 *   └─ isEntityManagerFactoryOpen(): Check status
 * 
 * PERSISTENCE.XML:
 * - src/main/resources/META-INF/persistence.xml
 * - Định nghĩa:
 *   ├─ Persistence Unit name
 *   ├─ Entity classes list
 *   ├─ Database URL
 *   ├─ Username/password
 *   ├─ Hibernate dialect (MariaDBDialect)
 *   ├─ hbm2ddl.auto: update | create | validate
 *   └─ Connection pool settings
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.infrastructure.db;

