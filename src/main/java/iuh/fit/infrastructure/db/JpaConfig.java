package iuh.fit.infrastructure.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.logging.Logger;

/**
 * Class: JpaConfig (Database Configuration)
 * 
 * Tầng: INFRASTRUCTURE - Database Configuration Layer
 * Trách nhiệm: Quản lý EntityManagerFactory và cung cấp EntityManager cho ứng dụng
 * 
 * Pattern: Singleton
 * - Chỉ có một instance EntityManagerFactory trong toàn bộ ứng dụng
 * - Singleton đảm bảo hiệu suất và quản lý resource tốt
 * 
 * Quy trình:
 * 1. Ứng dụng khởi động → JpaConfig.getEntityManagerFactory() được gọi
 * 2. Tạo EntityManagerFactory từ persistence.xml
 * 3. Factory này được reuse để tạo EntityManager cho mỗi transaction
 */
public class JpaConfig {
    
    private static final Logger logger = Logger.getLogger(JpaConfig.class.getName());
    
    private static EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT_NAME = "qlkhachsan-pu";
    
    /**
     * Private constructor để ngăn instantiation từ bên ngoài
     * Đây là Singleton pattern
     */
    private JpaConfig() {
    }
    
    /**
     * Lấy EntityManagerFactory (Lazy Initialization)
     * Chỉ tạo EMF một lần lúc first call
     * 
     * @return EntityManagerFactory instance
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            try {
                logger.info("🔄 Khởi tạo EntityManagerFactory từ persistence.xml...");
                
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                
                logger.info("✅ EntityManagerFactory được tạo thành công!");
            } catch (Exception e) {
                logger.severe("❌ Lỗi khởi tạo EntityManagerFactory: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Không thể khởi tạo EntityManagerFactory", e);
            }
        }
        return emf;
    }
    
    /**
     * Lấy EntityManager mới từ factory
     * Mỗi EntityManager quản lý một transaction
     * 
     * @return EntityManager instance mới
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Đóng EntityManagerFactory
     * Gọi khi ứng dụng shutdown
     * QUAN TRỌNG: Phải gọi phương thức này để giải phóng resource
     */
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            logger.info("🔌 Đóng EntityManagerFactory...");
            emf.close();
            emf = null;
            logger.info("✅ EntityManagerFactory đã được đóng");
        }
    }
    
    /**
     * Kiểm tra xem EMF có đang mở không
     * @return true nếu EMF mở, false nếu đã đóng
     */
    public static boolean isEntityManagerFactoryOpen() {
        return emf != null && emf.isOpen();
    }
}

