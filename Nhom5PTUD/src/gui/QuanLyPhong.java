package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.Phong_DAO;
import entity.LoaiPhong;
import entity.Phong;
import entity.TinhTrangPhong; // <<< 1. IMPORT ENUM

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class QuanLyPhong extends JPanel implements ActionListener {

    private JTextField txtMaPhong, txtTenPhong, txtGiaPhong, txtSucChua, txtLoaiGiuong, txtTimKiem;
    private JComboBox<LoaiPhong> cboLoaiPhong;
    // <<< 2. THAY ĐỔI: JComboBox phải dùng enum TinhTrangPhong >>>
    private JComboBox<TinhTrangPhong> cboTinhTrang;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTim;
    private JTable table;
    private DefaultTableModel model;
    
    private Phong_DAO phongDAO;

    private final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private final Color COLOR_BG = new Color(245, 247, 250);

    // Xóa các hằng số String, chúng ta sẽ dùng enum TinhTrangPhong.values()
    
    public QuanLyPhong() {
        phongDAO = new Phong_DAO(); 
        
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formContainer = createFormPanel();
        add(formContainer, BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        // Tải dữ liệu ban đầu
        loadDataToTable(phongDAO.getDanhSachPhong());
    }

    private JPanel createFormPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ==== Dòng 1 ====
        gbc.gridx = 0; gbc.gridy = 0;
        pnl.add(new JLabel("Mã phòng:"), gbc);
        gbc.gridx = 1;
        txtMaPhong = new JTextField(15);
        pnl.add(txtMaPhong, gbc);

        gbc.gridx = 2;
        pnl.add(new JLabel("Tên phòng:"), gbc);
        gbc.gridx = 3;
        txtTenPhong = new JTextField(15);
        pnl.add(txtTenPhong, gbc);

        // ==== Dòng 2 ====
        gbc.gridx = 0; gbc.gridy = 1;
        pnl.add(new JLabel("Loại phòng:"), gbc);
        gbc.gridx = 1;
        // JComboBox này chứa các đối tượng LoaiPhong (Enum)
        cboLoaiPhong = new JComboBox<>(LoaiPhong.values()); 
        pnl.add(cboLoaiPhong, gbc);

        gbc.gridx = 2;
        pnl.add(new JLabel("Giá phòng:"), gbc);
        gbc.gridx = 3;
        txtGiaPhong = new JTextField(15);
        pnl.add(txtGiaPhong, gbc);

        // ==== Dòng 3 ====
        gbc.gridx = 0; gbc.gridy = 2;
        pnl.add(new JLabel("Sức chứa:"), gbc);
        gbc.gridx = 1;
        txtSucChua = new JTextField(15);
        pnl.add(txtSucChua, gbc);

        gbc.gridx = 2;
        pnl.add(new JLabel("Loại giường:"), gbc);
        gbc.gridx = 3;
        txtLoaiGiuong = new JTextField(15);
        pnl.add(txtLoaiGiuong, gbc);

        // ==== Dòng 4 ====
        gbc.gridx = 0; gbc.gridy = 3;
        pnl.add(new JLabel("Tình trạng:"), gbc);
        gbc.gridx = 1;
        // <<< 3. THAY ĐỔI: JComboBox chứa các đối tượng TinhTrangPhong (Enum) >>>
        cboTinhTrang = new JComboBox<>(TinhTrangPhong.values());
        pnl.add(cboTinhTrang, gbc);

        gbc.gridx = 2;
        pnl.add(new JLabel("Tìm kiếm:"), gbc);
        gbc.gridx = 3;
        txtTimKiem = new JTextField(15);
        pnl.add(txtTimKiem, gbc);

        // ==== Nút ====
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        pnlBtn.setBackground(Color.WHITE);
        btnThem = taoButton("Thêm", new Color(46, 204, 113));
        btnSua = taoButton("Sửa", new Color(241, 196, 15));
        btnXoa = taoButton("Xóa", new Color(231, 76, 60));
        btnLamMoi = taoButton("Làm mới", new Color(52, 152, 219));
        btnTim = taoButton("Tìm", new Color(155, 89, 182));

        pnlBtn.add(btnThem);
        pnlBtn.add(btnSua);
        pnlBtn.add(btnXoa);
        pnlBtn.add(btnLamMoi);
        pnlBtn.add(btnTim);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        pnl.add(pnlBtn, gbc);

        return pnl;
    }

    private JScrollPane createTablePanel() {
        String[] colNames = {"Mã phòng", "Tên phòng", "Loại phòng", "Giá phòng", "Sức chứa", "Loại giường", "Tình trạng"};
        model = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(220, 220, 220));

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtMaPhong.setText(model.getValueAt(row, 0).toString());
                    txtTenPhong.setText(model.getValueAt(row, 1).toString());
                    
                    // <<< 4. THAY ĐỔI: Lấy ENUM từ model >>>
                    // Cột 2 (LoaiPhong) và Cột 6 (TinhTrangPhong) giờ là ENUM
                    LoaiPhong loaiPhong = (LoaiPhong) model.getValueAt(row, 2);
                    TinhTrangPhong tinhTrang = (TinhTrangPhong) model.getValueAt(row, 6);

                    cboLoaiPhong.setSelectedItem(loaiPhong);
                    
                    txtGiaPhong.setText(model.getValueAt(row, 3).toString());
                    txtSucChua.setText(model.getValueAt(row, 4).toString());
                    txtLoaiGiuong.setText(model.getValueAt(row, 5).toString());
                    
                    cboTinhTrang.setSelectedItem(tinhTrang);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_PRIMARY, 1, true),
                "Danh sách phòng",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                COLOR_PRIMARY
        ));

        return scroll;
    }

    private JButton taoButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setBorder(new LineBorder(bg.darker(), 1, true));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        btn.addActionListener(this);
        return btn;
    }

    private void loadDataToTable(List<Phong> dsPhong) {
        model.setRowCount(0);
        for (Phong p : dsPhong) {
            // <<< 5. THAY ĐỔI: Thêm trực tiếp ĐỐI TƯỢNG ENUM vào model >>>
            model.addRow(new Object[]{
                p.getMaPhong(),
                p.getTenPhong(),
                p.getLoaiPhong(),   // Thêm ENUM LoaiPhong
                p.getGiaPhong(),
                p.getSucChua(),
                p.getLoaiGiuong(),
                p.getTinhTrang()    // Thêm ENUM TinhTrangPhong
            });
        }
    }

    private void lamMoi() {
        txtMaPhong.setText("");
        txtTenPhong.setText("");
        txtGiaPhong.setText("");
        txtSucChua.setText("");
        txtLoaiGiuong.setText("");
        txtTimKiem.setText("");
        cboLoaiPhong.setSelectedIndex(0);
        cboTinhTrang.setSelectedIndex(0);
        loadDataToTable(phongDAO.getDanhSachPhong());
    }

    /**
     * ✅ SỬA LẠI:
     * Lấy Enum từ ComboBox và trả về đối tượng Phong
     * (đúng với constructor của entity.Phong)
     */
    private Phong taoPhongTuForm() throws Exception {
        String ma = txtMaPhong.getText().trim();
        String ten = txtTenPhong.getText().trim();
        
        // Lấy ENUM trực tiếp từ ComboBox
        LoaiPhong loai = (LoaiPhong) cboLoaiPhong.getSelectedItem();

        double gia;
        try {
            gia = Double.parseDouble(txtGiaPhong.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Giá phòng phải là số!");
        }

        int sucChua;
        try {
            sucChua = Integer.parseInt(txtSucChua.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Sức chứa phải là số nguyên!");
        }

        String loaiGiuong = txtLoaiGiuong.getText().trim();
        
        // Lấy ENUM trực tiếp từ ComboBox
        TinhTrangPhong tinhTrang = (TinhTrangPhong) cboTinhTrang.getSelectedItem();

        // Trả về đối tượng Phong đúng chuẩn
        return new Phong(ma, ten, loai, sucChua, loaiGiuong, gia, tinhTrang);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnThem) {
            try {
                Phong p = taoPhongTuForm();
                if (phongDAO.themPhong(p)) {
                    JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
                    loadDataToTable(phongDAO.getDanhSachPhong());
                } else {
                    JOptionPane.showMessageDialog(this, "Mã phòng đã tồn tại!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else if (o == btnSua) {
            try {
                Phong p = taoPhongTuForm();
                if (phongDAO.capNhatPhong(p)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadDataToTable(phongDAO.getDanhSachPhong());
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy mã phòng!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else if (o == btnXoa) {
            String ma = txtMaPhong.getText().trim();
            if (ma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (phongDAO.xoaPhong(ma)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadDataToTable(phongDAO.getDanhSachPhong());
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy mã phòng!");
                }
            }
        } else if (o == btnLamMoi) {
            lamMoi();
        } else if (o == btnTim) {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                loadDataToTable(phongDAO.getDanhSachPhong());
                return;
            }

            // <<< 6. THAY ĐỔI: Tìm kiếm dựa trên .toString() của Enum >>>
            List<Phong> ds = phongDAO.getDanhSachPhong().stream()
                    .filter(p -> p.getTenPhong().toLowerCase().contains(keyword) ||
                                  p.getMaPhong().toLowerCase().contains(keyword) ||
                                  p.getLoaiPhong().toString().toLowerCase().contains(keyword) || // Dùng toString()
                                  p.getTinhTrang().toString().toLowerCase().contains(keyword))  // Dùng toString()
                    .toList();
            loadDataToTable(ds);
        }
    }
}