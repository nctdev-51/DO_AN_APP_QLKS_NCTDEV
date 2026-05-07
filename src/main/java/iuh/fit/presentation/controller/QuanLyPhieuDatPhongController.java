package iuh.fit.presentation.controller;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.dto.NhanVienDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.INhanVienService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.util.List;
import java.util.Optional;

public class QuanLyPhieuDatPhongController {

    private final IPhieuDatPhongService phieuDatPhongService;
    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    private final INhanVienService nhanVienService; // Dùng để lấy tên nhân viên
    private final TaiKhoanDTO currentUser;

    private ObservableList<PhieuDatPhongDTO> phieuList;
    private FilteredList<PhieuDatPhongDTO> filteredList;

    // UI Components
    private TextField txtTimKiemSDT;
    private DatePicker dpTuNgay, dpDenNgay;
    private FlowPane cardContainer;

    // --- BẢNG MÀU UI/UX HIỆN ĐẠI ĐỒNG BỘ ---
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_PRIMARY_DARK = "#1e3a8a";
    private final String COLOR_BG_MAIN = "#f8fafc";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";

    private final String COLOR_SUCCESS = "#10b981"; // Đã nhận / Hoàn thành
    private final String COLOR_WARNING = "#f59e0b"; // Chờ nhận
    private final String COLOR_DANGER = "#ef4444";  // Đã hủy
    private final String COLOR_INFO = "#0ea5e9";    // Chi tiết (Màu xanh dương)

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

        // --- 1. HEADER ---
        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("QUẢN LÝ PHIẾU ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Tìm kiếm, theo dõi, cập nhật trạng thái và chỉnh sửa các booking hiện tại.");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        headerBox.getChildren().addAll(lblTitle, lblSubTitle);

        // --- 2. BỘ LỌC TÌM KIẾM ---
        HBox filterBox = createFilterBar();

        // --- 3. KHU VỰC HIỂN THỊ DANH SÁCH (THẺ CARD) ---
        cardContainer = new FlowPane();
        cardContainer.setHgap(25);
        cardContainer.setVgap(25);
        cardContainer.setPadding(new Insets(5));

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        rootPane.getChildren().addAll(headerBox, filterBox, scrollPane);
        loadDataToView();

        return rootPane;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-padding: 20; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        bar.setEffect(new DropShadow(10, Color.web("#000000", 0.03)));

        String inputStyle = "-fx-padding: 8 12; -fx-background-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-font-size: 13px; -fx-background-color: #f8fafc;";

        Label lblTu = new Label("Từ ngày nhận:");
        lblTu.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblTu.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpTuNgay = new DatePicker();
        dpTuNgay.setStyle(inputStyle); dpTuNgay.setPrefWidth(140);
        dpTuNgay.setOnAction(e -> filterData());

        Label lblDen = new Label("Đến:");
        lblDen.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblDen.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpDenNgay = new DatePicker();
        dpDenNgay.setStyle(inputStyle); dpDenNgay.setPrefWidth(140);
        dpDenNgay.setOnAction(e -> filterData());

        Label lblSDT = new Label("   |   Khách hàng (SĐT):");
        lblSDT.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblSDT.setTextFill(Color.web(COLOR_TEXT_MUTED));

        txtTimKiemSDT = new TextField();
        txtTimKiemSDT.setPromptText("Nhập số điện thoại...");
        txtTimKiemSDT.setStyle(inputStyle); txtTimKiemSDT.setPrefWidth(200);
        txtTimKiemSDT.textProperty().addListener((obs, oldVal, newVal) -> filterData());

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLamMoi = new Button("🔄 Làm mới");
        btnLamMoi.setCursor(Cursor.HAND);
        btnLamMoi.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;");
        btnLamMoi.setOnAction(e -> {
            dpTuNgay.setValue(null);
            dpDenNgay.setValue(null);
            txtTimKiemSDT.clear();
            loadDataToView();
        });

