package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QuanLyPhieuDatPhongController {

    private final IPhieuDatPhongService phieuDatPhongService;
    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    private final INhanVienService nhanVienService;
    private final TaiKhoanDTO currentUser;

    private ObservableList<PhieuDatPhongDTO> phieuList = FXCollections.observableArrayList();
    private FilteredList<PhieuDatPhongDTO> filteredList;

    // Cache để tăng tốc (Tránh gọi Database liên tục trong vòng lặp vẽ thẻ)
    private final Map<String, KhachHangDTO> customerCache = new HashMap<>();
    // Cache để lưu thông tin phòng, tránh gọi DB liên tục
    private final Map<String, PhongDTO> roomCache = new HashMap<>();

    private TextField txtTimKiemSDT;
    private DatePicker dpTuNgay, dpDenNgay;
    private FlowPane cardContainer;
    private final ProgressIndicator loadingOverlay = new ProgressIndicator();

    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_BG_MAIN = "#f8fafc";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";

    private final String STATUS_WAITING = "CHO_NHAN_PHONG";
    private final String STATUS_STAYING = "DA_NHAN_PHONG";
    private final String STATUS_CANCELLED = "DA_HUY";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QuanLyPhieuDatPhongController(IPhieuDatPhongService phieuDatPhongService,
                                         IPhongService phongService,
                                         IKhachHangService khachHangService,
                                         INhanVienService nhanVienService,
                                         TaiKhoanDTO currentUser) {
        this.phieuDatPhongService = phieuDatPhongService;
        this.phongService = phongService;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.currentUser = currentUser;
    }

    public VBox createQuanLyPhieuView() {
        VBox rootPane = new VBox(25);
        rootPane.setStyle("-fx-background-color: " + COLOR_BG_MAIN + "; -fx-padding: 30;");

        // Header
        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("QUẢN LÝ PHIẾU ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        headerBox.getChildren().addAll(lblTitle, new Label("Tìm kiếm và điều phối booking khách sạn"));

        // Filter Bar
        HBox filterBox = createFilterBar();

        // Container Cards
        cardContainer = new FlowPane(25, 25);
        cardContainer.setPadding(new Insets(10));

        StackPane scrollContent = new StackPane(cardContainer, loadingOverlay);
        loadingOverlay.setVisible(false);
        loadingOverlay.setMaxSize(50, 50);

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        rootPane.getChildren().addAll(headerBox, filterBox, scrollPane);

        loadDataAsync(); // 👉 Load dữ liệu ngầm cực nhanh
        return rootPane;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 12;");

        txtTimKiemSDT = new TextField();
        txtTimKiemSDT.setPromptText("🔍 Tìm theo SĐT khách...");
        txtTimKiemSDT.setPrefWidth(250);
        txtTimKiemSDT.textProperty().addListener((obs, old, nw) -> filterData());

        dpTuNgay = new DatePicker(); dpTuNgay.setPromptText("Từ ngày");
        dpTuNgay.setOnAction(e -> filterData());

        dpDenNgay = new DatePicker(); dpDenNgay.setPromptText("Đến ngày");
        dpDenNgay.setOnAction(e -> filterData());

        Button btnLamMoi = new Button("Làm mới");
        btnLamMoi.setOnAction(e -> {
            dpTuNgay.setValue(null);
            dpDenNgay.setValue(null);
            txtTimKiemSDT.clear();
            loadDataAsync(); // 👉 Phải gọi lại hàm này để bốc dữ liệu từ DB lên lại
        });

        bar.getChildren().addAll(new Label("Lọc:"), txtTimKiemSDT, dpTuNgay, dpDenNgay, btnLamMoi);
        return bar;
    }

    // =========================================================================
    // 🚀 LOAD DỮ LIỆU ASYNC (CHỐNG TREO MÁY)
    // =========================================================================
    private void loadDataAsync() {
        loadingOverlay.setVisible(true);
        cardContainer.setOpacity(0.5);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // 1. Tải danh sách phiếu
                List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();

                // 2. Tải danh sách khách hàng vào Cache
                List<KhachHangDTO> allKH = khachHangService.getAllKhachHang();
                customerCache.clear();
                for (KhachHangDTO kh : allKH) customerCache.put(kh.getMaKhachHang(), kh);

                // 👉 THÊM MỚI: Tải danh sách phòng vào Cache
                List<PhongDTO> allPhong = phongService.getAllPhong();
                roomCache.clear();
                for (PhongDTO p : allPhong) roomCache.put(p.getMaPhong(), p);

                Platform.runLater(() -> {
                    phieuList.setAll(allPhieu);
                    filteredList = new FilteredList<>(phieuList, p -> true);
                    renderCards();
                });
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            loadingOverlay.setVisible(false);
            cardContainer.setOpacity(1.0);
        });

        new Thread(task).start();
    }

    private void filterData() {
        if (filteredList == null) return;
        String sdt = txtTimKiemSDT.getText().trim().toLowerCase();
        LocalDate tu = dpTuNgay.getValue();
        LocalDate den = dpDenNgay.getValue();

        filteredList.setPredicate(p -> {
            boolean matchNgay = true;
            if (tu != null) matchNgay = !p.getNgayNhan().isBefore(tu);
            if (matchNgay && den != null) matchNgay = !p.getNgayNhan().isAfter(den);

            boolean matchSDT = true;
            if (!sdt.isEmpty()) {
                KhachHangDTO kh = customerCache.get(p.getMaKhachHang());
                matchSDT = (kh != null && kh.getSoDienThoai().contains(sdt));
            }
            return matchNgay && matchSDT;
        });
        renderCards();
    }

    // =========================================================================
    // 🎨 RENDER CARDS - ĐÃ FIX LỖI "TÀNG HÌNH" CHỮ
    // =========================================================================
    private void renderCards() {
        Platform.runLater(() -> {
            cardContainer.getChildren().clear();

            // In ra console để Tú debug xem có dữ liệu không
            System.out.println(">>> Tổng số phiếu lấy được: " + filteredList.size());

            if (filteredList.isEmpty()) {
                Label lblEmpty = new Label("📭 Không tìm thấy phiếu. Hãy thử bấm 'Làm mới'");
                lblEmpty.setStyle("-fx-text-fill: gray; -fx-font-size: 14;");
                cardContainer.getChildren().add(lblEmpty);
                return;
            }

            for (PhieuDatPhongDTO phieu : filteredList) {
                try {
                    cardContainer.getChildren().add(createPhieuCard(phieu));
                } catch (Exception e) {
                    // 👉 Nếu 1 phiếu bị NULL thông tin làm crash app, Tú sẽ thấy mã phiếu đó ở đây
                    System.err.println("❌ Lỗi vẽ Card cho phiếu: " + phieu.getMaPhieu());
                    e.printStackTrace();
                }
            }
        });
    }

    private VBox createPhieuCard(PhieuDatPhongDTO phieu) {
        VBox card = new VBox(10);
        card.setPrefWidth(350);
        card.setMinWidth(350);
        card.setMinHeight(200); // Ép chiều cao tối thiểu
        card.setPadding(new Insets(20));

        // --- 1. XỬ LÝ TRẠNG THÁI & MÀU SẮC ---
        String status = phieu.getTrangThai() != null ? phieu.getTrangThai().toUpperCase() : "";
        String color = "#64748b";
        String statusText = "Chờ Xác Nhận";

        if (status.contains("CHO_NHAN") || status.contains("ĐANG CHỜ")) {
            color = "#f59e0b"; statusText = "Chờ Nhận Phòng";
        } else if (status.contains("DA_NHAN") || status.contains("ĐANG Ở")) {
            color = "#10b981"; statusText = "Đang Ở";
        } else if (status.contains("HUY")) {
            color = "#ef4444"; statusText = "Đã Hủy";
        } else if (status.contains("TRA_PHONG") || status.contains("CHECKOUT")) {
            color = "#2563eb"; statusText = "Đã Trả Phòng";
        }

        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + color + "; -fx-border-width: 1 1 1 6;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.1)));

        // --- 2. HEADER (Mã phiếu & Trạng thái) ---
        HBox head = new HBox();
        head.setAlignment(Pos.CENTER_LEFT);

        String maPhieu = phieu.getMaPhieu() != null ? phieu.getMaPhieu() : "N/A";
        Label lblMa = new Label(maPhieu);
        // 👉 ÉP CHỮ MÀU ĐEN ĐẬM
        lblMa.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: 900; -fx-font-size: 18px; -fx-text-fill: #0f172a;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label lblSt = new Label(statusText);
        lblSt.setStyle("-fx-background-color: " + color + "20; -fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-size: 11px;");

        head.getChildren().addAll(lblMa, sp, lblSt);

        // --- 3. BODY (Thông tin khách, Phòng & Thời gian) ---
        KhachHangDTO kh = customerCache.get(phieu.getMaKhachHang());
        String ten = (kh != null && kh.getHoTen() != null) ? kh.getHoTen() : "Khách chưa có tên";
        String sdt = (kh != null && kh.getSoDienThoai() != null) ? kh.getSoDienThoai() : "Chưa có SĐT";


        // 👉 LẤY TÊN PHÒNG TỪ CACHED
        PhongDTO p = roomCache.get(phieu.getMaPhong());
        String tenPhong = (p != null) ? p.getTenPhong() : "Phòng: " + phieu.getMaPhong();

        String ngayNhan = phieu.getNgayNhan() != null ? phieu.getNgayNhan().format(DATE_FORMATTER) : "??/??/????";
        String ngayTra = phieu.getNgayTra() != null ? phieu.getNgayTra().format(DATE_FORMATTER) : "??/??/????";

        VBox body = new VBox(8);
        body.setPadding(new Insets(10, 0, 10, 0));

        Label lblTen = new Label("👤 Khách: " + ten);
        lblTen.setStyle("-fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblSdt = new Label("📞 SĐT: " + sdt);
        lblSdt.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        // 👉 THÊM LABEL TÊN PHÒNG VÀO ĐÂY
        Label lblRoom = new Label("🏨 " + tenPhong);
        lblRoom.setStyle("-fx-text-fill: #0f172a; -fx-font-weight: 900; -fx-font-size: 14px;");

        Label lblTime = new Label("📅 " + ngayNhan + " ➝ " + ngayTra);
        lblTime.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold; -fx-font-size: 13px;");

        // Add tất cả vào body
        body.getChildren().addAll(lblTen, lblSdt, lblRoom, lblTime);

        // --- 4. ACTIONS (Nút bấm) ---
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (statusText.equals("Chờ Nhận Phòng")) {
            Button btnHuy = new Button("Hủy Phiếu");
            btnHuy.setCursor(Cursor.HAND);
            btnHuy.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-background-radius: 6;");
            btnHuy.setOnAction(e -> xuLyHuyPhieu(phieu));

            Button btnNhan = new Button("Nhận Phòng");
            btnNhan.setCursor(Cursor.HAND);
            btnNhan.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
            btnNhan.setOnAction(e -> xuLyNhanPhong(phieu));

            actions.getChildren().addAll(btnHuy, btnNhan);

        } else if (statusText.equals("Đang Ở")) {
            // 👉 THÊM NÚT NÀY ĐỂ XỬ LÝ THANH TOÁN SAU & TRẢ PHÒNG
            Button btnTraPhong = new Button("💳 Thanh toán & Trả phòng");
            btnTraPhong.setCursor(Cursor.HAND);
            btnTraPhong.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
            btnTraPhong.setOnAction(e -> xuLyTraPhong(phieu));

            actions.getChildren().add(btnTraPhong);
        }

        card.getChildren().addAll(head, new Separator(), body, actions);
        return card;
    }

    // =========================================================================
    // XỬ LÝ NGHIỆP VỤ (ĐÃ ĐỒNG BỘ TÊN TRẠNG THÁI VỚI DB)
    // =========================================================================
    // =========================================================================
    // XỬ LÝ NGHIỆP VỤ (CẬP NHẬT: LỰA CHỌN THANH TOÁN TRƯỚC/SAU)
    // =========================================================================
    private void xuLyNhanPhong(PhieuDatPhongDTO phieu) {
        // 1. Tạo hộp thoại hỏi ý kiến Lễ tân
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Tùy chọn nhận phòng");
        alert.setHeaderText("Khách hàng thanh toán ngay hay thanh toán sau?");
        alert.setContentText("Vui lòng chọn hình thức:");

        // Tạo các nút bấm tùy chỉnh
        ButtonType btnThanhToanNgay = new ButtonType("💵 Thanh toán ngay", ButtonBar.ButtonData.YES);
        ButtonType btnThanhToanSau = new ButtonType("⏳ Thanh toán sau", ButtonBar.ButtonData.NO);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnThanhToanNgay, btnThanhToanSau, btnHuy);

        // Hiển thị và chờ người dùng chọn
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == btnThanhToanNgay) {
            // 👉 KỊCH BẢN 1: KHÁCH MUỐN THANH TOÁN LUÔN
            Stage currentStage = (Stage) cardContainer.getScene().getWindow();
            double tongTien = phieu.getTongTien() != null ? phieu.getTongTien() : 0.0;

            ThanhToanController thanhToanCtrl = new ThanhToanController(tongTien);
            String phuongThuc = thanhToanCtrl.showThanhToanDialog(currentStage);

            // Nếu thanh toán thành công (không bấm X tắt ngang)
            if (phuongThuc != null) {
                thucHienNhanPhongVaoDB(phieu, "Đã thanh toán (" + phuongThuc + ")");
            }

        } else if (result.isPresent() && result.get() == btnThanhToanSau) {
            // 👉 KỊCH BẢN 2: KHÁCH THANH TOÁN SAU (Ghi sổ nợ)
            thucHienNhanPhongVaoDB(phieu, "Thanh toán sau");
        }
    }

    // --- Hàm Helper: Xử lý phần chung cập nhật Database ---
    private void thucHienNhanPhongVaoDB(PhieuDatPhongDTO phieu, String ghiChu) {
        try {
            // 1. Đổi trạng thái phiếu
            phieu.setTrangThai(STATUS_STAYING); // "DA_NHAN_PHONG"

            // Tùy chọn: Nếu DTO của bạn có setGhiChu, hãy mở comment dòng dưới để lưu lại lịch sử
            // phieu.setGhiChu(ghiChu);

            phieuDatPhongService.updatePhieuDatPhong(phieu);

            // 2. Đổi trạng thái phòng sang "Đang ở"
            phongService.updatePhongTrangThai(phieu.getMaPhong(), "Đang ở");

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Khách đã nhận phòng! [" + ghiChu + "]");
            loadDataAsync(); // Tải lại giao diện
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể nhận phòng: " + e.getMessage());
        }
    }

    private void xuLyHuyPhieu(PhieuDatPhongDTO phieu) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hủy phiếu này và giải phóng phòng?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                phieu.setTrangThai(STATUS_CANCELLED); // "DA_HUY"
                phieuDatPhongService.updatePhieuDatPhong(phieu);

                phongService.updatePhongTrangThai(phieu.getMaPhong(), "Trống");

                showAlert(Alert.AlertType.INFORMATION, "Đã hủy", "Phiếu đã được hủy thành công!");
                loadDataAsync();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể hủy: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    // =========================================================================
    // XỬ LÝ NGHIỆP VỤ: THANH TOÁN VÀ TRẢ PHÒNG (CHECK-OUT)
    // =========================================================================
    private void xuLyTraPhong(PhieuDatPhongDTO phieu) {
        Stage currentStage = (Stage) cardContainer.getScene().getWindow();
        double tongTien = phieu.getTongTien() != null ? phieu.getTongTien() : 0.0;

        // 1. Mở bảng thanh toán để khách trả tiền
        ThanhToanController thanhToanCtrl = new ThanhToanController(tongTien);
        String phuongThuc = thanhToanCtrl.showThanhToanDialog(currentStage);

        // 2. Nếu khách đã thanh toán thành công (không tắt ngang bảng)
        if (phuongThuc != null) {
            try {
                // Đổi trạng thái Phiếu thành "Đã Trả Phòng"
                phieu.setTrangThai("DA_TRA_PHONG");
                phieuDatPhongService.updatePhieuDatPhong(phieu);

                // Giải phóng phòng, đưa về trạng thái "Trống" để đón khách mới
                phongService.updatePhongTrangThai(phieu.getMaPhong(), "Trống");

                showAlert(Alert.AlertType.INFORMATION, "Hoàn tất", "Đã thanh toán (" + phuongThuc + ") và trả phòng thành công!");
                loadDataAsync(); // Tải lại danh sách
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Không thể trả phòng: " + e.getMessage());
            }
        }
    }
}