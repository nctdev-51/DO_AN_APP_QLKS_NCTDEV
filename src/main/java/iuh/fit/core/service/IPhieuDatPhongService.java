package iuh.fit.core.service;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.entity.PhieuDatPhong;

import java.time.LocalDate;
import java.util.List;

public interface IPhieuDatPhongService {
    List<PhieuDatPhongDTO> getAllPhieuDatPhong();
    PhieuDatPhongDTO getPhieuDatPhongById(String maPhieu);
    List<PhieuDatPhongDTO> getPhieuDatPhongByKhachHang(String maKhachHang);
    List<PhieuDatPhongDTO> getPhieuDatPhongByPhong(String maPhong);
    List<PhieuDatPhongDTO> getPhieuDatPhongInDateRange(LocalDate startDate, LocalDate endDate);

    // ✅ Thêm mới: Lọc phiếu đặt phòng theo trạng thái
    List<PhieuDatPhongDTO> getPhieuDatPhongByTrangThai(String status);

    PhieuDatPhongDTO addPhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException;
    PhieuDatPhongDTO updatePhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException;
    boolean deletePhieuDatPhong(String maPhieu);

    // Thực thi transaction lưu phiếu lúc đặt phòng
    boolean bookRoomTransaction(PhieuDatPhongDTO phieuDTO);

    // ✅ Thêm mới: Transaction Checkout (Lưu phiếu + cập nhật phòng + lưu hóa đơn)
    boolean checkoutTransaction(String maPhieu, HoaDonDTO hoaDonDTO);
    List<PhieuDatPhong> findByTrangThai(String trangThai);

    List<PhieuDatPhong> findAll();

    String phatSinhMaPhieuMoi();
}