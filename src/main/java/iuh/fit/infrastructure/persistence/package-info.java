/**
 * Package: iuh.fit.infrastructure.persistence
 * 
 * TẦNG: INFRASTRUCTURE - Repository Implementation (Adapter)
 * 
 * TRÁCH NHIỆM:
 * - Implement Repository interfaces từ CORE layer
 * - Execute JPA/Hibernate queries
 * - Transform Entity ↔ Database
 * - Handle transactions
 * - Exception handling & logging
 * 
 * NGUYÊN TẮC (Adapter Pattern):
 * 
 * CORE defines Port:            INFRASTRUCTURE implements Adapter:
 * ├─ IRepository                ├─ RepositoryImpl
 * ├─ findById(id)               ├─ EntityManager.find()
 * ├─ save(entity)               ├─ EntityManager.persist()
 * └─ update(entity)             └─ EntityManager.merge()
 * 
 * LUỒNG:
 * 1. Service gọi IRepository (interface)
 * 2. RepositoryImpl (adapter) implement interface
 * 3. Adapter sử dụng JPA/Hibernate
 * 4. JPA translate thành SQL
 * 5. Database execute SQL
 * 6. Result map lại Entity
 * 
 * NGUYÊN TẮC TRIỂN KHAI:
 * ✅ Implement tất cả methods từ Interface
 * ✅ Sử dụng EntityManager từ JpaConfig
 * ✅ Handle EntityTransaction (begin, commit, rollback)
 * ✅ Log operations (info, warn, error)
 * ✅ Close EntityManager trong finally block
 * ✅ Detailed error handling
 * ❌ KHÔNG có business logic (validation, rules)
 * ❌ KHÔNG gọi Service layer
 * ❌ KHÔNG có @Repository annotation (không dùng Spring DI)
 * 
 * TRANSACTION PATTERN:
 * 
 * EntityManager em = JpaConfig.getEntityManager();
 * EntityTransaction tx = em.getTransaction();
 * try {
 *     tx.begin();              // START transaction
 *     em.persist(entity);      // Execute operation
 *     tx.commit();             // SAVE changes
 *     return entity;
 * } catch (Exception e) {
 *     if (tx.isActive()) {
 *         tx.rollback();       // UNDO changes if error
 *     }
 *     throw new RuntimeException(e);
 * } finally {
 *     em.close();              // CLEANUP
 * }
 * 
 * CÁC FILES:
 * - TaiKhoanRepositoryImpl.java
 *   ├─ findByTaiKhoan(username)
 *   ├─ existsByTaiKhoan(username)
 *   ├─ save(entity)
 *   ├─ findAll()
 *   ├─ deleteByTaiKhoan(username)
 *   └─ update(entity)
 * 
 * - KhachHangRepositoryImpl.java
 *   ├─ findById(id)
 *   ├─ findBySoDienThoai(phone)
 *   ├─ findAll()
 *   ├─ save(entity)
 *   ├─ update(entity)
 *   └─ deleteById(id)
 * 
 * LƯỚI ÝÝ:
 * - Repository Implementation KHÔNG phụ thuộc vào Business Logic
 * - Nó chỉ translate Interface method → Database Query
 * - Tất cả validation nên ở Service layer
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.infrastructure.persistence;

