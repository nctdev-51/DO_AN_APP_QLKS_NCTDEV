package gui;

import client.network.SocketClient;
import client.service.KhachHangRemoteService;
import client.service.KhachHangRemoteServiceImpl;
import dto.KhachHangDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuanLyKhachHang extends JPanel implements ActionListener, MouseListener {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final KhachHangRemoteService khachHangRemoteService;

    private JTextField txtMaKH, txtHoTen, txtSoDT, txtTimKiem;
    private JComboBox<String> cbLoaiKH;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang, btnLamMoi, btnTimKiem;
    private JSpinner spnNgaySinh;

    public QuanLyKhachHang() {
        khachHangRemoteService = new KhachHangRemoteServiceImpl(new SocketClient("127.0.0.1", 9999, 3000, 5000));

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        JPanel pNorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pNorth.setBackground(new Color(65, 105, 225));
        JLabel lblTieuDe = new JLabel("QUAN LY KHACH HANG");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        lblTieuDe.setForeground(Color.WHITE);
        pNorth.add(lblTieuDe);
        add(pNorth, BorderLayout.NORTH);

        Box b = Box.createVerticalBox();
        b.setBorder(new EmptyBorder(20, 40, 20, 40));
        Dimension txtSize = new Dimension(250, 28);
        Dimension lblSize = new Dimension(130, 28);

        Box b1 = Box.createHorizontalBox();
        JLabel lblMaKH = new JLabel("Ma khach hang:");
        txtMaKH = new JTextField();
        txtMaKH.setEditable(false);
        txtMaKH.setBackground(new Color(230, 230, 230));
        lblMaKH.setPreferredSize(lblSize);
        txtMaKH.setPreferredSize(txtSize);
        b1.add(lblMaKH);
        b1.add(Box.createHorizontalStrut(10));
        b1.add(txtMaKH);
        b.add(b1);
        b.add(Box.createVerticalStrut(15));

        Box b2 = Box.createHorizontalBox();
        JLabel lblHoTen = new JLabel("Ho ten:");
        txtHoTen = new JTextField();
        lblHoTen.setPreferredSize(lblSize);
        txtHoTen.setPreferredSize(txtSize);
        b2.add(lblHoTen);
        b2.add(Box.createHorizontalStrut(10));
        b2.add(txtHoTen);
        b.add(b2);
        b.add(Box.createVerticalStrut(15));

        Box b3 = Box.createHorizontalBox();
        JLabel lblSoDT = new JLabel("So dien thoai:");
        txtSoDT = new JTextField();
        lblSoDT.setPreferredSize(lblSize);
        txtSoDT.setPreferredSize(txtSize);
        b3.add(lblSoDT);
        b3.add(Box.createHorizontalStrut(10));
        b3.add(txtSoDT);
        b.add(b3);
        b.add(Box.createVerticalStrut(15));

        Box b4 = Box.createHorizontalBox();
        JLabel lblNgaySinh = new JLabel("Ngay sinh:");
        spnNgaySinh = new JSpinner(new SpinnerDateModel());
        spnNgaySinh.setEditor(new JSpinner.DateEditor(spnNgaySinh, "yyyy-MM-dd"));
        lblNgaySinh.setPreferredSize(lblSize);
        spnNgaySinh.setPreferredSize(txtSize);
        b4.add(lblNgaySinh);
        b4.add(Box.createHorizontalStrut(10));
        b4.add(spnNgaySinh);
        b.add(b4);
        b.add(Box.createVerticalStrut(15));

        Box b5 = Box.createHorizontalBox();
        JLabel lblLoaiKH = new JLabel("Loai khach hang:");
        cbLoaiKH = new JComboBox<>(new String[]{"KHACH_VANG_LAI", "KHACH_HOI_VIEN"});
        lblLoaiKH.setPreferredSize(lblSize);
        cbLoaiKH.setPreferredSize(txtSize);
        b5.add(lblLoaiKH);
        b5.add(Box.createHorizontalStrut(10));
        b5.add(cbLoaiKH);
        b.add(b5);
        b.add(Box.createVerticalStrut(20));

        Box b6 = Box.createHorizontalBox();
        btnThem = taoNut("Them", new Color(60, 179, 113));
        btnSua = taoNut("Sua", new Color(255, 165, 0));
        btnXoa = taoNut("Xoa", new Color(220, 20, 60));
        btnXoaTrang = taoNut("Xoa trang", new Color(100, 149, 237));
        btnLamMoi = taoNut("Lam moi", new Color(72, 61, 139));
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(160, 28));
        btnTimKiem = taoNut("Tim kiem", new Color(30, 144, 255));
        b6.add(btnThem);
        b6.add(Box.createHorizontalStrut(10));
        b6.add(btnSua);
        b6.add(Box.createHorizontalStrut(10));
        b6.add(btnXoa);
        b6.add(Box.createHorizontalStrut(10));
        b6.add(btnXoaTrang);
        b6.add(Box.createHorizontalStrut(10));
        b6.add(btnLamMoi);
        b6.add(Box.createHorizontalStrut(20));
        b6.add(txtTimKiem);
        b6.add(Box.createHorizontalStrut(10));
        b6.add(btnTimKiem);
        b.add(b6);
        b.add(Box.createVerticalStrut(25));

        Box b7 = Box.createHorizontalBox();
        tableModel = new DefaultTableModel(new String[]{"Ma KH", "Ho ten", "SDT", "Ngay sinh", "Loai KH"}, 0);
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

        docDuLieuVaoTable();
    }

    private JButton taoNut(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return btn;
    }

    private void docDuLieuVaoTable() {
        tableModel.setRowCount(0);
        List<KhachHangDTO> list;
        try {
            list = khachHangRemoteService.getAllKhachHang();
        } catch (Exception ex) {
            list = new ArrayList<>();
            hienThiLoi(ex.getMessage());
        }
        for (KhachHangDTO kh : list) {
            tableModel.addRow(new Object[]{kh.getMaKhachHang(), kh.getHoTen(), kh.getSoDienThoai(), kh.getNgaySinh(), kh.getLoaiKhachHang()});
        }
    }

    private KhachHangDTO fromField() {
        Date date = (Date) spnNgaySinh.getValue();
        LocalDate ngaySinh = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return new KhachHangDTO(
                txtMaKH.getText().trim(),
                txtHoTen.getText().trim(),
                txtSoDT.getText().trim(),
                ngaySinh,
                cbLoaiKH.getSelectedItem().toString()
        );
    }

    private void hienThiLoi(String msg) {
        JOptionPane.showMessageDialog(this, msg == null ? "Loi he thong" : msg, "Loi", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == btnThem) {
                khachHangRemoteService.createKhachHang(fromField());
                docDuLieuVaoTable();
            } else if (src == btnSua) {
                khachHangRemoteService.updateKhachHang(fromField());
                docDuLieuVaoTable();
            } else if (src == btnXoa) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    khachHangRemoteService.deleteKhachHang(tableModel.getValueAt(row, 0).toString());
                    docDuLieuVaoTable();
                }
            } else if (src == btnTimKiem) {
                String ma = txtTimKiem.getText().trim();
                KhachHangDTO kh = khachHangRemoteService.searchByMa(ma);
                tableModel.setRowCount(0);
                if (kh != null) {
                    tableModel.addRow(new Object[]{kh.getMaKhachHang(), kh.getHoTen(), kh.getSoDienThoai(), kh.getNgaySinh(), kh.getLoaiKhachHang()});
                }
            } else if (src == btnLamMoi) {
                docDuLieuVaoTable();
            } else if (src == btnXoaTrang) {
                txtMaKH.setText("");
                txtHoTen.setText("");
                txtSoDT.setText("");
                txtTimKiem.setText("");
                spnNgaySinh.setValue(new Date());
                cbLoaiKH.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            hienThiLoi(ex.getMessage());
        }
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
}
