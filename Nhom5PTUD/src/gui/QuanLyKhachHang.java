package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import dao.KhachHang_DAO;
import entity.KhachHang;
import entity.LoaiKhachHang;

public class QuanLyKhachHang extends JPanel implements ActionListener, MouseListener {

    private DefaultTableModel tableModel;
    private JTable table;
    private KhachHang_DAO kh_dao;

    private JLabel lblMaKH, lblHoTen, lblSoDT, lblNgaySinh, lblLoaiKH;
    private JTextField txtMaKH, txtHoTen, txtSoDT, txtTimKiem;
    private JComboBox<String> cbLoaiKH;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang, btnLamMoi, btnTimKiem;
    private JSpinner spnNgaySinh;

    public QuanLyKhachHang() {
        kh_dao = new KhachHang_DAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        JPanel pNorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pNorth.setBackground(new Color(65, 105, 225));
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        lblTieuDe.setForeground(Color.WHITE);
        pNorth.add(lblTieuDe);
        add(pNorth, BorderLayout.NORTH);

        Box b = Box.createVerticalBox();
        b.setBorder(new EmptyBorder(20, 40, 20, 40));
        Dimension txtSize = new Dimension(250, 28);
        Dimension lblSize = new Dimension(130, 28);

        // Mã KH
        Box b1 = Box.createHorizontalBox();
        lblMaKH = new JLabel("Mã khách hàng:");
        txtMaKH = new JTextField();
        txtMaKH.setEditable(false);
        txtMaKH.setBackground(new Color(230, 230, 230));
        lblMaKH.setPreferredSize(lblSize);
        txtMaKH.setPreferredSize(txtSize);
        b1.add(lblMaKH); b1.add(Box.createHorizontalStrut(10)); b1.add(txtMaKH);
        b.add(b1); b.add(Box.createVerticalStrut(15));

        // Họ tên
        Box b2 = Box.createHorizontalBox();
        lblHoTen = new JLabel("Họ tên:");
        txtHoTen = new JTextField();
        lblHoTen.setPreferredSize(lblSize);
        txtHoTen.setPreferredSize(txtSize);
        b2.add(lblHoTen); b2.add(Box.createHorizontalStrut(10)); b2.add(txtHoTen);
        b.add(b2); b.add(Box.createVerticalStrut(15));

        // SĐT
        Box b3 = Box.createHorizontalBox();
        lblSoDT = new JLabel("Số điện thoại:");
        txtSoDT = new JTextField();
        lblSoDT.setPreferredSize(lblSize);
        txtSoDT.setPreferredSize(txtSize);
        b3.add(lblSoDT); b3.add(Box.createHorizontalStrut(10)); b3.add(txtSoDT);
        b.add(b3); b.add(Box.createVerticalStrut(15));

        // Ngày sinh
        Box b4 = Box.createHorizontalBox();
        lblNgaySinh = new JLabel("Ngày sinh:");
        spnNgaySinh = new JSpinner(new SpinnerDateModel());
        spnNgaySinh.setEditor(new JSpinner.DateEditor(spnNgaySinh, "yyyy-MM-dd"));
        lblNgaySinh.setPreferredSize(lblSize);
        spnNgaySinh.setPreferredSize(txtSize);
        b4.add(lblNgaySinh); b4.add(Box.createHorizontalStrut(10)); b4.add(spnNgaySinh);
        b.add(b4); b.add(Box.createVerticalStrut(15));

        // Loại KH
        Box b5 = Box.createHorizontalBox();
        lblLoaiKH = new JLabel("Loại khách hàng:");
        cbLoaiKH = new JComboBox<>(new String[]{"KHACH_VANG_LAI", "KHACH_HOI_VIEN"});
        lblLoaiKH.setPreferredSize(lblSize);
        cbLoaiKH.setPreferredSize(txtSize);
        b5.add(lblLoaiKH); b5.add(Box.createHorizontalStrut(10)); b5.add(cbLoaiKH);
        b.add(b5); b.add(Box.createVerticalStrut(20));

        // Buttons
        Box b6 = Box.createHorizontalBox();
        btnThem = taoNut("Thêm", new Color(60, 179, 113));
        btnSua = taoNut("Sửa", new Color(255, 165, 0));
        btnXoa = taoNut("Xóa", new Color(220, 20, 60));
        btnXoaTrang = taoNut("Xóa trắng", new Color(100, 149, 237));
        btnLamMoi = taoNut("Làm mới", new Color(72, 61, 139));
        txtTimKiem = new JTextField(); txtTimKiem.setPreferredSize(new Dimension(160, 28));
        btnTimKiem = taoNut("Tìm kiếm", new Color(30, 144, 255));
        b6.add(btnThem); b6.add(Box.createHorizontalStrut(10));
        b6.add(btnSua); b6.add(Box.createHorizontalStrut(10));
        b6.add(btnXoa); b6.add(Box.createHorizontalStrut(10));
        b6.add(btnXoaTrang); b6.add(Box.createHorizontalStrut(10));
        b6.add(btnLamMoi); b6.add(Box.createHorizontalStrut(20));
        b6.add(txtTimKiem); b6.add(Box.createHorizontalStrut(10));
        b6.add(btnTimKiem);
        b.add(b6); b.add(Box.createVerticalStrut(25));

        // Bảng
        Box b7 = Box.createHorizontalBox();
        String[] headers = {"Mã KH", "Họ tên", "SĐT", "Ngày sinh", "Loại KH"};
        tableModel = new DefaultTableModel(headers, 0);
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(850, 320));
        b7.add(scroll);
        b.add(b7);
        add(b, BorderLayout.CENTER);

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnXoaTrang.addActionListener(this);
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
        return btn;
    }

    private void DocDuLieuVaoTable() {
        tableModel.setRowCount(0);
        List<KhachHang> list = kh_dao.getAllKhachHang();
        for (KhachHang kh : list) {
            tableModel.addRow(new Object[]{ kh.getMaKhachHang(), kh.getHoTen(),
                    kh.getSoDienThoai(), kh.getNgaySinh(), kh.getLoaiKhachHang() });
        }
    }

    private KhachHang revertFromField() {
        try {
            String ma = txtMaKH.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String sdt = txtSoDT.getText().trim();

            Date date = (Date) spnNgaySinh.getValue();
            LocalDate ngaySinh = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LoaiKhachHang loai = LoaiKhachHang.valueOf(cbLoaiKH.getSelectedItem().toString());

            return new KhachHang(ma, hoTen, sdt, ngaySinh, loai);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu!");
            return null;
        }
    }

    private void themKH() {
        KhachHang kh = revertFromField();
        if (kh != null) {
            if (kh_dao.create(kh)) {
                DocDuLieuVaoTable();
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                xoaTrang();
            } else {
                JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại!");
            }
        }
    }

    private void suaKH() {
        KhachHang kh = revertFromField();
        if (kh != null) {
            if (kh_dao.update(kh)) {
                DocDuLieuVaoTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            }
        }
    }

    private void xoaKH() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String ma = tableModel.getValueAt(row, 0).toString();
            if (kh_dao.delete(ma)) {
                DocDuLieuVaoTable();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                xoaTrang();
            }
        }
    }

    private void timKH() {
        String ma = txtTimKiem.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã KH cần tìm!");
            return;
        }
        KhachHang kh = kh_dao.search(ma);
        tableModel.setRowCount(0);
        if (kh != null) {
            tableModel.addRow(new Object[]{
                    kh.getMaKhachHang(), kh.getHoTen(), kh.getSoDienThoai(),
                    kh.getNgaySinh(), kh.getLoaiKhachHang()
            });
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm ra!");
        }
    }

    private void xoaTrang() {
        txtMaKH.setText("");
        txtHoTen.setText("");
        txtSoDT.setText("");
        txtTimKiem.setText("");
        spnNgaySinh.setValue(new Date());
        cbLoaiKH.setSelectedIndex(0);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaKH.setText(tableModel.getValueAt(row, 0).toString());
            txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
            txtSoDT.setText(tableModel.getValueAt(row, 2).toString());
            LocalDate d = (LocalDate) tableModel.getValueAt(row, 3);
            spnNgaySinh.setValue(Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            cbLoaiKH.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
