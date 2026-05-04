package iuh.fit.core.repository;

import iuh.fit.core.entity.TaiKhoan;

import java.util.Optional;
import java.util.List;

/**
 * Interface: ITaiKhoanRepository (Repository Port)
 * 
 * Tầng: CORE - Repository Layer (Bounded Context Port)
 * Trách nhiệm: Định nghĩa hợp đồng (contract) để thao tác dữ liệu Tài Khoản
 * 
 * Nguyên tắc:
 * - KHÔNG phụ thuộc vào JPA, Hibernate hay bất kỳ DB framework nào
 * - KHÔNG có @Repository annotation (annotation ở adapter layer)
 * - CHỈ định nghĩa các method business-focused, không có SQL trực tiếp
 * - Implementation sẽ được cung cấp ở infrastructure.persistence layer
 * 
 * Pattern: Dependency Inversion Principle (DIP)
 * - Core layer định nghĩa interface
 * - Infrastructure layer implement interface
 * - Service layer gọi interface, không biết implementation
 */
public interface ITaiKhoanRepository {
    
    /**
     * Tìm tài khoản theo username
     * @param taiKhoan Username
     * @return Optional chứa TaiKhoan nếu tồn tại
     */
    Optional<TaiKhoan> findByTaiKhoan(String taiKhoan);
    
    /**
     * Kiểm tra xem tài khoản có tồn tại hay không
     * @param taiKhoan Username
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByTaiKhoan(String taiKhoan);
    
    /**
     * Lưu một tài khoản mới vào database
     * @param taiKhoan Entity TaiKhoan cần lưu
     * @return TaiKhoan vừa được lưu
     */
    TaiKhoan save(TaiKhoan taiKhoan);
    
    /**
     * Lấy tất cả tài khoản
     * @return Danh sách tất cả TaiKhoan
     */
    List<TaiKhoan> findAll();
    
    /**
     * Xóa tài khoản theo username
     * @param taiKhoan Username cần xóa
     */
    void deleteByTaiKhoan(String taiKhoan);
    
    /**
     * Cập nhật tài khoản
     * @param taiKhoan Entity TaiKhoan cần cập nhật
     * @return TaiKhoan vừa được cập nhật
     */
    TaiKhoan update(TaiKhoan taiKhoan);
}

