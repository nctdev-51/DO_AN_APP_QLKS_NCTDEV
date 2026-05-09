package iuh.fit.infrastructure.network;

import iuh.fit.core.dto.*;
import iuh.fit.core.repository.*;
import iuh.fit.infrastructure.persistence.*;
import iuh.fit.core.service.*;
import iuh.fit.core.service.impl.*;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class HotelServer {
    private static IPhongService phongService;
    private static IKhachHangService khachHangService;
    private static IPhieuDatPhongService phieuDatPhongService;
    private static IDichVuService dichVuService;
    private static IHoaDonService hoaDonService;
    private static IChiTietHoaDonService chiTietHoaDonService;
    private static INhanVienService nhanVienService;
    private static IGiaoCaService giaoCaService;
    private static ICaLamViecService caLamViecService;
    private static IPhanCongService phanCongService;
    private static IBaoCaoService baoCaoService;
    private static IAuthenticationService authenticationService;
    private static IYeuCauPheDuyetService yeuCauPheDuyetService;

    public static void main(String[] args) {
        initServices();
        ExecutorService pool = Executors.newFixedThreadPool(20);
        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            System.out.println("TTV Hotel Server đang chạy, cổng 9999");
            while (true) {
                Socket socket = serverSocket.accept();
                pool.submit(new ClientHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // (Tùy chọn) Đóng pool khi server tắt
            pool.shutdown();
        }
    }

    private static void initServices() {
        // Repositories
        IPhongRepository phongRepo = new PhongRepositoryImpl();
        IKhachHangRepository khRepo = new KhachHangRepositoryImpl();
        IPhieuDatPhongRepository phieuRepo = new PhieuDatPhongRepositoryImpl();
        IDichVuRepository dichVuRepo = new DichVuRepositoryImpl();
        IHoaDonRepository hoaDonRepo = new HoaDonRepositoryImpl();
        IChiTietHoaDonRepository ctHoaDonRepo = new ChiTietHoaDonRepositoryImpl();
        INhanVienRepository nvRepo = new NhanVienRepositoryImpl();
        IPhanCongRepository pcRepo = new PhanCongRepositoryImpl();
        IBaoCaoRepository baoCaoRepo = new BaoCaoRepositoryImpl();
        ITaiKhoanRepository tkRepo = new TaiKhoanRepositoryImpl();
        IChiTietPhieuDatPhongRepository ctPhieuRepo = new ChiTietPhieuDatPhongRepositoryImpl();
        IKhuyenMaiRepository khuyenMaiRepo = new KhuyenMaiRepositoryImpl();


        // 👉 FIX 1: Dùng ILichSuCaLamViecRepository thay vì IGiaoCaRepository cũ
        ILichSuCaLamViecRepository lichSuRepo = new LichSuCaLamViecRepositoryImpl();

        // 👉 FIX 2: Khởi tạo thêm Repository cho Yêu cầu phê duyệt
        IYeuCauPheDuyetRepository yeuCauRepo = new YeuCauPheDuyetRepositoryImpl();

        // Services
        phongService = new PhongServiceImpl(phongRepo);
        khachHangService = new KhachHangServiceImpl(khRepo);
        phieuDatPhongService = new PhieuDatPhongServiceImpl(phieuRepo);
        dichVuService = new DichVuServiceImpl(dichVuRepo);
        hoaDonService = new HoaDonServiceImpl(
                hoaDonRepo,
                ctHoaDonRepo,
                phieuRepo,
                phongRepo,
                dichVuRepo,
                ctPhieuRepo,
                khRepo,
                nvRepo,
                khuyenMaiRepo
        );
        chiTietHoaDonService = new ChiTietHoaDonServiceImpl(
                ctHoaDonRepo,
                ctPhieuRepo,
                dichVuRepo,
                phieuRepo
        );
        nhanVienService = new NhanVienServiceImpl(nvRepo);
        caLamViecService = new CaLamViecServiceImpl();
        phanCongService = new PhanCongServiceImpl(pcRepo);
        baoCaoService = new BaoCaoServiceImpl(baoCaoRepo);
        authenticationService = new AuthenticationServiceImpl(tkRepo);

        // 👉 FIX 1: Truyền đúng lichSuRepo vào GiaoCaServiceImpl
        giaoCaService = new GiaoCaServiceImpl(lichSuRepo, pcRepo, hoaDonRepo);

        // 👉 FIX 2: Khởi tạo Service Yêu cầu phê duyệt
        yeuCauPheDuyetService = new YeuCauPheDuyetServiceImpl(yeuCauRepo, giaoCaService);
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        public ClientHandler(Socket socket) { this.socket = socket; }
        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                while (true) {
                    Request req = (Request) in.readObject();
                    Response res = processRequest(req);
                    out.writeObject(res);
                    out.flush();
                }
            } catch (EOFException e) { /* client disconnected */ }
            catch (Exception e) { e.printStackTrace(); }
        }

        private Response processRequest(Request req) {
            try {
                CommandType cmd = req.getCommandType();
                Object data = req.getData();
                switch (cmd) {
                    // === Phòng ===
                    case GET_ALL_PHONG:
                        return success(phongService.getAllPhong());
                    case GET_PHONG_BY_ID:
                        return success(phongService.getPhongById((String) data));
                    case GET_PHONG_BY_TINH_TRANG:
                        return success(phongService.getPhongByTinhTrang((String) data));
                    case GET_PHONG_BY_MA_LOAI_PHONG:
                        return success(phongService.getPhongByMaLoaiPhong((String) data));
                    case ADD_PHONG:
                        return success(phongService.addPhong((PhongDTO) data));
                    case UPDATE_PHONG:
                        return success(phongService.updatePhong((PhongDTO) data));
                    case DELETE_PHONG:
                        return success(phongService.deletePhong((String) data));
                    case FIND_AVAILABLE_ROOMS: {
                        Object[] arr = (Object[]) data;
                        return success(phongService.findAvailableRooms(
                                (LocalDate) arr[0],
                                (LocalDate) arr[1],
                                (double) arr[2],
                                (double) arr[3],
                                (String) arr[4]
                        ));
                    }

                    // === Khách hàng ===
                    case GET_ALL_KHACH_HANG:
                        return success(khachHangService.getAllKhachHang());
                    case GET_KHACH_HANG_BY_ID:
                        return success(khachHangService.getKhachHangById((String) data));
                    case GET_KHACH_HANG_BY_SDT:
                        return success(khachHangService.getKhachHangBySoDienThoai((String) data));
                    case ADD_KHACH_HANG:
                        return success(khachHangService.addKhachHang((KhachHangDTO) data));
                    case UPDATE_KHACH_HANG:
                        return success(khachHangService.updateKhachHang((KhachHangDTO) data));
                    case DELETE_KHACH_HANG:
                        return success(khachHangService.deleteKhachHang((String) data));

                    // === Phiếu đặt phòng ===
                    case PHAT_SINH_MA_PHIEU_MOI:
                        return success(phieuDatPhongService.phatSinhMaPhieuMoi());
                    case GET_ALL_PHIEU_DAT_PHONG:
                        return success(phieuDatPhongService.getAllPhieuDatPhong());
                    case GET_PHIEU_DAT_PHONG_BY_ID:
                        return success(phieuDatPhongService.getPhieuDatPhongById((String) data));
                    case GET_PHIEU_DAT_PHONG_BY_KHACH_HANG:
                        return success(phieuDatPhongService.getPhieuDatPhongByKhachHang((String) data));
                    case GET_PHIEU_DAT_PHONG_BY_PHONG:
                        return success(phieuDatPhongService.getPhieuDatPhongByPhong((String) data));
                    case GET_PHIEU_DAT_PHONG_IN_DATE_RANGE: {
                        Object[] arr = (Object[]) data;
                        return success(phieuDatPhongService.getPhieuDatPhongInDateRange(
                                (LocalDate) arr[0], (LocalDate) arr[1]));
                    }
                    case ADD_PHIEU_DAT_PHONG:
                        return success(phieuDatPhongService.addPhieuDatPhong((PhieuDatPhongDTO) data));
                    case UPDATE_PHIEU_DAT_PHONG:
                        return success(phieuDatPhongService.updatePhieuDatPhong((PhieuDatPhongDTO) data));
                    case DELETE_PHIEU_DAT_PHONG:
                        return success(phieuDatPhongService.deletePhieuDatPhong((String) data));
                    case BOOK_ROOM_TRANSACTION:
                        return success(phieuDatPhongService.bookRoomTransaction((PhieuDatPhongDTO) data));

                    // === Dịch vụ ===
                    case GET_ALL_DICH_VU:
                        return success(dichVuService.getAllDichVu());
                    case GET_DICH_VU_BY_ID:
                        return success(dichVuService.getDichVuById((String) data));
                    case ADD_DICH_VU:
                        return success(dichVuService.addDichVu((DichVuDTO) data));
                    case UPDATE_DICH_VU:
                        return success(dichVuService.updateDichVu((DichVuDTO) data));
                    case DELETE_DICH_VU:
                        return success(dichVuService.deleteDichVu((String) data));

                    // === Hóa đơn ===
                    case GET_ALL_HOA_DON: // 👉 THÊM CASE NÀY VÀO
                        return success(hoaDonService.getAllHoaDon());

                    case GET_HOA_DON_BY_DATE_RANGE: {
                        Object[] arr = (Object[]) data;
                        return success(hoaDonService.getHoaDonByDateRange(
                                (LocalDate) arr[0], (LocalDate) arr[1]));
                    }
                    case GET_TOTAL_SERVICE_REVENUE_BY_DATE_RANGE: {
                        Object[] arr = (Object[]) data;
                        return success(hoaDonService.getTotalServiceRevenueByDateRange(
                                (LocalDate) arr[0], (LocalDate) arr[1]));
                    }
                    case GET_HOA_DON_BY_PHIEU_DAT:
                        return success(hoaDonService.getHoaDonByPhieuDat((String) data));
                    case CALCULATE_INVOICE_AT_CHECKOUT: {
                        Object[] arr = (Object[]) data;
                        return success(hoaDonService.calculateInvoiceAtCheckout(
                                (String) arr[0], (double) arr[1], (double) arr[2]));
                    }
                    case ADD_HOA_DON:
                        return success(hoaDonService.addHoaDon((HoaDonDTO) data));

                    // === Chi tiết hóa đơn ===
                    case GET_ALL_CHI_TIET_HOA_DON:
                        return success(chiTietHoaDonService.getAllChiTietHoaDon());
                    case GET_CHI_TIET_HOA_DON_BY_ID: {
                        Object[] arr = (Object[]) data;
                        return success(chiTietHoaDonService.getChiTietHoaDonById(
                                (String) arr[0], (String) arr[1]));
                    }
                    case ADD_CHI_TIET_HOA_DON:
                        return success(chiTietHoaDonService.addChiTietHoaDon((ChiTietHoaDonDTO) data));
                    case UPDATE_CHI_TIET_HOA_DON:
                        return success(chiTietHoaDonService.updateChiTietHoaDon((ChiTietHoaDonDTO) data));
                    case DELETE_CHI_TIET_HOA_DON: {
                        Object[] arr = (Object[]) data;
                        return success(chiTietHoaDonService.deleteChiTietHoaDon(
                                (String) arr[0], (String) arr[1]));
                    }
                    case GET_CHI_TIET_HOA_DON_BY_HOA_DON:
                        return success(chiTietHoaDonService.getChiTietHoaDonByHoaDon((String) data));
                    case GET_CHI_TIET_BY_MA_PHIEU:
                        return success(chiTietHoaDonService.getChiTietByMaPhieu((String) data));
                    case ADD_OR_UPDATE_CHI_TIET:
                        chiTietHoaDonService.addOrUpdateChiTiet((ChiTietHoaDonDTO) data);
                        return success(null);
                    case DELETE_BY_HOA_DON:
                        chiTietHoaDonService.deleteByHoaDon((String) data);
                        return success(null);

                    // === Nhân viên ===
                    case GET_ALL_NHAN_VIEN:
                        return success(nhanVienService.getAllNhanVien());
                    case GET_NHAN_VIEN_BY_ID:
                        return success(nhanVienService.getNhanVienById((String) data));
                    case GET_NHAN_VIEN_BY_SDT:
                        return success(nhanVienService.getNhanVienBySoDienThoai((String) data));
                    case ADD_NHAN_VIEN:
                        return success(nhanVienService.addNhanVien((NhanVienDTO) data));
                    case UPDATE_NHAN_VIEN:
                        return success(nhanVienService.updateNhanVien((NhanVienDTO) data));
                    case DELETE_NHAN_VIEN:
                        return success(nhanVienService.deleteNhanVien((String) data));
                    case GENERATE_MA_NHAN_VIEN:
                        return success(nhanVienService.generateMaNhanVien());

                    // === Giao ca ===
                    case GET_CA_DANG_LAM:
                        return success(giaoCaService.getCaDangLam((String) data));
                    case NHAN_CA: {
                        Map<String, Object> params = (Map<String, Object>) data;
                        giaoCaService.nhanCa(
                                (String) params.get("maNV"),
                                (Double) params.get("tien"),
                                (Boolean) params.get("override"));
                        return success(null);
                    }
                    case GIAO_CA: {
                        Map<String, Object> params = (Map<String, Object>) data;
                        giaoCaService.giaoCa(
                                (String) params.get("maLichSu"),
                                (Double) params.get("tien"),
                                (String) params.get("ghiChu"));
                        return success(null);
                    }

                    // === Ca làm việc & Phân công ===
                    case GET_ALL_CA_LAM_VIEC:
                        return success(caLamViecService.getAll());
                    case GET_ALL_PHAN_CONG:
                        return success(phanCongService.findAll());
                    case SAVE_PHAN_CONG:
                        phanCongService.save((PhanCongDTO) data);
                        return success(null);
                    case DELETE_PHAN_CONG:
                        phanCongService.delete((String) data);
                        return success(null);

                    // === Báo cáo ===
                    case GET_ALL_BAO_CAO:
                        return success(baoCaoService.findAll());
                    case SAVE_BAO_CAO:
                        baoCaoService.save((BaoCaoDTO) data);
                        return success(null);
                    case UPDATE_BAO_CAO:
                        baoCaoService.update((BaoCaoDTO) data);
                        return success(null);

                    // === Authentication ===
                    case LOGIN: {
                        Object[] arr = (Object[]) data;
                        return success(authenticationService.login(
                                (String) arr[0], (String) arr[1]));
                    }

                    // === Phiếu đặt phòng (Bổ sung thêm hàm checkout) ===
                    case CHECKOUT_TRANSACTION: {
                        Object[] arr = (Object[]) data;
                        return success(phieuDatPhongService.checkoutTransaction(
                                (String) arr[0], (HoaDonDTO) arr[1]));
                    }

                    // === Yêu cầu phê duyệt (Chức năng mới hoàn toàn) ===
                    // LƯU Ý: Nhớ khai báo và khởi tạo yeuCauPheDuyetService ở đầu file HotelServer nhé!
                    case TAO_YEU_CAU: {
                        Map<String, Object> params = (Map<String, Object>) data;
                        yeuCauPheDuyetService.taoYeuCau(
                                (String) params.get("maNV"),
                                (String) params.get("tenNV"),
                                (Double) params.get("tien"),
                                (String) params.get("lyDo")
                        );
                        return success(null);
                    }
                    case DUYET_YEU_CAU: {
                        Map<String, Object> params = (Map<String, Object>) data;
                        yeuCauPheDuyetService.duyetYeuCau(
                                (String) params.get("maYC"),
                                (String) params.get("maQL"),
                                (Boolean) params.get("dongY")
                        );
                        return success(null);
                    }
                    case GET_YEU_CAU_CHUA_DUYET:
                        return success(yeuCauPheDuyetService.getYeuCauChuaDuyet());

                    default:
                        return fail("Lệnh không được hỗ trợ: " + cmd);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return fail(e.getMessage());
            }
        }
        private Response success(Object data) {
            return Response.builder().success(true).data(data).build();
        }
        private Response fail(String msg) {
            return Response.builder().success(false).message(msg).build();
        }
    }
}