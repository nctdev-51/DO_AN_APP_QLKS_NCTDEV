package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
// <<< THÊM 2 IMPORT NÀY >>>
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dao.LichSuCaLamViec_DAO;
import entity.LichSuCaLamViec;
import entity.NhanVien;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class NhanCaBanGiao_Dialog extends JDialog {

    private LichSuCaLamViec_DAO ls_dao;
    private NhanVien nhanVien;
    private LichSuCaLamViec caHienTai;

    private JLabel lblNhanVien, lblCa, lblNgay, lblThoiGianNhan, lblTrangThai;
    
    private TienMatPanel pnlTienDauCa;
    private TienMatPanel pnlTienCuoiCa;
    
    private JTextField txtChi, txtThuPhong, txtThuDichVu; 
    private JButton btnNhanCa, btnBanGiao;
    private JPanel pnlNhanCa, pnlBanGiao, pnlCenter; 
    
    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DecimalFormat MONEY_FORMATTER = new DecimalFormat("#,##0");
    
    // <<< SỬA: Đưa lblTienDauCaReadOnly_BanGiao ra làm biến toàn cục >>>
    private JLabel lblTienDauCaReadOnly_BanGiao; // (Đã đổi tên)
    
    private JLabel lblTongThuDuKien;
    private JLabel lblTongTienHeThong; 
    private JLabel lblChenhLech; 

    public NhanCaBanGiao_Dialog(Frame owner, NhanVien nhanVien) {
        super(owner, "Nhận / Bàn giao ca", true);
        this.nhanVien = nhanVien;
        this.ls_dao = new LichSuCaLamViec_DAO();

        setSize(550, 750); 
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel Thông tin (Không thay đổi)
        JPanel pnlInfo = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Thông tin ca làm việc"));
        pnlInfo.add(new JLabel("Nhân viên:"));
        lblNhanVien = new JLabel(nhanVien.getHoTen());
        pnlInfo.add(lblNhanVien);
        pnlInfo.add(new JLabel("Ngày làm:"));
        lblNgay = new JLabel("...");
        pnlInfo.add(lblNgay);
        pnlInfo.add(new JLabel("Ca trực:"));
        lblCa = new JLabel("...");
        pnlInfo.add(lblCa);
        pnlInfo.add(new JLabel("Trạng thái:"));
        lblTrangThai = new JLabel("...");
        lblTrangThai.setFont(lblTrangThai.getFont().deriveFont(Font.BOLD));
        pnlInfo.add(lblTrangThai);
        pnlInfo.add(new JLabel("Thời gian nhận ca:"));
        lblThoiGianNhan = new JLabel("...");
        pnlInfo.add(lblThoiGianNhan);
        add(pnlInfo, BorderLayout.NORTH);
        
        pnlCenter = new JPanel(new CardLayout());
        
        // --- Panel Nhận Ca (SỬ DỤNG TienMatPanel) ---
        pnlNhanCa = new JPanel(new BorderLayout(10, 10));
        pnlTienDauCa = new TienMatPanel("Tiền mặt đầu ca (nhập hoặc bấm nút)");
        btnNhanCa = new JButton("XÁC NHẬN NHẬN CA");
        btnNhanCa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnNhanCa.setBackground(new Color(46, 125, 50));
        btnNhanCa.setForeground(Color.WHITE);
        
        pnlNhanCa.add(pnlTienDauCa, BorderLayout.CENTER);
        pnlNhanCa.add(btnNhanCa, BorderLayout.SOUTH);
        
        
        // --- Panel Bàn Giao Ca (SỬ DỤNG TienMatPanel) ---
        pnlBanGiao = new JPanel(new BorderLayout(10, 10));
        
        // Panel trên: Doanh thu và Chi
        JPanel pnlDoanhThu = new JPanel(new GridBagLayout());
        pnlDoanhThu.setBorder(BorderFactory.createTitledBorder("Doanh thu & Chi tiêu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontMoney = new Font("Segoe UI", Font.BOLD, 14);

        // Hàng 0: Tiền mặt đầu ca (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lbl1 = new JLabel("1. Tiền mặt đầu ca (đã nhận):");
        lbl1.setFont(fontLabel);
        pnlDoanhThu.add(lbl1, gbc);
        
        gbc.gridx = 1;
        // <<< SỬA: Gán vào biến toàn cục, bỏ khai báo "JLabel" >>>
        lblTienDauCaReadOnly_BanGiao = new JLabel("0 VND");
        lblTienDauCaReadOnly_BanGiao.setFont(fontMoney);
        pnlDoanhThu.add(lblTienDauCaReadOnly_BanGiao, gbc);

        // Hàng 1: Thu phòng (auto)
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lbl2 = new JLabel("2. Tổng thu phòng (hệ thống):");
        lbl2.setFont(fontLabel);
        pnlDoanhThu.add(lbl2, gbc);
        gbc.gridx = 1;
        txtThuPhong = new JTextField("0"); 
        txtThuPhong.setFont(fontMoney);
        txtThuPhong.setEditable(false);
        txtThuPhong.setBackground(new Color(240, 240, 240));
        pnlDoanhThu.add(txtThuPhong, gbc);

        // Hàng 2: Thu dịch vụ (auto)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lbl3 = new JLabel("3. Tổng thu dịch vụ (hệ thống):");
        lbl3.setFont(fontLabel);
        pnlDoanhThu.add(lbl3, gbc);
        gbc.gridx = 1;
        txtThuDichVu = new JTextField("0");
        txtThuDichVu.setFont(fontMoney);
        txtThuDichVu.setEditable(false);
        txtThuDichVu.setBackground(new Color(240, 240, 240));
        pnlDoanhThu.add(txtThuDichVu, gbc);
        
        // Hàng 3: Tổng chi (nhập tay)
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lbl5 = new JLabel("4. Tổng chi (nhập tay):");
        lbl5.setFont(fontLabel);
        pnlDoanhThu.add(lbl5, gbc);
        gbc.gridx = 1;
        txtChi = new JTextField("0");
        txtChi.setFont(fontMoney);
        pnlDoanhThu.add(txtChi, gbc);
        
        // Panel dưới: Kiểm đếm tiền cuối ca
        pnlTienCuoiCa = new TienMatPanel("Tiền mặt cuối ca (đếm thực tế)");
        
        // Panel Chênh Lệch
        JPanel pnlKetQua = new JPanel(new GridLayout(0, 2, 10, 5));
        pnlKetQua.setBackground(Color.WHITE);
        pnlKetQua.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        lblTongTienHeThong = new JLabel("Dự kiến: 0 VND");
        lblTongTienHeThong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongTienHeThong.setForeground(new Color(0, 128, 0));
        
        lblChenhLech = new JLabel("Chênh lệch: 0 VND");
        lblChenhLech.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChenhLech.setForeground(Color.BLACK);

        pnlKetQua.add(lblTongTienHeThong);
        pnlKetQua.add(lblChenhLech);
        
        // Ghép Panel Bàn Giao
        JPanel pnlBanGiaoCenter = new JPanel();
        pnlBanGiaoCenter.setLayout(new BoxLayout(pnlBanGiaoCenter, BoxLayout.Y_AXIS));
        pnlBanGiaoCenter.add(pnlDoanhThu);
        pnlBanGiaoCenter.add(Box.createVerticalStrut(10));
        pnlBanGiaoCenter.add(pnlTienCuoiCa);
        pnlBanGiaoCenter.add(pnlKetQua);
        
        btnBanGiao = new JButton("XÁC NHẬN BÀN GIAO CA");
        btnBanGiao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBanGiao.setBackground(Color.RED);
        btnBanGiao.setForeground(Color.WHITE);
        
        pnlBanGiao.add(pnlBanGiaoCenter, BorderLayout.CENTER);
        pnlBanGiao.add(btnBanGiao, BorderLayout.SOUTH);
        
        // Thêm vào CardLayout
        pnlCenter.add(new JPanel(), "EMPTY"); // Panel rỗng
        pnlCenter.add(pnlNhanCa, "NHAN_CA");
        pnlCenter.add(pnlBanGiao, "BAN_GIAO");
        add(pnlCenter, BorderLayout.CENTER);

        // Kiểm tra trạng thái
        kiemTraTrangThaiCa();
        
        // Gán sự kiện
        btnNhanCa.addActionListener(e -> thucHienNhanCa());
        btnBanGiao.addActionListener(e -> thucHienBanGiao());
        
        // Thêm Listener để tự động tính toán
        
        // <<< SỬA: Lỗi 1 (Visibility) >>>
        // Dùng hàm public mới (addTongTienChangeListener) thay vì truy cập private field
        pnlTienCuoiCa.addTongTienChangeListener(e -> capNhatTinhToanBanGiao());
        
        // <<< Lỗi 2 (Imports) đã được sửa bằng cách thêm import ở đầu file >>>
        txtChi.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { capNhatTinhToanBanGiao(); }
            public void removeUpdate(DocumentEvent e) { capNhatTinhToanBanGiao(); }
            public void insertUpdate(DocumentEvent e) { capNhatTinhToanBanGiao(); }
        });
    }

    private void kiemTraTrangThaiCa() {
        caHienTai = ls_dao.getCaLamViecHienTai(nhanVien.getMaNhanVien());
        CardLayout cl = (CardLayout) (pnlCenter.getLayout());

        if (caHienTai == null) {
            lblNgay.setText("Hôm nay");
            lblCa.setText("Không có ca được phân công");
            lblTrangThai.setText("Không có ca");
            lblTrangThai.setForeground(Color.RED);
            cl.show(pnlCenter, "EMPTY");
        } else {
            lblNgay.setText(caHienTai.getNgayLamViec().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            lblCa.setText(caHienTai.getCaLamViec().toString());
            
            if (caHienTai.getThoiGianNhanCa() == null) {
                // Ca hôm nay, CHƯA NHẬN
                lblTrangThai.setText("Chưa nhận ca");
                lblTrangThai.setForeground(Color.BLUE);
                lblThoiGianNhan.setText("Chưa nhận");
                cl.show(pnlCenter, "NHAN_CA");
            } else {
                // ĐÃ NHẬN, CHƯA BÀN GIAO
                lblTrangThai.setText("Đang làm việc");
                lblTrangThai.setForeground(new Color(0, 128, 0));
                lblThoiGianNhan.setText(caHienTai.getThoiGianNhanCa().format(DATETIME_FORMATTER));
                
                // CẬP NHẬT TỰ ĐỘNG
                double tienDauCa = caHienTai.getTienMatDauCa();
                
                // <<< SỬA: Lỗi 3 (getComponent) >>>
                // Truy cập trực tiếp vào biến toàn cục đã khai báo
                lblTienDauCaReadOnly_BanGiao.setText(MONEY_FORMATTER.format(tienDauCa));
                
                double thuPhong = ls_dao.getTongThuPhongTrongCa(nhanVien.getMaNhanVien(), caHienTai.getThoiGianNhanCa());
                double thuDV = ls_dao.getTongThuDichVuTrongCa(nhanVien.getMaNhanVien(), caHienTai.getThoiGianNhanCa());
                
                txtThuPhong.setText(MONEY_FORMATTER.format(thuPhong));
                txtThuDichVu.setText(MONEY_FORMATTER.format(thuDV));
                
                capNhatTinhToanBanGiao();
                
                cl.show(pnlCenter, "BAN_GIAO");
            }
        }
    }
    
    // (Bỏ hàm moBangKiemDem)
    
    /**
     * Tự động cập nhật các label tính toán
     */
    private void capNhatTinhToanBanGiao() {
        if (caHienTai == null) return; 
        
        double tienDauCa = caHienTai.getTienMatDauCa();
        double thuPhong = parseDouble(txtThuPhong.getText(), "Thu phòng");
        double thuDV = parseDouble(txtThuDichVu.getText(), "Thu dịch vụ");
        double chi = parseDouble(txtChi.getText(), "Chi");
        double tienCuoiCa = pnlTienCuoiCa.getTien(); // Lấy từ panel
        
        if (thuPhong < 0) thuPhong = 0;
        if (thuDV < 0) thuDV = 0;
        if (chi < 0) chi = 0;

        double tongThu = thuPhong + thuDV;
        double tongHeThong = (tienDauCa + tongThu) - chi;
        double chenhLech = tienCuoiCa - tongHeThong;

        // lblTongThuDuKien không tồn tại, bỏ qua
        lblTongTienHeThong.setText("Dự kiến: " + MONEY_FORMATTER.format(tongHeThong));
        lblChenhLech.setText("Chênh lệch: " + MONEY_FORMATTER.format(chenhLech));
        
        if (chenhLech == 0) {
            lblChenhLech.setForeground(new Color(0, 128, 0)); // Xanh
        } else {
            lblChenhLech.setForeground(Color.RED); // Đỏ
        }
    }
    
    private double parseDouble(String text, String fieldName) {
        try {
            return Double.parseDouble(
                text.replace(",", "")
                    .replace(".", "")
                    .replace(" VND", "")
                    .trim()
            );
        } catch (NumberFormatException e) {
            return 0; 
        }
    }

    private void thucHienNhanCa() {
        double tienDauCa = pnlTienDauCa.getTien(); 
        
        if (tienDauCa < 0) {
            JOptionPane.showMessageDialog(this, "Tiền đầu ca không hợp lệ!");
            return;
        }
        
        if (ls_dao.nhanCa(caHienTai.getMaLichSu(), tienDauCa)) {
            JOptionPane.showMessageDialog(this, "Nhận ca thành công!");
            kiemTraTrangThaiCa(); 
        } else {
            JOptionPane.showMessageDialog(this, "Nhận ca thất bại!");
        }
    }

    private void thucHienBanGiao() {
        capNhatTinhToanBanGiao();
        
        double thuPhong = parseDouble(txtThuPhong.getText(), "Thu phòng");
        double thuDV = parseDouble(txtThuDichVu.getText(), "Thu dịch vụ");
        double chi = parseDouble(txtChi.getText(), "Chi");
        double tienCuoiCa = pnlTienCuoiCa.getTien();
        double chenhLech = parseDouble(lblChenhLech.getText().replace("Chênh lệch: ", ""), "Chênh lệch");
        
        if (chenhLech != 0) {
            if (JOptionPane.showConfirmDialog(this, 
                "Phát hiện chênh lệch " + MONEY_FORMATTER.format(chenhLech) + ".\n" +
                "Bạn có chắc muốn tiếp tục bàn giao?",
                "Cảnh báo chênh lệch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        
        caHienTai.setTongThuPhong(thuPhong);
        caHienTai.setTongThuDichVu(thuDV);
        caHienTai.setTongChi(chi);
        caHienTai.setTienMatCuoiCa(tienCuoiCa);
        
        if (ls_dao.banGiaoCa(caHienTai)) {
            JOptionPane.showMessageDialog(this, "Bàn giao ca thành công!");
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Bàn giao ca thất bại!");
        }
    }
}