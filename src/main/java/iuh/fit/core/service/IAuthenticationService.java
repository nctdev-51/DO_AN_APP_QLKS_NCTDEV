package iuh.fit.core.service;

import iuh.fit.core.dto.TaiKhoanDTO;

/**
 * Interface: IAuthenticationService (Service Port)
 * 
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Định nghĩa hợp đồng cho nghiệp vụ xác thực/đăng nhập
 * 
 * Đây là business logic interface:
 * - Không chứa JPA, database queries
 * - Chỉ định nghĩa những thao tác business-level
 * - Implementation sẽ gọi repository để truy vấn dữ liệu
 */
public interface IAuthenticationService {
    
    /**
     * Đăng nhập: Xác thực tài khoản và mật khẩu
     * @param taiKhoan Username
     * @param matKhau Password
     * @return TaiKhoanDTO nếu đăng nhập thành công, null nếu thất bại
     * @throws IllegalArgumentException nếu username/password trống
     */
    TaiKhoanDTO login(String taiKhoan, String matKhau) throws IllegalArgumentException;
    
    /**
     * Kiểm tra xem tài khoản có tồn tại hay không
     * @param taiKhoan Username
     * @return true nếu tồn tại
     */
    boolean checkAccountExists(String taiKhoan);
    
    /**
     * Đăng ký tài khoản mới (nếu được phép)
     * @param taiKhoanDTO DTO chứa thông tin account
     * @return TaiKhoanDTO vừa được tạo, null nếu thất bại
     * @throws IllegalArgumentException nếu tài khoản đã tồn tại
     */
    TaiKhoanDTO register(TaiKhoanDTO taiKhoanDTO) throws IllegalArgumentException;
    
    /**
     * Đổi mật khẩu
     * @param taiKhoan Username
     * @param matKhauCu Mật khẩu cũ
     * @param matKhauMoi Mật khẩu mới
     * @return true nếu đổi thành công
     */
    boolean changePassword(String taiKhoan, String matKhauCu, String matKhauMoi);
}

