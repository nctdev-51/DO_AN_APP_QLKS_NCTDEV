package gui;

import dto.PhongFilterResult;
import entity.NhanVien;
import entity.Phong;

// <<< THÊM MỚI 2 IMPORT NÀY >>>
import gui.ChonPhong_Gui; // Cần để cast 'owner'
import gui.DatPhong_Gui;   // Cần để mở phiếu đặt phòng

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ChonPhuongAn_Dialog extends JDialog {

    private final PhongFilterResult result;
    private final NhanVien nhanVien;
    private final Date checkInDate;
    private final Date checkOutDate;
    private final Frame owner;
    private final NumberFormat currencyFormatter;

    public ChonPhuongAn_Dialog(Frame owner, PhongFilterResult result, NhanVien nhanVien, Date checkInDate, Date checkOutDate) {
        super(owner, "Chọn phương án đặt phòng", true); // true = modal
        this.owner = owner;
        this.result = result;
        this.nhanVien = nhanVien;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        initializeUI();
    }

    private void initializeUI() {
        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel(result.getMessage(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(lblTitle, BorderLayout.NORTH);

        // Panel chính chứa danh sách các phương án, dùng BoxLayout để xếp dọc
        JPanel pnlOptionsList = new JPanel();
        pnlOptionsList.setLayout(new BoxLayout(pnlOptionsList, BoxLayout.Y_AXIS));
        pnlOptionsList.setBackground(Color.WHITE);

        List<List<Phong>> options = result.getPhongOptions();
        for (int i = 0; i < options.size(); i++) {
            List<Phong> phuongAn = options.get(i);
            // Tạo một panel cho mỗi phương án
            JPanel optionPanel = taoPhuongAnPanel(phuongAn, i + 1);
            pnlOptionsList.add(optionPanel);
            pnlOptionsList.add(Box.createVerticalStrut(10)); // Khoảng cách giữa các phương án
        }

        // Đặt pnlOptionsList vào JScrollPane
        JScrollPane scrollPane = new JScrollPane(pnlOptionsList);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Nút Đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSouth.setBorder(new EmptyBorder(5, 10, 5, 10));
        pnlSouth.add(btnClose);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    /**
     * Hàm helper để tạo một JPanel cho một phương án (1 phòng hoặc combo)
     */
    private JPanel taoPhuongAnPanel(List<Phong> phuongAn, int index) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        panel.setBackground(new Color(248, 249, 250));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Giới hạn chiều cao
        panel.setPreferredSize(new Dimension(600, 120));

        // 1. Thông tin phương án (Bên trái)
        boolean laCombo = phuongAn.size() > 1;
        
        // Tính toán thông tin
        String tenPhuongAn = laCombo ? "Phương án Combo " + index : "Phương án " + index;
        String cacPhong = phuongAn.stream().map(Phong::getMaPhong).collect(Collectors.joining(", "));
        int tongSucChua = phuongAn.stream().mapToInt(Phong::getSucChua).sum();
        double tongGia = phuongAn.stream().mapToDouble(Phong::getGiaPhong).sum();

        // Dùng HTML để hiển thị
        String infoHtml = String.format("<html>" +
                        "<body style='width: 350px;'>" +
                        "<h3 style='margin:0;'>" + tenPhuongAn + "</h3>" +
                        "<p style='margin: 4px 0;'><b>Phòng:</b> " + cacPhong + "</p>" +
                        "<p style='margin: 4px 0;'><b>Tổng sức chứa:</b> " + tongSucChua + " người</p>" +
                        "<p style='margin: 4px 0; color: red;'><b>Tổng giá:</b> " + currencyFormatter.format(tongGia) + "/đêm</p>" +
                        "</body></html>",
                cacPhong, tongSucChua, currencyFormatter.format(tongGia) // Java 11+ không cần 3 tham số cuối này
        );
        JLabel lblInfo = new JLabel(infoHtml);
        panel.add(lblInfo, BorderLayout.CENTER);


        // 2. Các nút hành động (Bên phải)
        JPanel pnlActions = new JPanel();
        pnlActions.setLayout(new BoxLayout(pnlActions, BoxLayout.Y_AXIS));
        pnlActions.setOpaque(false); // Trong suốt

        JButton btnXemChiTiet = new JButton("Xem chi tiết");
        JButton btnDatPhong = new JButton("  Đặt phương án này  ");
        btnDatPhong.setBackground(new Color(0, 122, 204));
        btnDatPhong.setForeground(Color.WHITE);
        btnDatPhong.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Căn lề 2 nút
        btnXemChiTiet.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDatPhong.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        pnlActions.add(btnDatPhong);
        pnlActions.add(Box.createVerticalStrut(10));
        pnlActions.add(btnXemChiTiet);
        
        panel.add(pnlActions, BorderLayout.EAST);

        // 3. Xử lý sự kiện cho các nút
        
        // Nút Xem Chi Tiết
        btnXemChiTiet.addActionListener(e -> {
            StringBuilder details = new StringBuilder("Chi tiết các phòng trong phương án:\n\n");
            for (Phong p : phuongAn) {
                details.append(String.format("- Phòng %s (Loại: %s, Sức chứa: %d, Giá: %s)\n",
                        p.getMaPhong(),
                        p.getLoaiPhong() != null ? p.getLoaiPhong().toString() : "N/A",
                        p.getSucChua(),
                        currencyFormatter.format(p.getGiaPhong())
                ));
            }
            JOptionPane.showMessageDialog(this, details.toString(), "Chi tiết phương án", JOptionPane.INFORMATION_MESSAGE);
        });

        // <<< THAY THẾ TOÀN BỘ LOGIC NÚT ĐẶT PHÒNG >>>
        btnDatPhong.addActionListener(e -> {
            // 1. Lấy danh sách mã phòng (String) từ List<Phong>
            List<String> listMaPhong = phuongAn.stream()
                                               .map(Phong::getMaPhong)
                                               .collect(Collectors.toList());
            
            // 2. Kiểm tra 'owner'
            // Constructor của DatPhong_Gui yêu cầu một ChonPhong_Gui
            ChonPhong_Gui parentFrame = null;
            if (owner instanceof ChonPhong_Gui) {
                parentFrame = (ChonPhong_Gui) owner;
            } else {
                // Xử lý lỗi nếu owner không đúng (hiếm khi xảy ra nếu luồng đúng)
                 JOptionPane.showMessageDialog(this, 
                    "Lỗi nghiêm trọng: Frame cha không phải là 'ChonPhong_Gui'.\nKhông thể mở phiếu đặt phòng.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Đóng dialog này
            dispose(); 
            
            // 4. Mở DatPhong_Gui với đầy đủ thông tin
            // (checkInDate và checkOutDate đã có sẵn trong class này)
            new DatPhong_Gui(listMaPhong, nhanVien, parentFrame, checkInDate, checkOutDate).setVisible(true);
        });
        // <<< KẾT THÚC THAY THẾ >>>

        return panel;
    }
}