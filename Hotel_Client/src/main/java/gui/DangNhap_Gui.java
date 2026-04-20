package gui;

import client.network.SocketClient;
import client.service.AuthRemoteService;
import client.service.AuthRemoteServiceImpl;
import core.entity.NhanVien;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class DangNhap_Gui extends JFrame {
    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JLabel lblThongBao;
    private JCheckBox chkHienMatKhau;
    private final AuthRemoteService authRemoteService;

    public DangNhap_Gui() {
        authRemoteService = new AuthRemoteServiceImpl(new SocketClient("127.0.0.1", 9999, 3000, 5000));
        initializeUI();
        setupEventListeners();
    }

    private void initializeUI() {
        setTitle("Dang nhap he thong - Khach san TATP");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ImageIcon anhNen = new ImageIcon("data/images/boathouse-9871057_1920.jpg");
        Image img = (anhNen.getImageLoadStatus() == MediaTracker.COMPLETE)
                ? anhNen.getImage().getScaledInstance(900, 650, Image.SCALE_SMOOTH)
                : createDefaultBackground();

        JLabel lblBackground = new JLabel(new ImageIcon(img));
        lblBackground.setLayout(new GridBagLayout());

        JPanel pnl = new JPanel();
        pnl.setOpaque(false);
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setPreferredSize(new Dimension(380, 400));

        JLabel lblTieuDe = new JLabel("DANG NHAP HE THONG");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTieuDe.setForeground(new Color(0, 82, 164));
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtTaiKhoanLocal = new JTextField();
        txtTaiKhoanLocal.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTaiKhoanLocal.setMaximumSize(new Dimension(380, 45));
        txtTaiKhoanLocal.setBorder(new CompoundBorder(
                new LineBorder(new Color(150, 150, 150), 1, true),
                new EmptyBorder(8, 15, 8, 15)
        ));
        txtTaiKhoan = txtTaiKhoanLocal;

        JPasswordField txtMatKhauLocal = new JPasswordField();
        txtMatKhauLocal.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtMatKhauLocal.setMaximumSize(new Dimension(380, 45));
        txtMatKhauLocal.setBorder(new CompoundBorder(
                new LineBorder(new Color(150, 150, 150), 1, true),
                new EmptyBorder(8, 15, 8, 15)
        ));
        txtMatKhau = txtMatKhauLocal;

        chkHienMatKhau = new JCheckBox("Hien mat khau");
        chkHienMatKhau.setOpaque(false);
        chkHienMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblThongBao = new JLabel(" ");
        lblThongBao.setHorizontalAlignment(SwingConstants.CENTER);
        lblThongBao.setPreferredSize(new Dimension(380, 40));
        lblThongBao.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnDangNhap = new JButton("DANG NHAP");
        btnDangNhap.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(new Color(0, 102, 204));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDangNhap.setMaximumSize(new Dimension(380, 55));

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

        pnl.add(Box.createVerticalStrut(20));
        pnl.add(lblTieuDe);
        pnl.add(Box.createVerticalStrut(30));
        pnl.add(txtTaiKhoan);
        pnl.add(Box.createVerticalStrut(20));
        pnl.add(txtMatKhau);
        pnl.add(Box.createVerticalStrut(8));
        pnl.add(chkHienMatKhau);
        pnl.add(Box.createVerticalStrut(12));
        pnl.add(lblThongBao);
        pnl.add(Box.createVerticalStrut(20));
        pnl.add(btnDangNhap);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 380, 0, 80);
        lblBackground.add(pnl, gbc);

        add(lblBackground);
    }

    private void setupEventListeners() {
        btnDangNhap.addActionListener(e -> thucHienDangNhap());
        ActionListener enterListener = e -> thucHienDangNhap();
        txtTaiKhoan.addActionListener(enterListener);
        txtMatKhau.addActionListener(enterListener);

        KeyAdapter clearError = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                lblThongBao.setText(" ");
                lblThongBao.setOpaque(false);
                lblThongBao.setBorder(null);
            }
        };
        txtTaiKhoan.addKeyListener(clearError);
        txtMatKhau.addKeyListener(clearError);

        chkHienMatKhau.addActionListener(e -> txtMatKhau.setEchoChar(chkHienMatKhau.isSelected() ? (char) 0 : '•'));
    }

    private void thucHienDangNhap() {
        String taiKhoan = txtTaiKhoan.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
            hienThiLoi("Vui long nhap day du tai khoan va mat khau!");
            return;
        }

        try {
            // Business validation belongs to server; GUI only sends request and renders response.
            NhanVien nv = authRemoteService.login(taiKhoan, matKhau);
            hienThiThanhCong("Dang nhap thanh cong! Chao mung " + nv.getHoTen());
            Timer timer = new Timer(1200, e -> {
                dispose();
                TrangChu_Gui trangChu = new TrangChu_Gui(nv);
                trangChu.setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        } catch (Exception ex) {
            hienThiLoi(ex.getMessage() == null ? "Dang nhap that bai!" : ex.getMessage());
            txtMatKhau.setText("");
            txtMatKhau.requestFocus();
        }
    }

    private void hienThiLoi(String message) {
        lblThongBao.setOpaque(true);
        lblThongBao.setBackground(new Color(220, 53, 69, 180));
        lblThongBao.setForeground(Color.WHITE);
        lblThongBao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblThongBao.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        lblThongBao.setText(message);
    }

    private void hienThiThanhCong(String message) {
        lblThongBao.setOpaque(true);
        lblThongBao.setBackground(new Color(25, 135, 84, 180));
        lblThongBao.setForeground(Color.WHITE);
        lblThongBao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblThongBao.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        lblThongBao.setText(message);
    }

    private Image createDefaultBackground() {
        BufferedImage image = new BufferedImage(900, 650, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        GradientPaint gradient = new GradientPaint(0, 0, new Color(220, 230, 245), 900, 650, new Color(180, 200, 230));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 900, 650);
        g2d.dispose();
        return image;
    }
}
