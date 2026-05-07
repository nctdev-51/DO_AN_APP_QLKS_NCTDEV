package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

import dao.HoaDon_DAO;
import entity.HoaDon;

public class QuanLyThongKeDoanhThu extends JPanel {
	private HoaDon_DAO hoaDonDAO;
	private DefaultTableModel modelTable;
	private JComboBox<String> cboQuy, cboNhanVien;
	private JComboBox<Integer> cboNam;
	private JDateChooser dcTuNgay, dcDenNgay;
	private JButton btnThongKe, btnViewBang, btnViewBieuDo;
	private CardLayout cardLayout;
	private JTabbedPane tabbedPane;
	
	// Label thống kê cho từng tab
	private JLabel lblTongDoanhThu, lblTongSoHoaDon, lblDoanhThuTB;
	private JLabel lblTongDoanhThuDV, lblTongSoDichVu, lblDoanhThuDVTB;
	private JLabel lblTongDoanhThuPhong, lblTongSoPhong, lblDoanhThuPhongTB;

	public QuanLyThongKeDoanhThu() {



		setLayout(new BorderLayout());

		hoaDonDAO = new HoaDon_DAO();

		// --- Header ---
		JLabel lblTieuDe = new JLabel("QUẢN LÝ THỐNG KÊ DOANH THU", SwingConstants.CENTER);
		lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
		lblTieuDe.setForeground(Color.WHITE);
		lblTieuDe.setOpaque(true);
		lblTieuDe.setBackground(new Color(70, 130, 180));
		lblTieuDe.setPreferredSize(new Dimension(getWidth(), 70));
		add(lblTieuDe, BorderLayout.NORTH);

		// --- Menu (ẩn đi khi nhúng vào TrangChu) ---
		// JPanel pnlMenu = createMenuPanel();
		// add(pnlMenu, BorderLayout.WEST);

		// --- Panel chính ---
		JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
		pnlMain.setBackground(Color.WHITE);
		pnlMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// --- Panel nội dung với Tab ---
		tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

		// Tab 1: Tổng doanh thu
		JPanel pnlTabTongDoanhThu = createTabTongDoanhThu();
		tabbedPane.addTab("Tổng doanh thu", pnlTabTongDoanhThu);

		// Tab 2: Doanh thu dịch vụ
		JPanel pnlTabDichVu = createTabDichVu();
		tabbedPane.addTab("Doanh thu dịch vụ", pnlTabDichVu);

		// Tab 3: Doanh thu phòng
		JPanel pnlTabPhong = createTabPhong();
		tabbedPane.addTab("Doanh thu phòng", pnlTabPhong);

		pnlMain.add(tabbedPane, BorderLayout.CENTER);

		add(pnlMain, BorderLayout.CENTER);

		setVisible(true);
	}

	// Tạo Tab 1: Tổng doanh thu (Bộ lọc + Thống kê + Danh sách)
	private JPanel createTabTongDoanhThu() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(Color.WHITE);

		// --- Panel phía trên (lọc + thống kê) ---
		JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
		pnlTop.setBackground(Color.WHITE);

		// Panel bộ lọc
		JPanel pnlFilter = createPanelFilter();
		pnlTop.add(pnlFilter, BorderLayout.NORTH);

		// Panel thống kê tổng quan - Tổng doanh thu
		JPanel pnlThongKeTong = createPanelThongKeTong();
		pnlTop.add(pnlThongKeTong, BorderLayout.CENTER);

		panel.add(pnlTop, BorderLayout.NORTH);

		// --- Panel nội dung (CardLayout) ---
		cardLayout = new CardLayout();
		JPanel pnlContent = new JPanel(cardLayout);
		pnlContent.setBackground(Color.WHITE);

		// Panel bảng tổng hóa đơn
		JPanel pnlBangTong = createPanelTable("Danh sách hóa đơn", new String[]{"Mã HĐ", "Ngày lập", "Khách hàng", 
			"Mã phòng", "Nhân viên", "Dịch vụ", "Tiền phòng", "Tổng tiền", "Thuế VAT", "Ghi chú"});
		pnlContent.add(pnlBangTong, "BANG");

		// Panel biểu đồ
		JPanel pnlBieuDo = createPanelBieuDo("Biểu đồ doanh thu");
		pnlContent.add(pnlBieuDo, "BIEUDOM");

		panel.add(pnlContent, BorderLayout.CENTER);

