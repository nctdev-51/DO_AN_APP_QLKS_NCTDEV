package iuh.fit.core.repository;

import iuh.fit.core.entity.NhanVien;

import java.util.Optional;
import java.util.List;

/**
 * Interface: INhanVienRepository (Repository Port)
 * 
 * Tầng: CORE - Repository Layer
 * Trách nhiệm: Định nghĩa hợp đồng để thao tác dữ liệu Nhân Viên
 */
public interface INhanVienRepository {
    
    /**
     * Tìm nhân viên theo mã
     * @param maNhanVien Mã nhân viên
     * @return Optional chứa NhanVien nếu tồn tại
     */
    Optional<NhanVien> findById(String maNhanVien);
    
    /**
     * Lấy tất cả nhân viên
     * @return Danh sách tất cả nhân viên
     */
    List<NhanVien> findAll();
    
    /**
     * Lưu nhân viên mới
     * @param nhanVien Entity NhanVien
     * @return NhanVien vừa được lưu
     */
    NhanVien save(NhanVien nhanVien);
    
    /**
     * Cập nhật nhân viên
     * @param nhanVien Entity NhanVien
     * @return NhanVien vừa được cập nhật
     */
    NhanVien update(NhanVien nhanVien);
    
    /**
     * Xóa nhân viên theo mã
     * @param maNhanVien Mã nhân viên
     */
    void deleteById(String maNhanVien);
    
    /**
     * Tìm nhân viên theo số điện thoại
     * @param soDienThoai Số điện thoại
     * @return Optional chứa NhanVien nếu tồn tại
     */
    Optional<NhanVien> findBySoDienThoai(String soDienThoai);
}