        bar.getChildren().addAll(lblTu, dpTuNgay, lblDen, dpDenNgay, lblSDT, txtTimKiemSDT, spacer, btnLamMoi);
        return bar;
    }

    // =========================================================================
    // XỬ LÝ DỮ LIỆU & BỘ LỌC
    // =========================================================================

    private void loadDataToView() {
        try {
            List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();
            phieuList = FXCollections.observableArrayList(allPhieu);
            filteredList = new FilteredList<>(phieuList, p -> true);
            renderCards();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu phiếu đặt phòng!");
        }
    }

    private void filterData() {
        if (filteredList == null) return;

        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();
        String keywordSDT = txtTimKiemSDT.getText().trim();

        filteredList.setPredicate(phieu -> {
            boolean matchNgay = true;
            if (tuNgay != null && phieu.getNgayNhan() != null) {
                matchNgay = !phieu.getNgayNhan().isBefore(tuNgay);
            }
            if (matchNgay && denNgay != null && phieu.getNgayNhan() != null) {
                matchNgay = !phieu.getNgayNhan().isAfter(denNgay);
            }

            boolean matchSDT = true;
            if (!keywordSDT.isEmpty()) {
                try {
                    KhachHangDTO kh = khachHangService.getKhachHangById(phieu.getMaKhachHang());
                    if (kh != null && kh.getSoDienThoai() != null) {
                        matchSDT = kh.getSoDienThoai().contains(keywordSDT);
                    } else {
                        matchSDT = false;
                    }
                } catch (Exception e) {
                    matchSDT = false;
                }
            }
            return matchNgay && matchSDT;
        });

        renderCards();
    }

    // =========================================================================
    // VẼ GIAO DIỆN CÁC THẺ (CARD RENDERER)
    // =========================================================================

    private void renderCards() {
        cardContainer.getChildren().clear();

        if (filteredList.isEmpty()) {
            Label lblEmpty = new Label("📭 Không tìm thấy phiếu đặt phòng nào phù hợp.");
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
            lblEmpty.setTextFill(Color.web(COLOR_TEXT_MUTED));
            lblEmpty.setPadding(new Insets(40));
            cardContainer.getChildren().add(lblEmpty);
            return;
        }

        for (PhieuDatPhongDTO phieu : filteredList) {
            cardContainer.getChildren().add(createPhieuCard(phieu));
        }
    }

    private VBox createPhieuCard(PhieuDatPhongDTO phieu) {
        VBox card = new VBox(12);
        card.setPrefWidth(360);
        card.setPadding(new Insets(20));

        // --- XÁC ĐỊNH TRẠNG THÁI VÀ MÀU SẮC ---
        String statusText = phieu.getTrangThai() != null ? phieu.getTrangThai().trim().toUpperCase() : "CHUA_XAC_DINH";
        String displayStatus;
        String statusColorHex;

        if (statusText.contains("NHAN_PHONG") || statusText.contains("NHẬN PHÒNG") || statusText.equals("ĐANG Ở")) {
            statusColorHex = COLOR_SUCCESS;
            displayStatus = "Đã Nhận Phòng";
        } else if (statusText.contains("HUY") || statusText.contains("HỦY")) {
            statusColorHex = COLOR_DANGER;
            displayStatus = "Đã Hủy Phiếu";
        } else if (statusText.contains("TRA_PHONG") || statusText.contains("TRẢ PHÒNG")) {
            statusColorHex = COLOR_TEXT_MUTED;
            displayStatus = "Đã Trả Phòng";
        } else {
            statusColorHex = COLOR_WARNING;
            displayStatus = "Chờ Nhận Phòng";
        }

        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: " + COLOR_BORDER + " " + COLOR_BORDER + " " + COLOR_BORDER + " " + statusColorHex + "; -fx-border-width: 1 1 1 6;");

        DropShadow ds = new DropShadow(10, Color.web("#000000", 0.04));
        card.setEffect(ds);

        // --- HEADER ---
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_LEFT);

        Label lblMa = new Label(phieu.getMaPhieu());
        lblMa.setFont(Font.font("Segoe UI", FontWeight.BLACK, 20));
        lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblStatus = new Label("• " + displayStatus);
        lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lblStatus.setStyle("-fx-background-color: " + statusColorHex + "1A; -fx-text-fill: " + statusColorHex + "; -fx-padding: 5 12; -fx-background-radius: 8;");

        Region topSpacer = new Region(); HBox.setHgrow(topSpacer, Priority.ALWAYS);
        topBox.getChildren().addAll(lblMa, topSpacer, lblStatus);

        // --- NỘI DUNG CHI TIẾT ---
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(5, 0, 10, 0));

        String tenKhach = "N/A", sdtKhach = "N/A";
        try {
            KhachHangDTO kh = khachHangService.getKhachHangById(phieu.getMaKhachHang());
            if (kh != null) { tenKhach = kh.getHoTen(); sdtKhach = kh.getSoDienThoai(); }
        } catch (Exception e) { }

        infoBox.getChildren().add(createIconLabel("👤", "Khách hàng:", tenKhach));
        infoBox.getChildren().add(createIconLabel("📞", "Số điện thoại:", sdtKhach));

        String strNgayNhan = phieu.getNgayNhan() != null ? phieu.getNgayNhan().format(DATE_FORMATTER) : "N/A";
        String strNgayTra = phieu.getNgayTra() != null ? phieu.getNgayTra().format(DATE_FORMATTER) : "N/A";
        infoBox.getChildren().add(createIconLabel("🕒", "Thời gian:", strNgayNhan + " ➝ " + strNgayTra));

        String dsPhongStr = "-";
        try {
            List<PhongDTO> phongList = phongService.getPhongByPhieuDat(phieu.getMaPhieu());
            if (phongList != null && !phongList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (PhongDTO p : phongList) sb.append(p.getMaPhong()).append(", ");
                dsPhongStr = sb.substring(0, sb.length() - 2);
            }
        } catch (Exception e) {}
        infoBox.getChildren().add(createIconLabel("🏨", "Phòng đặt:", dsPhongStr));

        Separator sep = new Separator();

        // --- TỔNG TIỀN ---
        HBox botBox = new HBox();
        botBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTienTxt = new Label("Tổng tiền:");
        lblTienTxt.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblTienTxt.setTextFill(Color.web(COLOR_TEXT_MUTED));

        Label lblTienVal = new Label(String.format("%,.0f đ", phieu.getTongTien() != null ? phieu.getTongTien() : 0.0));
        lblTienVal.setFont(Font.font("Segoe UI", FontWeight.BLACK, 18));
        lblTienVal.setTextFill(Color.web(COLOR_PRIMARY));

        Region botSpacer = new Region(); HBox.setHgrow(botSpacer, Priority.ALWAYS);
        botBox.getChildren().addAll(lblTienTxt, botSpacer, lblTienVal);

        // --- NÚT HÀNH ĐỘNG ---
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        final String finalTenKhach = tenKhach;
        final String finalSdtKhach = sdtKhach;

        Button btnChiTiet = new Button("Chi Tiết");
        btnChiTiet.setCursor(Cursor.HAND);
        btnChiTiet.setStyle("-fx-background-color: " + COLOR_INFO + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8;");
        btnChiTiet.setOnAction(e -> showEditDialog(phieu, finalTenKhach, finalSdtKhach));

        if (displayStatus.equals("Chờ Nhận Phòng")) {
            Button btnHuy = new Button("❌ Hủy");
            btnHuy.setCursor(Cursor.HAND);
            btnHuy.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_DANGER + "; -fx-border-color: " + COLOR_DANGER + "; -fx-border-radius: 6; -fx-font-weight: bold; -fx-padding: 8;");
            btnHuy.setOnAction(e -> xuLyHuyPhieu(phieu));

            Button btnNhan = new Button("🔑 Nhận Phòng");
            btnNhan.setCursor(Cursor.HAND);
            btnNhan.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8;");
            btnNhan.setOnAction(e -> xuLyNhanPhong(phieu));

            btnHuy.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnHuy, Priority.ALWAYS);
            btnChiTiet.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnChiTiet, Priority.ALWAYS);
            btnNhan.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnNhan, Priority.ALWAYS);

            actionBox.getChildren().addAll(btnHuy, btnChiTiet, btnNhan);
        } else {
            btnChiTiet.setStyle("-fx-background-color: " + COLOR_INFO + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10;");
            btnChiTiet.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnChiTiet, Priority.ALWAYS);
            actionBox.getChildren().add(btnChiTiet);
        }

        card.getChildren().addAll(topBox, infoBox, sep, botBox, actionBox);

        // Hover effect
        card.setOnMouseEntered(e -> ds.setRadius(15));
        card.setOnMouseExited(e -> ds.setRadius(10));

        return card;
    }

    // =========================================================================
    // DIALOG CHI TIẾT / SỬA PHIẾU
    // =========================================================================
    private void showEditDialog(PhieuDatPhongDTO phieu, String tenKhach, String sdtKhach) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Chi Tiết Phiếu Đặt Phòng: " + phieu.getMaPhieu());

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white;");

        Label lblTitle = new Label("THÔNG TIN CHI TIẾT PHIẾU ĐẶT");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 20));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        // Lấy thông tin bổ sung
        String maPhong = phieu.getMaPhong() != null ? phieu.getMaPhong() : "N/A";
        String tenPhong = "N/A";
        String giaPhong = "0 đ";
        try {
            if (!"N/A".equals(maPhong)) {
                PhongDTO p = phongService.getPhongById(maPhong);
                if (p != null) {
                    tenPhong = p.getTenPhong();
                    giaPhong = String.format("%,.0f đ", p.getGiaPhong());
                }
            }
        } catch (Exception ignored) {}

        String maNV = phieu.getMaNhanVien() != null ? phieu.getMaNhanVien() : "N/A";
        String tenNV = "N/A";
        try {
            if (!"N/A".equals(maNV) && nhanVienService != null) {
                NhanVienDTO nv = nhanVienService.getNhanVienById(maNV);
                if (nv != null) tenNV = nv.getHoTen();
            }
        } catch (Exception ignored) {}

        GridPane grid = new GridPane();
        grid.setHgap(30); grid.setVgap(15);
        String lblStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + COLOR_TEXT_MUTED + ";";
        String valStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: 900; -fx-font-size: 14px; -fx-text-fill: " + COLOR_TEXT_MAIN + ";";

        grid.add(createStyledLabel("Mã Phiếu:", lblStyle), 0, 0);
        grid.add(createStyledLabel(phieu.getMaPhieu(), valStyle), 1, 0);
        grid.add(createStyledLabel("Trạng Thái:", lblStyle), 2, 0);
        Label lblSt = createStyledLabel(phieu.getTrangThai(), valStyle);
        lblSt.setTextFill(Color.web(COLOR_PRIMARY));
        grid.add(lblSt, 3, 0);

        grid.add(createStyledLabel("Mã Khách Hàng:", lblStyle), 0, 1);
        grid.add(createStyledLabel(phieu.getMaKhachHang(), valStyle), 1, 1);
        grid.add(createStyledLabel("Tên Khách Hàng:", lblStyle), 2, 1);
        grid.add(createStyledLabel(tenKhach, valStyle), 3, 1);

        grid.add(createStyledLabel("SĐT Khách:", lblStyle), 0, 2);
        grid.add(createStyledLabel(sdtKhach, valStyle), 1, 2);
        grid.add(createStyledLabel("Ngày Đặt Phiếu:", lblStyle), 2, 2);
        grid.add(createStyledLabel(phieu.getNgayDat() != null ? phieu.getNgayDat().format(DATE_FORMATTER) : "N/A", valStyle), 3, 2);

        grid.add(createStyledLabel("Mã Phòng:", lblStyle), 0, 3);
        grid.add(createStyledLabel(maPhong, valStyle), 1, 3);
        grid.add(createStyledLabel("Tên Phòng:", lblStyle), 2, 3);
        grid.add(createStyledLabel(tenPhong, valStyle), 3, 3);

        grid.add(createStyledLabel("Giá Phòng:", lblStyle), 0, 4);
        grid.add(createStyledLabel(giaPhong, valStyle), 1, 4);
        grid.add(createStyledLabel("Tổng Tiền:", lblStyle), 2, 4);
        Label lblTien = createStyledLabel(String.format("%,.0f đ", phieu.getTongTien()), valStyle);
        lblTien.setTextFill(Color.web(COLOR_SUCCESS));
        grid.add(lblTien, 3, 4);

        grid.add(createStyledLabel("Mã Nhân Viên:", lblStyle), 0, 5);
        grid.add(createStyledLabel(maNV, valStyle), 1, 5);
        grid.add(createStyledLabel("Tên Nhân Viên:", lblStyle), 2, 5);
        grid.add(createStyledLabel(tenNV, valStyle), 3, 5);

        grid.add(createStyledLabel("Ngày Nhận:", lblStyle), 0, 6);
        DatePicker dpNhan = new DatePicker(phieu.getNgayNhan());
        dpNhan.setPrefWidth(160);
        grid.add(dpNhan, 1, 6);

        grid.add(createStyledLabel("Ngày Trả:", lblStyle), 2, 6);
        DatePicker dpTra = new DatePicker(phieu.getNgayTra());
        dpTra.setPrefWidth(160);
        grid.add(dpTra, 3, 6);

        String status = phieu.getTrangThai() != null ? phieu.getTrangThai().toUpperCase() : "";
        boolean isEditable = status.contains("DAT_PHONG") || status.contains("ĐẶT PHÒNG") || status.contains("CHO_NHAN");
        dpNhan.setDisable(!isEditable);
        dpTra.setDisable(!isEditable);

        Button btnSave = new Button("💾 Lưu Ngày Thay Đổi");
        btnSave.setCursor(Cursor.HAND);
        btnSave.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 8;");
        btnSave.setDisable(!isEditable);
        btnSave.setOnAction(e -> {
            if (dpTra.getValue().isBefore(dpNhan.getValue())) {
                showAlert(Alert.AlertType.WARNING, "Lỗi Nhập Liệu", "Ngày trả không được nhỏ hơn ngày nhận!");
                return;
            }
            try {
                phieu.setNgayNhan(dpNhan.getValue());
                phieu.setNgayTra(dpTra.getValue());
                phieuDatPhongService.updatePhieuDatPhong(phieu);
                showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Đã cập nhật thông tin phiếu!");
                dialog.close();
                loadDataToView();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", "Không thể lưu thay đổi: " + ex.getMessage());
            }
        });

        Button btnCancel = new Button("Đóng");
        btnCancel.setCursor(Cursor.HAND);
        btnCancel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: " + COLOR_TEXT_MAIN + "; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 8;");
        btnCancel.setOnAction(e -> dialog.close());

        HBox btnBox = new HBox(15, btnCancel, btnSave);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(lblTitle, new Separator(), grid, new Separator(), btnBox);
        Scene scene = new Scene(root, 700, 480);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private Label createStyledLabel(String text, String style) {
        Label lbl = new Label(text != null ? text : "N/A");
        lbl.setStyle(style);
        return lbl;
    }

    // =========================================================================
    // LOGIC NHẬN PHÒNG & HỦY PHIẾU
    // =========================================================================
    private void xuLyNhanPhong(PhieuDatPhongDTO phieu) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xác nhận khách đã đến NHẬN PHÒNG cho phiếu: " + phieu.getMaPhieu() + "?");
        confirm.setTitle("Xác nhận"); confirm.setHeaderText(null);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                phieu.setTrangThai("Nhận Phòng");
                phieuDatPhongService.updatePhieuDatPhong(phieu);

                List<PhongDTO> dsPhong = phongService.getPhongByPhieuDat(phieu.getMaPhieu());
                if(dsPhong != null) {
                    for (PhongDTO p : dsPhong) {
                        p.setTinhTrang("Đang ở");
                        phongService.updatePhong(p);
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật trạng thái thành ĐÃ NHẬN PHÒNG!");
                loadDataToView();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật: " + e.getMessage());
            }
        }
    }

    private void xuLyHuyPhieu(PhieuDatPhongDTO phieu) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "CẢNH BÁO: Bạn có chắc muốn HỦY phiếu này? Các phòng sẽ được giải phóng.", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận Hủy"); confirm.setHeaderText(null);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            try {
                phieu.setTrangThai("Hủy");
                phieuDatPhongService.updatePhieuDatPhong(phieu);

                List<PhongDTO> dsPhong = phongService.getPhongByPhieuDat(phieu.getMaPhieu());
                if(dsPhong != null) {
                    for (PhongDTO p : dsPhong) {
                        p.setTinhTrang("Trống");
                        phongService.updatePhong(p);
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã hủy phiếu và giải phóng phòng!");
                loadDataToView();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi hủy phiếu: " + e.getMessage());
            }
        }
    }

    private HBox createIconLabel(String icon, String title, String value) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.TOP_LEFT);

        Label lblTitle = new Label(icon + " " + title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblTitle.setPrefWidth(110);
        lblTitle.setMinWidth(110);

        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblValue.setTextFill(Color.web(COLOR_TEXT_MAIN));
        lblValue.setWrapText(true);
        HBox.setHgrow(lblValue, Priority.ALWAYS);

        box.getChildren().addAll(lblTitle, lblValue);
        return box;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}