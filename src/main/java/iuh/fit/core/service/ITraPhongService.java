package iuh.fit.core.service;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.ChiTietHoaDonDTO;

import java.util.List;

/**
 * Interface: ITraPhongService (Service Port)
 *
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Định nghĩa hợp đồng logic nghiệp vụ cho Trả Phòng & Thanh Toán
 *
 * Pattern: Tách business logic từ Controller
 */
public interface ITraPhongService {

    /**
     * Lấy danh sách phiếu đang ở (DA_NHAN_PHONG)
     * @return Danh sách phiếu đặt phòng có trạng thái "DA_NHAN_PHONG"
     */
    List<PhieuDatPhongDTO> getPhieuDangO();

    /**
     * Lấy chi tiết phiếu + tính tiền phòng và dịch vụ
     * @param maPhieu Mã phiếu đặt phòng
     * @return PhieuDatPhongDTO với thông tin đầy đủ
     * @throws IllegalArgumentException nếu phiếu không tồn tại
     */
    PhieuDatPhongDTO loadPhieuDetail(String maPhieu) throws IllegalArgumentException;

    /**
     * Tính hóa đơn chi tiết (tiền phòng + dịch vụ + VAT + chiết khấu)
     * @param maPhieu Mã phiếu
     * @param vatPercent Tỷ lệ VAT (0.08 = 8%)
     * @param chietKhau Chiết khấu (đ)
     * @return HoaDonDTO chứa tất cả thông tin tính toán
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    HoaDonDTO calculateCheckoutInvoice(String maPhieu, double vatPercent, double chietKhau)
            throws IllegalArgumentException;

    /**
     * Lấy danh sách dịch vụ của phiếu
     * @param maPhieu Mã phiếu
     * @return Danh sách dịch vụ đã sử dụng
     */
    List<ChiTietHoaDonDTO> getChiTietDichVu(String maPhieu);

    /**
     * Xác nhận trả phòng: Lưu hóa đơn + Update phiếu + Update phòng
     * @param maPhieu Mã phiếu
     * @param hoaDon Thông tin hóa đơn
     * @param phuongThucThanhToan Phương thức thanh toán
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    void confirmCheckout(String maPhieu, HoaDonDTO hoaDon, String phuongThucThanhToan)
            throws IllegalArgumentException;
}

