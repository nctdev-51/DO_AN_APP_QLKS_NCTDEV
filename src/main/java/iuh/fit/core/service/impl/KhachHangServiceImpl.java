package iuh.fit.core.service.impl;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.entity.KhachHang;
import iuh.fit.core.repository.IKhachHangRepository;
import iuh.fit.core.service.IKhachHangService;

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

    // 👉 HÀM ADD ĐÃ ĐƯỢC GỘP LẠI (Vừa Validate, vừa sinh mã xịn)
    @Override
    public KhachHangDTO addKhachHang(KhachHangDTO khachHangDTO) throws IllegalArgumentException {
        // 1. Validate input
        validateKhachHang(khachHangDTO);

        // 2. Kiểm tra số điện thoại không trùng lặp
        if (khachHangRepository.findBySoDienThoai(khachHangDTO.getSoDienThoai()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        // 3. Sinh mã khách hàng tự động theo thứ tự (KH016, KH017...)
        String maMoi = khachHangRepository.phatSinhMaKhachHangMoi();
        khachHangDTO.setMaKhachHang(maMoi);

        // 4. Chuyển DTO → Entity
        KhachHang entity = convertToEntity(khachHangDTO);

        // 5. Lưu vào database
        KhachHang saved = khachHangRepository.save(entity);

        // 6. Trả về đối tượng đã lưu (chứa mã ID thật) cho Controller dùng
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
        dto.setMaKhachHang(entity.getMaKhachHang().trim()); // Thêm trim() cho an toàn
        dto.setHoTen(entity.getHoTen());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setLoaiKhachHang(entity.getLoaiKhachHang());

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

        return entity;
    }
}