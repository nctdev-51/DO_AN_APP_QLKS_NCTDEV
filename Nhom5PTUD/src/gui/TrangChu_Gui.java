package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.NumberFormat;
// <<< THÊM MỚI 1: Import cho đồng hồ >>>
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // <<< THÊM MỚI
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.event.*;
import java.util.stream.*;
import java.util.Arrays; // <<< THÊM MỚI
import java.util.Comparator;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import dao.PhieuDatPhong_DAO; // <<< THÊM MỚI
import dao.Phong_DAO;
import entity.NhanVien;
import entity.PhieuDatPhong; // <<< THÊM MỚI
import entity.Phong;
// <<< THÊM MỚI (1): Import panel mới >>>
import gui.QuanLyPhieuDatPhong_Gui;

// <<< THÊM MỚI: Import cho Dialog Card >>>
import javax.swing.border.MatteBorder;

// <<< THÊM MỚI: Import cho Dialog Thêm Dịch Vụ >>>
import gui.ThemDichVu_Dialog;


public class TrangChu_Gui extends JFrame {

    private JButton btnDashboard, btnTrangChinh, btnPhong, btnThongKe, btnKhuyenMai, btnDichVu, btnKhachHang, btnNhanVien, btnDangXuat;
    private JPanel pnlPhong, pnlThongKe, pnlKhuyenMai, pnlDichVu, pnlKhachHang, pnlNhanVien;
    private JPanel pCenter;
    private CardLayout cardLayout;

    // <<< THAY ĐỔI: Chuyển lên làm biến instance để inner class truy cập >>>
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    // <<< THÊM MỚI: Khai báo DAO ở đây để Dialog có thể dùng >>>
    private Phong_DAO phongDAO;

    // <<< THÊM MỚI: Khai báo DAO cho phiếu đặt phòng >>>
    private PhieuDatPhong_DAO pdpDAO;

    public NhanVien nhanVien;

    private JLabel lblChaoMung;
    private JPanel pnlCaLamViec;
    private JButton btnCaLamViec;
    private JButton btnPhanCongCa;

    private QuanLyCaLamViec quanLyCaLamViecPanel;
    private QuanLyPhanCongCa_Gui quanLyPhanCongCaPanel;

    // <<< THÊM MỚI (2): Khai báo biến cho panel mới >>>
    private QuanLyPhieuDatPhong_Gui quanLyPhieuDatPhongPanel;

    // <<< THÊM MỚI 2: Biến cho đồng hồ >>>
    private JLabel lblDongHo;
    private Timer timerDongHo;
    private QuanLyGoiDichVu_Gui quanLyGoiDichVuPanel;

