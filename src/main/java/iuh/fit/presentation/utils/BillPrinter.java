package iuh.fit.presentation.utils;

import iuh.fit.core.dto.HoaDonDTO;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;

public class BillPrinter {

    public static void printInvoice(HoaDonDTO hd, String phuongThuc) {
        // 1. Khung hóa đơn
        VBox container = new VBox(10);
        container.setPadding(new Insets(40));
        container.setStyle("-fx-background-color: white;");
        container.setPrefWidth(450);

        // Header Khách sạn
        Label brand = new Label("TTV HOTEL");
        brand.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label address = new Label("12 Nguyễn Văn Bảo, P.4, Gò Vấp, TP.HCM\nSĐT: 0123.456.789");
        address.setStyle("-fx-text-alignment: center;");

        VBox header = new VBox(5, brand, address);
        header.setAlignment(javafx.geometry.Pos.CENTER);

        Line line1 = new Line(0, 0, 370, 0);

        // Thông tin hóa đơn
        VBox info = new VBox(8);
        info.setPadding(new Insets(20, 0, 20, 0));
        info.getChildren().addAll(
                createRow("Mã hóa đơn:", hd.getMaHoaDon()),
                createRow("Ngày lập:", hd.getNgayLap().toString()),
                createRow("Khách hàng:", hd.getMaKhachHang()),
                createRow("Phòng:", hd.getTenPhong())
        );

        Line line2 = new Line(0, 0, 370, 0);

        // Chi tiết tiền
        VBox pricing = new VBox(8);
        pricing.setPadding(new Insets(10, 0, 10, 0));
        pricing.getChildren().addAll(
                createRow("Tiền phòng:", String.format("%,.0f đ", hd.getTongTienPhong())),
                createRow("Tiền dịch vụ:", String.format("%,.0f đ", hd.getTongTienDichVu())),
                createRow("Thuế VAT:", String.format("%,.0f đ", hd.getThueVAT())),
                createRow("Chiết khấu:", "-" + String.format("%,.0f đ", hd.getChietKhau()))
        );

        Label total = new Label("TỔNG CỘNG: " + String.format("%,.0f đ", hd.getTongTien()));
        total.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label footer = new Label("Cảm ơn quý khách - Hẹn gặp lại!");
        footer.setPadding(new Insets(30, 0, 0, 0));

        container.getChildren().addAll(header, line1, info, line2, pricing, total, footer);
        container.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        // 2. Thực hiện lệnh in (Mở hộp thoại chọn Save as PDF)
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(container);
            if (success) {
                job.endJob();
            }
        }
    }

    private static HBox createRow(String label, String value) {
        HBox row = new HBox();
        Label l = new Label(label);
        javafx.scene.layout.Region s = new javafx.scene.layout.Region();
        HBox.setHgrow(s, javafx.scene.layout.Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle("-fx-font-weight: bold;");
        row.getChildren().addAll(l, s, v);
        return row;
    }
}