		return panel;
	}

	// Tạo Tab 2: Doanh thu dịch vụ (Bộ lọc + Thống kê + Danh sách)
	private JPanel createTabDichVu() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(Color.WHITE);

		// --- Panel phía trên (lọc + thống kê) ---
		JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
		pnlTop.setBackground(Color.WHITE);

		// Panel bộ lọc
		JPanel pnlFilter = createPanelFilter();
		pnlTop.add(pnlFilter, BorderLayout.NORTH);

		// Panel thống kê tổng quan - Dịch vụ
		JPanel pnlThongKeDV = createPanelThongKeDichVu();
		pnlTop.add(pnlThongKeDV, BorderLayout.CENTER);

		panel.add(pnlTop, BorderLayout.NORTH);

		// --- Panel danh sách dịch vụ ---
		JPanel pnlTableDV = createPanelTable("Danh sách dịch vụ", new String[]{"Mã HĐ", "Ngày lập", "Khách hàng", 
			"Mã phòng", "Nhân viên", "Dịch vụ", "Tổng tiền dịch vụ"});
		panel.add(pnlTableDV, BorderLayout.CENTER);

		return panel;
	}

	// Tạo Tab 3: Doanh thu phòng (Bộ lọc + Thống kê + Danh sách)
	private JPanel createTabPhong() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(Color.WHITE);

		// --- Panel phía trên (lọc + thống kê) ---
		JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
		pnlTop.setBackground(Color.WHITE);

		// Panel bộ lọc
		JPanel pnlFilter = createPanelFilter();
		pnlTop.add(pnlFilter, BorderLayout.NORTH);

		// Panel thống kê tổng quan - Phòng
		JPanel pnlThongKePhong = createPanelThongKePhong();
		pnlTop.add(pnlThongKePhong, BorderLayout.CENTER);

		panel.add(pnlTop, BorderLayout.NORTH);

		// --- Panel danh sách phòng ---
		JPanel pnlTablePhong = createPanelTable("Danh sách phòng", new String[]{"Mã HĐ", "Ngày lập", "Khách hàng", 
			"Mã phòng", "Nhân viên", "Tiền phòng", "Tổng tiền phòng", "Ghi chú"});
		panel.add(pnlTablePhong, BorderLayout.CENTER);

		return panel;
	}

	// Tạo menu
	private JPanel createMenuPanel() {
		JPanel pnlMenu = new JPanel();
		pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
		pnlMenu.setBackground(new Color(70, 130, 180));
		pnlMenu.setPreferredSize(new Dimension(240, getHeight()));

		JLabel lblTen = new JLabel(" MENU", SwingConstants.CENTER);
		lblTen.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTen.setForeground(Color.WHITE);
		lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);

		pnlMenu.add(Box.createVerticalStrut(25));
		pnlMenu.add(lblTen);
		pnlMenu.add(Box.createVerticalStrut(20));

		// Nút menu
		JButton btnTrangChinh = new JButton("Trang chủ");
		JButton btnPhong = new JButton("Phòng");
		btnThongKe = new JButton("Thống kê");
		JButton btnKhuyenMai = new JButton("Khuyến mãi");
		JButton btnDichVu = new JButton("Dịch vụ");
		JButton btnKhachHang = new JButton("Khách hàng");
		JButton btnNhanVien = new JButton("Nhân viên");

		JButton[] buttons = { btnTrangChinh, btnPhong, btnThongKe, btnKhuyenMai, btnDichVu, btnKhachHang, btnNhanVien };

		for (JButton btn : buttons) {
			styleMenuButton(btn);
			pnlMenu.add(btn);
			pnlMenu.add(Box.createVerticalStrut(8));
		}

		// Nút Đăng xuất
		JButton btnDangXuat = new JButton("Đăng xuất");
		btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnDangXuat.setBackground(new Color(255, 77, 77));
		btnDangXuat.setForeground(Color.WHITE);
		btnDangXuat.setFocusPainted(false);
		btnDangXuat.setBorder(new LineBorder(new Color(255, 77, 77), 2, true));
		btnDangXuat.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDangXuat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		btnDangXuat.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnDangXuat.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnDangXuat.setBackground(new Color(220, 20, 60));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnDangXuat.setBackground(new Color(255, 77, 77));
			}
		});

		pnlMenu.add(Box.createVerticalStrut(30));
		pnlMenu.add(btnDangXuat);
		pnlMenu.add(Box.createVerticalGlue());

		return pnlMenu;
	}

	// Style nút menu
	private void styleMenuButton(JButton btn) {
		btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btn.setBackground(new Color(30, 60, 90));
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(new Color(50, 90, 140));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(new Color(30, 60, 90));
			}
		});
	}

	// Tạo panel bộ lọc (Chung cho tất cả tab)
	private JPanel createPanelFilter() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1, 10, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new TitledBorder(new LineBorder(new Color(200, 200, 200), 1), "Bộ lọc doanh thu",
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

		// Row 1: Lọc theo quý/năm (Bên trái) | Nhân viên + Thống kê toàn bộ (Bên phải)
		JPanel pnlRow1 = new JPanel(new BorderLayout(15, 5));
		pnlRow1.setBackground(Color.WHITE);

		// Bên trái: Lọc theo quý/năm
		JPanel pnlQuyNam = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		pnlQuyNam.setBackground(Color.WHITE);

		JLabel lblQuy = new JLabel("Quý:");
		lblQuy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		cboQuy = new JComboBox<>();
		cboQuy.addItem("Toàn bộ quý");
		cboQuy.addItem("Quý 1");
		cboQuy.addItem("Quý 2");
		cboQuy.addItem("Quý 3");
		cboQuy.addItem("Quý 4");
		cboQuy.setPreferredSize(new Dimension(120, 35));

		JLabel lblNam = new JLabel("Năm:");
		lblNam.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		cboNam = new JComboBox<>();
		for (int i = 2020; i <= LocalDate.now().getYear(); i++) {
			cboNam.addItem(i);
		}
		cboNam.setSelectedItem(LocalDate.now().getYear());
		cboNam.setPreferredSize(new Dimension(100, 35));

		JButton btnLocQuy = new JButton("Lọc quý");
		btnLocQuy.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnLocQuy.setBackground(new Color(70, 130, 180));
		btnLocQuy.setForeground(Color.WHITE);
		btnLocQuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLocQuy.setFocusPainted(false);
		btnLocQuy.setPreferredSize(new Dimension(90, 35));
		btnLocQuy.addActionListener(e -> locTheoQuy());

		pnlQuyNam.add(lblQuy);
		pnlQuyNam.add(cboQuy);
		pnlQuyNam.add(lblNam);
		pnlQuyNam.add(cboNam);
		pnlQuyNam.add(btnLocQuy);

		// Bên phải: Nhân viên và Thống kê toàn bộ
		JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
		pnlRight.setBackground(Color.WHITE);

		JLabel lblNhanVien = new JLabel("Nhân viên:");
		lblNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		cboNhanVien = new JComboBox<>();
		cboNhanVien.addItem("Tất cả");
		cboNhanVien.setPreferredSize(new Dimension(120, 35));

		JButton btnLocNhanVien = new JButton("Lọc nhân viên");
		btnLocNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnLocNhanVien.setBackground(new Color(156, 39, 176));
		btnLocNhanVien.setForeground(Color.WHITE);
		btnLocNhanVien.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLocNhanVien.setFocusPainted(false);
		btnLocNhanVien.setPreferredSize(new Dimension(130, 35));
		btnLocNhanVien.addActionListener(e -> locTheoNhanVien());

		pnlRight.add(lblNhanVien);
		pnlRight.add(cboNhanVien);
		pnlRight.add(btnLocNhanVien);

		pnlRow1.add(pnlQuyNam, BorderLayout.WEST);
		pnlRow1.add(pnlRight, BorderLayout.EAST);

		// Row 2: Lọc theo khoảng ngày
		JPanel pnlKhoangNgay = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		pnlKhoangNgay.setBackground(Color.WHITE);

		JLabel lblTuNgay = new JLabel("Từ ngày:");
		lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		dcTuNgay = new JDateChooser();
		dcTuNgay.setDateFormatString("dd/MM/yyyy");
		dcTuNgay.setPreferredSize(new Dimension(115, 35));
		dcTuNgay.setDate(java.sql.Date.valueOf(LocalDate.now().withDayOfMonth(1)));

		JLabel lblDenNgay = new JLabel("Đến:");
		lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		dcDenNgay = new JDateChooser();
		dcDenNgay.setDateFormatString("dd/MM/yyyy");
		dcDenNgay.setPreferredSize(new Dimension(115, 35));
		dcDenNgay.setDate(java.sql.Date.valueOf(LocalDate.now()));

		JButton btnLocKhoang = new JButton("Lọc khoảng");
		btnLocKhoang.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnLocKhoang.setBackground(new Color(70, 130, 180));
		btnLocKhoang.setForeground(Color.WHITE);
		btnLocKhoang.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLocKhoang.setFocusPainted(false);
		btnLocKhoang.setPreferredSize(new Dimension(90, 35));
		btnLocKhoang.addActionListener(e -> locTheoKhoangNgay());

		pnlKhoangNgay.add(lblTuNgay);
		pnlKhoangNgay.add(dcTuNgay);
		pnlKhoangNgay.add(lblDenNgay);
		pnlKhoangNgay.add(dcDenNgay);
		pnlKhoangNgay.add(btnLocKhoang);

		// Row 3: Hình thức hiển thị (Bên trái) | Xuất Excel + In báo cáo (Bên phải)
		JPanel pnlRow3 = new JPanel(new BorderLayout(15, 5));
		pnlRow3.setBackground(Color.WHITE);

		// Bên trái: Hình thức hiển thị
		JPanel pnlViewMode = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		pnlViewMode.setBackground(Color.WHITE);

		JLabel lblView = new JLabel("Hiển thị:");
		lblView.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		btnViewBang = new JButton("Bảng");
		btnViewBang.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnViewBang.setBackground(new Color(13, 110, 253));
		btnViewBang.setForeground(Color.WHITE);
		btnViewBang.setFocusPainted(false);
		btnViewBang.setPreferredSize(new Dimension(100, 35));
		btnViewBang.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnViewBieuDo = new JButton("Biểu đồ");
		btnViewBieuDo.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnViewBieuDo.setBackground(new Color(33, 150, 243));
		btnViewBieuDo.setForeground(Color.WHITE);
		btnViewBieuDo.setFocusPainted(false);
		btnViewBieuDo.setPreferredSize(new Dimension(100, 35));
		btnViewBieuDo.setCursor(new Cursor(Cursor.HAND_CURSOR));

		pnlViewMode.add(lblView);
		pnlViewMode.add(btnViewBang);
		pnlViewMode.add(btnViewBieuDo);

		// Bên phải: Xuất Excel, In báo cáo
		JPanel pnlExportPrint = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
		pnlExportPrint.setBackground(Color.WHITE);

		JButton btnXuatExcel = new JButton("Xuất Excel");
		btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnXuatExcel.setBackground(new Color(76, 175, 80));
		btnXuatExcel.setForeground(Color.WHITE);
		btnXuatExcel.setFocusPainted(false);
		btnXuatExcel.setPreferredSize(new Dimension(110, 35));
		btnXuatExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnXuatExcel.addActionListener(e -> xuatExcel());

		JButton btnInBaoCao = new JButton("In báo cáo");
		btnInBaoCao.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnInBaoCao.setBackground(new Color(33, 150, 243));
		btnInBaoCao.setForeground(Color.WHITE);
		btnInBaoCao.setFocusPainted(false);
		btnInBaoCao.setPreferredSize(new Dimension(110, 35));
		btnInBaoCao.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnInBaoCao.addActionListener(e -> inBaoCao());
		
		JButton btnThongKeToanBo = new JButton("Thống kê toàn bộ");
		btnThongKeToanBo.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnThongKeToanBo.setBackground(new Color(76, 175, 80));
		btnThongKeToanBo.setForeground(Color.WHITE);
		btnThongKeToanBo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnThongKeToanBo.setFocusPainted(false);
		btnThongKeToanBo.setPreferredSize(new Dimension(130, 35));
		btnThongKeToanBo.addActionListener(e -> thongKeToanBo());

		pnlExportPrint.add(btnXuatExcel);
		pnlExportPrint.add(btnInBaoCao);
		pnlExportPrint.add(btnThongKeToanBo);

		pnlRow3.add(pnlViewMode, BorderLayout.WEST);
		pnlRow3.add(pnlExportPrint, BorderLayout.EAST);

		panel.add(pnlRow1);
		panel.add(pnlKhoangNgay);
		panel.add(pnlRow3);

		return panel;
	}

	// Tạo panel thống kê tổng quan - Tổng doanh thu
	private JPanel createPanelThongKeTong() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3, 15, 0));
		panel.setBackground(Color.WHITE);
		panel.setPreferredSize(new Dimension(0, 100));
		panel.setBorder(new TitledBorder(new LineBorder(new Color(200, 200, 200), 1), "Thống kê tổng quan",
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

		// Tổng doanh thu
		JPanel pnlDoanhThu = taoOThongKe(new Color(76, 175, 80), "Tổng doanh thu", "0 VND");
		lblTongDoanhThu = (JLabel) pnlDoanhThu.getComponent(1);
		panel.add(pnlDoanhThu);

		// Số hóa đơn
		JPanel pnlSoHoaDon = taoOThongKe(new Color(33, 150, 243), "Số hóa đơn", "0");
		lblTongSoHoaDon = (JLabel) pnlSoHoaDon.getComponent(1);
		panel.add(pnlSoHoaDon);

		// Doanh thu trung bình
		JPanel pnlDoanhThuTB = taoOThongKe(new Color(255, 193, 7), "Doanh thu TB/Hóa đơn", "0 VND");
		lblDoanhThuTB = (JLabel) pnlDoanhThuTB.getComponent(1);
		panel.add(pnlDoanhThuTB);

		return panel;
	}

	// Tạo panel thống kê - Doanh thu dịch vụ
	private JPanel createPanelThongKeDichVu() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3, 15, 0));
		panel.setBackground(Color.WHITE);
		panel.setPreferredSize(new Dimension(0, 100));
		panel.setBorder(new TitledBorder(new LineBorder(new Color(200, 200, 200), 1), "Thống kê dịch vụ",
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

		// Tổng doanh thu dịch vụ
		JPanel pnlDoanhThuDV = taoOThongKe(new Color(76, 175, 80), "Tổng doanh thu dịch vụ", "0 VND");
		lblTongDoanhThuDV = (JLabel) pnlDoanhThuDV.getComponent(1);
		panel.add(pnlDoanhThuDV);

		// Tổng số dịch vụ
		JPanel pnlSoDichVu = taoOThongKe(new Color(33, 150, 243), "Tổng số dịch vụ", "0");
		lblTongSoDichVu = (JLabel) pnlSoDichVu.getComponent(1);
		panel.add(pnlSoDichVu);

		// Doanh thu dịch vụ TB
		JPanel pnlDoanhThuDVTB = taoOThongKe(new Color(255, 193, 7), "Doanh thu DV TB/Hóa đơn", "0 VND");
		lblDoanhThuDVTB = (JLabel) pnlDoanhThuDVTB.getComponent(1);
		panel.add(pnlDoanhThuDVTB);

		return panel;
	}

	// Tạo panel thống kê - Doanh thu phòng
	private JPanel createPanelThongKePhong() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3, 15, 0));
		panel.setBackground(Color.WHITE);
		panel.setPreferredSize(new Dimension(0, 100));
		panel.setBorder(new TitledBorder(new LineBorder(new Color(200, 200, 200), 1), "Thống kê phòng",
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

		// Tổng doanh thu phòng
		JPanel pnlDoanhThuPhong = taoOThongKe(new Color(76, 175, 80), "Tổng doanh thu phòng", "0 VND");
		lblTongDoanhThuPhong = (JLabel) pnlDoanhThuPhong.getComponent(1);
		panel.add(pnlDoanhThuPhong);

		// Tổng số phòng
		JPanel pnlSoPhong = taoOThongKe(new Color(33, 150, 243), "Tổng số phòng", "0");
		lblTongSoPhong = (JLabel) pnlSoPhong.getComponent(1);
		panel.add(pnlSoPhong);

		// Doanh thu phòng TB
		JPanel pnlDoanhThuPhongTB = taoOThongKe(new Color(255, 193, 7), "Doanh thu Phòng TB/Hóa đơn", "0 VND");
		lblDoanhThuPhongTB = (JLabel) pnlDoanhThuPhongTB.getComponent(1);
		panel.add(pnlDoanhThuPhongTB);

		return panel;
	}

	// Tạo ô thống kê
	private JPanel taoOThongKe(Color color, String tieuDe, String giaTri) {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(new CompoundBorder(BorderFactory.createLineBorder(color, 2),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JLabel lblTieuDe = new JLabel(tieuDe);
		lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblTieuDe.setForeground(color);

		JLabel lblGiaTri = new JLabel(giaTri);
		lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblGiaTri.setForeground(color);

		panel.add(lblTieuDe, BorderLayout.NORTH);
		panel.add(lblGiaTri, BorderLayout.CENTER);

		return panel;
	}


	// Tạo panel danh sách (Bảng)
	private JPanel createPanelTable(String title, String[] columnNames) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(new TitledBorder(new LineBorder(new Color(200, 200, 200), 1), title,
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

		// Bảng hóa đơn (Read-only)
		modelTable = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Không thể chỉnh sửa
			}
		};
		
		JTable tblHoaDon = new JTable(modelTable);
		tblHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		tblHoaDon.setRowHeight(25);
		tblHoaDon.setGridColor(new Color(200, 200, 200));
		tblHoaDon.setSelectionBackground(new Color(70, 130, 180));
		tblHoaDon.setSelectionForeground(Color.WHITE);
		tblHoaDon.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		// Header table - Cho phép di chuyển cột
		tblHoaDon.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		tblHoaDon.getTableHeader().setBackground(new Color(70, 130, 180));
		tblHoaDon.getTableHeader().setForeground(Color.WHITE);
		tblHoaDon.getTableHeader().setPreferredSize(new Dimension(0, 30));
		tblHoaDon.getTableHeader().setReorderingAllowed(true); // Cho phép di chuyển cột

		JScrollPane scroll = new JScrollPane(tblHoaDon);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setUnitIncrement(5);

		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	// Tạo panel biểu đồ cột
	private JPanel createPanelBieuDo(String title) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(new TitledBorder(new LineBorder(new Color(200, 200, 200), 1), title,
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

		// Tạo placeholder cho biểu đồ
		JPanel pnlChart = new JPanel();
		pnlChart.setBackground(new Color(240, 240, 240));
		pnlChart.setLayout(new BorderLayout());

		JLabel lblChart = new JLabel("Biểu đồ cột sẽ được hiển thị ở đây\n(Sử dụng thư viện JFreeChart hoặc tương tự)", 
									 SwingConstants.CENTER);
		lblChart.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		lblChart.setForeground(new Color(100, 100, 100));
		pnlChart.add(lblChart, BorderLayout.CENTER);

		panel.add(pnlChart, BorderLayout.CENTER);

		return panel;
	}

	// Lọc theo quý
	private void locTheoQuy() {
		String quy = (String) cboQuy.getSelectedItem();
		Integer nam = (Integer) cboNam.getSelectedItem();
		JOptionPane.showMessageDialog(this, "Lọc theo: " + quy + " - Năm " + nam, "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
		// Sẽ cập nhật dữ liệu sau
	}

	// Lọc theo khoảng ngày
	private void locTheoKhoangNgay() {
		java.util.Date tuNgay = dcTuNgay.getDate();
		java.util.Date denNgay = dcDenNgay.getDate();
		
		if (tuNgay == null || denNgay == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ ngày", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		JOptionPane.showMessageDialog(this, "Lọc từ ngày: " + tuNgay + "\nĐến ngày: " + denNgay, "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
		// Sẽ cập nhật dữ liệu sau
	}

	// Lọc theo nhân viên
	private void locTheoNhanVien() {
		String nhanVien = (String) cboNhanVien.getSelectedItem();
		JOptionPane.showMessageDialog(this, "Lọc theo nhân viên: " + nhanVien, "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
		// Sẽ cập nhật dữ liệu sau
	}

	// Thống kê toàn bộ - Làm mới tất cả bộ lọc
	private void thongKeToanBo() {
		cboQuy.setSelectedIndex(0); // Chọn "Toàn bộ quý"
		cboNhanVien.setSelectedIndex(0); // Chọn "Tất cả"
		dcTuNgay.setDate(java.sql.Date.valueOf(LocalDate.of(2020, 1, 1)));
		dcDenNgay.setDate(java.sql.Date.valueOf(LocalDate.now()));
		
		JOptionPane.showMessageDialog(this, "Đã làm mới tất cả bộ lọc - Thống kê toàn bộ dữ liệu", "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
		// Sẽ cập nhật dữ liệu sau
	}

	// Xuất Excel
	private void xuatExcel() {
		JOptionPane.showMessageDialog(this, "Chức năng xuất Excel sẽ được triển khai sau!", "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// In báo cáo
	private void inBaoCao() {
		JOptionPane.showMessageDialog(this, "Chức năng in báo cáo sẽ được triển khai sau!", "Thông báo",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new QuanLyThongKeDoanhThu().setVisible(true));
	}
}