    private java.util.List<JButton> getButtonsFromPanel(JPanel panel) {
        java.util.List<JButton> list = new java.util.ArrayList<>();
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton) {
                list.add((JButton) c);
            }
        }
        return list;
    }

    public TrangChu_Gui(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        this.phongDAO = new Phong_DAO(); // <<< THÊM MỚI: Khởi tạo DAO
        this.pdpDAO = new PhieuDatPhong_DAO(); // <<< THÊM MỚI
        initializeUI();
        startClock(); // <<< THÊM MỚI 3: Khởi động đồng hồ >>>
    }

    public TrangChu_Gui() {
        ConnectDB.getInstance();
        NhanVien nvTest = new NhanVien_DAO().getNhanVienByMa("NV002");
        if (nvTest == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy NV002 (Test) trong CSDL!");
            nvTest = new NhanVien();
            nvTest.setHoTen("Nhân viên Test");
        }
        this.nhanVien = nvTest;
        this.phongDAO = new Phong_DAO(); // <<< THÊM MỚI: Khởi tạo DAO
        this.pdpDAO = new PhieuDatPhong_DAO(); // <<< THÊM MỚI
        initializeUI();
        startClock(); // <<< THÊM MỚI 3: Khởi động đồng hồ >>>
    }

    private void initializeUI() {
        setTitle("Quản lý khách sạn TATP");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // ... (Code phần Header và Menu giữ nguyên) ...
        // ---------- Header ----------
        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        pnlTieuDe.setBackground(new Color(33, 105, 170));
        pnlTieuDe.setPreferredSize(new Dimension(0, 72));
        pnlTieuDe.setBorder(new EmptyBorder(6, 12, 6, 12));

        JPanel pnlHeaderLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pnlHeaderLeft.setOpaque(false);

        lblDongHo = new JLabel("<html><center>00:00:00<br>00/00/0000</center></html>");
        lblDongHo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDongHo.setForeground(Color.WHITE);
        lblDongHo.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE));
        lblDongHo.setPreferredSize(new Dimension(120, 60));
        lblDongHo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHÁCH SẠN TATP");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(Color.WHITE);

        pnlHeaderLeft.add(lblDongHo);
        pnlHeaderLeft.add(lblTieuDe);

        JPanel pnlChaoMung = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlChaoMung.setOpaque(false);

        JButton btnNhanCaDialog = new JButton("Nhận/Bàn Giao Ca");
        btnNhanCaDialog.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNhanCaDialog.setBackground(new Color(255, 170, 0));
        btnNhanCaDialog.setForeground(Color.BLACK);
        btnNhanCaDialog.setFocusPainted(false);
        btnNhanCaDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon iconAvatar = new ImageIcon(
                new ImageIcon("data/images/pngtree-default-avatar-image_2235111.jpg")
                        .getImage()
                        .getScaledInstance(42, 42, Image.SCALE_SMOOTH)
        );
        JLabel lblAvatar = new JLabel(iconAvatar);
        lblAvatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));

        lblChaoMung = new JLabel("Xin chào: " + (nhanVien != null ? nhanVien.getHoTen() : "Nhân viên"));
        lblChaoMung.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChaoMung.setForeground(Color.WHITE);

        pnlChaoMung.add(btnNhanCaDialog);
        pnlChaoMung.add(lblChaoMung);
        pnlChaoMung.add(lblAvatar);

        pnlTieuDe.add(pnlHeaderLeft, BorderLayout.WEST);
        pnlTieuDe.add(pnlChaoMung, BorderLayout.EAST);

        add(pnlTieuDe, BorderLayout.NORTH);

        // ---------- Left menu (Giữ nguyên) ----------
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setBackground(new Color(20, 60, 100));
        pnlMenu.setPreferredSize(new Dimension(240, 0));
        pnlMenu.setBorder(new EmptyBorder(16, 0, 16, 0));

        JLabel lblMenuTitle = new JLabel("MENU");
        lblMenuTitle.setOpaque(false);
        lblMenuTitle.setForeground(Color.WHITE);
        lblMenuTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMenuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMenuTitle.setBorder(new EmptyBorder(0, 20, 0, 0));
        pnlMenu.add(lblMenuTitle);
        pnlMenu.add(Box.createVerticalStrut(12));

        btnDashboard = new JButton(" Dashboard");
        btnDashboard.setIcon(getIcon("mushroom_4702012", 30, 30));

        btnTrangChinh = new JButton(" Trang chủ");
        btnTrangChinh.setIcon(getIcon("google-symbol_2875331", 30, 30));

        btnPhong = new JButton(" Phòng");
        btnPhong.setIcon(getIcon("newspaper_4410328", 30, 30));

        btnThongKe = new JButton(" Thống kê");
        btnThongKe.setIcon(getIcon("oil_8084515", 30, 30));

        btnKhuyenMai = new JButton(" Khuyến mãi");
        btnKhuyenMai.setIcon(getIcon("yay_3770755", 30, 30));


        btnDichVu = new JButton(" Dịch vụ");
        btnDichVu.setIcon(getIcon("admin_2875381", 30, 30));

        btnKhachHang = new JButton(" Khách hàng");
        btnKhachHang.setIcon(getIcon("halloween-party_2682288", 30, 30));

        btnNhanVien = new JButton(" Nhân viên");
        btnNhanVien.setIcon(getIcon("customer-support_1195620", 30, 30));

        btnDangXuat = new JButton("Đăng xuất");

        btnCaLamViec = new JButton(" Ca làm việc");
        btnCaLamViec.setIcon(getIcon("idea_1196033", 30, 30));

        btnPhanCongCa = new JButton(" Phân công ca");
        btnPhanCongCa.setIcon(getIcon("overdue_6755655", 30, 30));

        JButton[] buttons = {
                btnDashboard, btnTrangChinh, btnPhong, btnThongKe,
                btnKhuyenMai, btnDichVu, btnKhachHang, btnNhanVien, btnPhanCongCa,btnCaLamViec
        };

        pnlPhong = taoSubMenu(new String[]{"Đặt phòng", "Quản lý phiếu đặt", "Gọi dịch vụ", "Quản lý phòng"});
        pnlKhuyenMai = taoSubMenu(new String[]{"Quản lý khuyến mãi", "Tìm kiếm khuyến mãi"});
        pnlDichVu = taoSubMenu(new String[]{"Quản lý dịch vụ", "Tìm kiếm dịch vụ"});
        pnlKhachHang = taoSubMenu(new String[]{"Quản lý khách hàng", "Tìm kiếm khách hàng"});
        pnlNhanVien = taoSubMenu(new String[]{"Quản lý nhân viên", "Tìm kiếm nhân viên"});
        pnlCaLamViec = taoSubMenu(new String[]{"Quản lý ca làm việc", "Lịch sử ca làm việc"});
        quanLyGoiDichVuPanel = new QuanLyGoiDichVu_Gui(this.nhanVien);

        btnPhong.addActionListener(e -> toggleSubMenu(pnlPhong));
        btnKhuyenMai.addActionListener(e -> toggleSubMenu(pnlKhuyenMai));
        btnDichVu.addActionListener(e -> toggleSubMenu(pnlDichVu));
        btnKhachHang.addActionListener(e -> toggleSubMenu(pnlKhachHang));
        btnNhanVien.addActionListener(e -> toggleSubMenu(pnlNhanVien));
        btnCaLamViec.addActionListener(e -> toggleSubMenu(pnlCaLamViec));
        btnPhanCongCa.addActionListener(e -> cardLayout.show(pCenter, "QuanLyPhanCong"));

        for (JButton btn : buttons) {
            styleMenuButton(btn);
            pnlMenu.add(btn);

            if (btn == btnPhong) pnlMenu.add(pnlPhong);
            if (btn == btnKhuyenMai) pnlMenu.add(pnlKhuyenMai);
            if (btn == btnDichVu) pnlMenu.add(pnlDichVu);
            if (btn == btnKhachHang) pnlMenu.add(pnlKhachHang);
            if (btn == btnNhanVien) pnlMenu.add(pnlNhanVien);
            if (btn == btnCaLamViec) pnlMenu.add(pnlCaLamViec);

            pnlMenu.add(Box.createVerticalStrut(8));
        }

        pnlMenu.add(Box.createVerticalGlue());
        styleLogoutButton(btnDangXuat);

        int menuWidth = pnlMenu.getPreferredSize().width;
        int fixedHeight = 60;

        btnDangXuat.setPreferredSize(new Dimension(menuWidth, fixedHeight));
        btnDangXuat.setMaximumSize(new Dimension(Integer.MAX_VALUE, fixedHeight));

        pnlMenu.add(btnDangXuat);

        add(pnlMenu, BorderLayout.WEST);

        // ---------- Center area (CardLayout) ----------
        cardLayout = new CardLayout();
        pCenter = new JPanel(cardLayout);
        pCenter.setBackground(Color.WHITE);

        JPanel trangChuPanel = taoTrangChuPanel();
        QuanLyPhong quanLyPhongPanel = new QuanLyPhong();
        QuanLyDichVu quanLyDichVuPanel = new QuanLyDichVu();
        QuanLyKhuyenMai quanLyKhuyenMaiPanel = new QuanLyKhuyenMai();
        QuanLyKhachHang quanLyKhachHangPanel = new QuanLyKhachHang();

        quanLyPhieuDatPhongPanel = new QuanLyPhieuDatPhong_Gui();


        pCenter.add(trangChuPanel, "TrangChu");
        pCenter.add(quanLyPhongPanel, "QuanLyPhong");
        pCenter.add(quanLyDichVuPanel, "QuanLyDichVu");
        pCenter.add(quanLyKhuyenMaiPanel, "QuanLyKhuyenMai");
        pCenter.add(quanLyKhachHangPanel, "QuanLyKhachHang");
        pCenter.add(new QuanLyNhanVien(), "QuanLyNhanVien");
        pCenter.add(new LichSuCaLamViec_Gui(), "LichSuCaLamViec");

        pCenter.add(quanLyPhieuDatPhongPanel, "QuanLyPhieuDatPhong");

        pCenter.add(quanLyGoiDichVuPanel, "QuanLyGoiDichVu");
        pCenter.add(new QuanLyThongKeDoanhThu(), "ThongKeDoanhThu");
        quanLyCaLamViecPanel = new QuanLyCaLamViec();
        quanLyPhanCongCaPanel = new QuanLyPhanCongCa_Gui();
        quanLyCaLamViecPanel.setPhanCongPanel(quanLyPhanCongCaPanel);
        pCenter.add(quanLyCaLamViecPanel, "QuanLyCaLamViec");
        pCenter.add(quanLyPhanCongCaPanel, "QuanLyPhanCong");

        add(pCenter, BorderLayout.CENTER);

        // ---------- Submenu actions ----------
        java.util.List<JButton> buttonsPhong = getButtonsFromPanel(pnlPhong);

        // <<< BẮT ĐẦU SỬA: Cập nhật logic cho 4 nút menu Phòng >>>
        if (buttonsPhong.size() >= 4) { // Kiểm tra >= 4
            JButton btnDatPhong = buttonsPhong.get(0);
            JButton btnQLPhieuDat = buttonsPhong.get(1);
            JButton btnGoiDV = buttonsPhong.get(2); // Nút "Gọi dịch vụ"
            JButton btnQLPhong = buttonsPhong.get(3); // Nút "Quản lý phòng"

            btnDatPhong.addActionListener(e -> new ChonPhong_Gui(this.nhanVien).setVisible(true));
            btnQLPhieuDat.addActionListener(e -> cardLayout.show(pCenter, "QuanLyPhieuDatPhong"));
            btnGoiDV.addActionListener(e -> cardLayout.show(pCenter, "QuanLyGoiDichVu")); // Thêm sự kiện
            btnQLPhong.addActionListener(e -> cardLayout.show(pCenter, "QuanLyPhong"));
        }
        // <<< KẾT THÚC SỬA >>>

        java.util.List<JButton> buttonsCaLamViec = getButtonsFromPanel(pnlCaLamViec);
        if (buttonsCaLamViec.size() >= 2) {
            buttonsCaLamViec.get(0).addActionListener(e -> cardLayout.show(pCenter, "QuanLyCaLamViec"));
            buttonsCaLamViec.get(1).addActionListener(e -> cardLayout.show(pCenter, "LichSuCaLamViec"));
        }

        if (pnlDichVu.getComponentCount() > 0 && pnlDichVu.getComponent(0) instanceof JButton)
            ((JButton) pnlDichVu.getComponent(0)).addActionListener(e -> cardLayout.show(pCenter, "QuanLyDichVu"));
        if (pnlKhuyenMai.getComponentCount() > 0 && pnlKhuyenMai.getComponent(0) instanceof JButton)
            ((JButton) pnlKhuyenMai.getComponent(0)).addActionListener(e -> cardLayout.show(pCenter, "QuanLyKhuyenMai"));
        if (pnlKhachHang.getComponentCount() > 0 && pnlKhachHang.getComponent(0) instanceof JButton)
            ((JButton) pnlKhachHang.getComponent(0)).addActionListener(e -> cardLayout.show(pCenter, "QuanLyKhachHang"));
        if (pnlNhanVien.getComponentCount() > 0 && pnlNhanVien.getComponent(0) instanceof JButton)
            ((JButton) pnlNhanVien.getComponent(0)).addActionListener(e -> cardLayout.show(pCenter, "QuanLyNhanVien"));

        btnTrangChinh.addActionListener(e -> cardLayout.show(pCenter, "TrangChu"));
        btnDashboard.addActionListener(e -> cardLayout.show(pCenter, "TrangChu"));
        btnThongKe.addActionListener(e -> cardLayout.show(pCenter, "ThongKeDoanhThu"));
        btnDangXuat.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if(timerDongHo != null) timerDongHo.stop();
                dispose();
                ConnectDB.getInstance();
                SwingUtilities.invokeLater(() -> new DangNhap_Gui());
            }
        });

        btnNhanCaDialog.addActionListener(e -> {
            if (nhanVien == null) {
                JOptionPane.showMessageDialog(this, "Không xác định được nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new NhanCaBanGiao_Dialog(this, this.nhanVien).setVisible(true);
        });
    }

    // ----- Hàm taoTrangChuPanel() và các hàm style (Giữ nguyên) -----

    private JPanel taoTrangChuPanel() {
        // ============================================================
        // PANEL GỐC: Nền xám nhạt, padding đều
        // ============================================================
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // ============================================================
        // SPLIT PANE: TRÁI (Biểu đồ) | PHẢI (Danh sách phòng)
        // ============================================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.42);
        splitPane.setDividerLocation(660);
        splitPane.setOneTouchExpandable(false);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);
        splitPane.setBackground(new Color(245, 247, 250));

        // ============================================================
        // BÊN TRÁI: BIỂU ĐỒ + STAT CARDS
        // ============================================================
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 12));
        pnlLeft.setBackground(new Color(245, 247, 250));
        pnlLeft.setBorder(new EmptyBorder(0, 0, 0, 6));

        // --- Tiêu đề panel trái ---
        JPanel pnlLeftHeader = new JPanel(new BorderLayout());
        pnlLeftHeader.setBackground(new Color(245, 247, 250));
        JLabel lblLeftTitle = new JLabel("📊  Thống kê tổng quan");
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblLeftTitle.setForeground(new Color(30, 60, 100));
        lblLeftTitle.setBorder(new EmptyBorder(0, 4, 8, 0));
        pnlLeftHeader.add(lblLeftTitle, BorderLayout.WEST);
        pnlLeft.add(pnlLeftHeader, BorderLayout.NORTH);

        // --- 3 Stat Cards ngang ---
        JPanel pnlStatCards = new JPanel(new GridLayout(1, 3, 10, 0));
        pnlStatCards.setOpaque(false);
        pnlStatCards.add(createQuickStatCard("Phòng trống", "28", new Color(22, 163, 74), "🏨"));
        pnlStatCards.add(createQuickStatCard("Đang sử dụng", "15", new Color(234, 88, 12), "🔑"));
        pnlStatCards.add(createQuickStatCard("Đã đặt trước", "7", new Color(220, 38, 38), "📋"));
        pnlLeft.add(pnlStatCards, BorderLayout.SOUTH);

        // --- Khu vực biểu đồ (placeholder) ---
        JPanel pnlChartArea = new JPanel(new BorderLayout());
        pnlChartArea.setBackground(Color.WHITE);
        pnlChartArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 224, 230), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Placeholder nội dung biểu đồ
        JPanel pnlChartPlaceholder = new JPanel(new GridBagLayout());
        pnlChartPlaceholder.setBackground(Color.WHITE);
        GridBagConstraints gbcPh = new GridBagConstraints();
        gbcPh.gridy = 0;
        JLabel iconChart = new JLabel("📈");
        iconChart.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        JLabel lblChartHint = new JLabel("Khu vực hiển thị biểu đồ");
        lblChartHint.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChartHint.setForeground(new Color(150, 160, 175));
        JLabel lblChartSub = new JLabel("(Tích hợp JFreeChart tại đây)");
        lblChartSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblChartSub.setForeground(new Color(190, 195, 205));

        JPanel colPh = new JPanel();
        colPh.setLayout(new BoxLayout(colPh, BoxLayout.Y_AXIS));
        colPh.setOpaque(false);
        colPh.add(Box.createVerticalGlue());
        iconChart.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblChartHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblChartSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        colPh.add(iconChart);
        colPh.add(Box.createVerticalStrut(10));
        colPh.add(lblChartHint);
        colPh.add(Box.createVerticalStrut(4));
        colPh.add(lblChartSub);
        colPh.add(Box.createVerticalGlue());
        pnlChartPlaceholder.add(colPh);

        pnlChartArea.add(pnlChartPlaceholder, BorderLayout.CENTER);

        // Label tiêu đề biểu đồ
        JLabel lblChartTitle = new JLabel("Tỷ lệ sử dụng phòng");
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChartTitle.setForeground(new Color(60, 80, 110));
        pnlChartArea.add(lblChartTitle, BorderLayout.NORTH);

        pnlLeft.add(pnlChartArea, BorderLayout.CENTER);

        // ============================================================
        // BÊN PHẢI: FILTER + DANH SÁCH PHÒNG
        // ============================================================
        JPanel pnlRight = new JPanel(new BorderLayout(0, 10));
        pnlRight.setBackground(new Color(245, 247, 250));
        pnlRight.setBorder(new EmptyBorder(0, 6, 0, 0));

        // --- Header bên phải ---
        JPanel pnlRightHeader = new JPanel(new BorderLayout());
        pnlRightHeader.setBackground(new Color(245, 247, 250));
        JLabel lblRightTitle = new JLabel("🏠  Danh sách phòng");
        lblRightTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblRightTitle.setForeground(new Color(30, 60, 100));
        lblRightTitle.setBorder(new EmptyBorder(0, 4, 0, 0));

        // Legend trạng thái phòng (inline, bên phải tiêu đề)
        JPanel pnlLegend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        pnlLegend.setOpaque(false);
        pnlLegend.add(trangThaiPhong(new Color(22, 163, 74), "Trống"));
        pnlLegend.add(trangThaiPhong(new Color(234, 88, 12), "Đang ở"));
        pnlLegend.add(trangThaiPhong(new Color(220, 38, 38), "Đã đặt"));
        pnlLegend.add(trangThaiPhong(new Color(100, 116, 139), "Bảo trì"));

        pnlRightHeader.add(lblRightTitle, BorderLayout.WEST);
        pnlRightHeader.add(pnlLegend, BorderLayout.EAST);
        pnlRightHeader.setBorder(new EmptyBorder(0, 0, 6, 0));
        pnlRight.add(pnlRightHeader, BorderLayout.NORTH);

        // --- TOP: Filter ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(new Color(245, 247, 250));

        JPanel pnlFilterContainer = new JPanel(new BorderLayout());
        pnlFilterContainer.setOpaque(false);

        // ==================== FILTER PANEL ====================
        JPanel pnlFilterTop = new JPanel(new GridBagLayout());
        pnlFilterTop.setBackground(Color.WHITE);
        pnlFilterTop.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 225, 235), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,6,4,6);
        gbc.gridy = 0; // Tất cả đều nằm trên 1 hàng y = 0
        gbc.fill = GridBagConstraints.HORIZONTAL; // Mặc định fill ngang

        // DATE: Nhận / Trả
        final JDateChooser dcCheckIn = new JDateChooser();
        dcCheckIn.setDateFormatString("dd/MM/yyyy");
        dcCheckIn.setPreferredSize(new Dimension(110, 24));

        final JDateChooser dcCheckOut = new JDateChooser();
        dcCheckOut.setDateFormatString("dd/MM/yyyy");
        dcCheckOut.setPreferredSize(new Dimension(110, 24));

        // Guest button
        final JButton btnGuestRoom = new JButton("2 khách"); // <<< SỬA: Bỏ "1 phòng"
        btnGuestRoom.setFocusPainted(false);
        btnGuestRoom.setPreferredSize(new Dimension(130, 36));
        btnGuestRoom.setBackground(Color.WHITE);
        btnGuestRoom.setForeground(new Color(33, 105, 170));
        btnGuestRoom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnGuestRoom.setBorder(new LineBorder(new Color(33, 105, 170), 1, true));

        // price controls (formatter + slider)
        NumberFormat intFormat = NumberFormat.getIntegerInstance(new Locale("vi","VN"));
        NumberFormatter numberFormatter = new NumberFormatter(intFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setCommitsOnValidEdit(true);

        final int PRICE_MIN = 0;
        final int PRICE_MAX = 24_000_000;

        final JFormattedTextField txtPriceMin = new JFormattedTextField(numberFormatter);
        final JFormattedTextField txtPriceMax = new JFormattedTextField(numberFormatter);

        txtPriceMin.setColumns(10);
        txtPriceMax.setColumns(10);

        txtPriceMin.setValue(PRICE_MIN);
        txtPriceMax.setValue(PRICE_MAX);

        final JSlider sliderPrice = new JSlider(PRICE_MIN, PRICE_MAX, PRICE_MAX);
        sliderPrice.setPreferredSize(new Dimension(220, 18));
        sliderPrice.setOpaque(false);

        final JComboBox<String> cbPriceTier = new JComboBox<>(new String[]{"Tất cả", "Thấp", "Trung bình", "Cao"});
        cbPriceTier.setPreferredSize(new Dimension(120, 26));

        // compactPrice panel (will expand horizontally)
        JPanel compactPrice = new JPanel();
        compactPrice.setLayout(new BoxLayout(compactPrice, BoxLayout.Y_AXIS));
        compactPrice.setOpaque(false);
        JPanel priceTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); priceTop.setOpaque(false);
        priceTop.add(new JLabel("<html><b>Khoảng giá</b></html>"));

        JPanel priceMid = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); // Thay vgap = 4 thành 0

        priceMid.setOpaque(false);
        priceMid.add(sliderPrice);
        JPanel priceBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); priceBottom.setOpaque(false);
        priceBottom.add(txtPriceMin);
        priceBottom.add(new JLabel("VND"));
        priceBottom.add(Box.createHorizontalStrut(6));
        priceBottom.add(txtPriceMax);
        priceBottom.add(new JLabel("VND"));
        compactPrice.add(priceTop);
        compactPrice.add(priceMid);
        compactPrice.add(priceBottom);
        compactPrice.setBorder(new EmptyBorder(0,6,0,6));
        compactPrice.setPreferredSize(new Dimension(380, 60));
        compactPrice.setMinimumSize(new Dimension(200, 60));

        // Buttons
        JButton btnTim = new JButton("🔍  Tìm");
        btnTim.setPreferredSize(new Dimension(100, 32));
        btnTim.setBackground(new Color(33, 105, 170));
        btnTim.setForeground(Color.WHITE);
        btnTim.setFocusPainted(false);
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTim.setBorder(new LineBorder(new Color(25, 85, 145), 1, true));
        btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnReset = new JButton("↺  Đặt lại");
        btnReset.setPreferredSize(new Dimension(100, 32));
        btnReset.setBackground(new Color(241, 243, 246));
        btnReset.setForeground(new Color(80, 90, 110));
        btnReset.setFocusPainted(false);
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnReset.setBorder(new LineBorder(new Color(210, 215, 225), 1, true));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // 1. Tạo Panel con (pnlDates) để chứa 2 hàng ngày
        JPanel pnlDates = new JPanel(new GridBagLayout());
        pnlDates.setOpaque(false); // Làm trong suốt để hòa vào nền
        GridBagConstraints gbcDates = new GridBagConstraints();
        gbcDates.anchor = GridBagConstraints.WEST; // Căn lề trái
        gbcDates.insets = new Insets(0, 0, 4, 4); // Khoảng cách giữa các mục

        // Hàng 0: Nhận
        gbcDates.gridx = 0;
        gbcDates.gridy = 0;
        pnlDates.add(new JLabel("Nhận:"), gbcDates);
        gbcDates.gridx = 1;
        pnlDates.add(dcCheckIn, gbcDates);

        // Hàng 1: Trả
        gbcDates.gridx = 0;
        gbcDates.gridy = 1;
        pnlDates.add(new JLabel("Trả:"), gbcDates);
        gbcDates.gridx = 1;
        pnlDates.add(dcCheckOut, gbcDates);

        // --- Thêm các thành phần vào pnlFilterTop ---

        // 2. Thêm panel pnlDates vào pnlFilterTop
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE; // Không co giãn panel ngày
        gbc.anchor = GridBagConstraints.WEST; // Căn lề trái
        pnlFilterTop.add(pnlDates, gbc);

        // 3. Thêm các thành phần còn lại, bắt đầu từ gridx = 1
        gbc.fill = GridBagConstraints.HORIZONTAL; // Đặt lại fill ngang

        gbc.gridx = 1; gbc.weightx = 0;
        pnlFilterTop.add(Box.createHorizontalStrut(8), gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlFilterTop.add(new JLabel("Khách:"), gbc);

        gbc.gridx = 3; gbc.weightx = 0;
        pnlFilterTop.add(btnGuestRoom, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        pnlFilterTop.add(new JLabel("Khoảng:"), gbc);

        gbc.gridx = 5; gbc.weightx = 0;
        pnlFilterTop.add(cbPriceTier, gbc);

        gbc.gridx = 6; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        pnlFilterTop.add(compactPrice, gbc);

        gbc.gridx = 7; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlFilterTop.add(btnTim, gbc);

        gbc.gridx = 8; gbc.weightx = 0;
        pnlFilterTop.add(btnReset, gbc);

        pnlFilterContainer.add(pnlFilterTop, BorderLayout.CENTER);
        pnlTop.add(pnlFilterContainer, BorderLayout.CENTER);

        pnlRight.add(pnlTop, BorderLayout.NORTH);


        // --- CENTER: Danh sách phòng ---
        final JPanel pnlTang = new JPanel();
        pnlTang.setLayout(new BoxLayout(pnlTang, BoxLayout.Y_AXIS));
        pnlTang.setBackground(new Color(245, 247, 250));

        JScrollPane scroll = new JScrollPane(pnlTang);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 225, 235), 1, true),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        scroll.setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        pnlRight.add(scroll, BorderLayout.CENTER);

        // ---------- Load data and build list ----------
        // <<< THAY ĐỔI: Dùng biến instance thay vì biến cục bộ >>>
        // Phong_DAO phongDAO = new Phong_DAO();
        final List<Phong> dsPhong = phongDAO.getDanhSachPhong();

        // <<< THAY ĐỔI: Đã chuyển currencyFormatter lên làm biến instance >>>
        // final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        Runnable buildTangFromList = () -> {
            pnlTang.removeAll();
            @SuppressWarnings("unchecked")
            java.util.List<Phong> visible = (java.util.List<Phong>) pnlTang.getClientProperty("visibleRooms");
            if (visible == null) visible = new java.util.ArrayList<>(dsPhong);

            java.util.Map<String, java.util.List<Phong>> theoTang = new java.util.LinkedHashMap<>();
            for (Phong p : visible) {
                String maPhong = p.getMaPhong();
                String soTang = "  Tầng " + (maPhong != null && maPhong.length() > 1 ? maPhong.charAt(1) : '0');
                theoTang.putIfAbsent(soTang, new java.util.ArrayList<>());
                theoTang.get(soTang).add(p);
            }

            for (String tang : theoTang.keySet()) {
                JPanel tungTang = new JPanel(new BorderLayout(10, 0));
                tungTang.setBackground(Color.WHITE);
                tungTang.setBorder(new CompoundBorder(
                        new LineBorder(new Color(228, 232, 240), 1, true),
                        new EmptyBorder(10, 12, 10, 12)));

                JLabel lblTang = new JLabel(tang, SwingConstants.LEFT);
                lblTang.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lblTang.setForeground(new Color(33, 105, 170));
                lblTang.setPreferredSize(new Dimension(100, 40));
                tungTang.add(lblTang, BorderLayout.WEST);

                JPanel panelPhongTang = new JPanel(new GridLayout(0, 6, 8, 8));
                panelPhongTang.setBackground(Color.WHITE);

                for (Phong p : theoTang.get(tang)) {

                    // 1. Lấy thông tin
                    String maPhong = (p.getMaPhong() == null ? "N/A" : p.getMaPhong());
                    String giaPhong = currencyFormatter.format(p.getGiaPhong());
                    String sucChua = p.getSucChua() + " người";

                    // 2. Tạo nội dung HTML
                    String buttonText = "<html><center>" +
                            "<b>" + maPhong + "</b><br>" + // In đậm mã phòng
                            giaPhong + "<br>" +
                            sucChua +
                            "</center></html>";

                    // 3. Tạo JButton với nội dung và kích thước mới
                    JButton btn = new JButton(buttonText) {
                        @Override
                        public Dimension getPreferredSize() {
                            return new Dimension(90, 90);
                        }
                    };

                    btn.setFocusPainted(false);
                    btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    btn.setForeground(Color.WHITE);
                    btn.setBorder(new LineBorder(new Color(255,255,255,60), 1, true));
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


                    if (p.getTinhTrang() == null) {
                        btn.setBackground(new Color(100, 116, 139));
                    } else {
                        switch(p.getTinhTrang()) {
                            case TRONG:
                                btn.setBackground(new Color(22, 163, 74));
                                break;
                            case DANG_SU_DUNG:
                                btn.setBackground(new Color(234, 88, 12));
                                break;
                            case DA_DAT:
                                btn.setBackground(new Color(220, 38, 38));
                                break;
                            case DANG_DON_DEP:
                            case DANG_SUA_CHUA:
                                btn.setBackground(new Color(100, 116, 139));
                                break;
                        }
                    }

                    // <<< BẮT ĐẦU THAY THẾ: LOGIC SỰ KIỆN NÚT PHÒNG >>>
                    // Truyền thêm "TrangChu_Gui.this.nhanVien" vào
                    btn.addActionListener(e -> {
                        // 1. Lấy trạng thái phòng
                        entity.TinhTrangPhong trangThai = p.getTinhTrang();

                        // 2. Lấy ngày đã chọn từ filter
                        Date selCheckIn = dcCheckIn.getDate();
                        Date selCheckOut = dcCheckOut.getDate();

                        // 3. XỬ LÝ THEO TRẠNG THÁI
                        // === NGHIỆP VỤ MỚI: Click phòng đang ở để GỌI DỊCH VỤ ===
                        if (trangThai == entity.TinhTrangPhong.DANG_SU_DUNG) {

                            // A. Tìm phiếu đặt phòng đang hoạt động cho phòng này
                            // (Sử dụng pdpDAO đã thêm vào TrangChu_Gui)
                            String maPhieu = pdpDAO.getMaPhieuHoatDong(p.getMaPhong());

                            if (maPhieu == null) {
                                JOptionPane.showMessageDialog(TrangChu_Gui.this,
                                        "Lỗi: Không tìm thấy Phiếu Đặt Phòng đang hoạt động cho phòng " + p.getMaPhong(),
                                        "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // B. Tạo đối tượng PhieuDatPhong (chỉ cần mã)
                            PhieuDatPhong pdp = new PhieuDatPhong(maPhieu);

                            // C. Mở Dialog Thêm Dịch Vụ
                            // (Sử dụng ThemDichVu_Dialog.java mà bạn đã cung cấp)
                            ThemDichVu_Dialog dialog = new ThemDichVu_Dialog(TrangChu_Gui.this, p, pdp);
                            dialog.setVisible(true);

                        }
                        // === NGHIỆP VỤ CŨ: Click các phòng khác để ĐẶT PHÒNG ===
                        else {
                            if (selCheckIn != null && selCheckOut != null) {
                                // (Logic cũ: kiểm tra ngày)
                                if (!selCheckOut.after(selCheckIn)) {
                                    JOptionPane.showMessageDialog(TrangChu_Gui.this, "Ngày trả phải sau ngày nhận.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                // (Logic cũ: kiểm tra phòng có sẵn trong khoảng đã chọn)
                                boolean ok = phongDAO.isPhongAvailable(p.getMaPhong(), selCheckIn, selCheckOut);
                                if (!ok) {
                                    JOptionPane.showMessageDialog(TrangChu_Gui.this,
                                            String.format("Phòng %s hiện có đặt chồng lên khoảng %1$td/%1$tm → %2$td/%2$tm. Vui lòng chọn phòng khác.",
                                                    p.getMaPhong(), selCheckIn, selCheckOut),
                                            "Phòng không sẵn có", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }

                                // (Logic cũ: Mở dialog đặt phòng với ngày đã chọn)
                                new ChiTietPhong_Dialog(TrangChu_Gui.this, p, TrangChu_Gui.this.nhanVien, selCheckIn, selCheckOut).setVisible(true);

                            } else {
                                // (Logic cũ: Mở dialog đặt phòng, chưa có ngày)
                                new ChiTietPhong_Dialog(TrangChu_Gui.this, p, TrangChu_Gui.this.nhanVien).setVisible(true);
                            }
                        }
                    });
                    // <<< KẾT THÚC THAY THẾ >>>

                    panelPhongTang.add(btn);
                }

                tungTang.add(panelPhongTang, BorderLayout.CENTER);
                pnlTang.add(tungTang);
                pnlTang.add(Box.createVerticalStrut(8));
            }

            pnlTang.revalidate();
            pnlTang.repaint();
        };

        // initial show all
        pnlTang.putClientProperty("visibleRooms", new java.util.ArrayList<>(dsPhong));
        buildTangFromList.run();

        // <<< ========================================================== >>>
        // <<< === BẮT ĐẦU NÂNG CẤP POPUP CHỌN KHÁCH (THEO YÊU CẦU) === >>>
        // <<< ========================================================== >>>

        // ---------------- Guest popup (ĐÃ NÂNG CẤP) ----------------
        final int[] nguoiLon = {2};
        final List<Integer> dsTuoiTreEm = new ArrayList<>();
        // <<< XÓA: final int[] soPhong = {1}; >>>

        JPanel guestContent = new JPanel();
        guestContent.setLayout(new BoxLayout(guestContent, BoxLayout.Y_AXIS));
        guestContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        guestContent.setBackground(Color.WHITE);

        // Class Row vẫn dùng cho Người lớn
        class Row {
            JPanel panel;
            JLabel lbl;
            JButton minus;
            JButton plus;
            JTextField value;
            JTextField getValueField() { return value; }
            Row(String title, int initial) {
                panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
                panel.setBackground(Color.WHITE);
                lbl = new JLabel(title);
                lbl.setPreferredSize(new Dimension(90, 24));
                minus = new JButton("-");
                minus.setPreferredSize(new Dimension(36, 28));
                plus = new JButton("+");
                plus.setPreferredSize(new Dimension(36, 28));
                value = new JTextField(String.valueOf(initial));
                value.setPreferredSize(new Dimension(40, 28));
                value.setHorizontalAlignment(JTextField.CENTER);
                value.setEditable(false);
                panel.add(lbl);
                panel.add(minus);
                panel.add(value);
                panel.add(plus);
            }
            JPanel get() { return panel; }
        }

        Row rowNguoiLon = new Row("Người lớn", nguoiLon[0]);
        // <<< XÓA: Row rowPhong = new Row("Phòng", soPhong[0]); >>>

        rowNguoiLon.minus.addActionListener(e -> {
            if (nguoiLon[0] > 1) { nguoiLon[0]--; rowNguoiLon.getValueField().setText(String.valueOf(nguoiLon[0])); }
        });
        rowNguoiLon.plus.addActionListener(e -> { nguoiLon[0]++; rowNguoiLon.getValueField().setText(String.valueOf(nguoiLon[0])); });

        // <<< XÓA: Các ActionListener của rowPhong >>>

        // --- Bắt đầu Panel Trẻ Em (MỚI) ---
        JPanel pnlTreEmList = new JPanel();
        pnlTreEmList.setLayout(new BoxLayout(pnlTreEmList, BoxLayout.Y_AXIS));
        pnlTreEmList.setBackground(Color.WHITE);

        JScrollPane scrollTreEm = new JScrollPane(pnlTreEmList);
        scrollTreEm.setOpaque(false);
        scrollTreEm.getViewport().setOpaque(false);
        scrollTreEm.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        scrollTreEm.setPreferredSize(new Dimension(280, 100)); // Giới hạn chiều cao

        JButton btnThemTreEm = new JButton("+ Thêm trẻ em");
        btnThemTreEm.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnThemTreEm.setFocusPainted(false);
        // --- Kết thúc Panel Trẻ Em ---

        guestContent.add(rowNguoiLon.get());

        // Thêm phần trẻ em
        JLabel lblTreEmTitle = new JLabel("Trẻ em (0-17 tuổi):");
        lblTreEmTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTreEmTitle.setPreferredSize(new Dimension(90, 24));
        lblTreEmTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTreEmTitle.setBorder(new EmptyBorder(5, 5, 0, 0));

        guestContent.add(lblTreEmTitle);
        guestContent.add(scrollTreEm);
        guestContent.add(btnThemTreEm);
        guestContent.add(Box.createVerticalStrut(8));

        // <<< XÓA: guestContent.add(rowPhong.get()); >>>
        // <<< XÓA: guestContent.add(Box.createVerticalStrut(8)); >>>

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(300, 1));
        guestContent.add(sep);
        guestContent.add(Box.createVerticalStrut(8));

        // <<< XÓA: JCheckBox cbPet = ... >>>
        // <<< XÓA: guestContent.add(cbPet); >>>
        // <<< XÓA: guestContent.add(Box.createVerticalStrut(8)); >>>

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Color.WHITE);
        JButton btnApplyGuest = new JButton("Áp dụng");
        JButton btnCancelGuest = new JButton("Hủy");
        actions.add(btnCancelGuest);
        actions.add(btnApplyGuest);
        guestContent.add(actions);

        final JPopupMenu popupGuest = new JPopupMenu();
        popupGuest.setBorder(new LineBorder(new Color(200,200,200),1,true));
        popupGuest.setLayout(new BorderLayout());
        popupGuest.add(guestContent, BorderLayout.CENTER);

        // Nút "+ Thêm trẻ em"
        btnThemTreEm.addActionListener(e -> {
            dsTuoiTreEm.add(6); // Thêm 1 trẻ 6 tuổi (mặc định)
            // Cập nhật lại UI danh sách trẻ em
            capNhatDanhSachTreEm(pnlTreEmList, dsTuoiTreEm, popupGuest);
        });

        btnGuestRoom.addActionListener(e -> {
            // Cập nhật lại UI trước khi hiển thị
            rowNguoiLon.getValueField().setText(String.valueOf(nguoiLon[0]));
            capNhatDanhSachTreEm(pnlTreEmList, dsTuoiTreEm, popupGuest); // Cập nhật ds trẻ em
            // <<< XÓA: rowPhong.getValueField()... >>>

            popupGuest.show(btnGuestRoom, 0, btnGuestRoom.getHeight());
            popupGuest.pack(); // Tự động điều chỉnh kích thước popup
        });

        btnApplyGuest.addActionListener(e -> {
            // <<< SỬA: Cập nhật text nút, bỏ "số phòng" >>>
            int tongSoNguoi = nguoiLon[0] + dsTuoiTreEm.size();
            btnGuestRoom.setText(tongSoNguoi + " khách");
            popupGuest.setVisible(false);
        });

        btnCancelGuest.addActionListener(e -> popupGuest.setVisible(false));

        // <<< ======================================================== >>>
        // <<< === KẾT THÚC NÂNG CẤP POPUP CHỌN KHÁCH === >>>
        // <<< ======================================================== >>>


        // ---------------- Price slider <-> min/max fields syncing ----------------
        final boolean[] priceSyncing = {false};

        // tier thresholds (example)
        final int TIER_LOW_MAX = 500_000;
        final int TIER_MED_MAX = 2_000_000;
        final int TIER_HIGH_MIN = TIER_MED_MAX + 1;

        // slider -> update max field
        sliderPrice.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (priceSyncing[0]) return;
                priceSyncing[0] = true;
                try {
                    int val = sliderPrice.getValue();
                    txtPriceMax.setValue(val);
                    Number curMin = (Number) txtPriceMin.getValue();
                    if (curMin != null && val < curMin.intValue()) {
                        txtPriceMin.setValue(val);
                    }
                    cbPriceTier.setSelectedItem("Tùy chỉnh");
                } finally {
                    priceSyncing[0] = false;
                }
            }
        });

        txtPriceMin.addPropertyChangeListener("value", evt -> {
            if (priceSyncing[0]) return;
            priceSyncing[0] = true;
            try {
                Number v = (Number) txtPriceMin.getValue();
                if (v == null) {
                    txtPriceMin.setValue(PRICE_MIN);
                    return;
                }
                int minVal = Math.max(PRICE_MIN, v.intValue());
                Number curMaxN = (Number) txtPriceMax.getValue();
                int curMax = (curMaxN != null) ? curMaxN.intValue() : PRICE_MAX;
                if (minVal > curMax) {
                    txtPriceMax.setValue(minVal);
                    sliderPrice.setValue(minVal);
                }
                txtPriceMin.setValue(minVal);
                cbPriceTier.setSelectedItem("Tùy chỉnh");
            } finally {
                priceSyncing[0] = false;
            }
        });

        txtPriceMax.addPropertyChangeListener("value", evt -> {
            if (priceSyncing[0]) return;
            priceSyncing[0] = true;
            try {
                Number v = (Number) txtPriceMax.getValue();
                if (v == null) {
                    txtPriceMax.setValue(PRICE_MAX);
                    return;
                }
                int maxVal = Math.min(PRICE_MAX, v.intValue());
                Number curMinN = (Number) txtPriceMin.getValue();
                int curMin = (curMinN != null) ? curMinN.intValue() : PRICE_MIN;
                if (maxVal < curMin) {
                    txtPriceMin.setValue(maxVal);
                }
                txtPriceMax.setValue(maxVal);
                sliderPrice.setValue(maxVal);
                cbPriceTier.setSelectedItem("Tùy chỉnh");
            } finally {
                priceSyncing[0] = false;
            }
        });

        // tier selection sets min/max
        cbPriceTier.addActionListener(e -> {
            String sel = (String) cbPriceTier.getSelectedItem();
            if (sel == null) return;
            if ("Tất cả".equals(sel)) {
                priceSyncing[0] = true;
                try {
                    txtPriceMin.setValue(PRICE_MIN);
                    txtPriceMax.setValue(PRICE_MAX);
                    sliderPrice.setValue(PRICE_MAX);
                } finally {
                    priceSyncing[0] = false;
                }
            } else if ("Thấp".equals(sel)) {
                priceSyncing[0] = true;
                try {
                    txtPriceMin.setValue(PRICE_MIN);
                    txtPriceMax.setValue(TIER_LOW_MAX);
                    sliderPrice.setValue(TIER_LOW_MAX);
                } finally {
                    priceSyncing[0] = false;
                }
            } else if ("Trung bình".equals(sel)) {
                priceSyncing[0] = true;
                try {
                    txtPriceMin.setValue(TIER_LOW_MAX + 1);
                    txtPriceMax.setValue(TIER_MED_MAX);
                    sliderPrice.setValue(TIER_MED_MAX);
                } finally {
                    priceSyncing[0] = false;
                }
            } else if ("Cao".equals(sel)) {
                priceSyncing[0] = true;
                try {
                    txtPriceMin.setValue(TIER_HIGH_MIN);
                    txtPriceMax.setValue(PRICE_MAX);
                    sliderPrice.setValue(PRICE_MAX);
                } finally {
                    priceSyncing[0] = false;
                }
            }
        });


        // <<< =================================================================== >>>
        // <<< === THAY THẾ TOÀN BỘ btnTim.addActionListener BẰNG CODE NÀY === >>>
        // <<< =================================================================== >>>
        // [DÁN ĐÈ LÊN btnTim.addActionListener CŨ]
        btnTim.addActionListener(e -> {
            // 1. LẤY ĐẦU VÀO TỪ GIAO DIỆN
            Date checkInDate = dcCheckIn.getDate();
            Date checkOutDate = dcCheckOut.getDate();
            int priceMin = ((Number) txtPriceMin.getValue()).intValue();
            int priceMax = ((Number) txtPriceMax.getValue()).intValue();
            int soNguoiLon = nguoiLon[0];

            // --- CẬP NHẬT MÀU SẮC TRẠNG THÁI TRÊN MÀN HÌNH CHÍNH ---
            List<Phong> dsPhongHienThi;
            if (checkInDate != null && checkOutDate != null) {
                // Đã chọn đủ ngày -> Xem trạng thái chính xác trong khoảng này
                dsPhongHienThi = phongDAO.getDSPhongTaiThoiDiem(checkInDate, checkOutDate);
            } else if (checkInDate != null) {
                // Mới chọn ngày nhận -> Xem trạng thái tại thời điểm nhận
                dsPhongHienThi = phongDAO.getDSPhongTaiThoiDiem(checkInDate, null);
            } else {
                // Chưa chọn ngày -> Xem trạng thái hiện tại
                dsPhongHienThi = phongDAO.getDanhSachPhong();
            }
            pnlTang.putClientProperty("visibleRooms", dsPhongHienThi);
            buildTangFromList.run();

            // --- NẾU CHƯA CHỌN ĐỦ NGÀY THÌ DỪNG TẠI ĐÂY ---
            if (checkInDate == null || checkOutDate == null) {
                return;
            }

            // --- KIỂM TRA TÍNH HỢP LỆ ---
            if (!checkOutDate.after(checkInDate)) {
                JOptionPane.showMessageDialog(panel, "Ngày trả phòng phải sau ngày nhận phòng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. TÍNH TỔNG SỨC CHỨA CẦN THIẾT (Cần có hàm tinhTongSucChuaCanThiet ở Bước 3)
            int tongSucChuaCanThiet = tinhTongSucChuaCanThiet(soNguoiLon, dsTuoiTreEm);

            // 3. LẤY DỮ LIỆU PHÒNG TRỐNG TỪ CSDL
            List<Phong> availableRooms = phongDAO.getPhongTheoTieuChi(checkInDate, checkOutDate, priceMin, priceMax);

            if (availableRooms.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Không tìm thấy phòng nào trống trong khoảng thời gian và mức giá này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // Nếu không tìm thấy, vẫn hiển thị danh sách đã cập nhật màu ở trên để nhân viên thấy rõ phòng nào bận.
                return;
            }

            // [QUAN TRỌNG] Cưỡng ép trạng thái về 'TRỐNG' (màu xanh) để tránh gây hiểu lầm
            // vì các phòng này chắc chắn trống vào ngày khách chọn, dù hiện tại có thể đang có khách.
            for (Phong p : availableRooms) {
                p.setTinhTrang(entity.TinhTrangPhong.TRONG);
            }

            // 4. TÌM KIẾM PHÒNG ĐƠN & TỔ HỢP
            // 4a. Phòng đơn đủ sức chứa
            List<Phong> singleCandidates = new ArrayList<>();
            for (Phong r : availableRooms) {
                if (r.getSucChua() >= tongSucChuaCanThiet) {
                    singleCandidates.add(r);
                }
            }
            singleCandidates.sort(Comparator.comparingDouble(Phong::getGiaPhong));

            // 4b. Tổ hợp nhiều phòng (Cần có hàm findCombinations ở Bước 3)
            List<List<Phong>> combos = findCombinations(availableRooms, tongSucChuaCanThiet, 5); // Giới hạn 5 phòng

            // Lọc trùng: Bỏ tổ hợp 1 phòng nếu đã có trong danh sách phòng đơn
            combos.removeIf(c -> c.size() == 1 && singleCandidates.stream().anyMatch(s -> s.getMaPhong().equals(c.get(0).getMaPhong())));

            // 5. KIỂM TRA KẾT QUẢ
            if (singleCandidates.isEmpty() && combos.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Tìm thấy " + availableRooms.size() + " phòng trống, nhưng không đủ sức chứa cho " + tongSucChuaCanThiet + " người.\n" +
                                "Vui lòng thử giảm số lượng khách hoặc chia nhỏ đoàn.",
                        "Không đủ sức chứa", JOptionPane.WARNING_MESSAGE);
                // Hiển thị tất cả phòng trống tìm được để nhân viên tự cân nhắc
                pnlTang.putClientProperty("visibleRooms", availableRooms);
                buildTangFromList.run();
                return;
            }

            // 6. HIỂN THỊ DIALOG KẾT QUẢ
            PhongSearchResult result = new PhongSearchResult(singleCandidates, combos);
            ChonToHopPhong_Dialog dialog = new ChonToHopPhong_Dialog(result);
            dialog.setVisible(true); // Code dừng tại đây chờ chọn

            // 7. XỬ LÝ SAU KHI ĐÓNG DIALOG
            List<Phong> luaChon = dialog.getSelectedOption();
            if (luaChon != null && !luaChon.isEmpty()) {
                // Nếu chọn 1 phương án -> Chỉ hiển thị các phòng đó
                pnlTang.putClientProperty("visibleRooms", luaChon);
                buildTangFromList.run();
            } else {
                // Nếu đóng dialog mà không chọn -> Hiển thị tất cả phòng trống để họ tự chọn thủ công
                pnlTang.putClientProperty("visibleRooms", availableRooms);
                buildTangFromList.run();
            }
        });

        btnReset.addActionListener(e -> {
            dcCheckIn.setDate(null);
            dcCheckOut.setDate(null);
            txtPriceMin.setValue(PRICE_MIN);
            txtPriceMax.setValue(PRICE_MAX);
            sliderPrice.setValue(PRICE_MAX);
            cbPriceTier.setSelectedItem("Tất cả");
            // <<< SỬA LỖI: Reset danh sách trẻ em >>>
            nguoiLon[0] = 2;
            dsTuoiTreEm.clear(); // Xóa hết trẻ em
            // <<< XÓA: soPhong[0] = 1; >>>
            btnGuestRoom.setText(nguoiLon[0] + " khách"); // <<< SỬA: Bỏ "số phòng"

            pnlTang.putClientProperty("visibleRooms", new java.util.ArrayList<>(dsPhong));
            buildTangFromList.run();
        });

        splitPane.setLeftComponent(pnlLeft);
        splitPane.setRightComponent(pnlRight);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * <<< HÀM NÀY ĐÃ ĐƯỢC SỬA LỖI LOGIC TỔ HỢP TRÙNG >>>
     */


    // <<< ======================================================== >>>
    // <<< === CÁC HÀM HELPER MỚI CHO LOGIC TRẺ EM === >>>
    // <<< ======================================================== >>>

    /**
     * HÀM MỚI: Tính tổng sức chứa cần thiết dựa theo quy tắc
     * (Trẻ > 12 tuổi = 1, 2 Trẻ <= 12 tuổi = 1)
     */


    /**
     * HÀM MỚI: Cập nhật UI cho danh sách trẻ em trong Popup
     * (ĐÃ SỬA LỖI AMBIGUOUS CONSTRUCTOR)
     */
    private void capNhatDanhSachTreEm(JPanel pnlList, List<Integer> dsTuoi, JPopupMenu popup) {
        pnlList.removeAll();

        for (int i = 0; i < dsTuoi.size(); i++) {
            final int index = i; // Cần final để dùng trong lambda

            JPanel pnlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            pnlRow.setBackground(Color.WHITE);

            JLabel lbl = new JLabel("Tuổi trẻ " + (i + 1) + ":");
            lbl.setPreferredSize(new Dimension(80, 24));

            // <<< SỬA LỖI Ở ĐÂY >>>
            // 1. Lấy giá trị ra biến int một cách rõ ràng
            int tuoiHienTai = dsTuoi.get(i);

            // 2. Sử dụng biến int đó
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(tuoiHienTai, 0, 17, 1));
            // <<< KẾT THÚC SỬA LỖI >>>

            spinner.setPreferredSize(new Dimension(60, 24));

            // Khi thay đổi spinner, cập nhật ngay vào List
            spinner.addChangeListener(e -> {
                dsTuoi.set(index, (Integer) spinner.getValue());
            });

            JButton btnXoa = new JButton("Xóa");
            btnXoa.setMargin(new Insets(2, 5, 2, 5));

            // Khi nhấn Xóa
            btnXoa.addActionListener(e -> {
                dsTuoi.remove(index); // Xóa khỏi danh sách
                capNhatDanhSachTreEm(pnlList, dsTuoi, popup); // Vẽ lại UI
            });

            pnlRow.add(lbl);
            pnlRow.add(spinner);
            pnlRow.add(btnXoa);

            pnlList.add(pnlRow);
        }

        pnlList.revalidate();
        pnlList.repaint();
        popup.pack(); // Tự động điều chỉnh lại kích thước popup
    }


    // ... (Code các hàm styleMenuButton, styleLogoutButton, taoSubMenu, v.v. giữ nguyên) ...

    private void styleMenuButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(28, 60, 95));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.setIconTextGap(15);
        btn.setBorder(new EmptyBorder(6, 20, 6, 12));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(38, 90, 140));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(28, 60, 95));
            }
        });
    }

    private void styleLogoutButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(220, 57, 57));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(200, 45, 45), 1, true));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(200, 40, 40));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(220, 57, 57));
            }
        });
    }

    private JPanel taoSubMenu(String[] tenButtons) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(new Color(15, 45, 80));
        pnl.setVisible(false);
        pnl.setBorder(new EmptyBorder(10, 0, 10, 0));

        for (String ten : tenButtons) {
            JButton btn = new JButton("•  " + ten);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btn.setBackground(pnl.getBackground());
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(SwingConstants.LEFT);

            btn.setBorder(new EmptyBorder(8, 40, 8, 8));

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(40, 80, 130));
                    btn.setForeground(Color.WHITE);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(pnl.getBackground());
                    btn.setForeground(Color.WHITE);
                }
            });

            pnl.add(btn);

            pnl.add(Box.createVerticalStrut(5));
        }
        return pnl;
    }

    private void toggleSubMenu(JPanel subMenu) {
        // Đóng tất cả các submenu khác trước
        if (subMenu != pnlPhong) pnlPhong.setVisible(false);
        if (subMenu != pnlKhuyenMai) pnlKhuyenMai.setVisible(false);
        if (subMenu != pnlDichVu) pnlDichVu.setVisible(false);
        if (subMenu != pnlKhachHang) pnlKhachHang.setVisible(false);
        if (subMenu != pnlNhanVien) pnlNhanVien.setVisible(false);
        if (subMenu != pnlCaLamViec) pnlCaLamViec.setVisible(false);

        // Mở/đóng submenu được chọn
        subMenu.setVisible(!subMenu.isVisible());
        revalidate();
        repaint();
    }

    private JPanel trangThaiPhong(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 1));
        panel.setOpaque(false);
        JPanel dot = new JPanel();
        dot.setBackground(color);
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setBorder(BorderFactory.createLineBorder(color.darker(), 1, true));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(60, 70, 90));
        panel.add(dot);
        panel.add(lbl);
        return panel;
    }

    private JPanel createQuickStatCard(String title, String value, Color color, String emoji) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 225, 235), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Top row: emoji + title
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topRow.setOpaque(false);
        JLabel lblEmoji = new JLabel(emoji);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(100, 110, 130));
        topRow.add(lblEmoji);
        topRow.add(lblTitle);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);

        // Bottom accent bar
        JPanel accent = new JPanel();
        accent.setPreferredSize(new Dimension(0, 3));
        accent.setBackground(color);

        card.add(topRow, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(accent, BorderLayout.SOUTH);

        return card;
    }

    // Keep old overload for backward compatibility
    private JPanel createQuickStatCard(String title, String value, Color color) {
        return createQuickStatCard(title, value, color, "•");
    }

    /**
     * Hàm này được gọi bởi DangNhap_Gui để tự động mở dialog Nhận Ca
     * ngay sau khi đăng nhập thành công.
     */
    public void moDialogNhanCaTuDong() {
        SwingUtilities.invokeLater(() -> {
            new NhanCaBanGiao_Dialog(this, this.nhanVien).setVisible(true);
        });
    }

    // <<< THÊM MỚI: Hàm khởi động đồng hồ >>>
    private void startClock() {
        // Định dạng thời gian
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Tạo Timer, cập nhật mỗi 1000ms (1 giây)
        timerDongHo = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            String timeStr = now.format(timeFormatter);
            String dateStr = now.format(dateFormatter);

            // Cập nhật JLabel (dùng HTML để xuống dòng)
            lblDongHo.setText("<html><center>" + timeStr + "<br>" + dateStr + "</center></html>");
        });
        timerDongHo.setInitialDelay(0); // Chạy ngay lập tức
        timerDongHo.start();
    }

    private ImageIcon getIcon(String tenIcon, int width, int height) {
        try {
            // 1. Ưu tiên tải từ Classpath (cách làm chuẩn, hoạt động cả trong JAR)
            // Giả định cấu trúc thư mục là: src/data/icons/tenIcon.png
            String resourcePath = "/data/icons/" + tenIcon + ".png";
            InputStream is = getClass().getResourceAsStream(resourcePath);

            BufferedImage bImg = null;

            if (is != null) {
                // Tìm thấy qua classpath
                bImg = ImageIO.read(is);
            } else {
                // 2. Dự phòng: Thử tải từ hệ thống tệp tin (giống cách tải avatar)
                // Cách này không linh hoạt bằng nhưng khớp với code hiện có của bạn
                String filePath = "data/icons/" + tenIcon + ".png";
                File f = new File(filePath);

                if (f.exists()) {
                    // <<< SỬA LỖI TYPO: ImageMIO -> ImageIO >>>
                    bImg = ImageIO.read(f);
                } else {
                    // 3. Không tìm thấy ảnh
                    System.err.println("Không tìm thấy icon: " + resourcePath + " hoặc " + filePath);
                    return null; // Trả về null nếu không tìm thấy
                }
            }

            // 4. Thay đổi kích thước ảnh
            Image scaledImg = bImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            // 5. Trả về ImageIcon
            return new ImageIcon(scaledImg);

        } catch (IOException e) {
            System.err.println("Lỗi khi tải icon: " + tenIcon);
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConnectDB.getInstance();
            NhanVien_DAO nv_dao = new NhanVien_DAO();
            NhanVien nvTest = nv_dao.getNhanVienByMa("NV002");

            if (nvTest == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy NV002 (Test) trong CSDL! Vui lòng kiểm tra CSDL và chuỗi kết nối.");
                System.exit(0);
            }

            try {
                UIManager.setLookAndFeel( new com.formdev.flatlaf.FlatLightLaf() );
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }

            new TrangChu_Gui(nvTest).setVisible(true);
        });
    }

    // =========================================================================
    // === CÁC INNER CLASS MỚI ĐƯỢC THÊM VÀO ĐÂY (THEO YÊU CẦU) ===
    // =========================================================================

    /**
     * Lớp con để chứa kết quả tìm kiếm (thay thế cho service)
     */
    class PhongSearchResult {
        private final List<Phong> singleRoomOptions;
        private final List<List<Phong>> combinationOptions;

        public PhongSearchResult(List<Phong> singleRoomOptions, List<List<Phong>> combinationOptions) {
            this.singleRoomOptions = singleRoomOptions;
            this.combinationOptions = combinationOptions;
        }

        public List<Phong> getSingleRoomOptions() { return singleRoomOptions; }
        public List<List<Phong>> getCombinationOptions() { return combinationOptions; }
    }


    /**
     * Inner Class: JDialog để hiển thị danh sách các Card lựa chọn phòng/tổ hợp
     */
    class ChonToHopPhong_Dialog extends JDialog {

        private JPanel pnlListOptions;
        private JScrollPane scrollPane;
        private List<Phong> selectedOption = null; // Đây là kết quả trả về

        // Định nghĩa màu và viền
        private final Color COLOR_DON = new Color(0, 122, 204); // Xanh dương
        private final Color COLOR_HOP = new Color(40, 167, 69); // Xanh lá
        private final Border LEFT_BORDER_DON = new MatteBorder(0, 5, 0, 0, COLOR_DON);
        private final Border LEFT_BORDER_HOP = new MatteBorder(0, 5, 0, 0, COLOR_HOP);


        public ChonToHopPhong_Dialog(PhongSearchResult result) {
            // Gọi super() của JDialog, trỏ 'this' của TrangChu_Gui làm parent
            super(TrangChu_Gui.this, "Kết Quả Tìm Kiếm", true);

            setSize(800, 600);
            setLocationRelativeTo(TrangChu_Gui.this); // Căn giữa so với frame cha
            setLayout(new BorderLayout(0, 10));
            ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

            // Tiêu đề
            JLabel lblTitle = new JLabel("Các lựa chọn phòng phù hợp:", SwingConstants.LEFT);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
            add(lblTitle, BorderLayout.NORTH);

            // Panel chính chứa danh sách các lựa chọn
            pnlListOptions = new JPanel();
            pnlListOptions.setLayout(new BoxLayout(pnlListOptions, BoxLayout.Y_AXIS));
            pnlListOptions.setBackground(new Color(245, 245, 245));
            pnlListOptions.setBorder(new EmptyBorder(5, 5, 5, 5)); // Đệm cho scrollpane

            // Thêm JScrollPane
            scrollPane = new JScrollPane(pnlListOptions);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            add(scrollPane, BorderLayout.CENTER);

            // Xây dựng UI từ dữ liệu
            buildUIFromData(result);
        }

        /**
         * Lấy lựa chọn của người dùng sau khi dialog đóng
         */
        public List<Phong> getSelectedOption() {
            return selectedOption;
        }

        /**
         * Hàm này được gọi bởi Card bên trong để set kết quả và đóng dialog
         */
        private void xuLyChon(List<Phong> option) {
            this.selectedOption = option;
            this.dispose(); // Đóng cửa sổ
        }

        /**
         * Dựng các panel lựa chọn từ PhongSearchResult
         */
        private void buildUIFromData(PhongSearchResult result) {
            // 1. Thêm các phòng đơn
            List<Phong> singleOptions = result.getSingleRoomOptions();
            if (singleOptions != null && !singleOptions.isEmpty()) {
                for (Phong phong : singleOptions) {
                    // Chuyển phòng đơn thành List<Phong>
                    pnlListOptions.add(new PhongOptionCard(Arrays.asList(phong), "PHÒNG ĐƠN", this));
                    pnlListOptions.add(Box.createVerticalStrut(8));
                }
            }

            // 2. Thêm các tổ hợp phòng (Tối đa 10)
            List<List<Phong>> comboOptions = result.getCombinationOptions();
            if (comboOptions != null && !comboOptions.isEmpty()) {

                int limit = Math.min(comboOptions.size(), 10);



                for (int i = 0; i < limit; i++) { // Chỉ lặp 10 lần
                    List<Phong> combo = comboOptions.get(i);
                    pnlListOptions.add(new PhongOptionCard(combo, "TỔ HỢP", this));
                    pnlListOptions.add(Box.createVerticalStrut(8));
                }
            }

            // 3. Nếu không có kết quả nào
            if (pnlListOptions.getComponentCount() == 0) {
                JLabel lblEmpty = new JLabel("Không tìm thấy phòng hay tổ hợp nào.");
                lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
                pnlListOptions.add(lblEmpty);
            }
        }



        // =====================================================================
        // === INNER CLASS CHO CARD LỰA CHỌN (Giống PhieuDatPhongCard) ===
        // =====================================================================

        class PhongOptionCard extends JPanel {

            private List<Phong> option;
            private String loai;
            private ChonToHopPhong_Dialog parentDialog;

            public PhongOptionCard(List<Phong> option, String loai, ChonToHopPhong_Dialog parent) {
                this.option = option;
                this.loai = loai;
                this.parentDialog = parent;

                setLayout(new BorderLayout(15, 10));
                setBackground(Color.WHITE);

                // Viền ngoài (shadow) + viền màu
                Border shadowBorder = BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true);
                Border marginBorder = new EmptyBorder(10, 10, 10, 10);
                Border colorBorder = loai.equals("PHÒNG ĐƠN") ? LEFT_BORDER_DON : LEFT_BORDER_HOP;

                setBorder(new CompoundBorder(shadowBorder, new CompoundBorder(colorBorder, marginBorder)));

                // <<< SỬA LỖI: Chiều cao tự động dựa trên số phòng >>>
                int preferredHeight = 120 + (option.size() > 1 ? (option.size() * 25) : 0);
                setPreferredSize(new Dimension(600, preferredHeight));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredHeight));
                setMinimumSize(new Dimension(600, 120));


                // --- Panel chứa nút (BÊN PHẢI) ---
                JPanel pnlButton = new JPanel(new GridBagLayout()); // Dùng GridBag để căn giữa nút
                pnlButton.setOpaque(false);
                pnlButton.setPreferredSize(new Dimension(180, 0));

                JButton btnChon = new JButton("Chọn Lựa Chọn Này");

                pnlButton.add(btnChon, new GridBagConstraints()); // Thêm vào giữa
                add(pnlButton, BorderLayout.EAST);

                // --- Panel thông tin (BÊN TRÁI) ---
                JPanel pnlInfo = new JPanel(new BorderLayout(5, 5));
                pnlInfo.setOpaque(false);

                // TOP: Loại
                JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                pnlTop.setOpaque(false);
                JLabel lblLoai = new JLabel(loai + " (" + option.size() + " phòng)");
                lblLoai.setFont(new Font("Segoe UI", Font.BOLD, 18));
                pnlTop.add(lblLoai);

                // <<< BẮT ĐẦU THAY ĐỔI LỚN >>>

                // CENTER: Chi tiết TỪNG phòng (dùng JScrollPane)
                JPanel pnlRoomDetails = new JPanel();
                pnlRoomDetails.setLayout(new BoxLayout(pnlRoomDetails, BoxLayout.Y_AXIS));
                pnlRoomDetails.setOpaque(false);
                pnlRoomDetails.setBorder(new EmptyBorder(5, 0, 5, 0));

                // Tính toán tổng
                int tongSucChua = 0;
                double tongGia = 0;

                for(Phong p : option) {
                    // Tạo một panel nhỏ cho mỗi phòng
                    JPanel pnlMotPhong = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
                    pnlMotPhong.setOpaque(false);

                    JLabel lblMaPhong = new JLabel("• " + p.getMaPhong());
                    lblMaPhong.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lblMaPhong.setPreferredSize(new Dimension(80, 20)); // Cố định độ rộng

                    // Dùng html để hiển thị label: value
                    JLabel lblChiTiet = new JLabel(String.format(
                            "<html><b>Loại:</b> %s | <b>Sức chứa:</b> %d | <b>Giá:</b> %s</html>",
                            p.getLoaiPhong().toString(), // "DON", "DOI" (từ Enum)
                            p.getSucChua(),
                            currencyFormatter.format(p.getGiaPhong())
                    ));
                    lblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                    pnlMotPhong.add(lblMaPhong);
                    pnlMotPhong.add(lblChiTiet);
                    pnlRoomDetails.add(pnlMotPhong);

                    // Cộng dồn
                    tongSucChua += p.getSucChua();
                    tongGia += p.getGiaPhong();
                }

                // Bọc pnlRoomDetails trong JScrollPane
                JScrollPane scrollDetails = new JScrollPane(pnlRoomDetails);
                scrollDetails.setOpaque(false);
                scrollDetails.getViewport().setOpaque(false);
                scrollDetails.setBorder(BorderFactory.createEmptyBorder());
                // Xóa giới hạn chiều cao để nó tự dãn
                // scrollDetails.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); 

                // BOTTOM: Tổng cộng
                JPanel pnlBottom = new JPanel(new GridLayout(0, 2, 20, 0)); // 2 cột
                pnlBottom.setOpaque(false);
                pnlBottom.setBorder(new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                        new EmptyBorder(8, 0, 0, 0) // Padding top
                ));

                JLabel lblTongSucChua = new JLabel(String.format(
                        "<html><b>Tổng sức chứa:</b> %d người</html>", tongSucChua
                ));
                lblTongSucChua.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JLabel lblTongGia = new JLabel(String.format(
                        "<html><b>Tổng giá:</b> <font color='red'>%s</font></html>",
                        currencyFormatter.format(tongGia)
                ));
                lblTongGia.setFont(new Font("Segoe UI", Font.BOLD, 14));

                pnlBottom.add(lblTongSucChua);
                pnlBottom.add(lblTongGia);

                // <<< KẾT THÚC THAY ĐỔI LỚN >>>

                // Set màu và style
                if (loai.equals("PHÒNG ĐƠN")) {
                    lblLoai.setForeground(COLOR_DON);
                    styleCardButton(btnChon, COLOR_DON);
                } else {
                    lblLoai.setForeground(COLOR_HOP);
                    styleCardButton(btnChon, COLOR_HOP);
                }

                // Ghép lại
                pnlInfo.add(pnlTop, BorderLayout.NORTH);
                pnlInfo.add(scrollDetails, BorderLayout.CENTER); // Thay pnlCenter bằng scrollDetails
                pnlInfo.add(pnlBottom, BorderLayout.SOUTH);

                add(pnlInfo, BorderLayout.CENTER);

                // --- Sự kiện cho nút ---
                btnChon.addActionListener(e -> {
                    // Gọi hàm của dialog cha để xử lý
                    parentDialog.xuLyChon(this.option);
                });
            }

            // Helper tạo label (Giống PhieuDatPhongCard)
            private JLabel createIconLabel(String icon, String title, String value) {
                // Thêm một chút CSS để `value` không bị tràn
                String html = String.format(
                        "<html><body style='width: 400px;'>%s <b>%s</b> %s</body></html>",
                        icon, title, value
                );
                JLabel lbl = new JLabel(html);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                return lbl;
            }

            // Helper style nút (Giống PhieuDatPhongCard)
            private void styleCardButton(JButton btn, Color color) {
                btn.setBackground(color);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                btn.setFocusPainted(false);
                btn.setBorder(new EmptyBorder(8, 15, 8, 15));
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }
        } // --- Hết inner class PhongOptionCard ---
    } // --- Hết inner class ChonToHopPhong_Dialog ---

    // =============================================================================
    // === CÁC HÀM BỔ TRỢ CHO TÌM KIẾM NÂNG CAO (DÁN VÀO CUỐI CLASS TRANGCHU_GUI) ===
    // =============================================================================

    /**
     * Hàm tính tổng sức chứa cần thiết (bao gồm quy đổi trẻ em)
     */
    private int tinhTongSucChuaCanThiet(int soNguoiLon, List<Integer> dsTuoiTreEm) {
        int treEmTren12 = 0;
        int treEmDuoi12 = 0;
        for (int tuoi : dsTuoiTreEm) {
            if (tuoi > 12) treEmTren12++;
            else treEmDuoi12++;
        }
        // 1 trẻ > 12 tính là 1 người lớn. 2 trẻ <= 12 tính là 1 người lớn.
        return soNguoiLon + treEmTren12 + (int) Math.ceil(treEmDuoi12 / 2.0);
    }

    /**
     * Hàm tìm các tổ hợp phòng (Backtracking)
     */
    private List<List<Phong>> findCombinations(List<Phong> availableRooms, int guestsNeeded, int maxRooms) {
        List<List<Phong>> results = new ArrayList<>();
        // Sắp xếp giảm dần theo sức chứa để ưu tiên tìm phòng lớn trước
        availableRooms.sort((p1, p2) -> Integer.compare(p2.getSucChua(), p1.getSucChua()));

        // Gọi hàm đệ quy để bắt đầu tìm kiếm
        findCombosRecursive(availableRooms, guestsNeeded, maxRooms, 0, new ArrayList<>(), 0, results);

        // Sắp xếp kết quả: Ưu tiên ít phòng hơn, sau đó đến giá rẻ hơn
        results.sort((c1, c2) -> {
            int sizeCompare = Integer.compare(c1.size(), c2.size());
            if (sizeCompare != 0) return sizeCompare;
            double price1 = c1.stream().mapToDouble(Phong::getGiaPhong).sum();
            double price2 = c2.stream().mapToDouble(Phong::getGiaPhong).sum();
            return Double.compare(price1, price2);
        });

        // Chỉ lấy tối đa 15 kết quả tốt nhất để không làm rối mắt
        if (results.size() > 15) {
            return new ArrayList<>(results.subList(0, 15));
        }
        return results;
    }

    /**
     * Hàm đệ quy hỗ trợ cho findCombinations
     */
    private void findCombosRecursive(List<Phong> pool, int guestsNeeded, int maxRooms, int startIndex,
                                     List<Phong> currentCombo, int currentCapacity, List<List<Phong>> results) {
        // Điều kiện dừng 1: Đã đủ sức chứa -> Lưu kết quả và dừng nhánh này
        if (currentCapacity >= guestsNeeded) {
            results.add(new ArrayList<>(currentCombo));
            return;
        }

        // Điều kiện dừng 2: Đã đạt giới hạn số phòng -> Dừng nhánh này
        if (currentCombo.size() >= maxRooms) {
            return;
        }

        // Duyệt các phòng còn lại trong danh sách
        for (int i = startIndex; i < pool.size(); i++) {
            Phong p = pool.get(i);

            // Thêm phòng vào tổ hợp hiện tại
            currentCombo.add(p);

            // Gọi đệ quy để tìm phòng tiếp theo
            findCombosRecursive(pool, guestsNeeded, maxRooms, i + 1, currentCombo, currentCapacity + p.getSucChua(), results);

            // Quay lui (Backtrack): Bỏ phòng vừa thêm để thử phòng khác
            currentCombo.remove(currentCombo.size() - 1);
        }
    }
}