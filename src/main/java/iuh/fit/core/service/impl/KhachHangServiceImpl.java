package iuh.fit.core.service.impl;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.entity.KhachHang;
import iuh.fit.core.repository.IKhachHangRepository;
import iuh.fit.core.service.IKhachHangService;
// Chú ý: Đã xóa dòng import LoaiKhachHang vì không còn dùng Enum nữa

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class: KhachHangServiceImpl (Service Implementation)
 *
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Implement logic nghiệp vụ Quản lý Khách Hàng
 */
public class KhachHangServiceImpl implements IKhachHangService {

    private final IKhachHangRepository khachHangRepository;

    public KhachHangServiceImpl(IKhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    public List<KhachHangDTO> getAllKhachHang() {
        return khachHangRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public KhachHangDTO getKhachHangById(String maKhachHang) {
        return khachHangRepository.findById(maKhachHang)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public KhachHangDTO getKhachHangBySoDienThoai(String soDienThoai) {
        return khachHangRepository.findBySoDienThoai(soDienThoai)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public KhachHangDTO addKhachHang(KhachHangDTO khachHangDTO) throws IllegalArgumentException {
        // Business Logic: Validate input
        validateKhachHang(khachHangDTO);

        // Kiểm tra số điện thoại không trùng lặp
        if (khachHangRepository.findBySoDienThoai(khachHangDTO.getSoDienThoai()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        // Tạo mã khách hàng tự động (ví dụ: KH001, KH002, ...)
        String maKhachHang = generateMaKhachHang();

        // Chuyển DTO → Entity
        KhachHang entity = convertToEntity(khachHangDTO);
        entity.setMaKhachHang(maKhachHang);

        // Lưu vào repository
        KhachHang saved = khachHangRepository.save(entity);

        return convertToDTO(saved);
    }

    @Override
    public KhachHangDTO updateKhachHang(KhachHangDTO khachHangDTO) throws IllegalArgumentException {
        validateKhachHang(khachHangDTO);

        // Kiểm tra khách hàng có tồn tại không
        if (!khachHangRepository.findById(khachHangDTO.getMaKhachHang()).isPresent()) {
            throw new IllegalArgumentException("Khách hàng không tồn tại");
        }

        KhachHang entity = convertToEntity(khachHangDTO);
        KhachHang updated = khachHangRepository.update(entity);

        return convertToDTO(updated);
    }

    @Override
    public boolean deleteKhachHang(String maKhachHang) {
        try {
            khachHangRepository.deleteById(maKhachHang);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Helper: Validate dữ liệu khách hàng
     */
    private void validateKhachHang(KhachHangDTO dto) throws IllegalArgumentException {
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }
        if (dto.getSoDienThoai() == null || dto.getSoDienThoai().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (!dto.getSoDienThoai().matches("\\d{10}")) {
            throw new IllegalArgumentException("Số điện thoại phải có 10 chữ số");
        }
    }

    /**
     * Helper: Chuyển Entity → DTO
     */
    private KhachHangDTO convertToDTO(KhachHang entity) {
        if (entity == null) return null;

        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKhachHang(entity.getMaKhachHang());
        dto.setHoTen(entity.getHoTen());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setLoaiKhachHang(entity.getLoaiKhachHang());

        // (Tùy chọn) Nếu trong Entity KhachHang của bạn cũng đã thêm trường này
        // dto.setDoiTuongKhach(entity.getDoiTuongKhach());

        return dto;
    }

    /**
     * Helper: Chuyển DTO → Entity
     */
    private KhachHang convertToEntity(KhachHangDTO dto) {
        if (dto == null) return null;

        KhachHang entity = new KhachHang();
        entity.setMaKhachHang(dto.getMaKhachHang());
        entity.setHoTen(dto.getHoTen());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setLoaiKhachHang(dto.getLoaiKhachHang());

        // (Tùy chọn) Map ngược lại nếu entity có
        // entity.setDoiTuongKhach(dto.getDoiTuongKhach());

        return entity;
    }

    private String generateMaKhachHang() {
        // Sử dụng timestamp đơn giản để tạo mã duy nhất
        return "KH" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );
    }
}