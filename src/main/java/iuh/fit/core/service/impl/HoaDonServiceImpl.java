package iuh.fit.core.service.impl;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.entity.HoaDon;
import iuh.fit.core.entity.PhieuDatPhong;
import iuh.fit.core.entity.Phong;
import iuh.fit.core.repository.IHoaDonRepository;
import iuh.fit.core.repository.IChiTietHoaDonRepository;
import iuh.fit.core.repository.IPhieuDatPhongRepository;
import iuh.fit.core.repository.IPhongRepository;
import iuh.fit.core.service.IHoaDonService;
import iuh.fit.infrastructure.mapper.HoaDonMapper;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HoaDonServiceImpl implements IHoaDonService {

    private final IHoaDonRepository hoaDonRepository;
    private final IChiTietHoaDonRepository chiTietHoaDonRepository;
    private final IPhieuDatPhongRepository phieuDatPhongRepository;
    private final IPhongRepository phongRepository;

    public HoaDonServiceImpl(IHoaDonRepository hoaDonRepository, IChiTietHoaDonRepository chiTietHoaDonRepository,
                            IPhieuDatPhongRepository phieuDatPhongRepository, IPhongRepository phongRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
        this.phongRepository = phongRepository;
    }

    @Override
    public List<HoaDonDTO> getAllHoaDon() {
        return hoaDonRepository.findAll().stream()
                .map(HoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HoaDonDTO getHoaDonById(String maHoaDon) {
        return hoaDonRepository.findById(maHoaDon)
                .map(HoaDonMapper::toDTO)
                .orElse(null);
    }

    @Override
    public HoaDonDTO addHoaDon(HoaDonDTO hoaDonDTO) throws IllegalArgumentException {
        if (hoaDonDTO.getMaHoaDon() == null || hoaDonDTO.getMaHoaDon().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được trống");
        }
        HoaDon hoaDon = HoaDonMapper.toEntity(hoaDonDTO);
        HoaDon saved = hoaDonRepository.save(hoaDon);
        return HoaDonMapper.toDTO(saved);
    }

    @Override
    public HoaDonDTO updateHoaDon(HoaDonDTO hoaDonDTO) throws IllegalArgumentException {
        if (hoaDonDTO.getMaHoaDon() == null || hoaDonDTO.getMaHoaDon().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được trống");
        }
        HoaDon hoaDon = HoaDonMapper.toEntity(hoaDonDTO);
        HoaDon updated = hoaDonRepository.update(hoaDon);
        return HoaDonMapper.toDTO(updated);
    }

    @Override
    public boolean deleteHoaDon(String maHoaDon) {
        try {
            hoaDonRepository.deleteById(maHoaDon);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<HoaDonDTO> getHoaDonByPhieuDat(String maPhieuDat) {
        return hoaDonRepository.findByPhieuDatPhong(maPhieuDat).stream()
                .map(HoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HoaDonDTO calculateInvoiceAtCheckout(String maPhieuDat, double thueVAT, double chietKhau) throws IllegalArgumentException {
        Optional<PhieuDatPhong> phieuOpt = phieuDatPhongRepository.findById(maPhieuDat);
        if (!phieuOpt.isPresent()) {
            throw new IllegalArgumentException("Phiếu đặt phòng không tồn tại: " + maPhieuDat);
        }

        PhieuDatPhong phieu = phieuOpt.get();

        Optional<Phong> phongOpt = phongRepository.findById(phieu.getMaPhong());
        if (!phongOpt.isPresent()) {
            throw new IllegalArgumentException("Phòng không tồn tại: " + phieu.getMaPhong());
        }

        Phong phong = phongOpt.get();

        // Tính số ngày ở
        LocalDate ngayNhan = phieu.getNgayNhan() != null ? phieu.getNgayNhan() : LocalDate.now();
        LocalDate ngayTra = phieu.getNgayTra() != null ? phieu.getNgayTra() : LocalDate.now();
        long soNgayO = ChronoUnit.DAYS.between(ngayNhan, ngayTra);
        if (soNgayO <= 0) soNgayO = 1;

        // Tính tổng tiền phòng
        double tongTienPhong = soNgayO * phong.getGiaPhong();

        // Tính tổng tiền dịch vụ từ Chi tiết hóa đơn
        double tongTienDichVu = 0.0;

        // Tạo mã hóa đơn
        String maHoaDon = generateInvoiceId();

        // Tính VAT
        double thueVATTinh = (tongTienPhong + tongTienDichVu) * (thueVAT / 100.0);

        // Tính tổng tiền sau chiết khấu
        double tongTien = tongTienPhong + tongTienDichVu + thueVATTinh - chietKhau;

        // Tạo hóa đơn
        HoaDonDTO hoaDonDTO = new HoaDonDTO();
        hoaDonDTO.setMaHoaDon(maHoaDon);
        hoaDonDTO.setMaPhongDat(maPhieuDat);
        hoaDonDTO.setNgayLap(LocalDate.now());
        hoaDonDTO.setTongTienPhong(tongTienPhong);
        hoaDonDTO.setTongTienDichVu(tongTienDichVu);
        hoaDonDTO.setThueVAT(thueVATTinh);
        hoaDonDTO.setChietKhau(chietKhau);
        hoaDonDTO.setTongTien(tongTien);
        hoaDonDTO.setTrangThaiThanhToan("Chưa thanh toán");

        return hoaDonDTO;
    }

    @Override
    public List<HoaDonDTO> getHoaDonByDateRange(LocalDate startDate, LocalDate endDate) {
        return hoaDonRepository.findByDateRange(startDate, endDate).stream()
                .map(HoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        return hoaDonRepository.getTotalRevenueByDateRange(startDate, endDate);
    }

    @Override
    public double getTotalServiceRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        List<HoaDon> hoaDons = hoaDonRepository.findByDateRange(startDate, endDate);
        return hoaDons.stream()
                .flatMap(h -> h.getChiTietHoaDons().stream())
                .mapToDouble(c -> c.getThanhTien())
                .sum();
    }

    private String generateInvoiceId() {
        String dateStr = LocalDate.now().toString().replace("-", "");
        String timestamp = String.valueOf(System.nanoTime() % 1000);
        return "INV-" + dateStr + "-" + String.format("%03d", Integer.parseInt(timestamp.substring(Math.max(0, timestamp.length() - 3))));
    }
}

