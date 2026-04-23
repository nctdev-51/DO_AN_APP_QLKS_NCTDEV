package gui;

import core.entity.NhanVien;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TrangChu_Gui extends JFrame {
    private final NhanVien nhanVien;

    private JPanel pnlPhong;
    private JPanel pnlThongKe;
    private JPanel pnlKhuyenMai;
    private JPanel pnlDichVu;
    private JPanel pnlKhachHang;
    private JPanel pnlNhanVien;
    private JPanel pnlCaLamViec;

    private JPanel pCenter;
    private CardLayout cardLayout;
    private final Set<String> registeredCards = new HashSet<>();

    private JLabel lblDongHo;
    private Timer timerDongHo;

    private enum RoomState {
        TRONG, DANG_O, DA_DAT
    }

    private static class RoomItem {
        private final String maPhong;
        private final int tang;
        private final int sucChua;
        private final double gia;
        private final RoomState tinhTrang;

        private RoomItem(String maPhong, int tang, int sucChua, double gia, RoomState tinhTrang) {
            this.maPhong = maPhong;
            this.tang = tang;
            this.sucChua = sucChua;
            this.gia = gia;
            this.tinhTrang = tinhTrang;
        }
    }

    public TrangChu_Gui(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        initializeUI();
        startClock();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Quản lý khách sạn TATP");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

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

        JButton btnNhanCaDialog = new JButton("Nhận/Bàn giao ca");
        btnNhanCaDialog.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNhanCaDialog.setBackground(new Color(255, 170, 0));
        btnNhanCaDialog.setForeground(Color.BLACK);
        btnNhanCaDialog.setFocusPainted(false);
        btnNhanCaDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblChaoMung = new JLabel("Xin chào: " + (nhanVien != null ? nhanVien.getHoTen() : "Nhân viên"));
        lblChaoMung.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChaoMung.setForeground(Color.WHITE);

        JLabel lblAvatar = taoAvatarLabel();

        pnlChaoMung.add(btnNhanCaDialog);
        pnlChaoMung.add(lblChaoMung);
        pnlChaoMung.add(lblAvatar);

        pnlTieuDe.add(pnlHeaderLeft, BorderLayout.WEST);
        pnlTieuDe.add(pnlChaoMung, BorderLayout.EAST);
        add(pnlTieuDe, BorderLayout.NORTH);

        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setBackground(new Color(20, 60, 100));
        pnlMenu.setPreferredSize(new Dimension(240, 0));
        pnlMenu.setBorder(new EmptyBorder(16, 0, 16, 0));

        JLabel lblMenuTitle = new JLabel("MENU");
        lblMenuTitle.setForeground(Color.WHITE);
        lblMenuTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMenuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMenuTitle.setBorder(new EmptyBorder(0, 20, 0, 0));
        pnlMenu.add(lblMenuTitle);
        pnlMenu.add(Box.createVerticalStrut(12));

        JButton btnDashboard = new JButton(" Dashboard");
        JButton btnTrangChinh = new JButton(" Trang chủ");
        JButton btnPhong = new JButton(" Phòng");
        JButton btnThongKe = new JButton(" Thống kê");
        JButton btnKhuyenMai = new JButton(" Khuyến mãi");
        JButton btnDichVu = new JButton(" Dịch vụ");
        JButton btnKhachHang = new JButton(" Khách hàng");
        JButton btnNhanVien = new JButton(" Nhân viên");
        JButton btnPhanCongCa = new JButton(" Phân công ca");
        JButton btnCaLamViec = new JButton(" Ca làm việc");
        JButton btnDangXuat = new JButton("Đăng xuất");

        JButton[] buttons = {
                btnDashboard, btnTrangChinh, btnPhong, btnThongKe,
                btnKhuyenMai, btnDichVu, btnKhachHang, btnNhanVien, btnPhanCongCa, btnCaLamViec
        };

        pnlPhong = taoSubMenu(new String[]{"Đặt phòng", "Quản lý phiếu đặt", "Gọi dịch vụ", "Quản lý phòng"});
        pnlThongKe = taoSubMenu(new String[]{"Doanh thu", "Phòng", "Dịch vụ"});
        pnlKhuyenMai = taoSubMenu(new String[]{"Quản lý khuyến mãi", "Tìm kiếm khuyến mãi"});
        pnlDichVu = taoSubMenu(new String[]{"Quản lý dịch vụ", "Tìm kiếm dịch vụ"});
        pnlKhachHang = taoSubMenu(new String[]{"Quản lý khách hàng", "Tìm kiếm khách hàng"});
        pnlNhanVien = taoSubMenu(new String[]{"Quản lý nhân viên", "Tìm kiếm nhân viên"});
        pnlCaLamViec = taoSubMenu(new String[]{"Quản lý ca làm việc", "Lịch sử ca làm việc"});

        btnPhong.addActionListener(e -> toggleSubMenu(pnlPhong));
        btnThongKe.addActionListener(e -> toggleSubMenu(pnlThongKe));
        btnKhuyenMai.addActionListener(e -> toggleSubMenu(pnlKhuyenMai));
        btnDichVu.addActionListener(e -> toggleSubMenu(pnlDichVu));
        btnKhachHang.addActionListener(e -> toggleSubMenu(pnlKhachHang));
        btnNhanVien.addActionListener(e -> toggleSubMenu(pnlNhanVien));
        btnCaLamViec.addActionListener(e -> toggleSubMenu(pnlCaLamViec));
        btnPhanCongCa.addActionListener(e -> showPlaceholder("QuanLyPhanCong"));

        for (JButton btn : buttons) {
            styleMenuButton(btn);
            pnlMenu.add(btn);

            if (btn == btnPhong) pnlMenu.add(pnlPhong);
            if (btn == btnThongKe) pnlMenu.add(pnlThongKe);
            if (btn == btnKhuyenMai) pnlMenu.add(pnlKhuyenMai);
            if (btn == btnDichVu) pnlMenu.add(pnlDichVu);
            if (btn == btnKhachHang) pnlMenu.add(pnlKhachHang);
            if (btn == btnNhanVien) pnlMenu.add(pnlNhanVien);
            if (btn == btnCaLamViec) pnlMenu.add(pnlCaLamViec);

            pnlMenu.add(Box.createVerticalStrut(8));
        }

        pnlMenu.add(Box.createVerticalGlue());
        styleLogoutButton(btnDangXuat);
        btnDangXuat.setPreferredSize(new Dimension(pnlMenu.getPreferredSize().width, 60));
        btnDangXuat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pnlMenu.add(btnDangXuat);
        add(pnlMenu, BorderLayout.WEST);

        cardLayout = new CardLayout();
        pCenter = new JPanel(cardLayout);
        pCenter.setBackground(Color.WHITE);

        addCard(taoTrangChuPanel(), "TrangChu");
        addCard(taoPlaceholderPanel("Quản lý phòng", "Khu vực này sẽ hiển thị chức năng quản lý phòng."), "QuanLyPhong");
        addCard(taoPlaceholderPanel("Quản lý phiếu đặt", "Khu vực này sẽ hiển thị danh sách phiếu đặt phòng."), "QuanLyPhieuDatPhong");
        addCard(taoPlaceholderPanel("Gọi dịch vụ", "Khu vực này sẽ hiển thị chức năng gọi dịch vụ."), "QuanLyGoiDichVu");
        addCard(taoPlaceholderPanel("Thống kê", "Khu vực này sẽ hiển thị báo cáo và số liệu."), "QuanLyThongKe");
        addCard(taoPlaceholderPanel("Quản lý khuyến mãi", "Khu vực này sẽ hiển thị danh sách khuyến mãi."), "QuanLyKhuyenMai");
        addCard(taoPlaceholderPanel("Quản lý dịch vụ", "Khu vực này sẽ hiển thị dịch vụ và gói dịch vụ."), "QuanLyDichVu");
        addCard(new QuanLyKhachHang(), "QuanLyKhachHang");
        addCard(taoPlaceholderPanel("Quản lý nhân viên", "Khu vực này sẽ hiển thị danh sách nhân viên."), "QuanLyNhanVien");
        addCard(taoPlaceholderPanel("Quản lý ca làm việc", "Khu vực này sẽ hiển thị ca làm việc."), "QuanLyCaLamViec");
        addCard(taoPlaceholderPanel("Lịch sử ca làm việc", "Khu vực này sẽ hiển thị lịch sử nhận/bàn giao ca."), "LichSuCaLamViec");
        addCard(taoPlaceholderPanel("Phân công ca", "Khu vực này sẽ hiển thị phân công ca cho nhân viên."), "QuanLyPhanCong");
        addCard(taoPlaceholderPanel("Tìm kiếm khách hàng", "Khu vực tìm kiếm khách hàng đang được hoàn thiện."), "TimKiemKhachHang");
        addCard(taoPlaceholderPanel("Tìm kiếm nhân viên", "Khu vực tìm kiếm nhân viên đang được hoàn thiện."), "TimKiemNhanVien");

        add(pCenter, BorderLayout.CENTER);

        btnTrangChinh.addActionListener(e -> cardLayout.show(pCenter, "TrangChu"));
        btnDashboard.addActionListener(e -> cardLayout.show(pCenter, "TrangChu"));

        setupSubMenuActions();

        btnDangXuat.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn đăng xuất?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                if (timerDongHo != null) {
                    timerDongHo.stop();
                }
                dispose();
                new DangNhap_Gui();
            }
        });

        btnNhanCaDialog.addActionListener(e -> moDialogNhanCaTuDong());
    }

    private void setupSubMenuActions() {
        addActionsFromPanel(pnlPhong, new Runnable[]{
                () -> showPlaceholder("TrangChu"),
                () -> showPlaceholder("QuanLyPhieuDatPhong"),
                () -> showPlaceholder("QuanLyGoiDichVu"),
                () -> showPlaceholder("QuanLyPhong")
        });

        addActionsFromPanel(pnlThongKe, new Runnable[]{
                () -> showPlaceholder("QuanLyThongKe"),
                () -> showPlaceholder("QuanLyThongKe"),
                () -> showPlaceholder("QuanLyThongKe")
        });

        addActionsFromPanel(pnlKhuyenMai, new Runnable[]{
                () -> showPlaceholder("QuanLyKhuyenMai"),
                () -> showPlaceholder("QuanLyKhuyenMai")
        });

        addActionsFromPanel(pnlDichVu, new Runnable[]{
                () -> showPlaceholder("QuanLyDichVu"),
                () -> showPlaceholder("QuanLyDichVu")
        });

        addActionsFromPanel(pnlKhachHang, new Runnable[]{
                () -> showPlaceholder("QuanLyKhachHang"),
                () -> showPlaceholder("TimKiemKhachHang")
        });

        addActionsFromPanel(pnlNhanVien, new Runnable[]{
                () -> showPlaceholder("QuanLyNhanVien"),
                () -> showPlaceholder("TimKiemNhanVien")
        });

        addActionsFromPanel(pnlCaLamViec, new Runnable[]{
                () -> showPlaceholder("QuanLyCaLamViec"),
                () -> showPlaceholder("LichSuCaLamViec")
        });
    }

    private void addActionsFromPanel(JPanel panel, Runnable[] actions) {
        int index = 0;
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton && index < actions.length) {
                Runnable action = actions[index++];
                ((JButton) component).addActionListener(e -> action.run());
            }
        }
    }

    private void addCard(JComponent panel, String key) {
        pCenter.add(panel, key);
        registeredCards.add(key);
    }

    private void showPlaceholder(String title) {
        if (!registeredCards.contains(title)) {
            addCard(taoPlaceholderPanel(title, "Mục này đang được phát triển."), title);
        }
        cardLayout.show(pCenter, title);
    }

    private JLabel taoAvatarLabel() {
        ImageIcon icon = getImageIcon("data/images/pngtree-default-avatar-image_2235111.jpg", 42, 42);
        JLabel lblAvatar;
        if (icon != null) {
            lblAvatar = new JLabel(icon);
        } else {
            lblAvatar = new JLabel("NV", SwingConstants.CENTER);
            lblAvatar.setOpaque(true);
            lblAvatar.setBackground(new Color(255, 255, 255, 30));
            lblAvatar.setForeground(Color.WHITE);
            lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }
        lblAvatar.setPreferredSize(new Dimension(42, 42));
        lblAvatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        return lblAvatar;
    }

    private ImageIcon getImageIcon(String path, int width, int height) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/" + path);
            BufferedImage bufferedImage;
            if (inputStream != null) {
                bufferedImage = javax.imageio.ImageIO.read(inputStream);
            } else {
                File file = new File(path);
                if (!file.exists()) {
                    return null;
                }
                bufferedImage = javax.imageio.ImageIO.read(file);
            }
            if (bufferedImage == null) {
                return null;
            }
            Image scaled = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) {
            return null;
        }
    }

    private JPanel taoTrangChuPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel pnlTop = new JPanel(new BorderLayout(10, 0));
        pnlTop.setBackground(Color.WHITE);

        JPanel pnlTrangThai = new JPanel(new GridLayout(0, 1, 0, 8));
        pnlTrangThai.setBackground(Color.WHITE);
        pnlTrangThai.add(trangThaiPhong(new Color(0, 180, 0), "Phòng trống"));
        pnlTrangThai.add(trangThaiPhong(new Color(255, 170, 0), "Phòng đang ở"));
        pnlTrangThai.add(trangThaiPhong(new Color(200, 0, 0), "Phòng đã đặt"));
        pnlTop.add(pnlTrangThai, BorderLayout.WEST);

        JPanel pnlFilterTop = new JPanel(new GridBagLayout());
        pnlFilterTop.setBackground(new Color(250, 250, 250));
        pnlFilterTop.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JSpinner spCheckIn = new JSpinner(new SpinnerDateModel());
        JSpinner spCheckOut = new JSpinner(new SpinnerDateModel());
        spCheckIn.setEditor(new JSpinner.DateEditor(spCheckIn, "dd/MM/yyyy"));
        spCheckOut.setEditor(new JSpinner.DateEditor(spCheckOut, "dd/MM/yyyy"));
        spCheckIn.setPreferredSize(new Dimension(110, 24));
        spCheckOut.setPreferredSize(new Dimension(110, 24));

        JPanel pnlDates = new JPanel(new GridBagLayout());
        pnlDates.setOpaque(false);
        GridBagConstraints gbcDates = new GridBagConstraints();
        gbcDates.anchor = GridBagConstraints.WEST;
        gbcDates.insets = new Insets(0, 0, 4, 4);

        gbcDates.gridx = 0;
        gbcDates.gridy = 0;
        pnlDates.add(new JLabel("Nhận:"), gbcDates);
        gbcDates.gridx = 1;
        pnlDates.add(spCheckIn, gbcDates);

        gbcDates.gridx = 0;
        gbcDates.gridy = 1;
        pnlDates.add(new JLabel("Trả:"), gbcDates);
        gbcDates.gridx = 1;
        pnlDates.add(spCheckOut, gbcDates);

        final int[] nguoiLon = {2};
        final List<Integer> dsTreEm = new ArrayList<>();
        JButton btnGuestRoom = new JButton("2 khách");
        btnGuestRoom.setFocusPainted(false);
        btnGuestRoom.setPreferredSize(new Dimension(130, 36));
        btnGuestRoom.setBackground(Color.WHITE);
        btnGuestRoom.setBorder(new LineBorder(new Color(0, 120, 215), 2, true));

        NumberFormat intFormat = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));
        NumberFormatter numberFormatter = new NumberFormatter(intFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setCommitsOnValidEdit(true);

        final int PRICE_MIN = 0;
        final int PRICE_MAX = 5_000_000;

        JFormattedTextField txtPriceMin = new JFormattedTextField(numberFormatter);
        JFormattedTextField txtPriceMax = new JFormattedTextField(numberFormatter);
        txtPriceMin.setColumns(8);
        txtPriceMax.setColumns(8);
        txtPriceMin.setValue(PRICE_MIN);
        txtPriceMax.setValue(PRICE_MAX);

        JSlider sliderPrice = new JSlider(PRICE_MIN, PRICE_MAX, PRICE_MAX);
        sliderPrice.setPreferredSize(new Dimension(180, 18));
        sliderPrice.setOpaque(false);

        JComboBox<String> cbPriceTier = new JComboBox<>(new String[]{"Tất cả", "Thấp", "Trung bình", "Cao"});
        cbPriceTier.setPreferredSize(new Dimension(120, 26));

        JPanel compactPrice = new JPanel();
        compactPrice.setLayout(new BoxLayout(compactPrice, BoxLayout.Y_AXIS));
        compactPrice.setOpaque(false);
        JPanel priceTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        priceTop.setOpaque(false);
        priceTop.add(new JLabel("<html><b>Khoảng giá</b></html>"));

        JPanel priceMid = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        priceMid.setOpaque(false);
        priceMid.add(sliderPrice);

        JPanel priceBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        priceBottom.setOpaque(false);
        priceBottom.add(txtPriceMin);
        priceBottom.add(new JLabel("VND"));
        priceBottom.add(txtPriceMax);
        priceBottom.add(new JLabel("VND"));

        compactPrice.add(priceTop);
        compactPrice.add(priceMid);
        compactPrice.add(priceBottom);

        JButton btnTim = new JButton("Tìm");
        btnTim.setPreferredSize(new Dimension(80, 28));
        btnTim.setBackground(new Color(0, 122, 204));
        btnTim.setForeground(Color.WHITE);
        btnTim.setFocusPainted(false);

        JButton btnReset = new JButton("Đặt lại");
        btnReset.setPreferredSize(new Dimension(80, 28));
        btnReset.setBackground(new Color(180, 180, 180));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        pnlFilterTop.add(pnlDates, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        pnlFilterTop.add(new JLabel("Khách:"), gbc);
        gbc.gridx = 2;
        pnlFilterTop.add(btnGuestRoom, gbc);
        gbc.gridx = 3;
        pnlFilterTop.add(new JLabel("Mức:"), gbc);
        gbc.gridx = 4;
        pnlFilterTop.add(cbPriceTier, gbc);
        gbc.gridx = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlFilterTop.add(compactPrice, gbc);
        gbc.gridx = 6;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlFilterTop.add(btnTim, gbc);
        gbc.gridx = 7;
        pnlFilterTop.add(btnReset, gbc);

        pnlTop.add(pnlFilterTop, BorderLayout.CENTER);
        panel.add(pnlTop, BorderLayout.NORTH);

        JPanel pnlTang = new JPanel();
        pnlTang.setLayout(new BoxLayout(pnlTang, BoxLayout.Y_AXIS));
        pnlTang.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(pnlTang);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Danh sách phòng theo tầng"
        ));
        panel.add(scroll, BorderLayout.CENTER);

        List<RoomItem> dsPhongMau = taoDanhSachPhongMau();
        buildRoomByFloor(pnlTang, dsPhongMau);

        final boolean[] syncing = {false};
        ChangeListener sliderListener = e -> {
            if (syncing[0]) return;
            syncing[0] = true;
            try {
                int val = sliderPrice.getValue();
                txtPriceMax.setValue(val);
                Number minVal = (Number) txtPriceMin.getValue();
                if (minVal != null && val < minVal.intValue()) {
                    txtPriceMin.setValue(val);
                }
            } finally {
                syncing[0] = false;
            }
        };
        sliderPrice.addChangeListener(sliderListener);

        cbPriceTier.addActionListener(e -> {
            String selected = (String) cbPriceTier.getSelectedItem();
            if (selected == null) return;
            syncing[0] = true;
            try {
                switch (selected) {
                    case "Thấp" -> {
                        txtPriceMin.setValue(0);
                        txtPriceMax.setValue(500_000);
                        sliderPrice.setValue(500_000);
                    }
                    case "Trung bình" -> {
                        txtPriceMin.setValue(500_001);
                        txtPriceMax.setValue(2_000_000);
                        sliderPrice.setValue(2_000_000);
                    }
                    case "Cao" -> {
                        txtPriceMin.setValue(2_000_001);
                        txtPriceMax.setValue(PRICE_MAX);
                        sliderPrice.setValue(PRICE_MAX);
                    }
                    default -> {
                        txtPriceMin.setValue(PRICE_MIN);
                        txtPriceMax.setValue(PRICE_MAX);
                        sliderPrice.setValue(PRICE_MAX);
                    }
                }
            } finally {
                syncing[0] = false;
            }
        });

        JPanel pnlTreEmList = new JPanel();
        pnlTreEmList.setLayout(new BoxLayout(pnlTreEmList, BoxLayout.Y_AXIS));
        pnlTreEmList.setBackground(Color.WHITE);
        JScrollPane scrollTreEm = new JScrollPane(pnlTreEmList);
        scrollTreEm.setPreferredSize(new Dimension(280, 100));

        JPanel guestContent = new JPanel();
        guestContent.setLayout(new BoxLayout(guestContent, BoxLayout.Y_AXIS));
        guestContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        guestContent.setBackground(Color.WHITE);

        JPanel rowNguoiLon = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        rowNguoiLon.setBackground(Color.WHITE);
        JButton btnMinus = new JButton("-");
        JButton btnPlus = new JButton("+");
        JTextField txtNguoiLon = new JTextField(String.valueOf(nguoiLon[0]));
        txtNguoiLon.setEditable(false);
        txtNguoiLon.setHorizontalAlignment(JTextField.CENTER);
        txtNguoiLon.setPreferredSize(new Dimension(40, 28));
        rowNguoiLon.add(new JLabel("Người lớn"));
        rowNguoiLon.add(btnMinus);
        rowNguoiLon.add(txtNguoiLon);
        rowNguoiLon.add(btnPlus);

        JLabel lblTreEmTitle = new JLabel("Trẻ em (0-17 tuổi):");
        lblTreEmTitle.setBorder(new EmptyBorder(6, 0, 4, 0));
        JButton btnThemTreEm = new JButton("+ Thêm trẻ em");
        btnThemTreEm.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Color.WHITE);
        JButton btnApplyGuest = new JButton("Áp dụng");
        JButton btnCancelGuest = new JButton("Hủy");
        actions.add(btnCancelGuest);
        actions.add(btnApplyGuest);

        guestContent.add(rowNguoiLon);
        guestContent.add(lblTreEmTitle);
        guestContent.add(scrollTreEm);
        guestContent.add(btnThemTreEm);
        guestContent.add(Box.createVerticalStrut(8));
        guestContent.add(new JSeparator());
        guestContent.add(Box.createVerticalStrut(8));
        guestContent.add(actions);

        JPopupMenu popupGuest = new JPopupMenu();
        popupGuest.setLayout(new BorderLayout());
        popupGuest.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        popupGuest.add(guestContent, BorderLayout.CENTER);

        btnMinus.addActionListener(e -> {
            if (nguoiLon[0] > 1) {
                nguoiLon[0]--;
                txtNguoiLon.setText(String.valueOf(nguoiLon[0]));
            }
        });
        btnPlus.addActionListener(e -> {
            nguoiLon[0]++;
            txtNguoiLon.setText(String.valueOf(nguoiLon[0]));
        });

        btnThemTreEm.addActionListener(e -> {
            dsTreEm.add(6);
            capNhatDanhSachTreEm(pnlTreEmList, dsTreEm, popupGuest);
        });

        btnGuestRoom.addActionListener(e -> {
            txtNguoiLon.setText(String.valueOf(nguoiLon[0]));
            capNhatDanhSachTreEm(pnlTreEmList, dsTreEm, popupGuest);
            popupGuest.show(btnGuestRoom, 0, btnGuestRoom.getHeight());
            popupGuest.pack();
        });

        btnApplyGuest.addActionListener(e -> {
            btnGuestRoom.setText((nguoiLon[0] + dsTreEm.size()) + " khách");
            popupGuest.setVisible(false);
        });
        btnCancelGuest.addActionListener(e -> popupGuest.setVisible(false));

        btnTim.addActionListener(e -> {
            Date checkIn = (Date) spCheckIn.getValue();
            Date checkOut = (Date) spCheckOut.getValue();
            if (checkOut.before(checkIn)) {
                JOptionPane.showMessageDialog(this, "Ngày trả phải sau ngày nhận.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int minPrice = ((Number) txtPriceMin.getValue()).intValue();
            int maxPrice = ((Number) txtPriceMax.getValue()).intValue();
            int tongKhach = nguoiLon[0] + dsTreEm.size();

            List<RoomItem> filtered = new ArrayList<>();
            for (RoomItem room : dsPhongMau) {
                boolean giaHopLe = room.gia >= minPrice && room.gia <= maxPrice;
                boolean sucChuaHopLe = room.sucChua >= Math.max(1, tongKhach);
                if (giaHopLe && sucChuaHopLe) {
                    filtered.add(room);
                }
            }

            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phòng phù hợp với tiêu chí.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            buildRoomByFloor(pnlTang, filtered);
        });

        btnReset.addActionListener(e -> {
            txtPriceMin.setValue(PRICE_MIN);
            txtPriceMax.setValue(PRICE_MAX);
            sliderPrice.setValue(PRICE_MAX);
            cbPriceTier.setSelectedItem("Tất cả");
            nguoiLon[0] = 2;
            dsTreEm.clear();
            btnGuestRoom.setText("2 khách");
            buildRoomByFloor(pnlTang, dsPhongMau);
        });

        return panel;
    }

    private List<RoomItem> taoDanhSachPhongMau() {
        List<RoomItem> list = new ArrayList<>();
        list.add(new RoomItem("P101", 1, 2, 650_000, RoomState.TRONG));
        list.add(new RoomItem("P102", 1, 3, 850_000, RoomState.DANG_O));
        list.add(new RoomItem("P103", 1, 4, 1_150_000, RoomState.DA_DAT));
        list.add(new RoomItem("P104", 1, 2, 780_000, RoomState.TRONG));

        list.add(new RoomItem("P201", 2, 2, 700_000, RoomState.TRONG));
        list.add(new RoomItem("P202", 2, 4, 1_250_000, RoomState.DANG_O));
        list.add(new RoomItem("P203", 2, 3, 980_000, RoomState.TRONG));
        list.add(new RoomItem("P204", 2, 6, 1_850_000, RoomState.DA_DAT));

        list.add(new RoomItem("P301", 3, 2, 750_000, RoomState.TRONG));
        list.add(new RoomItem("P302", 3, 3, 990_000, RoomState.TRONG));
        list.add(new RoomItem("P303", 3, 4, 1_350_000, RoomState.DANG_O));
        list.add(new RoomItem("P304", 3, 6, 2_100_000, RoomState.TRONG));
        return list;
    }

    private void buildRoomByFloor(JPanel pnlTang, List<RoomItem> dsPhong) {
        pnlTang.removeAll();

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        int maxFloor = 0;
        for (RoomItem room : dsPhong) {
            if (room.tang > maxFloor) {
                maxFloor = room.tang;
            }
        }

        for (int tang = 1; tang <= maxFloor; tang++) {
            JPanel tungTang = new JPanel(new BorderLayout(10, 0));
            tungTang.setBackground(Color.WHITE);
            tungTang.setBorder(new CompoundBorder(
                    new LineBorder(new Color(235, 235, 235), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));

            JLabel lblTang = new JLabel("Tầng " + tang, SwingConstants.LEFT);
            lblTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTang.setPreferredSize(new Dimension(90, 46));
            tungTang.add(lblTang, BorderLayout.WEST);

            JPanel panelPhongTang = new JPanel(new GridLayout(0, 6, 10, 10));
            panelPhongTang.setBackground(new Color(248, 249, 250));

            for (RoomItem room : dsPhong) {
                if (room.tang != tang) {
                    continue;
                }

                String buttonText = "<html><center><b>" + room.maPhong + "</b><br>"
                        + currency.format(room.gia) + "<br>"
                        + room.sucChua + " khách</center></html>";

                JButton btn = new JButton(buttonText);
                btn.setFocusPainted(false);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setForeground(Color.WHITE);
                btn.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
                btn.setPreferredSize(new Dimension(90, 95));

                switch (room.tinhTrang) {
                    case TRONG -> btn.setBackground(new Color(0, 180, 0));
                    case DANG_O -> btn.setBackground(new Color(255, 170, 0));
                    case DA_DAT -> btn.setBackground(new Color(200, 0, 0));
                }

                btn.addActionListener(e -> JOptionPane.showMessageDialog(
                        this,
                        "Phòng " + room.maPhong + "\nGiá: " + currency.format(room.gia) + "\nSức chứa: " + room.sucChua + " khách",
                        "Thông tin phòng",
                        JOptionPane.INFORMATION_MESSAGE
                ));

                panelPhongTang.add(btn);
            }

            if (panelPhongTang.getComponentCount() > 0) {
                tungTang.add(panelPhongTang, BorderLayout.CENTER);
                pnlTang.add(tungTang);
                pnlTang.add(Box.createVerticalStrut(10));
            }
        }

        pnlTang.revalidate();
        pnlTang.repaint();
    }

    private void capNhatDanhSachTreEm(JPanel pnlList, List<Integer> dsTuoi, JPopupMenu popup) {
        pnlList.removeAll();

        for (int i = 0; i < dsTuoi.size(); i++) {
            final int index = i;
            JPanel pnlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            pnlRow.setBackground(Color.WHITE);

            JLabel lbl = new JLabel("Tuổi trẻ " + (i + 1) + ":");
            lbl.setPreferredSize(new Dimension(80, 24));

            int tuoiHienTai = dsTuoi.get(i);
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(tuoiHienTai, 0, 17, 1));
            spinner.setPreferredSize(new Dimension(60, 24));
            spinner.addChangeListener(e -> dsTuoi.set(index, (Integer) spinner.getValue()));

            JButton btnXoa = new JButton("Xóa");
            btnXoa.setMargin(new Insets(2, 5, 2, 5));
            btnXoa.addActionListener(e -> {
                dsTuoi.remove(index);
                capNhatDanhSachTreEm(pnlList, dsTuoi, popup);
            });

            pnlRow.add(lbl);
            pnlRow.add(spinner);
            pnlRow.add(btnXoa);
            pnlList.add(pnlRow);
        }

        pnlList.revalidate();
        pnlList.repaint();
        popup.pack();
    }

    private JPanel taoPlaceholderPanel(String title, String description) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 230, 240), 1, true),
                new EmptyBorder(24, 24, 24, 24)
        ));
        content.setPreferredSize(new Dimension(520, 220));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(33, 105, 170));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><div style='text-align:center;'>" + description + "</div></html>", SwingConstants.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDesc.setForeground(new Color(90, 90, 90));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(lblTitle);
        content.add(Box.createVerticalStrut(12));
        content.add(lblDesc);

        panel.add(content);
        return panel;
    }

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
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(pnl.getBackground());
                }
            });

            pnl.add(btn);
            pnl.add(Box.createVerticalStrut(5));
        }
        return pnl;
    }

    private void toggleSubMenu(JPanel subMenu) {
        if (subMenu != pnlPhong) pnlPhong.setVisible(false);
        if (subMenu != pnlThongKe) pnlThongKe.setVisible(false);
        if (subMenu != pnlKhuyenMai) pnlKhuyenMai.setVisible(false);
        if (subMenu != pnlDichVu) pnlDichVu.setVisible(false);
        if (subMenu != pnlKhachHang) pnlKhachHang.setVisible(false);
        if (subMenu != pnlNhanVien) pnlNhanVien.setVisible(false);
        if (subMenu != pnlCaLamViec) pnlCaLamViec.setVisible(false);

        subMenu.setVisible(!subMenu.isVisible());
        revalidate();
        repaint();
    }

    private JPanel trangThaiPhong(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panel.setBackground(Color.WHITE);
        JPanel o = new JPanel();
        o.setBackground(color);
        o.setPreferredSize(new Dimension(16, 16));
        o.setBorder(new LineBorder(Color.GRAY, 1));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(o);
        panel.add(lbl);
        return panel;
    }

    public void moDialogNhanCaTuDong() {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                this,
                "Chức năng Nhận/Bàn giao ca sẽ được kết nối trong bước tiếp theo.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        ));
    }

    private void startClock() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        timerDongHo = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            lblDongHo.setText("<html><center>" + now.format(timeFormatter) + "<br>" + now.format(dateFormatter) + "</center></html>");
        });
        timerDongHo.setInitialDelay(0);
        timerDongHo.start();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new TrangChu_Gui(new NhanVien());
        });
    }
}
