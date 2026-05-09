package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class QuanLyPhanCongCaController {
    private final IPhanCongService phanCongService;
    private final INhanVienService nhanVienService;
    private final ICaLamViecService caLamViecService;

    private GridPane spreadsheet;
    private DatePicker dpNgayBatDau;
    private int daysToRender = 7;

    private List<CaLamViecDTO> listCa;
    private List<NhanVienDTO> listNhanVien;
    private List<PhanCongDTO> listPhanCongHienTai;

    public QuanLyPhanCongCaController(IPhanCongService pc, INhanVienService nv, ICaLamViecService ca) {
        this.phanCongService = pc;
        this.nhanVienService = nv;
        this.caLamViecService = ca;
    }

    public VBox createView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f8fafc;");

        // --- TITLE & TOOLBAR ---
        Label lblTitle = new Label("BẢNG THEO DÕI & PHÂN CÔNG CA LÀM VIỆC (MULTI-STAFF)");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 24));
        lblTitle.setTextFill(Color.web("#0f172a"));

        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0;");

        dpNgayBatDau = new DatePicker(LocalDate.now());
        dpNgayBatDau.setPrefWidth(140);
        dpNgayBatDau.setOnAction(e -> reloadGrid());

        Button btn1Day = createToolButton("1 Ngày"); btn1Day.setOnAction(e -> { daysToRender = 1; reloadGrid(); });
        Button btn3Day = createToolButton("3 Ngày"); btn3Day.setOnAction(e -> { daysToRender = 3; reloadGrid(); });
        Button btn7Day = createToolButton("1 Tuần"); btn7Day.setOnAction(e -> { daysToRender = 7; reloadGrid(); });
        Button btn30Day = createToolButton("1 Tháng"); btn30Day.setOnAction(e -> { daysToRender = 30; reloadGrid(); });

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLoad = new Button("Làm Mới Dữ Liệu");
        btnLoad.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnLoad.setOnAction(e -> reloadGrid());

        toolbar.getChildren().addAll(new Label("Từ ngày:"), dpNgayBatDau, btn1Day, btn3Day, btn7Day, btn30Day, spacer, btnLoad);

        // --- SPREADSHEET ---
        spreadsheet = new GridPane();
        spreadsheet.setHgap(3); spreadsheet.setVgap(3);
        spreadsheet.setStyle("-fx-background-color: #cbd5e1; -fx-padding: 3;");

        ScrollPane scroll = new ScrollPane(spreadsheet);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(lblTitle, toolbar, scroll);

        fetchDataAndBuildGrid();
        return root;
    }

    private Button createToolButton(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 5 15;");
        return b;
    }

    private void reloadGrid() {
        spreadsheet.getChildren().clear();
        fetchDataAndBuildGrid();
    }

    private void fetchDataAndBuildGrid() {
        try {
            listCa = caLamViecService.getAll();
            listNhanVien = nhanVienService.getAllNhanVien();
            listPhanCongHienTai = phanCongService.findAll();

            if (listCa == null || listCa.isEmpty()) {
                spreadsheet.add(new Label("⚠ Cảnh báo: Hệ thống chưa có dữ liệu Ca làm việc!"), 0, 0);
                return;
            }

            buildGridHeaders();
            buildGridRows();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildGridHeaders() {
        spreadsheet.getColumnConstraints().clear();

        ColumnConstraints dateCol = new ColumnConstraints(130);
        spreadsheet.getColumnConstraints().add(dateCol);

        Label lblDateH = createHeaderCell("Ngày / Tháng");
        spreadsheet.add(lblDateH, 0, 0);

        for (int i = 0; i < listCa.size(); i++) {
            spreadsheet.getColumnConstraints().add(new ColumnConstraints(280)); // Nới rộng cột để chứa nhiều NV
            CaLamViecDTO ca = listCa.get(i);
            Label lblCa = createHeaderCell(ca.getTenCa() + "\n(" + ca.getGioBatDau() + " - " + ca.getGioKetThuc() + ")");
            spreadsheet.add(lblCa, i + 1, 0);
        }
    }

    private void buildGridRows() {
        LocalDate startDate = dpNgayBatDau.getValue();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy\n(EEEE)");

        for (int row = 0; row < daysToRender; row++) {
            LocalDate currentDate = startDate.plusDays(row);

            Label lblDate = new Label(currentDate.format(dtf));
            lblDate.setAlignment(Pos.CENTER);
            lblDate.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            lblDate.setStyle("-fx-background-color: white; -fx-padding: 15 5; -fx-font-weight: bold; -fx-text-fill: #334155; -fx-text-alignment: center;");
            spreadsheet.add(lblDate, 0, row + 1);

            for (int col = 0; col < listCa.size(); col++) {
                CaLamViecDTO ca = listCa.get(col);

                // LẤY DANH SÁCH NHIỀU NHÂN VIÊN TRONG CÙNG 1 CA
                List<PhanCongDTO> assignedInThisShift = listPhanCongHienTai.stream()
                        .filter(pc -> pc.getNgayLamViec().equals(currentDate) && pc.getMaCa().equals(ca.getMaCa()))
                        .collect(Collectors.toList());

                VBox cell = createMultiInteractiveCell(currentDate, ca, assignedInThisShift);
                spreadsheet.add(cell, col + 1, row + 1);
            }
        }
    }

    private Label createHeaderCell(String text) {
        Label lbl = new Label(text);
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lbl.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15 10; -fx-text-alignment: center;");
        return lbl;
    }

    // =========================================================================
    // TẠO Ô LƯỚI HỖ TRỢ NGHIỆP VỤ THỰC TẾ (XIN NGHỈ, THAY NGƯỜI)
    // =========================================================================
    private VBox createMultiInteractiveCell(LocalDate date, CaLamViecDTO ca, List<PhanCongDTO> assignedList) {
        VBox box = new VBox(8);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        box.setPadding(new Insets(10));

        // Lọc ra số người THỰC SỰ ĐI LÀM (Loại trừ những người đã báo vắng mặt)
        long activeStaffCount = assignedList.stream()
                .filter(pc -> pc.getTrangThai() == null || !pc.getTrangThai().equals("VANG_MAT"))
                .count();

        // Cảnh báo Đỏ nếu không có ai đi làm
        if (activeStaffCount == 0) {
            box.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #ef4444; -fx-border-width: 2;");
        } else {
            box.setStyle("-fx-background-color: #ecfdf5; -fx-border-color: #10b981; -fx-border-width: 1;");
        }

        VBox listContainer = new VBox(5);

        for (PhanCongDTO pc : assignedList) {
            HBox rowNV = new HBox(5);
            rowNV.setAlignment(Pos.CENTER_LEFT);

            boolean isAbsent = pc.getTrangThai() != null && pc.getTrangThai().equals("VANG_MAT");

            // UX: Nếu vắng mặt, làm xám màu và gạch ngang tên
            if (isAbsent) {
                rowNV.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 5 8; -fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-opacity: 0.7;");
            } else {
                rowNV.setStyle("-fx-background-color: white; -fx-padding: 5 8; -fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-cursor: hand;");
            }

            Label lblName = new Label((isAbsent ? "❌ " : "👤 ") + pc.getTenNhanVien());
            lblName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            if (isAbsent) lblName.setStyle("-fx-strikethrough: true; -fx-text-fill: #94a3b8;");
            else lblName.setTextFill(Color.web("#1e293b"));

            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

            Button btnAction = new Button("⚙");
            btnAction.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-padding: 2;");

            // MENU NGHIỆP VỤ KHI CLICK VÀO NHÂN VIÊN
            ContextMenu contextMenu = new ContextMenu();

            MenuItem itemHuy = new MenuItem("Xóa khỏi ca (Xếp nhầm)");
            itemHuy.setStyle("-fx-text-fill: #ef4444;");
            itemHuy.setOnAction(e -> handleXoaNhanVien(pc, activeStaffCount));

            MenuItem itemNghi = new MenuItem("Báo vắng đột xuất (Lưu lịch sử HR)");
            itemNghi.setOnAction(e -> handleBaoVangMat(pc, activeStaffCount));

            MenuItem itemDiLamLai = new MenuItem("Hủy vắng mặt (Đi làm lại)");
            itemDiLamLai.setOnAction(e -> {
                pc.setTrangThai("CHUA_LAM");
                try {
                    phanCongService.update(pc);
                    reloadGrid();
                } catch (Exception ex) {}
            });

            if (isAbsent) contextMenu.getItems().add(itemDiLamLai);
            else contextMenu.getItems().addAll(itemNghi, new SeparatorMenuItem(), itemHuy);

            btnAction.setOnMouseClicked(e -> contextMenu.show(btnAction, e.getScreenX(), e.getScreenY()));
            rowNV.setOnMouseClicked(e -> contextMenu.show(rowNV, e.getScreenX(), e.getScreenY()));

            rowNV.getChildren().addAll(lblName, sp, btnAction);
            listContainer.getChildren().add(rowNV);
        }

        if (activeStaffCount == 0) {
            Label lblEmpty = new Label("⚠ Thiếu nhân sự trực ca!");
            lblEmpty.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 11px;");
            listContainer.getChildren().add(lblEmpty);
        }

        // =====================================================================
        // COMBOBOX TÌM KIẾM THÔNG MINH (AUTOCOMPLETE)
        // =====================================================================
        ComboBox<NhanVienDTO> cbAddNV = new ComboBox<>();
        cbAddNV.setEditable(true);
        cbAddNV.getEditor().setPromptText("🔍 Gõ tên hoặc mã NV...");
        cbAddNV.setMaxWidth(Double.MAX_VALUE);
        cbAddNV.setStyle("-fx-font-size: 11px; -fx-background-color: white; -fx-border-color: #94a3b8; -fx-border-radius: 4;");

        // 1. Lấy danh sách nhân viên chưa có trong ca này
        List<NhanVienDTO> availableNVs = listNhanVien.stream()
                .filter(nv -> assignedList.stream().noneMatch(pc -> pc.getMaNhanVien().equals(nv.getMaNhanVien())))
                .collect(Collectors.toList());

        // 2. Bọc list vào FilteredList để làm tính năng Lọc (Search)
        javafx.collections.transformation.FilteredList<NhanVienDTO> filteredItems =
                new javafx.collections.transformation.FilteredList<>(FXCollections.observableArrayList(availableNVs), p -> true);

        cbAddNV.setItems(filteredItems);

        // 3. Định dạng cách hiển thị (Mã - Tên)
        cbAddNV.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(NhanVienDTO nv) {
                return nv == null ? "" : nv.getMaNhanVien() + " - " + nv.getHoTen();
            }
            @Override public NhanVienDTO fromString(String string) {
                return cbAddNV.getItems().stream()
                        .filter(nv -> (nv.getMaNhanVien() + " - " + nv.getHoTen()).equals(string))
                        .findFirst().orElse(null);
            }
        });

        // 4. Lắng nghe thao tác gõ phím của Quản lý để lọc danh sách Real-time
        cbAddNV.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = cbAddNV.getEditor();
            final NhanVienDTO selected = cbAddNV.getSelectionModel().getSelectedItem();

            // Tránh việc tự động lọc lại khi người dùng vừa dùng chuột click chọn 1 người
            if (selected != null && editor.getText().equals(cbAddNV.getConverter().toString(selected))) {
                return;
            }

            filteredItems.setPredicate(nv -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String filter = removeAccents(newValue.toLowerCase().trim());
                String tenNV = removeAccents(nv.getHoTen().toLowerCase());
                String maNV = removeAccents(nv.getMaNhanVien().toLowerCase());

                return tenNV.contains(filter) || maNV.contains(filter);
            });

            if (!filteredItems.isEmpty() && cbAddNV.getScene() != null) {
                cbAddNV.show();
            } else {
                cbAddNV.hide();
            }
        });

        // 5. Bắt sự kiện khi Quản lý chọn nhân viên (Click hoặc Enter) để lưu Database
        cbAddNV.setOnAction(e -> {
            NhanVienDTO selected = cbAddNV.getValue();

            if (selected != null && selected.getMaNhanVien() != null) {

                // Bắt lỗi chống kiệt sức (Làm quá 2 ca/ngày)
                long soCaTrongNgay = listPhanCongHienTai.stream()
                        .filter(pc -> pc.getMaNhanVien().equals(selected.getMaNhanVien()) && pc.getNgayLamViec().equals(date))
                        .count();

                if (soCaTrongNgay >= 2) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Nhân viên này đã làm 2 ca trong ngày hôm nay. Việc xếp thêm ca sẽ vi phạm luật lao động!", ButtonType.OK);
                    alert.showAndWait();
                    reloadGrid();
                    return;
                }

                try {
                    PhanCongDTO newPC = new PhanCongDTO();
                    newPC.setMaPhanCong("PC_" + System.currentTimeMillis());
                    newPC.setMaNhanVien(selected.getMaNhanVien());
                    newPC.setTenNhanVien(selected.getHoTen());
                    newPC.setMaCa(ca.getMaCa());
                    newPC.setTenCa(ca.getTenCa());
                    newPC.setNgayLamViec(date);
                    newPC.setTrangThai("CHUA_LAM");
                    newPC.setGhiChu("");

                    phanCongService.save(newPC);
                    reloadGrid();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Lỗi phân công: " + ex.getMessage()).show();
                    reloadGrid();
                }
            }
        });

        box.getChildren().addAll(listContainer, new Separator(), cbAddNV);
        return box;
    }

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ BÊN TRONG ---

    private void handleXoaNhanVien(PhanCongDTO pc, long activeStaffCount) {
        if (activeStaffCount == 1 && (pc.getTrangThai() == null || !pc.getTrangThai().equals("VANG_MAT"))) {
            showSafeFailWarning("Không thể xóa", "Ca làm việc phải có ít nhất 1 người.\nHãy thêm nhân viên thế chỗ trước khi xóa người này!");
        } else {
            try {
                phanCongService.delete(pc.getMaPhanCong());
                reloadGrid();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void handleBaoVangMat(PhanCongDTO pc, long activeStaffCount) {
        if (activeStaffCount == 1) {
            showSafeFailWarning("Nguy cơ bỏ trống ca", "Đây là nhân viên duy nhất trong ca.\nBạn vẫn có thể đánh dấu vắng mặt, nhưng hệ thống sẽ báo ĐỎ yêu cầu bạn lập tức bổ sung người trực thay thế!");
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Báo vắng mặt đột xuất");
        dialog.setHeaderText("Ghi nhận lý do vắng mặt cho " + pc.getTenNhanVien());
        dialog.setContentText("Lý do (VD: Bệnh, Việc gia đình...):");

        dialog.showAndWait().ifPresent(lyDo -> {
            pc.setTrangThai("VANG_MAT");
            pc.setGhiChu(lyDo);
            try {
                phanCongService.update(pc);
                reloadGrid();
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }

    private void showSafeFailWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ràng buộc Vận Hành");
        alert.setHeaderText("⛔ " + title);
        alert.setContentText(content);
        alert.show();
    }

    // =========================================================================
    // HÀM HỖ TRỢ TÌM KIẾM KHÔNG DẤU
    // =========================================================================
    private String removeAccents(String str) {
        if (str == null) return "";
        try {
            String temp = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replace('đ','d').replace('Đ','D');
        } catch (Exception e) {
            return str;
        }
    }
}