package iuh.fit.presentation.controller;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.dto.NhanVienDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import iuh.fit.core.service.IDichVuService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DatPhongController {

    private IKhachHangService khachHangService;
    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IDichVuService dichVuService;

    private List<String> danhSachMaPhong;
    private NhanVienDTO nhanVien;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Runnable onRefresh;

    // UI Components
    private TextField txtSdt, txtHoTen;
    private DatePicker dpNgaySinh;
    private ComboBox<String> cboLoaiKhach, cboKhuyenMai;
    private DatePicker dpNgayDat, dpNgayTra;
    private TextField txtSoNgayThue;

    // Bảng dữ liệu phòng
    private TableView<PhongDTO> tablePhong;
    private ObservableList<PhongDTO> phongList;

    // Tính tiền
    private Label lblTongTienPhongVal, lblTongTienDichVuVal, lblKhuyenMaiVal, lblTongThanhToanVal;
    private double tongTienPhong = 0.0;
    private double tongTienDichVu = 0.0;
    private double tienKhuyenMai = 0.0;
    private double tongThanhToan = 0.0;

    // --- BẢNG MÀU UI/UX HIỆN ĐẠI ---
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_ACCENT = "#0ea5e9";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";

    public DatPhongController(List<String> danhSachMaPhong, NhanVienDTO nhanVien, LocalDate checkIn, LocalDate checkOut,
                              IKhachHangService khachHangService, IPhongService phongService,
                              IPhieuDatPhongService phieuDatPhongService, IDichVuService dichVuService, Runnable onRefresh) {
        this.danhSachMaPhong = danhSachMaPhong;
        this.nhanVien = nhanVien;
        this.checkInDate = checkIn != null ? checkIn : LocalDate.now();
        this.checkOutDate = checkOut != null ? checkOut : LocalDate.now().plusDays(1);

        this.khachHangService = khachHangService;
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.dichVuService = dichVuService;
        this.onRefresh = onRefresh;
    }

    public void showDialog(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Lập Phiếu Đặt Phòng");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);

        VBox rootPane = new VBox(20);
        rootPane.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-padding: 25;");

        // --- 1. HEADER ---
        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("LẬP PHIẾU ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 26));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Tạo mới phiếu đặt phòng cho " + danhSachMaPhong.size() + " phòng đã chọn");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        headerBox.getChildren().addAll(lblTitle, lblSubTitle);

        // --- 2. KHU VỰC THÔNG TIN (TOP) ---
        HBox topInfoBox = new HBox(20);
        VBox pnlKhachHang = createKhachHangPanel();
        HBox.setHgrow(pnlKhachHang, Priority.ALWAYS);
        VBox pnlThuePhong = createThuePhongPanel();
        HBox.setHgrow(pnlThuePhong, Priority.ALWAYS);
        topInfoBox.getChildren().addAll(pnlKhachHang, pnlThuePhong);

        // --- 3. KHU VỰC CHI TIẾT & TÍNH TIỀN (BOTTOM) ---
        HBox bottomMainBox = new HBox(20);
        VBox.setVgrow(bottomMainBox, Priority.ALWAYS);

        VBox pnlTable = createTablePhongPanel();
        HBox.setHgrow(pnlTable, Priority.ALWAYS);
        VBox pnlTinhTien = createTinhTienPanel(stage);

        bottomMainBox.getChildren().addAll(pnlTable, pnlTinhTien);
        rootPane.getChildren().addAll(headerBox, topInfoBox, bottomMainBox);

        ScrollPane scrollPane = new ScrollPane(rootPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-control-inner-background: " + COLOR_BG + "; -fx-border-color: transparent;");

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);

        // ✅ Khởi tạo Logic và Validate
        setupDateValidation();
        loadPhongData();
        tinhSoNgayVaTien();

        stage.showAndWait();
    }

    // =========================================================================
    // ✅ CẢI THIỆN: VALIDATE NGÀY CHẶT CHẼ TRÊN GIAO DIỆN
    // =========================================================================
    private void setupDateValidation() {
        // Chặn chọn ngày quá khứ cho Ngày nhận phòng
        dpNgayDat.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // Ngày trả phòng luôn phải lớn hơn ngày nhận phòng
        dpNgayTra.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate checkIn = dpNgayDat.getValue();
                setDisable(empty || date.isBefore(checkIn != null ? checkIn.plusDays(1) : LocalDate.now().plusDays(1)));
            }
        });

        dpNgayDat.valueProperty().addListener((obs, oldV, newV) -> {
            // Tự động đẩy ngày trả lên nếu ngày nhận bị dời qua ngày trả hiện tại
            if (dpNgayTra.getValue() != null && newV != null && !dpNgayTra.getValue().isAfter(newV)) {
                dpNgayTra.setValue(newV.plusDays(1));
            }
            tinhSoNgayVaTien();
        });

        dpNgayTra.valueProperty().addListener((obs, oldV, newV) -> tinhSoNgayVaTien());
    }

    // =========================================================================
    // 1. PANEL KHÁCH HÀNG
    // =========================================================================
    private VBox createKhachHangPanel() {
        VBox box = createCardBox("👤 Thông tin Khách hàng");
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        String inputStyle = "-fx-padding: 8 10; -fx-background-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-font-size: 13px;";

        txtSdt = new TextField(); txtSdt.setPromptText("Nhập SĐT..."); txtSdt.setStyle(inputStyle);
        txtHoTen = new TextField(); txtHoTen.setPromptText("Họ và tên"); txtHoTen.setStyle(inputStyle);

        dpNgaySinh = new DatePicker(); dpNgaySinh.setStyle(inputStyle); dpNgaySinh.setMaxWidth(Double.MAX_VALUE);
        cboLoaiKhach = new ComboBox<>(FXCollections.observableArrayList("KHACH_MOI", "KHACH_HOI_VIEN", "KHACH_VIP"));
        cboLoaiKhach.setValue("KHACH_MOI");
        cboLoaiKhach.setStyle(inputStyle); cboLoaiKhach.setMaxWidth(Double.MAX_VALUE);

        HBox sdtBox = new HBox(8);
        Button btnTim = new Button("🔍 Tìm");
        btnTim.setStyle("-fx-background-color: " + COLOR_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 0 10;");
        btnTim.setOnAction(e -> timKhachHang());
        HBox.setHgrow(txtSdt, Priority.ALWAYS);
        sdtBox.getChildren().addAll(txtSdt, btnTim);

        grid.add(createInputBox("Số điện thoại", sdtBox), 0, 0);
        grid.add(createInputBox("Họ và Tên", txtHoTen), 1, 0);
        grid.add(createInputBox("Ngày sinh", dpNgaySinh), 0, 1);
        grid.add(createInputBox("Loại khách hàng", cboLoaiKhach), 1, 1);

        box.getChildren().add(grid);
        return box;
    }

    private VBox createThuePhongPanel() {
        VBox box = createCardBox("📅 Thông tin Thuê phòng");
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        String inputStyle = "-fx-padding: 8 10; -fx-background-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-font-size: 13px;";

        dpNgayDat = new DatePicker(checkInDate); dpNgayDat.setStyle(inputStyle); dpNgayDat.setMaxWidth(Double.MAX_VALUE);
        dpNgayTra = new DatePicker(checkOutDate); dpNgayTra.setStyle(inputStyle); dpNgayTra.setMaxWidth(Double.MAX_VALUE);

        txtSoNgayThue = new TextField("1");
        txtSoNgayThue.setEditable(false);
        txtSoNgayThue.setStyle(inputStyle + " -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");

        cboKhuyenMai = new ComboBox<>(FXCollections.observableArrayList("Không có", "Giảm 10%", "Voucher 200k"));
        cboKhuyenMai.setValue("Không có");
        cboKhuyenMai.setStyle(inputStyle); cboKhuyenMai.setMaxWidth(Double.MAX_VALUE);

        cboKhuyenMai.valueProperty().addListener((obs, oldV, newV) -> capNhatTongTien());

        grid.add(createInputBox("Ngày nhận phòng", dpNgayDat), 0, 0);
        grid.add(createInputBox("Ngày trả phòng", dpNgayTra), 1, 0);
        grid.add(createInputBox("Số ngày lưu trú", txtSoNgayThue), 0, 1);
        grid.add(createInputBox("Mã khuyến mãi", cboKhuyenMai), 1, 1);

        box.getChildren().add(grid);
        return box;
    }

    // =========================================================================
    // ✅ CẢI THIỆN: BẢNG PHÒNG HIỂN THỊ ĐỘNG (Tính tiền theo ngày)
    // =========================================================================
    private VBox createTablePhongPanel() {
        VBox box = createCardBox("🏨 Tổ hợp Phòng (" + danhSachMaPhong.size() + " phòng)");

        tablePhong = new TableView<>();
        tablePhong.setStyle("-fx-font-size: 14px; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6;");
        tablePhong.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tablePhong, Priority.ALWAYS);

        TableColumn<PhongDTO, String> colMa = new TableColumn<>("Phòng");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maPhong"));

        TableColumn<PhongDTO, String> colGia = new TableColumn<>("Đơn Giá / Đêm");
        colGia.setCellValueFactory(p -> new SimpleStringProperty(String.format("%,.0f đ", p.getValue().getGiaPhong())));

        // Cột Thành Tiền = Giá phòng * Số ngày (Cập nhật động)
        TableColumn<PhongDTO, String> colThanhTien = new TableColumn<>("Thành Tiền");
        colThanhTien.setCellValueFactory(p -> {
            int days = 1;
            try { days = Integer.parseInt(txtSoNgayThue.getText()); } catch(Exception ignored){}
            return new SimpleStringProperty(String.format("%,.0f đ", p.getValue().getGiaPhong() * days));
        });
        colThanhTien.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");

        tablePhong.getColumns().addAll(colMa, colGia, colThanhTien);
        box.getChildren().add(tablePhong);
        return box;
    }

    private VBox createTinhTienPanel(Stage stage) {
        VBox rightCol = new VBox(15);
        rightCol.setPrefWidth(320);

        VBox boxTien = createCardBox("💰 Bảng Tính Tiền");
        VBox rowBox = new VBox(12);

        lblTongTienPhongVal = createMoneyLabel();
        lblTongTienDichVuVal = createMoneyLabel();
        lblKhuyenMaiVal = createMoneyLabel(); lblKhuyenMaiVal.setTextFill(Color.web(COLOR_DANGER));

        lblTongThanhToanVal = new Label("0 đ");
        lblTongThanhToanVal.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        lblTongThanhToanVal.setTextFill(Color.web(COLOR_PRIMARY));

        rowBox.getChildren().addAll(
                createMoneyRow("Tiền phòng:", lblTongTienPhongVal),
                createMoneyRow("Tiền dịch vụ:", lblTongTienDichVuVal),
                createMoneyRow("Khuyến mãi:", lblKhuyenMaiVal),
                new Separator(),
                createMoneyRow("TỔNG CỘNG:", lblTongThanhToanVal)
        );
        boxTien.getChildren().add(rowBox);

        VBox btnBox = new VBox(10);
        Button btnLuuTT = new Button("💵 Lưu & Thanh Toán");
        btnLuuTT.setMaxWidth(Double.MAX_VALUE); btnLuuTT.setPrefHeight(40);
        btnLuuTT.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLuuTT.setOnAction(e -> xuLyLuu(true, stage));

        Button btnLuuCho = new Button("⏳ Lưu (Chờ Khách)");
        btnLuuCho.setMaxWidth(Double.MAX_VALUE); btnLuuCho.setPrefHeight(40);
        btnLuuCho.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLuuCho.setOnAction(e -> xuLyLuu(false, stage));

        btnBox.getChildren().addAll(btnLuuTT, btnLuuCho);
        rightCol.getChildren().addAll(boxTien, btnBox);
        return rightCol;
    }

    private void timKhachHang() {
        String sdt = txtSdt.getText().trim();
        if (sdt.isEmpty()) { showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập số điện thoại!"); return; }

        // Cần có hàm tìm KH theo SĐT trong service, tạm filter từ list
        try {
            KhachHangDTO found = khachHangService.getAllKhachHang().stream()
                    .filter(k -> sdt.equals(k.getSoDienThoai())).findFirst().orElse(null);
            if (found != null) {
                txtHoTen.setText(found.getHoTen());
                dpNgaySinh.setValue(found.getNgaySinh());
                cboLoaiKhach.setValue(found.getLoaiKhachHang());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Khách hàng mới. Vui lòng nhập thông tin!");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadPhongData() {
        try {
            List<PhongDTO> tatCaPhong = phongService.getAllPhong();
            phongList = FXCollections.observableArrayList();
            tongTienPhong = 0.0;
            for (String ma : danhSachMaPhong) {
                for (PhongDTO p : tatCaPhong) {
                    if (p.getMaPhong().equals(ma)) { phongList.add(p); tongTienPhong += p.getGiaPhong(); break; }
                }
            }
            tablePhong.setItems(phongList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void tinhSoNgayVaTien() {
        if (dpNgayDat.getValue() != null && dpNgayTra.getValue() != null) {
            long days = ChronoUnit.DAYS.between(dpNgayDat.getValue(), dpNgayTra.getValue());
            if (days < 1) days = 1;
            txtSoNgayThue.setText(String.valueOf(days));

            tablePhong.refresh(); // ✅ Refresh bảng để cập nhật lại cột Thành Tiền
            capNhatTongTien();
        }
    }

    private void capNhatTongTien() {
        int soNgay = 1;
        try { soNgay = Integer.parseInt(txtSoNgayThue.getText()); } catch (Exception e) { }

        double tienPhongHienTai = tongTienPhong * soNgay;
        double tongTruocKM = tienPhongHienTai + tongTienDichVu;
        tienKhuyenMai = 0;

        String km = cboKhuyenMai.getValue();
        if (km != null) {
            if(km.contains("10%")) tienKhuyenMai = tongTruocKM * 0.10;
            else if (km.contains("200k")) tienKhuyenMai = 200000;
        }

        tongThanhToan = Math.max(0, tongTruocKM - tienKhuyenMai);

        lblTongTienPhongVal.setText(String.format("%,.0f đ", tienPhongHienTai));
        lblKhuyenMaiVal.setText(String.format("-%,.0f đ", tienKhuyenMai));
        lblTongThanhToanVal.setText(String.format("%,.0f đ", tongThanhToan));
    }

    // =========================================================================
    // ✅ CẢI THIỆN: LOGIC LƯU DB CHUẨN VÀO HÓA ĐƠN VÀ PHIẾU
    // =========================================================================
    // =========================================================================
    // ✅ CẢI THIỆN: LOGIC LƯU DB KẾT HỢP GIAO DIỆN THANH TOÁN
    // =========================================================================
    // =========================================================================
    // ✅ CẢI THIỆN: LOGIC LƯU DB KẾT HỢP GIAO DIỆN THANH TOÁN & ĐỔI TRẠNG THÁI PHÒNG
    // =========================================================================
    private void xuLyLuu(boolean coThanhToan, Stage stage) {
        if (txtSdt.getText().trim().isEmpty() || txtHoTen.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập thông tin khách hàng!"); return;
        }

        // --- BƯỚC 1: THANH TOÁN ---
        String loaiThanhToanDB = "CHUA_THANH_TOAN";
        if (coThanhToan) {
            ThanhToanController paymentCtrl = new ThanhToanController(tongThanhToan);
            String resultMethod = paymentCtrl.showThanhToanDialog(stage);
            if (resultMethod == null) return;
            loaiThanhToanDB = resultMethod;
        }

        try {
            // 1. Xử lý Khách Hàng
            String sdt = txtSdt.getText().trim();
            KhachHangDTO kh = khachHangService.getAllKhachHang().stream()
                    .filter(k -> sdt.equals(k.getSoDienThoai())).findFirst().orElse(null);

            // Trong DatPhongController.java (Hàm xuLyLuu)

            if (kh == null) {
                kh = new KhachHangDTO();
                // Mã này giờ chỉ là mã tạm, Service của Tú sẽ ghi đè mã mới
                kh.setMaKhachHang("KH" + (System.currentTimeMillis() % 100000));
                kh.setSoDienThoai(sdt);
                kh.setHoTen(txtHoTen.getText());
                kh.setNgaySinh(dpNgaySinh.getValue() != null ? dpNgaySinh.getValue() : LocalDate.of(2000, 1, 1));
                kh.setLoaiKhachHang(cboLoaiKhach.getValue());

                // 👉 THỦ PHẠM ĐÂY: Bạn phải gán "kh =" để lấy lại cái DTO chứa mã ID thật từ Database
                kh = khachHangService.addKhachHang(kh);
            }

            int soNgay = Integer.parseInt(txtSoNgayThue.getText());
            String nextMaPhieu = phieuDatPhongService.phatSinhMaPhieuMoi();
            int soThuTuPhieu = Integer.parseInt(nextMaPhieu.substring(3));// Cắt lấy số 19

            // 2. Tạo Phiếu Đặt Phòng
            for (PhongDTO p : phongList) {
                PhieuDatPhongDTO phieu = new PhieuDatPhongDTO();
                phieu.setMaPhieu(String.format("PDP%03d", soThuTuPhieu++));
                phieu.setMaKhachHang(kh.getMaKhachHang());
                phieu.setMaPhong(p.getMaPhong());
                phieu.setNgayDat(LocalDate.now());
                phieu.setNgayNhan(dpNgayDat.getValue());
                phieu.setNgayTra(dpNgayTra.getValue());

                // 👉 FIX LỖI: Dùng mã nhân viên hợp lệ (5 ký tự và có trong DB)
                String maNV = (nhanVien != null) ? nhanVien.getMaNhanVien() : "NV001";
                phieu.setMaNhanVien(maNV);

                // 👉 FIX LỖI NULL: Phải gán tổng tiền
                phieu.setTongTien(p.getGiaPhong() * soNgay);

                if (coThanhToan) {
                    phieu.setTrangThai("DA_NHAN_PHONG");
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Đang ở");
                } else {
                    phieu.setTrangThai("CHO_NHAN_PHONG");
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Đã Đặt");
                }

                phieuDatPhongService.bookRoomTransaction(phieu);
            }

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu phiếu thành công!");

            // 👉 KÍCH HOẠT REFRESH: Báo cho màn hình Quản lý tải lại dữ liệu
            if (onRefresh != null) onRefresh.run();
            stage.close();

        } catch (Exception e) {
            System.err.println("--- LỖI LƯU DỮ LIỆU ---");
            e.printStackTrace(); // Tú xem lỗi đỏ ở Console nếu vẫn không lưu được
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu: " + e.getMessage());
        }
    }

    // Helpers UI
    private VBox createCardBox(String title) {
        VBox box = new VBox(12);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 18; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10;");
        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY));
        box.getChildren().add(lblTitle);
        DropShadow shadow = new DropShadow(); shadow.setColor(Color.web("#000000", 0.03)); shadow.setRadius(8); shadow.setOffsetY(3);
        box.setEffect(shadow);
        return box;
    }

    private VBox createInputBox(String labelText, javafx.scene.Node inputControl) {
        VBox box = new VBox(4);
        Label lbl = new Label(labelText); lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12)); lbl.setTextFill(Color.web(COLOR_TEXT_MUTED));
        box.getChildren().addAll(lbl, inputControl);
        return box;
    }

    private Label createMoneyLabel() {
        Label lbl = new Label("0 đ");
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lbl.setTextFill(Color.web(COLOR_TEXT_MAIN));
        return lbl;
    }

    private HBox createMoneyRow(String text, Label valueLabel) {
        HBox box = new HBox();
        Label lbl = new Label(text); lbl.setFont(Font.font("Segoe UI", 13)); lbl.setTextFill(Color.web(COLOR_TEXT_MUTED));
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().addAll(lbl, spacer, valueLabel);
        return box;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }
}