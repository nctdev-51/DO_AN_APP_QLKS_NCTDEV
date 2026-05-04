package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Date; // <<< 1. THÊM IMPORT Date
import entity.Phong;
import entity.NhanVien;

public class ChiTietPhong_Dialog extends JDialog {
    
    public NhanVien nv; 
    // <<< 2. THÊM BIẾN ĐỂ LƯU NGÀY (NẾU CÓ) >>>
    private Date checkInDate = null;
    private Date checkOutDate = null;

    /**
     * Constructor 1: Chỉ xem chi tiết (3 tham số) - GIỮ NGUYÊN
     */
    public ChiTietPhong_Dialog(JFrame parent, Phong phong, NhanVien nhanVien) {
        this(parent, phong, nhanVien, null, null); // Gọi sang constructor chính
    }

    /**
     * <<< 3. THÊM CONSTRUCTOR MỚI (5 tham số) >>>
     * Dùng khi đã chọn ngày từ TrangChu_Gui
     */
    public ChiTietPhong_Dialog(JFrame parent, Phong phong, NhanVien nhanVien, Date checkIn, Date checkOut) {
        super(parent, "Chi tiết phòng " + phong.getMaPhong(), true);
        
        this.nv = nhanVien;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        
        setLayout(new BorderLayout(10, 10));
        setSize(400, 350); // Tăng chiều cao một chút
        setLocationRelativeTo(parent);

        JPanel pnlThongTin = new JPanel(new GridLayout(6, 2, 10, 10)); // Tăng lên 6 hàng
        pnlThongTin.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        pnlThongTin.setBackground(Color.WHITE);

        pnlThongTin.add(new JLabel("Mã phòng:"));
        pnlThongTin.add(new JLabel(phong.getMaPhong()));

        pnlThongTin.add(new JLabel("Tên phòng:"));
        pnlThongTin.add(new JLabel(phong.getTenPhong()));

        pnlThongTin.add(new JLabel("Loại phòng:"));
        pnlThongTin.add(new JLabel(phong.getLoaiPhong() != null ? phong.getLoaiPhong().toString() : "Chưa có"));

        pnlThongTin.add(new JLabel("Giá phòng:"));
        pnlThongTin.add(new JLabel(String.format("%,.0f VND", phong.getGiaPhong())));

        pnlThongTin.add(new JLabel("Tình trạng:"));
        pnlThongTin.add(new JLabel(phong.getTinhTrang().toString()));
        
        // Hiển thị ngày đã chọn (nếu có)
        if (checkIn != null && checkOut != null) {
             java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
             pnlThongTin.add(new JLabel("Thời gian đặt:"));
             pnlThongTin.add(new JLabel("<html>" + sdf.format(checkIn) + " ⮕ " + sdf.format(checkOut) + "</html>"));
        } else {
             pnlThongTin.add(new JLabel("")); pnlThongTin.add(new JLabel("")); // Placeholder
        }

        add(pnlThongTin, BorderLayout.CENTER);

        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose());
        JPanel pnlButton = new JPanel();
        pnlButton.add(btnDong);
        
        JButton btnDatPhong = new JButton("Đặt phòng này");

        // Chỉ cho đặt nếu phòng TRỐNG
        if (phong.getTinhTrang().name().equals("TRONG")) { // So sánh an toàn hơn bằng name()
            btnDatPhong.setBackground(new Color(46, 204, 113));
            btnDatPhong.setForeground(Color.WHITE);
            btnDatPhong.setFocusPainted(false);

            btnDatPhong.addActionListener(e -> {
                dispose(); 
                
                // Mở ChonPhong_Gui và truyền ngày vào (cần sửa thêm ChonPhong_Gui sau này nếu muốn tự động điền)
                ChonPhong_Gui dialogChonPhong = new ChonPhong_Gui(this.nv);
                
                // <<< QUAN TRỌNG: NẾU CÓ NGÀY, TỰ ĐỘNG TẠO PHIẾU TẠM >>>
                if (this.checkInDate != null && this.checkOutDate != null) {
                     // Gọi một phương thức mới bên ChonPhong_Gui để xử lý việc này
                     // Ví dụ: dialogChonPhong.tuDongTaoPhieuChoPhong(phong, checkInDate, checkOutDate);
                     // Hiện tại chưa có hàm đó, nên chỉ mở lên thôi.
                }
                
                dialogChonPhong.setVisible(true);
            });

            pnlButton.add(btnDatPhong);
        }

        add(pnlButton, BorderLayout.SOUTH);
    }
}