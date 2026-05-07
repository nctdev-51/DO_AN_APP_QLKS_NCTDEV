package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class QuanLyPhieuDatPhongController {

    private IPhieuDatPhongService phieuDatPhongService;
    private IPhongService phongService;
    private IKhachHangService khachHangService;
    private TaiKhoanDTO currentUser;

    private ObservableList<PhieuDatPhongDTO> phieuList;
    private FilteredList<PhieuDatPhongDTO> filteredList;

    // UI Components
    private TextField txtTimKiemSDT;
    private DatePicker dpTuNgay, dpDenNgay;
    private FlowPane cardContainer; // Khu vực chứa các thẻ phiếu

    // --- BẢNG MÀU UI/UX HIỆN ĐẠI ---
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_PRIMARY_HOVER = "#1d4ed8";
    private final String COLOR_SUCCESS = "#10b981"; // Đã nhận
    private final String COLOR_WARNING = "#f59e0b"; // Chờ nhận
    private final String COLOR_DANGER = "#ef4444";  // Đã hủy
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QuanLyPhieuDatPhongController(IPhieuDatPhongService phieuDatPhongService,
                                         IPhongService phongService,
                                         IKhachHangService khachHangService,
                                         TaiKhoanDTO currentUser) {
        this.phieuDatPhongService = phieuDatPhongService;
        this.phongService = phongService;
        this.khachHangService = khachHangService;
        this.currentUser = currentUser;
    }

    public VBox createQuanLyPhieuView() {
        VBox rootPane = new VBox(20);
        rootPane.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-padding: 30;");

        // --- 1. HEADER ---
        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("QUẢN LÝ PHIẾU ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Tìm kiếm, theo dõi và cập nhật trạng thái các phiếu đặt phòng");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        headerBox.getChildren().addAll(lblTitle, lblSubTitle);

        // --- 2. BỘ LỌC TÌM KIẾM ---
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-padding: 15 20; -fx-background-radius: 10; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10;");

        DropShadow shadow = new DropShadow(); shadow.setColor(Color.web("#000000", 0.04)); shadow.setRadius(10); shadow.setOffsetY(3);
        filterBox.setEffect(shadow);

        String inputStyle = "-fx-padding: 8 12; -fx-background-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-font-size: 13px; -fx-background-color: #f8fafc;";

        VBox boxTuNgay = new VBox(5);
        Label lblTu = new Label("Từ ngày nhận:"); lblTu.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12)); lblTu.setTextFill(Color.web(COLOR_TEXT_MUTED));
        dpTuNgay = new DatePicker(); dpTuNgay.setStyle(inputStyle); dpTuNgay.setPrefWidth(140);
        boxTuNgay.getChildren().addAll(lblTu, dpTuNgay);

        VBox boxDenNgay = new VBox(5);
        Label lblDen = new Label("Đến ngày nhận:"); lblDen.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12)); lblDen.setTextFill(Color.web(COLOR_TEXT_MUTED));
        dpDenNgay = new DatePicker(); dpDenNgay.setStyle(inputStyle); dpDenNgay.setPrefWidth(140);
        boxDenNgay.getChildren().addAll(lblDen, dpDenNgay);

        VBox boxSDT = new VBox(5);
        Label lblSDT = new Label("SĐT Khách hàng:"); lblSDT.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12)); lblSDT.setTextFill(Color.web(COLOR_TEXT_MUTED));
        txtTimKiemSDT = new TextField(); txtTimKiemSDT.setPromptText("Nhập số điện thoại..."); txtTimKiemSDT.setStyle(inputStyle); txtTimKiemSDT.setPrefWidth(180);
        boxSDT.getChildren().addAll(lblSDT, txtTimKiemSDT);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnTim = new Button("🔍 Áp dụng lọc");
        btnTim.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnTim.setOnAction(e -> filterData());

        Button btnLamMoi = new Button("🔄 Làm mới");
        btnLamMoi.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: " + COLOR_TEXT_MAIN + "; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnLamMoi.setOnAction(e -> {
            dpTuNgay.setValue(null);
            dpDenNgay.setValue(null);
            txtTimKiemSDT.clear();
            loadDataToView();
        });

        HBox boxButtons = new HBox(10);
        boxButtons.setAlignment(Pos.BOTTOM_RIGHT); // Căn nút bấm ngang hàng với ô nhập liệu
        boxButtons.getChildren().addAll(btnTim, btnLamMoi);

        filterBox.getChildren().addAll(boxTuNgay, boxDenNgay, boxSDT, spacer, boxButtons);

        // --- 3. KHU VỰC HIỂN THỊ DANH SÁCH (THẺ CARD) ---
        cardContainer = new FlowPane();
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // --- GỘP TẤT CẢ ---
        rootPane.getChildren().addAll(headerBox, filterBox, scrollPane);

        // Tải dữ liệu lần đầu
        loadDataToView();

        return rootPane;
    }

    // =========================================================================
    // XỬ LÝ DỮ LIỆU & BỘ LỌC
    // =========================================================================

    private void loadDataToView() {
        try {
            // Giả định service có hàm lấy tất cả phiếu
            List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();
            phieuList = FXCollections.observableArrayList(allPhieu);
            filteredList = new FilteredList<>(phieuList, p -> true);
            renderCards();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu phiếu đặt phòng!");
        }
    }

    private void filterData() {
        if (filteredList == null) return;

        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();
        String keywordSDT = txtTimKiemSDT.getText().trim();

        filteredList.setPredicate(phieu -> {
            boolean matchNgay = true;
            if (tuNgay != null && phieu.getNgayNhan() != null) {
                matchNgay = !phieu.getNgayNhan().isBefore(tuNgay);
            }
            if (matchNgay && denNgay != null && phieu.getNgayNhan() != null) {
                matchNgay = !phieu.getNgayNhan().isAfter(denNgay);
            }

            boolean matchSDT = true;
            if (!keywordSDT.isEmpty()) {
                // Phải lấy SDT thông qua KhachHangService vì PhieuDatPhongDTO chỉ có maKhachHang
                try {
                    String sdtKhach = khachHangService.getKhachHangById(phieu.getMaKhachHang()).getSoDienThoai();
                    matchSDT = sdtKhach.contains(keywordSDT);
                } catch (Exception e) {
                    matchSDT = false; // Lỗi tìm khách -> bỏ qua phiếu này
                }
            }
            return matchNgay && matchSDT;
        });

        renderCards();
    }

    // =========================================================================
    // VẼ GIAO DIỆN CÁC THẺ (CARD RENDERER)
    // =========================================================================

    private void renderCards() {
        cardContainer.getChildren().clear();

        if (filteredList.isEmpty()) {
            Label lblEmpty = new Label("📭 Không tìm thấy phiếu đặt phòng nào phù hợp.");
            lblEmpty.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
            lblEmpty.setTextFill(Color.web(COLOR_TEXT_MUTED));
            lblEmpty.setPadding(new Insets(40));
            cardContainer.getChildren().add(lblEmpty);
            return;
        }

        for (PhieuDatPhongDTO phieu : filteredList) {
            cardContainer.getChildren().add(createPhieuCard(phieu));
        }
    }

    private VBox createPhieuCard(PhieuDatPhongDTO phieu) {
        VBox card = new VBox(10);
        card.setPrefWidth(350); // Chiều rộng mỗi thẻ
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12;");

        DropShadow shadow = new DropShadow(); shadow.setColor(Color.web("#000000", 0.05)); shadow.setRadius(8); shadow.setOffsetY(3);
        card.setEffect(shadow);

        // --- MÀU SẮC DỰA TRÊN TRẠNG THÁI ---
        String statusText = phieu.getTrangThai() != null ? phieu.getTrangThai().trim() : "Chờ nhận";
        String statusColorHex = COLOR_WARNING; // Mặc định là Chờ
        if (statusText.equalsIgnoreCase("Đã nhận") || statusText.equalsIgnoreCase("Đang ở")) {
            statusColorHex = COLOR_SUCCESS;
            statusText = "Đã Nhận Phòng";
        } else if (statusText.equalsIgnoreCase("Đã hủy") || statusText.equalsIgnoreCase("Hủy")) {
            statusColorHex = COLOR_DANGER;
            statusText = "Đã Hủy Phiếu";
        } else {
            statusText = "Chờ Nhận Phòng";
        }

        // Tạo viền mỏng bên trái để phân biệt nhanh (Tương tự Border MatteBorder trong Swing)
        card.setStyle(card.getStyle() + "-fx-border-width: 1 1 1 5; -fx-border-color: " + COLOR_BORDER + " " + COLOR_BORDER + " " + COLOR_BORDER + " " + statusColorHex + ";");

        // --- HEADER CỦA CARD (Mã & Trạng thái) ---
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_LEFT);

        Label lblMa = new Label(phieu.getMaPhieu());
        lblMa.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));
        lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblStatus = new Label("• " + statusText);
        lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblStatus.setTextFill(Color.web(statusColorHex));
        lblStatus.setStyle("-fx-background-color: " + statusColorHex + "1A; -fx-padding: 4 10; -fx-background-radius: 15;");

        Region topSpacer = new Region(); HBox.setHgrow(topSpacer, Priority.ALWAYS);
        topBox.getChildren().addAll(lblMa, topSpacer, lblStatus);

        // --- NỘI DUNG CHI TIẾT ---
        VBox infoBox = new VBox(6);
        infoBox.setPadding(new Insets(10, 0, 10, 0));

        // Tên khách hàng & SDT
        String tenKhach = "N/A", sdtKhach = "N/A";
        try {
            iuh.fit.core.dto.KhachHangDTO kh = khachHangService.getKhachHangById(phieu.getMaKhachHang());
            if (kh != null) { tenKhach = kh.getHoTen(); sdtKhach = kh.getSoDienThoai(); }
        } catch (Exception e) { /* Bỏ qua nếu lỗi */ }

        infoBox.getChildren().add(createIconLabel("👤", "Khách hàng:", tenKhach));
        infoBox.getChildren().add(createIconLabel("📞", "SĐT:", sdtKhach));

        String strNgayNhan = phieu.getNgayNhan() != null ? phieu.getNgayNhan().format(DATE_FORMATTER) : "N/A";
        String strNgayTra = phieu.getNgayTra() != null ? phieu.getNgayTra().format(DATE_FORMATTER) : "N/A";
        infoBox.getChildren().add(createIconLabel("📅", "Thời gian:", strNgayNhan + " ➡️ " + strNgayTra));

        // Danh sách phòng (Lấy từ Service)
        String dsPhongStr = "-";
        try {
            List<PhongDTO> phongList = phongService.getPhongByPhieuDat(phieu.getMaPhieu());
            if (phongList != null && !phongList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (PhongDTO p : phongList) sb.append(p.getMaPhong()).append(", ");
                dsPhongStr = sb.substring(0, sb.length() - 2);
            }
        } catch (Exception e) {}
        infoBox.getChildren().add(createIconLabel("🏨", "Phòng đặt:", dsPhongStr));

        // --- ĐƯỜNG KẺ & TỔNG TIỀN ---
        Separator sep = new Separator();
        HBox botBox = new HBox();
        botBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTienTxt = new Label("Tổng tiền:"); lblTienTxt.setFont(Font.font("Segoe UI", 13)); lblTienTxt.setTextFill(Color.web(COLOR_TEXT_MUTED));
        Label lblTienVal = new Label(String.format("%,.0f đ", phieu.getTongTien()));
        lblTienVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16)); lblTienVal.setTextFill(Color.web(COLOR_PRIMARY));
        Region botSpacer = new Region(); HBox.setHgrow(botSpacer, Priority.ALWAYS);
        botBox.getChildren().addAll(lblTienTxt, botSpacer, lblTienVal);

        // --- NÚT HÀNH ĐỘNG ---
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        // CHỈ HIỂN THỊ NÚT NẾU TRẠNG THÁI LÀ "CHỜ NHẬN"
        if (statusText.equals("Chờ Nhận Phòng") || statusText.equals("Đặt Phòng")) {
            Button btnNhan = new Button("🔑 Nhận Phòng");
            btnNhan.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnNhan, Priority.ALWAYS);
            btnNhan.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8;");
            btnNhan.setOnAction(e -> xuLyNhanPhong(phieu.getMaPhieu()));

            Button btnHuy = new Button("❌ Hủy Phiếu");
            btnHuy.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnHuy, Priority.ALWAYS);
            btnHuy.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_DANGER + "; -fx-border-color: " + COLOR_DANGER + "; -fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8;");
            btnHuy.setOnAction(e -> xuLyHuyPhieu(phieu.getMaPhieu()));

            actionBox.getChildren().addAll(btnHuy, btnNhan);
        } else {
            // Nếu đã Nhận hoặc đã Hủy -> Hiện một cái Badge Disable
            Label lblDone = new Label(statusText);
            lblDone.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(lblDone, Priority.ALWAYS);
            lblDone.setAlignment(Pos.CENTER);
            lblDone.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8;");
            actionBox.getChildren().add(lblDone);
        }

        card.getChildren().addAll(topBox, infoBox, sep, botBox, actionBox);
        return card;
    }

    // =========================================================================
    // HÀM HELPER & XỬ LÝ SỰ KIỆN NÚT
    // =========================================================================

    private HBox createIconLabel(String icon, String title, String value) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.TOP_LEFT);

        Label lblTitle = new Label(icon + " " + title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblTitle.setPrefWidth(110);
        lblTitle.setMinWidth(110);

        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lblValue.setTextFill(Color.web(COLOR_TEXT_MAIN));
        lblValue.setWrapText(true);
        HBox.setHgrow(lblValue, Priority.ALWAYS);

        box.getChildren().addAll(lblTitle, lblValue);
        return box;
    }

    private void xuLyNhanPhong(String maPhieu) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn xác nhận khách ĐÃ ĐẾN NHẬN PHÒNG cho mã phiếu: " + maPhieu + "?");
        confirm.setTitle("Xác nhận Nhận Phòng");
        confirm.setHeaderText(null);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                // Gọi tới hàm Controller chung để xử lý logic checkin
                QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(phieuDatPhongService, phongService, khachHangService, null, null, null, currentUser);
                if (controller.processCheckIn(maPhieu)) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật trạng thái: ĐÃ NHẬN PHÒNG!");
                    loadDataToView(); // Refresh lại danh sách
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", "Lỗi: " + e.getMessage());
            }
        }
    }

    private void xuLyHuyPhieu(String maPhieu) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "CẢNH BÁO: Bạn có chắc muốn HỦY phiếu " + maPhieu + "? Các phòng sẽ được trả về trạng thái TRỐNG.", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận Hủy");
        confirm.setHeaderText(null);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            try {
                QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(phieuDatPhongService, phongService, khachHangService, null, null, null, currentUser);
                if (controller.cancelBooking(maPhieu)) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã HỦY phiếu đặt phòng thành công!");
                    loadDataToView(); // Refresh lại danh sách
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", "Lỗi: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}