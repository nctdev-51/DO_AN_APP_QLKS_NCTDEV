package gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
// <<< THÊM IMPORT >>>
import javax.swing.border.TitledBorder; // Lỗi 1: Thiếu TitledBorder
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// <<< THÊM 2 IMPORT >>>
import java.awt.event.FocusAdapter; // Lỗi 2: Thiếu FocusAdapter
import java.awt.event.FocusEvent; // Lỗi 3: Thiếu FocusEvent
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.text.NumberFormatter;

/**
 * Một JPanel chuyên dụng để nhập tiền mặt,
 * bao gồm cả nút bấm mệnh giá và ô nhập tổng.
 * Giao diện được cải tiến để đẹp và dễ dùng hơn.
 */
public class TienMatPanel extends JPanel {

    private JFormattedTextField txtTongTien;
    private final DecimalFormat MONEY_FORMATTER_PLAIN = new DecimalFormat("#,##0");
    private final NumberFormatter numberFormatter;
    private double tongTien = 0;

    public TienMatPanel(String title) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 240), 1), 
            title, 
            TitledBorder.LEFT, // Lỗi TitledBorder được sửa
            TitledBorder.TOP,  // Lỗi TitledBorder được sửa
            new Font("Segoe UI", Font.BOLD, 14), 
            new Color(70, 130, 180) 
        ));

        // --- Cài đặt Formatter cho ô nhập tiền ---
        numberFormatter = new NumberFormatter(MONEY_FORMATTER_PLAIN);
        numberFormatter.setValueClass(Double.class);
        numberFormatter.setAllowsInvalid(false); 
        numberFormatter.setCommitsOnValidEdit(true);
        
        txtTongTien = new JFormattedTextField(numberFormatter);
        txtTongTien.setValue(0.0); 
        txtTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        txtTongTien.setForeground(new Color(0, 100, 0));
        txtTongTien.setHorizontalAlignment(JTextField.RIGHT);
        txtTongTien.setColumns(10); 
        txtTongTien.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 200), 2), 
            new EmptyBorder(8, 8, 8, 8) 
        ));
        
        // Thêm focus listener để chọn toàn bộ text khi click vào
        // (Lỗi FocusAdapter và FocusEvent đã được sửa)
        txtTongTien.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() -> txtTongTien.selectAll());
            }
        });

        // --- Panel Nút Bấm Mệnh Giá ---
        JPanel pnlBangTien = new JPanel(new GridLayout(4, 4, 8, 8)); 
        pnlBangTien.setBackground(Color.WHITE);
        
        int[] menhGia = {
            5000000, 2000000, 1000000, 500000, 
            200000, 100000, 50000, 20000,   
            10000, 5000, 2000, 1000        
        };
        String[] tenMenhGia = {
            "5 TRIỆU", "2 TRIỆU", "1 TRIỆU", "500k",
            "200k", "100k", "50k", "20k",
            "10k", "5k", "2k", "1k"
        };

        for (int i = 0; i < menhGia.length; i++) {
            JButton btn = createCurrencyButton(tenMenhGia[i], menhGia[i]);
            pnlBangTien.add(btn);
        }
        
        // Nút Xóa (Reset về 0)
        JButton btnXoa = new JButton("XÓA TẤT CẢ");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        btnXoa.setBackground(new Color(255, 100, 100)); 
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0), 2)); 
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.addActionListener(e -> setTien(0));
        
        JPanel pnlBottomButtons = new JPanel(new GridLayout(1, 2, 8, 8)); 
        pnlBottomButtons.setBackground(Color.WHITE);

        JButton btnLaySoDuMau = new JButton("LẤY SỐ DƯ MẪU");
        btnLaySoDuMau.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLaySoDuMau.setBackground(new Color(150, 200, 255)); 
        btnLaySoDuMau.setForeground(Color.BLACK);
        btnLaySoDuMau.setFocusPainted(false);
        btnLaySoDuMau.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2));
        btnLaySoDuMau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLaySoDuMau.addActionListener(e -> laySoDuMacDinh());

        pnlBottomButtons.add(btnLaySoDuMau);
        pnlBottomButtons.add(btnXoa);

        // --- Ghép Layout ---
        JPanel pnlTongTienWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlTongTienWrapper.setBackground(Color.WHITE);
        pnlTongTienWrapper.add(txtTongTien); 

        add(pnlTongTienWrapper, BorderLayout.NORTH);
        add(pnlBangTien, BorderLayout.CENTER);
        add(pnlBottomButtons, BorderLayout.SOUTH);
    }

    private JButton createCurrencyButton(String text, int menhGia) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        btn.setBackground(new Color(200, 220, 255)); 
        btn.setForeground(new Color(0, 50, 150)); 
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(150, 180, 220), 1)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(170, 200, 240)); } 
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(200, 220, 255)); } 
        });
        
        btn.addActionListener(e -> {
            tongTien = getTien(); 
            tongTien += menhGia;
            if (tongTien < 0) tongTien = 0; 
            txtTongTien.setValue(tongTien);
        });
        return btn;
    }

    public double getTien() {
        try {
            txtTongTien.commitEdit(); 
        } catch (ParseException e) {
            // Bỏ qua lỗi
        }
        Object value = txtTongTien.getValue();
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    public void setTien(double soTien) {
        this.tongTien = soTien;
        if (this.tongTien < 0) this.tongTien = 0; 
        txtTongTien.setValue(this.tongTien);
    }
    
    public void setEditable(boolean editable) {
        txtTongTien.setEditable(editable);
        if(!editable) {
            txtTongTien.setBackground(new Color(240, 240, 240));
        } else {
            txtTongTien.setBackground(Color.WHITE);
        }
    }

    public void addTongTienChangeListener(PropertyChangeListener listener) {
        txtTongTien.addPropertyChangeListener("value", listener);
    }

    private void laySoDuMacDinh() {
        double[] soDuMau = {5000000, 3000000, 10000000, 2000000, 7500000};
        double soDu = soDuMau[(int)(Math.random() * soDuMau.length)];
        setTien(soDu);
    }
}