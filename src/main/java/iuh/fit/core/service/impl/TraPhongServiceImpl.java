package iuh.fit.core.service.impl;

import iuh.fit.core.dto.*;
import iuh.fit.core.entity.PhieuDatPhong;
import iuh.fit.core.repository.*;
import iuh.fit.core.service.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class: TraPhongServiceImpl (Service Implementation)
 *
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Implement ITraPhongService - Logic nghiệp vụ Trả Phòng & Thanh Toán
 *
 * Refactored từ TraPhongController:
 * - Logic lọc phiếu (loadPhieuDatPhong)
 * - Logic tính tiền (calculateInvoice)
 * - Logic xác nhận trả phòng (confirmCheckout)
 */
public class TraPhongServiceImpl implements ITraPhongService {

    private final IPhieuDatPhongService phieuDatPhongService;
    private final IPhongService phongService;
    private final IHoaDonService hoaDonService;
    private final IChiTietHoaDonService chiTietHoaDonService;

    public TraPhongServiceImpl(IPhieuDatPhongService phieuDatPhongService,
                              IPhongService phongService,
                              IHoaDonService hoaDonService,
                              IChiTietHoaDonService chiTietHoaDonService) {
        this.phieuDatPhongService = phieuDatPhongService;
        this.phongService = phongService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
    }

    @Override
    public List<PhieuDatPhongDTO> getPhieuDangO() {
        return phieuDatPhongService.getAllPhieuDatPhong().stream()
                .filter(p -> "DA_NHAN_PHONG".equalsIgnoreCase(p.getTrangThai()))
                .collect(Collectors.toList());
    }

    @Override
    public PhieuDatPhongDTO loadPhieuDetail(String maPhieu) throws IllegalArgumentException {
        if (maPhieu == null || maPhieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được để trống");
        }

        PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
        if (phieu == null) {
            throw new IllegalArgumentException("Phiếu không tồn tại: " + maPhieu);
        }

        return phieu;
    }

    @Override
    public List<ChiTietHoaDonDTO> getChiTietDichVu(String maPhieu) {
        try {
            return chiTietHoaDonService.getChiTietByMaPhieu(maPhieu);
        } catch (Exception e) {
            System.err.println("[TraPhongService] Lỗi lấy dịch vụ: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public HoaDonDTO calculateCheckoutInvoice(String maPhieu, double vatPercent, double chietKhau)
            throws IllegalArgumentException {
        // Validate input
        if (maPhieu == null || maPhieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được để trống");
        }
        if (vatPercent < 0 || vatPercent > 1) {
            throw new IllegalArgumentException("VAT phải từ 0 đến 1");
        }
        if (chietKhau < 0) {
            throw new IllegalArgumentException("Chiết khấu không được âm");
        }

        // Lấy phiếu
        PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
        if (phieu == null) {
            throw new IllegalArgumentException("Phiếu không tồn tại");
        }

        // Tính tiền phòng
        PhongDTO phong = phongService.getPhongById(phieu.getMaPhong());
        if (phong == null) {
            throw new IllegalArgumentException("Phòng không tồn tại");
        }

        LocalDate ngayNhan = phieu.getNgayNhan() != null ? phieu.getNgayNhan() : LocalDate.now();
        LocalDate ngayTra = phieu.getNgayTra() != null ? phieu.getNgayTra() : LocalDate.now();

        long soNgay = ChronoUnit.DAYS.between(ngayNhan, ngayTra);
        if (soNgay < 1) soNgay = 1;

        double tongTienPhong = phong.getGiaPhong() * soNgay;

        // Tính tiền dịch vụ
        double tongTienDichVu = 0;
        try {
            List<ChiTietHoaDonDTO> dichVus = chiTietHoaDonService.getChiTietByMaPhieu(maPhieu);
            if (dichVus != null && !dichVus.isEmpty()) {
                tongTienDichVu = dichVus.stream()
                        .mapToDouble(ChiTietHoaDonDTO::getThanhTien)
                        .sum();
            }
        } catch (Exception e) {
            System.err.println("[TraPhongService] Lỗi tính tiền DV: " + e.getMessage());
        }

        // Tính VAT
        double thanhTienTruocVAT = tongTienPhong + tongTienDichVu;
        double tienVAT = thanhTienTruocVAT * vatPercent;

        // Tính tổng sau VAT
        double tongTienSauVAT = thanhTienTruocVAT + tienVAT;

        // Tính tổng sau chiết khấu
        double tongTienThanhToan = tongTienSauVAT - chietKhau;

        // Tạo HoaDonDTO
        HoaDonDTO hoaDon = new HoaDonDTO();
        hoaDon.setMaHoaDon("HD" + System.currentTimeMillis());
        hoaDon.setMaKhachHang(phieu.getMaKhachHang());
        hoaDon.setMaPhongDat(phieu.getMaPhong());
        hoaDon.setNgayLap(LocalDate.now());
        hoaDon.setTongTienPhong(tongTienPhong);
        hoaDon.setTongTienDichVu(tongTienDichVu);
        hoaDon.setThueVAT(tienVAT);
        hoaDon.setChietKhau(chietKhau);
        hoaDon.setTongTien(tongTienThanhToan);
        hoaDon.setTrangThaiThanhToan("Chưa Thanh Toán");
        hoaDon.setGhiChu(String.format(
                "Trả phòng %s | Tiền phòng: %.0f | Dịch vụ: %.0f | VAT (%.0f%%): %.0f | Chiết khấu: %.0f",
                maPhieu, tongTienPhong, tongTienDichVu, vatPercent * 100, tienVAT, chietKhau
        ));

        return hoaDon;
    }

    @Override
    public void confirmCheckout(String maPhieu, HoaDonDTO hoaDon, String phuongThucThanhToan)
            throws IllegalArgumentException {
        // Validate input
        if (maPhieu == null || maPhieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được để trống");
        }
        if (hoaDon == null) {
            throw new IllegalArgumentException("Hóa đơn không được null");
        }
        if (hoaDon.getTongTien() <= 0) {
            throw new IllegalArgumentException("Tổng tiền phải > 0");
        }

        // Lấy phiếu
        PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
        if (phieu == null) {
            throw new IllegalArgumentException("Phiếu không tồn tại");
        }

        try {
            // 1. Lưu hóa đơn
            hoaDon.setMaNhanVien(phieu.getMaNhanVien());
            hoaDon.setMaKhachHang(phieu.getMaKhachHang());
            hoaDon.setMaPhongDat(phieu.getMaPhong());
            hoaDon.setTrangThaiThanhToan("Đã Thanh Toán");
            hoaDon.setGhiChu(hoaDon.getGhiChu() + " | Phương thức: " + phuongThucThanhToan);

            hoaDonService.addHoaDon(hoaDon);

            // 2. Update phiếu → DA_TRA_PHONG
            phieu.setTrangThai("DA_TRA_PHONG");
            phieu.setTongTien(hoaDon.getTongTien());
            phieuDatPhongService.updatePhieuDatPhong(phieu);

            // 3. Update phòng → Trống
            PhongDTO phong = phongService.getPhongById(phieu.getMaPhong());
            if (phong != null) {
                phong.setTinhTrang("Trống");
                phongService.updatePhong(phong);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi xác nhận trả phòng: " + e.getMessage(), e);
        }
    }
}

