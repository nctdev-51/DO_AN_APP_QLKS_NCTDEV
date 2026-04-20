package server.handler;

import core.entity.KhachHang;
import core.entity.LoaiKhachHang;
import core.entity.NhanVien;
import core.service.AuthService;
import core.service.KhachHangService;
import core.service.impl.AuthServiceImpl;
import core.service.impl.KhachHangServiceImpl;
import shared.model.LoginRequestData;
import shared.model.RequestObject;
import shared.model.ResponseObject;

import java.time.LocalDate;
import java.util.Map;

public class ActionDispatcher {
    private final AuthService authService = new AuthServiceImpl();
    private final KhachHangService khachHangService = new KhachHangServiceImpl();

    public ResponseObject dispatch(RequestObject request) {
        System.out.println("\n--- [SERVER ROUTER] Đã nhận Request ---");
        System.out.println(">> ACTION: " + request.getAction());

        if (request == null || request.getAction() == null) {
            System.out.println("❌ Lỗi: Yêu cầu không hợp lệ hoặc rỗng.");
            return ResponseObject.fail("Yeu cau khong hop le hoac rỗng.");
        }

        try {
            return switch (request.getAction()) {
                case "AUTH_LOGIN" -> handleAuthLogin(request.getPayload());
                case "KHACH_HANG_GET_ALL" -> {
                    System.out.println(">> Đang gọi KhachHangService.getAll()...");
                    yield ResponseObject.ok("OK", khachHangService.getAll());
                }
                case "KHACH_HANG_SEARCH_BY_MA" -> handleSearchByMa(request.getPayload());
                case "KHACH_HANG_CREATE" -> handleCreateKhachHang(request.getPayload());
                case "KHACH_HANG_UPDATE" -> handleUpdateKhachHang(request.getPayload());
                case "KHACH_HANG_DELETE" -> handleDeleteKhachHang(request.getPayload());
                default -> {
                    System.out.println("❌ Lỗi: Action chưa được hỗ trợ.");
                    yield ResponseObject.fail("Action khong duoc ho tro: " + request.getAction());
                }
            };
        } catch (Exception ex) {
            // ĐÂY LÀ ĐOẠN QUAN TRỌNG NHẤT ĐỂ TÌM LỖI
            System.err.println("\n🔥 CÓ LỖI NGHIÊM TRỌNG TRONG QUÁ TRÌNH XỬ LÝ (JPA/Service) 🔥");
            ex.printStackTrace(); // In toàn bộ stack trace ra console

            return ResponseObject.fail("Loi xu ly server: " + ex.getMessage());
        }
    }

    private ResponseObject handleAuthLogin(Object payload) {
        System.out.println(">> Đang xử lý đăng nhập...");

        // 1. Ép kiểu an toàn
        if (!(payload instanceof LoginRequestData)) {
            System.out.println("❌ Lỗi: Payload không phải là LoginRequestData.");
            return ResponseObject.fail("Du lieu gui len khong dung dinh dang dang nhap.");
        }

        LoginRequestData login = (LoginRequestData) payload;
        System.out.println("   Username nhận được: " + login.getUsername()); // Log xem username có bị rỗng không

        // 2. Kiểm tra dữ liệu rỗng
        if (login.getUsername() == null || login.getUsername().trim().isEmpty() ||
                login.getPassword() == null || login.getPassword().trim().isEmpty()) {
            return ResponseObject.fail("Vui long nhap day du tai khoan va mat khau.");
        }

        // 3. Gọi Service (Nơi giao tiếp với DB)
        NhanVien nhanVien = authService.login(login);

        if (nhanVien == null) {
            System.out.println("❌ Lỗi: Sai tài khoản hoặc mật khẩu (hoặc tài khoản bị khóa).");
            return ResponseObject.fail("Tai khoan hoac mat khau khong dung.");
        }

        System.out.println("✅ Đăng nhập thành công: " + nhanVien.getHoTen());
        return ResponseObject.ok("Dang nhap thanh cong", nhanVien);
    }

    private ResponseObject handleSearchByMa(Object payload) {
        System.out.println(">> Đang xử lý tìm khách hàng theo mã...");
        Map<?, ?> map = (Map<?, ?>) payload;
        String maKhachHang = String.valueOf(map.get("maKhachHang"));
        return ResponseObject.ok("OK", khachHangService.findById(maKhachHang));
    }

    private ResponseObject handleCreateKhachHang(Object payload) {
        System.out.println(">> Đang xử lý tạo khách hàng mới...");
        KhachHang khachHang = toKhachHang(payload);
        if (khachHang.getMaKhachHang() == null || khachHang.getMaKhachHang().isBlank()) {
            khachHang.setMaKhachHang("KH" + System.currentTimeMillis() % 10000);
        }
        return ResponseObject.ok("Them khach hang thanh cong", khachHangService.create(khachHang));
    }

    private ResponseObject handleUpdateKhachHang(Object payload) {
        System.out.println(">> Đang xử lý cập nhật khách hàng...");
        KhachHang khachHang = toKhachHang(payload);
        return ResponseObject.ok("Cap nhat khach hang thanh cong", khachHangService.update(khachHang));
    }

    private ResponseObject handleDeleteKhachHang(Object payload) {
        System.out.println(">> Đang xử lý xóa khách hàng...");
        Map<?, ?> map = (Map<?, ?>) payload;
        String maKhachHang = String.valueOf(map.get("maKhachHang"));
        boolean deleted = khachHangService.deleteById(maKhachHang);
        if (!deleted) {
            return ResponseObject.fail("Khong tim thay khach hang de xoa.");
        }
        return ResponseObject.ok("Xoa khach hang thanh cong", true);
    }

    @SuppressWarnings("unchecked")
    private KhachHang toKhachHang(Object payload) {
        if (payload instanceof KhachHang khachHang) {
            return khachHang;
        }
        Map<String, Object> map = (Map<String, Object>) payload;
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(asString(map.get("maKhachHang")));
        khachHang.setHoTen(asString(map.get("hoTen")));
        khachHang.setSoDienThoai(asString(map.get("soDienThoai")));
        khachHang.setNgaySinh(LocalDate.parse(asString(map.get("ngaySinh"))));
        khachHang.setLoaiKhachHang(LoaiKhachHang.valueOf(asString(map.get("loaiKhachHang"))));
        return khachHang;
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}