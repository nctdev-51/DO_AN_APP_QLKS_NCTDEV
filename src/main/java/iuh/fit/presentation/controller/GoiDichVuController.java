package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.*;
import java.util.stream.Collectors;

public class GoiDichVuController {

    private IDichVuService dichVuService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IChiTietHoaDonService chiTietHoaDonService;

    // UI Components
    private VBox pnlGroups;
    private FlowPane pnlMenu;
    private VBox pnlSelectedRooms;
    private VBox pnlCartItems;
    private Label lblTongTien;
    private TextField txtSearch;

    // Data
    private List<DichVuDTO> listAllServices = new ArrayList<>();
    private ObservableList<CartItem> currentCart = FXCollections.observableArrayList();
    private List<PhieuDatPhongDTO> selectedGroupRooms = new ArrayList<>();
    private Set<String> targetMaPhieuList = new HashSet<>();

    // Màu sắc chuẩn
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";

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

        // ==========================================
        // 0. KHU VỰC TIÊU ĐỀ (HEADER)
        // ==========================================
        VBox headerBox = new VBox(5);
        headerBox.setPadding(new Insets(25, 20, 5, 20));

        Label lblMainTitle = new Label("GỌI DỊCH VỤ POS");
        lblMainTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblMainTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Cung cấp dịch vụ ăn uống, tiện ích nhanh chóng cho khách lưu trú hoặc theo đoàn.");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        headerBox.getChildren().addAll(lblMainTitle, lblSubTitle);
        root.setTop(headerBox); // Đặt Tiêu đề lên đầu trang

        // ==========================================
        // 1. CỘT TRÁI: DANH SÁCH ĐOÀN / GIA ĐÌNH
        // ==========================================
        VBox leftCol = new VBox(15);
        leftCol.setPrefWidth(280);
        leftCol.setPadding(new Insets(20));
        leftCol.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 1px 0 0;");

        Label lblGroupTitle = new Label("👥 ĐOÀN / GIA ĐÌNH");
        lblGroupTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblGroupTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        pnlGroups = new VBox(10);
        ScrollPane scrollGroups = new ScrollPane(pnlGroups);
        scrollGroups.setFitToWidth(true);
        scrollGroups.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 0;");
        scrollGroups.setBorder(Border.EMPTY);

        leftCol.getChildren().addAll(lblGroupTitle, new Separator(), scrollGroups);
        root.setLeft(leftCol);

        // ==========================================
        // 2. CỘT GIỮA: THỰC ĐƠN DỊCH VỤ
        // ==========================================
        VBox centerArea = new VBox(15);
        centerArea.setPadding(new Insets(20));

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm món ăn, đồ uống...");
        txtSearch.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-background-radius: 20; -fx-border-radius: 20; -fx-padding: 10 20;");
        txtSearch.textProperty().addListener((obs, old, newVal) -> filterServices(newVal));

        pnlMenu = new FlowPane(15, 15);
        ScrollPane scrollMenu = new ScrollPane(pnlMenu);
        scrollMenu.setFitToWidth(true);
        scrollMenu.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 0;");
        scrollMenu.setBorder(Border.EMPTY);
        VBox.setVgrow(scrollMenu, Priority.ALWAYS);

        centerArea.getChildren().addAll(txtSearch, scrollMenu);
        root.setCenter(centerArea);

        // ==========================================
        // 3. CỘT PHẢI: CHI TIẾT ÁP DỤNG & GIỎ HÀNG
        // ==========================================
        VBox rightCol = new VBox(15);
        rightCol.setPrefWidth(350);
        rightCol.setPadding(new Insets(20));
        rightCol.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 1px;");

        // Phần chọn phòng
        VBox boxTarget = new VBox(10);
        boxTarget.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        Label lblTarget = new Label("🎯 ÁP DỤNG CHO PHÒNG:");
        lblTarget.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblTarget.setTextFill(Color.web(COLOR_TEXT_MAIN));
        pnlSelectedRooms = new VBox(8);
        boxTarget.getChildren().addAll(lblTarget, pnlSelectedRooms);

