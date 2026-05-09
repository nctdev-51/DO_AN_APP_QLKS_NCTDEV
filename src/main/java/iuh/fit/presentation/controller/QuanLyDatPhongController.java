package iuh.fit.presentation.controller;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.LocalDateTime; // 👉 THÊM IMPORT NÀY
import java.util.List;

/**
 * Controller: Đặt & Nhận Phòng
 * Cho phép khách hàng đặt phòng, xem phòng trống, tạo phiếu đặt
 */
public class QuanLyDatPhongController {

    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IKhachHangService khachHangService;
    private TaiKhoanDTO currentUser;

    // UI Components
    private ComboBox<KhachHangDTO> cbKhachHang;
    private DatePicker dpCheckIn, dpCheckOut;
    private ListView<PhongDTO> lvPhongTrong;
    private Label lblTongTien, lblPhongChon;
    private Button btnTaoDon, btnHuyDat;
    private TextField tfMaDonTextField;

    private PhongDTO phongDuocChon = null;
    private KhachHangDTO khachHangDuocChon = null;

    // --- BẢNG MÀU ---
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#1e293b";
    private final String COLOR_BORDER = "#cbd5e1";
    private final String COLOR_BG_LIGHT = "#f1f5f9";

    public QuanLyDatPhongController(IPhongService phongService, IPhieuDatPhongService phieuDatPhongService,
                                    IKhachHangService khachHangService, TaiKhoanDTO currentUser) {
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.khachHangService = khachHangService;
        this.currentUser = currentUser;
    }

    public Scene createQuanLyDatPhongScene() {
        VBox mainVBox = new VBox(20);
        mainVBox.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + "; -fx-padding: 25;");

        // 1. TIÊU ĐỀ
        Label lblTitle = new Label("🏨 ĐẶT & NHẬN PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        // 2. PANEL LỌC PHÒNG
        HBox filterPanel = createFilterPanel();

        // 3. DANH SÁCH PHÒNG
        HBox roomListPanel = createRoomListPanel();

        // 4. CHI TIẾT ĐẶT PHÒNG
        HBox bookingDetailPanel = createBookingDetailPanel();

        mainVBox.getChildren().addAll(lblTitle, filterPanel, roomListPanel, bookingDetailPanel);

        ScrollPane scrollPane = new ScrollPane(mainVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        // 👉 Tải mã phiếu lúc vừa mở giao diện
        refreshMaPhieu();

        return new Scene(scrollPane, 1400, 900);
    }

    // --- BỘ LỌC PHÒNG ---
    private HBox createFilterPanel() {
        HBox filterPanel = new HBox(20);
        filterPanel.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + ";");
        filterPanel.setPadding(new Insets(20));
        filterPanel.setAlignment(Pos.CENTER_LEFT);

        // Chọn Khách Hàng
        VBox vbKH = new VBox(8);
        Label lblKH = new Label("👤 Khách Hàng:");
        lblKH.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        cbKhachHang = new ComboBox<>();
        cbKhachHang.setPrefWidth(250);
        cbKhachHang.setStyle("-fx-border-radius: 5;");
        loadKhachHang();
        cbKhachHang.setOnAction(e -> khachHangDuocChon = cbKhachHang.getValue());
        vbKH.getChildren().addAll(lblKH, cbKhachHang);

        // Ngày Check-in
        VBox vbCheckIn = new VBox(8);
        Label lblCheckIn = new Label("📅 Ngày Nhận:");
        lblCheckIn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        dpCheckIn = new DatePicker();
        dpCheckIn.setValue(LocalDate.now());
        dpCheckIn.setStyle("-fx-border-radius: 5;");
        vbCheckIn.getChildren().addAll(lblCheckIn, dpCheckIn);

        // Ngày Check-out
        VBox vbCheckOut = new VBox(8);
        Label lblCheckOut = new Label("📅 Ngày Trả:");
        lblCheckOut.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        dpCheckOut = new DatePicker();
        dpCheckOut.setValue(LocalDate.now().plusDays(1));
        dpCheckOut.setStyle("-fx-border-radius: 5;");
        vbCheckOut.getChildren().addAll(lblCheckOut, dpCheckOut);

        // Nút tìm phòng
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnTim = new Button("🔍 Tìm Phòng Trống");
        btnTim.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnTim.setOnAction(e -> searchAvailableRooms());

        filterPanel.getChildren().addAll(vbKH, vbCheckIn, vbCheckOut, spacer, btnTim);
        return filterPanel;
    }

