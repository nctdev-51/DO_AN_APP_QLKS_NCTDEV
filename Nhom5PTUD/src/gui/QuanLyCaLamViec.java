package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import dao.CaLamViec_DAO;
import entity.CaLamViec;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// (Lớp TimeSpinnerEditor đã bị xóa ở lần trước, điều đó là đúng)

public class QuanLyCaLamViec extends JPanel {

    private JTextField txtMaCa, txtTenCa, txtGhiChu;
    private JComboBox<String> cbGioBatDau, cbPhutBatDau;
    private JComboBox<String> cbGioKetThuc, cbPhutKetThuc;
    private JCheckBox chkTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private JTable tableCa;
    private DefaultTableModel modelCa;
    private CaLamViec_DAO ca_dao;
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // <<< THÊM MỚI 1: Biến để giữ tham chiếu đến panel Phân Công >>>
    private QuanLyPhanCongCa_Gui phanCongPanel;

    // <<< THÊM MỚI 2: Hàm để TrangChu_Gui "gửi" panel Phân Công vào >>>
    public void setPhanCongPanel(QuanLyPhanCongCa_Gui phanCongPanel) {
        this.phanCongPanel = phanCongPanel;
    }

    public QuanLyCaLamViec() {
        ca_dao = new CaLamViec_DAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // ... (Toàn bộ code giao diện (GridBagLayout) giữ nguyên) ...
        // Panel Form
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createTitledBorder("Thông tin ca làm việc"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã ca:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtMaCa = new JTextField(20);
        pnlForm.add(txtMaCa, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        pnlForm.add(new JLabel("Tên ca:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        txtTenCa = new JTextField(20);
        pnlForm.add(txtTenCa, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Giờ bắt đầu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        
        JPanel pnlGioBatDau = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlGioBatDau.setOpaque(false); // Để nền trong suốt
        cbGioBatDau = createHourComboBox();
        cbPhutBatDau = createMinuteComboBox();
        pnlGioBatDau.add(cbGioBatDau);
        pnlGioBatDau.add(Box.createHorizontalStrut(5));
        pnlGioBatDau.add(new JLabel(":"));
        pnlGioBatDau.add(Box.createHorizontalStrut(5));
        pnlGioBatDau.add(cbPhutBatDau);
        pnlForm.add(pnlGioBatDau, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        pnlForm.add(new JLabel("Giờ kết thúc:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        
        JPanel pnlGioKetThuc = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlGioKetThuc.setOpaque(false);
        cbGioKetThuc = createHourComboBox();
        cbPhutKetThuc = createMinuteComboBox();
        pnlGioKetThuc.add(cbGioKetThuc);
        pnlGioKetThuc.add(Box.createHorizontalStrut(5));
        pnlGioKetThuc.add(new JLabel(":"));
        pnlGioKetThuc.add(Box.createHorizontalStrut(5));
        pnlGioKetThuc.add(cbPhutKetThuc);
        pnlForm.add(pnlGioKetThuc, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
        txtGhiChu = new JTextField();
        pnlForm.add(txtGhiChu, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        chkTrangThai = new JCheckBox("Đang áp dụng", true);
        chkTrangThai.setBackground(Color.WHITE);
        pnlForm.add(chkTrangThai, gbc);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnlButtons.setBackground(Color.WHITE);
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa (Vô hiệu hóa)");
        btnLamMoi = new JButton("Làm mới");
        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.NORTH);

        String[] cols = {"Mã ca", "Tên ca", "Giờ bắt đầu", "Giờ kết thúc", "Ghi chú", "Trạng thái"};
        modelCa = new DefaultTableModel(cols, 0);
        tableCa = new JTable(modelCa);
        tableCa.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(tableCa);
        scroll.setBorder(BorderFactory.createTitledBorder("Danh sách ca làm việc"));
        add(scroll, BorderLayout.CENTER);

        loadDataToTable();

        btnLamMoi.addActionListener(e -> clearFields());
        btnThem.addActionListener(e -> themCa());
        btnSua.addActionListener(e -> suaCa());
        btnXoa.addActionListener(e -> xoaCa());

        tableCa.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableCa.getSelectedRow();
                if (row >= 0) {
                    txtMaCa.setText(modelCa.getValueAt(row, 0).toString());
                    txtTenCa.setText(modelCa.getValueAt(row, 1).toString());
                    
                    LocalTime gioBatDau = LocalTime.parse(modelCa.getValueAt(row, 2).toString(), TIME_FORMATTER);
                    LocalTime gioKetThuc = LocalTime.parse(modelCa.getValueAt(row, 3).toString(), TIME_FORMATTER);
                    
                    cbGioBatDau.setSelectedItem(String.format("%02d", gioBatDau.getHour()));
                    cbPhutBatDau.setSelectedItem(String.format("%02d", gioBatDau.getMinute()));
                    cbGioKetThuc.setSelectedItem(String.format("%02d", gioKetThuc.getHour()));
                    cbPhutKetThuc.setSelectedItem(String.format("%02d", gioKetThuc.getMinute()));
                    
                    txtGhiChu.setText(modelCa.getValueAt(row, 4).toString());
                    chkTrangThai.setSelected(modelCa.getValueAt(row, 5).toString().equals("Đang áp dụng"));
                    txtMaCa.setEditable(false);
                }
            }
        });
    }

    // ... (Hàm createHourComboBox, createMinuteComboBox, getLocalTimeFromComboBoxes giữ nguyên) ...
    private JComboBox<String> createHourComboBox() {
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        return new JComboBox<>(hours);
    }
    private JComboBox<String> createMinuteComboBox() {
        String[] minutes = new String[60];
        for (int i = 0; i < 60; i++) {
            minutes[i] = String.format("%02d", i);
        }
        return new JComboBox<>(minutes);
    }
    private LocalTime getLocalTimeFromComboBoxes(JComboBox<String> cbGio, JComboBox<String> cbPhut) {
        try {
            int gio = Integer.parseInt((String) cbGio.getSelectedItem());
            int phut = Integer.parseInt((String) cbPhut.getSelectedItem());
            return LocalTime.of(gio, phut);
        } catch (NumberFormatException e) {
            return LocalTime.MIDNIGHT;
        }
    }

    private void loadDataToTable() {
        modelCa.setRowCount(0);
        List<CaLamViec> dsCa = ca_dao.getAllCaLamViec();
        if (dsCa == null || dsCa.isEmpty()) {
            return; 
        }
        
        for (CaLamViec ca : dsCa) {
            String gioBatDau = (ca.getThoiGianBatDau() != null) ? ca.getThoiGianBatDau().format(TIME_FORMATTER) : "N/A";
            String gioKetThuc = (ca.getThoiGianKetThuc() != null) ? ca.getThoiGianKetThuc().format(TIME_FORMATTER) : "N/A";
            
            modelCa.addRow(new Object[]{
                ca.getMaCa(),
                ca.getTenCa(),
                gioBatDau,
                gioKetThuc,
                ca.getGhiChu(),
                ca.isTrangThai() ? "Đang áp dụng" : "Không áp dụng"
            });
        }
    }

    private void clearFields() {
        txtMaCa.setText("");
        txtTenCa.setText("");
        txtGhiChu.setText("");
        cbGioBatDau.setSelectedItem("00");
        cbPhutBatDau.setSelectedItem("00");
        cbGioKetThuc.setSelectedItem("00");
        cbPhutKetThuc.setSelectedItem("00");
        chkTrangThai.setSelected(true);
        txtMaCa.setEditable(true);
        tableCa.clearSelection();
    }

    private CaLamViec getCaFromFields() {
        String maCa = txtMaCa.getText().trim();
        String tenCa = txtTenCa.getText().trim();
        LocalTime gioBatDau = getLocalTimeFromComboBoxes(cbGioBatDau, cbPhutBatDau);
        LocalTime gioKetThuc = getLocalTimeFromComboBoxes(cbGioKetThuc, cbPhutKetThuc);
        String ghiChu = txtGhiChu.getText().trim();
        boolean trangThai = chkTrangThai.isSelected();

        if (maCa.isEmpty() || tenCa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã ca và Tên ca không được để trống!");
            return null;
        }
        if (gioBatDau.equals(gioKetThuc)) {
             JOptionPane.showMessageDialog(this, "Giờ bắt đầu và giờ kết thúc không được trùng nhau!");
            return null;
        }
        return new CaLamViec(maCa, tenCa, gioBatDau, gioKetThuc, ghiChu, trangThai);
    }
    
    // <<< THÊM MỚI 3: Hàm trung gian để gọi cập nhật >>>
    private void updatePhanCongPanel() {
        if (phanCongPanel != null) {
            // Gọi hàm public đã tạo ở Bước 1
            phanCongPanel.loadComboBoxes(); 
        }
    }

    private void themCa() {
        CaLamViec ca = getCaFromFields();
        if (ca != null) {
            if (ca_dao.addCaLamViec(ca)) {
                JOptionPane.showMessageDialog(this, "Thêm ca làm việc thành công!");
                loadDataToTable();
                clearFields();
                // <<< THÊM MỚI 4: Gọi hàm cập nhật >>>
                updatePhanCongPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại! (Có thể trùng Mã ca)");
            }
        }
    }

    private void suaCa() {
        int row = tableCa.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca cần sửa!");
            return;
        }
        CaLamViec ca = getCaFromFields();
        if (ca != null) {
            if (ca_dao.updateCaLamViec(ca)) {
                JOptionPane.showMessageDialog(this, "Cập nhật ca làm việc thành công!");
                loadDataToTable();
                clearFields();
                // <<< THÊM MỚI 4: Gọi hàm cập nhật >>>
                updatePhanCongPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        }
    }

    private void xoaCa() {
        int row = tableCa.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca cần xóa!");
            return;
        }

        // Lấy Mã ca và Tên ca trực tiếp từ text field (đã được populate khi click)
        String maCa = txtMaCa.getText().trim();
        String tenCa = txtTenCa.getText().trim();

        if (maCa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không thể xác định ca cần xóa!");
            return;
        }

        // Thay đổi câu thông báo để cảnh báo người dùng
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn XÓA VĨNH VIỄN ca làm việc:\n" + tenCa + " (" + maCa + ")?\n" +
            "Hành động này không thể hoàn tác.", 
            "Xác nhận Xóa", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            
            // <<< THAY ĐỔI CHÍNH: Gọi hàm deleteCaLamViec >>>
            if (ca_dao.deleteCaLamViec(maCa)) {
                JOptionPane.showMessageDialog(this, "Đã xóa ca làm việc thành công!");
                loadDataToTable();
                clearFields();
                
                // Vẫn giữ lại để đồng bộ với Panel Phân Công
                updatePhanCongPanel(); 
            } else {
                // Lỗi này thường xảy ra nếu ca đã được phân công (Foreign Key constraint)
                JOptionPane.showMessageDialog(this, "Xóa thất bại!\n" +
                    "Lỗi: Ca này có thể đang được sử dụng (ví dụ: đã được phân công cho nhân viên).");
            }
        }
    }
}