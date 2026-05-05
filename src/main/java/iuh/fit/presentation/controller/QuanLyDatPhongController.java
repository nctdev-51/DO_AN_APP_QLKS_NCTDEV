package iuh.fit.presentation.controller;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class QuanLyDatPhongController {

    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IKhachHangService khachHangService;
    private TaiKhoanDTO currentUser;

    // UI Components lọc
    private DatePicker dpCheckIn;
    private DatePicker dpCheckOut;

    // Sơ đồ phòng
    private FlowPane pnlRoomMap;

    // Giỏ hàng phòng đã chọn
    private ListView<PhongDTO> lvSelectedRooms;
    private List<PhongDTO> selectedRoomsList;

    // Label tổng tiền dự kiến
    private Label lblTongTien;

    public QuanLyDatPhongController(IPhongService phongService,
                                    IPhieuDatPhongService phieuDatPhongService,
                                    IKhachHangService khachHangService,
                                    TaiKhoanDTO currentUser) {
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.khachHangService = khachHangService;
        this.currentUser = currentUser;
        this.selectedRoomsList = new ArrayList<>();
    }

    public Scene createQuanLyDatPhongScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: white;");

        // ==========================================
        // 1. BỘ LỌC TÌM KIẾM PHÒNG TRỐNG (TOP)
        // ==========================================
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(10, 10, 20, 10));
        filterBox.setStyle("-fx-border-width: 0 0 1px 0; -fx-border-color: #ddd;");

        Label lblTitle = new Label("TÌM & ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitle.setTextFill(Color.web("#2980b9"));

        dpCheckIn = new DatePicker(LocalDate.now());
        dpCheckOut = new DatePicker(LocalDate.now().plusDays(1));

        Button btnTimPhong = new Button("🔍 Lọc Phòng Trống");
        btnTimPhong.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnTimPhong.setOnAction(e -> loadRoomsToMap());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBox.getChildren().addAll(lblTitle, spacer, new Label("Ngày Nhận:"), dpCheckIn, new Label("Ngày Trả:"), dpCheckOut, btnTimPhong);
        root.setTop(filterBox);

        // ==========================================
        // 2. SƠ ĐỒ PHÒNG (CENTER)
        // ==========================================
        pnlRoomMap = new FlowPane();
        pnlRoomMap.setHgap(15);
        pnlRoomMap.setVgap(15);
        pnlRoomMap.setPadding(new Insets(20));

        ScrollPane scrollMap = new ScrollPane(pnlRoomMap);
        scrollMap.setFitToWidth(true);
        scrollMap.setStyle("-fx-background-color: transparent; -fx-background: white;");

        root.setCenter(scrollMap);

        // ==========================================
        // 3. FORM LẬP PHIẾU ĐẶT PHÒNG (RIGHT)
        // ==========================================
        VBox rightPanel = new VBox(15);
        rightPanel.setPrefWidth(350);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 0 1px;");

        Label lblCart = new Label("THÔNG TIN ĐẶT PHÒNG");
        lblCart.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Thông tin khách hàng
        TextField txtSDT = new TextField();
        txtSDT.setPromptText("Nhập Số Điện Thoại");
        Button btnTimKH = new Button("Tìm");
        HBox khBox = new HBox(5, txtSDT, btnTimKH);

        TextField txtTenKH = new TextField();
        txtTenKH.setPromptText("Nhập Họ tên khách hàng");

        // Danh sách phòng đang chọn
        Label lblSelected = new Label("Phòng đang chọn:");
        lvSelectedRooms = new ListView<>();
        lvSelectedRooms.setPrefHeight(150);
        // Custom Cell để hiển thị Đẹp
        lvSelectedRooms.setCellFactory(param -> new ListCell<PhongDTO>() {
            @Override
            protected void updateItem(PhongDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getMaPhong() + " - " + item.getMaLoaiPhong() + " - " + item.getGiaPhong() + "đ");
                }
            }
        });

        // Tính Tiền
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblTongTien.setTextFill(Color.RED);
        priceBox.getChildren().addAll(new Label("Tạm tính:"), lblTongTien);

        // Nút Lưu Phiếu
        Button btnXacNhanDat = new Button("✅ TẠO PHIẾU ĐẶT PHÒNG");
        btnXacNhanDat.setMaxWidth(Double.MAX_VALUE);
        btnXacNhanDat.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12px; -fx-font-size: 14px;");
        btnXacNhanDat.setOnAction(e -> handleBookRooms(txtSDT.getText(), txtTenKH.getText()));

        rightPanel.getChildren().addAll(
                lblCart,
                new Label("Số ĐT Khách:"), khBox,
                new Label("Tên Khách:"), txtTenKH,
                new Separator(),
                lblSelected, lvSelectedRooms,
                new Separator(),
                priceBox,
                btnXacNhanDat
        );

        root.setRight(rightPanel);

        // Load phòng khi mở form
        loadRoomsToMap();

        return new Scene(root);
    }

    /**
     * Tải danh sách phòng trống từ Database và vẽ Sơ đồ (Màu xanh/Vàng)
     */
    private void loadRoomsToMap() {
        pnlRoomMap.getChildren().clear();
        selectedRoomsList.clear();
        updateCartUI();

        LocalDate in = dpCheckIn.getValue();
        LocalDate out = dpCheckOut.getValue();

        if (in != null && out != null && out.isAfter(in)) {
            // Lấy từ Service (Hàm findAvailableRooms mà chúng ta đã cấu hình trong Interface/Impl)
            List<PhongDTO> dsPhongTrong = phongService.findAvailableRooms(in, out, 0, 99000000);

            for (PhongDTO p : dsPhongTrong) {
                // Tạo một Card Phòng nhỏ
                VBox card = new VBox(5);
                card.setPrefSize(100, 90);
                card.setAlignment(Pos.CENTER);
                card.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 5px; -fx-cursor: hand;");

                Label lblMa = new Label(p.getMaPhong());
                lblMa.setFont(Font.font("System", FontWeight.BOLD, 16));
                lblMa.setTextFill(Color.WHITE);

                Label lblLoai = new Label(p.getMaLoaiPhong());
                lblLoai.setTextFill(Color.WHITE);

                Label lblGia = new Label(p.getGiaPhong() + "đ");
                lblGia.setTextFill(Color.web("#f1c40f")); // Vàng
                lblGia.setFont(Font.font("System", FontWeight.BOLD, 12));

                card.getChildren().addAll(lblMa, lblLoai, lblGia);

                // --- LOGIC CLICK CHỌN PHÒNG ---
                card.setOnMouseClicked(e -> {
                    if (selectedRoomsList.contains(p)) {
                        // Bỏ chọn
                        selectedRoomsList.remove(p);
                        card.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 5px; -fx-cursor: hand;"); // Xanh lá
                    } else {
                        // Chọn
                        selectedRoomsList.add(p);
                        card.setStyle("-fx-background-color: #f39c12; -fx-background-radius: 5px; -fx-cursor: hand;"); // Cam vàng
                    }
                    updateCartUI();
                });

                pnlRoomMap.getChildren().add(card);
            }

            if (dsPhongTrong.isEmpty()) {
                pnlRoomMap.getChildren().add(new Label("Không có phòng trống trong khoảng thời gian này."));
            }
        }
    }

    /**
     * Cập nhật Giỏ hàng (ListView) bên phải và tính tổng tiền
     */
    private void updateCartUI() {
        lvSelectedRooms.getItems().setAll(selectedRoomsList);

        double total = 0;
        for (PhongDTO p : selectedRoomsList) {
            total += p.getGiaPhong();
        }

        // Tính theo số ngày
        long days = java.time.temporal.ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
        if (days < 1) days = 1;
        total = total * days;

        lblTongTien.setText(String.format("%,.0f đ", total));
    }

    private void handleBookRooms(String sdt, String tenKH) {
        if (selectedRoomsList.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ít nhất 1 phòng trên sơ đồ!");
            a.show();
            return;
        }
        if (tenKH == null || tenKH.trim().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập tên Khách hàng!");
            a.show();
            return;
        }

        // Tìm hoặc tạo khách hàng
        KhachHangDTO khDTO = khachHangService.getKhachHangBySoDienThoai(sdt);
        if (khDTO == null) {
            khDTO = new KhachHangDTO();
            khDTO.setHoTen(tenKH);
            khDTO.setSoDienThoai(sdt);
            khDTO.setLoaiKhachHang("KHACH_MOI");
            try {
                khDTO = khachHangService.addKhachHang(khDTO);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Không thể tạo khách hàng: " + e.getMessage()).show();
                return;
            }
        }

        // Tạo DTO Phiếu Đặt Phòng (hiện tại chỉ hỗ trợ 1 phòng/phiếu)
        PhieuDatPhongDTO pdpDTO = new PhieuDatPhongDTO();
        pdpDTO.setMaPhieu("PDP_" + System.currentTimeMillis());
        pdpDTO.setMaKhachHang(khDTO.getMaKhachHang());
        pdpDTO.setMaPhong(selectedRoomsList.get(0).getMaPhong()); // lấy phòng đầu tiên
        pdpDTO.setNgayDat(LocalDate.now());
        pdpDTO.setNgayNhan(dpCheckIn.getValue());
        pdpDTO.setNgayTra(dpCheckOut.getValue());
        pdpDTO.setTrangThai("CHO_NHAN_PHONG");
        pdpDTO.setMaNhanVien(currentUser.getMaNhanVien());

        // Tính tổng tiền
        long days = ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
        if (days < 1) days = 1;
        double tongTien = selectedRoomsList.get(0).getGiaPhong() * days;
        pdpDTO.setTongTien(tongTien);

        boolean success = phieuDatPhongService.bookRoomTransaction(pdpDTO);

        if (success) {
            new Alert(Alert.AlertType.INFORMATION, "Tạo Phiếu Đặt Phòng thành công!").show();
            loadRoomsToMap();
        } else {
            new Alert(Alert.AlertType.ERROR, "Có lỗi xảy ra khi lưu phiếu đặt phòng.").show();
        }
    }
}