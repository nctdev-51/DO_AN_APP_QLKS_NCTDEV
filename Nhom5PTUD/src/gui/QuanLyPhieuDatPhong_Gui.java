package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import dao.PhieuDatPhong_DAO;
import entity.ChiTietDichVu;
import entity.PhieuDatPhong;
import entity.Phong;
import entity.TrangThaiPhieuDat;

public class QuanLyPhieuDatPhong_Gui extends JPanel {

    // Bỏ JTable, Model
    private PhieuDatPhong_DAO pdp_DAO;

    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;
    private JTextField txtTimKhachHang;
    
    // Panel chính chứa danh sách các card
    private JPanel pnlDanhSachPhieu;
    private JScrollPane scrollDanhSach;

    public QuanLyPhieuDatPhong_Gui() {
        pdp_DAO = new PhieuDatPhong_DAO();
        
        setLayout(new BorderLayout(0, 10)); // 0px ngang, 10px dọc
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250)); // Màu nền xám nhạt

        // Panel tìm kiếm (NORTH)
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlTimKiem.setBackground(Color.WHITE);
        pnlTimKiem.setBorder(BorderFactory.createTitledBorder("Tìm kiếm và lọc phiếu"));
        
        pnlTimKiem.add(new JLabel("Từ ngày nhận:"));
        dateTuNgay = new JDateChooser();
        dateTuNgay.setPreferredSize(new Dimension(130, 25));
        pnlTimKiem.add(dateTuNgay);

        pnlTimKiem.add(new JLabel("Đến ngày nhận:"));
        dateDenNgay = new JDateChooser();
        dateDenNgay.setPreferredSize(new Dimension(130, 25));
        pnlTimKiem.add(dateDenNgay);

        pnlTimKiem.add(new JLabel("SĐT Khách hàng:"));
        txtTimKhachHang = new JTextField(15);
        txtTimKhachHang.setPreferredSize(new Dimension(150, 25));
        pnlTimKiem.add(txtTimKhachHang);
        
        JButton btnTim = new JButton("Tìm");
        JButton btnLamMoi = new JButton("Làm mới DS");
        pnlTimKiem.add(btnTim);
        pnlTimKiem.add(btnLamMoi);

        add(pnlTimKiem, BorderLayout.NORTH);

        // Panel chứa danh sách phiếu (CENTER)
        pnlDanhSachPhieu = new JPanel();
        pnlDanhSachPhieu.setLayout(new BoxLayout(pnlDanhSachPhieu, BoxLayout.Y_AXIS)); // Sắp xếp theo chiều dọc
        pnlDanhSachPhieu.setBackground(new Color(245, 247, 250)); // Màu nền

        // Thêm một panel đệm để các card không bị kéo dãn
        JPanel pnlListContainer = new JPanel(new BorderLayout());
        pnlListContainer.setBackground(new Color(245, 247, 250));
        pnlListContainer.add(pnlDanhSachPhieu, BorderLayout.NORTH);

        scrollDanhSach = new JScrollPane(pnlListContainer);
        scrollDanhSach.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu đặt phòng"));
        
        add(scrollDanhSach, BorderLayout.CENTER);

        // Bỏ pnlChucNang (SOUTH) vì các nút đã nằm trên card

        // Nạp dữ liệu
        loadDataToList();

        // Xử lý sự kiện
        btnLamMoi.addActionListener(e -> {
            txtTimKhachHang.setText("");
            dateTuNgay.setDate(null);
            dateDenNgay.setDate(null);
            loadDataToList();
        });
        
        btnTim.addActionListener(e -> loadDataToList());

        // Tự động tải lại dữ liệu mỗi khi panel được hiển thị
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadDataToList();
            }
        });
    }

    /**
     * Tải dữ liệu từ DAO và hiển thị lên danh sách các Card Panel
     */
    public void loadDataToList() {
        pnlDanhSachPhieu.removeAll(); // Xóa các card cũ
        
        // Lấy dữ liệu đã lọc
        String sdt = txtTimKhachHang.getText();
        java.util.Date tuNgay = dateTuNgay.getDate();
        java.util.Date denNgay = dateDenNgay.getDate();
        
        List<PhieuDatPhong> dsPhieu = pdp_DAO.getPhieuDatPhongLoc(sdt, tuNgay, denNgay);
        
        if (dsPhieu.isEmpty()) {
            JLabel lblTrong = new JLabel("Không tìm thấy phiếu đặt phòng nào phù hợp.");
            lblTrong.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblTrong.setBorder(new EmptyBorder(20, 20, 20, 20));
            pnlDanhSachPhieu.add(lblTrong);
        } else {
            for (PhieuDatPhong pdp : dsPhieu) {
                // Lấy chi tiết phòng và dịch vụ cho phiếu này
                // (Việc này nên được cache, nhưng tạm thời gọi trực tiếp)
                pdp.setDsPhong(pdp_DAO.getDanhSachPhongTheoMaPhieu(pdp.getMaPhieu()));
                pdp.setDsDichVu(pdp_DAO.getDanhSachDichVuTheoMaPhieu(pdp.getMaPhieu()));
                
                // Tạo một Card JPanel mới cho mỗi phiếu
                PhieuDatPhongCard card = new PhieuDatPhongCard(pdp, this);
                pnlDanhSachPhieu.add(card);
                pnlDanhSachPhieu.add(Box.createVerticalStrut(10)); // Khoảng cách giữa các card
            }
        }
        
        pnlDanhSachPhieu.revalidate();
        pnlDanhSachPhieu.repaint();
    }
    
    private String formatTien(double tien) {
        DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
        return df.format(tien);
    }
    
 // ==================================================================
    // LỚP NỘI BỘ (INNER CLASS) CHO CARD HIỂN THỊ PHIẾU ĐẶT
    // ==================================================================
    class PhieuDatPhongCard extends JPanel {
        
        private PhieuDatPhong pdp;
        private QuanLyPhieuDatPhong_Gui parentGui;
        private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private final Color COLOR_CHO = new Color(0, 122, 204);
        private final Color COLOR_NHAN = new Color(40, 167, 69);
        private final Color COLOR_HUY = new Color(220, 53, 69);
        private final Border LEFT_BORDER_CHO = new MatteBorder(0, 5, 0, 0, COLOR_CHO);
        private final Border LEFT_BORDER_NHAN = new MatteBorder(0, 5, 0, 0, COLOR_NHAN);
        private final Border LEFT_BORDER_HUY = new MatteBorder(0, 5, 0, 0, COLOR_HUY);

        public PhieuDatPhongCard(PhieuDatPhong pdp, QuanLyPhieuDatPhong_Gui parent) {
            this.pdp = pdp;
            this.parentGui = parent;
            
            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);
            // Viền ngoài (shadow)
            setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 10, 10, 10)
            ));
            // Tăng chiều cao để chứa thêm dòng dịch vụ
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 220)); 
            
            // --- Panel chứa nút (BÊN PHẢI) ---
            JPanel pnlButtons = new JPanel();
            pnlButtons.setLayout(new BoxLayout(pnlButtons, BoxLayout.Y_AXIS));
            pnlButtons.setBackground(Color.WHITE);
            pnlButtons.setOpaque(false);
            
            JButton btnNhanPhong = new JButton("Nhận Phòng");
            JButton btnHuyPhieu = new JButton("Hủy Phiếu");
            
            styleCardButton(btnNhanPhong, COLOR_NHAN);
            styleCardButton(btnHuyPhieu, COLOR_HUY);
            
            pnlButtons.add(btnNhanPhong);
            pnlButtons.add(Box.createVerticalStrut(8));
            pnlButtons.add(btnHuyPhieu);
            
            // Chỉ cho phép khi đang "Chờ nhận"
            if (pdp.getTrangThai() != TrangThaiPhieuDat.CHO_NHAN_PHONG) {
                btnNhanPhong.setEnabled(false);
                btnHuyPhieu.setEnabled(false);
            }
            
            add(pnlButtons, BorderLayout.EAST);

            // --- Panel thông tin (BÊN TRÁI) ---
            JPanel pnlInfo = new JPanel(new BorderLayout(5, 5));
            pnlInfo.setOpaque(false);
            
            // TOP: Mã phiếu và Trạng thái
            JPanel pnlTop = new JPanel(new BorderLayout());
            pnlTop.setOpaque(false);
            JLabel lblMaPhieu = new JLabel(pdp.getMaPhieu());
            lblMaPhieu.setFont(new Font("Segoe UI", Font.BOLD, 18));
            
            JLabel lblTrangThai = new JLabel(pdp.getTrangThai().toString().toUpperCase());
            lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            // Set màu theo trạng thái
            if (pdp.getTrangThai() == TrangThaiPhieuDat.CHO_NHAN_PHONG) {
                lblTrangThai.setForeground(COLOR_CHO);
                setBorder(new CompoundBorder(LEFT_BORDER_CHO, getBorder()));
            } else if (pdp.getTrangThai() == TrangThaiPhieuDat.DA_NHAN) {
                lblTrangThai.setForeground(COLOR_NHAN);
                setBorder(new CompoundBorder(LEFT_BORDER_NHAN, getBorder()));
            } else {
                lblTrangThai.setForeground(COLOR_HUY);
                setBorder(new CompoundBorder(LEFT_BORDER_HUY, getBorder()));
            }
            
            pnlTop.add(lblMaPhieu, BorderLayout.WEST);
            pnlTop.add(lblTrangThai, BorderLayout.EAST);
            
            // CENTER: Thông tin chi tiết
            JPanel pnlCenter = new JPanel(new GridLayout(0, 2, 10, 5)); // 2 cột
            pnlCenter.setOpaque(false);
            pnlCenter.setBorder(new EmptyBorder(10, 0, 10, 0));
            
            // Cột 1
            pnlCenter.add(createIconLabel("👤", "Khách hàng:", pdp.getKhachHang() != null ? pdp.getKhachHang().getHoTen() : "N/A"));
            pnlCenter.add(createIconLabel("📞", "SĐT:", pdp.getKhachHang() != null ? pdp.getKhachHang().getSoDienThoai() : "N/A"));
            
            // Cột 2
            pnlCenter.add(createIconLabel("📅", "Ngày nhận:", pdp.getNgayNhan().format(dtf)));
            pnlCenter.add(createIconLabel("📅", "Ngày trả:", pdp.getNgayTra().format(dtf)));
            
            // BOTTOM: Chứa Phòng VÀ Dịch vụ
            JPanel pnlBottom = new JPanel();
            // Dùng BoxLayout để xếp 2 dòng (phòng, dịch vụ) theo chiều dọc
            pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.Y_AXIS)); 
            pnlBottom.setOpaque(false);
            // Thêm viền trên và một chút đệm
            pnlBottom.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)), 
                new EmptyBorder(5, 0, 5, 0) // Đệm trên 5px, dưới 5px
            ));

            // 1. Dòng cho Phòng (Dùng 1 panel con để giữ FlowLayout.LEFT)
            JPanel pnlPhongRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pnlPhongRow.setOpaque(false);
            pnlPhongRow.setAlignmentX(Component.LEFT_ALIGNMENT); // <-- Căn lề trái
            
            JLabel lblPhongTitle = new JLabel("Phòng: ");
            lblPhongTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
            pnlPhongRow.add(lblPhongTitle);
            
            StringBuilder dsPhongStr = new StringBuilder();
            if (pdp.getDsPhong().isEmpty()) {
                dsPhongStr.append(" (Không có)");
            } else {
                for (Phong p : pdp.getDsPhong()) {
                    dsPhongStr.append(p.getMaPhong()).append(", ");
                }
                dsPhongStr.setLength(dsPhongStr.length() - 2);
            }
            JLabel lblPhongList = new JLabel(dsPhongStr.toString());
            lblPhongList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pnlPhongRow.add(lblPhongList);
            
            // Thêm dòng Phòng vào pnlBottom
            pnlBottom.add(pnlPhongRow);

            // Thêm khoảng cách nhỏ giữa 2 dòng
            pnlBottom.add(Box.createVerticalStrut(5));

            // 2. Dòng cho Dịch Vụ (MỚI)
            JPanel pnlDichVuRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pnlDichVuRow.setOpaque(false);
            pnlDichVuRow.setAlignmentX(Component.LEFT_ALIGNMENT); // <-- Căn lề trái

            JLabel lblDichVuTitle = new JLabel("Dịch vụ: ");
            lblDichVuTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
            pnlDichVuRow.add(lblDichVuTitle);

            StringBuilder dsDichVuStr = new StringBuilder();
            // Kiểm tra null vì dsDichVu được tải sau
            if (pdp.getDsDichVu() == null || pdp.getDsDichVu().isEmpty()) {
                dsDichVuStr.append("(Không có)");
            } else {
                for (ChiTietDichVu ctdv : pdp.getDsDichVu()) {
                    // Hiển thị Tên DV và số lượng
                    dsDichVuStr.append(ctdv.getDichVu().getTenDichVu());
                    dsDichVuStr.append(" (x").append(ctdv.getSoLuong()).append("), ");
                }
                dsDichVuStr.setLength(dsDichVuStr.length() - 2);
            }
            JLabel lblDichVuList = new JLabel(dsDichVuStr.toString());
            lblDichVuList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pnlDichVuRow.add(lblDichVuList);

            // Thêm dòng Dịch Vụ vào pnlBottom
            pnlBottom.add(pnlDichVuRow);
            
            // Ghép lại
            pnlInfo.add(pnlTop, BorderLayout.NORTH);
            pnlInfo.add(pnlCenter, BorderLayout.CENTER);
            pnlInfo.add(pnlBottom, BorderLayout.SOUTH); // <-- Thêm pnlBottom vào đây
            
            add(pnlInfo, BorderLayout.CENTER);
            
            // --- Sự kiện cho nút ---
            btnNhanPhong.addActionListener(e -> xuLyNhanPhong());
            btnHuyPhieu.addActionListener(e -> xuLyHuyPhieu());
        }
        
        private void xuLyNhanPhong() {
            int confirm = JOptionPane.showConfirmDialog(parentGui, 
                "Xác nhận nhận phòng cho phiếu " + pdp.getMaPhieu() + "?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = pdp_DAO.nhanPhong(pdp.getMaPhieu());
                if (success) {
                    JOptionPane.showMessageDialog(parentGui, "Đã xác nhận nhận phòng!");
                    parentGui.loadDataToList(); // Tải lại danh sách
                } else {
                    JOptionPane.showMessageDialog(parentGui, "Xác nhận thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        private void xuLyHuyPhieu() {
            int confirm = JOptionPane.showConfirmDialog(parentGui, 
                "Bạn có chắc muốn HỦY phiếu " + pdp.getMaPhieu() + "?\nCác phòng sẽ được trả về trạng thái 'Trống'.", 
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = pdp_DAO.huyPhieu(pdp.getMaPhieu());
                if (success) {
                    JOptionPane.showMessageDialog(parentGui, "Đã hủy phiếu thành công!");
                    parentGui.loadDataToList(); // Tải lại danh sách
                } else {
                    JOptionPane.showMessageDialog(parentGui, "Hủy phiếu thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        // Helper tạo label
        private JLabel createIconLabel(String icon, String title, String value) {
            JLabel lbl = new JLabel("<html>" + icon + " <b>" + title + "</b> " + value + "</html>");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            return lbl;
        }
        
        // Helper style nút
        private void styleCardButton(JButton btn, Color color) {
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorder(new EmptyBorder(8, 15, 8, 15));
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
        }
    }
}