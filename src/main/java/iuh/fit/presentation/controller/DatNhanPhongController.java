package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DatNhanPhongController {

    private final IPhongService phongService;
    private final IPhieuDatPhongService phieuDatPhongService;
    private final IKhachHangService khachHangService;
    private final IHoaDonService hoaDonService;
    private final IChiTietHoaDonService chiTietHoaDonService;
    private final IDichVuService dichVuService;
    private final TaiKhoanDTO currentUser;
    private final Stage primaryStage;

    private VBox roomContainer;
    private List<PhongDTO> allRoomsCache = new ArrayList<>();

    // Các Component của Bộ lọc
    private TextField txtSearch;
    private ComboBox<String> cbLoai;
    private ComboBox<String> cbTrangThai;
    private DatePicker dpIn;
    private DatePicker dpOut;

    // Bảng màu chuẩn hiện đại
    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_PRIMARY_HOVER = "#004999";
    private final String COLOR_BG_LIGHT = "#f8fafc";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";

    // Màu trạng thái phòng
    private final String COLOR_AVAILABLE = "#10b981";
    private final String COLOR_BOOKED = "#ef4444";
    private final String COLOR_OCCUPIED = "#f59e0b";
    private final String COLOR_MAINTENANCE = "#64748b";

    public DatNhanPhongController(IPhongService phongService, IPhieuDatPhongService phieuDatPhongService,
                                  IKhachHangService khachHangService, IHoaDonService hoaDonService,
                                  IChiTietHoaDonService chiTietHoaDonService, IDichVuService dichVuService,
                                  TaiKhoanDTO currentUser, Stage primaryStage) {
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.khachHangService = khachHangService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.dichVuService = dichVuService;
        this.currentUser = currentUser;
        this.primaryStage = primaryStage;
    }

    public VBox createView() {
        VBox rootPane = new VBox(20);
        rootPane.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + "; -fx-padding: 25 35;");

        // --- 1. HEADER ---
        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("ĐẶT & NHẬN PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Tìm kiếm phòng trống, lập phiếu đặt phòng mới hoặc xử lý nhận phòng cho khách.");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        headerBox.getChildren().addAll(lblTitle, lblSubTitle);

        // --- 2. BỘ LỌC TÌM KIẾM ---
        HBox filterBar = createFilterBar();

        // --- 3. LƯỚI DANH SÁCH PHÒNG ---
        roomContainer = new VBox(20);
        roomContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(roomContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS); // Ép scroll pane chiếm hết khoảng trống

        rootPane.getChildren().addAll(headerBox, filterBar, scrollPane);

        // Load toàn bộ phòng lần đầu tiên
        loadDataAsync();

        return rootPane;
    }

    private HBox createFilterBar() {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1;");

        DropShadow shadow = new DropShadow(); shadow.setColor(Color.web("#000000", 0.03)); shadow.setRadius(10); shadow.setOffsetY(4);
        card.setEffect(shadow);

        Label icon = new Label("🔍");
        icon.setFont(Font.font(14));

        txtSearch = new TextField();
        txtSearch.setPromptText("Nhập mã hoặc tên phòng...");
        txtSearch.setPrefWidth(200);
        txtSearch.setStyle("-fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc; -fx-padding: 8 12;");

        cbLoai = new ComboBox<>();
        cbLoai.getItems().addAll("Tất cả loại", "DON", "DOI", "GIADINH", "VIP");
        cbLoai.setValue("Tất cả loại");
        cbLoai.setStyle("-fx-font-size: 13px; -fx-padding: 3 5;");

        cbTrangThai = new ComboBox<>();
        cbTrangThai.getItems().addAll("Tất cả tình trạng", "Trống", "Đã Đặt", "Đang ở", "Bảo Trì");
        cbTrangThai.setValue("Tất cả tình trạng");
        cbTrangThai.setStyle("-fx-font-size: 13px; -fx-padding: 3 5;");

        Label lblIn = new Label("Từ ngày:");
        lblIn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lblIn.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpIn = new DatePicker();
        dpIn.setPromptText("Check-in");
        dpIn.setEditable(false);
        dpIn.setStyle("-fx-font-size: 13px;");
        dpIn.setPrefWidth(130);

        Label lblOut = new Label("Đến:");
        lblOut.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lblOut.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpOut = new DatePicker();
        dpOut.setPromptText("Check-out");
        dpOut.setEditable(false);
        dpOut.setStyle("-fx-font-size: 13px;");
        dpOut.setPrefWidth(130);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSearch = new Button("Tìm kiếm phòng");
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnSearch.setOnMouseEntered(e -> btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;"));
        btnSearch.setOnMouseExited(e -> btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;"));
        btnSearch.setOnAction(e -> filterRooms());

        card.getChildren().addAll(icon, txtSearch, cbLoai, cbTrangThai, lblIn, dpIn, lblOut, dpOut, spacer, btnSearch);
        return card;
    }

    private void loadDataAsync() {
        Task<List<PhongDTO>> task = new Task<>() {
            @Override
            protected List<PhongDTO> call() {
                return phongService.getAllPhong();
            }
        };
        task.setOnSucceeded(evt -> {
            allRoomsCache = task.getValue();
            renderRoomsByFloor(allRoomsCache);
        });
        task.setOnFailed(evt -> {
            // Im lặng xử lý hoặc in ra console
            System.err.println("Lỗi tải danh sách phòng: " + task.getException().getMessage());
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void filterRooms() {
        LocalDate in = dpIn.getValue();
        LocalDate out = dpOut.getValue();
        List<PhongDTO> sourceData;

        // Nếu có chọn ngày, yêu cầu backend quét phòng trống
        if (in != null && out != null && out.isAfter(in)) {
            sourceData = phongService.findAvailableRooms(in, out, 0, Double.MAX_VALUE);
        } else {
            sourceData = new ArrayList<>(allRoomsCache);
        }

        String kw = txtSearch.getText() != null ? txtSearch.getText().trim() : "";
        String loai = cbLoai.getValue();
        String tt = cbTrangThai.getValue();

        List<PhongDTO> filtered = sourceData.stream()
                .filter(p -> kw.isEmpty()
                        || removeAccents((p.getMaPhong() != null ? p.getMaPhong() : "").toLowerCase()).contains(removeAccents(kw.toLowerCase()))
                        || removeAccents((p.getTenPhong() != null ? p.getTenPhong() : "").toLowerCase()).contains(removeAccents(kw.toLowerCase())))
                .filter(p -> loai.startsWith("Tất cả") || loai.equalsIgnoreCase(p.getMaLoaiPhong()))
                .filter(p -> tt.startsWith("Tất cả") || tt.equalsIgnoreCase(p.getTinhTrang()))
                .collect(Collectors.toList());

        renderRoomsByFloor(filtered);
    }

    private void renderRoomsByFloor(List<PhongDTO> rooms) {
        Platform.runLater(() -> {
            roomContainer.getChildren().clear();

            if (rooms == null || rooms.isEmpty()) {
                Label emptyLabel = new Label("📭 Không tìm thấy phòng nào phù hợp tiêu chí.");
                emptyLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
                emptyLabel.setTextFill(Color.web(COLOR_TEXT_MUTED));
                emptyLabel.setPadding(new Insets(30));
                roomContainer.getChildren().add(emptyLabel);
                return;
            }

            // Nhóm phòng theo tầng (Lấy ký tự đầu tiên của mã phòng làm tầng)
            Map<String, List<PhongDTO>> roomsByFloor = new TreeMap<>();
            for (PhongDTO room : rooms) {
                String floorKey = (room.getMaPhong() != null && !room.getMaPhong().isEmpty()) ? room.getMaPhong().substring(0, 1) : "0";
                roomsByFloor.computeIfAbsent(floorKey, k -> new ArrayList<>()).add(room);
            }

            // Vẽ giao diện cho từng tầng
            for (String floor : roomsByFloor.keySet()) {
                List<PhongDTO> floorRooms = roomsByFloor.get(floor);

                Label floorLabel = new Label("📍 Tầng " + floor);
                floorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
                floorLabel.setTextFill(Color.web(COLOR_PRIMARY));
                floorLabel.setPadding(new Insets(10, 0, 5, 0));
                roomContainer.getChildren().add(floorLabel);

                // Lưới chứa phòng của tầng đó (FlowPane giúp tự động xuống dòng)
                FlowPane flowPane = new FlowPane();
                flowPane.setHgap(15);
                flowPane.setVgap(15);

                for (PhongDTO room : floorRooms) {
                    VBox roomCard = createRoomCard(room);
                    flowPane.getChildren().add(roomCard);
                }

                roomContainer.getChildren().add(flowPane);
            }
        });
    }

    private VBox createRoomCard(PhongDTO room) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1;");
        card.setPrefWidth(220);
        card.setPrefHeight(180);

        // Hiệu ứng Hover
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #cbd5e1; -fx-border-width: 1; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1;"));

        // Sự kiện Click: Mở ChiTietPhongController (chuẩn bài của bạn)
        card.setOnMouseClicked(e -> {
            try {
                ChiTietPhongController detailController = new ChiTietPhongController(
                        room, dpIn.getValue(), dpOut.getValue(), currentUser,
                        phongService, phieuDatPhongService, khachHangService,
                        hoaDonService, chiTietHoaDonService, dichVuService,
                        this::loadDataAsync
                );
                detailController.showDialog(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        card.setCursor(Cursor.HAND);

        // Tên/Mã phòng
        Label lblMa = new Label(room.getMaPhong());
        lblMa.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
        lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

        // Loại phòng
        Label lblType = new Label(room.getMaLoaiPhong());
        lblType.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        lblType.setTextFill(Color.web(COLOR_TEXT_MUTED));

        // Trạng thái
        String statusText = room.getTinhTrang();
        String statusColor = getStatusColor(statusText);
        Label lblStatus = new Label(getStatusEmoji(statusText) + " " + statusText);
        lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblStatus.setStyle("-fx-background-color: " + statusColor + "1A; -fx-padding: 5 10; -fx-background-radius: 6; -fx-text-fill: " + statusColor + ";");

        // Giá phòng
        Label lblPrice = new Label(String.format("%,.0f đ", room.getGiaPhong()));
        lblPrice.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 15));
        lblPrice.setTextFill(Color.web(COLOR_PRIMARY));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(lblMa, lblType, lblStatus, spacer, lblPrice);
        return card;
    }

    private String getStatusColor(String status) {
        if (status == null) return COLOR_TEXT_MUTED;
        return switch (status) {
            case "Trống" -> COLOR_AVAILABLE;
            case "Đã Đặt" -> COLOR_BOOKED;
            case "Đang ở" -> COLOR_OCCUPIED;
            case "Bảo Trì" -> COLOR_MAINTENANCE;
            default -> COLOR_TEXT_MUTED;
        };
    }

    private String getStatusEmoji(String status) {
        if (status == null) return "❓";
        return switch (status) {
            case "Trống" -> "✅";
            case "Đã Đặt" -> "📅";
            case "Đang ở" -> "🔑";
            case "Bảo Trì" -> "🔧";
            default -> "❓";
        };
    }

    private String removeAccents(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized)
                .replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }
}