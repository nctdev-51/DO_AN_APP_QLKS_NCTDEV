package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.*;

import dao.DangNhap_DAO;
// <<< THÊM IMPORT NÀY >>>
import dao.NhanVien_DAO; 
import entity.NhanVien;
import entity.TaiKhoan;

public class DangNhap_Gui extends JFrame {
    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JLabel lblTaiKhoan, lblMatKhau, lblThongBao;
    private JCheckBox chkHienMatKhau;
    private DangNhap_DAO dangNhapDAO;
    
    // <<< THÊM MỚI: Cần NhanVien_DAO để lấy thông tin NV từ TaiKhoan >>>
    private NhanVien_DAO nhanVienDAO;

    public DangNhap_Gui() {
        dangNhapDAO = new DangNhap_DAO();
        nhanVienDAO = new NhanVien_DAO(); // <<< KHỞI TẠO DAO >>>
        initializeUI();
        setupEventListeners();
    }

    private void initializeUI() {
        setTitle("Đăng nhập hệ thống - Khách sạn TATP");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tải ảnh nền, nếu không thấy sẽ dùng nền màu
        ImageIcon anhNen = new ImageIcon("data/images/boathouse-9871057_1920.jpg");
        Image img;
        if (anhNen.getImageLoadStatus() == MediaTracker.COMPLETE) {
             img = anhNen.getImage().getScaledInstance(900, 650, Image.SCALE_SMOOTH);
        } else {
            System.err.println("Không tìm thấy ảnh nền, sử dụng nền mặc định.");
            img = createDefaultBackground();
        }
        
        JLabel lblBackground = new JLabel(new ImageIcon(img));
        lblBackground.setLayout(new GridBagLayout());

        JPanel pnlGiaoDien = new JPanel();
        pnlGiaoDien.setOpaque(false);
        pnlGiaoDien.setLayout(new BoxLayout(pnlGiaoDien, BoxLayout.Y_AXIS));
        pnlGiaoDien.setPreferredSize(new Dimension(380, 400));

        Font fontChinh = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 18);
        Color mauChu = new Color(45, 45, 45);

        JLabel lblTieuDe = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTieuDe.setForeground(new Color(0, 82, 164));
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTaiKhoan = new JLabel("Tài khoản");
        lblTaiKhoan.setFont(fontLabel);
        lblTaiKhoan.setForeground(mauChu);
        lblTaiKhoan.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTaiKhoan.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        txtTaiKhoan = new JTextField();
        txtTaiKhoan.setFont(fontChinh);
        txtTaiKhoan.setMaximumSize(new Dimension(380, 45));
        txtTaiKhoan.setBorder(new CompoundBorder(
            new LineBorder(new Color(150, 150, 150), 1, true),
            new EmptyBorder(8, 15, 8, 15)
        ));

        lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(fontLabel);
        lblMatKhau.setForeground(mauChu);
        lblMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMatKhau.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        txtMatKhau = new JPasswordField();
        txtMatKhau.setFont(fontChinh);
        txtMatKhau.setMaximumSize(new Dimension(380, 45));
        txtMatKhau.setBorder(new CompoundBorder(
            new LineBorder(new Color(150, 150, 150), 1, true),
            new EmptyBorder(8, 15, 8, 15)
        ));

        chkHienMatKhau = new JCheckBox("Hiện mật khẩu");
        chkHienMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkHienMatKhau.setOpaque(false);
        chkHienMatKhau.setForeground(mauChu);
        chkHienMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblThongBao = new JLabel(" "); // <<< Thêm khoảng trắng để giữ chỗ
        lblThongBao.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblThongBao.setForeground(Color.RED);
        lblThongBao.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblThongBao.setPreferredSize(new Dimension(380, 40)); // <<< Đặt chiều cao
        lblThongBao.setHorizontalAlignment(SwingConstants.CENTER);

        btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(new Color(0, 102, 204));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDangNhap.setMaximumSize(new Dimension(380, 55));
        btnDangNhap.setBorder(new LineBorder(new Color(0, 102, 204), 2, true));

