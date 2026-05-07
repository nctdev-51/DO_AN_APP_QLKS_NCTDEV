package iuh.fit.core.service.impl;

import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.entity.HoaDon;
import iuh.fit.core.entity.ChiTietPhieuDatPhong;
import iuh.fit.core.entity.PhieuDatPhong;
import iuh.fit.core.repository.*;
import iuh.fit.core.service.IHoaDonService;
import iuh.fit.infrastructure.mapper.HoaDonMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HoaDonServiceImpl implements IHoaDonService {

    private final IHoaDonRepository hoaDonRepository;
    private final IChiTietHoaDonRepository chiTietHDRepository;
    private final IPhieuDatPhongRepository phieuDatPhongRepository;
    private final IPhongRepository phongRepository;
    private final IDichVuRepository dichVuRepository;
    private final IChiTietPhieuDatPhongRepository chiTietPhieuRepository;
    private final IKhachHangRepository khachHangRepository;
    private final INhanVienRepository nhanVienRepository;
    private final IKhuyenMaiRepository khuyenMaiRepository;

    // Sửa lại Constructor để nhận đủ 9 tham số
    public HoaDonServiceImpl(IHoaDonRepository hoaDonRepository,
                             IChiTietHoaDonRepository chiTietHDRepository,
                             IPhieuDatPhongRepository phieuDatPhongRepository,
                             IPhongRepository phongRepository,
                             IDichVuRepository dichVuRepository,
                             IChiTietPhieuDatPhongRepository chiTietPhieuRepository,
                             IKhachHangRepository khachHangRepository,
                             INhanVienRepository nhanVienRepository,
                             IKhuyenMaiRepository khuyenMaiRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHDRepository = chiTietHDRepository;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
        this.phongRepository = phongRepository;
        this.dichVuRepository = dichVuRepository;
        this.chiTietPhieuRepository = chiTietPhieuRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khuyenMaiRepository = khuyenMaiRepository;
    }

    @Override
    public List<HoaDonDTO> getHoaDonByDateRange(LocalDate start, LocalDate end) {
        return hoaDonRepository.findByNgayLapBetween(start, end).stream()
                .map(HoaDonMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalServiceRevenueByDateRange(LocalDate start, LocalDate end) {
        List<HoaDon> hoaDons = hoaDonRepository.findByNgayLapBetween(start, end);
        return hoaDons.stream()
                .mapToDouble(HoaDon::getTongTienDichVu)
                .sum();
    }

    @Override
    public List<HoaDonDTO> getHoaDonByPhieuDat(String maPhieu) {
        var phieuOpt = phieuDatPhongRepository.findById(maPhieu);
        if (phieuOpt.isEmpty()) {
            return List.of();
        }
        // Có thể bổ sung query lấy Hóa Đơn bằng maPhieu ở Repository
        return List.of();
    }

    // ✅ IMPLEMENTATION: Logic tính hóa đơn chi tiết (Phòng + Dịch vụ + VAT + Chiết khấu)
    @Override
    public HoaDonDTO calculateInvoiceAtCheckout(String maPhieu, double thueVAT, double chietKhau) {
        var phieuOpt = phieuDatPhongRepository.findById(maPhieu);
        if (phieuOpt.isEmpty()) {
            throw new IllegalArgumentException("Phiếu đặt phòng không tồn tại: " + maPhieu);
        }

        PhieuDatPhong phieu = phieuOpt.get();
        HoaDonDTO hoaDonDTO = new HoaDonDTO();

        // Khởi tạo mã Hóa Đơn tự động
        String maHoaDon = "HD" + System.currentTimeMillis();
        hoaDonDTO.setMaHoaDon(maHoaDon);
        hoaDonDTO.setMaPhieu(maPhieu);
        hoaDonDTO.setNgayLap(LocalDate.now());
        hoaDonDTO.setThueVAT(thueVAT);
        hoaDonDTO.setChietKhau(chietKhau);
        hoaDonDTO.setTrangThaiThanhToan("CHUA_THANH_TOAN");

        if (phieu.getKhachHang() != null) {
            hoaDonDTO.setMaKhachHang(phieu.getKhachHang().getMaKhachHang());
            hoaDonDTO.setTenKhachHang(phieu.getKhachHang().getHoTen());
        }
        if (phieu.getNhanVien() != null) {
            hoaDonDTO.setMaNhanVien(phieu.getNhanVien().getMaNhanVien());
            hoaDonDTO.setHoTenNhanVien(phieu.getNhanVien().getHoTen());
        }
        if (phieu.getPhong() != null) {
            hoaDonDTO.setMaPhongDat(phieu.getPhong().getMaPhong());
            hoaDonDTO.setTenPhong(phieu.getPhong().getTenPhong());
        }

        // 1. Tính tiền phòng
        double tongTienPhong = 0;
        if (phieu.getPhong() != null && phieu.getNgayNhan() != null && phieu.getNgayTra() != null) {
            long soNgay = java.time.temporal.ChronoUnit.DAYS.between(phieu.getNgayNhan(), phieu.getNgayTra());
            soNgay = Math.max(1, soNgay); // Tối thiểu 1 ngày
            tongTienPhong = phieu.getPhong().getGiaPhong() * soNgay;
        }
        hoaDonDTO.setTongTienPhong(tongTienPhong);

        // 2. Tính tiền dịch vụ chi tiết
        double tongTienDichVu = 0;
        List<ChiTietHoaDonDTO> dsChiTietDichVu = new ArrayList<>();

        for (ChiTietPhieuDatPhong chiTiet : phieu.getDsChiTietPhieuDatPhong()) {
            if (chiTiet.getDichVu() != null) {
                double giaDichVu = chiTiet.getDichVu().getGiaTien();
                int soLuong = chiTiet.getSoLuong();
                double thanhTienDichVu = giaDichVu * soLuong;
                tongTienDichVu += thanhTienDichVu;

                // Lưu lại log/danh sách chi tiết dịch vụ dùng (nếu DTO hỗ trợ)
                ChiTietHoaDonDTO ctHD = new ChiTietHoaDonDTO();
                ctHD.setMaDichVu(chiTiet.getDichVu().getMaDichVu());
                ctHD.setTenDichVu(chiTiet.getDichVu().getTenDichVu());
                ctHD.setSoLuong(soLuong);
                ctHD.setGiaTienTungDichVu(giaDichVu); // ✅ Đã fix
                ctHD.setThanhTien(thanhTienDichVu);
                dsChiTietDichVu.add(ctHD);
            }
        }
        hoaDonDTO.setTongTienDichVu(tongTienDichVu);

        // 3. Tính toán công thức: (Tiền Phòng + Tiền DV) + VAT - Chiết Khấu
        double thanhTienTruocVAT = tongTienPhong + tongTienDichVu;
        double tienVAT = thanhTienTruocVAT * thueVAT;

        // Chiết khấu tính trên tổng tiền đã bao gồm VAT
        double tongTienSauVAT = thanhTienTruocVAT + tienVAT;
        double tienGiam = tongTienSauVAT * (chietKhau / 100.0);
        double tongTienThanhToanCuoiCung = tongTienSauVAT - tienGiam;

        hoaDonDTO.setTongTien(tongTienThanhToanCuoiCung);
        hoaDonDTO.setGhiChu(String.format("Tiền phòng: %.2f | Dịch vụ: %.2f | VAT (%.0f%%): %.2f | Chiết khấu (%.0f%%): %.2f",
                tongTienPhong, tongTienDichVu, thueVAT * 100, tienVAT, chietKhau, tienGiam));

        return hoaDonDTO;
    }

    @Override
    public HoaDonDTO addHoaDon(HoaDonDTO dto) {
        if (dto.getMaHoaDon() == null || dto.getMaHoaDon().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống");
        }
        HoaDon entity = HoaDonMapper.dtoToEntity(dto);
        HoaDon saved = hoaDonRepository.save(entity);
        return HoaDonMapper.entityToDTO(saved);
    }
}