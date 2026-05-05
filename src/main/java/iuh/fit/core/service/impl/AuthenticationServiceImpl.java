package iuh.fit.core.service.impl;

import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.entity.TaiKhoan;
import iuh.fit.core.repository.ITaiKhoanRepository;
import iuh.fit.core.service.IAuthenticationService;

/**
 * Class: AuthenticationServiceImpl (Service Implementation)
 * 
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Implement logic nghiệp vụ xác thực
 * 
 * PLAIN TEXT PASSWORD:
 * - Mật khẩu được lưu dưới dạng plain text trong database (VD: '123')
 * - Không dùng hash (MD5, BCrypt, v.v.)
 * - So sánh trực tiếp bằng .equals()
 * 
 * PRODUCTION NOTE: Trong production nên hash password bằng BCrypt/Argon2!
 */
public class AuthenticationServiceImpl implements IAuthenticationService {
    
    private final ITaiKhoanRepository taiKhoanRepository;
    
    /**
     * Constructor: Dependency Injection
     * Repository được inject từ ngoài để tăng testability
     * 
     * @param taiKhoanRepository Implementation của ITaiKhoanRepository
     */
    public AuthenticationServiceImpl(ITaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }
    
    @Override
    public TaiKhoanDTO login(String tenDangNhap, String matKhau) throws IllegalArgumentException {
        // Business Logic 1: Validate input
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Tài khoản không được để trống");
        }
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        
        // Business Logic 2: Tìm tài khoản từ repository
        var taiKhoanOptional = taiKhoanRepository.findByTaiKhoan(tenDangNhap.trim());
        
        if (taiKhoanOptional.isEmpty()) {
            return null; // Tài khoản không tồn tại
        }
        
        TaiKhoan foundAccount = taiKhoanOptional.get();
        
        // Business Logic 3: So sánh mật khẩu PLAIN TEXT (không hash)
        // ✅ Dùng .equals() để so sánh trực tiếp
        if (!foundAccount.getMatKhau().equals(matKhau)) {
            return null; // Mật khẩu sai
        }
        
        // Check if account is active
        if (!foundAccount.isTrangThaiTK()) {
            throw new IllegalArgumentException("Tài khoản đã bị vô hiệu hóa");
        }
        
        // Business Logic 4: Chuyển Entity → DTO để trả về
        return convertToDTO(foundAccount);
    }
    
    @Override
    public boolean checkAccountExists(String tenDangNhap) {
        return taiKhoanRepository.existsByTaiKhoan(tenDangNhap);
    }
    
    @Override
    public TaiKhoanDTO register(TaiKhoanDTO taiKhoanDTO) throws IllegalArgumentException {
        // Validate input
        if (taiKhoanDTO.getTaiKhoan() == null || taiKhoanDTO.getTaiKhoan().trim().isEmpty()) {
            throw new IllegalArgumentException("Tài khoản không được để trống");
        }
        if (taiKhoanDTO.getMatKhau() == null || taiKhoanDTO.getMatKhau().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        
        // Kiểm tra tài khoản đã tồn tại
        if (checkAccountExists(taiKhoanDTO.getTaiKhoan())) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại");
        }
        
        // Tạo entity mới từ DTO và lưu vào DB
        TaiKhoan newAccount = new TaiKhoan();
        newAccount.setTenDangNhap(taiKhoanDTO.getTaiKhoan());
        // ✅ Lưu password dưới dạng PLAIN TEXT (không hash)
        newAccount.setMatKhau(taiKhoanDTO.getMatKhau());
        newAccount.setTrangThaiTK(true); // Default: active
        
        TaiKhoan savedAccount = taiKhoanRepository.save(newAccount);
        
        return convertToDTO(savedAccount);
    }
    
    @Override
    public boolean changePassword(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        var accountOptional = taiKhoanRepository.findByTaiKhoan(tenDangNhap);
        
        if (accountOptional.isEmpty()) {
            return false;
        }
        
        TaiKhoan account = accountOptional.get();
        
        // Kiểm tra mật khẩu cũ (PLAIN TEXT comparison)
        if (!account.getMatKhau().equals(matKhauCu)) {
            return false;
        }
        
        // Cập nhật mật khẩu mới (PLAIN TEXT)
        account.setMatKhau(matKhauMoi);
        taiKhoanRepository.update(account);
        
        return true;
    }
    
    /**
     * Helper: Chuyển Entity TaiKhoan → DTO
     * Tách biệt Entity khỏi tầng presentation
     * 
     * ⚠️ SECURITY NOTE: Không nên trả password về client trong production!
     */
    private TaiKhoanDTO convertToDTO(TaiKhoan entity) {
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setTaiKhoan(entity.getTenDangNhap()); // Map tenDangNhap → taiKhoan in DTO
        // ⚠️ WARNING: Không nên include password trong DTO response
        // dto.setMatKhau(entity.getMatKhau()); // COMMENTED OUT - Security best practice
        
        if (entity.getNhanVien() != null) {
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
            dto.setHoTenNhanVien(entity.getNhanVien().getHoTen());
        }
        return dto;
    }
}

