package gui;

import entity.PhanCongCaLamViec;
import dao.PhanCongCaLamViec_DAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class NhanCa_Dialog extends JDialog {
    private PhanCongCaLamViec phanCong;
    private PhanCongCaLamViec_DAO phanCongDAO;
    private JLabel lblThongTin, lblCa, lblThoiGian;
    private JButton btnNhanCa, btnHuy;
    private boolean daNhanCa = false;

    public NhanCa_Dialog(Frame parent, PhanCongCaLamViec phanCong) {
        super(parent, "Nhận Ca Làm Việc", true);
        this.phanCong = phanCong;
        this.phanCongDAO = new PhanCongCaLamViec_DAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // ========== PANEL THÔNG TIN ==========
        JPanel pnlThongTin = new JPanel(new GridLayout(4, 1, 10, 10));
        pnlThongTin.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        lblThongTin = new JLabel("Bạn có ca làm việc được phân công:");
        lblThongTin.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblCa = new JLabel(phanCong.getTenCa() + " (" + phanCong.getThoiGianBatDau() + " - " + phanCong.getThoiGianKetThuc() + ")");
        lblCa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCa.setForeground(new Color(0, 100, 0));

        JLabel lblNgay = new JLabel("Ngày: " + new SimpleDateFormat("dd/MM/yyyy").format(phanCong.getNgayLamViec()));
        lblNgay.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblThoiGian = new JLabel("Thời gian hiện tại: " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new java.util.Date()));
        lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThoiGian.setForeground(Color.GRAY);

        pnlThongTin.add(lblThongTin);
        pnlThongTin.add(lblCa);
        pnlThongTin.add(lblNgay);
        pnlThongTin.add(lblThoiGian);

        add(pnlThongTin, BorderLayout.CENTER);

        // ========== PANEL NÚT ==========
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnNhanCa = new JButton("Nhận Ca");
        btnHuy = new JButton("Để Sau");

        styleButton(btnNhanCa, new Color(34, 139, 34));
        styleButton(btnHuy, new Color(108, 117, 125));

        btnNhanCa.addActionListener(e -> nhanCa());
        btnHuy.addActionListener(e -> dispose());

        pnlNut.add(btnNhanCa);
        pnlNut.add(btnHuy);

        add(pnlNut, BorderLayout.SOUTH);

        // Cập nhật thời gian mỗi giây
        Timer timer = new Timer(1000, e -> {
            lblThoiGian.setText("Thời gian hiện tại: " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new java.util.Date()));
        });
        timer.start();
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker()),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private void nhanCa() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        if (phanCongDAO.nhanCa(phanCong.getMaPhanCong(), now)) {
            JOptionPane.showMessageDialog(this, "Nhận ca thành công! Chúc bạn làm việc hiệu quả!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            daNhanCa = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Nhận ca thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isDaNhanCa() {
        return daNhanCa;
    }
}