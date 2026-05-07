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

    // Bảng màu
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_PRIMARY = "#3b82f6";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_TEXT_MAIN = "#1e293b";

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

        // --- CỘT GIỮA: MENU DỊCH VỤ ---
        VBox centerArea = new VBox(20);
        centerArea.setPadding(new Insets(20));

        HBox menuHeader = new HBox(15);
        menuHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblTitle = new Label("🍽️ THỰC ĐƠN DỊCH VỤ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm món ăn...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-background-radius: 20; -fx-padding: 8 15;");
        txtSearch.textProperty().addListener((obs, old, newVal) -> filterServices(newVal));

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        menuHeader.getChildren().addAll(lblTitle, spacer, txtSearch);

        pnlMenu = new FlowPane(15, 15);
        ScrollPane scrollMenu = new ScrollPane(pnlMenu);
        scrollMenu.setFitToWidth(true);
        scrollMenu.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollMenu, Priority.ALWAYS);

        centerArea.getChildren().addAll(menuHeader, scrollMenu);
        root.setCenter(centerArea);

        // --- CỘT PHẢI: GIỎ HÀNG ---
        VBox rightCart = new VBox(15);
        rightCart.setPrefWidth(380);
        rightCart.setPadding(new Insets(20));
        rightCart.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 1px;");

        Label lblCartTitle = new Label("GIỎ HÀNG DỊCH VỤ");
        lblCartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        cbPhongPhucVu = new ComboBox<>();
        cbPhongPhucVu.setMaxWidth(Double.MAX_VALUE);
        cbPhongPhucVu.setPromptText("-- Chọn phòng đang ở --");

        pnlCartItems = new VBox(10);
        ScrollPane scrollCart = new ScrollPane(pnlCartItems);
        scrollCart.setFitToWidth(true);
        scrollCart.setPrefHeight(400);

        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.BLACK, 24));
        lblTongTien.setTextFill(Color.web(COLOR_SUCCESS));

        Button btnXacNhan = new Button("HOÀN TẤT GỌI MÓN");
        btnXacNhan.setMaxWidth(Double.MAX_VALUE);
        btnXacNhan.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 10;");
        btnXacNhan.setOnAction(e -> handleConfirmOrder());

        rightCart.getChildren().addAll(lblCartTitle, new Label("Phòng khách ở:"), cbPhongPhucVu, new Separator(), scrollCart, new Label("TỔNG TIỀN:"), lblTongTien, btnXacNhan);
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
                    setText(e || i == null ? null : "Phòng " + i.getMaPhong() + " - " + i.getTenKhachHang());
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
            VBox card = new VBox(10);
            card.setPrefSize(150, 190);
            card.setPadding(new Insets(10));
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #f1f5f9; -fx-border-width: 2; -fx-cursor: hand;");

            StackPane imgBox = new StackPane();
            imgBox.setPrefSize(120, 90);
            imgBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");

            ImageView iv = new ImageView(); iv.setFitWidth(110); iv.setFitHeight(80); iv.setPreserveRatio(true);
            try {
                iv.setImage(new Image(getClass().getResourceAsStream("/images/" + dv.getMaDichVu() + ".png")));
            } catch (Exception e) { imgBox.getChildren().add(new Label("🍽️") {{ setFont(Font.font(40)); }}); }
            imgBox.getChildren().add(iv);

            Label n = new Label(dv.getTenDichVu()); n.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13)); n.setWrapText(true); n.setTextAlignment(TextAlignment.CENTER);
            Label p = new Label(String.format("%,.0f đ", dv.getGiaTien())); p.setTextFill(Color.web(COLOR_PRIMARY));

            card.getChildren().addAll(imgBox, n, p);
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
        for (CartItem item : currentCart) {
            HBox row = new HBox(10); row.setAlignment(Pos.CENTER_LEFT); row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 8; -fx-background-radius: 8;");
            VBox info = new VBox(2);
            info.getChildren().addAll(new Label(item.dto.getTenDichVu()) {{ setFont(Font.font(null, FontWeight.BOLD, 12)); }}, new Label(String.format("%,.0f đ", item.dto.getGiaTien())));
            Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
            Button sub = new Button("-"); sub.setOnAction(e -> { item.qty--; if(item.qty<=0) currentCart.remove(item); updateCartUI(); });
            Label q = new Label(String.valueOf(item.qty)); q.setPrefWidth(20); q.setAlignment(Pos.CENTER);
            Button add = new Button("+"); add.setOnAction(e -> { item.qty++; updateCartUI(); });
            row.getChildren().addAll(info, s, sub, q, add);
            pnlCartItems.getChildren().add(row);
            total += (item.dto.getGiaTien() * item.qty);
        }
        lblTongTien.setText(String.format("%,.0f đ", total));
    }

    private void handleConfirmOrder() {
        PhieuDatPhongDTO pdp = cbPhongPhucVu.getValue();
        if (pdp == null || currentCart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn phòng và dịch vụ!").show();
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
            new Alert(Alert.AlertType.INFORMATION, "Đã gửi yêu cầu phục vụ!").show();
            currentCart.clear(); updateCartUI();
        } catch (Exception ex) { ex.printStackTrace(); }
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