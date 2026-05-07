package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import connectDB.ConnectDB;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.text.DecimalFormat; // Thêm để định dạng tiền

import dao.PhieuDatPhong_DAO;
import dao.KhachHang_DAO;
import dao.Phong_DAO;
import dao.DichVu_DAO; // Thêm DAO Dịch Vụ
import entity.PhieuDatPhong;
import entity.KhachHang;
import entity.Phong;
import entity.LoaiKhachHang;
import entity.NhanVien; // Thêm Entity NhanVien
import entity.ChiTietDichVu; // Thêm Entity ChiTietDichVu
import entity.DichVu;
import entity.TrangThaiPhieuDat; // Thêm Enum TrangThaiPhieuDat

import java.time.LocalDate;
import java.time.ZoneId;
import javax.swing.DefaultComboBoxModel;

public class DatPhong_Gui extends JFrame {
    // Dữ liệu được truyền từ ChonPhong_Gui
    private List<String> danhSachMaPhong; // Danh sách mã phòng (String)
    private NhanVien nhanVien; // Nhân viên đang đăng nhập
    private ChonPhong_Gui parentFrame; // Frame cha (ChonPhong_Gui)

    // UI Components
    private JLabel lblHoTen, lblSdt, lblNgaySinh, lblLoaiKhach;
	private JTextField txtHoTen, txtSdt;
	private JSpinner spNgaySinh;
	private JComboBox<LoaiKhachHang> cboLoaiKhach;
	private JButton btnTimKiem;

	private JLabel lblNgayDat, lblNgayTra, lblSoNgayThue, lblDichVu, lblKhuyenMai;
	private JSpinner spNgayDat, spNgayTra;
	private JTextField txtSoNgayThue;
	private JComboBox<String> cboKhuyenMai; 

	private JTable tbl;
	private DefaultTableModel tableModel;
	private JButton btnLuu, btnTraPhong, btnCancel, btnThemDichVu;

	private JLabel lblTongTienPhong, lblTongTienDichVu, lblKhuyenMaiTien, lblTongThanhToan;
	private JTextField txtTongTienPhong, txtTongTienDichVu, txtKhuyenMaiTien, txtTongThanhToan;

    // DAO
    private PhieuDatPhong_DAO phieuDatPhongDAO;
    private KhachHang_DAO khachHangDAO;
    private Phong_DAO phongDAO;
    private DichVu_DAO dichVuDAO;

    // Dữ liệu tải từ CSDL
    private List<DichVu> danhSachDichVuTuDB; // List<DichVu>
    private Map<String, Double> bangGiaPhong; // Map<MaPhong, Gia>
    private Map<String, Phong> mapPhong; // Map<MaPhong, PhongObject>
    
    // =========================================================================
    // <<< NÂNG CẤP LƯU TRỮ DỊCH VỤ >>>
    // =========================================================================
    // Thay vì dựa vào JTable, chúng ta dùng Map làm "Source of Truth"
    // Key: maPhong (String), Value: Danh sách dịch vụ của phòng đó
    private Map<String, List<ChiTietDichVu>> servicesPerRoom;
    // =========================================================================


    /**
     * ✅ CONSTRUCTOR ĐÃ SỬA LẠI
     * Nhận danh sách mã phòng, nhân viên, frame cha, và ngày đã chọn
     */
    public DatPhong_Gui(List<String> danhSachMaPhong, NhanVien nhanVien, ChonPhong_Gui parent, Date ngayNhan, Date ngayTra) {
        this.danhSachMaPhong = danhSachMaPhong;
        this.nhanVien = nhanVien;
        this.parentFrame = parent;

        // Khởi tạo DAO
        this.phieuDatPhongDAO = new PhieuDatPhong_DAO();
        this.khachHangDAO = new KhachHang_DAO();
        this.phongDAO = new Phong_DAO();
        this.dichVuDAO = new DichVu_DAO();
        this.mapPhong = new HashMap<>();
        this.bangGiaPhong = new HashMap<>();
        
        // <<< NÂNG CẤP: Khởi tạo Map lưu dịch vụ >>>
        this.servicesPerRoom = new HashMap<>();
        for (String maPhong : danhSachMaPhong) {
            this.servicesPerRoom.put(maPhong, new ArrayList<>());
        }
        
        // (Lỗi: bangGiaDichVu chưa được khởi tạo)
        // -> Đã di chuyển khoiTaoDuLieu lên trước initGUI
        khoiTaoDuLieuDichVuVaPhong(); // 1. Tải dữ liệu
        initGUI(); // 2. Dựng giao diện (đã khởi tạo tableModel và listener)

        // 3. Set ngày (việc này sẽ kích hoạt listener, nhưng tableModel vẫn rỗng)
        spNgayDat.setValue(ngayNhan);
        spNgayTra.setValue(ngayTra);
        
        // 4. Tải danh sách phòng vào bảng
        loadPhongVaoTable();
        
        // 5. Tính tiền lại 1 lần sau khi bảng đã có dữ liệu
        capNhatTongTien();
    }

