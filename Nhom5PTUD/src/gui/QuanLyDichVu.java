package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import dao.DichVu_DAO;
import entity.DichVu;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class QuanLyDichVu extends JPanel implements ActionListener, MouseListener {
    private DefaultTableModel tableModel;
    private JTable table;
    private DichVu_DAO dv_dao;

    private JLabel lblTenDV, lblMaDV, lblGiaTien, lblMoTa;
    private JTextField txtTenDV, txtMaDV, txtGiaTien, txtTimKiem;
    private JTextArea txtMoTa;
    private JButton btnXoaTrang, btnLamMoi, btnThem, btnTimKiem, btnSua;

    public QuanLyDichVu() {
        dv_dao = new DichVu_DAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));

        // ====== HEADER ======
        JPanel pNorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pNorth.setBackground(new Color(220, 0, 0));
        pNorth.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblTieuDe = new JLabel("QUẢN LÝ DỊCH VỤ");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 28));
        lblTieuDe.setForeground(Color.WHITE);
        pNorth.add(lblTieuDe);
        add(pNorth, BorderLayout.NORTH);

        // ====== MAIN CONTENT ======
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBorder(new EmptyBorder(20, 40, 20, 40));
        pnlMain.setBackground(new Color(245, 247, 250));
        add(pnlMain, BorderLayout.CENTER);

        // ====== FORM INPUT ======
        JPanel pnlForm = createFormPanel();
        pnlMain.add(pnlForm, BorderLayout.NORTH);

        // ====== TABLE ======
        JScrollPane scrollTable = createTable();
        pnlMain.add(scrollTable, BorderLayout.CENTER);

        // ====== BUTTON PANEL ======
        JPanel pnlButtons = createButtonPanel();
        pnlMain.add(pnlButtons, BorderLayout.SOUTH);

        // ====== EVENTS ======
        attachEvents();
        loadTableData();
    }

    private JPanel createFormPanel() {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        pnlForm.setPreferredSize(new Dimension(0, 220));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        int row = 0;

        // Row 1: Mã DV và Tên DV
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.1;
        lblMaDV = new JLabel("Mã DV:");
        lblMaDV.setFont(labelFont);
        pnlForm.add(lblMaDV, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.4;
        txtMaDV = createStyledTextField();
        txtMaDV.setFont(fieldFont);
        pnlForm.add(txtMaDV, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.1;
        lblTenDV = new JLabel("Tên DV:");
        lblTenDV.setFont(labelFont);
        pnlForm.add(lblTenDV, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.4;
        txtTenDV = createStyledTextField();
        txtTenDV.setFont(fieldFont);
        pnlForm.add(txtTenDV, gbc);
        row++;

        // Row 2: Giá tiền
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.1;
        lblGiaTien = new JLabel("Giá tiền:");
        lblGiaTien.setFont(labelFont);
        pnlForm.add(lblGiaTien, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.4;
        txtGiaTien = createStyledTextField();
        txtGiaTien.setFont(fieldFont);
        pnlForm.add(txtGiaTien, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.1;
        lblMoTa = new JLabel("Mô tả:");
        lblMoTa.setFont(labelFont);
        pnlForm.add(lblMoTa, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.4;
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setFont(fieldFont);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollMoTa.setPreferredSize(new Dimension(0, 70));
        pnlForm.add(scrollMoTa, gbc);

        return pnlForm;
    }

    private JScrollPane createTable() {
        String[] headers = {"Mã Dịch Vụ", "Tên Dịch Vụ", "Giá Tiền", "Mô Tả"};
        tableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép edit trực tiếp
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(255, 235, 235));
        table.setSelectionForeground(Color.BLACK);
        
        // Header table
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 0, 0));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scroll.setPreferredSize(new Dimension(0, 300));
        
        return scroll;
    }

    private JPanel createButtonPanel() {
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlButtons.setBackground(new Color(245, 247, 250));
        pnlButtons.setBorder(new EmptyBorder(20, 0, 10, 0));

        // Search panel
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setBackground(new Color(245, 247, 250));
        JLabel lblTim = new JLabel("Tìm theo mã:");
        lblTim.setFont(new Font("Arial", Font.BOLD, 14));
        pnlSearch.add(lblTim);
        
        txtTimKiem = createStyledTextField();
        txtTimKiem.setPreferredSize(new Dimension(200, 35));
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 14));
        pnlSearch.add(txtTimKiem);
        
        btnTimKiem = createStyledButton("🔍 Tìm kiếm", new Color(0, 102, 204));
        pnlSearch.add(btnTimKiem);

        // Function buttons
        JPanel pnlFunctionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlFunctionButtons.setBackground(new Color(245, 247, 250));
        
        btnThem = createStyledButton("➕ Thêm mới", new Color(40, 167, 69));
        btnSua = createStyledButton("✏️ Cập nhật", new Color(255, 193, 7));
        btnXoaTrang = createStyledButton("🗑️ Xóa trắng", new Color(108, 117, 125));
        btnLamMoi = createStyledButton("🔄 Làm mới", new Color(23, 162, 184));

        pnlFunctionButtons.add(btnThem);
        pnlFunctionButtons.add(btnSua);
        pnlFunctionButtons.add(btnXoaTrang);
        pnlFunctionButtons.add(btnLamMoi);

        // Add to main panel
        pnlButtons.add(pnlSearch);
        pnlButtons.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlButtons.add(pnlFunctionButtons);

        return pnlButtons;
    }

    private JTextField createStyledTextField() {
        JTextField txt = new JTextField();
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txt.setBackground(Color.WHITE);
        return txt;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker()),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }

    private void attachEvents() {
        btnLamMoi.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnTimKiem.addActionListener(this);
        
        table.addMouseListener(this);
    }

    private void loadTableData() {
        List<DichVu> list = dv_dao.getAllDichVu();
        tableModel.setRowCount(0);
        for (DichVu dv : list) {
            tableModel.addRow(new Object[]{
                dv.getMaDichVu(),
                dv.getTenDichVu(),
                String.format("%,d VND", (int) dv.getGiaTien()),
                dv.getMoTa()
            });
        }
    }

    private DichVu getDataFromForm() {
        String maDV = txtMaDV.getText().trim();
        String tenDV = txtTenDV.getText().trim();
        double giaTien = Double.parseDouble(txtGiaTien.getText().trim());
        String moTa = txtMoTa.getText().trim();
        return new DichVu(maDV, tenDV, giaTien, moTa);
    }

    private boolean validateData() {
        String maDV = txtMaDV.getText().trim();
        String tenDV = txtTenDV.getText().trim();
        String giaTienStr = txtGiaTien.getText().trim();

        if (maDV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Dịch Vụ không được để trống");
            txtMaDV.requestFocus();
            return false;
        }
        if (!maDV.matches("^DV\\d+$")) {
            JOptionPane.showMessageDialog(this, "Mã dịch vụ phải có định dạng DVxxx (VD: DV001)");
            txtMaDV.requestFocus();
            return false;
        }
        if (tenDV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên dịch vụ không được để trống");
            txtTenDV.requestFocus();
            return false;
        }
        if (giaTienStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá tiền không được để trống");
            txtGiaTien.requestFocus();
            return false;
        }
        try {
            double giaTien = Double.parseDouble(giaTienStr);
            if (giaTien <= 0) {
                JOptionPane.showMessageDialog(this, "Giá tiền phải lớn hơn 0");
                txtGiaTien.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá tiền phải là số hợp lệ");
            txtGiaTien.requestFocus();
            return false;
        }
        return true;
    }

    private void addDichVu() {
        if (validateData()) {
            DichVu dv = getDataFromForm();
            
            // Kiểm tra mã dịch vụ đã tồn tại chưa
            if (dv_dao.isMaDichVuExists(dv.getMaDichVu())) {
                JOptionPane.showMessageDialog(this, "❌ Mã dịch vụ đã tồn tại!");
                return;
            }
            
            if (dv_dao.addDichVu(dv)) {
                JOptionPane.showMessageDialog(this, "✅ Thêm dịch vụ thành công!");
                loadTableData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Thêm dịch vụ thất bại!");
            }
        }
    }

    private void updateDichVu() {
        if (validateData()) {
            DichVu dv = getDataFromForm();
            
            if (dv_dao.updateDichVu(dv)) {
                JOptionPane.showMessageDialog(this, "✏️ Cập nhật dịch vụ thành công!");
                loadTableData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Cập nhật dịch vụ thất bại!");
            }
        }
    }

    private void searchDichVu() {
        String maDV = txtTimKiem.getText().trim();
        if (maDV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã dịch vụ cần tìm");
            txtTimKiem.requestFocus();
            return;
        }
        
        DichVu dv = dv_dao.searchDichVu(maDV);
        tableModel.setRowCount(0);
        if (dv != null) {
            tableModel.addRow(new Object[]{
                dv.getMaDichVu(),
                dv.getTenDichVu(),
                String.format("%,d VND", (int) dv.getGiaTien()),
                dv.getMoTa()
            });
            JOptionPane.showMessageDialog(this, "✅ Tìm thấy dịch vụ: " + dv.getTenDichVu());
        } else {
            JOptionPane.showMessageDialog(this, "❌ Không tìm thấy dịch vụ có mã " + maDV);
            loadTableData(); // Hiển thị lại toàn bộ dữ liệu
        }
    }

    private void clearForm() {
        txtMaDV.setText("");
        txtTenDV.setText("");
        txtGiaTien.setText("");
        txtMoTa.setText("");
        txtTimKiem.setText("");
        txtMaDV.requestFocus();
    }

    private void deleteDichVu() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn dịch vụ cần xóa!");
            return;
        }

        String maDV = tableModel.getValueAt(selectedRow, 0).toString();
        String tenDV = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa dịch vụ:\n" + tenDV + " (" + maDV + ")?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (dv_dao.deleteDichVu(maDV)) {
                JOptionPane.showMessageDialog(this, "🗑️ Đã xóa dịch vụ thành công!");
                loadTableData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Xóa dịch vụ thất bại!");
            }
        }
    }

    // ====== EVENT HANDLERS ======
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnThem) {
            addDichVu();
        } else if (source == btnSua) {
            updateDichVu();
        } else if (source == btnXoaTrang) {
            clearForm();
        } else if (source == btnLamMoi) {
            loadTableData();
            clearForm();
        } else if (source == btnTimKiem) {
            searchDichVu();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) { // Double click để xóa
            deleteDichVu();
        } else { // Single click để hiển thị lên form
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaDV.setText(tableModel.getValueAt(row, 0).toString());
                txtTenDV.setText(tableModel.getValueAt(row, 1).toString());
                String giaTienStr = tableModel.getValueAt(row, 2).toString().replace(" VND", "").replace(",", "");
                txtGiaTien.setText(giaTienStr);
                txtMoTa.setText(tableModel.getValueAt(row, 3).toString());
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}