    // --- DANH SÁCH PHÒNG ---
    private HBox createRoomListPanel() {
        HBox listPanel = new HBox(15);
        listPanel.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + ";");
        listPanel.setPadding(new Insets(20));

        VBox vbList = new VBox(10);
        Label lblRooms = new Label("🛏️ PHÒNG TRỐNG");
        lblRooms.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        lvPhongTrong = new ListView<>();
        lvPhongTrong.setPrefHeight(300);
        lvPhongTrong.setStyle("-fx-border-radius: 5;");
        lvPhongTrong.setCellFactory(param -> new ListCell<PhongDTO>() {
            @Override
            protected void updateItem(PhongDTO phong, boolean empty) {
                super.updateItem(phong, empty);
                if (empty || phong == null) {
                    setText(null);
                } else {
                    setText(phong.getMaPhong() + " - " + phong.getTenPhong() + " (" + String.format("%,.0f", phong.getGiaPhong()) + "đ/đêm)");
                }
            }
        });

        lvPhongTrong.setOnMouseClicked(e -> {
            PhongDTO selected = lvPhongTrong.getSelectionModel().getSelectedItem();
            if (selected != null) {
                phongDuocChon = selected;
                displayRoomDetail(selected);
            }
        });

        vbList.getChildren().addAll(lblRooms, lvPhongTrong);
        listPanel.getChildren().add(vbList);
        HBox.setHgrow(vbList, Priority.ALWAYS);

