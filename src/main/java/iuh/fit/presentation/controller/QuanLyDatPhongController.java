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
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.concurrent.Task;
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

    // UI Components
    private DatePicker dpCheckIn;
    private DatePicker dpCheckOut;
    private FlowPane pnlRoomMap;
    private ListView<PhongDTO> lvSelectedRooms;
    private List<PhongDTO> selectedRoomsList;
    private Label lblTongTien;

    // --- BẢNG MÀU HIỆN ĐẠI ---
    private final String COLOR_PRIMARY = "#2563eb"; // Blue 600
    private final String COLOR_PRIMARY_HOVER = "#1d4ed8"; // Blue 700
    private final String COLOR_BG_LIGHT = "#f1f5f9"; // Slate 100
    private final String COLOR_CARD_BG = "white";
    private final String COLOR_AVAILABLE = "#10b981"; // Emerald 500
    private final String COLOR_SELECTED_BG = "#eff6ff"; // Blue 50
    private final String COLOR_TEXT_MAIN = "#1e293b"; // Slate 800
    private final String COLOR_TEXT_MUTED = "#64748b"; // Slate 500
    private final String COLOR_BORDER = "#cbd5e1"; // Slate 300

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
        root.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        // Đổ bóng chung cho các Panel
        DropShadow panelShadow = new DropShadow();
        panelShadow.setColor(Color.color(0, 0, 0, 0.05));
        panelShadow.setRadius(10);
        panelShadow.setOffsetY(3);

        // ==========================================
        // 1. BỘ LỌC TÌM KIẾM PHÒNG TRỐNG (TOP)
        // ==========================================
        HBox filterBox = new HBox(20);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(15, 25, 15, 25));
        filterBox.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-border-width: 0 0 1px 0; -fx-border-color: #e2e8f0;");
        filterBox.setEffect(panelShadow);

        Label lblTitle = new Label("ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY));

        VBox inBox = new VBox(5);
        Label lblIn = new Label("Ngày Nhận");
        lblIn.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_TEXT_MUTED + ";");
        dpCheckIn = new DatePicker(LocalDate.now());
        dpCheckIn.setStyle("-fx-font-size: 13px;");
        dpCheckIn.setEditable(false);
        inBox.getChildren().addAll(lblIn, dpCheckIn);

        VBox outBox = new VBox(5);
        Label lblOut = new Label("Ngày Trả");
        lblOut.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_TEXT_MUTED + ";");
        dpCheckOut = new DatePicker(LocalDate.now().plusDays(1));
        dpCheckOut.setStyle("-fx-font-size: 13px;");
        dpCheckOut.setEditable(false);
        outBox.getChildren().addAll(lblOut, dpCheckOut);

        Button btnTimPhong = new Button("🔍 TÌM PHÒNG TRỐNG");
        btnTimPhong.setCursor(Cursor.HAND);
        btnTimPhong.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20;");
        btnTimPhong.setOnMouseEntered(e -> btnTimPhong.setStyle("-fx-background-color: " + COLOR_PRIMARY_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20;"));
        btnTimPhong.setOnMouseExited(e -> btnTimPhong.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20;"));
        btnTimPhong.setOnAction(e -> loadRoomsToMap());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBox.getChildren().addAll(lblTitle, spacer, inBox, outBox, btnTimPhong);

        // Bọc filterBox để tạo margin
        VBox topWrapper = new VBox(filterBox);
        topWrapper.setPadding(new Insets(0, 0, 15, 0));
        root.setTop(topWrapper);

        // ==========================================
        // 2. SƠ ĐỒ PHÒNG (CENTER)
        // ==========================================
        pnlRoomMap = new FlowPane();
        pnlRoomMap.setHgap(15);
        pnlRoomMap.setVgap(15);
        pnlRoomMap.setPadding(new Insets(15));

        ScrollPane scrollMap = new ScrollPane(pnlRoomMap);
        scrollMap.setFitToWidth(true);
        scrollMap.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");
        scrollMap.setBorder(Border.EMPTY);

        root.setCenter(scrollMap);

        // ==========================================
        // 3. FORM LẬP PHIẾU ĐẶT PHÒNG (RIGHT)
        // ==========================================
        VBox rightPanel = new VBox(20);
        rightPanel.setPrefWidth(380);
        rightPanel.setPadding(new Insets(25));
        rightPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12 0 0 12;");

        DropShadow leftShadow = new DropShadow();
        leftShadow.setColor(Color.color(0, 0, 0, 0.08));
        leftShadow.setRadius(15);
        leftShadow.setOffsetX(-5);
        rightPanel.setEffect(leftShadow);

        Label lblCart = new Label("THÔNG TIN ĐẶT PHÒNG");
        lblCart.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblCart.setTextFill(Color.web(COLOR_TEXT_MAIN));

        // Form Khách Hàng
        String inputStyle = "-fx-padding: 10; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc;";

        VBox khBox = new VBox(8);
        Label lblSDT = new Label("Số điện thoại:");
        lblSDT.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        TextField txtSDT = new TextField();
        txtSDT.setPromptText("Nhập số điện thoại...");
        txtSDT.setStyle(inputStyle);

        Button btnTimKH = new Button("Tìm");
        btnTimKH.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 15; -fx-cursor: hand;");

        HBox sdtRow = new HBox(10, txtSDT, btnTimKH);
        HBox.setHgrow(txtSDT, Priority.ALWAYS);

        Label lblTen = new Label("Họ tên khách hàng:");
        lblTen.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        TextField txtTenKH = new TextField();
        txtTenKH.setPromptText("Nhập họ tên...");
        txtTenKH.setStyle(inputStyle);

        khBox.getChildren().addAll(lblSDT, sdtRow, lblTen, txtTenKH);

        // Danh sách phòng chọn
        VBox cartBox = new VBox(8);
        Label lblSelected = new Label("Phòng đang chọn:");
        lblSelected.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        lvSelectedRooms = new ListView<>();
        lvSelectedRooms.setPrefHeight(180);
        lvSelectedRooms.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #e2e8f0; -fx-control-inner-background: #f8fafc;");

        // Custom Cell để hiển thị Đẹp trong Giỏ hàng
        lvSelectedRooms.setCellFactory(param -> new ListCell<PhongDTO>() {
            @Override
            protected void updateItem(PhongDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    HBox box = new HBox();
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.setPadding(new Insets(8, 5, 8, 5));

                    VBox info = new VBox(2);
                    Label lMa = new Label("Phòng " + item.getMaPhong());
                    lMa.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                    Label lLoai = new Label(item.getMaLoaiPhong());
                    lLoai.setFont(Font.font(11));
                    lLoai.setTextFill(Color.web(COLOR_TEXT_MUTED));
                    info.getChildren().addAll(lMa, lLoai);

                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);

                    Label lGia = new Label(String.format("%,.0fđ", item.getGiaPhong()));
                    lGia.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    lGia.setTextFill(Color.web(COLOR_PRIMARY));

                    box.getChildren().addAll(info, sp, lGia);
                    setGraphic(box);
                    setStyle("-fx-border-width: 0 0 1px 0; -fx-border-color: #e2e8f0;");
                }
            }
        });
        cartBox.getChildren().addAll(lblSelected, lvSelectedRooms);

        // Tổng Tiền
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        priceBox.setPadding(new Insets(15, 10, 15, 10));
        priceBox.setStyle("-fx-background-color: #fffbeb; -fx-border-color: #fde68a; -fx-border-radius: 8; -fx-background-radius: 8;"); // Vàng nhạt nổi bật

        Label lblTextTien = new Label("TỔNG TẠM TÍNH:");
        lblTextTien.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblTextTien.setTextFill(Color.web("#d97706")); // Amber 600

        Region pSpacer = new Region();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);

        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
        lblTongTien.setTextFill(Color.web("#b45309")); // Amber 700
        priceBox.getChildren().addAll(lblTextTien, pSpacer, lblTongTien);

        // Nút Lưu Phiếu
        Button btnXacNhanDat = new Button("TẠO PHIẾU ĐẶT PHÒNG");
        btnXacNhanDat.setMaxWidth(Double.MAX_VALUE);
        btnXacNhanDat.setCursor(Cursor.HAND);
        btnXacNhanDat.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15px; -fx-font-size: 15px; -fx-background-radius: 8;");
        btnXacNhanDat.setOnMouseEntered(e -> btnXacNhanDat.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15px; -fx-font-size: 15px; -fx-background-radius: 8;"));
        btnXacNhanDat.setOnMouseExited(e -> btnXacNhanDat.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15px; -fx-font-size: 15px; -fx-background-radius: 8;"));
        btnXacNhanDat.setOnAction(e -> handleBookRooms(txtSDT.getText(), txtTenKH.getText()));

        rightPanel.getChildren().addAll(
                lblCart,
                khBox,
                cartBox,
                priceBox,
                btnXacNhanDat
        );

        root.setRight(rightPanel);
        BorderPane.setMargin(rightPanel, new Insets(0, 0, 0, 15));

        loadRoomsToMap();

        return new Scene(root);
    }

    private void loadRoomsToMap() {
        pnlRoomMap.getChildren().clear();
        selectedRoomsList.clear();
        updateCartUI();

        LocalDate in = dpCheckIn.getValue();
        LocalDate out = dpCheckOut.getValue();

        if (in != null && out != null && out.isAfter(in)) {
            Task<List<PhongDTO>> loadTask = new Task<>() {
                @Override
                protected List<PhongDTO> call() {
                    return phongService.findAvailableRooms(in, out, 0, 99000000);
                }
            };

            loadTask.setOnSucceeded(evt -> {
                List<PhongDTO> dsPhongTrong = loadTask.getValue();

                for (PhongDTO p : dsPhongTrong) {
                    // Tạo thẻ Card
                    VBox card = new VBox(5);
                    card.setPrefSize(130, 100);
                    card.setAlignment(Pos.CENTER);

                    DropShadow cardShadow = new DropShadow();
                    cardShadow.setColor(Color.color(0, 0, 0, 0.06));
                    cardShadow.setRadius(5);
                    cardShadow.setOffsetY(3);
                    card.setEffect(cardShadow);

                    String defaultStyle = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 4 1 1 1; -fx-border-color: " + COLOR_AVAILABLE + " #e2e8f0 #e2e8f0 #e2e8f0; -fx-cursor: hand;";
                    String selectedStyle = "-fx-background-color: " + COLOR_SELECTED_BG + "; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 4 1 1 1; -fx-border-color: " + COLOR_PRIMARY + " " + COLOR_PRIMARY + " " + COLOR_PRIMARY + " " + COLOR_PRIMARY + "; -fx-cursor: hand;";

                    card.setStyle(defaultStyle);

                    Label lblMa = new Label(p.getMaPhong());
                    lblMa.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
                    lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

                    Label lblLoai = new Label(p.getMaLoaiPhong());
                    lblLoai.setFont(Font.font("Segoe UI", 12));
                    lblLoai.setTextFill(Color.web(COLOR_TEXT_MUTED));

                    Label lblGia = new Label(String.format("%,.0fđ", p.getGiaPhong()));
                    lblGia.setTextFill(Color.web(COLOR_PRIMARY));
                    lblGia.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                    card.getChildren().addAll(lblMa, lblLoai, lblGia);

                    // Hiệu ứng Hover nâng thẻ
                    card.setOnMouseEntered(e -> card.setTranslateY(-3));
                    card.setOnMouseExited(e -> card.setTranslateY(0));

                    // Click chọn phòng
                    card.setOnMouseClicked(e -> {
                        if (selectedRoomsList.contains(p)) {
                            selectedRoomsList.remove(p);
                            card.setStyle(defaultStyle);
                        } else {
                            selectedRoomsList.add(p);
                            card.setStyle(selectedStyle);
                        }
                        updateCartUI();
                    });

                    pnlRoomMap.getChildren().add(card);
                }

                if (dsPhongTrong.isEmpty()) {
                    Label emptyLbl = new Label("Không có phòng trống trong khoảng thời gian này.");
                    emptyLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-padding: 20;");
                    pnlRoomMap.getChildren().add(emptyLbl);
                }
            });

            loadTask.setOnFailed(evt -> {
                Throwable ex = loadTask.getException();
                Label emptyLbl = new Label("Lỗi tải danh sách phòng: " + (ex != null ? ex.getMessage() : "không xác định"));
                emptyLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #ef4444; -fx-padding: 20;");
                pnlRoomMap.getChildren().add(emptyLbl);
            });

            Thread worker = new Thread(loadTask, "booking-room-load-task");
            worker.setDaemon(true);
            worker.start();
        }
    }

    private void updateCartUI() {
        lvSelectedRooms.getItems().setAll(selectedRoomsList);

        double total = 0;
        for (PhongDTO p : selectedRoomsList) {
            total += p.getGiaPhong();
        }

        long days = ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
        if (days < 1) days = 1;
        total = total * days;

        lblTongTien.setText(String.format("%,.0f đ", total));
    }

    private void handleBookRooms(String sdt, String tenKH) {
        if (selectedRoomsList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ít nhất 1 phòng trên sơ đồ!");
            return;
        }
        if (tenKH == null || tenKH.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập tên Khách hàng!");
            return;
        }

        /*
         * LƯU Ý GÓC ĐỘ LOGIC: Đoạn code gốc của bạn hiện chỉ lấy selectedRoomsList.get(0).
         * Nghĩa là cho dù người dùng chọn 5 phòng, hệ thống cũng chỉ tạo 1 phiếu cho phòng đầu tiên.
         * Để an toàn trong UI, tôi thêm cảnh báo này nếu họ chọn > 1 phòng.
         */
        if (selectedRoomsList.size() > 1) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Hệ thống hiện tại chỉ hỗ trợ đặt 1 phòng cho mỗi phiếu. Phòng [" + selectedRoomsList.get(0).getMaPhong() + "] sẽ được ưu tiên đặt trước.");
        }

        KhachHangDTO khDTO = khachHangService.getKhachHangBySoDienThoai(sdt);
        if (khDTO == null) {
            khDTO = new KhachHangDTO();
            khDTO.setHoTen(tenKH);
            khDTO.setSoDienThoai(sdt);
            khDTO.setLoaiKhachHang("KHACH_MOI");
            try {
                khDTO = khachHangService.addKhachHang(khDTO);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo khách hàng: " + e.getMessage());
                return;
            }
        }

        PhieuDatPhongDTO pdpDTO = new PhieuDatPhongDTO();
        pdpDTO.setMaPhieu("PDP_" + System.currentTimeMillis());
        pdpDTO.setMaKhachHang(khDTO.getMaKhachHang());
        pdpDTO.setMaPhong(selectedRoomsList.get(0).getMaPhong());
        pdpDTO.setNgayDat(LocalDate.now());
        pdpDTO.setNgayNhan(dpCheckIn.getValue());
        pdpDTO.setNgayTra(dpCheckOut.getValue());
        pdpDTO.setTrangThai("CHO_NHAN_PHONG");
        pdpDTO.setMaNhanVien(currentUser.getMaNhanVien());

        long days = ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
        if (days < 1) days = 1;
        double tongTien = selectedRoomsList.get(0).getGiaPhong() * days;
        pdpDTO.setTongTien(tongTien);

        boolean success = phieuDatPhongService.bookRoomTransaction(pdpDTO);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tạo Phiếu Đặt Phòng thành công!");
            loadRoomsToMap();
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Có lỗi xảy ra khi lưu phiếu đặt phòng.");
        }
    }

    // Hàm tiện ích để hiển thị thông báo đẹp hơn chút
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}