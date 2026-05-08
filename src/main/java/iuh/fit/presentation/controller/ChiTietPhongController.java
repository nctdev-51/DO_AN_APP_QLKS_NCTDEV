package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class ChiTietPhongController {

    private PhongDTO phong;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private TaiKhoanDTO currentUser;

    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IKhachHangService khachHangService;
    private IHoaDonService hoaDonService;
    private IChiTietHoaDonService chiTietHoaDonService;
    private IDichVuService dichVuService;

    private Runnable onRefreshDashboard;

    // Bảng màu thiết kế chuẩn (Đồng bộ với hệ thống)
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_AVAILABLE = "#10b981"; // Xanh lá - Trống
    private final String COLOR_OCCUPIED = "#f59e0b";  // Cam - Đang ở
    private final String COLOR_BOOKED = "#3b82f6";    // Xanh dương - Đã đặt
    private final String COLOR_MAINTENANCE = "#64748b"; // Xám - Bảo trì
    private final String COLOR_DANGER = "#ef4444";    // Đỏ - Hủy/Xóa
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BG = "#f1f5f9";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            String trangThai = getNormalizedStatus(phong.getTinhTrang());

            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Phòng - " + phong.getMaPhong());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentStage);
            stage.setResizable(false);
            stage.setWidth(550);
            stage.setHeight(650);

            VBox mainContent = new VBox();
            mainContent.setStyle("-fx-background-color: " + COLOR_BG + ";");

            // Header động theo trạng thái
            HBox header = createModalHeader(trangThai);
            mainContent.getChildren().add(header);

            // Vùng nội dung
            VBox content = new VBox(20);
            content.setPadding(new Insets(25));
            VBox.setVgrow(content, Priority.ALWAYS);

            switch (trangThai) {
                case "TRONG" -> content.getChildren().add(createEmptyRoomContent(stage));
                case "DANG_O" -> content.getChildren().add(createOccupiedRoomContent(stage));
                case "DA_DAT" -> content.getChildren().add(createBookedRoomContent(stage));
                case "BAO_TRI" -> content.getChildren().add(createMaintenanceRoomContent(stage));
                default -> content.getChildren().add(new Label("Trạng thái phòng không hợp lệ."));
            }

            mainContent.getChildren().add(content);

            Scene scene = new Scene(mainContent);
            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi Hệ Thống", "Không thể hiển thị chi tiết phòng: " + ex.getMessage());
        }
    }

    // =====================================================================
    // 1. HEADER (TIÊU ĐỀ POPUP)
    // =====================================================================
    private HBox createModalHeader(String statusKey) {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 25, 20, 25));

        String colorHex, text, icon;
        switch (statusKey) {
            case "TRONG" -> { colorHex = COLOR_AVAILABLE; text = "Có Sẵn"; icon = "✨"; }
            case "DA_DAT" -> { colorHex = COLOR_BOOKED; text = "Đã Đặt Trước"; icon = "📅"; }
            case "DANG_O" -> { colorHex = COLOR_OCCUPIED; text = "Đang Phục Vụ"; icon = "🔑"; }
            default -> { colorHex = COLOR_MAINTENANCE; text = "Đang Bảo Trì"; icon = "🔧"; }
        }

        header.setStyle("-fx-background-color: white; -fx-border-width: 0 0 4 0; -fx-border-color: " + colorHex + ";");

        VBox titleBox = new VBox(5);
        Label lblMa = new Label(icon + " PHÒNG " + phong.getMaPhong());
        lblMa.setFont(Font.font("Segoe UI", FontWeight.BLACK, 24));
        lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblLoai = new Label("Loại: " + (phong.getMaLoaiPhong() != null ? phong.getMaLoaiPhong().toUpperCase() : "TIÊU CHUẨN"));
        lblLoai.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblLoai.setTextFill(Color.web(COLOR_TEXT_MUTED));
        titleBox.getChildren().addAll(lblMa, lblLoai);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox priceBox = new VBox(5);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        Label lblStatus = new Label(text);
        lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblStatus.setTextFill(Color.web(colorHex));
        lblStatus.setStyle("-fx-background-color: " + colorHex + "15; -fx-padding: 4 10; -fx-background-radius: 12;");

        Label lblGia = new Label(String.format("%,.0f đ", phong.getGiaPhong()));
        lblGia.setFont(Font.font("Segoe UI", FontWeight.BLACK, 18));
        lblGia.setTextFill(Color.web(COLOR_PRIMARY));
        priceBox.getChildren().addAll(lblStatus, lblGia);

        header.getChildren().addAll(titleBox, spacer, priceBox);
        return header;
    }

    // =====================================================================
    // 2. PHÒNG TRỐNG
    // =====================================================================
    private VBox createEmptyRoomContent(Stage parentStage) {
        VBox content = new VBox(20);

        VBox card = createCardBackground();
        Label lblMsg = new Label("Phòng đã được dọn dẹp và sẵn sàng đón khách.");
        lblMsg.setFont(Font.font("Segoe UI", 14));
        lblMsg.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblMsg.setWrapText(true);
        card.getChildren().add(lblMsg);

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnBook = createActionButton("🏨 LẬP PHIẾU ĐẶT PHÒNG NHANH", COLOR_AVAILABLE);
        btnBook.setOnAction(e -> {
            parentStage.close();
            try {
                NhanVienDTO nvDTO = new NhanVienDTO();
                if (currentUser != null) {
                    nvDTO.setMaNhanVien(currentUser.getMaNhanVien());
                    nvDTO.setHoTen(currentUser.getHoTenNhanVien());
                }
                DatPhongController datPhongCtrl = new DatPhongController(
                        List.of(phong.getMaPhong()), nvDTO, checkInDate, checkOutDate,
                        khachHangService, phongService, phieuDatPhongService, dichVuService, onRefreshDashboard
                );
                datPhongCtrl.showDialog((Stage) btnBook.getScene().getWindow());
            } catch (Exception ex) { showMessage("Lỗi", "Không thể mở form đặt phòng."); }
        });

        content.getChildren().addAll(card, spacer, btnBook);
        return content;
    }

    // =====================================================================
    // 3. PHÒNG ĐANG Ở (ĐÃ TÍCH HỢP THANH TOÁN QR TRỰC TIẾP)
    // =====================================================================
    private VBox createOccupiedRoomContent(Stage parentStage) {
        VBox content = new VBox(20);

        List<PhieuDatPhongDTO> phieus = phieuDatPhongService.getPhieuDatPhongByPhong(phong.getMaPhong());
        PhieuDatPhongDTO currentPhieu = null;

        if (phieus != null) {
            currentPhieu = phieus.stream()
                    .filter(p -> {
                        if (p.getTrangThai() == null) return false;
                        String st = p.getTrangThai().toUpperCase();
                        return st.contains("NHẬN PHÒNG") || st.contains("NHAN PHONG") || st.equals("DANG_O") || st.equals("DA_NHAN_PHONG");
                    })
                    .max(Comparator.comparing(PhieuDatPhongDTO::getNgayNhan, Comparator.nullsFirst(Comparator.naturalOrder())))
                    .orElse(null);
        }

        if (currentPhieu != null) {
            KhachHangDTO khachHang = khachHangService.getKhachHangById(currentPhieu.getMaKhachHang());
            String tenKhach = (khachHang != null && khachHang.getHoTen() != null) ? khachHang.getHoTen() : "Khách vãng lai";
            String sdtKhach = (khachHang != null && khachHang.getSoDienThoai() != null) ? khachHang.getSoDienThoai() : "Không có";

            VBox cardKhach = createCardBackground();
            Label lblTitle1 = new Label("THÔNG TIN LƯU TRÚ");
            lblTitle1.setFont(Font.font("Segoe UI", FontWeight.BLACK, 13));
            lblTitle1.setTextFill(Color.web(COLOR_TEXT_MUTED));

            cardKhach.getChildren().addAll(
                    lblTitle1, new Separator(),
                    createRow("👤 Khách đại diện:", tenKhach),
                    createRow("📞 Số điện thoại:", sdtKhach),
                    createRow("🕒 Giờ Check-in:", currentPhieu.getNgayNhan() != null ? currentPhieu.getNgayNhan().format(DATE_FORMATTER) : "N/A"),
                    createRow("⏳ Dự kiến Check-out:", currentPhieu.getNgayTra() != null ? currentPhieu.getNgayTra().format(DATE_FORMATTER) : "N/A")
            );

            // Tính toán tài chính
            double tongTien = currentPhieu.getTongTien() != null ? currentPhieu.getTongTien() : 0.0;
            double tienCoc = currentPhieu.getTienCoc() != null ? currentPhieu.getTienCoc() : 0.0;

            VBox cardTien = createCardBackground();
            cardTien.setStyle("-fx-background-color: #fffbeb; -fx-background-radius: 12; -fx-border-color: #fde68a; -fx-border-radius: 12; -fx-padding: 20;");

            Label lblTitle2 = new Label("TẠM TÍNH CHI PHÍ");
            lblTitle2.setFont(Font.font("Segoe UI", FontWeight.BLACK, 13));
            lblTitle2.setTextFill(Color.web("#b45309"));

            Label lblTotal = new Label(String.format("%,.0f VNĐ", tongTien));
            lblTotal.setFont(Font.font("Segoe UI", FontWeight.BLACK, 24));
            lblTotal.setTextFill(Color.web(COLOR_OCCUPIED));

            Label lblCoc = new Label(String.format("Đã cọc: %,.0f VNĐ", tienCoc));
            lblCoc.setFont(Font.font("Segoe UI", 13));
            lblCoc.setTextFill(Color.web(COLOR_TEXT_MUTED));

            cardTien.getChildren().addAll(lblTitle2, lblTotal, lblCoc);

            Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

            Button btnCheckout = createActionButton("💳 THANH TOÁN & TRẢ PHÒNG", COLOR_OCCUPIED);
            final PhieuDatPhongDTO finalPhieu = currentPhieu;

            // 👉 ĐÃ XÓA HOÀN TOÀN TỪ KHÓA processCheckOut VÀ THAY BẰNG CỔNG THANH TOÁN QR
            // 👉 ĐÃ FIX: Logic Đóng Popup trước -> Mở Thanh Toán sau
            btnCheckout.setOnAction(e -> {
                double canThanhToan = tongTien - tienCoc;
                if (canThanhToan < 0) canThanhToan = 0;

                // Lấy cửa sổ chính của phần mềm (Nằm phía dưới popup)
                Stage mainWindow = (Stage) parentStage.getOwner();
                if (mainWindow == null) mainWindow = parentStage;

                // 1. Đóng Popup Chi tiết phòng NGAY LẬP TỨC để tránh xung đột
                parentStage.close();

                // 2. Mở Cổng thanh toán (Hệ thống sẽ tự nhúng đè lên giao diện Sơ đồ phòng)
                ThanhToanController paymentCtrl = new ThanhToanController(canThanhToan);

                paymentCtrl.showThanhToanDialog(mainWindow, phuongThuc -> {
                    if (phuongThuc != null) { // Khách đã nhấn Hoàn tất
                        try {
                            // Cập nhật trạng thái phòng thành Trống
                            phong.setTinhTrang("Trống");
                            phongService.updatePhong(phong);

                            // Tải lại màn hình Sơ đồ
                            if (onRefreshDashboard != null) onRefreshDashboard.run();

                            showMessage("Thành Công", "Đã thanh toán bằng " + phuongThuc + " và trả phòng hoàn tất!");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showMessage("Lỗi Hệ Thống", "Gặp sự cố khi cập nhật dữ liệu: " + ex.getMessage());
                        }
                    } else {
                        // Nếu khách nhấn nút "Hủy thanh toán", chỉ cần refresh lại sơ đồ
                        if (onRefreshDashboard != null) onRefreshDashboard.run();
                    }
                });
            });

            content.getChildren().addAll(cardKhach, cardTien, spacer, btnCheckout);
        } else {
            content.getChildren().add(new Label("⚠ Lỗi: Hệ thống báo phòng Đang Ở nhưng không tìm thấy Phiếu Đặt Phòng nào khớp!"));
        }
        return content;
    }

    // =====================================================================
    // 4. PHÒNG ĐÃ ĐẶT (CHỜ CHECK-IN)
    // =====================================================================
    private VBox createBookedRoomContent(Stage parentStage) {
        VBox content = new VBox(20);

        List<PhieuDatPhongDTO> phieus = phieuDatPhongService.getPhieuDatPhongByPhong(phong.getMaPhong());
        PhieuDatPhongDTO bookedPhieu = null;
        if (phieus != null) {
            bookedPhieu = phieus.stream()
                    .filter(p -> {
                        if (p.getTrangThai() == null) return false;
                        String st = p.getTrangThai().toUpperCase();
                        return st.contains("ĐẶT PHÒNG") || st.contains("DAT PHONG") || st.contains("CHƯA NHẬN") || st.equals("DA_DAT");
                    })
                    .findFirst().orElse(null);
        }

        if (bookedPhieu != null) {
            KhachHangDTO khachHang = khachHangService.getKhachHangById(bookedPhieu.getMaKhachHang());
            String tenKhach = (khachHang != null && khachHang.getHoTen() != null) ? khachHang.getHoTen() : "Không rõ";

            VBox card = createCardBackground();
            Label lblTitle1 = new Label("THÔNG TIN ĐẶT CHỖ");
            lblTitle1.setFont(Font.font("Segoe UI", FontWeight.BLACK, 13));
            lblTitle1.setTextFill(Color.web(COLOR_TEXT_MUTED));

            card.getChildren().addAll(
                    lblTitle1, new Separator(),
                    createRow("🔖 Mã phiếu:", bookedPhieu.getMaPhieu()),
                    createRow("👤 Khách đặt:", tenKhach),
                    createRow("🕒 Ngày Check-in:", bookedPhieu.getNgayNhan() != null ? bookedPhieu.getNgayNhan().format(DATE_FORMATTER) : "N/A"),
                    createRow("💰 Đã cọc:", String.format("%,.0f VNĐ", bookedPhieu.getTienCoc() != null ? bookedPhieu.getTienCoc() : 0.0))
            );

            Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

            HBox btnBox = new HBox(15);
            Button btnCheckin = createActionButton("🔑 CHECK-IN NGAY", COLOR_AVAILABLE);
            Button btnCancel = createActionButton("❌ HỦY ĐẶT", COLOR_DANGER);
            btnCancel.setStyle("-fx-background-color: transparent; -fx-border-color: " + COLOR_DANGER + "; -fx-text-fill: " + COLOR_DANGER + "; -fx-font-weight: bold; -fx-border-radius: 8; -fx-background-radius: 8;");

            HBox.setHgrow(btnCheckin, Priority.ALWAYS);
            HBox.setHgrow(btnCancel, Priority.ALWAYS);
            btnBox.getChildren().addAll(btnCancel, btnCheckin);

            final PhieuDatPhongDTO finalP = bookedPhieu;
            btnCheckin.setOnAction(e -> { /* Logic Checkin của bạn */ });
            btnCancel.setOnAction(e -> { /* Logic Cancel của bạn */ });

            content.getChildren().addAll(card, spacer, btnBox);
        } else {
            content.getChildren().add(new Label("⚠ Không tìm thấy phiếu đặt chỗ tương ứng!"));
        }
        return content;
    }

    // =====================================================================
    // 5. PHÒNG BẢO TRÌ
    // =====================================================================
    private VBox createMaintenanceRoomContent(Stage parentStage) {
        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);

        Label icon = new Label("🛠️");
        icon.setFont(Font.font(80));

        Label lblTitle = new Label("Đang Trong Quá Trình Bảo Trì");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 20));
        lblTitle.setTextFill(Color.web(COLOR_MAINTENANCE));

        Label lblMsg = new Label("Phòng này đang được khóa để sửa chữa hoặc vệ sinh chuyên sâu.\nKhông thể xếp khách vào lúc này.");
        lblMsg.setFont(Font.font("Segoe UI", 14));
        lblMsg.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblMsg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnComplete = createActionButton("✅ HOÀN TẤT BẢO TRÌ (MỞ LẠI PHÒNG)", COLOR_AVAILABLE);
        btnComplete.setOnAction(e -> {
            try {
                // Logic hoàn tất bảo trì, update phòng thành "Trống"
                phong.setTinhTrang("Trống");
                phongService.updatePhong(phong);
                parentStage.close();
                if(onRefreshDashboard != null) onRefreshDashboard.run();
                showMessage("Thành công", "Phòng đã sẵn sàng hoạt động trở lại!");
            } catch (Exception ex) {
                showMessage("Lỗi", "Không thể cập nhật phòng.");
            }
        });

        content.getChildren().addAll(icon, lblTitle, lblMsg, spacer, btnComplete);
        return content;
    }

    // =====================================================================
    // HELPER UI METHODS
    // =====================================================================
    private VBox createCardBackground() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12;");
        DropShadow ds = new DropShadow(8, Color.web("#000000", 0.04));
        ds.setOffsetY(3);
        box.setEffect(ds);
        return box;
    }

    private HBox createRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lblK = new Label(label);
        lblK.setFont(Font.font("Segoe UI", 14));
        lblK.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblK.setPrefWidth(140);

        Label lblV = new Label(value);
        lblV.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblV.setTextFill(Color.web(COLOR_TEXT_MAIN));
        lblV.setWrapText(true);

        row.getChildren().addAll(lblK, lblV);
        return row;
    }

    private Button createActionButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(45);
        btn.setCursor(Cursor.HAND);
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + colorHex + "DD; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8;"));
        return btn;
    }

    private String getNormalizedStatus(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "BAO_TRI";

        // Xử lý chuỗi: xóa khoảng trắng, đưa về chữ thường để so sánh cho chuẩn
        String s = raw.trim().toLowerCase();

        // 1. So sánh trực tiếp chữ Tiếng Việt (Cách này an toàn nhất)
        if (s.equals("trống") || s.equals("trong")) return "TRONG";
        if (s.equals("đã đặt") || s.equals("da dat")) return "DA_DAT";
        if (s.equals("đang ở") || s.equals("dang o") || s.equals("đang phục vụ")) return "DANG_O";
        if (s.equals("bảo trì") || s.equals("bao tri")) return "BAO_TRI";

        // 2. Phương án dự phòng dùng Regex lọc dấu nếu các chữ trên không khớp
        String normalized = removeAccents(s).toUpperCase();
        if (normalized.contains("TRONG")) return "TRONG";
        if (normalized.contains("DAT")) return "DA_DAT";
        if (normalized.contains("DANG") || normalized.equals("O")) return "DANG_O";

        return "BAO_TRI"; // Nếu không khớp cái nào mới hiện Bảo trì
    }

    // Thêm hàm này vào cuối file ChiTietPhongController.java
    private String removeAccents(String input) {
        if (input == null) return "";
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        return java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("")
                .replace('đ', 'd')
                .replace('Đ', 'D');
    }

    private void showMessage(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.show();
    }
}