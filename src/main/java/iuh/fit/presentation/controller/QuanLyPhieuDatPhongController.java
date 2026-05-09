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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Consumer;
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
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Consumer<String> onNavigateToCheckout;
    public QuanLyPhieuDatPhongController(IPhieuDatPhongService phieuDatPhongService,
                                         IPhongService phongService,
                                         IKhachHangService khachHangService,
                                         INhanVienService nhanVienService,
                                         TaiKhoanDTO currentUser, // 👉 ĐÃ THÊM DẤU PHẨY
                                         Consumer<String> onNavigateToCheckout) {
        this.phieuDatPhongService = phieuDatPhongService;
        this.phongService = phongService;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.currentUser = currentUser;
        this.onNavigateToCheckout = onNavigateToCheckout;
    }

    public VBox createQuanLyPhieuView() {
        VBox rootPane = new VBox(25);
        rootPane.setStyle("-fx-background-color: " + COLOR_BG_MAIN + "; -fx-padding: 30;");

        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("QUẢN LÝ PHIẾU ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Quản lý phiếu đặt, nhận/trả phòng và trạng thái cọc.");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        headerBox.getChildren().addAll(lblTitle, lblSubTitle);

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
            if (p.getNgayNhan() != null && p.getNgayTra() != null) {
                LocalDate startPhieu = p.getNgayNhan().toLocalDate();
                LocalDate endPhieu = p.getNgayTra().toLocalDate();

                if (tu != null && den != null) {
                    matchNgay = !startPhieu.isAfter(den) && !endPhieu.isBefore(tu);
                } else if (tu != null) {
                    matchNgay = !endPhieu.isBefore(tu);
                } else if (den != null) {
                    matchNgay = !startPhieu.isAfter(den);
                }
            }

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

        // 👉 TÍCH HỢP NGHIỆP VỤ ĐẾN TRỄ & QUÁ HẠN
        if (status.contains("CHO_NHAN")) {
            double tienCoc = first.getTienCoc() != null ? first.getTienCoc() : 0;
            if (first.getNgayNhan() != null) {
                if (laPhieuQuaHan(first.getNgayNhan(), tienCoc)) {
                    colorTemp = "#7f1d1d"; // Đỏ thẫm
                    statusTextTemp = "QUÁ HẠN (NO-SHOW)";
                    isOverdueLocal = true;
                } else if (LocalDateTime.now().isAfter(first.getNgayNhan())) {
                    colorTemp = "#f59e0b"; // Cam
                    statusTextTemp = "Khách Đến Trễ";
                } else {
                    colorTemp = "#10b981"; // Xanh lá
                    statusTextTemp = "Chờ Nhận Phòng";
                }
            } else {
                colorTemp = "#10b981"; statusTextTemp = "Chờ Nhận Phòng";
            }
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

        if (status.contains("DA_TRA_PHONG") || status.contains("HUY") || canThu <= 0) {
            HBox rowDone = new HBox();
            String textHienThi = "Đã thanh toán đủ:";
            if (status.contains("HUY")) textHienThi = "Đã hủy:";
            else if (status.contains("DA_TRA_PHONG")) textHienThi = "Hoàn tất (0 nợ):";

            Label lblTextTien = new Label(textHienThi);
            lblTextTien.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981; -fx-font-size: 13px;");
            Label lblTien = new Label("0 đ");
            lblTien.setStyle("-fx-font-weight: 900; -fx-font-size: 16px; -fx-text-fill: #10b981;");

            Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
            rowDone.getChildren().addAll(lblTextTien, sp2, lblTien);
            bottomRow.getChildren().add(rowDone);
        } else {
            HBox rowTong = new HBox();
            rowTong.getChildren().addAll(new Label("Tổng tiền:"), new Region(), new Label(String.format("%,.0f đ", tongTien)));
            HBox.setHgrow(rowTong.getChildren().get(1), Priority.ALWAYS);

            HBox rowCoc = new HBox();
            Label vCoc = new Label(String.format("- %,.0f đ", tienCoc));
            vCoc.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
            rowCoc.getChildren().addAll(new Label("Đã đặt cọc:"), new Region(), vCoc);
            HBox.setHgrow(rowCoc.getChildren().get(1), Priority.ALWAYS);

            HBox rowConLai = new HBox();
            Label lConLai = new Label(isGhiNo ? "Được phép Ghi nợ:" : "Cần thu thêm:");
            lConLai.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (isGhiNo ? "#f59e0b" : "#ef4444") + ";");
            Label vConLai = new Label(String.format("%,.0f đ", canThu));
            vConLai.setStyle("-fx-font-weight: 900; -fx-font-size: 16px; -fx-text-fill: " + (isGhiNo ? "#f59e0b" : "#ef4444") + ";");

            rowConLai.getChildren().addAll(lConLai, new Region(), vConLai);
            HBox.setHgrow(rowConLai.getChildren().get(1), Priority.ALWAYS);

            bottomRow.getChildren().addAll(rowTong, rowCoc, new Separator(), rowConLai);
        }

        HBox actions = new HBox(10);
        actions.setPadding(new Insets(10, 0, 0, 0));
        actions.setAlignment(Pos.CENTER);

        // 👉 TÍCH HỢP NÚT BẤM CHO TỪNG NGHIỆP VỤ
        if (status.contains("CHO_NHAN")) {
            if (isOverdue) {
                Button btnHuyNoShow = new Button("Hủy & Thu phí Cọc");
                btnHuyNoShow.setStyle("-fx-background-color: #7f1d1d; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 6;");
                btnHuyNoShow.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnHuyNoShow, Priority.ALWAYS);
                btnHuyNoShow.setOnAction(e -> { e.consume(); xuLyHuyPhieuNhom(group); });
                actions.getChildren().add(btnHuyNoShow);
            } else {
                Button btnNhan = new Button("Nhận Phòng");
                btnNhan.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 6;");
                btnNhan.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnNhan, Priority.ALWAYS);
                btnNhan.setOnAction(e -> { e.consume(); xuLyNhanPhongNhom(group, canThu); });

                Button btnHuyPhieu = new Button("Hủy Phiếu");
                btnHuyPhieu.setStyle("-fx-background-color: white; -fx-border-color: #ef4444; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 7 15; -fx-background-radius: 6;");
                btnHuyPhieu.setOnAction(e -> { e.consume(); xuLyHuyPhieuNhom(group); });

                actions.getChildren().addAll(btnNhan, btnHuyPhieu);
            }
        }
        else if (status.contains("DA_NHAN") || status.contains("ĐANG Ở")) {
            Button btnThanhToan = new Button("Thanh Toán");
            btnThanhToan.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 6;");
            btnThanhToan.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnThanhToan, Priority.ALWAYS);
            btnThanhToan.setOnAction(e -> { e.consume(); xuLyTraPhongNhom(group, canThu); });

            Button btnHuyPhong = new Button("Hủy Phòng");
            btnHuyPhong.setStyle("-fx-background-color: white; -fx-border-color: #f59e0b; -fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 7 15; -fx-background-radius: 6;");
            btnHuyPhong.setOnAction(e -> { e.consume(); xuLyHuyPhongDangO(group); });

            actions.getChildren().addAll(btnThanhToan, btnHuyPhong);
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

        DatePicker dpIn = new DatePicker(first.getNgayNhan() != null ? first.getNgayNhan().toLocalDate() : LocalDate.now());
        dpIn.setDisable(true);

        Label lOut = new Label("Ngày Trả (Gia hạn):"); lOut.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b;");

        DatePicker dpOut = new DatePicker(first.getNgayTra() != null ? first.getNgayTra().toLocalDate() : LocalDate.now());
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
                LocalDate newTraDate = dpOut.getValue();
                LocalDateTime newTra = newTraDate.atTime(12, 0);

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

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ CHÍNH ---
    private void xuLyNhanPhongNhom(List<PhieuDatPhongDTO> group, double canThuHienTai) {
        double tongPhuThu = 0;
        for (PhieuDatPhongDTO p : group) {
            double giaPhongMotDem = p.getTongTien();
            tongPhuThu += tinhPhuThuNhanSom(p.getNgayNhan(), giaPhongMotDem);
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận Nhận Phòng");
        alert.setHeaderText("Xác nhận cho " + group.size() + " phòng.");

        String msg = "Khách đến nhận phòng.";
        if (tongPhuThu > 0) {
            msg += "\n⚠️ PHÁT SINH PHỤ THU NHẬN SỚM: " + String.format("%,.0f đ", tongPhuThu);
            msg += "\nTổng tiền khách cần đóng thêm: " + String.format("%,.0f đ", canThuHienTai + tongPhuThu);
        }
        alert.setContentText(msg);

        ButtonType btnNhan = new ButtonType("Đồng ý & Nhận phòng", ButtonBar.ButtonData.YES);
        ButtonType btnHuy = new ButtonType("Bỏ qua", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnNhan, btnHuy);

        if (alert.showAndWait().orElse(btnHuy) == btnNhan) {
            try {
                for (PhieuDatPhongDTO p : group) {
                    if (tongPhuThu > 0) {
                        double phuThuMoiPhong = tongPhuThu / group.size();
                        p.setTongTien(p.getTongTien() + phuThuMoiPhong);
                    }
                    p.setTrangThai(STATUS_STAYING);
                    phieuDatPhongService.updatePhieuDatPhong(p);
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Đang ở");
                }
                loadDataAsync();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã nhận phòng thành công!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage());
            }
        }
    }

    private void xuLyTraPhongNhom(List<PhieuDatPhongDTO> group, double canThu) {
        // 👉 BỎ QUA KIỂM TRA TIỀN NỢ. LÚC NÀO CŨNG PHẢI CHUYỂN SANG TAB THANH TOÁN
        // ĐỂ LỄ TÂN KIỂM TRA DỊCH VỤ VÀ IN HÓA ĐƠN!
        String maPhieu = group.get(0).getMaPhieu();

        if (onNavigateToCheckout != null) {
            onNavigateToCheckout.accept(maPhieu); // Bắn tín hiệu chuyển trang
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", "Chức năng chuyển trang chưa được cấu hình.");
        }
    }

    private void xuLyHuyPhieuNhom(List<PhieuDatPhongDTO> group) {
        PhieuDatPhongDTO first = group.get(0);
        double tongCoc = group.stream().mapToDouble(p -> p.getTienCoc() != null ? p.getTienCoc() : 0).sum();
        double phiHuy = tinhPhiHuyPhieu(first.getNgayNhan(), tongCoc);

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Xác nhận hủy phiếu");
        alert.setHeaderText("Hủy đợt đặt phòng này?");

        String noiDung = "Thời gian hủy: " + LocalDateTime.now().format(DATE_FORMATTER);
        if (phiHuy > 0) {
            noiDung += "\n⚠️ PHÍ HỦY (Do quá hạn hoặc hủy dưới 24h): " + String.format("%,.0f đ", phiHuy);
            noiDung += "\n(Hệ thống sẽ giữ lại tiền cọc của khách)";
        } else {
            noiDung += "\n✅ MIỄN PHÍ HỦY (Hủy trước 24h). Vui lòng hoàn cọc cho khách: " + String.format("%,.0f đ", tongCoc);
        }
        alert.setContentText(noiDung);

        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.OK) {
            try {
                for (PhieuDatPhongDTO p : group) {
                    p.setTrangThai(STATUS_CANCELLED);
                    phieuDatPhongService.updatePhieuDatPhong(p);
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Trống");
                }
                loadDataAsync();
                showAlert(Alert.AlertType.INFORMATION, "Đã hủy", "Đã cập nhật trạng thái phiếu.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage());
            }
        }
    }

    private void xuLyHuyPhongDangO(List<PhieuDatPhongDTO> group) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Xác nhận hủy phòng đang ở");
        alert.setHeaderText("CẢNH BÁO: Khách đang ở trong " + group.size() + " phòng này!");
        alert.setContentText("Việc hủy phòng sẽ trả trạng thái phòng về 'Trống'. Bạn có chắc chắn muốn tiếp tục không?");

        ButtonType btnDongY = new ButtonType("Đồng ý hủy", ButtonBar.ButtonData.YES);
        ButtonType btnThoat = new ButtonType("Quay lại", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnDongY, btnThoat);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnDongY) {
            try {
                for (PhieuDatPhongDTO p : group) {
                    p.setTrangThai(STATUS_CANCELLED);
                    phieuDatPhongService.updatePhieuDatPhong(p);
                    phongService.updatePhongTrangThai(p.getMaPhong(), "Trống");
                }
                loadDataAsync();
                showAlert(Alert.AlertType.INFORMATION, "Hoàn tất", "Đã hủy các phòng đang ở thành công.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    // --- HÀM TÍNH PHỤ THU NHẬN PHÒNG SỚM ---
    private double tinhPhuThuNhanSom(LocalDateTime ngayNhanDuKien, double giaPhongMotDem) {
        LocalDateTime bayGio = LocalDateTime.now();
        if (ngayNhanDuKien != null && bayGio.toLocalDate().isEqual(ngayNhanDuKien.toLocalDate()) && bayGio.getHour() < 14) {
            int gio = bayGio.getHour();
            if (gio < 6) return giaPhongMotDem;        // Trước 6h: 100%
            if (gio < 9) return giaPhongMotDem * 0.5;  // 6h - 9h: 50%
            if (gio < 12) return giaPhongMotDem * 0.3; // 9h - 12h: 30%
        }
        return 0; // Sau 12h: Miễn phí
    }

    // --- HÀM TÍNH PHÍ HỦY PHÒNG ---
    private double tinhPhiHuyPhieu(LocalDateTime ngayNhanDuKien, double tienCoc) {
        if (ngayNhanDuKien == null) return 0;
        long soGioConLai = java.time.Duration.between(LocalDateTime.now(), ngayNhanDuKien).toHours();
        if (soGioConLai < 24) {
            return tienCoc; // Hủy trong vòng 24h hoặc trễ hơn: Mất 100% cọc
        }
        return 0; // Hủy trước 24h: Miễn phí (Hoàn cọc)
    }

    // --- KIỂM TRA PHIẾU CÓ BỊ QUÁ HẠN (NO-SHOW) KHÔNG ---
    private boolean laPhieuQuaHan(LocalDateTime ngayNhanDuKien, double tienCoc) {
        if (ngayNhanDuKien == null) return false;
        LocalDateTime bayGio = LocalDateTime.now();

        // Nếu chưa đóng tiền cọc: Quá hạn sau 18:00 cùng ngày
        if (tienCoc <= 0) {
            return bayGio.isAfter(ngayNhanDuKien.withHour(18).withMinute(0));
        }

        // Nếu đã đóng tiền cọc: Giữ phòng đến 12:00 trưa ngày hôm sau
        return bayGio.isAfter(ngayNhanDuKien.plusDays(1).withHour(12).withMinute(0));
    }
}