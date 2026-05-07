package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
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
    private IDichVuService dichVuService;
    private TaiKhoanDTO currentUser;
    private Runnable onBackToHome;

    private DatePicker dpCheckIn, dpCheckOut;
    private FlowPane pnlRoomMap;
    private ListView<PhongDTO> lvSelectedRooms;
    private List<PhongDTO> selectedRoomsList;
    private Label lblTongTien;

    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_AVAILABLE = "#10b981";
    private final String COLOR_SELECTED_BG = "#eff6ff";

    public QuanLyDatPhongController(IPhongService phongService, IPhieuDatPhongService phieuDatPhongService,
                                    IKhachHangService khachHangService, IDichVuService dichVuService,
                                    TaiKhoanDTO currentUser, Runnable onBackToHome) {
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.khachHangService = khachHangService;
        this.dichVuService = dichVuService;
        this.currentUser = currentUser;
        this.onBackToHome = onBackToHome;
        this.selectedRoomsList = new ArrayList<>();
    }

    public BorderPane createQuanLyDatPhongView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f1f5f9;");

        // BỘ LỌC
        HBox filterBox = new HBox(20);
        filterBox.setPadding(new Insets(15, 25, 15, 25));
        filterBox.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1px 0; -fx-border-color: #e2e8f0;");
        Label lblTitle = new Label("CHỌN PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY));

        dpCheckIn = new DatePicker(LocalDate.now());
        dpCheckOut = new DatePicker(LocalDate.now().plusDays(1));
        Button btnTimPhong = new Button("🔍 TÌM PHÒNG TRỐNG");
        btnTimPhong.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnTimPhong.setOnAction(e -> loadRoomsToMap());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        filterBox.getChildren().addAll(lblTitle, spacer, new Label("Nhận:"), dpCheckIn, new Label("Trả:"), dpCheckOut, btnTimPhong);
        root.setTop(filterBox);

        // SƠ ĐỒ PHÒNG
        pnlRoomMap = new FlowPane(15, 15);
        pnlRoomMap.setPadding(new Insets(15));
        ScrollPane scrollMap = new ScrollPane(pnlRoomMap);
        scrollMap.setFitToWidth(true);
        scrollMap.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");
        root.setCenter(scrollMap);

        // GIỎ HÀNG
        VBox rightPanel = new VBox(20);
        rightPanel.setPrefWidth(350);
        rightPanel.setPadding(new Insets(25));
        rightPanel.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 1px;");

        Label lblCart = new Label("PHÒNG ĐÃ CHỌN");
        lblCart.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        lvSelectedRooms = new ListView<>();
        lvSelectedRooms.setPrefHeight(250);
        lvSelectedRooms.setCellFactory(param -> new ListCell<>() {
            @Override protected void updateItem(PhongDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); }
                else { setText("Phòng " + item.getMaPhong() + " - " + String.format("%,.0f đ", item.getGiaPhong())); }
            }
        });

        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
        lblTongTien.setTextFill(Color.web("#d97706"));

        Button btnTiepTuc = new Button("TIẾP TỤC LẬP PHIẾU ➔");
        btnTiepTuc.setMaxWidth(Double.MAX_VALUE);
        btnTiepTuc.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        btnTiepTuc.setOnAction(e -> handleTiepTuc(root));

        rightPanel.getChildren().addAll(lblCart, lvSelectedRooms, new Label("Tạm tính tiền phòng:"), lblTongTien, btnTiepTuc);
        root.setRight(rightPanel);

        loadRoomsToMap();
        return root;
    }

    private void loadRoomsToMap() {
        pnlRoomMap.getChildren().clear();
        selectedRoomsList.clear();
        updateCartUI();

        LocalDate in = dpCheckIn.getValue();
        LocalDate out = dpCheckOut.getValue();
        if (in != null && out != null && out.isAfter(in)) {
            try {
                List<PhongDTO> dsPhong = phongService.findAvailableRooms(in, out, 0, 99000000);
                for (PhongDTO p : dsPhong) {
                    VBox card = new VBox(5);
                    card.setPrefSize(130, 100);
                    card.setAlignment(Pos.CENTER);
                    String defaultStyle = "-fx-background-color: white; -fx-border-width: 4 1 1 1; -fx-border-color: " + COLOR_AVAILABLE + " #e2e8f0 #e2e8f0 #e2e8f0; -fx-cursor: hand; -fx-background-radius: 5;";
                    String selectedStyle = "-fx-background-color: " + COLOR_SELECTED_BG + "; -fx-border-width: 4 1 1 1; -fx-border-color: " + COLOR_PRIMARY + "; -fx-cursor: hand; -fx-background-radius: 5;";

                    card.setStyle(defaultStyle);
                    Label lblMa = new Label(p.getMaPhong());
                    lblMa.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
                    card.getChildren().addAll(lblMa, new Label(String.format("%,.0fđ", p.getGiaPhong())));

                    card.setOnMouseClicked(e -> {
                        if (selectedRoomsList.contains(p)) { selectedRoomsList.remove(p); card.setStyle(defaultStyle); }
                        else { selectedRoomsList.add(p); card.setStyle(selectedStyle); }
                        updateCartUI();
                    });
                    pnlRoomMap.getChildren().add(card);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void updateCartUI() {
        lvSelectedRooms.getItems().setAll(selectedRoomsList);
        double total = selectedRoomsList.stream().mapToDouble(PhongDTO::getGiaPhong).sum();
        long days = Math.max(1, ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue()));
        lblTongTien.setText(String.format("%,.0f đ", total * days));
    }

    private void handleTiepTuc(BorderPane currentRoot) {
        if (selectedRoomsList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ít nhất 1 phòng!").show(); return;
        }
        // Gọi màn hình Bước 2
        LapPhieuDatPhongController lapPhieuController = new LapPhieuDatPhongController(
                phieuDatPhongService, khachHangService, dichVuService, currentUser,
                selectedRoomsList, dpCheckIn.getValue(), dpCheckOut.getValue(), onBackToHome
        );
        currentRoot.setCenter(lapPhieuController.createLapPhieuView());
        currentRoot.setTop(null);
        currentRoot.setRight(null);
    }

    public void preselectRoom(PhongDTO phong) {
        if (phong != null && !selectedRoomsList.contains(phong)) {
            selectedRoomsList.add(phong);
            updateCartUI();
        }
    }
}