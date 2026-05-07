package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class LapPhieuDatPhongController {

    private IPhieuDatPhongService phieuDatPhongService;
    private IKhachHangService khachHangService;
    private IDichVuService dichVuService;
    private TaiKhoanDTO currentUser;
    private Runnable onBackToHome;

    private List<PhongDTO> selectedRooms;
    private LocalDate checkIn, checkOut;
    private long totalDays;

    // UI Components
    private TextField txtSDT, txtHoTen, txtSearchDV;
    private ComboBox<String> cbLoaiKhach;
    private FlowPane pnlServiceMenu;
    private VBox pnlServiceCart;
    private Label lblTienPhong, lblTienDichVu, lblTongCong;

    // Data
    private ObservableList<CartItem> serviceCart = FXCollections.observableArrayList();
    private List<DichVuDTO> allServices;

    // Màu sắc
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_TEXT_MAIN = "#1e293b";

    public LapPhieuDatPhongController(IPhieuDatPhongService phieuDatPhongService,
                                      IKhachHangService khachHangService, IDichVuService dichVuService,
                                      TaiKhoanDTO currentUser, List<PhongDTO> selectedRooms,
                                      LocalDate checkIn, LocalDate checkOut, Runnable onBackToHome) {
        this.phieuDatPhongService = phieuDatPhongService;
        this.khachHangService = khachHangService;
        this.dichVuService = dichVuService;
        this.currentUser = currentUser;
        this.selectedRooms = selectedRooms;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.onBackToHome = onBackToHome;
        this.totalDays = Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
    }

    public BorderPane createLapPhieuView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 15;");

        // --- HEADER ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Button btnBack = new Button("⬅ Quay Lại");
        btnBack.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 5; -fx-cursor: hand;");
        btnBack.setOnAction(e -> onBackToHome.run());
        Label lblTitle = new Label("LẬP PHIẾU ĐẶT PHÒNG & DỊCH VỤ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        header.getChildren().addAll(btnBack, lblTitle);
        root.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 15, 0));

        // --- MAIN CONTENT (3 CỘT) ---
        HBox mainContent = new HBox(15);

        // CỘT 1: KHÁCH HÀNG & PHÒNG (BÊN TRÁI)
        VBox colLeft = buildLeftColumn();
        colLeft.setPrefWidth(320);

        // CỘT 2: MENU DỊCH VỤ (Ở GIỮA - RỘNG NHẤT)
        VBox colCenter = buildCenterColumn();
        HBox.setHgrow(colCenter, Priority.ALWAYS);

        // CỘT 3: GIỎ HÀNG DỊCH VỤ (BÊN PHẢI)
        VBox colRight = buildRightColumn();
        colRight.setPrefWidth(320);

        mainContent.getChildren().addAll(colLeft, colCenter, colRight);
        root.setCenter(mainContent);

        // Load dữ liệu ban đầu
        try {
            allServices = dichVuService.getAllDichVu();
            renderServiceMenu(allServices);
        } catch (Exception e) { e.printStackTrace(); }

        return root;
    }

    private VBox buildLeftColumn() {
        VBox col = new VBox(15);

        // Form Khách hàng
        VBox boxKhach = new VBox(10);
        boxKhach.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        Label lblHeader = new Label("👤 KHÁCH HÀNG"); lblHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        txtSDT = new TextField(); txtSDT.setPromptText("Số điện thoại...");
        txtSDT.textProperty().addListener((obs, old, newVal) -> { if(newVal.length() >= 10) timKhachHang(); });

        txtHoTen = new TextField(); txtHoTen.setPromptText("Họ và tên...");
        cbLoaiKhach = new ComboBox<>(FXCollections.observableArrayList("KHACH_MOI", "KHACH_THUONG_XUYEN", "KHACH_HOI_VIEN"));
        cbLoaiKhach.setValue("KHACH_MOI");
        cbLoaiKhach.setMaxWidth(Double.MAX_VALUE);

        boxKhach.getChildren().addAll(lblHeader, new Label("SĐT:"), txtSDT, new Label("Họ tên:"), txtHoTen, new Label("Loại:"), cbLoaiKhach);

        // Thông tin phòng đã chọn
        VBox boxPhong = new VBox(10);
        boxPhong.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        Label lblHeaderP = new Label("🏨 PHÒNG ĐÃ CHỌN"); lblHeaderP.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        ListView<String> lv = new ListView<>();
        lv.setPrefHeight(150);
        double roomTotal = 0;
        for(PhongDTO p : selectedRooms) {
            lv.getItems().add("P." + p.getMaPhong() + " - " + String.format("%,.0f đ", p.getGiaPhong()));
            roomTotal += p.getGiaPhong();
        }
        double totalRoomPriceFinal = roomTotal * totalDays;

        boxPhong.getChildren().addAll(lblHeaderP, new Label("Thời gian: " + totalDays + " ngày"), lv);

        col.getChildren().addAll(boxKhach, boxPhong);
        return col;
    }

    private VBox buildCenterColumn() {
        VBox col = new VBox(15);
        col.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label("🍽️ CHỌN DỊCH VỤ THÊM"); lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        txtSearchDV = new TextField(); txtSearchDV.setPromptText("Tìm món ăn, đồ uống...");
        txtSearchDV.setPrefWidth(250);
        txtSearchDV.textProperty().addListener((obs, old, newVal) -> filterServices(newVal));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        searchBox.getChildren().addAll(lbl, sp, txtSearchDV);

        pnlServiceMenu = new FlowPane(15, 15);
        ScrollPane scroll = new ScrollPane(pnlServiceMenu);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");

        col.getChildren().addAll(searchBox, new Separator(), scroll);
        return col;
    }

    private VBox buildRightColumn() {
        VBox col = new VBox(15);
        col.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

        Label lbl = new Label("🛒 DỊCH VỤ ĐÃ CHỌN"); lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        pnlServiceCart = new VBox(8);
        ScrollPane scroll = new ScrollPane(pnlServiceCart);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Khung Thanh Toán
        VBox boxPay = new VBox(10);
        boxPay.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8;");

        lblTienPhong = new Label("Tiền phòng: 0 đ");
        lblTienDichVu = new Label("Tiền dịch vụ: 0 đ");
        lblTongCong = new Label("0 đ");
        lblTongCong.setFont(Font.font("Segoe UI", FontWeight.BLACK, 24));
        lblTongCong.setTextFill(Color.web(COLOR_SUCCESS));

        Button btnXacNhan = new Button("XÁC NHẬN ĐẶT PHÒNG");
        btnXacNhan.setMaxWidth(Double.MAX_VALUE);
        btnXacNhan.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-font-size: 16px; -fx-cursor: hand;");
        btnXacNhan.setOnAction(e -> handleConfirmBooking());

        boxPay.getChildren().addAll(lblTienPhong, lblTienDichVu, new Separator(), new Label("TỔNG CỘNG:"), lblTongCong, btnXacNhan);

        col.getChildren().addAll(lbl, scroll, boxPay);

        // Tính tiền phòng lần đầu
        updatePriceDisplay();

        return col;
    }

    private void renderServiceMenu(List<DichVuDTO> list) {
        pnlServiceMenu.getChildren().clear();
        for (DichVuDTO dv : list) {
            VBox card = new VBox(8);
            card.setPrefSize(140, 180);
            card.setPadding(new Insets(10));
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;");

            // Hover effect
            card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f0f7ff; -fx-border-color: " + COLOR_PRIMARY + "; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;"));
            card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand;"));

            // Hình ảnh
            StackPane imgBox = new StackPane();
            imgBox.setPrefSize(100, 80);
            imgBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8;");

            // Tìm ảnh thật hoặc dùng Emoji
            Label icon = new Label("🍽️"); icon.setFont(Font.font(35));
            imgBox.getChildren().add(icon);

            Label name = new Label(dv.getTenDichVu());
            name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            name.setTextAlignment(TextAlignment.CENTER);
            name.setWrapText(true);

            Label price = new Label(String.format("%,.0f đ", dv.getGiaTien()));
            price.setTextFill(Color.web(COLOR_PRIMARY));
            price.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

            card.getChildren().addAll(imgBox, name, price);
            card.setOnMouseClicked(e -> addToCart(dv));
            pnlServiceMenu.getChildren().add(card);
        }
    }

    private void addToCart(DichVuDTO dv) {
        Optional<CartItem> exist = serviceCart.stream().filter(i -> i.dto.getMaDichVu().equals(dv.getMaDichVu())).findFirst();
        if (exist.isPresent()) {
            exist.get().qty++;
        } else {
            serviceCart.add(new CartItem(dv, 1));
        }
        renderCartUI();
    }

    private void renderCartUI() {
        pnlServiceCart.getChildren().clear();
        for (CartItem item : serviceCart) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 8; -fx-background-radius: 8;");

            VBox info = new VBox(2);
            Label n = new Label(item.dto.getTenDichVu()); n.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            Label p = new Label(String.format("%,.0f đ", item.dto.getGiaTien())); p.setFont(Font.font(10));
            info.getChildren().addAll(n, p);

            Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);

            Button btnSub = new Button("-"); btnSub.setOnAction(e -> { item.qty--; if(item.qty<=0) serviceCart.remove(item); renderCartUI(); });
            Label lblQty = new Label(String.valueOf(item.qty)); lblQty.setPrefWidth(20); lblQty.setAlignment(Pos.CENTER);
            Button btnAdd = new Button("+"); btnAdd.setOnAction(e -> { item.qty++; renderCartUI(); });

            row.getChildren().addAll(info, s, btnSub, lblQty, btnAdd);
            pnlServiceCart.getChildren().add(row);
        }
        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        double roomTotal = selectedRooms.stream().mapToDouble(PhongDTO::getGiaPhong).sum() * totalDays;
        double svTotal = serviceCart.stream().mapToDouble(i -> i.dto.getGiaTien() * i.qty).sum();

        lblTienPhong.setText(String.format("Tiền phòng (%d ngày): %,.0f đ", totalDays, roomTotal));
        lblTienDichVu.setText(String.format("Tiền dịch vụ: %,.0f đ", svTotal));
        lblTongCong.setText(String.format("%,.0f đ", roomTotal + svTotal));
    }

    private void timKhachHang() {
        try {
            KhachHangDTO kh = khachHangService.getKhachHangBySoDienThoai(txtSDT.getText().trim());
            if (kh != null) {
                txtHoTen.setText(kh.getHoTen());
                cbLoaiKhach.setValue(kh.getLoaiKhachHang());
            }
        } catch (Exception e) {}
    }

    private void filterServices(String kw) {
        if(kw == null || kw.isEmpty()) renderServiceMenu(allServices);
        else renderServiceMenu(allServices.stream().filter(d -> d.getTenDichVu().toLowerCase().contains(kw.toLowerCase())).toList());
    }

    private void handleConfirmBooking() {
        try {
            // 1. Xử lý khách hàng (như cũ)
            KhachHangDTO kh = null;
            if (!txtSDT.getText().isEmpty() && !txtHoTen.getText().isEmpty()) {
                kh = khachHangService.getKhachHangBySoDienThoai(txtSDT.getText());
                if (kh == null) {
                    kh = new KhachHangDTO();
                    kh.setSoDienThoai(txtSDT.getText());
                    kh.setHoTen(txtHoTen.getText());
                    kh.setLoaiKhachHang(cbLoaiKhach.getValue());
                    kh.setNgaySinh(LocalDate.of(1990, 1, 1));
                    kh = khachHangService.addKhachHang(kh);
                }
            }

            // 2. Lưu Phiếu Đặt Phòng
            for (PhongDTO p : selectedRooms) {
                PhieuDatPhongDTO pdp = new PhieuDatPhongDTO();
//                pdp.setMaPhieu("PDP_" + System.currentTimeMillis());
                String shortId = String.format("%06d", new java.util.Random().nextInt(999999));
                pdp.setMaPhieu("PDP_" + shortId);
                pdp.setMaKhachHang(kh != null ? kh.getMaKhachHang() : null);
                pdp.setMaPhong(p.getMaPhong());
                pdp.setNgayDat(LocalDate.now());
                pdp.setNgayNhan(checkIn);
                pdp.setNgayTra(checkOut);
                pdp.setTrangThai("CHO_NHAN_PHONG");
                pdp.setMaNhanVien(currentUser.getMaNhanVien());
                pdp.setTongTien(p.getGiaPhong() * totalDays);

                phieuDatPhongService.addPhieuDatPhong(pdp);

                // MẸO: Bạn có thể lưu item trong serviceCart vào bảng ChiTietPhieuDatPhong tại đây
            }

            new Alert(Alert.AlertType.INFORMATION, "Đặt phòng thành công!").showAndWait();
            onBackToHome.run();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi: " + e.getMessage()).show();
        }
    }

    private class CartItem {
        DichVuDTO dto;
        int qty;
        CartItem(DichVuDTO d, int q) { this.dto = d; this.qty = q; }
    }
}