        btnDangNhap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnDangNhap.setBackground(new Color(0, 80, 160));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnDangNhap.setBackground(new Color(0, 102, 204));
            }
        });

        // Ghép layout
        pnlGiaoDien.add(Box.createVerticalStrut(20));
        pnlGiaoDien.add(lblTieuDe);
        pnlGiaoDien.add(Box.createVerticalStrut(30));
        pnlGiaoDien.add(lblTaiKhoan);
        pnlGiaoDien.add(Box.createVerticalStrut(5));
        pnlGiaoDien.add(txtTaiKhoan);
        pnlGiaoDien.add(Box.createVerticalStrut(20));
        pnlGiaoDien.add(lblMatKhau);
        pnlGiaoDien.add(Box.createVerticalStrut(5));
        pnlGiaoDien.add(txtMatKhau);
        pnlGiaoDien.add(Box.createVerticalStrut(8));
        pnlGiaoDien.add(chkHienMatKhau);
        pnlGiaoDien.add(Box.createVerticalStrut(12));
        pnlGiaoDien.add(lblThongBao);
        pnlGiaoDien.add(Box.createVerticalStrut(20));
        pnlGiaoDien.add(btnDangNhap);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 380, 0, 80);
        lblBackground.add(pnlGiaoDien, gbc);

        add(lblBackground);
        setVisible(true);
    }


    private void setupEventListeners() {
        btnDangNhap.addActionListener(e -> thucHienDangNhap());

        ActionListener enterListener = e -> thucHienDangNhap();

        txtTaiKhoan.addActionListener(enterListener);
        txtMatKhau.addActionListener(enterListener);

        KeyAdapter clearErrorListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // <<< CẬP NHẬT: Reset lại style thông báo >>>
                lblThongBao.setText(" ");
                lblThongBao.setOpaque(false);
                lblThongBao.setBorder(null);
            }
        };

        txtTaiKhoan.addKeyListener(clearErrorListener);
        txtMatKhau.addKeyListener(clearErrorListener);

        chkHienMatKhau.addActionListener(e -> {
            if (chkHienMatKhau.isSelected()) {
                txtMatKhau.setEchoChar((char) 0); 
            } else {
                txtMatKhau.setEchoChar('•'); 
            }
        });
    }

    private void thucHienDangNhap() {
        String taiKhoan = txtTaiKhoan.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
            hienThiLoi("Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        try {
            TaiKhoan tk = dangNhapDAO.ktDangNhap(taiKhoan, matKhau);

            if (tk != null) {
                // <<< CẬP NHẬT: Lấy đối tượng NhanVien đầy đủ >>>
                NhanVien nv = nhanVienDAO.getNhanVienByMa(tk.getNhanVien().getMaNhanVien());
                
                if(nv == null) {
                     hienThiLoi("Lỗi: Không tìm thấy thông tin nhân viên ứng với tài khoản.");
                     return;
                }
                
                if(!nv.isTrangThai()) {
                     hienThiLoi("Lỗi: Tài khoản này đã bị vô hiệu hóa (nhân viên đã nghỉ).");
                     return;
                }

                // Hiển thị thông báo thành công
                hienThiThanhCong("Đăng nhập thành công! Chào mừng " + nv.getHoTen());

                // Timer để người dùng đọc thông báo
                Timer timer = new Timer(1500, e -> {
                    dispose(); // Đóng cửa sổ đăng nhập
                    
                    // <<< THAY ĐỔI QUAN TRỌNG >>>
                    // 1. Khởi tạo TrangChu_Gui bằng đối tượng NhanVien
                    TrangChu_Gui trangChu = new TrangChu_Gui(nv);
                    trangChu.setVisible(true);
                    
                    // 2. Yêu cầu TrangChu_Gui mở dialog Nhận Ca
                    trangChu.moDialogNhanCaTuDong();
                });
                timer.setRepeats(false);
                timer.start();
                
            } else {
                hienThiLoi("Tài khoản hoặc mật khẩu không đúng!");
                txtMatKhau.setText("");
                txtMatKhau.requestFocus();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            hienThiLoi("⚠ Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    // <<< HÀM MỚI: Tách biệt logic hiển thị lỗi >>>
    private void hienThiLoi(String message) {
        lblThongBao.setOpaque(true); 
        lblThongBao.setBackground(new Color(220, 53, 69, 180)); 
        lblThongBao.setForeground(Color.WHITE); 
        lblThongBao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblThongBao.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        lblThongBao.setText(message);
    }
    
    // <<< HÀM MỚI: Tách biệt logic hiển thị thành công >>>
    private void hienThiThanhCong(String message) {
        lblThongBao.setOpaque(true); 
        lblThongBao.setBackground(new Color(25, 135, 84, 180)); // Màu xanh lá
        lblThongBao.setForeground(Color.WHITE); 
        lblThongBao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblThongBao.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        lblThongBao.setText(message);
    }


    // <<< XÓA HÀM: Hàm moManHinhChinh không còn được sử dụng >>>
    // private void moManHinhChinh(TaiKhoan taiKhoan) { ... }

    private Image createDefaultBackground() {
        BufferedImage image = new BufferedImage(900, 650, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        GradientPaint gradient = new GradientPaint(0, 0, new Color(220, 230, 245), 
                                                 900, 650, new Color(180, 200, 230));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 900, 650);
        
        g2d.dispose();
        return image;
    }

    public static void main(String[] args) {
        
        // <<< THÊM MỚI: Kết nối CSDL ngay khi chạy >>>
        try {
            connectDB.ConnectDB.getInstance();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Không thể kết nối CSDL! Vui lòng kiểm tra lại.\n" + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Thoát nếu không kết nối được
        }
        
        try {
          UIManager.setLookAndFeel( new com.formdev.flatlaf.FlatLightLaf() );
        } catch( Exception ex ) {
          System.err.println( "Failed to initialize LaF" );
        }
        
        SwingUtilities.invokeLater(() -> new DangNhap_Gui());
    }
}