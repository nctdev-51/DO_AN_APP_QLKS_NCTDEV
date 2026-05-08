package iuh.fit.core.repository;

import iuh.fit.core.entity.KhachHang;

import java.util.Optional;
import java.util.List;

/**
 * Interface: IKhachHangRepository (Repository Port)
 * 
 * Tầng: CORE - Repository Layer
 * Trách nhiệm: Định nghĩa hợp đồng để thao tác dữ liệu Khách Hàng
 */
public interface IKhachHangRepository {
    
    /**
     * Tìm khách hàng theo mã
     * @param maKhachHang Mã khách hàng
     * @return Optional chứa KhachHang nếu tồn tại
     */
    Optional<KhachHang> findById(String maKhachHang);
    
    /**
     * Lấy tất cả khách hàng
     * @return Danh sách tất cả khách hàng
     */
    List<KhachHang> findAll();
    
    /**
     * Lưu khách hàng mới
     * @param khachHang Entity KhachHang
     * @return KhachHang vừa được lưu
     */
    KhachHang save(KhachHang khachHang);
    
    /**
     * Cập nhật khách hàng
     * @param khachHang Entity KhachHang
     * @return KhachHang vừa được cập nhật
     */
    KhachHang update(KhachHang khachHang);
    
    /**
     * Xóa khách hàng theo mã
     * @param maKhachHang Mã khách hàng
     */
    void deleteById(String maKhachHang);
    
    /**
     * Tìm khách hàng theo số điện thoại
     * @param soDienThoai Số điện thoại
     * @return Optional chứa KhachHang nếu tồn tại
     */
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    String phatSinhMaKhachHangMoi();
}