    // Tải giá dịch vụ và giá phòng
    private void khoiTaoDuLieuDichVuVaPhong() {
        try {
            // Tải dịch vụ
            danhSachDichVuTuDB = dichVuDAO.getAllDichVu();
            // (Không cần bangGiaDichVu Map nữa vì ta dùng List<DichVu>)

            // Tải thông tin phòng (bao gồm giá)
            bangGiaPhong.clear();
            mapPhong.clear();
            List<Phong> dsTatCaPhong = phongDAO.getDanhSachPhong();
            for (Phong p : dsTatCaPhong) {
                bangGiaPhong.put(p.getMaPhong(), p.getGiaPhong());
                mapPhong.put(p.getMaPhong(), p);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu phòng/dịch vụ!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Đổ danh sách phòng được chọn vào JTable
    private void loadPhongVaoTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        for (String maPhong : danhSachMaPhong) {
            Phong p = mapPhong.get(maPhong);
            if (p != null) {
                tableModel.addRow(new Object[]{
                    p.getMaPhong().substring(1, 2), // Lấy tầng
                    p.getMaPhong(),
                    p.getLoaiPhong().toString(), // "Đơn", "Đôi"...
                    "", // Dịch vụ (ban đầu rỗng)
                    0.0, // Phí DV (ban đầu 0, SỬA LẠI KIỂU DOUBLE)
                    "", // Thời gian
                    "" // Ghi chú
                });
            }
        }
    }

    private void initGUI() {
		setTitle("Phiếu đặt phòng - " + danhSachMaPhong.size() + " phòng");
		setSize(1400, 800);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setBackground(new Color(250, 250, 250));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLayout(new BorderLayout(10, 10));
        
        String[] cols = { "Tầng", "Số phòng", "Loại phòng", "Dịch vụ", "Phí DV", "Thời gian", "Ghi chú" };
		tableModel = new DefaultTableModel(cols, 0) {
            // <<< NÂNG CẤP: Cột Phí DV là kiểu Double >>>
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Double.class;
                }
                return String.class;
            }
            
            @Override
			public boolean isCellEditable(int row, int column) {
				return false; // Không cho sửa
			}
        };

		JLabel lblTieuDe = new JLabel("PHIẾU ĐẶT PHÒNG", SwingConstants.CENTER);
		lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 30));
		lblTieuDe.setForeground(new Color(25, 118, 210));
		lblTieuDe.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		add(lblTieuDe, BorderLayout.NORTH);

		JPanel pnlMainContainer = new JPanel(new BorderLayout(15, 15));
		pnlMainContainer.setBackground(new Color(250, 250, 250));
		pnlMainContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

		JPanel pnlContent = new JPanel();
		pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
		pnlContent.setBackground(Color.WHITE);
		pnlContent.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		JPanel pnlTinhTien = taoPanelTinhTien();
		pnlTinhTien.setPreferredSize(new Dimension(350, 0));

		pnlContent.add(taoPanelThongTinKhach());
		pnlContent.add(Box.createVerticalStrut(15));
		pnlContent.add(taoPanelThuePhong()); // Hàm này sẽ gọi capNhatTongTien()
		pnlContent.add(Box.createVerticalStrut(15));
		pnlContent.add(taoPanelBangPhong()); // Hàm này sử dụng tableModel
		pnlContent.add(Box.createVerticalStrut(15));

		pnlMainContainer.add(new JScrollPane(pnlContent), BorderLayout.CENTER);
		pnlMainContainer.add(pnlTinhTien, BorderLayout.EAST);

		add(pnlMainContainer, BorderLayout.CENTER);
		add(taoPanelNut(), BorderLayout.SOUTH);
	}

	private JPanel taoPanelTinhTien() {
        // ... (Giữ nguyên hàm này, không cần sửa) ...
		JPanel pnl = new JPanel();
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
		pnl.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 200, 200), 1, true),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)));
		pnl.setBackground(Color.WHITE);
		pnl.setPreferredSize(new Dimension(320, 600));

		JLabel lblTieuDeTinhTien = new JLabel("TÍNH TIỀN", SwingConstants.CENTER);
		lblTieuDeTinhTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTieuDeTinhTien.setForeground(new Color(25, 118, 210));
		lblTieuDeTinhTien.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTieuDeTinhTien.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

		JPanel pnlNoiDung = new JPanel();
		pnlNoiDung.setLayout(new GridLayout(0, 1, 8, 8));
		pnlNoiDung.setBackground(Color.WHITE);

		lblTongTienPhong = new JLabel("Tổng tiền phòng:");
		lblTongTienDichVu = new JLabel("Tổng tiền dịch vụ:");
		lblKhuyenMaiTien = new JLabel("Khuyến mãi:");
		lblTongThanhToan = new JLabel("TỔNG THANH TOÁN:");

		txtTongTienPhong = new JTextField("0 VND");
		txtTongTienDichVu = new JTextField("0 VND");
		txtKhuyenMaiTien = new JTextField("0 VND");
		txtTongThanhToan = new JTextField("0 VND");

		Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);
		Font fontTextField = new Font("Segoe UI", Font.BOLD, 14);
		Font fontTong = new Font("Segoe UI", Font.BOLD, 16);

		lblTongTienPhong.setFont(fontLabel);
		lblTongTienDichVu.setFont(fontLabel);
		lblKhuyenMaiTien.setFont(fontLabel);
		lblTongThanhToan.setFont(fontTong);

		txtTongTienPhong.setFont(fontTextField);
		txtTongTienDichVu.setFont(fontTextField);
		txtKhuyenMaiTien.setFont(fontTextField);
		txtTongThanhToan.setFont(fontTong);

		Color colorTien = new Color(0, 100, 0);
		txtTongTienPhong.setForeground(colorTien);
		txtTongTienDichVu.setForeground(colorTien);
		txtKhuyenMaiTien.setForeground(Color.RED);
		txtTongThanhToan.setForeground(new Color(200, 0, 0));

		txtTongTienPhong.setEditable(false);
		txtTongTienDichVu.setEditable(false);
		txtKhuyenMaiTien.setEditable(false);
		txtTongThanhToan.setEditable(false);

		txtTongTienPhong.setHorizontalAlignment(JTextField.RIGHT);
		txtTongTienDichVu.setHorizontalAlignment(JTextField.RIGHT);
		txtKhuyenMaiTien.setHorizontalAlignment(JTextField.RIGHT);
		txtTongThanhToan.setHorizontalAlignment(JTextField.RIGHT);

		txtTongThanhToan.setBackground(new Color(255, 250, 240));
		txtTongThanhToan.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(255, 165, 0), 2, true),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		pnlNoiDung.add(taoDongTien("Tổng tiền phòng:", txtTongTienPhong));
		pnlNoiDung.add(taoDongTien("Tổng tiền dịch vụ:", txtTongTienDichVu));
		pnlNoiDung.add(taoDongTien("Khuyến mãi:", txtKhuyenMaiTien));
		pnlNoiDung.add(new JSeparator());
		pnlNoiDung.add(taoDongTien("TỔNG THANH TOÁN:", txtTongThanhToan));

		pnl.add(lblTieuDeTinhTien);
		pnl.add(Box.createVerticalStrut(10));
		pnl.add(pnlNoiDung);
		pnl.add(Box.createVerticalGlue());

		return pnl;
	}

	private JPanel taoDongTien(String label, JTextField textField) {
        // ... (Giữ nguyên hàm này) ...
		JPanel pnl = new JPanel(new BorderLayout(10, 0));
		pnl.setBackground(Color.WHITE);
		JLabel lbl = new JLabel(label);
		lbl.setFont(textField == txtTongThanhToan ? new Font("Segoe UI", Font.BOLD, 14) : new Font("Segoe UI", Font.PLAIN, 14));
		pnl.add(lbl, BorderLayout.WEST);
		pnl.add(textField, BorderLayout.CENTER);
		return pnl;
	}
    
    private JPanel taoPanelThuePhong() {
        // ... (Giữ nguyên hàm này) ...
		JPanel pnl = new JPanel(new GridLayout(4, 2, 15, 10));
		pnl.setBorder(tieuDe("Thông tin thuê phòng"));
		pnl.setBackground(Color.WHITE);

		lblNgayDat = new JLabel("Ngày nhận:"); // Đổi tên
		lblNgayTra = new JLabel("Ngày trả:");
		lblSoNgayThue = new JLabel("Số ngày thuê:");
		lblKhuyenMai = new JLabel("Khuyến mãi:");

		lblNgayDat.setFont(f());
		lblNgayTra.setFont(f());
		lblSoNgayThue.setFont(f());
		lblKhuyenMai.setFont(f());

		spNgayDat = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
		spNgayDat.setEditor(new JSpinner.DateEditor(spNgayDat, "dd/MM/yyyy"));
		spNgayDat.setFont(f());

		spNgayTra = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
		spNgayTra.setEditor(new JSpinner.DateEditor(spNgayTra, "dd/MM/yyyy"));
		spNgayTra.setFont(f());

		txtSoNgayThue = new JTextField();
		txtSoNgayThue.setFont(f());
		txtSoNgayThue.setEditable(false);

		cboKhuyenMai = new JComboBox<>(new String[] { "Không có", "Giảm 10%", "Giảm 20%", "Voucher 200k" });
		cboKhuyenMai.setFont(f());

		pnl.add(lblNgayDat);
		pnl.add(spNgayDat);
		pnl.add(lblNgayTra);
		pnl.add(spNgayTra);
		pnl.add(lblSoNgayThue);
		pnl.add(txtSoNgayThue);
		pnl.add(lblKhuyenMai);
		pnl.add(cboKhuyenMai);

		javax.swing.event.ChangeListener capNhatSoNgay = e -> {
			Date ngayDat = (Date) spNgayDat.getValue();
			Date ngayTra = (Date) spNgayTra.getValue();
			long diff = ngayTra.getTime() - ngayDat.getTime();
			long soNgay = diff / (1000 * 60 * 60 * 24);
            if (soNgay < 1) soNgay = 1; // Mặc định ít nhất 1 ngày
			txtSoNgayThue.setText(String.valueOf(soNgay));
			capNhatTongTien();
		};

		spNgayDat.addChangeListener(capNhatSoNgay);
		spNgayTra.addChangeListener(capNhatSoNgay);
		cboKhuyenMai.addActionListener(e -> capNhatTongTien());
        
        // Gọi lần đầu để tính toán
        capNhatSoNgay.stateChanged(null); 
		return pnl;
	}
    
    private JPanel taoPanelThongTinKhach() {
        // ... (Giữ nguyên hàm này) ...
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(tieuDe("Thông tin khách hàng"));
        pnl.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 15);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 15);

        // ======= Khởi tạo các label =======
        lblHoTen = new JLabel("Họ tên:");
        lblSdt = new JLabel("Số điện thoại:");
        lblNgaySinh = new JLabel("Ngày sinh:");
        lblLoaiKhach = new JLabel("Loại khách hàng:");

        lblHoTen.setFont(fontLabel);
        lblSdt.setFont(fontLabel);
        lblNgaySinh.setFont(fontLabel);
        lblLoaiKhach.setFont(fontLabel);

        // ======= Khởi tạo các input =======
        txtHoTen = new JTextField();
        txtHoTen.setFont(fontInput);
        txtHoTen.setPreferredSize(new Dimension(200, 30));

        txtSdt = new JTextField();
        txtSdt.setFont(fontInput);
        txtSdt.setPreferredSize(new Dimension(200, 30));

        spNgaySinh = new JSpinner(new SpinnerDateModel());
        spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh, "dd/MM/yyyy"));
        spNgaySinh.setFont(fontInput);
        spNgaySinh.setPreferredSize(new Dimension(200, 30));

        cboLoaiKhach = new JComboBox<>();
        cboLoaiKhach.setModel(new DefaultComboBoxModel<>(LoaiKhachHang.values()));
        cboLoaiKhach.setFont(fontInput);
        cboLoaiKhach.setPreferredSize(new Dimension(200, 30));

        btnTimKiem = new JButton("Tìm SĐT");
        btnTimKiem.setFont(fontLabel);
        btnTimKiem.addActionListener(e -> timKhachTheoSDT());

        // ======= Bố trí layout =======
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnl.add(lblSdt, gbc);

        gbc.gridx = 1;
        pnl.add(txtSdt, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.5;
        pnl.add(btnTimKiem, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        pnl.add(lblHoTen, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        pnl.add(txtHoTen, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        pnl.add(lblNgaySinh, gbc);

        gbc.gridx = 1;
        pnl.add(spNgaySinh, gbc);

        gbc.gridx = 2;
        pnl.add(lblLoaiKhach, gbc);

        gbc.gridx = 3;
        pnl.add(cboLoaiKhach, gbc);

        return pnl;
    }
    
    private void timKhachTheoSDT() {
        // ... (Giữ nguyên hàm này) ...
        String sdt = txtSdt.getText().trim();
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT để tìm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        KhachHang kh = khachHangDAO.timKhachHangTheoSDT(sdt);
        if (kh != null) {
            txtHoTen.setText(kh.getHoTen());
            cboLoaiKhach.setSelectedItem(kh.getLoaiKhachHang());
            spNgaySinh.setValue(Date.from(kh.getNgaySinh().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            JOptionPane.showMessageDialog(this, "Đã tìm thấy khách hàng!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng. Vui lòng nhập thông tin mới.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            txtHoTen.setText("");
            cboLoaiKhach.setSelectedIndex(0);
        }
    }

	private JPanel taoPanelBangPhong() {
		JPanel pnl = new JPanel(new BorderLayout(5, 5));
		pnl.setBorder(tieuDe("Chi tiết phòng & dịch vụ"));
		pnl.setBackground(Color.WHITE);

        // tableModel đã được khởi tạo trong initGUI()
		tbl = new JTable(tableModel);
		tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tbl.setRowHeight(30);
		tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        // <<< NÂNG CẤP: Set độ rộng cột >>>
        tbl.getColumnModel().getColumn(0).setPreferredWidth(40); // Tầng
        tbl.getColumnModel().getColumn(1).setPreferredWidth(60); // Số phòng
        tbl.getColumnModel().getColumn(2).setPreferredWidth(80); // Loại phòng
        tbl.getColumnModel().getColumn(3).setPreferredWidth(300); // Dịch vụ
        tbl.getColumnModel().getColumn(4).setPreferredWidth(100); // Phí DV

		btnThemDichVu = taoNutIcon("➕ Thêm dịch vụ cho phòng", new Color(33, 150, 243));
		JButton btnThemDichVuAll = taoNutIcon("🌐 Thêm dịch vụ cho toàn bộ", new Color(0, 150, 136));

		Dimension sizeNut = new Dimension(340, 55);
		btnThemDichVu.setPreferredSize(sizeNut);
		btnThemDichVuAll.setPreferredSize(sizeNut);

		JPanel pnlThem = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
		pnlThem.setBackground(Color.WHITE);
		pnlThem.add(btnThemDichVu);
		pnlThem.add(btnThemDichVuAll);

		pnl.add(pnlThem, BorderLayout.NORTH);
		pnl.add(new JScrollPane(tbl), BorderLayout.CENTER);

        // <<< NÂNG CẤP: GỌI HÀM MỚI >>>
		btnThemDichVu.addActionListener(e -> xuLyThemDichVuVaoPhong());
		btnThemDichVuAll.addActionListener(e -> xuLyThemDichVuToanBo());

		return pnl;
	}

	// Trong DatPhong_Gui.java
	private JPanel taoPanelNut() {
	    JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
	    pnl.setBackground(Color.WHITE);
	    pnl.setBorder(new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

	    // <<< THAY ĐỔI TÊN NÚT VÀ THÊM NÚT MỚI >>>
	    
	    // Nút 1: Lưu và chờ (logic cũ)
	    btnLuu = taoNutIcon("Lưu (Chờ nhận)", new Color(0, 122, 204)); // Đổi màu
	    
	    // Nút 2: Lưu và thanh toán (logic mới)
	    JButton btnLuuVaThanhToan = taoNutIcon("Lưu & Thanh toán ngay", new Color(46, 204, 113));
	    
	    btnCancel = taoNutIcon("Thoát", new Color(244, 67, 54));
	    
	    // (Bỏ nút btnTraPhong vì nó là "Hủy tạo phiếu", trùng với "Thoát")

	    // <<< THÊM SỰ KIỆN MỚI >>>
	    btnLuu.addActionListener(e -> xuLyLuuPhieu(false)); // false = không thanh toán
	    btnLuuVaThanhToan.addActionListener(e -> xuLyLuuPhieu(true)); // true = có thanh toán
	    btnCancel.addActionListener(e -> dispose());

	    pnl.add(btnLuu);
	    pnl.add(btnLuuVaThanhToan);
	    pnl.add(btnCancel);
	    return pnl;
	}

	private TitledBorder tieuDe(String text) {
        // ... (Giữ nguyên hàm này) ...
		TitledBorder border = BorderFactory.createTitledBorder(new LineBorder(new Color(200, 200, 200), 1, true), text);
		border.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
		border.setTitleColor(new Color(25, 118, 210));
		return border;
	}

	private Font f() {
        // ... (Giữ nguyên hàm này) ...
		return new Font("Segoe UI", Font.PLAIN, 14);
	}

	private JButton taoNutIcon(String text, Color color) {
        // ... (Giữ nguyên hàm này) ...
		JButton btn = new JButton(text);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btn.setFocusPainted(false);
		btn.setForeground(Color.WHITE);
		btn.setBackground(color);
		btn.setBorderPainted(false);
		btn.setOpaque(true);
		btn.setPreferredSize(new Dimension(220, 45));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
			@Override
			public void paint(Graphics g, JComponent c) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(btn.getBackground());
				g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
				super.paint(g2, c);
				g2.dispose();
			}
		});
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btn.setBackground(color.darker());
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				btn.setBackground(color);
			}
		});
		return btn;
	}
    
    // =========================================================================
    // <<< NÂNG CẤP HOÀN TOÀN LOGIC THÊM DỊCH VỤ >>>
    // =========================================================================

    /**
     * NÂNG CẤP: Mở Dialog chọn dịch vụ và xử lý cho 1 PHÒNG
     */
    private void xuLyThemDichVuVaoPhong() {
        int selectedRow = tbl.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng trong bảng!", "Chưa chọn phòng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maPhong = (String) tableModel.getValueAt(selectedRow, 1);
        
        // 1. Mở Dialog
        ChonDichVu_Dialog dialog = new ChonDichVu_Dialog(this, this.danhSachDichVuTuDB);
        dialog.setVisible(true);

        // 2. Lấy kết quả (chỉ chạy khi dialog đã đóng)
        Map<DichVu, Integer> selectedServices = dialog.getSelectedServices();
        if (selectedServices.isEmpty()) {
            return; // Người dùng không chọn gì
        }
        
        // 3. Lấy danh sách dịch vụ HIỆN TẠI của phòng này từ Map
        List<ChiTietDichVu> dsDVCuaPhong = servicesPerRoom.get(maPhong);
        
        // 4. MERGE (Gộp) dịch vụ mới vào danh sách cũ
        //    Đây là logic then chốt để CHỐNG TRÙNG LẶP
        for (Map.Entry<DichVu, Integer> entry : selectedServices.entrySet()) {
            DichVu dvMoi = entry.getKey();
            int soLuongThem = entry.getValue();
            
            // Tìm xem dịch vụ này đã có trong list chưa
            ChiTietDichVu dvTonTai = null;
            for (ChiTietDichVu ctdv : dsDVCuaPhong) {
                if (ctdv.getDichVu().equals(dvMoi)) {
                    dvTonTai = ctdv;
                    break;
                }
            }
            
            if (dvTonTai != null) {
                // ĐÃ CÓ: Chỉ cập nhật số lượng
                dvTonTai.setSoLuong(dvTonTai.getSoLuong() + soLuongThem);
            } else {
                // CHƯA CÓ: Tạo mới ChiTietDichVu và thêm vào list
                Phong phong = mapPhong.get(maPhong);
                // Gán phiếu tạm (sẽ được thay bằng phiếu thật khi lưu)
                PhieuDatPhong pdpFake = new PhieuDatPhong("PDP_TAM"); 
                
                ChiTietDichVu ctdvMoi = new ChiTietDichVu(pdpFake, phong, dvMoi, soLuongThem, "");
                dsDVCuaPhong.add(ctdvMoi);
            }
        }
        
        // 5. Cập nhật lại JTable (chỉ hàng này)
        capNhatHangTrongBang(selectedRow, dsDVCuaPhong);
        
        // 6. Cập nhật tổng tiền
        capNhatTongTien();
    }
    
    /**
     * NÂNG CẤP: Mở Dialog chọn dịch vụ và xử lý cho TOÀN BỘ phòng
     */
    private void xuLyThemDichVuToanBo() {
        if (tableModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Không có phòng nào để thêm dịch vụ!", "Danh sách trống", JOptionPane.WARNING_MESSAGE);
			return;
		}

        // 1. Mở Dialog
        ChonDichVu_Dialog dialog = new ChonDichVu_Dialog(this, this.danhSachDichVuTuDB);
        dialog.setVisible(true);

        // 2. Lấy kết quả
        Map<DichVu, Integer> selectedServices = dialog.getSelectedServices();
        if (selectedServices.isEmpty()) {
            return;
        }

        // 3. Lặp qua TẤT CẢ các hàng trong bảng
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String maPhong = (String) tableModel.getValueAt(row, 1);
            List<ChiTietDichVu> dsDVCuaPhong = servicesPerRoom.get(maPhong);
            
            // 4. MERGE dịch vụ mới vào từng phòng
            for (Map.Entry<DichVu, Integer> entry : selectedServices.entrySet()) {
                DichVu dvMoi = entry.getKey();
                int soLuongThem = entry.getValue();
                
                ChiTietDichVu dvTonTai = null;
                for (ChiTietDichVu ctdv : dsDVCuaPhong) {
                    if (ctdv.getDichVu().equals(dvMoi)) {
                        dvTonTai = ctdv;
                        break;
                    }
                }
                
                if (dvTonTai != null) {
                    dvTonTai.setSoLuong(dvTonTai.getSoLuong() + soLuongThem);
                } else {
                    Phong phong = mapPhong.get(maPhong);
                    PhieuDatPhong pdpFake = new PhieuDatPhong("PDP_TAM");
                    ChiTietDichVu ctdvMoi = new ChiTietDichVu(pdpFake, phong, dvMoi, soLuongThem, "");
                    dsDVCuaPhong.add(ctdvMoi);
                }
            }
            
            // 5. Cập nhật lại JTable (hàng hiện tại)
            capNhatHangTrongBang(row, dsDVCuaPhong);
        }
        
        // 6. Cập nhật tổng tiền (chỉ 1 lần sau khi xong)
        capNhatTongTien();
    }
    
    /**
     * HÀM HELPER MỚI: Cập nhật nội dung 1 hàng JTable từ List<ChiTietDichVu>
     */
    private void capNhatHangTrongBang(int row, List<ChiTietDichVu> dsDichVu) {
        if (dsDichVu == null || dsDichVu.isEmpty()) {
            tableModel.setValueAt("", row, 3);
            tableModel.setValueAt(0.0, row, 4);
            return;
        }
        
        StringBuilder tenDVBuilder = new StringBuilder();
        double tongPhiDV = 0.0;
        
        for (ChiTietDichVu ctdv : dsDichVu) {
            String ten = ctdv.getDichVu().getTenDichVu();
            int sl = ctdv.getSoLuong();
            double gia = ctdv.getDichVu().getGiaTien();
            
            tenDVBuilder.append(String.format("%s (x%d), ", ten, sl));
            tongPhiDV += (gia * sl);
        }
        
        // Xóa dấu phẩy cuối cùng
        String tenDVFinal = tenDVBuilder.substring(0, tenDVBuilder.length() - 2);
        
        tableModel.setValueAt(tenDVFinal, row, 3);
        tableModel.setValueAt(tongPhiDV, row, 4);
    }
    
    
    private void capNhatTongTien() {
        try {
            double tongTienPhong = 0.0;
            double tongTienDichVu = 0.0;
            double khuyenMaiTien = 0.0;
            double tongThanhToan = 0.0;
            
            DecimalFormat df = new DecimalFormat("#,##0");

            int soNgayThue = 1;
            try {
                soNgayThue = Integer.parseInt(txtSoNgayThue.getText());
                if (soNgayThue <= 0) {
                    soNgayThue = 1;
                }
            } catch (NumberFormatException e) {
                soNgayThue = 1;
            }

            // <<< NÂNG CẤP: Lấy tiền từ JTable (đã được cập nhật bởi hàm mới) >>>
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String maPhong = tableModel.getValueAt(i, 1).toString().trim();
                Double giaPhong = bangGiaPhong.get(maPhong);
                if (giaPhong != null) {
                    tongTienPhong += giaPhong;
                }

                // Lấy Phí DV (đã là kiểu Double)
                Object phiDVObj = tableModel.getValueAt(i, 4);
                if (phiDVObj instanceof Double) {
                    tongTienDichVu += (Double) phiDVObj;
                }
            }

            tongTienPhong *= soNgayThue;
            tongTienDichVu *= soNgayThue; // Dịch vụ cũng tính theo ngày

            String khuyenMai = cboKhuyenMai.getSelectedItem().toString();
            double tongTienTruocKM = tongTienPhong + tongTienDichVu;

            if (khuyenMai.contains("10%")) {
                khuyenMaiTien = tongTienTruocKM * 0.10;
            } else if (khuyenMai.contains("20%")) {
                khuyenMaiTien = tongTienTruocKM * 0.20;
            } else if (khuyenMai.contains("200k")) {
                khuyenMaiTien = 200_000;
            }

            tongThanhToan = tongTienTruocKM - khuyenMaiTien;
            if (tongThanhToan < 0) {
                tongThanhToan = 0;
            }

            txtTongTienPhong.setText(df.format(tongTienPhong) + " VND");
            txtTongTienDichVu.setText(df.format(tongTienDichVu) + " VND");
            txtKhuyenMaiTien.setText(df.format(khuyenMaiTien) + " VND");
            txtTongThanhToan.setText(df.format(tongThanhToan) + " VND");

        } catch (Exception e) {
            e.printStackTrace();
            txtTongTienPhong.setText("0 VND");
            txtTongTienDichVu.setText("0 VND");
            txtKhuyenMaiTien.setText("0 VND");
            txtTongThanhToan.setText("0 VND");
        }
    }


    /**
     * ✅ SỬA LẠI HOÀN TOÀN:
     * Logic lưu phiếu theo đúng CSDL N-N (1 Phiếu - N Phòng - N Dịch vụ)
     */
 // Trong DatPhong_Gui.java

    /**
     * Hàm xử lý chính, được gọi bởi cả 2 nút Lưu.
     * @param coThanhToan Ngay (true) hay không (false)
     */
 // Trong DatPhong_Gui.java

    /**
     * Hàm xử lý chính, được gọi bởi cả 2 nút Lưu.
     * @param coThanhToan Ngay (true) hay không (false)
     */
    private void xuLyLuuPhieu(boolean coThanhToan) {
        try {
            // 1. Lấy thông tin chung từ GUI (Giữ nguyên)
            String tenKhach = txtHoTen.getText().trim();
            String sdt = txtSdt.getText().trim();
            
            if (tenKhach.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin khách hàng (SĐT và Tên)!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LoaiKhachHang loaiKH = (LoaiKhachHang) cboLoaiKhach.getSelectedItem();
            Date utilNgaySinh = (Date) spNgaySinh.getValue();
            LocalDate ngaySinh = utilNgaySinh.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Date utilNgayNhan = (Date) spNgayDat.getValue();
            Date utilNgayTra = (Date) spNgayTra.getValue();
            LocalDate ngayNhan = utilNgayNhan.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate ngayTra = utilNgayTra.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (ngayTra.isBefore(ngayNhan) || ngayTra.isEqual(ngayNhan)) {
                 JOptionPane.showMessageDialog(this, "Ngày trả phải sau Ngày nhận!", "Lỗi ngày", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Xử lý Khách hàng (Tìm hoặc Tạo mới) (Giữ nguyên)
            KhachHang kh = khachHangDAO.timKhachHangTheoSDT(sdt);
            if (kh == null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Khách hàng mới. Bạn có muốn tạo mới khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                
                kh = new KhachHang();
                kh.setMaKhachHang(khachHangDAO.generateMaKH());
                kh.setHoTen(tenKhach);
                kh.setSoDienThoai(sdt);
                kh.setNgaySinh(ngaySinh);
                kh.setLoaiKhachHang(loaiKH);
                
                if (!khachHangDAO.create(kh)) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tạo khách hàng mới!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // 3. Khởi tạo PhieuDatPhong và các danh sách (Giữ nguyên)
            PhieuDatPhong pdp = new PhieuDatPhong();
            String maPhieuMoi = phieuDatPhongDAO.generateMaPhieu(); 
            pdp.setMaPhieu(maPhieuMoi); 
            pdp.setKhachHang(kh);
            pdp.setNhanVien(this.nhanVien); // <-- Dùng nhân viên đăng nhập
            pdp.setNgayDat(LocalDate.now()); 
            pdp.setNgayNhan(ngayNhan);
            pdp.setNgayTra(ngayTra);
            double tongTien = parseTien(txtTongThanhToan.getText());
            pdp.setTongTien(tongTien); 

            List<Phong> dsPhongDat = new ArrayList<>();
            for (String maPhong : this.danhSachMaPhong) {
                Phong p = mapPhong.get(maPhong);
                if (p != null) dsPhongDat.add(p);
            }
            
            List<ChiTietDichVu> dsDichVuDat = new ArrayList<>();
            for (List<ChiTietDichVu> dsCua1Phong : servicesPerRoom.values()) {
                for (ChiTietDichVu ctdv : dsCua1Phong) {
                    ctdv.setPhieuDatPhong(pdp); 
                    dsDichVuDat.add(ctdv);
                }
            }

            // 4. Xử lý thanh toán hoặc không (Giữ nguyên)
            List<entity.ChiTietThanhToan> dsThanhToan = new ArrayList<>();
            TrangThaiPhieuDat trangThaiCuoi;
            
            if (coThanhToan) {
                // (Mô phỏng thanh toán, giữ nguyên)
                int confirmThanhToan = JOptionPane.showConfirmDialog(this, 
                    String.format("Xác nhận thanh toán trả trước: %,.0f VND?", tongTien), 
                    "Xác nhận thanh toán", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirmThanhToan != JOptionPane.YES_OPTION) {
                     JOptionPane.showMessageDialog(this, "Thanh toán đã bị hủy. Phiếu chưa được lưu.", "Hủy", JOptionPane.INFORMATION_MESSAGE);
                     return;
                }
                entity.HinhThucThanhToan httt = new entity.HinhThucThanhToan("TM", "Tiền mặt", 0); 
                entity.ChiTietThanhToan cttt = new entity.ChiTietThanhToan(null, httt, tongTien, "Thanh toán trả trước");
                dsThanhToan.add(cttt);
                
                trangThaiCuoi = TrangThaiPhieuDat.DA_THANH_TOAN_TRUOC;
            } else {
                trangThaiCuoi = TrangThaiPhieuDat.CHO_NHAN_PHONG;
            }
            
            pdp.setTrangThai(trangThaiCuoi);

            // 5. GỌI TRANSACTION ĐỂ LƯU
            entity.HoaDon hoaDon = null;
            if (coThanhToan) {
                hoaDon = new entity.HoaDon();
                hoaDon.setMaHoaDon(phieuDatPhongDAO.generateMaHoaDon()); 
                hoaDon.setNgayLap(LocalDate.now());
                hoaDon.setThueVAT(0.1); 
                hoaDon.setTongTien(tongTien);
                
                // <<< THAY ĐỔI 1: Gán toàn bộ đối tượng PhieuDatPhong >>>
                hoaDon.setPhieuDatPhong(pdp); 
                
                // <<< THAY ĐỔI 2: Gán toàn bộ đối tượng NhanVien (từ đăng nhập) >>>
                hoaDon.setNhanVien(this.nhanVien);
                
                // <<< THAY ĐỔI 3: Gán toàn bộ đối tượng KhachHang >>>
                hoaDon.setKhachHang(kh);

                for(entity.ChiTietThanhToan cttt : dsThanhToan) {
                    cttt.setHoaDon(hoaDon); // Gán đối tượng Hóa đơn
                }
            }
            
            // Gọi DAO (Hàm DAO đã đúng, không cần sửa)
            boolean success = phieuDatPhongDAO.taoPhieuDatVaThanhToan(pdp, dsPhongDat, dsDichVuDat, hoaDon, dsThanhToan);

            // 6. Thông báo kết quả (Giữ nguyên)
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Đã lưu thành công Phiếu Đặt Phòng: " + pdp.getMaPhieu(), "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                if (parentFrame != null) {
                    parentFrame.refreshData();
                }
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi trong quá trình lưu Transaction.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng khi lưu phiếu đặt: \n" + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper để parse tiền từ "1,000,000 VND" -> 1000000.0
    private double parseTien(String tienStr) {
        try {
            // Loại bỏ " VND" và ","
            return Double.parseDouble(tienStr.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }
    
	private void xuLyHuyPhieu() {
        // ... (Giữ nguyên hàm này) ...
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy tạo phiếu đặt phòng này không?",
				"Xác nhận hủy", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			dispose(); // Chỉ cần đóng cửa sổ
		}
	}
}