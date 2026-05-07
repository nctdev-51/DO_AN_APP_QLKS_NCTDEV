package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task; // 👉 DÒNG NÀY LÀ DÒNG BỊ THIẾU
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ChonPhongController {

    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    private final IPhieuDatPhongService phieuDatPhongService;
    private final TaiKhoanDTO currentUser;
    private final Stage primaryStage;

    // UI Components
    private VBox roomContainer;
    private ListView<TempBooking> lvTempBookings;
    private ObservableList<TempBooking> tempBookingsList;
    private Set<String> currentSelectedRoomIds = new HashSet<>();

    // Filters
    private DatePicker dpIn, dpOut;
    private ComboBox<String> cbType;
    private Slider sliderPrice;
    private Label lblPriceValue;

    // Colors
    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_WARNING = "#f59e0b";
    private final String COLOR_BORDER = "#e2e8f0";

    public ChonPhongController(IPhongService phongService, IKhachHangService khachHangService,
                               IPhieuDatPhongService phieuDatPhongService, TaiKhoanDTO currentUser, Stage primaryStage) {
        this.phongService = phongService;
        this.khachHangService = khachHangService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.currentUser = currentUser;
        this.primaryStage = primaryStage;
        this.tempBookingsList = FXCollections.observableArrayList();
    }

    public HBox createView() {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // --- 1. SIDEBAR TRÁI: DANH SÁCH PHIẾU TẠM ---
        VBox leftSidebar = createSidebar();

        // --- 2. KHU VỰC CHÍNH: BỘ LỌC + LƯỚI PHÒNG ---
        VBox mainArea = new VBox(20);
        mainArea.setPadding(new Insets(25));
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        VBox header = new VBox(5);
        Label lblTitle = new Label("CHỌN PHÒNG TRỐNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 26));
        Label lblSub = new Label("Tìm và chọn các phòng trống để đưa vào danh sách chờ đặt.");
        lblSub.setTextFill(Color.web("#64748b"));
        header.getChildren().addAll(lblTitle, lblSub);

        HBox filterBar = createFilterBar();

        roomContainer = new VBox(20);
        ScrollPane scrollPane = new ScrollPane(roomContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainArea.getChildren().addAll(header, filterBar, scrollPane);

        root.getChildren().addAll(leftSidebar, mainArea);

        loadRooms(); // Load lần đầu
        return root;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(320);
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.setStyle("-fx-background-color: white; -fx-border-width: 0 1 0 0; -fx-border-color: " + COLOR_BORDER + ";");

        Label lblTitle = new Label("📑 PHIẾU TẠM (" + tempBookingsList.size() + ")");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tempBookingsList.addListener((javafx.collections.ListChangeListener<TempBooking>) c ->
                lblTitle.setText("📑 PHIẾU TẠM (" + tempBookingsList.size() + ")"));

        lvTempBookings = new ListView<>(tempBookingsList);
        VBox.setVgrow(lvTempBookings, Priority.ALWAYS);
        lvTempBookings.setCellFactory(param -> new TempBookingCell());

        Button btnProceed = new Button("✅ XÁC NHẬN ĐẶT PHÒNG");
        btnProceed.setMaxWidth(Double.MAX_VALUE);
        btnProceed.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8;");
        btnProceed.setCursor(Cursor.HAND);
        btnProceed.setOnAction(e -> handleConfirmAll());

        Button btnClearAll = new Button("🔄 Xóa tất cả");
        btnClearAll.setMaxWidth(Double.MAX_VALUE);
        btnClearAll.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-padding: 8;");
        btnClearAll.setOnAction(e -> {
            tempBookingsList.clear();
            loadRooms();
        });

        sidebar.getChildren().addAll(lblTitle, lvTempBookings, btnClearAll, btnProceed);
        return sidebar;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(15));
        bar.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12;");

        dpIn = new DatePicker(LocalDate.now());
        dpOut = new DatePicker(LocalDate.now().plusDays(1));
        dpIn.setPrefWidth(120); dpOut.setPrefWidth(120);

        cbType = new ComboBox<>(FXCollections.observableArrayList("Tất cả", "DON", "DOI", "GIADINH", "VIP"));
        cbType.setValue("Tất cả");

        VBox priceBox = new VBox(5);
        lblPriceValue = new Label("Giá tối đa: 5.000.000đ");
        lblPriceValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        sliderPrice = new Slider(0, 10000000, 5000000);
        sliderPrice.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblPriceValue.setText(String.format("Giá tối đa: %,.0fđ", newVal.doubleValue()));
        });
        priceBox.getChildren().addAll(lblPriceValue, sliderPrice);

        Button btnSearch = new Button("🔍 Tìm");
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6;");
        btnSearch.setOnAction(e -> loadRooms());

        Button btnAddTemp = new Button("➕ Thêm vào phiếu tạm");
        btnAddTemp.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6;");
        btnAddTemp.setOnAction(e -> handleAddToTemp());

        bar.getChildren().addAll(new Label("Từ:"), dpIn, new Label("Đến:"), dpOut, cbType, priceBox, btnSearch, btnAddTemp);
        return bar;
    }

    private void loadRooms() {
        roomContainer.getChildren().clear();
        currentSelectedRoomIds.clear();

        LocalDate in = dpIn.getValue();
        LocalDate out = dpOut.getValue();
        if (in == null || out == null || !out.isAfter(in)) return;

        double maxPrice = sliderPrice.getValue();
        String type = cbType.getValue();

        Task<List<PhongDTO>> task = new Task<>() {
            @Override protected List<PhongDTO> call() {
                return phongService.findAvailableRooms(in, out, 0, maxPrice);
            }
        };

        task.setOnSucceeded(e -> {
            List<PhongDTO> rooms = task.getValue();
            if (!type.equals("Tất cả")) {
                rooms = rooms.stream().filter(r -> r.getMaLoaiPhong().equals(type)).collect(Collectors.toList());
            }
            renderRoomGrid(rooms);
        });
        new Thread(task).start();
    }

    private void renderRoomGrid(List<PhongDTO> rooms) {
        if (rooms.isEmpty()) {
            roomContainer.getChildren().add(new Label("Không có phòng trống nào phù hợp."));
            return;
        }

        Map<String, List<PhongDTO>> byFloor = new TreeMap<>();
        for (PhongDTO r : rooms) {
            String floor = r.getMaPhong().substring(0, 1);
            byFloor.computeIfAbsent(floor, k -> new ArrayList<>()).add(r);
        }

        for (String floor : byFloor.keySet()) {
            Label lblFloor = new Label("📍 Tầng " + floor);
            lblFloor.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
            lblFloor.setTextFill(Color.web(COLOR_PRIMARY));

            FlowPane flow = new FlowPane(15, 15);
            for (PhongDTO r : byFloor.get(floor)) {
                flow.getChildren().add(createRoomNode(r));
            }
            roomContainer.getChildren().addAll(lblFloor, flow, new Region());
        }
    }

    private VBox createRoomNode(PhongDTO r) {
        VBox node = new VBox(5);
        node.setAlignment(Pos.CENTER);
        node.setPrefSize(110, 90);
        node.setCursor(Cursor.HAND);

        String baseStyle = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-border-width: 1;";
        node.setStyle(baseStyle);

        Label lblMa = new Label(r.getMaPhong());
        lblMa.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        Label lblPrice = new Label(String.format("%,.0fđ", r.getGiaPhong()));
        lblPrice.setStyle("-fx-font-size: 11px;");

        node.getChildren().addAll(lblMa, new Label(r.getMaLoaiPhong()), lblPrice);

        node.setOnMouseClicked(e -> {
            if (currentSelectedRoomIds.contains(r.getMaPhong())) {
                currentSelectedRoomIds.remove(r.getMaPhong());
                node.setStyle(baseStyle);
            } else {
                currentSelectedRoomIds.add(r.getMaPhong());
                node.setStyle(baseStyle + "-fx-background-color: " + COLOR_WARNING + "22; -fx-border-color: " + COLOR_WARNING + "; -fx-border-width: 2;");
            }
        });

        return node;
    }

    private void handleAddToTemp() {
        if (currentSelectedRoomIds.isEmpty()) {
            showAlert("Cảnh báo", "Vui lòng chọn ít nhất một phòng!");
            return;
        }

        LocalDate in = dpIn.getValue();
        LocalDate out = dpOut.getValue();

        List<String> rooms = new ArrayList<>(currentSelectedRoomIds);
        String id = "TẠM-" + System.currentTimeMillis() % 10000;
        tempBookingsList.add(new TempBooking(id, in, out, rooms));

        currentSelectedRoomIds.clear();
        loadRooms(); // Refresh lại sơ đồ
    }

    private void handleConfirmAll() {
        if (tempBookingsList.isEmpty()) {
            showAlert("Thông báo", "Danh sách phiếu tạm đang trống!");
            return;
        }
        // Logic chuyển sang màn hình nhập thông tin khách hàng và thanh toán
        showAlert("Thành công", "Đang chuyển sang màn hình hoàn tất đặt phòng...");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- INNER CLASSES ---
    public static class TempBooking {
        String id; LocalDate from, to; List<String> roomIds;
        TempBooking(String id, LocalDate f, LocalDate t, List<String> r) { this.id = id; this.from = f; this.to = t; this.roomIds = r; }
    }

    private class TempBookingCell extends ListCell<TempBooking> {
        @Override protected void updateItem(TempBooking item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setGraphic(null); setText(null); }
            else {
                VBox box = new VBox(3);
                Label lblId = new Label(item.id); lblId.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");
                Label lblDate = new Label(item.from.format(DateTimeFormatter.ofPattern("dd/MM")) + " ⮕ " + item.to.format(DateTimeFormatter.ofPattern("dd/MM")));
                Label lblRooms = new Label("Phòng: " + String.join(", ", item.roomIds));
                lblRooms.setStyle("-fx-font-size: 11px;");

                box.getChildren().addAll(lblId, lblDate, lblRooms);
                setGraphic(box);
            }
        }
    }
}