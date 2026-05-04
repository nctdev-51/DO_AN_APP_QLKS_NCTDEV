package iuh.fit.core.service;

import iuh.fit.core.dto.NhanVienDTO;
import iuh.fit.core.entity.NhanVien;
import iuh.fit.core.entity.LoaiNhanVien;
import iuh.fit.core.repository.INhanVienRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class: NhanVienServiceImpl (Service Implementation)
 * 
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Implement logic nghiệp vụ Quản lý Nhân Viên
 */
public class NhanVienServiceImpl implements INhanVienService {
    
    private final INhanVienRepository nhanVienRepository;
    
    public NhanVienServiceImpl(INhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }
    
    @Override
    public List<NhanVienDTO> getAllNhanVien() {
        return nhanVienRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public NhanVienDTO getNhanVienById(String maNhanVien) {
        return nhanVienRepository.findById(maNhanVien)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    @Override
    public NhanVienDTO getNhanVienBySoDienThoai(String soDienThoai) {
        return nhanVienRepository.findBySoDienThoai(soDienThoai)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    @Override
    public NhanVienDTO addNhanVien(NhanVienDTO nhanVienDTO) throws IllegalArgumentException {
        // Business Logic: Validate input
        validateNhanVien(nhanVienDTO);
        
        // Kiểm tra số điện thoại không trùng lặp
        if (nhanVienRepository.findBySoDienThoai(nhanVienDTO.getSoDienThoai()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
        
        // Tạo mã nhân viên tự động
        String maNhanVien = generateMaNhanVien();
        
        // Chuyển DTO → Entity
        NhanVien entity = convertToEntity(nhanVienDTO);
        entity.setMaNhanVien(maNhanVien);
        
        // Lưu vào repository
        NhanVien saved = nhanVienRepository.save(entity);
        
        return convertToDTO(saved);
    }
    
    @Override
    public NhanVienDTO updateNhanVien(NhanVienDTO nhanVienDTO) throws IllegalArgumentException {
        validateNhanVien(nhanVienDTO);
        
        // Kiểm tra nhân viên có tồn tại không
        if (!nhanVienRepository.findById(nhanVienDTO.getMaNhanVien()).isPresent()) {
            throw new IllegalArgumentException("Nhân viên không tồn tại");
        }
        
        NhanVien entity = convertToEntity(nhanVienDTO);
        NhanVien updated = nhanVienRepository.update(entity);
        
        return convertToDTO(updated);
    }
    
    @Override
    public boolean deleteNhanVien(String maNhanVien) {
        try {
            nhanVienRepository.deleteById(maNhanVien);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String generateMaNhanVien() {
        List<NhanVien> allNhanVien = nhanVienRepository.findAll();
        int nextId = allNhanVien.size() + 1;
        return String.format("NV%03d", nextId);
    }
    
    /**
     * Helper: Validate dữ liệu nhân viên
     */
    private void validateNhanVien(NhanVienDTO dto) throws IllegalArgumentException {
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }
        if (dto.getSoDienThoai() == null || dto.getSoDienThoai().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (!dto.getSoDienThoai().matches("\\d{10}")) {
            throw new IllegalArgumentException("Số điện thoại phải có 10 chữ số");
        }
        if (dto.getCccd() == null || dto.getCccd().trim().isEmpty()) {
            throw new IllegalArgumentException("CCCD không được để trống");
        }
    }
    
    /**
     * Helper: Chuyển Entity → DTO
     */
    private NhanVienDTO convertToDTO(NhanVien entity) {
        return new NhanVienDTO(
                entity.getMaNhanVien(),
                entity.getHoTen(),
                entity.getNgaySinh(),
                entity.isGioiTinh(),
                entity.getCccd(),
                entity.getSoDienThoai(),
                entity.isTrangThai(),
                entity.getLoaiNhanVien() != null ? entity.getLoaiNhanVien().name() : "",
                entity.getNgayVaoLam(),
                entity.getQueQuan()
        );
    }
    
    /**
     * Helper: Chuyển DTO → Entity
     */
    private NhanVien convertToEntity(NhanVienDTO dto) {
        NhanVien entity = new NhanVien();
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setGioiTinh(dto.isGioiTinh());
        entity.setCccd(dto.getCccd());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setTrangThai(dto.isTrangThai());
        if (dto.getLoaiNhanVien() != null && !dto.getLoaiNhanVien().isEmpty()) {
            entity.setLoaiNhanVien(LoaiNhanVien.valueOf(dto.getLoaiNhanVien()));
        }
        entity.setNgayVaoLam(dto.getNgayVaoLam());
        entity.setQueQuan(dto.getQueQuan());
        return entity;
    }
}

