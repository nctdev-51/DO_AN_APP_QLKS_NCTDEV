// File: iuh/fit/presentation/controller/QuanLyPheDuyetCaDialog.java
package iuh.fit.presentation.controller;

// 👉 SỬA: Import DTO thay vì Entity
import iuh.fit.core.dto.YeuCauPheDuyetDTO;
import iuh.fit.core.service.IYeuCauPheDuyetService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuanLyPheDuyetCaDialog {

    private final IYeuCauPheDuyetService yeuCauService;

    public QuanLyPheDuyetCaDialog(IYeuCauPheDuyetService yeuCauService) {
        this.yeuCauService = yeuCauService;
    }

    public void showDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Duyệt yêu cầu vào ca trễ");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        Label lblTitle = new Label("Danh sách yêu cầu xin vào ca");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        // 👉 SỬA: Đổi kiểu TableView sang DTO
        TableView<YeuCauPheDuyetDTO> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<YeuCauPheDuyetDTO, String> colNV = new TableColumn<>("Nhân viên");
        colNV.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTenNhanVien()));

        TableColumn<YeuCauPheDuyetDTO, String> colLyDo = new TableColumn<>("Lý do");
        colLyDo.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getLyDo()));

        TableColumn<YeuCauPheDuyetDTO, String> colThoiGian = new TableColumn<>("Thời gian yêu cầu");
        colThoiGian.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue().getThoiGianYeuCau().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

        table.getColumns().addAll(colNV, colLyDo, colThoiGian);

        Button btnDuyet = new Button("Duyệt");
        btnDuyet.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnTuChoi = new Button("Từ chối");
        btnTuChoi.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox actions = new HBox(10, btnDuyet, btnTuChoi);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Runnable refreshTable = () -> {
            // 👉 SỬA: Nhận List DTO
            List<YeuCauPheDuyetDTO> list = yeuCauService.getYeuCauChuaDuyet();
            table.setItems(FXCollections.observableArrayList(list));
        };

        refreshTable.run();

        btnDuyet.setOnAction(e -> {
            // 👉 SỬA: Lấy đối tượng DTO từ Table
            YeuCauPheDuyetDTO selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một yêu cầu").show();
                return;
            }
            try {
                yeuCauService.duyetYeuCau(selected.getMaYeuCau(), "admin", true);
                new Alert(Alert.AlertType.INFORMATION, "Đã duyệt cho " + selected.getTenNhanVien() + " vào ca").show();
                refreshTable.run();
                if (yeuCauService.getYeuCauChuaDuyet().isEmpty()) {
                    dialog.close();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi duyệt: " + ex.getMessage()).show();
            }
        });

        btnTuChoi.setOnAction(e -> {
            // 👉 SỬA: Lấy đối tượng DTO từ Table
            YeuCauPheDuyetDTO selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một yêu cầu").show();
                return;
            }
            try {
                yeuCauService.duyetYeuCau(selected.getMaYeuCau(), "admin", false);
                new Alert(Alert.AlertType.INFORMATION, "Đã từ chối yêu cầu của " + selected.getTenNhanVien()).show();
                refreshTable.run();
                if (yeuCauService.getYeuCauChuaDuyet().isEmpty()) {
                    dialog.close();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi: " + ex.getMessage()).show();
            }
        });

        root.getChildren().addAll(lblTitle, table, actions);
        dialog.setScene(new javafx.scene.Scene(root, 600, 400));
        dialog.showAndWait();
    }
}