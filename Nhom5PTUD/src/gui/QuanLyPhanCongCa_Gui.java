package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import dao.CaLamViec_DAO;
import dao.LichSuCaLamViec_DAO;
import dao.NhanVien_DAO;
import entity.CaLamViec;
import entity.LichSuCaLamViec;
import entity.NhanVien;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuanLyPhanCongCa_Gui extends JPanel {

    private JComboBox<NhanVien> cbNhanVien;
    private JComboBox<CaLamViec> cbCaLamViec;
    private JDateChooser dateChooser;
    private JButton btnPhanCong, btnLamMoi;
    private JScrollPane scrollPhanCong;
    private JPanel pnlLichPhanCong;

    private LichSuCaLamViec_DAO ls_dao;
    private NhanVien_DAO nv_dao;
    private CaLamViec_DAO ca_dao;
    
    private final Locale LOCALE_VN = new Locale("vi", "VN");
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // <<< THÊM MỚI: Biến lưu danh sách NV để dùng lại >>>
    private List<NhanVien> dsNhanVienFull;

    public QuanLyPhanCongCa_Gui() {
        ls_dao = new LichSuCaLamViec_DAO();
        nv_dao = new NhanVien_DAO();
        ca_dao = new CaLamViec_DAO();
        dsNhanVienFull = nv_dao.getAllNhanVien(); // Tải danh sách NV 1 lần

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel Form (Không thay đổi)
        JPanel pnlForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createTitledBorder("Phân công ca làm việc"));
        pnlForm.add(new JLabel("Nhân viên:"));
        cbNhanVien = new JComboBox<>();
        cbNhanVien.setPreferredSize(new Dimension(200, 25));
        pnlForm.add(cbNhanVien);
        pnlForm.add(new JLabel("Ca làm việc:"));
        cbCaLamViec = new JComboBox<>();
        cbCaLamViec.setPreferredSize(new Dimension(250, 25));
        pnlForm.add(cbCaLamViec);
        pnlForm.add(new JLabel("Ngày làm:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new Date());
        dateChooser.setPreferredSize(new Dimension(120, 25));
        pnlForm.add(dateChooser);
        btnPhanCong = new JButton("Phân công");
        btnLamMoi = new JButton("Làm mới");
        pnlForm.add(btnPhanCong);
        pnlForm.add(btnLamMoi);
        add(pnlForm, BorderLayout.NORTH);

        // Panel Lịch (Không thay đổi)
        pnlLichPhanCong = new JPanel();
        pnlLichPhanCong.setLayout(new BoxLayout(pnlLichPhanCong, BoxLayout.Y_AXIS));
        pnlLichPhanCong.setBackground(new Color(245, 245, 245));
        scrollPhanCong = new JScrollPane(pnlLichPhanCong);
        scrollPhanCong.setBorder(BorderFactory.createTitledBorder("Lịch phân công (Từ hôm nay)"));
        scrollPhanCong.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPhanCong, BorderLayout.CENTER);

        // Load data
        loadComboBoxes();
        loadPhanCongPanel(); 

        // Event Listeners
        btnLamMoi.addActionListener(e -> {
            dsNhanVienFull = nv_dao.getAllNhanVien(); // Tải lại danh sách NV
            loadComboBoxes(); // Tải lại combobox
            loadPhanCongPanel(); // Tải lại lịch
        });
        btnPhanCong.addActionListener(e -> phanCong());
    }

    /**
     * Tải lại ComboBox (PUBLIC) (Giữ nguyên)
     */
    public void loadComboBoxes() {
        // Load Nhân viên
        cbNhanVien.removeAllItems();
        if (dsNhanVienFull == null) {
            dsNhanVienFull = nv_dao.getAllNhanVien();
        }
        for (NhanVien nv : dsNhanVienFull) {
            cbNhanVien.addItem(nv);
        }

        // Load Ca làm việc
        cbCaLamViec.removeAllItems();
        List<CaLamViec> dsCa = ca_dao.getAllCaLamViec();
        for (CaLamViec ca : dsCa) {
            if (ca.isTrangThai()) {
                cbCaLamViec.addItem(ca);
            }
        }
    }

    /**
     * Tải lại và xây dựng giao diện hiển thị lịch phân công
     */
    private void loadPhanCongPanel() {
        pnlLichPhanCong.removeAll();
        // <<< SỬA: Gọi hàm DAO mới >>>
        List<LichSuCaLamViec> dsLS = ls_dao.getLichPhanCongTuHomNay();
        
        if (dsLS == null || dsLS.isEmpty()) {
            pnlLichPhanCong.add(new JLabel("  Không có ca nào được phân công sắp tới."));
        } else {
            // Gom nhóm theo Ngày
            Map<LocalDate, List<LichSuCaLamViec>> cacNgay = dsLS.stream()
                    .collect(Collectors.groupingBy(LichSuCaLamViec::getNgayLamViec));
            List<LocalDate> ngaySapXep = cacNgay.keySet().stream().sorted().collect(Collectors.toList());

            for (LocalDate ngay : ngaySapXep) {
                JPanel pnlNgay = createNgayPanel(ngay, cacNgay.get(ngay));
                pnlLichPhanCong.add(pnlNgay);
                pnlLichPhanCong.add(Box.createVerticalStrut(10));
            }
        }
        pnlLichPhanCong.revalidate();
        pnlLichPhanCong.repaint();
    }
    
    /**
     * Tạo Panel cho MỘT NGÀY (Không thay đổi)
     */
    private JPanel createNgayPanel(LocalDate ngay, List<LichSuCaLamViec> dsPhanCongTrongNgay) {
        JPanel pnlNgay = new JPanel(new BorderLayout(0, 5));
        pnlNgay.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(0, 120, 215)));
        pnlNgay.setBackground(Color.WHITE);
        String thu = ngay.getDayOfWeek().getDisplayName(TextStyle.FULL, LOCALE_VN);
        String ngayThang = ngay.format(DATE_FORMATTER);
        JLabel lblTieuDeNgay = new JLabel("  " + thu + ", " + ngayThang);
        lblTieuDeNgay.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTieuDeNgay.setOpaque(true);
        lblTieuDeNgay.setBackground(new Color(230, 242, 255));
        pnlNgay.add(lblTieuDeNgay, BorderLayout.NORTH);
        JPanel pnlCacCa = new JPanel();
        pnlCacCa.setLayout(new BoxLayout(pnlCacCa, BoxLayout.Y_AXIS));
        pnlCacCa.setBackground(Color.WHITE);
        pnlCacCa.setBorder(new EmptyBorder(5, 10, 5, 10));
        Map<CaLamViec, List<LichSuCaLamViec>> cacCa = dsPhanCongTrongNgay.stream()
                .collect(Collectors.groupingBy(LichSuCaLamViec::getCaLamViec));
        for (CaLamViec ca : cacCa.keySet()) {
            JPanel pnlCa = createCaPanel(ca, cacCa.get(ca));
            pnlCacCa.add(pnlCa);
            pnlCacCa.add(Box.createVerticalStrut(5));
        }
        pnlNgay.add(pnlCacCa, BorderLayout.CENTER);
        pnlNgay.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlNgay.getPreferredSize().height));
        return pnlNgay;
    }

    /**
     * Tạo Panel cho MỘT CA (Không thay đổi)
     */
    private JPanel createCaPanel(CaLamViec ca, List<LichSuCaLamViec> dsNhanVien) {
        JPanel pnlCa = new JPanel(new BorderLayout(10, 5));
        pnlCa.setBorder(BorderFactory.createTitledBorder(
                ca.getTenCa() + " (" + ca.getThoiGianBatDau() + " - " + ca.getThoiGianKetThuc() + ")"
        ));
        pnlCa.setOpaque(false);
        JPanel pnlDanhSachNV = new JPanel(new GridLayout(0, 3, 10, 5));
        pnlDanhSachNV.setOpaque(false);
        final int soLuongNVCungCa = dsNhanVien.size();
        for (LichSuCaLamViec phanCong : dsNhanVien) {
            JPanel pnlMotNhanVien = createNhanVienPanel(phanCong, soLuongNVCungCa);
            pnlDanhSachNV.add(pnlMotNhanVien);
        }
        pnlCa.add(pnlDanhSachNV, BorderLayout.CENTER);
        return pnlCa;
    }
    
    /**
     * Tạo Panel cho MỘT NHÂN VIÊN (Đã cập nhật)
     */
    private JPanel createNhanVienPanel(LichSuCaLamViec phanCong, int soLuongNVCungCa) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        JLabel lblTenNV = new JLabel(phanCong.getNhanVien().getHoTen());
        lblTenNV.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton btnXoa = new JButton("Xóa");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnXoa.setForeground(Color.RED);
        btnXoa.setMargin(new Insets(2, 5, 2, 5));
        btnXoa.addActionListener(e -> xoaPhanCong(phanCong, soLuongNVCungCa));
        
        // <<< SỬA: Vô hiệu hóa nút Xóa nếu ca đã được nhận >>>
        if (phanCong.getThoiGianNhanCa() != null) {
            btnXoa.setText("Đã nhận");
            btnXoa.setEnabled(false);
            btnXoa.setForeground(Color.GRAY);
        }
        // <<< KẾT THÚC SỬA >>>
        
        panel.add(lblTenNV, BorderLayout.CENTER);
        panel.add(btnXoa, BorderLayout.EAST);
        return panel;
    }


    private void phanCong() {
        NhanVien nv = (NhanVien) cbNhanVien.getSelectedItem();
        CaLamViec ca = (CaLamViec) cbCaLamViec.getSelectedItem();
        Date selectedDate = dateChooser.getDate();

        if (nv == null || ca == null || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin!");
            return;
        }
        
        LocalDate ngayLam = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        if (ngayLam.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Không thể phân công cho ngày trong quá khứ!");
            return;
        }

        String maLS = "LS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LichSuCaLamViec ls = new LichSuCaLamViec(maLS, nv, ca, ngayLam);

        if (ls_dao.addPhanCong(ls)) {
            JOptionPane.showMessageDialog(this, "Phân công thành công!");
            loadPhanCongPanel();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Phân công thất bại!\n" +
                "Lỗi: Nhân viên này có thể đã được phân công vào ca này trong ngày này.");
        }
    }

    // <<< HÀM MỚI: Hiển thị dialog chọn NV thay thế (Giữ nguyên) >>>
    private NhanVien timNhanVienThayThe(LichSuCaLamViec phanCongCu) {
        String nvCuMa = phanCongCu.getNhanVien().getMaNhanVien();

        // Lọc danh sách nhân viên, BỎ nhân viên cũ ra
        List<NhanVien> dsThayThe = dsNhanVienFull.stream()
                .filter(nv -> !nv.getMaNhanVien().equals(nvCuMa))
                .collect(Collectors.toList());
        
        if(dsThayThe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có nhân viên nào khác để thay thế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // Tạo ComboBox với danh sách đã lọc
        JComboBox<NhanVien> cbThayThe = new JComboBox<>(dsThayThe.toArray(new NhanVien[0]));

        // Hiển thị dialog
        int result = JOptionPane.showConfirmDialog(
            this, 
            cbThayThe, 
            "Yêu cầu chọn nhân viên thay thế", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (NhanVien) cbThayThe.getSelectedItem();
        }
        
        return null; // Người dùng nhấn Cancel
    }

    // <<< HÀM QUAN TRỌNG: Đã cập nhật logic "Hoán đổi" (Giữ nguyên) >>>
    private void xoaPhanCong(LichSuCaLamViec phanCong, int soLuongNVCungCa) {

        if (soLuongNVCungCa > 1) {
            // ----- TRƯỜNG HỢP 1: Ca còn nhiều người, XÓA bình thường -----
            String info = String.format("Bạn có chắc muốn XÓA phân công:\nNhân viên: %s\nCa: %s\nNgày: %s",
                    phanCong.getNhanVien().getHoTen(),
                    phanCong.getCaLamViec().getTenCa(),
                    phanCong.getNgayLamViec().format(DATE_FORMATTER)
            );
            
            if (JOptionPane.showConfirmDialog(this, info, "Xác nhận xóa phân công", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                
                if (ls_dao.deletePhanCong(phanCong.getMaLichSu())) {
                    JOptionPane.showMessageDialog(this, "Xóa phân công thành công!");
                    loadPhanCongPanel(); // Tải lại
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại!\n(Có thể ca này đã được nhân viên nhận?)");
                }
            }
        } else {
            // ----- TRƯỜNG HỢP 2: Ca chỉ còn 1 người, YÊU CẦU HOÁN ĐỔI -----
            String info = String.format("Nhân viên [ %s ] là người duy nhất trong ca này.\n" +
                                        "Bạn phải chọn một nhân viên khác để THAY THẾ.",
                                        phanCong.getNhanVien().getHoTen());
            
            JOptionPane.showMessageDialog(this, info, "Yêu cầu thay thế", JOptionPane.WARNING_MESSAGE);

            // Gọi hàm tìm NV thay thế
            NhanVien nvMoi = timNhanVienThayThe(phanCong);
            
            if (nvMoi != null) {
                // Nếu người dùng chọn NV mới, thực hiện hoán đổi
                if (ls_dao.hoanDoiPhanCong(phanCong, nvMoi)) {
                    JOptionPane.showMessageDialog(this, "Hoán đổi ca thành công!\n" +
                        "Đã xóa: " + phanCong.getNhanVien().getHoTen() + "\n" +
                        "Đã thêm: " + nvMoi.getHoTen());
                    loadPhanCongPanel(); // Tải lại
                } else {
                    JOptionPane.showMessageDialog(this, "Hoán đổi ca thất bại!\n(Lỗi CSDL hoặc ca đã được nhận)", "Lỗi Transaction", JOptionPane.ERROR_MESSAGE);
                }
            }
            // Nếu nvMoi == null (người dùng nhấn Cancel), không làm gì cả.
        }
    }
}