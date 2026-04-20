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
        if (request == null || request.getAction() == null) {
            return ResponseObject.fail("Yeu cau khong hop le.");
        }

        try {
            return switch (request.getAction()) {
                case "AUTH_LOGIN" -> handleAuthLogin(request.getPayload());
                case "KHACH_HANG_GET_ALL" -> ResponseObject.ok("OK", khachHangService.getAll());
                case "KHACH_HANG_SEARCH_BY_MA" -> handleSearchByMa(request.getPayload());
                case "KHACH_HANG_CREATE" -> handleCreateKhachHang(request.getPayload());
                case "KHACH_HANG_UPDATE" -> handleUpdateKhachHang(request.getPayload());
                case "KHACH_HANG_DELETE" -> handleDeleteKhachHang(request.getPayload());
                default -> ResponseObject.fail("Action khong duoc ho tro: " + request.getAction());
            };
        } catch (Exception ex) {
            return ResponseObject.fail("Loi xu ly server: " + ex.getMessage());
        }
    }

    private ResponseObject handleAuthLogin(Object payload) {
        LoginRequestData login = (LoginRequestData) payload;
        NhanVien nhanVien = authService.login(login);
        if (nhanVien == null) {
            return ResponseObject.fail("Tai khoan hoac mat khau khong dung.");
        }
        return ResponseObject.ok("Dang nhap thanh cong", nhanVien);
    }

    private ResponseObject handleSearchByMa(Object payload) {
        Map<?, ?> map = (Map<?, ?>) payload;
        String maKhachHang = String.valueOf(map.get("maKhachHang"));
        return ResponseObject.ok("OK", khachHangService.findById(maKhachHang));
    }

    private ResponseObject handleCreateKhachHang(Object payload) {
        KhachHang khachHang = toKhachHang(payload);
        if (khachHang.getMaKhachHang() == null || khachHang.getMaKhachHang().isBlank()) {
            khachHang.setMaKhachHang("KH" + System.currentTimeMillis() % 10000);
        }
        return ResponseObject.ok("Them khach hang thanh cong", khachHangService.create(khachHang));
    }

    private ResponseObject handleUpdateKhachHang(Object payload) {
        KhachHang khachHang = toKhachHang(payload);
        return ResponseObject.ok("Cap nhat khach hang thanh cong", khachHangService.update(khachHang));
    }

    private ResponseObject handleDeleteKhachHang(Object payload) {
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
