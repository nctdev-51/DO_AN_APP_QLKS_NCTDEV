package gui;

// Import các thư viện Swing và AWT
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

/**
 * Lớp này tạo giao diện Hủy phòng / Thanh toán.
 * *** ĐÃ REFACTOR ĐỂ GIỐNG PHONG CÁCH CỦA QuanLyPhong.java ***
 */
public class HuyPhong_Gui extends JFrame {

    private JTextField txtTenKhachHang;
    private JTextField txtSoDienThoai;
    private JTextField txtSoNguoi;
    private JSpinner spNgaySinh;

    private JTable tblChiTietPhong;
    private JTable tblChiTietDichVu;

    private JTextField txtTongTienPhong;
    private JTextField txtTongTienDichVu;
    private JTextField txtKhuyenMai;
    private JTextField txtPhuongThucThanhToan;
    private JTextField txtTongTien;

    private JButton btnInHoaDon;
    private JButton btnHuyBo;
    private JButton btnThoat;

    private final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private final Color COLOR_BG = new Color(245, 247, 250);

    public HuyPhong_Gui() {
        setTitle("Hủy phòng");
        setSize(1100, 800); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBackground(COLOR_BG);
        pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(pnlMain);

       
        JLabel lblTieuDe = new JLabel("HỦY PHÒNG / THANH TOÁN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(COLOR_PRIMARY);
        lblTieuDe.setBorder(new EmptyBorder(10, 0, 15, 0));
        pnlMain.add(lblTieuDe, BorderLayout.NORTH);


        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(COLOR_BG);

        
        pnlContent.add(createKhachHangPanel());

        
        pnlContent.add(createChiTietPhongTablePanel());

        
        pnlContent.add(createDichVuTablePanel());

        
        pnlContent.add(createTongTienPanel());

        
        JScrollPane scrollContent = new JScrollPane(pnlContent);
        scrollContent.setBorder(BorderFactory.createEmptyBorder()); // Bỏ viền của scroll pane
        scrollContent.setBackground(COLOR_BG);
        pnlMain.add(scrollContent, BorderLayout.CENTER);

        
        pnlMain.add(createActionsPanel(), BorderLayout.SOUTH);
    }

    
    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(COLOR_PRIMARY, 1, true),
                        "Thông tin khách hàng",
                        TitledBorder.LEADING, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 16),
                        COLOR_PRIMARY
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

  
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1;
        txtTenKhachHang = new JTextField(15);
        txtTenKhachHang.setEditable(false);
        panel.add(txtTenKhachHang, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Số ngày:"), gbc); 
        gbc.gridx = 3;
        txtSoNguoi = new JTextField(15);
        txtSoNguoi.setEditable(false);
        panel.add(txtSoNguoi, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        txtSoDienThoai = new JTextField(15);
        txtSoDienThoai.setEditable(false);
        panel.add(txtSoDienThoai, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 3;
        spNgaySinh = new JSpinner(new SpinnerDateModel());
        spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh, "dd/MM/yyyy"));
        spNgaySinh.setEnabled(false); 
        panel.add(spNgaySinh, gbc);

        return panel;
    }


