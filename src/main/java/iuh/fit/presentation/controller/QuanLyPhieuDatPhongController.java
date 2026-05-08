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
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class QuanLyPhieuDatPhongController {

    private final IPhieuDatPhongService phieuDatPhongService;
    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    private final INhanVienService nhanVienService;
    private final TaiKhoanDTO currentUser;

    private ObservableList<PhieuDatPhongDTO> phieuList = FXCollections.observableArrayList();
    private FilteredList<PhieuDatPhongDTO> filteredList;

    private final Map<String, KhachHangDTO> customerCache = new HashMap<>();
    private final Map<String, PhongDTO> roomCache = new HashMap<>();

    private TextField txtTimKiemSDT;
    private DatePicker dpTuNgay, dpDenNgay;
    private FlowPane cardContainer;
    private final ProgressIndicator loadingOverlay = new ProgressIndicator();

    private final String COLOR_BG_MAIN = "#f8fafc";
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

        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("QUẢN LÝ PHIẾU ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        lblTitle.setTextFill(Color.web("#0f172a"));
        headerBox.getChildren().addAll(lblTitle, new Label("Quản lý phiếu đặt theo từng đợt xác nhận giao dịch"));

        HBox filterBox = createFilterBar();

        cardContainer = new FlowPane(25, 25);
        cardContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        rootPane.getChildren().addAll(headerBox, filterBox, scrollPane);

        loadDataAsync();
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
        btnLamMoi.setOnAction(e -> loadDataAsync());

        bar.getChildren().addAll(new Label("Lọc:"), txtTimKiemSDT, dpTuNgay, dpDenNgay, btnLamMoi);
        return bar;
    }

    private void loadDataAsync() {
        loadingOverlay.setVisible(true);
        cardContainer.setOpacity(0.5);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<KhachHangDTO> allKH = khachHangService.getAllKhachHang();
                List<PhongDTO> allPhong = phongService.getAllPhong();
                List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();

                // NẠP CACHE KHÁCH HÀNG (TRIM ĐỂ KHỚP MÃ)
                customerCache.clear();
                for (KhachHangDTO kh : allKH) {
                    if (kh.getMaKhachHang() != null)
                        customerCache.put(kh.getMaKhachHang().trim().toUpperCase(), kh);
                }

                roomCache.clear();
                for (PhongDTO p : allPhong) {
                    if (p.getMaPhong() != null)
                        roomCache.put(p.getMaPhong().trim().toUpperCase(), p);
                }

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
            if (tu != null) matchNgay = (p.getNgayNhan() != null && !p.getNgayNhan().isBefore(tu));
            if (matchNgay && den != null) matchNgay = (p.getNgayNhan() != null && !p.getNgayNhan().isAfter(den));

            boolean matchSDT = true;
            if (!sdt.isEmpty()) {
                String maKH = p.getMaKhachHang() != null ? p.getMaKhachHang().trim().toUpperCase() : "";
                KhachHangDTO kh = customerCache.get(maKH);
                matchSDT = (kh != null && kh.getSoDienThoai() != null && kh.getSoDienThoai().contains(sdt));
            }
            return matchNgay && matchSDT;
        });
        renderCards();
    }

    // =========================================================================
    // 🎨 RENDER CARDS: GOM NHÓM THEO GIAO DỊCH XÁC NHẬN (TRANSACTION BASED)
    // =========================================================================
    private void renderCards() {
        Platform.runLater(() -> {
            cardContainer.getChildren().clear();

            if (filteredList == null || filteredList.isEmpty()) {
                cardContainer.getChildren().add(new Label("📭 Không tìm thấy phiếu."));
                return;
            }

            // 👉 GOM NHÓM THEO PHẦN MÃ GỐC (Trước dấu gạch ngang)
            Map<String, List<PhieuDatPhongDTO>> groupedMap = filteredList.stream()
                    .collect(Collectors.groupingBy(p -> {
                        String ma = (p.getMaPhieu() != null) ? p.getMaPhieu().trim() : "";
                        return ma.contains("-") ? ma.split("-")[0] : ma;
                    }));

            // SẮP XẾP: Mã phiếu mới nhất (số lớn nhất) hiện lên đầu
            List<List<PhieuDatPhongDTO>> sortedGroups = new ArrayList<>(groupedMap.values());
            sortedGroups.sort((a, b) -> b.get(0).getMaPhieu().compareTo(a.get(0).getMaPhieu()));

            for (List<PhieuDatPhongDTO> group : sortedGroups) {
                try {
                    cardContainer.getChildren().add(createPhieuCard(group));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private VBox createPhieuCard(List<PhieuDatPhongDTO> group) {
        PhieuDatPhongDTO first = group.get(0);
        VBox card = new VBox(10);
        card.setPrefWidth(350);
        card.setPadding(new Insets(20));

        // 1. TRẠNG THÁI & MÀU SẮC (GIỮ STYLE VIỀN TRÁI DÀY CŨ)
        String status = first.getTrangThai() != null ? first.getTrangThai().toUpperCase() : "";
        String color = "#64748b"; String statusText = "Chờ Xác Nhận";
        if (status.contains("CHO_NHAN")) { color = "#f59e0b"; statusText = "Chờ Nhận Phòng"; }
        else if (status.contains("DA_NHAN")) { color = "#10b981"; statusText = "Đang Ở"; }
        else if (status.contains("HUY")) { color = "#ef4444"; statusText = "Đã Hủy"; }
        else if (status.contains("TRA_PHONG")) { color = "#2563eb"; statusText = "Đã Trả Phòng"; }

        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + color + "; -fx-border-width: 1 1 1 6;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.1)));

        // 2. HEADER: HIỂN THỊ MÃ GIAO DỊCH
        HBox head = new HBox();
        head.setAlignment(Pos.CENTER_LEFT);

        String maHienThi = first.getMaPhieu().trim().toUpperCase();
        if (maHienThi.contains("-")) maHienThi = maHienThi.split("-")[0]; // Hiện mã gốc thôi cho đẹp

        String suffix = group.size() > 1 ? " (+" + (group.size() - 1) + " phòng)" : "";
        Label lblMa = new Label(maHienThi + suffix);
        lblMa.setStyle("-fx-font-weight: 900; -fx-font-size: 18px; -fx-text-fill: #0f172a;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblSt = new Label(statusText);
        lblSt.setStyle("-fx-background-color: " + color + "20; -fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-size: 11px;");
        head.getChildren().addAll(lblMa, sp, lblSt);

        // 3. BODY: HIỂN THỊ TÊN KHÁCH VÀ DANH SÁCH PHÒNG
        String maKH = first.getMaKhachHang() != null ? first.getMaKhachHang().trim().toUpperCase() : "";
        KhachHangDTO kh = customerCache.get(maKH);
        String ten = (kh != null) ? kh.getHoTen() : "Khách chưa cập nhật";
        String sdt = (kh != null) ? kh.getSoDienThoai() : "N/A";

        // Gom danh sách tên phòng
        String dsPhong = group.stream()
                .map(p -> {
                    String mP = p.getMaPhong() != null ? p.getMaPhong().trim().toUpperCase() : "";
                    return roomCache.containsKey(mP) ? roomCache.get(mP).getTenPhong() : mP;
                })
                .collect(Collectors.joining(", "));

        VBox body = new VBox(8);
        Label lblTen = new Label("👤 Khách: " + ten);
        lblTen.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");
        Label lblSdt = new Label("📞 SĐT: " + sdt);
        lblSdt.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        Label lblRoom = new Label("🏨 Phòng: " + dsPhong);
        lblRoom.setStyle("-fx-font-weight: 900; -fx-font-size: 14px; -fx-text-fill: " + color + ";");
        Label lblTime = new Label("📅 " + first.getNgayNhan().format(DATE_FORMATTER) + " ➜ " + first.getNgayTra().format(DATE_FORMATTER));
        lblTime.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        body.getChildren().addAll(lblTen, lblSdt, lblRoom, lblTime);

        // 4. ACTIONS: XỬ LÝ THEO NHÓM
        double tongTienNhom = group.stream().mapToDouble(p -> p.getTongTien() != null ? p.getTongTien() : 0.0).sum();
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (statusText.equals("Chờ Nhận Phòng")) {
            Button btnHuy = new Button("Hủy Nhóm");
            btnHuy.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-cursor: hand;");
            btnHuy.setOnAction(e -> xuLyHuyPhieuNhom(group));

            Button btnNhan = new Button("Nhận Phòng");
            btnNhan.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            btnNhan.setOnAction(e -> xuLyNhanPhongNhom(group, tongTienNhom));
            actions.getChildren().addAll(btnHuy, btnNhan);
        } else if (statusText.equals("Đang Ở")) {
            Button btnTra = new Button("💳 Thanh toán & Trả phòng");
            btnTra.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            btnTra.setOnAction(e -> xuLyTraPhongNhom(group, tongTienNhom));
            actions.getChildren().add(btnTra);
        }

        card.getChildren().addAll(head, new Separator(), body, actions);
        return card;
    }

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ ---
    private void xuLyNhanPhongNhom(List<PhieuDatPhongDTO> group, double tongTien) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Tùy chọn nhận phòng");
        alert.setHeaderText("Xác nhận cho " + group.size() + " phòng.");
        ButtonType btnNgay = new ButtonType("💵 Thanh toán ngay", ButtonBar.ButtonData.YES);
        ButtonType btnSau = new ButtonType("⏳ Thanh toán sau", ButtonBar.ButtonData.NO);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnNgay, btnSau, btnHuy);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnNgay) {
            Stage stage = (Stage) cardContainer.getScene().getWindow();
            if (new ThanhToanController(tongTien).showThanhToanDialog(stage) != null) thucHienLuuNhanGroup(group);
        } else if (result.isPresent() && result.get() == btnSau) {
            thucHienLuuNhanGroup(group);
        }
    }

    private void thucHienLuuNhanGroup(List<PhieuDatPhongDTO> group) {
        try {
            for (PhieuDatPhongDTO p : group) {
                p.setTrangThai(STATUS_STAYING);
                phieuDatPhongService.updatePhieuDatPhong(p);
                phongService.updatePhongTrangThai(p.getMaPhong(), "Đang ở");
            }
            loadDataAsync();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage()); }
    }

    private void xuLyTraPhongNhom(List<PhieuDatPhongDTO> group, double tongTien) {
        Stage stage = (Stage) cardContainer.getScene().getWindow();
        if (new ThanhToanController(tongTien).showThanhToanDialog(stage) != null) {
            try {
                for (PhieuDatPhongDTO p : group) {
                    p.setTrangThai("DA_TRA_PHONG");
                    phieuDatPhongService.updatePhieuDatPhong(p);
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Trống");
                }
                loadDataAsync();
            } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage()); }
        }
    }

    private void xuLyHuyPhieuNhom(List<PhieuDatPhongDTO> group) {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Hủy nhóm phòng này?", ButtonType.YES, ButtonType.NO).showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                for (PhieuDatPhongDTO p : group) {
                    p.setTrangThai(STATUS_CANCELLED);
                    phieuDatPhongService.updatePhieuDatPhong(p);
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Trống");
                }
                loadDataAsync();
            } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage()); }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}