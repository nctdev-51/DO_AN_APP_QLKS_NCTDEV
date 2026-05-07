package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ChiTietPhongController {

    private PhongDTO phong;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private TaiKhoanDTO currentUser;

    // Các services cần thiết
    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IKhachHangService khachHangService;
    private IHoaDonService hoaDonService;
    private IChiTietHoaDonService chiTietHoaDonService;
    private IDichVuService dichVuService;

    // Callback để load lại Main Dashboard sau khi thao tác
    private Runnable onRefreshDashboard;

    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_ACCENT = "#17a2b8";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_AVAILABLE = "#10b981";
    private final String COLOR_BOOKED = "#ef4444";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_OCCUPIED = "#f59e0b";
    private final String COLOR_MAINTENANCE = "#64748b";
    private final String COLOR_SUCCESS = "#10b981";

    public ChiTietPhongController(PhongDTO phong, LocalDate checkInDate, LocalDate checkOutDate, TaiKhoanDTO currentUser,
                                  IPhongService phongService, IPhieuDatPhongService phieuDatPhongService,
                                  IKhachHangService khachHangService, IHoaDonService hoaDonService,
                                  IChiTietHoaDonService chiTietHoaDonService, IDichVuService dichVuService,
                                  Runnable onRefreshDashboard) {
        this.phong = phong;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.currentUser = currentUser;
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.khachHangService = khachHangService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.dichVuService = dichVuService;
        this.onRefreshDashboard = onRefreshDashboard;
    }

    public void showDialog(Stage parentStage) {
        try {
            String trangThai = phong.getTinhTrang() != null ? phong.getTinhTrang().trim() : "Không xác định";

            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Phòng - " + phong.getMaPhong());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentStage);
            stage.setResizable(true);
            stage.setWidth(650);
            stage.setHeight(600);

            VBox mainContent = new VBox(0);
            mainContent.setStyle("-fx-background-color: #f8fafc;");

            // Header
            HBox header = createModalHeader(trangThai);
            mainContent.getChildren().add(header);

            // Content dựa trên trạng thái
            VBox content = new VBox(0);
            VBox.setVgrow(content, Priority.ALWAYS);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: white;");

            switch (trangThai) {
                case "Trống" -> content.getChildren().add(createEmptyRoomContent(stage));
                case "Đang ở" -> content.getChildren().add(createOccupiedRoomContent(stage));
                case "Đã Đặt" -> content.getChildren().add(createBookedRoomContent(stage));
                case "Bảo Trì" -> content.getChildren().add(createMaintenanceRoomContent(stage));
                default -> {
                    Label defaultLabel = new Label("Không xác định trạng thái phòng");
                    defaultLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
                    content.getChildren().add(defaultLabel);
                }
            }

            mainContent.getChildren().add(content);
            ScrollPane scrollPane = new ScrollPane(mainContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

            Scene scene = new Scene(scrollPane);
            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi", "Không thể hiển thị chi tiết phòng: " + ex.getMessage());
        }
    }

    private HBox createModalHeader(String trangThai) {
        HBox header = new HBox(15);
        header.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1 0; -fx-border-color: #e2e8f0; -fx-padding: 16 20;");
        header.setAlignment(Pos.CENTER_LEFT);

        String statusColorHex; String statusText;
        switch (trangThai) {
            case "Trống" -> { statusColorHex = COLOR_AVAILABLE; statusText = "✓ Có sẵn"; }
            case "Đã Đặt" -> { statusColorHex = COLOR_BOOKED; statusText = "📅 Đã đặt"; }
            case "Đang ở" -> { statusColorHex = COLOR_OCCUPIED; statusText = "🔑 Đang phục vụ"; }
            case "Bảo Trì" -> { statusColorHex = COLOR_MAINTENANCE; statusText = "🔧 Bảo trì"; }
            default -> { statusColorHex = COLOR_TEXT_MUTED; statusText = "❓ Không xác định"; }
        }

        Label lblMa = new Label(phong.getMaPhong());
        lblMa.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 20));
        lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblStatus = new Label(statusText);
        lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblStatus.setTextFill(Color.web(statusColorHex));
        lblStatus.setStyle("-fx-background-color: " + statusColorHex + "20; -fx-padding: 5 12; -fx-background-radius: 15;");

        Label lblLoai = new Label(phong.getMaLoaiPhong());
        lblLoai.setFont(Font.font("Segoe UI", 12));
        lblLoai.setTextFill(Color.web(COLOR_TEXT_MUTED));

        Label lblGia = new Label(String.format("%,.0f đ", phong.getGiaPhong()));
        lblGia.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblGia.setTextFill(Color.web(COLOR_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(lblMa, lblStatus, lblLoai, lblGia);
        return header;
    }

    private VBox createEmptyRoomContent(Stage parentStage) {
        VBox content = new VBox(18);
        Label title = new Label("Đặt Phòng Mới");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        title.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label subtitle = new Label("Phòng này có sẵn. Nhấn nút bên dưới để mở Form lập phiếu đặt phòng.");
        subtitle.setFont(Font.font("Segoe UI", 12));
        subtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        VBox infoBox = createInfoBox(
                new String[]{"Loại phòng", "Giá phòng", "Trạng thái"},
                new String[]{phong.getMaLoaiPhong(), String.format("%,.0f đ", phong.getGiaPhong()), "Có sẵn"}
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // NÚT BẤM KÍCH HOẠT GIAO DIỆN MỚI
        Button btnBook = new Button("🏨 Lập Phiếu Đặt Phòng");
        btnBook.setPrefWidth(Double.MAX_VALUE);
        btnBook.setPrefHeight(45);
        btnBook.setCursor(Cursor.HAND);
        btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8;");

        // Sự kiện hover
        btnBook.setOnMouseEntered(e -> btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "DD; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8;"));
        btnBook.setOnMouseExited(e -> btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8;"));

        btnBook.setOnAction(e -> {
            // Đóng cửa sổ chi tiết phòng hiện tại
            parentStage.close();

            try {
                // Tạo một List chứa mã phòng vừa được click
                List<String> listMaPhong = List.of(phong.getMaPhong());

                // 👉 CÁCH FIX: Ép kiểu/Trích xuất thông tin từ TaiKhoanDTO sang NhanVienDTO
                NhanVienDTO nvDTO = new NhanVienDTO();
                if (currentUser != null) {
                    nvDTO.setMaNhanVien(currentUser.getMaNhanVien());
                    nvDTO.setHoTen(currentUser.getHoTenNhanVien());
                }

                // Khởi tạo DatPhongController
                DatPhongController datPhongCtrl = new DatPhongController(
                        listMaPhong,        // Danh sách phòng cần đặt
                        nvDTO,              // 👉 ĐÃ FIX: Truyền nvDTO vào đây thay vì currentUser
                        checkInDate,        // Ngày nhận
                        checkOutDate,       // Ngày trả
                        khachHangService,
                        phongService,
                        phieuDatPhongService,
                        dichVuService,
                        onRefreshDashboard  // Hàm refresh dashboard
                );

                // Mở cửa sổ Lập phiếu đặt phòng mới
                Window mainWindow = btnBook.getScene().getWindow();
                datPhongCtrl.showDialog((Stage) mainWindow);

            } catch (Exception ex) {
                ex.printStackTrace();
                showMessage("Lỗi", "Không thể mở giao diện Đặt phòng: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(title, subtitle, infoBox, spacer, btnBook);
        return content;
    }

    private VBox createOccupiedRoomContent(Stage parentStage) {
        VBox content = new VBox(18);
        Label title = new Label("Chi Tiết Khách Đang Ở");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        List<PhieuDatPhongDTO> phieu = phieuDatPhongService.getPhieuDatPhongByPhong(phong.getMaPhong());
        PhieuDatPhongDTO currentPhieu = phieu.stream().filter(p -> "Nhận Phòng".equalsIgnoreCase(p.getTrangThai())).findFirst().orElse(null);

        if (currentPhieu != null) {
            KhachHangDTO khachHang = khachHangService.getKhachHangById(currentPhieu.getMaKhachHang());
            VBox khachBox = createInfoBox(
                    new String[]{"Khách", "Số điện thoại", "Nhận phòng", "Dự kiến trả"},
                    new String[]{khachHang.getHoTen(), khachHang.getSoDienThoai(), currentPhieu.getNgayNhan().toString(), currentPhieu.getNgayTra().toString()}
            );

            Label serviceTitle = new Label("Dịch Vụ Đã Sử Dụng");
            serviceTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            List<HoaDonDTO> hoaDons = hoaDonService.getHoaDonByPhieuDat(currentPhieu.getMaPhieu());
            VBox serviceBox = createServiceList(hoaDons);

            Label totalLabel = new Label(String.format("Tổng tiền hiện tại: %,.0f đ", currentPhieu.getTongTien()));
            totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            totalLabel.setTextFill(Color.web(COLOR_PRIMARY));
            totalLabel.setStyle("-fx-background-color: #f0f7ff; -fx-padding: 12 14; -fx-background-radius: 8;");

            HBox buttonBox = new HBox(12);
            Button btnCheckOut = createActionButton("💳 Trả Phòng", COLOR_BOOKED);
            final PhieuDatPhongDTO finalPhieu = currentPhieu;


            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            buttonBox.getChildren().addAll(btnCheckOut);
            content.getChildren().addAll(title, khachBox, serviceTitle, serviceBox, totalLabel, spacer, buttonBox);
        }
        return content;
    }

    private VBox createBookedRoomContent(Stage parentStage) {
        VBox content = new VBox(18);
        Label title = new Label("Thông Tin Đặt Chỗ");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        List<PhieuDatPhongDTO> phieu = phieuDatPhongService.getPhieuDatPhongByPhong(phong.getMaPhong());
        PhieuDatPhongDTO bookedPhieu = phieu.stream().filter(p -> "Đặt Phòng".equalsIgnoreCase(p.getTrangThai())).findFirst().orElse(null);

        if (bookedPhieu != null) {
            KhachHangDTO khachHang = khachHangService.getKhachHangById(bookedPhieu.getMaKhachHang());
            VBox bookingBox = createInfoBox(
                    new String[]{"Mã phiếu", "Khách hàng", "Ngày nhận", "Ngày trả", "Tổng giá"},
                    new String[]{bookedPhieu.getMaPhieu(), khachHang.getHoTen(), bookedPhieu.getNgayNhan().toString(), bookedPhieu.getNgayTra().toString(), String.format("%,.0f đ", bookedPhieu.getTongTien())}
            );

            HBox buttonBox = new HBox(12);
            Button btnCheckIn = createActionButton("🔑 Nhận Phòng (Check-in)", COLOR_AVAILABLE);
            Button btnCancel = createActionButton("❌ Hủy Đặt Phòng", COLOR_DANGER);

            final PhieuDatPhongDTO finalPhieu = bookedPhieu;
            btnCheckIn.setOnAction(e -> {
                QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(phieuDatPhongService, phongService, khachHangService, hoaDonService, chiTietHoaDonService, dichVuService, currentUser);
                if (controller.processCheckIn(finalPhieu.getMaPhieu())) { parentStage.close(); if(onRefreshDashboard != null) onRefreshDashboard.run(); }
            });
            btnCancel.setOnAction(e -> {
                QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(phieuDatPhongService, phongService, khachHangService, hoaDonService, chiTietHoaDonService, dichVuService, currentUser);
                if (controller.cancelBooking(finalPhieu.getMaPhieu())) { parentStage.close(); if(onRefreshDashboard != null) onRefreshDashboard.run(); }
            });

            Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
            buttonBox.getChildren().addAll(btnCheckIn, btnCancel);
            content.getChildren().addAll(title, bookingBox, spacer, buttonBox);
        }
        return content;
    }

    private VBox createMaintenanceRoomContent(Stage parentStage) {
        VBox content = new VBox(18);
        Label title = new Label("Thông Tin Bảo Trì");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        VBox infoBox = createInfoBox(new String[]{"Phòng", "Loại phòng", "Trạng thái"}, new String[]{phong.getMaPhong(), phong.getMaLoaiPhong(), "Bảo Trì"});

        Button btnComplete = createActionButton("✅ Hoàn Thành Bảo Trì", COLOR_AVAILABLE);
        btnComplete.setOnAction(e -> {
            QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(phieuDatPhongService, phongService, khachHangService, hoaDonService, chiTietHoaDonService, dichVuService, currentUser);
            if (controller.completeMaintenance(phong.getMaPhong())) { parentStage.close(); if(onRefreshDashboard != null) onRefreshDashboard.run(); }
        });

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        content.getChildren().addAll(title, infoBox, spacer, btnComplete);
        return content;
    }

    private void showBookingDialog(Stage parentStage) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Đặt Phòng - " + phong.getMaPhong());
            dialog.setResizable(false); dialog.setWidth(500); dialog.setHeight(450);

            VBox mainContent = new VBox(15); mainContent.setPadding(new Insets(20));
            Label title = new Label("Đặt Phòng Mới"); title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

            ComboBox<String> cbKhachHang = new ComboBox<>(); cbKhachHang.setMaxWidth(Double.MAX_VALUE);
            List<KhachHangDTO> khachs = khachHangService.getAllKhachHang();
            for (KhachHangDTO kh : khachs) cbKhachHang.getItems().add(kh.getMaKhachHang() + " - " + kh.getHoTen());

            DatePicker dpNhanPhong = new DatePicker(checkInDate != null ? checkInDate : LocalDate.now()); dpNhanPhong.setMaxWidth(Double.MAX_VALUE);
            DatePicker dpTraPhong = new DatePicker(checkOutDate != null ? checkOutDate : LocalDate.now().plusDays(1)); dpTraPhong.setMaxWidth(Double.MAX_VALUE);

            Button btnBook = new Button("✅ Đặt Phòng");
            btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
            btnBook.setOnAction(e -> {
                if (cbKhachHang.getValue() == null) { showMessage("Lỗi", "Vui lòng chọn khách hàng"); return; }
                String maKhach = cbKhachHang.getValue().split(" - ")[0];
                QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(phieuDatPhongService, phongService, khachHangService, hoaDonService, chiTietHoaDonService, dichVuService, currentUser);
                if (controller.processBooking(maKhach, phong.getMaPhong(), dpNhanPhong.getValue(), dpTraPhong.getValue())) {
                    dialog.close(); parentStage.close(); if(onRefreshDashboard != null) onRefreshDashboard.run();
                }
            });

            mainContent.getChildren().addAll(title, new Label("Khách Hàng:"), cbKhachHang, new Label("Ngày Nhận:"), dpNhanPhong, new Label("Ngày Trả:"), dpTraPhong, btnBook);
            dialog.setScene(new Scene(mainContent)); dialog.showAndWait();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Các hàm Helper (Giữ nguyên)
    private VBox createInfoBox(String[] labels, String[] values) {
        VBox box = new VBox(10); box.setPadding(new Insets(14)); box.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0;");
        for (int i = 0; i < labels.length; i++) {
            HBox row = new HBox(10); row.setAlignment(Pos.CENTER_LEFT);
            Label l = new Label(labels[i] + ":"); l.setPrefWidth(140);
            Label v = new Label(values[i]); v.setFont(Font.font("Segoe UI", 12));
            row.getChildren().addAll(l, v); box.getChildren().add(row);
        }
        return box;
    }

    private VBox createServiceList(List<HoaDonDTO> hoaDons) {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #e2e8f0;");
        if (hoaDons.isEmpty()) box.getChildren().add(new Label("Chưa có dịch vụ nào."));
        else {
            for (HoaDonDTO hd : hoaDons) {
                HBox row = new HBox(10);
                row.getChildren().addAll(new Label("• Dịch vụ"), new Region(), new Label(String.format("%,.0f đ", hd.getTongTienDichVu())));
                box.getChildren().add(row);
            }
        }
        return box;
    }

    private Button createActionButton(String text, String colorHex) {
        Button btn = new Button(text); btn.setPrefHeight(42); btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7;");
        return btn;
    }

    private void showMessage(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setContentText(message); a.show();
    }
}