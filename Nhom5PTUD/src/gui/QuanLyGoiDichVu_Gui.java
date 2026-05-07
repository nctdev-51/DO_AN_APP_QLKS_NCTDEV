package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;
import dao.ChiTietDichVu_DAO;
import dao.DichVu_DAO; 
import dao.PhieuDatPhong_DAO;
import dao.Phong_DAO;
import entity.ChiTietDichVu;
import entity.DichVu;
import entity.NhanVien;
import entity.PhieuDatPhong; 
import entity.Phong;
import entity.TinhTrangPhong;
import entity.TrangThaiPhieuDat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter; 
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // <<< THÊM MỚI
import java.util.stream.Collectors; 

public class QuanLyGoiDichVu_Gui extends JPanel {

    private NhanVien nhanVien;
    private Phong_DAO phong_DAO; 
    private DichVu_DAO dichVu_DAO;
    private PhieuDatPhong_DAO pdp_DAO;
    private ChiTietDichVu_DAO ctdv_DAO; 

    // === THAY ĐỔI BIẾN ===
    private JPanel pnlDanhSachPhieu; 
    // <<< XÓA BỎ: cmbDichVu, spinSoLuong, txtGhiChu >>>
    private DefaultTableModel modelDichVuDaGoi;
    private JTable tblDichVuDaGoi;
    private JLabel lblPhieuChon; 

    private PhieuDatPhong phieuDuocChon = null; 
    private String maPhieuHienTai = null; 
    
    private JPanel pnlChonPhongCuaPhieu; 
    private List<JCheckBox> dsCheckboxPhong; 

    // === BIẾN LƯU TRỮ MỚI ===
    private List<ChiTietDichVu> dsDichVuMoi; // Danh sách chờ lưu
    private List<DichVu> danhSachDichVuTuDB; // Danh sách dịch vụ đã tải
    
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd/MM");

