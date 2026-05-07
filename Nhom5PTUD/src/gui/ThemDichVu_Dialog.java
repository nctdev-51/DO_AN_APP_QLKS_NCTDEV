package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import dao.ChiTietDichVu_DAO;
import dao.DichVu_DAO;
import entity.ChiTietDichVu;
import entity.DichVu;
import entity.PhieuDatPhong;
import entity.Phong;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map; // <<< THÊM MỚI

/**
 * JDialog (cửa sổ popup) cho phép thêm dịch vụ vào một phòng cụ thể
 * cho một phiếu đặt phòng đang hoạt động.
 * * <<< NÂNG CẤP: Dialog này giờ sẽ mở ChonDichVu_Dialog >>>
 */
public class ThemDichVu_Dialog extends JDialog {

    // (Xóa bỏ các trường UI cũ: JComboBox, JSpinner, ...)
    
    // Dữ liệu cần thiết
    private Phong phong;
    private PhieuDatPhong pdp;
    
    // DAO
    private DichVu_DAO dv_dao;
    private ChiTietDichVu_DAO ctdv_dao;

    public ThemDichVu_Dialog(Frame owner, Phong phong, PhieuDatPhong pdp) {
        // Tạm thời set Dialog là vô hình, nó chỉ đóng vai trò trung gian
        super(owner, "Chọn dịch vụ cho phòng " + phong.getMaPhong(), true);
        this.phong = phong;
        this.pdp = pdp;
        
        // Khởi tạo DAO
        this.dv_dao = new DichVu_DAO();
        this.ctdv_dao = new ChiTietDichVu_DAO();

        // Ẩn cửa sổ dialog này đi
        setUndecorated(true); 
        setSize(1, 1);
        setLocationRelativeTo(owner);
        
        // Mở Dialog chọn dịch vụ
        moDialogChonDichVu(owner);
    }
    
    /**
     * Mở Dialog chính và xử lý kết quả
     */
    private void moDialogChonDichVu(Frame owner) {
        // 1. Tải danh sách dịch vụ
        List<DichVu> dsDichVuToanBo = dv_dao.getAllDichVu();
        if (dsDichVuToanBo == null || dsDichVuToanBo.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Không thể tải danh sách dịch vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // 2. Mở Dialog (sử dụng danh sách dịch vụ đã tải)
        ChonDichVu_Dialog dialog = new ChonDichVu_Dialog(owner, dsDichVuToanBo);
        dialog.setVisible(true);

        // 3. Lấy kết quả
        Map<DichVu, Integer> selectedServices = dialog.getSelectedServices();
        if (selectedServices.isEmpty()) {
            dispose(); // Người dùng hủy
            return;
        }
        
        // 4. Xử lý lưu vào CSDL (vì đây là dialog "thêm nhanh")
        int successCount = 0;
        for (Map.Entry<DichVu, Integer> entry : selectedServices.entrySet()) {
            DichVu dv = entry.getKey();
            int soLuong = entry.getValue();
            
            // Tạo ChiTietDichVu (ghi chú rỗng)
            ChiTietDichVu ctdv = new ChiTietDichVu(pdp, phong, dv, soLuong, "");

            // Gọi DAO để lưu vào CSDL
            if (ctdv_dao.addChiTietDichVu(ctdv)) {
                successCount++;
            }
        }
        
        JOptionPane.showMessageDialog(owner, "Đã thêm " + successCount + " loại dịch vụ mới vào phòng " + phong.getMaPhong());
        dispose(); // Đóng dialog trung gian này
    }
    
    /**
     * (Xóa 2 hàm cũ: loadComboBoxDichVu() và themDichVu())
     */
}