        // Phần giỏ hàng
        Label lblCart = new Label("🛒 MÓN ĐÃ CHỌN");
        lblCart.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblCart.setTextFill(Color.web(COLOR_TEXT_MAIN));
        pnlCartItems = new VBox(8);
        ScrollPane scrollCart = new ScrollPane(pnlCartItems);
        scrollCart.setFitToWidth(true);
        scrollCart.setPrefHeight(250);
        scrollCart.setStyle("-fx-background-color: transparent; -fx-control-inner-background: white; -fx-padding: 0;");
        scrollCart.setBorder(Border.EMPTY);

        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.BLACK, 22));
        lblTongTien.setTextFill(Color.web(COLOR_SUCCESS));

        Button btnOrder = new Button("XÁC NHẬN GỌI MÓN");
        btnOrder.setMaxWidth(Double.MAX_VALUE);
        btnOrder.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
        btnOrder.setOnAction(e -> handleBulkOrder());

        rightCol.getChildren().addAll(boxTarget, lblCart, scrollCart, new Separator(), new Label("TỔNG TIỀN:"), lblTongTien, btnOrder);
        root.setRight(rightCol);

        refreshData();
        return root;
    }

    private void refreshData() {
        try {
            List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();
            if (allPhieu != null) {
                List<PhieuDatPhongDTO> activePhieu = allPhieu.stream()
                        .filter(p -> p.getTrangThai() != null && (p.getTrangThai().contains("Nhận Phòng") || p.getTrangThai().equals("DA_NHAN_PHONG")))
                        .collect(Collectors.toList());

                Map<String, List<PhieuDatPhongDTO>> groups = activePhieu.stream()
                        .collect(Collectors.groupingBy(p -> {
                            String ten = p.getTenKhachHang() != null ? p.getTenKhachHang() : "Khách";
                            String ma = p.getMaKhachHang() != null ? p.getMaKhachHang() : "Vãng lai";
                            return ten + " (" + ma + ")";
                        }));

                pnlGroups.getChildren().clear();
                groups.forEach((groupName, rooms) -> {
                    VBox groupCard = new VBox(5);
                    groupCard.setPadding(new Insets(12));
                    // Card thường
                    groupCard.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;");
                    Label name = new Label(groupName); name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13)); name.setTextFill(Color.web(COLOR_TEXT_MAIN));
                    Label info = new Label("🏠 " + rooms.size() + " phòng đang ở"); info.setTextFill(Color.web("#64748b"));
                    groupCard.getChildren().addAll(name, info);

                    groupCard.setOnMouseClicked(e -> selectGroup(rooms, groupCard));
                    pnlGroups.getChildren().add(groupCard);
                });
            }

            listAllServices = dichVuService.getAllDichVu();
            if (listAllServices != null) {
                renderMenu(listAllServices);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void selectGroup(List<PhieuDatPhongDTO> rooms, VBox card) {
        // Reset tất cả các card khác
        pnlGroups.getChildren().forEach(n -> n.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;"));

        // Highlight card được chọn
        card.setStyle("-fx-background-color: #eff6ff; -fx-border-color: " + COLOR_PRIMARY + "; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-cursor: hand;");

        selectedGroupRooms = rooms;
        targetMaPhieuList.clear();
        pnlSelectedRooms.getChildren().clear();

        CheckBox cbAll = new CheckBox("Chọn tất cả các phòng");
        cbAll.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-cursor: hand;");
        pnlSelectedRooms.getChildren().add(cbAll);
        pnlSelectedRooms.getChildren().add(new Separator());

        List<CheckBox> listCb = new ArrayList<>();
        for (PhieuDatPhongDTO p : rooms) {
            String tenPhong = p.getTenPhong() != null ? p.getTenPhong() : "Phòng " + p.getMaPhong();
            CheckBox cb = new CheckBox(tenPhong);
            cb.setSelected(true);
            cb.setStyle("-fx-cursor: hand; -fx-text-fill: " + COLOR_TEXT_MAIN + ";");
            targetMaPhieuList.add(p.getMaPhieu());

            cb.selectedProperty().addListener((obs, old, val) -> {
                if(val) targetMaPhieuList.add(p.getMaPhieu());
                else targetMaPhieuList.remove(p.getMaPhieu());
                updateCartUI();
            });
            listCb.add(cb);
            pnlSelectedRooms.getChildren().add(cb);
        }

        cbAll.setSelected(true);
        cbAll.setOnAction(e -> {
            listCb.forEach(c -> c.setSelected(cbAll.isSelected()));
        });

        updateCartUI();
    }

    private void renderMenu(List<DichVuDTO> list) {
        pnlMenu.getChildren().clear();
        if (list == null) return;

        for (DichVuDTO dv : list) {
            VBox card = new VBox(8);
            card.setPrefSize(140, 180);
            card.setPadding(new Insets(10));
            card.setAlignment(Pos.CENTER);

            // Fix bo góc hoàn chỉnh
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-cursor: hand;");

            Label emoji = new Label(getEmoji(dv.getTenDichVu())); emoji.setFont(Font.font(40));
            Label name = new Label(dv.getTenDichVu()); name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            name.setTextFill(Color.web(COLOR_TEXT_MAIN));
            name.setWrapText(true); name.setTextAlignment(TextAlignment.CENTER);

            Label price = new Label(String.format("%,.0f đ", dv.getGiaTien())); price.setTextFill(Color.web(COLOR_PRIMARY));
            price.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

            card.getChildren().addAll(emoji, name, price);
            card.setOnMouseClicked(e -> { addToCart(dv); });

            // Hover có DropShadow nhẹ và bo viền đồng nhất
            card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #eff6ff; -fx-border-color: " + COLOR_PRIMARY + "; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-cursor: hand;"));
            card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-width: 1.5; -fx-cursor: hand;"));

            pnlMenu.getChildren().add(card);
        }
    }

    private void addToCart(DichVuDTO dv) {
        if (selectedGroupRooms.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn đoàn khách ở cột bên trái trước!").show();
            return;
        }
        Optional<CartItem> exist = currentCart.stream().filter(i -> i.dto.getMaDichVu().equals(dv.getMaDichVu())).findFirst();
        if (exist.isPresent()) exist.get().qty++;
        else currentCart.add(new CartItem(dv, 1));
        updateCartUI();
    }

    private void updateCartUI() {
        pnlCartItems.getChildren().clear();
        double grandTotal = 0;
        int numRooms = targetMaPhieuList.size();

        for (CartItem item : currentCart) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-padding: 8; -fx-background-radius: 8; -fx-border-radius: 8;");

            VBox info = new VBox(2);
            Label lblName = new Label(item.dto.getTenDichVu());
            lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            lblName.setTextFill(Color.web(COLOR_TEXT_MAIN));

            Label lblPrice = new Label(String.format("%,.0f đ/phòng", item.dto.getGiaTien()));
            lblPrice.setTextFill(Color.web("#64748b"));
            info.getChildren().addAll(lblName, lblPrice);

            Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);

            // Nút bấm xịn xò
            String btnStyle = "-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-weight: bold;";

            Button sub = new Button("-");
            sub.setStyle(btnStyle);
            sub.setOnAction(e -> { item.qty--; if(item.qty<=0) currentCart.remove(item); updateCartUI(); });

            Label q = new Label(String.valueOf(item.qty));
            q.setPrefWidth(20); q.setAlignment(Pos.CENTER);
            q.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

            Button add = new Button("+");
            add.setStyle(btnStyle);
            add.setOnAction(e -> { item.qty++; updateCartUI(); });

            row.getChildren().addAll(info, s, sub, q, add);
            pnlCartItems.getChildren().add(row);

            grandTotal += (item.dto.getGiaTien() * item.qty * numRooms);
        }
        lblTongTien.setText(String.format("%,.0f đ", grandTotal));
    }

    private void handleBulkOrder() {
        if (targetMaPhieuList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng tick chọn ít nhất 1 phòng để áp dụng dịch vụ!").show();
            return;
        }
        if (currentCart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Bạn chưa chọn dịch vụ nào!").show();
            return;
        }

        try {
            for (String maPhieu : targetMaPhieuList) {
                for (CartItem item : currentCart) {
                    ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
                    dto.setMaPhieu(maPhieu);
                    dto.setMaDichVu(item.dto.getMaDichVu());
                    dto.setSoLuong(item.qty);
                    dto.setGiaTienTungDichVu(item.dto.getGiaTien());
                    chiTietHoaDonService.addOrUpdateChiTiet(dto);
                }
            }
            new Alert(Alert.AlertType.INFORMATION, "Đã gọi món thành công cho " + targetMaPhieuList.size() + " phòng!").showAndWait();
            currentCart.clear();
            updateCartUI();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu dịch vụ: " + ex.getMessage()).show();
        }
    }

    private void filterServices(String kw) {
        if(kw == null || kw.isEmpty()) {
            renderMenu(listAllServices);
            return;
        }
        renderMenu(listAllServices.stream()
                .filter(d -> d.getTenDichVu().toLowerCase().contains(kw.toLowerCase()))
                .collect(Collectors.toList()));
    }

    private String getEmoji(String name) {
        if (name == null) return "🛎️";
        String n = name.toLowerCase();
        if(n.contains("nước") || n.contains("coca") || n.contains("pepsi")) return "🥤";
        if(n.contains("ăn") || n.contains("cơm") || n.contains("phở")) return "🍲";
        if(n.contains("cafe") || n.contains("cà phê") || n.contains("trà")) return "☕";
        if(n.contains("massage") || n.contains("spa")) return "💆";
        if(n.contains("giặt") || n.contains("ủi")) return "🧺";
        return "🛎️";
    }

    private class CartItem {
        DichVuDTO dto; int qty;
        CartItem(DichVuDTO d, int q) { this.dto = d; this.qty = q; }
    }
}