package iuh.fit.core.service;

import iuh.fit.core.dto.NhanVienDTO;

import java.util.List;

/**
 * Interface: INhanVienService (Service Port)
 * 
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Định nghĩa hợp đồng cho nghiệp vụ Quản lý Nhân Viên
 */
public interface INhanVienService {
    
    /**
     * Lấy danh sách tất cả nhân viên
     * @return Danh sách NhanVienDTO
     */
    List<NhanVienDTO> getAllNhanVien();
    
    /**
     * Tìm nhân viên theo mã
     * @param maNhanVien Mã nhân viên
     * @return NhanVienDTO nếu tìm thấy, null nếu không
     */
    NhanVienDTO getNhanVienById(String maNhanVien);
    
    /**
     * Tìm nhân viên theo số điện thoại
     * @param soDienThoai Số điện thoại
     * @return NhanVienDTO nếu tìm thấy, null nếu không
     */
    NhanVienDTO getNhanVienBySoDienThoai(String soDienThoai);
    
    /**
     * Thêm nhân viên mới
     * @param nhanVienDTO DTO chứa thông tin nhân viên
     * @return NhanVienDTO vừa được tạo
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    NhanVienDTO addNhanVien(NhanVienDTO nhanVienDTO) throws IllegalArgumentException;
    
    /**
     * Cập nhật thông tin nhân viên
     * @param nhanVienDTO DTO chứa thông tin nhân viên cần cập nhật
     * @return NhanVienDTO vừa được cập nhật
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    NhanVienDTO updateNhanVien(NhanVienDTO nhanVienDTO) throws IllegalArgumentException;
    
    /**
     * Xóa nhân viên
     * @param maNhanVien Mã nhân viên cần xóa
     * @return true nếu xóa thành công
     */
    boolean deleteNhanVien(String maNhanVien);
    
    /**
     * Lấy mã nhân viên tiếp theo (auto-generate)
     * @return Mã nhân viên mới (VD: NV001)
     */
    String generateMaNhanVien();
}