        return listPanel;
    }

    // --- CHI TIẾT ĐẶT PHÒNG ---
    private HBox createBookingDetailPanel() {
        HBox detailPanel = new HBox(20);
        detailPanel.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + ";");
        detailPanel.setPadding(new Insets(20));

        // Form bên trái
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(400);

        Label lblDetail = new Label("📋 CHI TIẾT ĐẶT PHÒNG");
        lblDetail.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        VBox vbMaDon = new VBox(5);
        Label lbl1 = new Label("Mã Đơn:");
        lbl1.setFont(Font.font("Segoe UI", 11));
        tfMaDonTextField = new TextField();
        tfMaDonTextField.setEditable(false);
        tfMaDonTextField.setStyle("-fx-control-inner-background: #f1f5f9; -fx-border-radius: 5; -fx-font-weight: bold;");
        vbMaDon.getChildren().addAll(lbl1, tfMaDonTextField);

        VBox vbPhong = new VBox(5);
        Label lbl2 = new Label("Phòng Chọn:");
        lbl2.setFont(Font.font("Segoe UI", 11));
        lblPhongChon = new Label("(Chưa chọn phòng)");
        lblPhongChon.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        vbPhong.getChildren().addAll(lbl2, lblPhongChon);

        VBox vbTong = new VBox(5);
        Label lbl3 = new Label("Tổng Tiền (đ):");
        lbl3.setFont(Font.font("Segoe UI", 11));
        lblTongTien = new Label("0");
        lblTongTien.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SUCCESS + ";");
        vbTong.getChildren().addAll(lbl3, lblTongTien);

        formBox.getChildren().addAll(lblDetail, vbMaDon, vbPhong, vbTong);

        // Nút bên phải
        VBox btnBox = new VBox(10);
        btnBox.setAlignment(Pos.TOP_CENTER);
        btnBox.setPrefWidth(180);

        btnTaoDon = new Button("✅ TẠO PHIẾU ĐẶT");
        btnTaoDon.setMaxWidth(Double.MAX_VALUE);
        btnTaoDon.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5; -fx-font-size: 12px; -fx-cursor: hand;");
        btnTaoDon.setOnAction(e -> createBooking());

        btnHuyDat = new Button("❌ HUỶ ĐẶT");
        btnHuyDat.setMaxWidth(Double.MAX_VALUE);
        btnHuyDat.setStyle("-fx-background-color: " + COLOR_DANGER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5; -fx-font-size: 12px; -fx-cursor: hand;");
        btnHuyDat.setOnAction(e -> cancelBooking());

        btnBox.getChildren().addAll(btnTaoDon, btnHuyDat);

        detailPanel.getChildren().addAll(formBox, btnBox);
        return detailPanel;
    }

    // --- HỖ TRỢ ---
    private void loadKhachHang() {
        try {
            List<KhachHangDTO> list = khachHangService.getAllKhachHang();
            if (list != null) {
                cbKhachHang.setItems(FXCollections.observableArrayList(list));
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải khách hàng: " + ex.getMessage());
        }
    }

    private void searchAvailableRooms() {
        LocalDate checkIn = dpCheckIn.getValue();
        LocalDate checkOut = dpCheckOut.getValue();

        if (checkIn == null || checkOut == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn ngày nhận và trả!");
            return;
        }

        if (checkOut.isBefore(checkIn)) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Ngày trả phải sau ngày nhận!");
            return;
        }

        try {
            // Cập nhật hàm lọc phòng trống (Đảm bảo gọi hàm từ service chính xác)
            List<PhongDTO> phongTrong = phongService.findAvailableRooms(checkIn, checkOut, 0, Double.MAX_VALUE, null);
            lvPhongTrong.setItems(FXCollections.observableArrayList(phongTrong));
            if (phongTrong.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Không có phòng trống trong khoảng thời gian này!");
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi tìm phòng: " + ex.getMessage());
        }
    }

    private void displayRoomDetail(PhongDTO phong) {
        lblPhongChon.setText(phong.getMaPhong() + " - " + phong.getTenPhong() + " (" + String.format("%,.0f", phong.getGiaPhong()) + "đ/đêm)");

        // Tính tổng tiền
        if (dpCheckIn.getValue() != null && dpCheckOut.getValue() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
            if (days <= 0) days = 1;
            double total = days * phong.getGiaPhong();
            lblTongTien.setText(String.format("%,.0f", total));
        }
    }

    private void createBooking() {
        if (khachHangDuocChon == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn khách hàng!");
            return;
        }

        if (phongDuocChon == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn phòng!");
            return;
        }

        try {
            PhieuDatPhongDTO phieu = new PhieuDatPhongDTO();
            phieu.setMaPhieu(tfMaDonTextField.getText());
            phieu.setMaKhachHang(khachHangDuocChon.getMaKhachHang());
            phieu.setMaPhong(phongDuocChon.getMaPhong());
            phieu.setNgayDat(LocalDate.now());

            // 👉 FIX: Nâng cấp thành LocalDateTime để khớp chuẩn hệ thống (Mặc định: Nhận 14:00, Trả 12:00)
            LocalDateTime inTime = dpCheckIn.getValue().atTime(14, 0);
            LocalDateTime outTime = dpCheckOut.getValue().atTime(12, 0);
            phieu.setNgayNhan(inTime);
            phieu.setNgayTra(outTime);

            phieu.setTrangThai("CHO_NHAN_PHONG");

            String maNV = (currentUser != null) ? currentUser.getMaNhanVien() : "NV001";
            phieu.setMaNhanVien(maNV);

            long days = java.time.temporal.ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
            if (days <= 0) days = 1;
            double total = days * phongDuocChon.getGiaPhong();
            phieu.setTongTien(total);

            // 👉 FIX: Sử dụng bookRoomTransaction để lưu an toàn và gọi updatePhongTrangThai
            phieuDatPhongService.bookRoomTransaction(phieu);
            phongService.updatePhongTrangThai(phongDuocChon.getMaPhong(), "Đã Đặt");

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tạo phiếu đặt thành công!\nMã: " + phieu.getMaPhieu());

            // Reset form
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi tạo phiếu: " + ex.getMessage());
        }
    }

    private void cancelBooking() {
        clearForm();
    }

    private void clearForm() {
        phongDuocChon = null;
        khachHangDuocChon = null;
        cbKhachHang.setValue(null);
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));

        lblPhongChon.setText("(Chưa chọn phòng)");
        lblTongTien.setText("0");
        lvPhongTrong.getSelectionModel().clearSelection();
        lvPhongTrong.setItems(FXCollections.observableArrayList());

        // Tải mã phiếu mới
        refreshMaPhieu();
    }

    // 👉 FIX: Lấy mã chuẩn từ Database thay vì Random
    private void refreshMaPhieu() {
        Platform.runLater(() -> {
            try {
                String newId = phieuDatPhongService.phatSinhMaPhieuMoi();
                tfMaDonTextField.setText(newId);
            } catch (Exception e) {
                tfMaDonTextField.setText("Lỗi sinh mã");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}