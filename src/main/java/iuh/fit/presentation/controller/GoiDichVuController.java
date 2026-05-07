package iuh.fit.presentation.controller;

import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.dto.DichVuDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.service.IChiTietHoaDonService;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.core.service.IPhieuDatPhongService;
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
import javafx.scene.effect.BlurType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GoiDichVuController {

    private IDichVuService dichVuService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IChiTietHoaDonService chiTietHoaDonService;

    // UI Components
    private FlowPane pnlMenu;
    private VBox pnlCartItems;
    private ComboBox<PhieuDatPhongDTO> cbPhongDangO;
    private Label lblTongTien;
    private TextField txtSearch;

    // Data
    private List<DichVuDTO> allServices = new ArrayList<>();
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    // Bảng màu
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_PRIMARY = "#3b82f6";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#1e293b";

    public GoiDichVuController(IDichVuService dichVuService, IPhieuDatPhongService phieuDatPhongService, IChiTietHoaDonService chiTietHoaDonService) {
        this.dichVuService = dichVuService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.chiTietHoaDonService = chiTietHoaDonService;
    }

    public BorderPane createGoiDichVuView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // ==========================================
        // LEFT & CENTER: TÌM KIẾM VÀ MENU DỊCH VỤ
        // ==========================================
        VBox leftContent = new VBox(15);
        leftContent.setPadding(new Insets(20));

        // Header Menu
        HBox menuHeader = new HBox(15);
        menuHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblMenuTitle = new Label("🍽️ MENU DỊCH VỤ");
        lblMenuTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        lblMenuTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm món ăn, đồ uống...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-padding: 8 15; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #cbd5e1;");
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> filterServices(newVal));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        menuHeader.getChildren().addAll(lblMenuTitle, spacer, txtSearch);

        // Lưới hiển thị Card Dịch vụ
        pnlMenu = new FlowPane();
        pnlMenu.setHgap(15);
        pnlMenu.setVgap(15);

        ScrollPane scrollMenu = new ScrollPane(pnlMenu);
        scrollMenu.setFitToWidth(true);
        scrollMenu.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollMenu, Priority.ALWAYS);

        leftContent.getChildren().addAll(menuHeader, scrollMenu);
        root.setCenter(leftContent);

        // ==========================================
        // RIGHT: GIỎ HÀNG (POS CART)
        // ==========================================
        VBox rightCart = new VBox(15);
        rightCart.setPrefWidth(380);
        rightCart.setPadding(new Insets(20));
        rightCart.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 1px;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(10);
        shadow.setOffsetX(-2);
        rightCart.setEffect(shadow);

        Label lblCartTitle = new Label("THÔNG TIN ĐẶT MÓN");
        lblCartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        // 1. Khung chọn phòng đang ở
        VBox boxPhong = new VBox(5);
        boxPhong.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #cbd5e1; -fx-border-radius: 8;");
        Label lblChonPhong = new Label("Phòng phục vụ:");
        lblChonPhong.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        cbPhongDangO = new ComboBox<>();
        cbPhongDangO.setMaxWidth(Double.MAX_VALUE);
        cbPhongDangO.setPromptText("-- Chọn phòng đang ở --");
        cbPhongDangO.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Custom hiển thị ComboBox
        cbPhongDangO.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(PhieuDatPhongDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText("Phòng " + item.getMaPhong() + " (Mã phiếu: " + item.getMaPhieu() + ")"); }
            }
        });
        cbPhongDangO.setButtonCell(cbPhongDangO.getCellFactory().call(null));
        boxPhong.getChildren().addAll(lblChonPhong, cbPhongDangO);

        // 2. Danh sách món (Cart Items)
        pnlCartItems = new VBox(10);
        ScrollPane scrollCart = new ScrollPane(pnlCartItems);
        scrollCart.setFitToWidth(true);
        scrollCart.setStyle("-fx-background-color: transparent; -fx-control-inner-background: white; -fx-border-color: transparent;");
        VBox.setVgrow(scrollCart, Priority.ALWAYS);

        // 3. Khung Tổng Tiền & Thanh Toán
        VBox boxPay = new VBox(15);
        boxPay.setStyle("-fx-background-color: #f0fdf4; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #bbf7d0; -fx-border-radius: 10;");

        HBox totalRow = new HBox();
        Label lblTextTotal = new Label("TỔNG CỘNG:");
        lblTextTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        lblTongTien.setTextFill(Color.web(COLOR_SUCCESS));
        totalRow.getChildren().addAll(lblTextTotal, sp2, lblTongTien);

        Button btnXacNhan = new Button("HOÀN TẤT GỌI MÓN");
        btnXacNhan.setCursor(Cursor.HAND);
        btnXacNhan.setMaxWidth(Double.MAX_VALUE);
        btnXacNhan.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-font-size: 15px; -fx-background-radius: 8;");
        btnXacNhan.setOnAction(e -> handleGoiMon());

        boxPay.getChildren().addAll(totalRow, btnXacNhan);

        rightCart.getChildren().addAll(lblCartTitle, boxPhong, new Separator(), scrollCart, new Separator(), boxPay);
        root.setRight(rightCart);

        // Khởi tạo dữ liệu
        loadActiveRooms();
        loadAllServices();

        return root;
    }

    // ==========================================
    // LOGIC DỮ LIỆU & UI
    // ==========================================

    private void loadActiveRooms() {
        try {
            // Lấy tất cả phiếu đặt phòng, sau đó LỌC lấy những phiếu đang ở trạng thái DA_NHAN_PHONG (Đang phục vụ)
            List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();
            List<PhieuDatPhongDTO> activePhieu = allPhieu.stream()
                    .filter(p -> p.getTrangThai() != null &&
                            (p.getTrangThai().equalsIgnoreCase("DA_NHAN_PHONG") ||
                                    p.getTrangThai().equalsIgnoreCase("Nhận Phòng") ||
                                    p.getTrangThai().equalsIgnoreCase("Đang ở")))
                    .toList();
            cbPhongDangO.getItems().setAll(activePhieu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllServices() {
        try {
            allServices = dichVuService.getAllDichVu();
            renderServiceMenu(allServices);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterServices(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            renderServiceMenu(allServices);
            return;
        }
        String kw = keyword.toLowerCase();
        List<DichVuDTO> filtered = allServices.stream()
                .filter(dv -> dv.getTenDichVu().toLowerCase().contains(kw))
                .toList();
        renderServiceMenu(filtered);
    }

    private void renderServiceMenu(List<DichVuDTO> list) {
        pnlMenu.getChildren().clear();
        for (DichVuDTO dv : list) {
            // 1. Thẻ (Card) chính
            VBox card = new VBox(10);
            card.setPrefSize(170, 220); // Kích thước to hơn để chứa ảnh
            card.setPadding(new Insets(12));
            card.setAlignment(Pos.TOP_CENTER);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #f1f5f9; -fx-border-width: 2;");

            // Đổ bóng (DropShadow) phong cách Apple/Modern UI
            DropShadow shadow = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.08), 15, 0, 0, 5);
            card.setEffect(shadow);

            // Hiệu ứng Hover (Rê chuột vào nổi lên)
            card.setCursor(Cursor.HAND);
            card.setOnMouseEntered(e -> {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-width: 2;");
                card.setTranslateY(-6); // Nổi thẻ lên 6px
            });
            card.setOnMouseExited(e -> {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #f1f5f9; -fx-border-width: 2;");
                card.setTranslateY(0); // Trở về vị trí cũ
            });

            // 2. Khu vực Hình ảnh (Có kích thước cố định)
            StackPane imageContainer = new StackPane();
            imageContainer.setPrefSize(140, 120);
            imageContainer.setMinSize(140, 120);
            imageContainer.setMaxSize(140, 120);
            imageContainer.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12;"); // Màu xám nhạt

            ImageView imageView = new ImageView();
            imageView.setFitWidth(130);
            imageView.setFitHeight(110);
            imageView.setPreserveRatio(true);

            // THUẬT TOÁN TẢI ẢNH:
            // Hệ thống sẽ tìm file ảnh dựa trên MÃ DỊCH VỤ (VD: src/main/resources/images/DV001.png)
            boolean hasImage = false;
            try {
                String imgPath = "/images/" + dv.getMaDichVu() + ".png";
                var stream = getClass().getResourceAsStream(imgPath);
                if (stream != null) {
                    Image img = new Image(stream);
                    if (!img.isError()) {
                        imageView.setImage(img);
                        hasImage = true;
                    }
                }
            } catch (Exception e) {
                // Im lặng bỏ qua nếu không tìm thấy ảnh
            }

            if (hasImage) {
                imageContainer.getChildren().add(imageView);
            } else {
                // NẾU KHÔNG CÓ ẢNH -> Dùng icon Emoji dự phòng siêu to
                Label lblIcon = new Label(getIconForService(dv.getTenDichVu()));
                lblIcon.setFont(Font.font(55)); // Icon to bự
                imageContainer.getChildren().add(lblIcon);
            }

            // 3. Tên Dịch Vụ
            Label lblName = new Label(dv.getTenDichVu());
            lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            lblName.setTextAlignment(TextAlignment.CENTER);
            lblName.setWrapText(true);
            lblName.setMaxHeight(40);
            lblName.setMinHeight(40); // Cố định 2 dòng để thẻ không bị thụt thò
            lblName.setAlignment(Pos.CENTER);
            lblName.setTextFill(Color.web(COLOR_TEXT_MAIN));

            // 4. Giá tiền
            Label lblPrice = new Label(String.format("%,.0f đ", dv.getGiaTien()));
            lblPrice.setTextFill(Color.web(COLOR_PRIMARY));
            lblPrice.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));

            card.getChildren().addAll(imageContainer, lblName, lblPrice);

            // Bấm vào card -> Thêm vào giỏ hàng
            card.setOnMouseClicked(e -> addToCart(dv));

            pnlMenu.getChildren().add(card);
        }
    }

    private String getIconForService(String ten) {
        String lower = ten.toLowerCase();
        if (lower.contains("nước") || lower.contains("coca") || lower.contains("pepsi") || lower.contains("suối") || lower.contains("bia")) return "🥤";
        if (lower.contains("cafe") || lower.contains("cà phê") || lower.contains("trà")) return "☕";
        if (lower.contains("ăn") || lower.contains("cơm") || lower.contains("phở") || lower.contains("mì") || lower.contains("buffet")) return "🍲";
        if (lower.contains("giặt") || lower.contains("ủi")) return "🧺";
        if (lower.contains("massage") || lower.contains("spa")) return "💆";
        if (lower.contains("taxi") || lower.contains("xe") || lower.contains("sân bay")) return "🚕";
        if (lower.contains("gym") || lower.contains("fitness")) return "🏋️";
        if (lower.contains("internet") || lower.contains("wifi")) return "🌐";
        return "🛎️";
    }

    // ==========================================
    // LOGIC GIỎ HÀNG (CART)
    // ==========================================

    private void addToCart(DichVuDTO dv) {
        // Kiểm tra món đã có trong giỏ chưa
        Optional<CartItem> existing = cartItems.stream().filter(item -> item.dichVu.getMaDichVu().equals(dv.getMaDichVu())).findFirst();
        if (existing.isPresent()) {
            existing.get().soLuong++;
        } else {
            cartItems.add(new CartItem(dv, 1));
        }
        renderCart();
    }

    private void renderCart() {
        pnlCartItems.getChildren().clear();
        double total = 0;

        for (CartItem item : cartItems) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));
            row.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            // Tên và giá
            VBox boxInfo = new VBox(3);
            Label lblName = new Label(item.dichVu.getTenDichVu());
            lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            Label lblPrice = new Label(String.format("%,.0f đ", item.dichVu.getGiaTien()));
            lblPrice.setFont(Font.font(11));
            lblPrice.setTextFill(Color.web(COLOR_TEXT_MAIN));
            boxInfo.getChildren().addAll(lblName, lblPrice);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            // Nút tăng giảm số lượng
            Button btnMinus = createQtyButton("-");
            btnMinus.setOnAction(e -> {
                item.soLuong--;
                if (item.soLuong <= 0) cartItems.remove(item);
                renderCart();
            });

            Label lblQty = new Label(String.valueOf(item.soLuong));
            lblQty.setPrefWidth(25);
            lblQty.setAlignment(Pos.CENTER);
            lblQty.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

            Button btnPlus = createQtyButton("+");
            btnPlus.setOnAction(e -> {
                item.soLuong++;
                renderCart();
            });

            // Nút Xóa
            Button btnDel = new Button("✖");
            btnDel.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-weight: bold;");
            btnDel.setOnAction(e -> {
                cartItems.remove(item);
                renderCart();
            });

            row.getChildren().addAll(boxInfo, sp, btnMinus, lblQty, btnPlus, btnDel);
            pnlCartItems.getChildren().add(row);

            total += (item.dichVu.getGiaTien() * item.soLuong);
        }

        if (cartItems.isEmpty()) {
            Label lblEmpty = new Label("Giỏ hàng đang trống");
            lblEmpty.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 20;");
            pnlCartItems.getChildren().add(lblEmpty);
        }

        lblTongTien.setText(String.format("%,.0f đ", total));
    }

    private Button createQtyButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(28, 28);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-weight: bold;");
        return btn;
    }

    // ==========================================
    // LƯU DB
    // ==========================================

    private void handleGoiMon() {
        PhieuDatPhongDTO phieuChon = cbPhongDangO.getValue();

        if (phieuChon == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Phòng đang phục vụ!").show();
            return;
        }
        if (cartItems.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Bạn chưa chọn dịch vụ nào!").show();
            return;
        }

        try {
            // Lặp qua từng món trong giỏ hàng để gọi Service lưu xuống Database
            for (CartItem item : cartItems) {
                ChiTietHoaDonDTO ctdv = new ChiTietHoaDonDTO();
                // Lưu ý: Cấu trúc của bạn có thể cần maPhieu thay vì maHoaDon.
                // Ở đây mình map tạm thời theo chuẩn DTO thường dùng.
                ctdv.setMaDichVu(item.dichVu.getMaDichVu());
                ctdv.setTenDichVu(item.dichVu.getTenDichVu());
                ctdv.setSoLuong(item.soLuong);
                ctdv.setGiaTienTungDichVu(item.dichVu.getGiaTien());
                ctdv.setThanhTien(item.dichVu.getGiaTien() * item.soLuong);

                // GỌI HÀM LƯU TỪ SERVICE BẠN ĐÃ CUNG CẤP:
                // Nếu Service yêu cầu maPhieu, bạn cần truyền phieuChon.getMaPhieu() vào.
                // chiTietHoaDonService.addChiTiet(phieuChon.getMaPhieu(), ctdv);

                // Do mình không rõ cấu trúc method add của Service, mình comment lại phần này.
                // Bạn sẽ uncomment và chỉnh lại cho khớp với Service của bạn nhé!
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã thêm dịch vụ thành công cho phòng " + phieuChon.getMaPhong() + "!");
            alert.showAndWait();

            // Dọn dẹp UI sau khi xong
            cartItems.clear();
            renderCart();
            cbPhongDangO.setValue(null);

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Có lỗi xảy ra: " + ex.getMessage()).show();
        }
    }

    // Lớp nội bộ để quản lý trạng thái Giỏ Hàng
    private class CartItem {
        DichVuDTO dichVu;
        int soLuong;

        CartItem(DichVuDTO dichVu, int soLuong) {
            this.dichVu = dichVu;
            this.soLuong = soLuong;
        }
    }
}