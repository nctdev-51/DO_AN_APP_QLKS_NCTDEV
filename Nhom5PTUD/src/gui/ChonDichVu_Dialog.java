package gui;

import entity.DichVu;
import entity.LoaiDichVu; 

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChonDichVu_Dialog extends JDialog {

    private JTabbedPane tabbedPane;
    private JButton btnXacNhan, btnHuy;
    
    // =========================================================================
    // <<< THAY ĐỔI (1): Đổi JSpinner thành JTextField >>>
    // =Ghi chú: Chúng ta sẽ lưu trữ ô text ở giữa để lấy số lượng
    private Map<DichVu, JTextField> countFieldMap = new HashMap<>();
    // =========================================================================
    
    // Kết quả trả về
    private Map<DichVu, Integer> selectedServices = new HashMap<>();

    /**
     * @param owner Frame cha
     * @param dsDichVuToanBo Danh sách tất cả dịch vụ (đã tải từ DAO)
     */
    public ChonDichVu_Dialog(Frame owner, List<DichVu> dsDichVuToanBo) {
        super(owner, "Chọn dịch vụ", true);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Phân loại dịch vụ theo "loaiDichVu"
        Map<LoaiDichVu, List<DichVu>> groupedServices = 
            dsDichVuToanBo.stream().collect(Collectors.groupingBy(DichVu::getLoaiDichVu));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Tạo tab cho từng loại dịch vụ
        for (LoaiDichVu loai : groupedServices.keySet()) {
            JPanel pnlCategory = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
            pnlCategory.setBackground(Color.WHITE);
            
            List<DichVu> dsTheoLoai = groupedServices.get(loai);
            dsTheoLoai.sort((dv1, dv2) -> dv1.getTenDichVu().compareTo(dv2.getTenDichVu()));
            
            for (DichVu dv : dsTheoLoai) {
                pnlCategory.add(createServiceCard(dv));
            }
            
            JScrollPane scrollPane = new JScrollPane(pnlCategory);
            scrollPane.setBorder(null);
            
            tabbedPane.addTab(loai.toString(), scrollPane);
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnXacNhan = new JButton("Xác nhận");
        btnHuy = new JButton("Hủy");
        pnlButtons.add(btnHuy);
        pnlButtons.add(btnXacNhan);
        add(pnlButtons, BorderLayout.SOUTH);

        // Sự kiện
        btnXacNhan.addActionListener(e -> xacNhanChon());
        btnHuy.addActionListener(e -> dispose());
    }

    /**
     * Tạo một Panel (card) cho một dịch vụ
     */
    private JPanel createServiceCard(DichVu dv) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(150, 200));
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        // 1. Hình ảnh
        ImageIcon icon = new ImageIcon(dv.getHinhAnh());
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE || !new File(dv.getHinhAnh()).exists()) {
            icon = new ImageIcon("data/services/default.png"); 
        }
        Image img = icon.getImage().getScaledInstance(130, 100, Image.SCALE_SMOOTH);
        JLabel lblHinhAnh = new JLabel(new ImageIcon(img));
        card.add(lblHinhAnh, BorderLayout.NORTH);

        // 2. Thông tin (Tên, Giá, Đơn vị)
        DecimalFormat df = new DecimalFormat("#,##0");
        String giaStr = df.format(dv.getGiaTien()) + "/" + dv.getDonViTinh().toString();
        
        JLabel lblTen = new JLabel(dv.getTenDichVu());
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTen.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblGia = new JLabel(giaStr);
        lblGia.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblGia.setForeground(Color.RED);
        lblGia.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1));
        pnlInfo.setOpaque(false);
        pnlInfo.add(lblTen);
        pnlInfo.add(lblGia);
        card.add(pnlInfo, BorderLayout.CENTER);

        // =========================================================================
        // <<< THAY ĐỔI (2): Thay JSpinner bằng Stepper (JButton + JTextField + JButton) >>>
        // =========================================================================
        
        // 3. Số lượng (Component Stepper)
        JPanel pnlStepper = new JPanel(new BorderLayout(3, 3)); 
        pnlStepper.setOpaque(false);
        pnlStepper.setPreferredSize(new Dimension(100, 28)); // Đặt kích thước cố định
        
        JButton btnMinus = new JButton("-");
        btnMinus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMinus.setMargin(new Insets(2, 6, 2, 6)); // Thu nhỏ nút
        
        JButton btnPlus = new JButton("+");
        btnPlus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPlus.setMargin(new Insets(2, 6, 2, 6));

        JTextField txtCount = new JTextField("0");
        txtCount.setEditable(false);
        txtCount.setHorizontalAlignment(JTextField.CENTER);
        txtCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtCount.setBackground(Color.WHITE); // Đảm bảo nền trắng
        txtCount.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        pnlStepper.add(btnMinus, BorderLayout.WEST);
        pnlStepper.add(txtCount, BorderLayout.CENTER);
        pnlStepper.add(btnPlus, BorderLayout.EAST);
        
        // Panel lót để giữ pnlStepper ở giữa (FlowLayout)
        JPanel pnlStepperWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlStepperWrapper.setOpaque(false);
        pnlStepperWrapper.add(pnlStepper);
        
        card.add(pnlStepperWrapper, BorderLayout.SOUTH);
        
        // Thêm sự kiện cho nút
        btnMinus.addActionListener(e -> {
            try {
                int count = Integer.parseInt(txtCount.getText());
                if (count > 0) {
                    count--;
                    txtCount.setText(String.valueOf(count));
                }
            } catch (NumberFormatException ex) { txtCount.setText("0"); }
        });
        
        btnPlus.addActionListener(e -> {
             try {
                int count = Integer.parseInt(txtCount.getText());
                if (count < 100) { // Giới hạn 100
                    count++;
                    txtCount.setText(String.valueOf(count));
                }
            } catch (NumberFormatException ex) { txtCount.setText("0"); }
        });
        
        // Lưu text field này vào Map
        countFieldMap.put(dv, txtCount);
        // =========================================================================

        return card;
    }

    /**
     * Xử lý khi nhấn nút "Xác nhận"
     */
    private void xacNhanChon() {
        selectedServices.clear();
        
        // =========================================================================
        // <<< THAY ĐỔI (3): Đọc giá trị từ JTextField thay vì JSpinner >>>
        // =========================================================================
        for (Map.Entry<DichVu, JTextField> entry : countFieldMap.entrySet()) {
            int soLuong = 0;
            try {
                // Đọc text từ ô số lượng
                soLuong = Integer.parseInt(entry.getValue().getText());
            } catch (NumberFormatException e) {
                soLuong = 0; // Bỏ qua nếu có lỗi
            }
            
            if (soLuong > 0) {
                selectedServices.put(entry.getKey(), soLuong);
            }
        }
        this.dispose(); // Đóng dialog
    }

    /**
     * Hàm public để Frame cha gọi và lấy kết quả
     * @return Map<DichVu, Integer> (Dịch vụ đã chọn và số lượng)
     */
    public Map<DichVu, Integer> getSelectedServices() {
        return selectedServices;
    }

    // Inner class WrapLayout (để các card tự động xuống hàng)
    class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            Dimension size = super.preferredLayoutSize(target);
            if (target.getParent() instanceof JViewport) {
                JViewport viewport = (JViewport) target.getParent();
                size.width = Math.max(viewport.getWidth() - 20, 100); 
            }
            return size;
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension size = super.minimumLayoutSize(target);
            if (target.getParent() instanceof JViewport) {
                JViewport viewport = (JViewport) target.getParent();
                size.width = Math.max(viewport.getWidth() - 20, 100);
            }
            return size;
        }
    }
}