package iuh.fit.core.service;

import iuh.fit.core.dto.KhachHangDTO;

import java.util.List;

/**
 * Interface: IKhachHangService (Service Port)
 * 
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Định nghĩa hợp đồng cho nghiệp vụ Quản lý Khách Hàng
 */
public interface IKhachHangService {
    
    /**
     * Lấy danh sách tất cả khách hàng
     * @return Danh sách KhachHangDTO
     */
    List<KhachHangDTO> getAllKhachHang();
    
    /**
     * Tìm khách hàng theo mã
     * @param maKhachHang Mã khách hàng
     * @return KhachHangDTO nếu tìm thấy, null nếu không
     */
    KhachHangDTO getKhachHangById(String maKhachHang);
    
    /**
     * Tìm khách hàng theo số điện thoại
     * @param soDienThoai Số điện thoại
     * @return KhachHangDTO nếu tìm thấy, null nếu không
     */
    KhachHangDTO getKhachHangBySoDienThoai(String soDienThoai);
    
    /**
     * Thêm khách hàng mới
     * @param khachHangDTO DTO chứa thông tin khách hàng
     * @return KhachHangDTO vừa được tạo
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    KhachHangDTO addKhachHang(KhachHangDTO khachHangDTO) throws IllegalArgumentException;
    
    /**
     * Cập nhật thông tin khách hàng
     * @param khachHangDTO DTO chứa thông tin khách hàng cần cập nhật
     * @return KhachHangDTO vừa được cập nhật
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    KhachHangDTO updateKhachHang(KhachHangDTO khachHangDTO) throws IllegalArgumentException;
    
    /**
     * Xóa khách hàng
     * @param maKhachHang Mã khách hàng cần xóa
     * @return true nếu xóa thành công
     */
    boolean deleteKhachHang(String maKhachHang);
}