    private JScrollPane createChiTietPhongTablePanel() {
        String[] columnNames = {"Ngày đặt", "Ngày trả", "Loại phòng", "Số ngày", "Giá phòng"};
        Object[][] data = {
                {"", "", "", "", "", ""}
        };

        tblChiTietPhong = new JTable(data, columnNames);
        
        tblChiTietPhong.setRowHeight(28);
        tblChiTietPhong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblChiTietPhong.setSelectionBackground(new Color(173, 216, 230));
        tblChiTietPhong.setGridColor(new Color(220, 220, 220));

        JTableHeader header = tblChiTietPhong.getTableHeader();
        header.setBackground(new Color(222, 235, 247)); 
        header.setForeground(COLOR_PRIMARY); 
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        
        
        JScrollPane scrollPane = new JScrollPane(tblChiTietPhong);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_PRIMARY, 1, true),
                "Chi tiết phòng",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                COLOR_PRIMARY
        ));
        scrollPane.setBackground(new Color(222, 235, 247));
        
       
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_BG);
        wrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        
        return scrollPane;
    }
  
    private JScrollPane createDichVuTablePanel() {
        String[] columnDV = {"Tên dịch vụ", "Số lượng", "Giá tiền"};
        Object[][] datadv = {
                {"", "", ""}
        };

        tblChiTietDichVu = new JTable(datadv, columnDV);
        
        
        tblChiTietDichVu.setRowHeight(28);
        tblChiTietDichVu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblChiTietDichVu.setSelectionBackground(new Color(173, 216, 230));
        tblChiTietDichVu.setGridColor(new Color(220, 220, 220));

       
        JTableHeader header = tblChiTietDichVu.getTableHeader();
        header.setBackground(new Color(222, 235, 247)); 
        header.setForeground(COLOR_PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        
        
        JScrollPane scrollPane = new JScrollPane(tblChiTietDichVu);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_PRIMARY, 1, true),
                "Chi tiết dịch vụ",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                COLOR_PRIMARY
        ));
        scrollPane.setBackground(new Color(222, 235, 247)); // Nền viền tiêu đề
        
        return scrollPane;
    }
    
 JPanel createTongTienPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(COLOR_PRIMARY, 1, true),
                        "Tổng thanh toán",
                        TitledBorder.LEADING, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 16),
                        COLOR_PRIMARY
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ==== Hàng 1: Tổng tiền phòng ====
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; // Căn phải label
        panel.add(new JLabel("Tổng tiền phòng:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Cho text field co giãn
        txtTongTienPhong = new JTextField(15);
        txtTongTienPhong.setEditable(false);
        panel.add(txtTongTienPhong, gbc);

        // ==== Hàng 2: Tổng tiền dịch vụ ====
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tổng tiền dịch vụ:"), gbc);
        
        gbc.gridx = 1;
        txtTongTienDichVu = new JTextField(15);
        txtTongTienDichVu.setEditable(false);
        panel.add(txtTongTienDichVu, gbc);
        
        // ==== Hàng 3: Khuyến mãi ====
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Khuyến mãi:"), gbc);
        
        gbc.gridx = 1;
        txtKhuyenMai = new JTextField(15);
        txtKhuyenMai.setEditable(false);
        panel.add(txtKhuyenMai, gbc);
        
        // ==== Hàng 4: Phương thức thanh toán ====
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Phương thức thanh toán:"), gbc);
        
        gbc.gridx = 1;
        txtPhuongThucThanhToan = new JTextField(15);
        txtPhuongThucThanhToan.setEditable(false);
        panel.add(txtPhuongThucThanhToan, gbc);

        // ==== Hàng 5: TỔNG TIỀN ====
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblTongTien = new JLabel("Tổng tiền:");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(lblTongTien, gbc);
        
        gbc.gridx = 1;
        txtTongTien = new JTextField(15);
        txtTongTien.setEditable(false);
        txtTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtTongTien.setForeground(Color.RED);
        panel.add(txtTongTien, gbc);

        return panel;
    }

   
    private JPanel createActionsPanel() {
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlActions.setBackground(Color.WHITE); // Nền trắng
        pnlActions.setBorder(new LineBorder(new Color(230, 230, 230), 1)); 
        btnInHoaDon = taoButton("🖨️ In phiếu", new Color(52, 152, 219)); 
        btnHuyBo = taoButton("Hủy bỏ", new Color(241, 196, 15));   
        btnThoat = taoButton("Thoát", new Color(231, 76, 60)); 

        btnThoat.addActionListener(e -> dispose());

        pnlActions.add(btnInHoaDon);
        pnlActions.add(btnHuyBo);
        pnlActions.add(btnThoat);

        return pnlActions;
    }


    private JButton taoButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 35)); 
        btn.setBorder(new LineBorder(bg.darker(), 1, true));
        btn.setOpaque(true); 

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

    
        return btn;
    }

    public static void main(String[] args) {
    
        
        SwingUtilities.invokeLater(() -> {
            new HuyPhong_Gui().setVisible(true);
        });
    }
}
