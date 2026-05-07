package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import entity.LoaiKhuyenMai;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuanLyKhuyenMai extends JPanel implements ActionListener, MouseListener {
    private DefaultTableModel tableModel;
    private JTable table;
    private KhuyenMai_DAO km_dao;

    private JLabel lblMaKM, lblTenKM, lblNgayBD, lblNgayKT, lblLoaiKM, lblChietKhau;
    private JTextField txtMaKM, txtTenKM, txtChietKhau, txtTimKiem;
    private JComboBox<String> cbLoaiKM;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang, btnLamMoi, btnTimKiem;
    private JSpinner spnNgayBD, spnNgayKT;

    public QuanLyKhuyenMai() {
        km_dao = new KhuyenMai_DAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        // ====== TIÊU ĐỀ ======
        JPanel pNorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pNorth.setBackground(new Color(30, 144, 255));
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHUYẾN MÃI");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setForeground(Color.WHITE);
        pNorth.add(lblTieuDe);
        add(pNorth, BorderLayout.NORTH);

        // ====== FORM NHẬP ======
        Box b = Box.createVerticalBox();
        b.setBorder(new EmptyBorder(20, 40, 20, 40));
        Dimension txtSize = new Dimension(250, 28);
        Dimension lblSize = new Dimension(130, 28);

        // Mã khuyến mãi
        Box b1 = Box.createHorizontalBox();
        lblMaKM = new JLabel("Mã khuyến mãi:");
        txtMaKM = new JTextField();
        txtMaKM.setEditable(false);
        txtMaKM.setBackground(new Color(230, 230, 230));
        lblMaKM.setPreferredSize(lblSize);
        txtMaKM.setPreferredSize(txtSize);
        b1.add(lblMaKM);
        b1.add(Box.createHorizontalStrut(10));
        b1.add(txtMaKM);
        b.add(b1);
        b.add(Box.createVerticalStrut(15));

        // Tên khuyến mãi
        Box b2 = Box.createHorizontalBox();
        lblTenKM = new JLabel("Tên khuyến mãi:");
        txtTenKM = new JTextField();
        lblTenKM.setPreferredSize(lblSize);
        txtTenKM.setPreferredSize(txtSize);
        b2.add(lblTenKM);
        b2.add(Box.createHorizontalStrut(10));
        b2.add(txtTenKM);
        b.add(b2);
        b.add(Box.createVerticalStrut(15));

        // Ngày bắt đầu
        Box b3 = Box.createHorizontalBox();
        lblNgayBD = new JLabel("Ngày bắt đầu:");
        lblNgayBD.setPreferredSize(lblSize);
        spnNgayBD = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorBD = new JSpinner.DateEditor(spnNgayBD, "yyyy-MM-dd");
        spnNgayBD.setEditor(editorBD);
        spnNgayBD.setPreferredSize(txtSize);
        b3.add(lblNgayBD);
        b3.add(Box.createHorizontalStrut(10));
        b3.add(spnNgayBD);
        b.add(b3);
        b.add(Box.createVerticalStrut(15));

        // Ngày kết thúc
        Box b4 = Box.createHorizontalBox();
        lblNgayKT = new JLabel("Ngày kết thúc:");
        lblNgayKT.setPreferredSize(lblSize);
        spnNgayKT = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorKT = new JSpinner.DateEditor(spnNgayKT, "yyyy-MM-dd");
        spnNgayKT.setEditor(editorKT);
        spnNgayKT.setPreferredSize(txtSize);
        b4.add(lblNgayKT);
        b4.add(Box.createHorizontalStrut(10));
        b4.add(spnNgayKT);
        b.add(b4);
        b.add(Box.createVerticalStrut(15));

        // Loại khuyến mãi
        Box b5 = Box.createHorizontalBox();
        lblLoaiKM = new JLabel("Loại khuyến mãi:");
        cbLoaiKM = new JComboBox<>(new String[]{"THEO_KHACH_HANG", "THEO_PHONG"});
        lblLoaiKM.setPreferredSize(lblSize);
        cbLoaiKM.setPreferredSize(txtSize);
        b5.add(lblLoaiKM);
        b5.add(Box.createHorizontalStrut(10));
        b5.add(cbLoaiKM);
        b.add(b5);
        b.add(Box.createVerticalStrut(15));

        // Chiết khấu
        Box b6 = Box.createHorizontalBox();
        lblChietKhau = new JLabel("Chiết khấu:");
        txtChietKhau = new JTextField();
        lblChietKhau.setPreferredSize(lblSize);
        txtChietKhau.setPreferredSize(txtSize);
        b6.add(lblChietKhau);
        b6.add(Box.createHorizontalStrut(10));
        b6.add(txtChietKhau);
        b.add(b6);
        b.add(Box.createVerticalStrut(20));

        // ====== BUTTON ======
        Box b7 = Box.createHorizontalBox();
        btnThem = taoNut("Thêm", new Color(60, 179, 113));
        btnSua = taoNut("Sửa", new Color(255, 165, 0));
        btnXoa = taoNut("Xóa", new Color(220, 20, 60));
        btnXoaTrang = taoNut("Xóa trắng", new Color(100, 149, 237));
        btnLamMoi = taoNut("Làm mới", new Color(72, 61, 139));
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(160, 28));
        btnTimKiem = taoNut("Tìm kiếm", new Color(30, 144, 255));

        b7.add(btnThem);
        b7.add(Box.createHorizontalStrut(10));
        b7.add(btnSua);
        b7.add(Box.createHorizontalStrut(10));
        b7.add(btnXoa);
        b7.add(Box.createHorizontalStrut(10));
        b7.add(btnXoaTrang);
        b7.add(Box.createHorizontalStrut(10));
        b7.add(btnLamMoi);
        b7.add(Box.createHorizontalStrut(20));
        b7.add(txtTimKiem);
        b7.add(Box.createHorizontalStrut(10));
        b7.add(btnTimKiem);
        b.add(b7);
        b.add(Box.createVerticalStrut(25));

        // ====== BẢNG ======
        Box b8 = Box.createHorizontalBox();
        String[] headers = {"Mã KM", "Tên KM", "Ngày BĐ", "Ngày KT", "Loại KM", "Chiết khấu"};
        tableModel = new DefaultTableModel(headers, 0);
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(850, 320));
        b8.add(scroll);
        b.add(b8);
        add(b, BorderLayout.CENTER);

        // ====== SỰ KIỆN ======
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTimKiem.addActionListener(this);
        table.addMouseListener(this);

        DocDuLieuVaoTable();
    }

    private JButton taoNut(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        return btn;
    }

    private void DocDuLieuVaoTable() {
        List<KhuyenMai> list = km_dao.getAllKhuyenMai();
        tableModel.setRowCount(0);

        for (KhuyenMai km : list) {
            tableModel.addRow(new Object[] {
                km.getMaKhuyenMai(),
                km.getTenKhuyenMai(),
                km.getNgayBatDau(),
                km.getNgayKetThuc(),
                km.getLoaiKhuyenMai(),
                km.getChietKhau()
            });
        }
    }


    private KhuyenMai revertFromField() {
        try {
            String ma = txtMaKM.getText().trim();
            if (ma.isEmpty()) ma = null; // để DAO tự sinh mã

            String ten = txtTenKM.getText().trim();
            Date dateBD = (Date) spnNgayBD.getValue();
            Date dateKT = (Date) spnNgayKT.getValue();
            LocalDate bd = dateBD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate kt = dateKT.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LoaiKhuyenMai loai = LoaiKhuyenMai.valueOf(cbLoaiKM.getSelectedItem().toString());
            double chietKhau = Double.parseDouble(txtChietKhau.getText().trim());
            return new KhuyenMai(ma, ten, bd, kt, loai, chietKhau);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + e.getMessage());
            return null;
        }
    }

    private boolean validData() {
        String ten = txtTenKM.getText().trim();
        String chietKhauStr = txtChietKhau.getText().trim();

        if (ten.length() < 5) {
            JOptionPane.showMessageDialog(this, "Tên khuyến mãi phải có ít nhất 5 ký tự");
            return false;
        }

        // ===== KIỂM TRA CHIẾT KHẤU =====
        double ck;
        try {
            ck = Double.parseDouble(chietKhauStr);
            if (ck < 0 || ck > 100) {
                JOptionPane.showMessageDialog(this, "Chiết khấu phải từ 0 đến 100%");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Chiết khấu phải là số!");
            return false;
        }

        // ===== KIỂM TRA NGÀY =====
        Date dateBD = (Date) spnNgayBD.getValue();
        Date dateKT = (Date) spnNgayKT.getValue();
        LocalDate ngayBD = dateBD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ngayKT = dateKT.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ngayHienTai = LocalDate.now();

        if (ngayBD.isBefore(ngayHienTai)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải từ hôm nay trở đi!");
            return false;
        }
        if (!ngayKT.isAfter(ngayBD)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!");
            return false;
        }

        return true;
    }


    private void xoaTrang() {
        txtMaKM.setText("");
        txtTenKM.setText("");
        txtChietKhau.setText("");
        txtTimKiem.setText("");
        cbLoaiKM.setSelectedIndex(0);
    }

    // ====== ACTION ======
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnThem) themKhuyenMai();
        else if (o == btnSua) suaKhuyenMai();
        else if (o == btnXoa) xoaKhuyenMai();
        else if (o == btnLamMoi) DocDuLieuVaoTable();
        else if (o == btnXoaTrang) xoaTrang();
        else if (o == btnTimKiem) timKhuyenMai();
    }

    private void themKhuyenMai() {
        if (!validData()) return;
        KhuyenMai km = revertFromField();
        if (km != null && km_dao.create(km)) {
            tableModel.addRow(new Object[]{
                    km.getMaKhuyenMai(),
                    km.getTenKhuyenMai(),
                    km.getNgayBatDau(),
                    km.getNgayKetThuc(),
                    km.getLoaiKhuyenMai(),
                    km.getChietKhau()
            });
            JOptionPane.showMessageDialog(this, "Thêm thành công! Mã mới: " + km.getMaKhuyenMai());
            txtMaKM.setText(km.getMaKhuyenMai());
            xoaTrang();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm khuyến mãi (trùng mã?)");
        }
    }

    private void suaKhuyenMai() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa!");
            return;
        }
        if (!validData()) return;
        KhuyenMai km = revertFromField();
        if (km != null && km_dao.update(km)) {
            tableModel.setValueAt(km.getTenKhuyenMai(), row, 1);
            tableModel.setValueAt(km.getNgayBatDau(), row, 2);
            tableModel.setValueAt(km.getNgayKetThuc(), row, 3);
            tableModel.setValueAt(km.getLoaiKhuyenMai(), row, 4);
            tableModel.setValueAt(km.getChietKhau(), row, 5);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        }
    }

    private void xoaKhuyenMai() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa!");
            return;
        }
        String ma = table.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa " + ma + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && km_dao.delete(ma)) {
            tableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
        }
    }

    private void timKhuyenMai() {
        String ma = txtTimKiem.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã khuyến mãi cần tìm!");
            return;
        }
        KhuyenMai km = km_dao.search(ma);
        tableModel.setRowCount(0);
        if (km != null) {
            tableModel.addRow(new Object[]{
                    km.getMaKhuyenMai(),
                    km.getTenKhuyenMai(),
                    km.getNgayBatDau(),
                    km.getNgayKetThuc(),
                    km.getLoaiKhuyenMai(),
                    km.getChietKhau()
            });
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mã " + ma);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            // Gán dữ liệu text field
            txtMaKM.setText(tableModel.getValueAt(row, 0).toString());
            txtTenKM.setText(tableModel.getValueAt(row, 1).toString());
            txtChietKhau.setText(tableModel.getValueAt(row, 5).toString());
            cbLoaiKM.setSelectedItem(tableModel.getValueAt(row, 4).toString());

            // Gán dữ liệu ngày bắt đầu và kết thúc
            Object objNgayBD = tableModel.getValueAt(row, 2);
            Object objNgayKT = tableModel.getValueAt(row, 3);

            try {
                if (objNgayBD != null) {
                    LocalDate ngayBD = LocalDate.parse(objNgayBD.toString());
                    Date dateBD = Date.from(ngayBD.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    spnNgayBD.setValue(dateBD);
                }
                if (objNgayKT != null) {
                    LocalDate ngayKT = LocalDate.parse(objNgayKT.toString());
                    Date dateKT = Date.from(ngayKT.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    spnNgayKT.setValue(dateKT);
                }
            } catch (Exception ex) {
                System.err.println("Lỗi khi chuyển đổi ngày trong mouseClicked: " + ex.getMessage());
            }
        }
    }


    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
