package gui;

import core.entity.NhanVien;

import javax.swing.*;
import java.awt.*;

public class TrangChu_Gui extends JFrame {
    public TrangChu_Gui(NhanVien nhanVien) {
        setTitle("Trang chu - " + (nhanVien == null ? "" : nhanVien.getHoTen()));
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lbl = new JLabel("TRANG CHU HE THONG KHACH SAN", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lbl, BorderLayout.CENTER);
    }
}