    public QuanLyGoiDichVu_Gui(NhanVien nv) {
        ConnectDB.getInstance(); 
        this.nhanVien = nv;
        this.phong_DAO = new Phong_DAO();
        this.dichVu_DAO = new DichVu_DAO();
        this.pdp_DAO = new PhieuDatPhong_DAO();
        this.ctdv_DAO = new ChiTietDichVu_DAO(); 
        this.dsDichVuMoi = new ArrayList<>();
        this.dsCheckboxPhong = new ArrayList<>(); 
        this.danhSachDichVuTuDB = new ArrayList<>(); // <<< KHỞI TẠO

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Panel chọn phiếu (BÊN TRÁI) - Giữ nguyên
        JScrollPane scrollPhieu = new JScrollPane();
        scrollPhieu.setBorder(new TitledBorder("Chọn phiếu đặt phòng đang hoạt động"));
        pnlDanhSachPhieu = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10)); 
        scrollPhieu.setViewportView(pnlDanhSachPhieu);
        scrollPhieu.setPreferredSize(new Dimension(450, 0)); 
        add(scrollPhieu, BorderLayout.WEST);

        // 2. Panel nghiệp vụ (TRUNG TÂM)
        JPanel pnlNghiepVu = new JPanel(new BorderLayout(10, 10));
        add(pnlNghiepVu, BorderLayout.CENTER);

        // 2.1. Panel Thêm Dịch Vụ (TOP)
        JPanel pnlThem = new JPanel(new BorderLayout(10, 10));
        pnlThem.setBorder(new TitledBorder("Thêm dịch vụ cho phiếu"));
        
        // Panel Top (chứa label chọn phiếu VÀ panel checkbox phòng) - Giữ nguyên
        JPanel pnlThemTop = new JPanel(new BorderLayout());
        lblPhieuChon = new JLabel("Vui lòng chọn một phiếu đang hoạt động bên trái...");
        lblPhieuChon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPhieuChon.setForeground(Color.RED);
        pnlThemTop.add(lblPhieuChon, BorderLayout.NORTH);
        pnlChonPhongCuaPhieu = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlChonPhongCuaPhieu.setBorder(new TitledBorder("Áp dụng cho các phòng"));
        pnlChonPhongCuaPhieu.setVisible(false); 
        pnlThemTop.add(pnlChonPhongCuaPhieu, BorderLayout.CENTER);
        pnlThem.add(pnlThemTop, BorderLayout.NORTH); 

        // =========================================================================
        // <<< NÂNG CẤP: Thay thế pnlThemForm cũ >>>
        // =========================================================================
        JPanel pnlThemForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tạo 1 nút bấm lớn duy nhất
        JButton btnMoDialogChon = new JButton("Mở Bảng Chọn Dịch Vụ...");
        btnMoDialogChon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMoDialogChon.setBackground(new Color(0, 122, 204));
        btnMoDialogChon.setForeground(Color.WHITE);
        btnMoDialogChon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weightx = 1.0;
        gbc.ipady = 20; // Làm cho nút cao hơn
        pnlThemForm.add(btnMoDialogChon, gbc);
        
        // Thêm sự kiện cho nút mới
        btnMoDialogChon.addActionListener(e -> moDialogChonDichVu());
        // =========================================================================
        
        pnlThem.add(pnlThemForm, BorderLayout.CENTER);
        pnlNghiepVu.add(pnlThem, BorderLayout.NORTH);

        // 2.2. Panel Danh sách dịch vụ đã gọi (CENTER) - Giữ nguyên
        modelDichVuDaGoi = new DefaultTableModel(new String[]{"Phòng", "Tên Dịch Vụ", "Số Lượng", "Đơn Giá", "Ghi Chú", "Thời Gian Gọi"}, 0);
        tblDichVuDaGoi = new JTable(modelDichVuDaGoi);
        tblDichVuDaGoi.getColumnModel().getColumn(0).setPreferredWidth(60); 
        tblDichVuDaGoi.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblDichVuDaGoi.getColumnModel().getColumn(2).setPreferredWidth(60); 
        JScrollPane scrollTable = new JScrollPane(tblDichVuDaGoi);
        scrollTable.setBorder(new TitledBorder("Danh sách dịch vụ đã gọi (theo phiếu)"));
        pnlNghiepVu.add(scrollTable, BorderLayout.CENTER);

        // 2.3. Panel Nút bấm (SOUTH) - Giữ nguyên
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("Lưu (Xác nhận gọi Dịch vụ)");
        JButton btnLamMoiDSPhieu = new JButton("Làm mới danh sách phiếu"); 
        pnlActions.add(btnLamMoiDSPhieu);
        pnlActions.add(btnLuu);
        pnlNghiepVu.add(pnlActions, BorderLayout.SOUTH);
        
        // Load dữ liệu ban đầu
        loadDanhSachPhieuDangHoatDong(); 
        loadDanhSachDichVu(); // <<< VẪN GỌI HÀM NÀY (để tải dữ liệu vào List)

        // Thêm sự kiện
        btnLamMoiDSPhieu.addActionListener(e -> loadDanhSachPhieuDangHoatDong()); 
        // <<< XÓA: btnThem.addActionListener(...) >>>
        btnLuu.addActionListener(e -> luuDichVuMoiVaoCSDL());
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadDanhSachPhieuDangHoatDong(); 
                clearSelectionUI(); 
            }
        });
    }

    /**
     * Tải danh sách các PHIẾU đang ở (Giữ nguyên)
     */
    private void loadDanhSachPhieuDangHoatDong() {
        pnlDanhSachPhieu.removeAll();
        List<PhieuDatPhong> dsPhieu = pdp_DAO.getDanhSachPhieuDatPhongDangHoatDong();
        int count = 0;
        if (dsPhieu != null) {
        	for (PhieuDatPhong pdp : dsPhieu) {
                if (pdp.getDsPhong() == null || pdp.getDsPhong().isEmpty()) continue;
                
                count++;
                
                // <<< BẮT ĐẦU NÂNG CẤP >>>
                // 1. Xác định trạng thái và màu sắc
                String trangThaiText = "";
                Color bgColor;
                
                if (pdp.getTrangThai() == TrangThaiPhieuDat.DA_NHAN) {
                    trangThaiText = "[ĐANG Ở]";
                    bgColor = new Color(255, 170, 0); // Cam
                } else { // (Vì đã lọc, đây chắc chắn là CHỜ NHẬN)
                    trangThaiText = "[CHỜ NHẬN]";
                    bgColor = new Color(0, 122, 204); // Xanh
                }
                // <<< KẾT THÚC NÂNG CẤP >>>

                // Lấy tên khách hàng
                String tenKH = (pdp.getKhachHang() != null && pdp.getKhachHang().getHoTen() != null) 
                                ? pdp.getKhachHang().getHoTen() : "Khách vãng lai";
                
                // Lấy danh sách phòng
                String dsPhongStr = pdp.getDsPhong().stream()
                                        .map(Phong::getMaPhong)
                                        .collect(Collectors.joining(", "));
                
                // 2. Tạo buttonText mới (thêm trạng thái vào dòng 1)
                String buttonText = String.format("<html><center>"
                                      + "<b>%s <font color='white'>%s</font></b>" // Dòng 1: PDP001 [ĐANG Ở]
                                      + "<br>%s" // Dòng 2: Tên KH
                                      + "<br>(%s)" // Dòng 3: (P101, P102)
                                      + "</center></html>",
                                        pdp.getMaPhieu(), trangThaiText, tenKH, dsPhongStr);

                JButton btnPhieu = new JButton(buttonText);
                btnPhieu.setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                // 3. Set màu nền
                btnPhieu.setBackground(bgColor);
                btnPhieu.setForeground(Color.WHITE);
                
                btnPhieu.setPreferredSize(new Dimension(190, 90)); 
                btnPhieu.addActionListener(e -> chonPhieu(pdp));
                pnlDanhSachPhieu.add(btnPhieu);
            }
        }
        if (count == 0) {
            pnlDanhSachPhieu.add(new JLabel("Không có phiếu đặt phòng nào đang hoạt động."));
        }
        pnlDanhSachPhieu.revalidate();
        pnlDanhSachPhieu.repaint();
    }

    /**
     * <<< NÂNG CẤP: Tải dịch vụ vào List thay vì JComboBox >>>
     */
    private void loadDanhSachDichVu() {
        this.danhSachDichVuTuDB = dichVu_DAO.getAllDichVu(); 
        if (this.danhSachDichVuTuDB == null) {
            this.danhSachDichVuTuDB = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách dịch vụ!", "Lỗi DAO", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý khi click chọn 1 PHIẾU (Giữ nguyên)
     */
    private void chonPhieu(PhieuDatPhong pdp) {
        phieuDuocChon = pdp;
        maPhieuHienTai = pdp.getMaPhieu();
        String dsPhongStr = pdp.getDsPhong().stream()
                                .map(Phong::getMaPhong)
                                .collect(Collectors.joining(", "));
        lblPhieuChon.setText(String.format("Phiếu %s (Phòng: %s)", pdp.getMaPhieu(), dsPhongStr));
        lblPhieuChon.setForeground(new Color(0, 120, 0));
        dsDichVuMoi.clear();
        loadDichVuDaGoiTruoc();
        
        pnlChonPhongCuaPhieu.removeAll();
        dsCheckboxPhong.clear();
        
        JCheckBox cbChonTatCa = new JCheckBox("Tất cả phòng");
        cbChonTatCa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cbChonTatCa.addActionListener(e -> {
            for (JCheckBox cb : dsCheckboxPhong) {
                cb.setSelected(cbChonTatCa.isSelected());
            }
        });
        pnlChonPhongCuaPhieu.add(cbChonTatCa);
        
        for (Phong p : pdp.getDsPhong()) {
            JCheckBox cbPhong = new JCheckBox(p.getMaPhong());
            cbPhong.setSelected(true); 
            dsCheckboxPhong.add(cbPhong);
            pnlChonPhongCuaPhieu.add(cbPhong);
        }
        
        cbChonTatCa.setSelected(true); 
        pnlChonPhongCuaPhieu.setVisible(true);
        pnlChonPhongCuaPhieu.revalidate();
        pnlChonPhongCuaPhieu.repaint();
    }

    /**
     * Tải các dịch vụ đã gọi của PHIẾU được chọn (Giữ nguyên)
     */
    private void loadDichVuDaGoiTruoc() {
        modelDichVuDaGoi.setRowCount(0); 
        if (phieuDuocChon == null) return;
        List<ChiTietDichVu> dsDaGoi = pdp_DAO.getDanhSachDichVuTheoMaPhieu(maPhieuHienTai);
        for (ChiTietDichVu ctdv : dsDaGoi) {
            modelDichVuDaGoi.addRow(new Object[]{
                (ctdv.getPhong() != null) ? ctdv.getPhong().getMaPhong() : "N/A", 
                ctdv.getDichVu().getTenDichVu(),
                ctdv.getSoLuong(),
                String.format("%,.0f VND", ctdv.getDichVu().getGiaTien()),
                ctdv.getGhiChu(),
                (ctdv.getThoiGianGoi() != null) ? ctdv.getThoiGianGoi().format(dtf) : "N/A"
            });
        }
    }

    /**
     * <<< XÓA BỎ: Hàm themDichVuVaoDanhSachTam() >>>
     */

    /**
     * <<< HÀM MỚI: Helper để lấy danh sách phòng từ Checkbox >>>
     */
    private List<Phong> layDanhSachPhongTuCheckbox() {
        List<Phong> cacPhongDuocChon = new ArrayList<>();
        if (phieuDuocChon == null) return cacPhongDuocChon;
        
        for (JCheckBox cb : dsCheckboxPhong) {
            if (cb.isSelected()) {
                String maPhong = cb.getText();
                // Tìm đối tượng Phong đầy đủ từ phieuDuocChon
                Phong p = phieuDuocChon.getDsPhong().stream()
                                .filter(phong -> phong.getMaPhong().equals(maPhong))
                                .findFirst().orElse(null);
                if (p != null) {
                    cacPhongDuocChon.add(p);
                }
            }
        }
        return cacPhongDuocChon;
    }

    /**
     * <<< HÀM MỚI: Được gọi bởi nút "Mở Bảng Chọn Dịch Vụ" >>>
     */
    private void moDialogChonDichVu() {
        if (phieuDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu đặt phòng trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Lấy danh sách phòng được chọn từ checkbox
        List<Phong> cacPhongDuocChon = layDanhSachPhongTuCheckbox();

        if (cacPhongDuocChon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một phòng để thêm dịch vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Mở Dialog (sử dụng danh sách dịch vụ đã tải sẵn)
        ChonDichVu_Dialog dialog = new ChonDichVu_Dialog((Frame) SwingUtilities.getWindowAncestor(this), this.danhSachDichVuTuDB);
        dialog.setVisible(true);

        // 3. Lấy kết quả
        Map<DichVu, Integer> selectedServices = dialog.getSelectedServices();
        if (selectedServices.isEmpty()) {
            return; // Người dùng hủy
        }
        
        int countAdded = 0;
        // 4. Lặp qua các dịch vụ đã chọn
        for (Map.Entry<DichVu, Integer> entry : selectedServices.entrySet()) {
            DichVu dv = entry.getKey();
            int soLuong = entry.getValue();
            
            // 5. Lặp qua các phòng đã chọn
            for (Phong p : cacPhongDuocChon) {
                // Tạo ChiTietDichVu (ghi chú rỗng vì dialog mới không có)
                ChiTietDichVu ctdvMoi = new ChiTietDichVu(phieuDuocChon, p, dv, soLuong, "");
                dsDichVuMoi.add(ctdvMoi);

                // Thêm vào bảng với trạng thái "Mới"
                modelDichVuDaGoi.addRow(new Object[]{
                    p.getMaPhong(),
                    dv.getTenDichVu(),
                    soLuong,
                    String.format("%,.0f VND", dv.getGiaTien()),
                    "", // Ghi chú rỗng
                    "[MỚI] " + ctdvMoi.getThoiGianGoi().format(dtf)
                });
                countAdded++;
            }
        }
        
        if(countAdded > 0) {
             JOptionPane.showMessageDialog(this, "Đã thêm " + selectedServices.size() + " loại dịch vụ cho " + cacPhongDuocChon.size() + " phòng vào danh sách tạm.\nNhấn 'Lưu' để xác nhận.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    /**
     * Lưu các dịch vụ trong danh sách tạm vào CSDL (Giữ nguyên)
     */
    private void luuDichVuMoiVaoCSDL() {
        if (phieuDuocChon == null || maPhieuHienTai == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn phiếu hoặc phiếu không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (dsDichVuMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dịch vụ mới nào để lưu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int successCount = 0;
        for (ChiTietDichVu ctdv : dsDichVuMoi) {
            if (ctdv_DAO.addChiTietDichVu(ctdv)) {
                successCount++;
            }
        }

        JOptionPane.showMessageDialog(this, "Đã lưu thành công " + successCount + "/" + dsDichVuMoi.size() + " dịch vụ mới.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        
        dsDichVuMoi.clear();
        loadDichVuDaGoiTruoc();
    }

    /**
     * Custom Renderer (Giữ nguyên)
     */
    class DichVuComboboxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof DichVu) {
                DichVu dv = (DichVu) value;
                setText(String.format("%s (%,.0f VND)", dv.getTenDichVu(), dv.getGiaTien()));
            }
            return this;
        }
    }

    /**
     * WrapLayout (Giữ nguyên)
     */
    class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            Dimension size = super.preferredLayoutSize(target);
            if (target.getParent() instanceof JViewport) {
                JViewport viewport = (JViewport) target.getParent();
                size.width = Math.max(viewport.getWidth() - 20, 100); 
            }
            return size;
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension size = super.minimumLayoutSize(target);
            if (target.getParent() instanceof JViewport) {
                JViewport viewport = (JViewport) target.getParent();
                size.width = Math.max(viewport.getWidth() - 20, 100);
            }
            return size;
        }
    }
    
    /**
     * Helper: Reset giao diện (Giữ nguyên)
     */
    private void clearSelectionUI() {
        phieuDuocChon = null;
        maPhieuHienTai = null;
        lblPhieuChon.setText("Vui lòng chọn một phiếu đang hoạt động bên trái...");
        lblPhieuChon.setForeground(Color.RED);
        modelDichVuDaGoi.setRowCount(0);
        dsDichVuMoi.clear();
        
        pnlChonPhongCuaPhieu.removeAll();
        pnlChonPhongCuaPhieu.setVisible(false);
        dsCheckboxPhong.clear();
    }
}