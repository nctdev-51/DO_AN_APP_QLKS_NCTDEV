package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class QuanLyPhieuDatPhongController {

    private final IPhieuDatPhongService phieuDatPhongService;
    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    private final INhanVienService nhanVienService;
    private final TaiKhoanDTO currentUser;

    private ObservableList<PhieuDatPhongDTO> phieuList = FXCollections.observableArrayList();
    private FilteredList<PhieuDatPhongDTO> filteredList;

    private final Map<String, KhachHangDTO> customerCache = new HashMap<>();
    private final Map<String, PhongDTO> roomCache = new HashMap<>();

    // UI Components
    private TextField txtTimKiemSDT;
    private DatePicker dpTuNgay, dpDenNgay;
    private ComboBox<String> cbLocTrangThai;
    private FlowPane cardContainer;
    private final ProgressIndicator loadingOverlay = new ProgressIndicator();

    private final String COLOR_BG_MAIN = "#f8fafc";
    private final String STATUS_WAITING = "CHO_NHAN_PHONG";
    private final String STATUS_STAYING = "DA_NHAN_PHONG";
    private final String STATUS_CANCELLED = "DA_HUY";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QuanLyPhieuDatPhongController(IPhieuDatPhongService phieuDatPhongService,
                                         IPhongService phongService,
                                         IKhachHangService khachHangService,
                                         INhanVienService nhanVienService,
                                         TaiKhoanDTO currentUser) {
        this.phieuDatPhongService = phieuDatPhongService;
        this.phongService = phongService;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.currentUser = currentUser;
    }

    public VBox createQuanLyPhieuView() {
        VBox rootPane = new VBox(25);
        rootPane.setStyle("-fx-background-color: " + COLOR_BG_MAIN + "; -fx-padding: 30;");

        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("QUẢN LÝ PHIẾU ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        lblTitle.setTextFill(Color.web("#0f172a"));
        headerBox.getChildren().addAll(lblTitle, new Label("Quản lý phiếu đặt, nhận/trả phòng và trạng thái cọc"));

        HBox filterBox = createFilterBar();

        cardContainer = new FlowPane(25, 25);
        cardContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        rootPane.getChildren().addAll(headerBox, filterBox, scrollPane);

        loadDataAsync();
        return rootPane;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 12;");

        txtTimKiemSDT = new TextField();
        txtTimKiemSDT.setPromptText("🔍 Tìm theo SĐT khách...");
        txtTimKiemSDT.setPrefWidth(220);
        txtTimKiemSDT.textProperty().addListener((obs, old, nw) -> filterData());

        cbLocTrangThai = new ComboBox<>(FXCollections.observableArrayList(
                "Tất cả",
                "Chờ nhận phòng",
                "Đang ở",
                "Đã trả phòng",
                "Đã hủy",
                "Đã cọc / Đã thanh toán",
                "Chưa cọc (Nợ)"
        ));
        cbLocTrangThai.setValue("Tất cả");
        cbLocTrangThai.setPrefWidth(180);
        cbLocTrangThai.setOnAction(e -> filterData());

        dpTuNgay = new DatePicker(); dpTuNgay.setPromptText("Từ ngày");
        dpTuNgay.setPrefWidth(130);
        dpTuNgay.setOnAction(e -> filterData());

        dpDenNgay = new DatePicker(); dpDenNgay.setPromptText("Đến ngày");
        dpDenNgay.setPrefWidth(130);
        dpDenNgay.setOnAction(e -> filterData());

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnLamMoi = new Button("Làm mới");
        btnLamMoi.setStyle("-fx-cursor: hand; -fx-background-color: #e2e8f0; -fx-font-weight: bold;");
        btnLamMoi.setOnAction(e -> {
            txtTimKiemSDT.clear();
            cbLocTrangThai.setValue("Tất cả");
            dpTuNgay.setValue(null);
            dpDenNgay.setValue(null);
            loadDataAsync();
        });

        bar.getChildren().addAll(new Label("Lọc:"), txtTimKiemSDT, cbLocTrangThai, dpTuNgay, dpDenNgay, sp, btnLamMoi);
        return bar;
    }

    private void loadDataAsync() {
        loadingOverlay.setVisible(true);
        cardContainer.setOpacity(0.5);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<KhachHangDTO> allKH = khachHangService.getAllKhachHang();
                List<PhongDTO> allPhong = phongService.getAllPhong();
                List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();

                customerCache.clear();
                for (KhachHangDTO kh : allKH) {
                    if (kh.getMaKhachHang() != null)
                        customerCache.put(kh.getMaKhachHang().trim().toUpperCase(), kh);
                }

                roomCache.clear();
                for (PhongDTO p : allPhong) {
                    if (p.getMaPhong() != null)
                        roomCache.put(p.getMaPhong().trim().toUpperCase(), p);
                }

                Platform.runLater(() -> {
                    phieuList.setAll(allPhieu);
                    filteredList = new FilteredList<>(phieuList, p -> true);
                    renderCards();
                });
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            loadingOverlay.setVisible(false);
            cardContainer.setOpacity(1.0);
        });
        new Thread(task).start();
    }

    private void filterData() {
        if (filteredList == null) return;
        String sdt = txtTimKiemSDT.getText().trim().toLowerCase();
        String locTrangThai = cbLocTrangThai.getValue();
        LocalDate tu = dpTuNgay.getValue();
        LocalDate den = dpDenNgay.getValue();

        filteredList.setPredicate(p -> {
            boolean matchNgay = true;
            if (tu != null) matchNgay = (p.getNgayNhan() != null && !p.getNgayNhan().isBefore(tu));
            if (matchNgay && den != null) matchNgay = (p.getNgayNhan() != null && !p.getNgayNhan().isAfter(den));

            boolean matchSDT = true;
            if (!sdt.isEmpty()) {
                String maKH = p.getMaKhachHang() != null ? p.getMaKhachHang().trim().toUpperCase() : "";
                KhachHangDTO kh = customerCache.get(maKH);
                matchSDT = (kh != null && kh.getSoDienThoai() != null && kh.getSoDienThoai().contains(sdt));
            }

            boolean matchTT = true;
            if (!locTrangThai.equals("Tất cả")) {
                String dbStatus = p.getTrangThai() != null ? p.getTrangThai().toUpperCase() : "";
                double coc = p.getTienCoc() != null ? p.getTienCoc() : 0;

                if (locTrangThai.equals("Chờ nhận phòng")) matchTT = dbStatus.contains("CHO_NHAN");
                else if (locTrangThai.equals("Đang ở")) matchTT = dbStatus.contains("DA_NHAN") || dbStatus.contains("ĐANG Ở");
                else if (locTrangThai.equals("Đã trả phòng")) matchTT = dbStatus.contains("TRA_PHONG");
                else if (locTrangThai.equals("Đã hủy")) matchTT = dbStatus.contains("HUY");
                else if (locTrangThai.equals("Đã cọc / Đã thanh toán")) matchTT = coc > 0 || dbStatus.contains("TRA_PHONG");
                else if (locTrangThai.equals("Chưa cọc (Nợ)")) matchTT = coc <= 0 && !dbStatus.contains("TRA_PHONG") && !dbStatus.contains("HUY");
            }

            return matchNgay && matchSDT && matchTT;
        });
        renderCards();
    }

    private void renderCards() {
        Platform.runLater(() -> {
            cardContainer.getChildren().clear();

            if (filteredList == null || filteredList.isEmpty()) {
                cardContainer.getChildren().add(new Label("📭 Không tìm thấy phiếu nào phù hợp."));
                return;
            }

            Map<String, List<PhieuDatPhongDTO>> groupedMap = filteredList.stream()
                    .collect(Collectors.groupingBy(p -> {
                        String ma = (p.getMaPhieu() != null) ? p.getMaPhieu().trim() : "";
                        return ma.contains("-") ? ma.split("-")[0] : ma;
                    }));

            List<List<PhieuDatPhongDTO>> sortedGroups = new ArrayList<>(groupedMap.values());
            sortedGroups.sort((a, b) -> b.get(0).getMaPhieu().compareTo(a.get(0).getMaPhieu()));

            for (List<PhieuDatPhongDTO> group : sortedGroups) {
                try {
                    cardContainer.getChildren().add(createPhieuCard(group));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private VBox createPhieuCard(List<PhieuDatPhongDTO> group) {
        PhieuDatPhongDTO first = group.get(0);
        VBox card = new VBox(10);
        card.setPrefWidth(350);
        card.setPadding(new Insets(20));
        card.setCursor(Cursor.HAND);

        String status = first.getTrangThai() != null ? first.getTrangThai().toUpperCase() : "";
        boolean isOverdueLocal = false;
        String colorTemp = "#64748b";
        String statusTextTemp = "Chờ Xác Nhận";

        if (status.contains("CHO_NHAN")) {
            if (first.getNgayNhan() != null && LocalDate.now().isAfter(first.getNgayNhan())) {
                colorTemp = "#dc2626"; statusTextTemp = "Quá Hạn Nhận Phòng"; isOverdueLocal = true;
            } else { colorTemp = "#f59e0b"; statusTextTemp = "Chờ Nhận Phòng"; }
        }
        else if (status.contains("DA_NHAN") || status.contains("ĐANG Ở")) { colorTemp = "#10b981"; statusTextTemp = "Đang Ở"; }
        else if (status.contains("HUY")) { colorTemp = "#ef4444"; statusTextTemp = "Đã Hủy"; }
        else if (status.contains("TRA_PHONG")) { colorTemp = "#2563eb"; statusTextTemp = "Đã Trả Phòng"; }

        final String finalColor = colorTemp;
        final String finalStatusText = statusTextTemp;
        final boolean isOverdue = isOverdueLocal;

        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + finalColor + "; -fx-border-width: 1 1 1 6;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.1)));

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-color: #cbd5e1 #cbd5e1 #cbd5e1 " + finalColor + "; -fx-border-width: 1 1 1 6;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + finalColor + "; -fx-border-width: 1 1 1 6;"));

        card.setOnMouseClicked(e -> showChiTietPhieuDialog(group, finalStatusText, finalColor));

        HBox head = new HBox();
        head.setAlignment(Pos.CENTER_LEFT);
        String maHienThi = first.getMaPhieu().trim().toUpperCase();
        if (maHienThi.contains("-")) maHienThi = maHienThi.split("-")[0];

        String suffix = group.size() > 1 ? " (+" + (group.size() - 1) + " phòng)" : "";
        Label lblMa = new Label(maHienThi + suffix);
        lblMa.setStyle("-fx-font-weight: 900; -fx-font-size: 18px; -fx-text-fill: #0f172a;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblSt = new Label(finalStatusText);

        if (isOverdue) lblSt.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-size: 11px; -fx-border-color: #dc2626; -fx-border-radius: 20;");
        else lblSt.setStyle("-fx-background-color: " + finalColor + "20; -fx-text-fill: " + finalColor + "; -fx-font-weight: bold; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-size: 11px;");
        head.getChildren().addAll(lblMa, sp, lblSt);

        String maKH = first.getMaKhachHang() != null ? first.getMaKhachHang().trim().toUpperCase() : "";
        KhachHangDTO kh = customerCache.get(maKH);
        String ten = (kh != null && kh.getHoTen() != null) ? kh.getHoTen() : (first.getTenKhachHang() != null ? first.getTenKhachHang() : "Khách vãng lai");
        String sdt = (kh != null && kh.getSoDienThoai() != null) ? kh.getSoDienThoai() : "N/A";

        String dsPhong = group.stream()
                .map(p -> {
                    String mP = p.getMaPhong() != null ? p.getMaPhong().trim().toUpperCase() : "";
                    return roomCache.containsKey(mP) ? roomCache.get(mP).getTenPhong() : mP;
                })
                .collect(Collectors.joining(", "));

        VBox body = new VBox(8);
        Label lblTen = new Label("👤 Khách: " + ten); lblTen.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");
        Label lblSdt = new Label("📞 SĐT: " + sdt); lblSdt.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        Label lblRoom = new Label("🏨 Phòng: " + dsPhong); lblRoom.setStyle("-fx-font-weight: 900; -fx-font-size: 14px; -fx-text-fill: " + finalColor + ";");
        Label lblTime = new Label("📅 " + (first.getNgayNhan() != null ? first.getNgayNhan().format(DATE_FORMATTER) : "N/A") + " ➜ " + (first.getNgayTra() != null ? first.getNgayTra().format(DATE_FORMATTER) : "N/A"));
        lblTime.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        body.getChildren().addAll(lblTen, lblSdt, lblRoom, lblTime);

        VBox bottomRow = new VBox(5);
        bottomRow.setPadding(new Insets(10, 0, 0, 0));

        double tongTien = group.stream().mapToDouble(p -> p.getTongTien() != null ? p.getTongTien() : 0).sum();
        double tienCoc = group.stream().mapToDouble(p -> p.getTienCoc() != null ? p.getTienCoc() : 0).sum();
        double canThu = tongTien - tienCoc;
        boolean isGhiNo = first.getLoaiThanhToan() != null && first.getLoaiThanhToan().equals("GHI_NO");

        if (status.contains("DA_TRA_PHONG") || status.contains("HUY")) {
            HBox rowDone = new HBox();
            Label lblTextTien = new Label(status.contains("HUY") ? "Đã hủy:" : "Khách nợ (Ghi nợ):");
            lblTextTien.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b; -fx-font-size: 13px;");
            Label lblTien = new Label("0 đ");
            lblTien.setStyle("-fx-font-weight: 900; -fx-font-size: 16px; -fx-text-fill: #10b981;");
            Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
            rowDone.getChildren().addAll(lblTextTien, sp2, lblTien);
            bottomRow.getChildren().add(rowDone);
        } else {
            HBox rowTong = new HBox();
            Label lTong = new Label("Tổng tiền:"); lTong.setStyle("-fx-text-fill: #1e293b;");
            Label vTong = new Label(String.format("%,.0f đ", tongTien)); vTong.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            Region s1 = new Region(); HBox.setHgrow(s1, Priority.ALWAYS);
            rowTong.getChildren().addAll(lTong, s1, vTong);

            HBox rowCoc = new HBox();
            Label lCoc = new Label("Đã đặt cọc:"); lCoc.setStyle("-fx-text-fill: #1e293b;");
            Label vCoc = new Label(String.format("- %,.0f đ", tienCoc));
            if (tienCoc <= 0) vCoc.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 12px;");
            else vCoc.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 12px;");
            Region s2 = new Region(); HBox.setHgrow(s2, Priority.ALWAYS);
            rowCoc.getChildren().addAll(lCoc, s2, vCoc);

            HBox rowConLai = new HBox();
            Label lConLai = new Label(isGhiNo ? "Được phép Ghi nợ:" : "Cần thu thêm:");
            lConLai.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (isGhiNo ? "#f59e0b" : "#ef4444") + "; -fx-font-size: 13px;");
            Label vConLai = new Label(String.format("%,.0f đ", canThu > 0 ? canThu : 0));
            vConLai.setStyle("-fx-font-weight: 900; -fx-text-fill: " + (isGhiNo ? "#f59e0b" : "#ef4444") + "; -fx-font-size: 16px;");
            Region s3 = new Region(); HBox.setHgrow(s3, Priority.ALWAYS);
            rowConLai.getChildren().addAll(lConLai, s3, vConLai);

            bottomRow.getChildren().addAll(rowTong, rowCoc, new Separator(), rowConLai);
        }

        HBox actions = new HBox(10);
        actions.setPadding(new Insets(10, 0, 0, 0));
        actions.setAlignment(Pos.CENTER);

        if (status.contains("CHO_NHAN")) {
            Button btnNhan = new Button("Nhận Phòng");
            btnNhan.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 6;");
            btnNhan.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnNhan, Priority.ALWAYS);
            btnNhan.setOnAction(e -> { e.consume(); xuLyNhanPhongNhom(group, canThu); });

            Button btnHuy = new Button("Hủy Phiếu");
            btnHuy.setStyle("-fx-background-color: white; -fx-border-color: #ef4444; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 7 15; -fx-background-radius: 6; -fx-border-radius: 6;");
            btnHuy.setOnAction(e -> { e.consume(); xuLyHuyPhieuNhom(group); });
            actions.getChildren().addAll(btnNhan, btnHuy);
        } else if (status.contains("DA_NHAN") || status.contains("ĐANG Ở")) {
            Button btnTra = new Button("Trả Phòng & TT");
            btnTra.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 6;");
            btnTra.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnTra, Priority.ALWAYS);
            btnTra.setOnAction(e -> { e.consume(); xuLyTraPhongNhom(group, canThu); });
            actions.getChildren().add(btnTra);
        }

        card.getChildren().addAll(head, new Separator(), body, new Separator(), bottomRow);
        if (!actions.getChildren().isEmpty()) card.getChildren().add(actions);
        return card;
    }

    // =========================================================================
    // 🛠️ DIALOG CHI TIẾT VÀ CẬP NHẬT (QUICK EDIT)
    // =========================================================================
    private void showChiTietPhieuDialog(List<PhieuDatPhongDTO> group, String statusText, String colorCode) {
        PhieuDatPhongDTO first = group.get(0);
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Chi Tiết Giao Dịch Đặt Phòng");

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");

        HBox head = new HBox(15);
        head.setAlignment(Pos.CENTER_LEFT);
        String maGiaoDich = first.getMaPhieu().contains("-") ? first.getMaPhieu().split("-")[0] : first.getMaPhieu();
        Label lblMa = new Label("Giao dịch: " + maGiaoDich);
        lblMa.setFont(Font.font("Segoe UI", FontWeight.BLACK, 20));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblSt = new Label(statusText);
        lblSt.setStyle("-fx-background-color: " + colorCode + "20; -fx-text-fill: " + colorCode + "; -fx-font-weight: bold; -fx-padding: 6 15; -fx-background-radius: 20;");
        head.getChildren().addAll(lblMa, sp, lblSt);

        String maKH = first.getMaKhachHang() != null ? first.getMaKhachHang().trim().toUpperCase() : "";
        KhachHangDTO kh = customerCache.get(maKH);
        String ten = (kh != null) ? kh.getHoTen() : (first.getTenKhachHang() != null ? first.getTenKhachHang() : "Khách vãng lai");
        String sdt = (kh != null) ? kh.getSoDienThoai() : "N/A";

        VBox infoBox = new VBox(8);
        infoBox.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

        Label lblKhachInfo = new Label("👤 Người đặt: " + ten + " - 📞 " + sdt);
        lblKhachInfo.setFont(Font.font(null, FontWeight.BOLD, 14));

        String dsPhongDialog = group.stream().map(PhieuDatPhongDTO::getMaPhong).collect(Collectors.joining(", "));
        Label lblPhongInfo = new Label("🚪 Danh sách mã phòng: " + dsPhongDialog);
        lblPhongInfo.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        infoBox.getChildren().addAll(lblKhachInfo, lblPhongInfo);

        GridPane grid = new GridPane();
        grid.setVgap(15); grid.setHgap(15);

        Label lIn = new Label("Ngày Nhận:"); lIn.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b;");
        DatePicker dpIn = new DatePicker(first.getNgayNhan()); dpIn.setDisable(true);

        Label lOut = new Label("Ngày Trả (Gia hạn):"); lOut.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b;");
        DatePicker dpOut = new DatePicker(first.getNgayTra());
        if (first.getTrangThai().contains("TRA_PHONG") || first.getTrangThai().contains("HUY")) dpOut.setDisable(true);

        double totalCoc = group.stream().mapToDouble(p -> p.getTienCoc() != null ? p.getTienCoc() : 0).sum();
        Label lCoc = new Label("Tổng cọc cả đoàn:"); lCoc.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b;");
        TextField txtCoc = new TextField(String.format("%.0f", totalCoc));
        txtCoc.setPromptText("Nhập số tiền...");
        if (first.getTrangThai().contains("TRA_PHONG") || first.getTrangThai().contains("HUY")) txtCoc.setDisable(true);

        Label lPhuongThuc = new Label("Phương thức:"); lPhuongThuc.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b;");
        ComboBox<String> cbPhuongThuc = new ComboBox<>(FXCollections.observableArrayList("Tiền mặt", "Chuyển khoản", "Ghi Nợ (Trả sau)"));
        String currentMethod = first.getLoaiThanhToan() != null ? first.getLoaiThanhToan() : "TIEN_MAT";
        if (currentMethod.equals("GHI_NO")) cbPhuongThuc.setValue("Ghi Nợ (Trả sau)");
        else if (currentMethod.equals("CHUYEN_KHOAN")) cbPhuongThuc.setValue("Chuyển khoản");
        else cbPhuongThuc.setValue("Tiền mặt");
        if (first.getTrangThai().contains("TRA_PHONG") || first.getTrangThai().contains("HUY")) cbPhuongThuc.setDisable(true);

        grid.addRow(0, lIn, dpIn, lOut, dpOut);
        grid.addRow(1, lCoc, txtCoc, lPhuongThuc, cbPhuongThuc);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button btnClose = new Button("Đóng");
        btnClose.setStyle("-fx-background-color: #e2e8f0; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnClose.setOnAction(e -> dialog.close());

        Button btnSave = new Button("Lưu Cập Nhật");
        btnSave.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8 20;");
        if (first.getTrangThai().contains("TRA_PHONG") || first.getTrangThai().contains("HUY")) btnSave.setDisable(true);

        btnSave.setOnAction(e -> {
            try {
                LocalDate newTra = dpOut.getValue();
                double newCocTotal = Double.parseDouble(txtCoc.getText().replaceAll("[^\\d]", ""));
                double cocPerRoom = newCocTotal / group.size();

                String valPT = cbPhuongThuc.getValue();
                String savePT = "TIEN_MAT";
                if (valPT.equals("Ghi Nợ (Trả sau)")) savePT = "GHI_NO";
                else if (valPT.equals("Chuyển khoản")) savePT = "CHUYEN_KHOAN";

                for (PhieuDatPhongDTO p : group) {
                    p.setNgayTra(newTra);
                    p.setTienCoc(cocPerRoom);
                    p.setLoaiThanhToan(savePT);
                    phieuDatPhongService.updatePhieuDatPhong(p);
                }
                loadDataAsync();
                dialog.close();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin phiếu!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng kiểm tra lại tiền cọc và ngày.");
            }
        });

        actions.getChildren().addAll(btnClose, btnSave);
        root.getChildren().addAll(head, infoBox, new Separator(), grid, new Separator(), actions);

        Scene scene = new Scene(root, 600, -1);
        dialog.setScene(scene);
        dialog.show();
    }

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ ---
    private void xuLyNhanPhongNhom(List<PhieuDatPhongDTO> group, double tongTien) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Tùy chọn nhận phòng");
        alert.setHeaderText("Xác nhận cho " + group.size() + " phòng.");
        ButtonType btnNgay = new ButtonType("Xác nhận Nhận Phòng", ButtonBar.ButtonData.YES);
        ButtonType btnHuy = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnNgay, btnHuy);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnNgay) {
            try {
                for (PhieuDatPhongDTO p : group) {
                    p.setTrangThai(STATUS_STAYING);
                    phieuDatPhongService.updatePhieuDatPhong(p);
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Đang ở");
                }
                loadDataAsync();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật trạng thái Nhận phòng.");
            } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage()); }
        }
    }

    private void xuLyTraPhongNhom(List<PhieuDatPhongDTO> group, double canThu) {
        boolean isGhiNo = group.get(0).getLoaiThanhToan() != null && group.get(0).getLoaiThanhToan().equals("GHI_NO");

        // 🚨 CHẶN ĐỨNG THẤT THOÁT DOANH THU 🚨
        if (canThu > 0 && !isGhiNo) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("BẢO VỆ DOANH THU KHÁCH SẠN");
            alert.setHeaderText("⛔ Khách chưa thanh toán đủ tiền!");
            alert.setContentText("Hệ thống khóa chức năng trả phòng nhanh vì khách vẫn còn nợ " + String.format("%,.0f đ", canThu) + ".\n\n"
                    + "👉 CÁCH XỬ LÝ:\n"
                    + "1. Mở menu [Thanh Toán - Trả Phòng] bên trái để lập hóa đơn chốt doanh thu.\n"
                    + "2. Nếu đây là khách đoàn/công ty được phép nợ, hãy click vào thẻ phiếu này -> Cập nhật Phương thức thành 'Ghi Nợ (Trả sau)'.");
            alert.showAndWait();
            return;
        }

        String msg = isGhiNo ? "Phòng này đang được GHI NỢ. Xác nhận cho khách đi và thu hồi " + group.size() + " phòng?"
                : "Khách đã thanh toán đủ. Xác nhận thu hồi " + group.size() + " phòng?";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                for (PhieuDatPhongDTO p : group) {
                    p.setTrangThai("DA_TRA_PHONG");
                    phieuDatPhongService.updatePhieuDatPhong(p);
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Trống");
                }
                loadDataAsync();
                showAlert(Alert.AlertType.INFORMATION, "Hoàn tất", "Đã trả phòng thành công.");
            } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage()); }
        }
    }

    private void xuLyHuyPhieuNhom(List<PhieuDatPhongDTO> group) {
        List<String> reasons = Arrays.asList("Khách gọi điện yêu cầu hủy", "Quá hạn nhận phòng (No-show)", "Lỗi đặt trùng phòng", "Lý do khác");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Khách gọi điện yêu cầu hủy", reasons);
        dialog.setTitle("Xác nhận hủy phiếu");
        dialog.setHeaderText("Hủy nhóm " + group.size() + " phiếu đặt phòng này?");
        dialog.setContentText("Chọn lý do hủy:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                for (PhieuDatPhongDTO p : group) {
                    p.setTrangThai(STATUS_CANCELLED);
                    phieuDatPhongService.updatePhieuDatPhong(p);
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Trống");
                }
                loadDataAsync();
                showAlert(Alert.AlertType.INFORMATION, "Đã Hủy", "Đã hủy phiếu đặt phòng thành công.\nLý do: " + result.get());
            } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage()); }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}