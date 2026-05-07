package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import entity.NhanVien;

public class QuanLyNhanVien extends JPanel {
    private JTextField txtMaNV, txtHoTen, txtSDT, txtCCCD, txtQueQuan;
    private JSpinner spnNgaySinh, spnNgayVaoLam;
    private JComboBox<String> cboLoaiNV;
    private JRadioButton radNam, radNu;
    private JCheckBox chkTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLuu, btnHuy;
    private JTable table;
    private DefaultTableModel model;

    public QuanLyNhanVien() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== PANEL TIÊU ĐỀ =====
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // ===== PANEL THÔNG TIN =====
        JPanel pnlThongTin = new JPanel(new GridBagLayout());
        pnlThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
        pnlThongTin.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== DÒNG 1 =====
        gbc.gridx = 0; gbc.gridy = 0;
        pnlThongTin.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1;
        txtMaNV = new JTextField(10);
        pnlThongTin.add(txtMaNV, gbc);

        gbc.gridx = 2;
        pnlThongTin.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 3;
        txtHoTen = new JTextField(15);
        pnlThongTin.add(txtHoTen, gbc);

        // ===== DÒNG 2 =====
        gbc.gridx = 0; gbc.gridy++;
        pnlThongTin.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel modelNgaySinh = new SpinnerDateModel();
        spnNgaySinh = new JSpinner(modelNgaySinh);
        spnNgaySinh.setEditor(new JSpinner.DateEditor(spnNgaySinh, "dd/MM/yyyy"));
        spnNgaySinh.setPreferredSize(new Dimension(150, 28));
        pnlThongTin.add(spnNgaySinh, gbc);

        gbc.gridx = 2;
        pnlThongTin.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 3;
        JPanel pnlGioiTinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlGioiTinh.setBackground(Color.WHITE);
        radNam = new JRadioButton("Nam", true);
        radNu = new JRadioButton("Nữ");
        ButtonGroup groupGT = new ButtonGroup();
        groupGT.add(radNam);
        groupGT.add(radNu);
        pnlGioiTinh.add(radNam);
        pnlGioiTinh.add(radNu);
        pnlThongTin.add(pnlGioiTinh, gbc);

        // ===== DÒNG 3 =====
        gbc.gridx = 0; gbc.gridy++;
        pnlThongTin.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        txtSDT = new JTextField(10);
        pnlThongTin.add(txtSDT, gbc);

        gbc.gridx = 2;
        pnlThongTin.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 3;
        txtCCCD = new JTextField(15);
        pnlThongTin.add(txtCCCD, gbc);

        // ===== DÒNG 4 =====
        gbc.gridx = 0; gbc.gridy++;
        pnlThongTin.add(new JLabel("Ngày vào làm:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel modelNgayVaoLam = new SpinnerDateModel();
        spnNgayVaoLam = new JSpinner(modelNgayVaoLam);
        spnNgayVaoLam.setEditor(new JSpinner.DateEditor(spnNgayVaoLam, "dd/MM/yyyy"));
        pnlThongTin.add(spnNgayVaoLam, gbc);

        gbc.gridx = 2;
        pnlThongTin.add(new JLabel("Loại NV:"), gbc);
        gbc.gridx = 3;
        cboLoaiNV = new JComboBox<>(new String[]{
                "Lễ tân", "Quản lý", "Phục vụ", "Bảo vệ", "Kỹ thuật"
        });
        pnlThongTin.add(cboLoaiNV, gbc);

        // ===== DÒNG 5 =====
        gbc.gridx = 0; gbc.gridy++;
        pnlThongTin.add(new JLabel("Quê quán:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtQueQuan = new JTextField(20);
        pnlThongTin.add(txtQueQuan, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 3;
        chkTrangThai = new JCheckBox("Đang làm việc", true);
        chkTrangThai.setBackground(Color.WHITE);
        pnlThongTin.add(chkTrangThai, gbc);

        add(pnlThongTin, BorderLayout.CENTER);

        // ===== PANEL NÚT CHỨC NĂNG =====
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLuu = new JButton("Lưu");
        btnHuy = new JButton("Hủy");
        pnlButton.add(btnThem);
        pnlButton.add(btnSua);
        pnlButton.add(btnXoa);
        pnlButton.add(btnLuu);
        pnlButton.add(btnHuy);
        add(pnlButton, BorderLayout.SOUTH);

        // ===== PANEL DANH SÁCH NHÂN VIÊN =====
        String[] cols = {"Mã NV", "Họ tên", "Ngày sinh", "Giới tính", "SĐT", "Loại NV", "Trạng thái"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Danh sách nhân viên"));
        scroll.setPreferredSize(new Dimension(870, 180));
        add(scroll, BorderLayout.EAST);
    }

    // ===== LẤY DỮ LIỆU TỪ FORM =====
    public NhanVien getNhanVienTuForm() {
        Date dateSinh = (Date) spnNgaySinh.getValue();
        LocalDate ngaySinh = dateSinh.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Date dateVL = (Date) spnNgayVaoLam.getValue();
        LocalDate ngayVaoLam = dateVL.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        NhanVien nv = new NhanVien();
        nv.setHoTen(txtHoTen.getText());
        nv.setMaNhanVien(txtMaNV.getText());
        nv.setSoDienThoai(txtSDT.getText());
        nv.setCCCD(txtCCCD.getText());
        nv.setNgaySinh(ngaySinh);
        nv.setNgayVaoLam(ngayVaoLam);
        nv.setGioiTinh(radNam.isSelected());
        nv.setTrangThai(chkTrangThai.isSelected());
        nv.setQueQuan(txtQueQuan.getText());
        return nv;
    }
}
