package iuh.fit.core.service.impl;


import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.entity.*;
import iuh.fit.core.repository.*;
import iuh.fit.core.service.IChiTietHoaDonService;
import iuh.fit.infrastructure.mapper.ChiTietHoaDonMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChiTietHoaDonServiceImpl implements IChiTietHoaDonService {

    private final IChiTietHoaDonRepository chiTietHoaDonRepository;
    private final IChiTietPhieuDatPhongRepository chiTietPhieuDatPhongRepository;
    private final IDichVuRepository dichVuRepository;  // 👈 thêm
    private final IPhieuDatPhongRepository phieuDatPhongRepository;

    public ChiTietHoaDonServiceImpl(IChiTietHoaDonRepository chiTietHoaDonRepository,
                                    IChiTietPhieuDatPhongRepository chiTietPhieuDatPhongRepository,
                                    IDichVuRepository dichVuRepository,
                                    IPhieuDatPhongRepository phieuDatPhongRepository) {
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
        this.chiTietPhieuDatPhongRepository = chiTietPhieuDatPhongRepository;
        this.dichVuRepository = dichVuRepository;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
    }

    @Override
    public List<ChiTietHoaDonDTO> getAllChiTietHoaDon() {
        return chiTietHoaDonRepository.findAll().stream()
                .map(ChiTietHoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChiTietHoaDonDTO getChiTietHoaDonById(String maHoaDon, String maDichVu) {
        if (maHoaDon == null || maDichVu == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        ChiTietHoaDonId id = new ChiTietHoaDonId(maHoaDon, maDichVu);
        return chiTietHoaDonRepository.findById(id)
                .map(ChiTietHoaDonMapper::toDTO)
                .orElse(null);
    }

    @Override
    public ChiTietHoaDonDTO addChiTietHoaDon(ChiTietHoaDonDTO dto) {
        if (dto.getMaHoaDon() == null || dto.getMaDichVu() == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        if (dto.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        // Dùng Mapper để chuyển đổi, mapper đã tự động tạo Khóa ghép và set Số lượng
        ChiTietHoaDon entity = ChiTietHoaDonMapper.toEntity(dto);

        ChiTietHoaDon saved = chiTietHoaDonRepository.save(entity);
        return ChiTietHoaDonMapper.toDTO(saved);
    }

    @Override
    public ChiTietHoaDonDTO updateChiTietHoaDon(ChiTietHoaDonDTO dto) {
        if (dto.getMaHoaDon() == null || dto.getMaDichVu() == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        if (dto.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        // Mapper đã tự động map ID ghép và Số lượng, không cần gọi lại các hàm set lỗi
        ChiTietHoaDon entity = ChiTietHoaDonMapper.toEntity(dto);

        ChiTietHoaDon updated = chiTietHoaDonRepository.update(entity);
        return ChiTietHoaDonMapper.toDTO(updated);
    }

    @Override
    public boolean deleteChiTietHoaDon(String maHoaDon, String maDichVu) {
        try {
            ChiTietHoaDonId id = new ChiTietHoaDonId(maHoaDon, maDichVu);
            chiTietHoaDonRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<ChiTietHoaDonDTO> getChiTietHoaDonByHoaDon(String maHoaDon) {
        return chiTietHoaDonRepository.findByHoaDon(maHoaDon).stream()
                .map(ChiTietHoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChiTietHoaDonDTO> getChiTietByMaPhieu(String maPhieu) {
        // Lấy tất cả ChiTietPhieuDatPhong của phiếu
        List<ChiTietPhieuDatPhong> list = chiTietPhieuDatPhongRepository.findByMaPhieu(maPhieu);

        return list.stream().map(ct -> {
            ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
            // Lấy thông tin từ entity (vì có @ManyToOne fetch EAGER)
            dto.setMaPhieu(ct.getMaPhieu());
            dto.setMaDichVu(ct.getMaDichVu());
            dto.setSoLuong(ct.getSoLuong());

            // DichVu đã được load sẵn (EAGER)
            if (ct.getDichVu() != null) {
                dto.setTenDichVu(ct.getDichVu().getTenDichVu());
                dto.setGiaTienTungDichVu(ct.getDichVu().getGiaTien());
                dto.setThanhTien(ct.getDichVu().getGiaTien() * ct.getSoLuong());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void addOrUpdateChiTiet(ChiTietHoaDonDTO dto) {
        if (dto == null || dto.getMaDichVu() == null) return;

        // Nếu đã có mã hóa đơn -> lưu thẳng vào ChiTietHoaDon (giữ nguyên code cũ)
        if (dto.getMaHoaDon() != null && !dto.getMaHoaDon().isEmpty()) {
            ChiTietHoaDonId id = new ChiTietHoaDonId(dto.getMaHoaDon(), dto.getMaDichVu());
            ChiTietHoaDon entity = ChiTietHoaDonMapper.toEntity(dto);
            if (chiTietHoaDonRepository.findById(id).isPresent()) {
                chiTietHoaDonRepository.update(entity);
            } else {
                chiTietHoaDonRepository.save(entity);
            }
            return;
        }

        // Chưa có hóa đơn, chỉ có mã phiếu -> lưu vào ChiTietPhieuDatPhong
        if (dto.getMaPhieu() != null && !dto.getMaPhieu().isEmpty()) {
            // Lấy phiếu (chỉ để kiểm tra tồn tại, không cần gán vào entity)
            phieuDatPhongRepository.findById(dto.getMaPhieu())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu: " + dto.getMaPhieu()));

            // Lấy dịch vụ (chỉ để kiểm tra tồn tại)
            dichVuRepository.findById(dto.getMaDichVu())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ: " + dto.getMaDichVu()));

            // Tìm xem đã có chi tiết nào cho phiếu + dịch vụ này chưa
            List<ChiTietPhieuDatPhong> ds = chiTietPhieuDatPhongRepository.findByMaPhieu(dto.getMaPhieu());
            Optional<ChiTietPhieuDatPhong> existing = ds.stream()
                    .filter(ct -> ct.getMaDichVu().equals(dto.getMaDichVu()))
                    .findFirst();

            if (existing.isPresent()) {
                ChiTietPhieuDatPhong ct = existing.get();
                ct.setSoLuong(ct.getSoLuong() + dto.getSoLuong());
                chiTietPhieuDatPhongRepository.save(ct); // update
            } else {
                ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong();
                ct.setMaPhieu(dto.getMaPhieu());   // tự động tạo id nếu cần
                ct.setMaDichVu(dto.getMaDichVu());
                ct.setSoLuong(dto.getSoLuong());
                ct.setGhiChu("");
                chiTietPhieuDatPhongRepository.save(ct);// persist mới
            }

        }
    }

    @Override
    public void deleteByHoaDon(String maHoaDon) {

        chiTietHoaDonRepository.deleteByHoaDon(maHoaDon);
    }

}