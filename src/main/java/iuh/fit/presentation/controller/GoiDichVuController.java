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
    private ComboBox<PhieuDatPhongDTO> cbPhongPhucVu;
    private Label lblTongTien;
    private TextField txtSearch;

    // Data
    private List<DichVuDTO> listAllServices = new ArrayList<>();
    private ObservableList<CartItem> currentCart = FXCollections.observableArrayList();

    // Bảng màu hiện đại
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_PRIMARY_DARK = "#1e3a8a";
    private final String COLOR_BG = "#f8fafc";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_BORDER = "#e2e8f0";

    public GoiDichVuController(IDichVuService dichVuService,
                               IPhieuDatPhongService phieuDatPhongService,
                               IChiTietHoaDonService chiTietHoaDonService) {
        this.dichVuService = dichVuService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.chiTietHoaDonService = chiTietHoaDonService;
    }

    public BorderPane createGoiDichVuView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // --- CỘT GIỮA: THỰC ĐƠN DỊCH VỤ ---
        VBox centerArea = new VBox(25);
        centerArea.setPadding(new Insets(30));

        HBox menuHeader = new HBox(20);
        menuHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label lblTitle = new Label("THỰC ĐƠN DỊCH VỤ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Lựa chọn các dịch vụ tiện ích để phục vụ phòng khách.");
        lblSubTitle.setFont(Font.font("Segoe UI", 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        titleBox.getChildren().addAll(lblTitle, lblSubTitle);

        txtSearch = new TextField();
        txtSearch.setPromptText("Tìm kiếm món ăn, đồ uống...");
        txtSearch.setPrefWidth(300);
        txtSearch.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cbd5e1; -fx-padding: 10 15; -fx-background-color: white;");
        txtSearch.textProperty().addListener((obs, old, newVal) -> filterServices(newVal));

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        menuHeader.getChildren().addAll(titleBox, spacer, txtSearch);

        pnlMenu = new FlowPane(20, 20);
        ScrollPane scrollMenu = new ScrollPane(pnlMenu);
        scrollMenu.setFitToWidth(true);
        scrollMenu.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollMenu, Priority.ALWAYS);

        centerArea.getChildren().addAll(menuHeader, scrollMenu);
        root.setCenter(centerArea);

        // --- CỘT PHẢI: GIỎ HÀNG (CART) ---
        VBox rightCart = new VBox(20);
        rightCart.setPrefWidth(400);
        rightCart.setPadding(new Insets(30, 25, 30, 25));
        rightCart.setStyle("-fx-background-color: white; -fx-border-width: 0 0 0 1px; -fx-border-color: " + COLOR_BORDER + ";");
        rightCart.setEffect(new DropShadow(20, Color.web("#000000", 0.05)));

        Label lblCartTitle = new Label("HÓA ĐƠN DỊCH VỤ");
        lblCartTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 20));
        lblCartTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        VBox roomSelectBox = new VBox(8);
        Label lblRoom = new Label("Gửi yêu cầu phục vụ đến phòng:");
        lblRoom.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblRoom.setTextFill(Color.web(COLOR_TEXT_MUTED));

        cbPhongPhucVu = new ComboBox<>();
        cbPhongPhucVu.setMaxWidth(Double.MAX_VALUE);
        cbPhongPhucVu.setPromptText("Bấm để chọn phòng đang phục vụ...");
        cbPhongPhucVu.setStyle("-fx-font-size: 14px; -fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-padding: 5;");
        roomSelectBox.getChildren().addAll(lblRoom, cbPhongPhucVu);

        pnlCartItems = new VBox(12);
        ScrollPane scrollCart = new ScrollPane(pnlCartItems);
        scrollCart.setFitToWidth(true);
        scrollCart.setPrefHeight(450);
        scrollCart.setStyle("-fx-background-color: transparent; -fx-control-inner-background: white; -fx-border-color: transparent;");

        VBox totalBox = new VBox(5);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setPadding(new Insets(15, 0, 15, 0));
        totalBox.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1 0 0 0;");

        Label lblTotalTitle = new Label("TỔNG CỘNG");
        lblTotalTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTotalTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        lblTongTien.setTextFill(Color.web(COLOR_PRIMARY));
        totalBox.getChildren().addAll(lblTotalTitle, lblTongTien);

        Button btnXacNhan = new Button("HOÀN TẤT ĐẶT DỊCH VỤ");
        btnXacNhan.setMaxWidth(Double.MAX_VALUE);
        btnXacNhan.setCursor(Cursor.HAND);
        btnXacNhan.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_PRIMARY + ", " + COLOR_PRIMARY_DARK + "); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 16; -fx-background-radius: 10;");
        btnXacNhan.setOnAction(e -> handleConfirmOrder());

        rightCart.getChildren().addAll(lblCartTitle, roomSelectBox, scrollCart, totalBox, btnXacNhan);
        root.setRight(rightCart);

        refreshData();
        return root;
    }

    private void refreshData() {
        try {
            List<PhieuDatPhongDTO> all = phieuDatPhongService.getAllPhieuDatPhong();
            List<PhieuDatPhongDTO> active = all.stream()
                    .filter(p -> p.getTrangThai() != null && (p.getTrangThai().contains("Nhận Phòng") || p.getTrangThai().equals("DA_NHAN_PHONG")))
                    .toList();
            cbPhongPhucVu.getItems().setAll(active);
            cbPhongPhucVu.setCellFactory(p -> new ListCell<>() {
                @Override protected void updateItem(PhieuDatPhongDTO i, boolean e) {
                    super.updateItem(i, e);
                    if (e || i == null) {
                        setText(null);
                    } else {
                        setText("Phòng " + i.getMaPhong() + " (Khách: " + i.getTenKhachHang() + ")");
                        setStyle("-fx-font-size: 14px; -fx-padding: 8;");
                    }
                }
            });
            cbPhongPhucVu.setButtonCell(cbPhongPhucVu.getCellFactory().call(null));

            listAllServices = dichVuService.getAllDichVu();
            renderMenu(listAllServices);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void renderMenu(List<DichVuDTO> list) {
        pnlMenu.getChildren().clear();
        for (DichVuDTO dv : list) {
            VBox card = new VBox(12);
            card.setPrefSize(180, 220);
            card.setPadding(new Insets(15));
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1; -fx-cursor: hand;");

            DropShadow ds = new DropShadow(10, Color.web("#000000", 0.03));
            card.setEffect(ds);

            StackPane imgBox = new StackPane();
            imgBox.setPrefSize(140, 100);
            imgBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");

            ImageView iv = new ImageView(); iv.setFitWidth(120); iv.setFitHeight(80); iv.setPreserveRatio(true);
            try {
                iv.setImage(new Image(getClass().getResourceAsStream("/images/" + dv.getMaDichVu() + ".png")));
                imgBox.getChildren().add(iv);
            } catch (Exception e) {
                // Xử lý khi không có hình ảnh: Hiển thị chữ "Dịch vụ" thay vì Emoji để tránh lỗi ô vuông
                Label lblPlaceholder = new Label("Dịch Vụ");
                lblPlaceholder.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
                lblPlaceholder.setTextFill(Color.web(COLOR_TEXT_MUTED));
                imgBox.getChildren().add(lblPlaceholder);
            }

            Label lblName = new Label(dv.getTenDichVu());
            lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            lblName.setTextFill(Color.web(COLOR_TEXT_MAIN));
            lblName.setWrapText(true);
            lblName.setTextAlignment(TextAlignment.CENTER);

            Label lblPrice = new Label(String.format("%,.0f đ", dv.getGiaTien()));
            lblPrice.setFont(Font.font("Segoe UI", FontWeight.BLACK, 15));
            lblPrice.setTextFill(Color.web(COLOR_PRIMARY));

            card.getChildren().addAll(imgBox, lblName, lblPrice);

            // Hover effect
            card.setOnMouseEntered(e -> {
                card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 12; -fx-border-width: 1; -fx-cursor: hand; -fx-translate-y: -3;");
                ds.setRadius(15);
            });
            card.setOnMouseExited(e -> {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1; -fx-cursor: hand; -fx-translate-y: 0;");
                ds.setRadius(10);
            });

            card.setOnMouseClicked(e -> addToCart(dv));
            pnlMenu.getChildren().add(card);
        }
    }

    private void addToCart(DichVuDTO dv) {
        Optional<CartItem> item = currentCart.stream().filter(i -> i.dto.getMaDichVu().equals(dv.getMaDichVu())).findFirst();
        if (item.isPresent()) item.get().qty++;
        else currentCart.add(new CartItem(dv, 1));
        updateCartUI();
    }

    private void updateCartUI() {
        pnlCartItems.getChildren().clear();
        double total = 0;

        if (currentCart.isEmpty()) {
            Label emptyLbl = new Label("Giỏ hàng đang trống");
            emptyLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-padding: 20;");
            pnlCartItems.getChildren().add(emptyLbl);
        }

        for (CartItem item : currentCart) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            VBox info = new VBox(4);
            Label lblName = new Label(item.dto.getTenDichVu());
            lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            lblName.setTextFill(Color.web(COLOR_TEXT_MAIN));

            Label lblPrice = new Label(String.format("%,.0f đ", item.dto.getGiaTien()));
            lblPrice.setFont(Font.font("Segoe UI", 13));
            lblPrice.setTextFill(Color.web(COLOR_PRIMARY));
            info.getChildren().addAll(lblName, lblPrice);

            Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);

            String btnStyle = "-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-weight: bold; -fx-cursor: hand;";
            Button sub = new Button("-"); sub.setStyle(btnStyle);
            sub.setOnAction(e -> { item.qty--; if(item.qty<=0) currentCart.remove(item); updateCartUI(); });

            Label q = new Label(String.valueOf(item.qty));
            q.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            q.setPrefWidth(25); q.setAlignment(Pos.CENTER);

            Button add = new Button("+"); add.setStyle(btnStyle);
            add.setOnAction(e -> { item.qty++; updateCartUI(); });

            row.getChildren().addAll(info, s, sub, q, add);
            pnlCartItems.getChildren().add(row);
            total += (item.dto.getGiaTien() * item.qty);
        }
        lblTongTien.setText(String.format("%,.0f đ", total));
    }

    private void handleConfirmOrder() {
        PhieuDatPhongDTO pdp = cbPhongPhucVu.getValue();
        if (pdp == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn phòng", "Vui lòng chọn phòng khách đang ở trước khi đặt dịch vụ.");
            return;
        }
        if (currentCart.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Giỏ hàng trống", "Vui lòng chọn ít nhất một dịch vụ để đặt.");
            return;
        }
        try {
            for (CartItem item : currentCart) {
                ChiTietHoaDonDTO ctdv = new ChiTietHoaDonDTO();
                ctdv.setMaPhieu(pdp.getMaPhieu());
                ctdv.setMaDichVu(item.dto.getMaDichVu());
                ctdv.setSoLuong(item.qty);
                ctdv.setGiaTienTungDichVu(item.dto.getGiaTien());
                chiTietHoaDonService.addOrUpdateChiTiet(ctdv);
            }
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã ghi nhận yêu cầu phục vụ cho phòng " + pdp.getMaPhong() + "!");
            currentCart.clear();
            updateCartUI();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể lưu dịch vụ: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void filterServices(String kw) {
        if(kw == null || kw.isEmpty()) renderMenu(listAllServices);
        else renderMenu(listAllServices.stream().filter(d -> d.getTenDichVu().toLowerCase().contains(kw.toLowerCase())).toList());
    }

    private class CartItem {
        DichVuDTO dto; int qty;
        CartItem(DichVuDTO d, int q) { this.dto = d; this.qty = q; }
    }
}