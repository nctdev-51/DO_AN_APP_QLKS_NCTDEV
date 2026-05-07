package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.ChiTietDichVu;
import entity.DichVu;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatPhong;
import entity.Phong;
import entity.TinhTrangPhong;
import entity.TrangThaiPhieuDat;
import java.sql.Date; // Đảm bảo import java.sql.Date
import java.util.ArrayList;
import java.util.List;
import entity.TinhTrangPhong; // Cần import TinhTrangPhong
import entity.TrangThaiPhieuDat;

public class PhieuDatPhong_DAO {

    private Connection con;
    private Phong_DAO phong_DAO;
    private KhachHang_DAO khachHang_DAO;
    private NhanVien_DAO nhanVien_DAO;
    private DichVu_DAO dichVu_DAO; // Giả sử đã có DichVu_DAO

    public PhieuDatPhong_DAO() {
        con = ConnectDB.getInstance().getConnection();
        phong_DAO = new Phong_DAO();
        khachHang_DAO = new KhachHang_DAO();
        nhanVien_DAO = new NhanVien_DAO();
        dichVu_DAO = new DichVu_DAO();
    }

    /**
     * Tạo mã phiếu đặt phòng mới (ví dụ: PDP005)
     */
    public String generateMaPhieu() {
        String maPhieu = "PDP001";
        String sql = "SELECT MAX(maPhieu) FROM PhieuDatPhong";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maxMaPhieu = rs.getString(1);
                if (maxMaPhieu != null && !maxMaPhieu.isEmpty()) {
                    int num = Integer.parseInt(maxMaPhieu.substring(3));
                    num++;
                    maPhieu = String.format("PDP%03d", num);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maPhieu;
    }

    /**
     * Nghiệp vụ cốt lõi: Thêm phiếu đặt phòng (Transaction)
     * @param pdp Thông tin phiếu đặt
     * @param dsPhong Danh sách phòng được chọn
     * @param dsDichVu Danh sách dịch vụ được chọn
     * @return true nếu đặt phòng thành công
     */
    /**
     * Nghiệp vụ cốt lõi: Thêm phiếu đặt phòng (Transaction)
     * @param pdp Thông tin phiếu đặt
     * @param dsPhong Danh sách phòng được chọn
     * @param dsDichVu Danh sách dịch vụ được chọn
     * @return true nếu đặt phòng thành công
     */
//    public boolean themPhieuDatPhong(PhieuDatPhong pdp, List<Phong> dsPhong, List<ChiTietDichVu> dsDichVu) {
//        Connection transCon = ConnectDB.getInstance().getConnection();
//        
//        try {
//            // 1. Bắt đầu TRANSACTION
//            transCon.setAutoCommit(false);
//
//            // 2. Thêm vào PhieuDatPhong
//            String sqlPDP = "INSERT INTO PhieuDatPhong (maPhieu, maKhachHang, maNhanVien, ngayDat, ngayNhan, ngayTra, tongTien, trangThai) " +
//                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//            PreparedStatement psPDP = transCon.prepareStatement(sqlPDP);
//            psPDP.setString(1, pdp.getMaPhieu());
//            psPDP.setString(2, pdp.getKhachHang().getMaKhachHang());
//            psPDP.setString(3, pdp.getNhanVien().getMaNhanVien());
//            psPDP.setDate(4, Date.valueOf(pdp.getNgayDat()));
//            psPDP.setDate(5, Date.valueOf(pdp.getNgayNhan()));
//            psPDP.setDate(6, Date.valueOf(pdp.getNgayTra()));
//            psPDP.setDouble(7, pdp.getTongTien());
//            psPDP.setString(8, pdp.getTrangThai().toString());
//            
//            int n1 = psPDP.executeUpdate();
//            if (n1 == 0) throw new SQLException("Thêm PhieuDatPhong thất bại");
//
//            // 3. Thêm vào ChiTietDatPhong (N-N)
//            String sqlCTDP = "INSERT INTO ChiTietDatPhong (maPhieu, maPhong) VALUES (?, ?)";
//            PreparedStatement psCTDP = transCon.prepareStatement(sqlCTDP);
//            for (Phong phong : dsPhong) {
//                psCTDP.setString(1, pdp.getMaPhieu());
//                psCTDP.setString(2, phong.getMaPhong());
//                psCTDP.addBatch(); // Thêm vào batch
//            }
//            int[] n2 = psCTDP.executeBatch();
//            if (n2.length == 0) throw new SQLException("Thêm ChiTietDatPhong thất bại");
//
//            // 4. Thêm vào ChiTietDichVu (nếu có)
//            if (dsDichVu != null && !dsDichVu.isEmpty()) {
//                
//                // <<< SỬA LỖI SQL: Thêm cột 'thoiGianGoi' (6 cột) >>>
//                String sqlCTDV = "INSERT INTO ChiTietDichVu (maPhieu, maPhong, maDichVu, thoiGianGoi, soLuong, ghiChu) VALUES (?, ?, ?, ?, ?, ?)";
//                PreparedStatement psCTDV = transCon.prepareStatement(sqlCTDV);
//
//                for (ChiTietDichVu ctdv : dsDichVu) {
//                    
//                    Phong phongCuaDV = ctdv.getPhong();
//                    
//                    if (phongCuaDV == null || phongCuaDV.getMaPhong() == null) {
//                        // (DatPhong_Gui đã xử lý việc gán phòng nên lỗi này không thể xảy ra)
//                        throw new SQLException("Lỗi logic: Dịch vụ '" + ctdv.getDichVu().getTenDichVu() + "' không được gán cho phòng nào.");
//                    }
//
//                    // <<< SỬA LỖI SQL: Gán 6 tham số >>>
//                    psCTDV.setString(1, pdp.getMaPhieu());
//                    psCTDV.setString(2, phongCuaDV.getMaPhong()); 
//                    psCTDV.setString(3, ctdv.getDichVu().getMaDichVu());
//                    // Lấy thời gian từ đối tượng (đã được set = now() khi tạo)
//                    psCTDV.setTimestamp(4, java.sql.Timestamp.valueOf(ctdv.getThoiGianGoi())); 
//                    psCTDV.setInt(5, ctdv.getSoLuong());
//                    psCTDV.setString(6, ctdv.getGhiChu());
//                    psCTDV.addBatch();
//                }
//                psCTDV.executeBatch();
//            }
//
//            // 5. Cập nhật trạng thái phòng
//            Phong_DAO transPhongDAO = new Phong_DAO(); 
//            for (Phong phong : dsPhong) {
//                boolean n4 = transPhongDAO.capNhatTrangThaiPhong(phong.getMaPhong(), TinhTrangPhong.DA_DAT, transCon);
//                if (!n4) throw new SQLException("Cập nhật trạng thái phòng thất bại");
//            }
//
//            // 6. Nếu tất cả thành công -> COMMIT
//            transCon.commit();
//            return true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            try {
//                // 7. Nếu có lỗi -> ROLLBACK
//                transCon.rollback();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//            return false;
//        } finally {
//            try {
//                // 8. Trả lại AutoCommit
//                transCon.setAutoCommit(true);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    
    

    /**
     * Lấy danh sách phiếu đặt phòng (Chưa bao gồm chi tiết)
     */
    public List<PhieuDatPhong> getAllPhieuDatPhong() {
        List<PhieuDatPhong> dsPhieu = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDatPhong";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                PhieuDatPhong pdp = parsePhieuDatPhong(rs);
                dsPhieu.add(pdp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhieu;
    }

    /**
     * Lấy 1 phiếu đặt phòng (bao gồm cả danh sách phòng và dịch vụ)
     */
    public PhieuDatPhong getPhieuDatPhongTheoMa(String maPhieu) {
        String sql = "SELECT * FROM PhieuDatPhong WHERE maPhieu = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
                PhieuDatPhong pdp = parsePhieuDatPhong(rs);
                // Lấy danh sách phòng
                pdp.setDsPhong(getDanhSachPhongTheoMaPhieu(maPhieu));
                // Lấy danh sách dịch vụ
                pdp.setDsDichVu(getDanhSachDichVuTheoMaPhieu(maPhieu));
                return pdp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Hàm helper parse PhieuDatPhong từ ResultSet
    private PhieuDatPhong parsePhieuDatPhong(ResultSet rs) throws SQLException {
        String maPhieu = rs.getString("maPhieu");
        String maKH = rs.getString("maKhachHang");
        String maNV = rs.getString("maNhanVien");
        LocalDate ngayDat = rs.getDate("ngayDat").toLocalDate();
        LocalDate ngayNhan = rs.getDate("ngayNhan").toLocalDate();
        LocalDate ngayTra = rs.getDate("ngayTra").toLocalDate();
        double tongTien = rs.getDouble("tongTien");
        TrangThaiPhieuDat trangThai = TrangThaiPhieuDat.fromString(rs.getString("trangThai"));
        
        KhachHang kh = khachHang_DAO.search(maKH);
        NhanVien nv = nhanVien_DAO.getNhanVienByMa(maNV);
        
        return new PhieuDatPhong(maPhieu, kh, nv, ngayDat, ngayNhan, ngayTra, tongTien, trangThai);
    }
    
    // Hàm helper lấy DS Phòng của 1 phiếu
    public List<Phong> getDanhSachPhongTheoMaPhieu(String maPhieu) {
        List<Phong> dsPhong = new ArrayList<>();
        String sql = "SELECT p.maPhong FROM Phong p JOIN ChiTietDatPhong ctdp ON p.maPhong = ctdp.maPhong WHERE ctdp.maPhieu = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Phong p = phong_DAO.getPhongTheoMa(rs.getString("maPhong"));
                dsPhong.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsPhong;
    }

    // Hàm helper lấy DS Dịch Vụ của 1 phiếu
 // Hàm helper lấy DS Dịch Vụ của 1 phiếu
    // SỬA LẠI ĐỂ TRẢ VỀ ĐỐI TƯỢNG PHÒNG
    public List<ChiTietDichVu> getDanhSachDichVuTheoMaPhieu(String maPhieu) {
        List<ChiTietDichVu> dsDichVu = new ArrayList<>();
        // SỬA SQL: Thêm ctdv.maPhong và p.*
        String sql = "SELECT dv.maDichVu, dv.tenDichVu, dv.giaTien, dv.moTa, " + 
                     "ctdv.soLuong, ctdv.ghiChu, ctdv.maPhong " + // Lấy maPhong từ CTDV
                     "FROM DichVu dv " +
                     "JOIN ChiTietDichVu ctdv ON dv.maDichVu = ctdv.maDichVu " +
                     "WHERE ctdv.maPhieu = ?";
        
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                DichVu dv = new DichVu(
                    rs.getString("maDichVu"),
                    rs.getString("tenDichVu"),
                    rs.getDouble("giaTien"),
                    rs.getString("moTa")
                );
                int soLuong = rs.getInt("soLuong");
                String ghiChu = rs.getString("ghiChu");
                String maPhongCuaDV = rs.getString("maPhong"); // <-- Lấy mã phòng
                
                // Cần 1 PhieuDatPhong giả để khởi tạo
                PhieuDatPhong pdpFake = new PhieuDatPhong(maPhieu);
                // Lấy đối tượng phòng từ DAO
                Phong phongCuaDV = phong_DAO.getPhongTheoMa(maPhongCuaDV); // <-- Lấy phòng

                // Khởi tạo ChiTietDichVu (dùng constructor mới)
                ChiTietDichVu ctdv = new ChiTietDichVu(pdpFake, phongCuaDV, dv, soLuong, ghiChu);
                dsDichVu.add(ctdv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsDichVu;
    }
    
    // Thêm các hàm cập nhật trạng thái phiếu (nhận phòng, hủy phòng,...)
    public boolean capNhatTrangThaiPhieu(String maPhieu, TrangThaiPhieuDat trangThaiMoi) {
        String sql = "UPDATE PhieuDatPhong SET trangThai = ? WHERE maPhieu = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi.toString());
            ps.setString(2, maPhieu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // cập nhật 9g37
    /**
     * Lấy danh sách phiếu đặt phòng CÓ LỌC
     * @param sdt SĐT khách hàng (tìm kiếm LIKE)
     * @param tuNgay Lọc từ ngày nhận
     * @param denNgay Lọc đến ngày nhận
     * @return Danh sách PhieuDatPhong đã lọc
     */
    public List<PhieuDatPhong> getPhieuDatPhongLoc(String sdt, java.util.Date tuNgay, java.util.Date denNgay) {
        List<PhieuDatPhong> dsPhieu = new ArrayList<>();
        // Dùng LEFT JOIN phòng khi khách hàng rỗng
        String sql = "SELECT pdp.* FROM PhieuDatPhong pdp " +
                     "LEFT JOIN KhachHang k ON pdp.maKhachHang = k.maKhachHang " +
                     "WHERE 1=1";
        
        List<Object> params = new ArrayList<>();

        if (sdt != null && !sdt.trim().isEmpty()) {
            sql += " AND k.soDienThoai LIKE ?";
            params.add("%" + sdt + "%");
        }
        if (tuNgay != null) {
            sql += " AND pdp.ngayNhan >= ?";
            params.add(new java.sql.Date(tuNgay.getTime()));
        }
        if (denNgay != null) {
            sql += " AND pdp.ngayNhan <= ?";
            params.add(new java.sql.Date(denNgay.getTime()));
        }
        
        sql += " ORDER BY pdp.ngayNhan DESC"; // Sắp xếp phiếu mới nhất lên đầu

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            for (Object param : params) {
                ps.setObject(i++, param);
            }
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                // Giả sử bạn đã có hàm parsePhieuDatPhong(rs)
                PhieuDatPhong pdp = parsePhieuDatPhong(rs); 
                dsPhieu.add(pdp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhieu;
    }

    /**
     * NGHIỆP VỤ: Nhận phòng (Transaction)
     * Cập nhật PhieuDatPhong -> DA_NHAN
     * Cập nhật các Phong liên quan -> DANG_SU_DUNG
     */
    public boolean nhanPhong(String maPhieu) {
        Connection transCon = ConnectDB.getInstance().getConnection();
        try {
            transCon.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật trạng thái phiếu
            if (!capNhatTrangThaiPhieu(maPhieu, TrangThaiPhieuDat.DA_NHAN)) {
                throw new SQLException("Cập nhật trạng thái phiếu thất bại");
            }

            // 2. Cập nhật trạng thái các phòng liên quan
            List<Phong> dsPhong = getDanhSachPhongTheoMaPhieu(maPhieu);
            if (dsPhong.isEmpty()) throw new SQLException("Không tìm thấy phòng nào cho phiếu này");

            Phong_DAO transPhongDAO = new Phong_DAO();
            for (Phong p : dsPhong) {
                transPhongDAO.capNhatTrangThaiPhong(p.getMaPhong(), TinhTrangPhong.DANG_SU_DUNG, transCon);
            }

            transCon.commit(); // Thành công
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                transCon.rollback(); // Thất bại
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        } finally {
            try {
                transCon.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * NGHIỆP VỤ: Hủy phiếu (Transaction)
     * Cập nhật PhieuDatPhong -> DA_HUY
     * Cập nhật các Phong liên quan -> TRONG
     */
    public boolean huyPhieu(String maPhieu) {
        Connection transCon = ConnectDB.getInstance().getConnection();
        try {
            transCon.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật trạng thái phiếu
            if (!capNhatTrangThaiPhieu(maPhieu, TrangThaiPhieuDat.DA_HUY)) {
                throw new SQLException("Cập nhật trạng thái phiếu thất bại");
            }

            // 2. Cập nhật trạng thái các phòng liên quan
            List<Phong> dsPhong = getDanhSachPhongTheoMaPhieu(maPhieu);
            if (dsPhong.isEmpty()) throw new SQLException("Không tìm thấy phòng nào cho phiếu này");

            Phong_DAO transPhongDAO = new Phong_DAO();
            for (Phong p : dsPhong) {
                transPhongDAO.capNhatTrangThaiPhong(p.getMaPhong(), TinhTrangPhong.TRONG, transCon);
            }

            transCon.commit(); // Thành công
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                transCon.rollback(); // Thất bại
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        } finally {
            try {
                transCon.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    /**
     * NGHIỆP VỤ: Thêm (gọi) dịch vụ cho một phòng đang ở (Đang sử dụng).
     * Phương thức này sẽ tự động tìm PhieuDatPhong đang hoạt động
     * (trạng thái 'Đã nhận') của phòng đó để gán dịch vụ vào.
     *
     * @param maPhong Phòng gọi dịch vụ (ví dụ: "P101")
     * @param maDichVu Dịch vụ được gọi (ví dụ: "DV003")
     * @param soLuong Số lượng
     * @param ghiChu Ghi chú (nếu có)
     * @return true nếu thêm thành công, false nếu phòng không hoạt động hoặc có lỗi
     */
    public boolean themDichVuVaoPhongDangO(String maPhong, String maDichVu, int soLuong, String ghiChu) {
        
        // 1. Tìm maPhieu đang hoạt động (trạng thái 'Đã nhận') của phòng này
        String maPhieuHienTai = null;
        String sqlFindMaPhieu = "SELECT pdp.maPhieu " +
                              "FROM PhieuDatPhong pdp " +
                              "JOIN ChiTietDatPhong ctdp ON pdp.maPhieu = ctdp.maPhieu " +
                              "WHERE ctdp.maPhong = ? AND pdp.trangThai = N'Đã nhận'"; // N'Đã nhận' là TrangThaiPhieuDat.DA_NHAN

        try (PreparedStatement psFind = con.prepareStatement(sqlFindMaPhieu)) {
            psFind.setString(1, maPhong);
            ResultSet rs = psFind.executeQuery();

            if (rs.next()) {
                maPhieuHienTai = rs.getString(1);
            } else {
                // Nếu không tìm thấy -> Phòng này không có phiếu nào đang "Đã nhận"
                // (Có thể phòng đang 'Trống' hoặc 'Chờ nhận phòng')
                System.err.println("Lỗi nghiệp vụ: Phòng " + maPhong + " không có phiếu đặt phòng nào đang hoạt động (Đã nhận).");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm mã phiếu hoạt động: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // 2. Thêm dịch vụ vào bảng ChiTietDichVu
        // CSDL sẽ tự động thêm 'thoiGianGoi' vì đã có DEFAULT GETDATE()
        String sqlInsertDV = "INSERT INTO ChiTietDichVu (maPhieu, maPhong, maDichVu, soLuong, ghiChu) " +
                             "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement psInsert = con.prepareStatement(sqlInsertDV)) {
            psInsert.setString(1, maPhieuHienTai);
            psInsert.setString(2, maPhong);
            psInsert.setString(3, maDichVu);
            psInsert.setInt(4, soLuong);
            psInsert.setString(5, ghiChu);
            
            return psInsert.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm ChiTietDichVu: " + e.getMessage());
            // Lỗi này có thể xảy ra nếu PK bị trùng (thời gian gọi quá nhanh)
            // Hoặc maDichVu không tồn tại (lỗi khóa ngoại)
            e.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Tìm mã phiếu đang hoạt động (trạng thái 'Đã nhận') của 1 phòng
     * @param maPhong Mã phòng cần tìm
     * @return String maPhieu, hoặc null nếu không tìm thấy
     */
    public String getMaPhieuHoatDong(String maPhong) {
        String sql = "SELECT pdp.maPhieu " +
                     "FROM PhieuDatPhong pdp " +
                     "JOIN ChiTietDatPhong ctdp ON pdp.maPhieu = ctdp.maPhieu " +
                     "WHERE ctdp.maPhong = ? AND pdp.trangThai = N'Đã nhận'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("maPhieu");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
 // ==========================================================================
    // === HÀM MỚI: BẮT BUỘC CHO QuanLyGoiDichVu_Gui (THEO YÊU CẦU MỚI) ===
    // ==========================================================================

    /**
     * [HÀM MỚI] Lấy danh sách phiếu đặt phòng đang hoạt động (trạng thái 'Đã nhận')
     * Dùng cho GUI Quản Lý Gọi Dịch Vụ.
     * @return Danh sách PhieuDatPhong (đã bao gồm KhachHang, NhanVien, và List<Phong>)
     */
    public List<PhieuDatPhong> getDanhSachPhieuDatPhongDangHoatDong() {
        List<PhieuDatPhong> dsPhieu = new ArrayList<>();
        
        // 1. Lấy tất cả phiếu có trạng thái 'Đã nhận'
        //    (Dựa theo logic của hàm getMaPhieuHoatDong)
        String sql = "SELECT * FROM PhieuDatPhong WHERE trangThai IN (N'Đã nhận', N'Chờ nhận phòng', N'Đã thanh toán (chờ nhận)') ORDER BY ngayNhan DESC";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                // 2. Dùng hàm parse có sẵn để lấy thông tin cơ bản (Phiếu, KH, NV)
                PhieuDatPhong pdp = parsePhieuDatPhong(rs);
                
                // 3. Dùng hàm helper có sẵn để lấy danh sách phòng của phiếu này
                //    (Điều này là bắt buộc để GUI mới hoạt động)
                List<Phong> dsPhong = getDanhSachPhongTheoMaPhieu(pdp.getMaPhieu());
                pdp.setDsPhong(dsPhong); // Gán danh sách phòng vào phiếu
                
                // Gán thêm danh sách dịch vụ
                List<ChiTietDichVu> dsDichVu = getDanhSachDichVuTheoMaPhieu(pdp.getMaPhieu());
                pdp.setDsDichVu(dsDichVu);
                
                // 4. Thêm phiếu đã đủ thông tin vào danh sách
                dsPhieu.add(pdp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhieu;
    }
    
    
 // Trong PhieuDatPhong_DAO.java

    /**
     * Helper: Tạo mã Hóa Đơn mới (ví dụ: HD005)
     */
    public String generateMaHoaDon() {
        String maHD = "HD001";
        String sql = "SELECT MAX(maHoaDon) FROM HoaDon";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maxMaHD = rs.getString(1);
                if (maxMaHD != null && !maxMaHD.isEmpty()) {
                    int num = Integer.parseInt(maxMaHD.substring(2));
                    num++;
                    maHD = String.format("HD%03d", num);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maHD;
    }


    /**
     * <<< HÀM TRANSACTION MỚI (THAY THẾ HÀM themPhieuDatPhong CŨ) >>>
     *
     * Nghiệp vụ cốt lõi: Tạo phiếu đặt phòng, VÀ có thể (tùy chọn) thanh toán ngay.
     * @param pdp (Phiếu đặt phòng - đã set trạng thái)
     * @param dsPhong (Danh sách phòng)
     * @param dsDichVu (Danh sách dịch vụ)
     * @param hoaDon (Hóa đơn - có thể là NULL nếu không thanh toán)
     * @param dsThanhToan (Chi tiết thanh toán - có thể là NULL/rỗng nếu không thanh toán)
     * @return true nếu toàn bộ transaction thành công
     */
 // Trong PhieuDatPhong_DAO.java

    /**
     * <<< HÀM TRANSACTION MỚI (ĐÃ SỬA LỖI BIÊN DỊCH) >>>
     *
     * Nghiệp vụ cốt lõi: Tạo phiếu đặt phòng, VÀ có thể (tùy chọn) thanh toán ngay.
     * @param pdp (Phiếu đặt phòng - đã set trạng thái)
     * @param dsPhong (Danh sách phòng)
     * @param dsDichVu (Danh sách dịch vụ)
     * @param hoaDon (Hóa đơn - có thể là NULL nếu không thanh toán)
     * @param dsThanhToan (Chi tiết thanh toán - có thể là NULL/rỗng nếu không thanh toán)
     * @return true nếu toàn bộ transaction thành công
     */
    public boolean taoPhieuDatVaThanhToan(PhieuDatPhong pdp, List<Phong> dsPhong, List<ChiTietDichVu> dsDichVu, 
                                        entity.HoaDon hoaDon, List<entity.ChiTietThanhToan> dsThanhToan) {
        
        Connection transCon = ConnectDB.getInstance().getConnection();
        
        try {
            // 1. Bắt đầu TRANSACTION
            transCon.setAutoCommit(false);

            // 2. Thêm vào PhieuDatPhong (Không đổi)
            String sqlPDP = "INSERT INTO PhieuDatPhong (maPhieu, maKhachHang, maNhanVien, ngayDat, ngayNhan, ngayTra, tongTien, trangThai) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psPDP = transCon.prepareStatement(sqlPDP);
            psPDP.setString(1, pdp.getMaPhieu());
            psPDP.setString(2, pdp.getKhachHang().getMaKhachHang());
            psPDP.setString(3, pdp.getNhanVien().getMaNhanVien());
            psPDP.setDate(4, Date.valueOf(pdp.getNgayDat()));
            psPDP.setDate(5, Date.valueOf(pdp.getNgayNhan()));
            psPDP.setDate(6, Date.valueOf(pdp.getNgayTra()));
            psPDP.setDouble(7, pdp.getTongTien());
            psPDP.setString(8, pdp.getTrangThai().toString()); 
            
            if (psPDP.executeUpdate() == 0) throw new SQLException("Thêm PhieuDatPhong thất bại");

            // 3. Thêm vào ChiTietDatPhong (N-N) (Không đổi)
            String sqlCTDP = "INSERT INTO ChiTietDatPhong (maPhieu, maPhong) VALUES (?, ?)";
            PreparedStatement psCTDP = transCon.prepareStatement(sqlCTDP);
            for (Phong phong : dsPhong) {
                psCTDP.setString(1, pdp.getMaPhieu());
                psCTDP.setString(2, phong.getMaPhong());
                psCTDP.addBatch(); 
            }
            if (psCTDP.executeBatch().length == 0) throw new SQLException("Thêm ChiTietDatPhong thất bại");

            // 4. Thêm vào ChiTietDichVu (nếu có) (Không đổi)
            if (dsDichVu != null && !dsDichVu.isEmpty()) {
                String sqlCTDV = "INSERT INTO ChiTietDichVu (maPhieu, maPhong, maDichVu, thoiGianGoi, soLuong, ghiChu) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psCTDV = transCon.prepareStatement(sqlCTDV);
                for (ChiTietDichVu ctdv : dsDichVu) {
                    psCTDV.setString(1, pdp.getMaPhieu());
                    psCTDV.setString(2, ctdv.getPhong().getMaPhong()); 
                    psCTDV.setString(3, ctdv.getDichVu().getMaDichVu());
                    psCTDV.setTimestamp(4, java.sql.Timestamp.valueOf(ctdv.getThoiGianGoi())); 
                    psCTDV.setInt(5, ctdv.getSoLuong());
                    psCTDV.setString(6, ctdv.getGhiChu());
                    psCTDV.addBatch();
                }
                psCTDV.executeBatch();
            }

            // 5. Cập nhật trạng thái phòng (sang 'Đã đặt') (Không đổi)
            Phong_DAO transPhongDAO = new Phong_DAO(); 
            for (Phong phong : dsPhong) {
                if (!transPhongDAO.capNhatTrangThaiPhong(phong.getMaPhong(), TinhTrangPhong.DA_DAT, transCon)) {
                    throw new SQLException("Cập nhật trạng thái phòng thất bại");
                }
            }

            // 6. Xử lý Hóa đơn và Thanh toán (NẾU CÓ)
            if (hoaDon != null && dsThanhToan != null && !dsThanhToan.isEmpty()) {
                
                // 6a. Thêm Hóa Đơn
                String sqlHD = "INSERT INTO HoaDon (maHoaDon, maPhieu, maNhanVien, maKhachHang, ngayLap, thueVAT, tongTien, ghiChu) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; 
                PreparedStatement psHD = transCon.prepareStatement(sqlHD);
                
                // <<< BẮT ĐẦU SỬA LỖI >>>
                psHD.setString(1, hoaDon.getMaHoaDon());
                // Lỗi 1: Phải lấy đối tượng PhieuDatPhong rồi mới lấy mã
                psHD.setString(2, hoaDon.getPhieuDatPhong().getMaPhieu()); 
                // Lỗi 2: Phải lấy đối tượng NhanVien rồi mới lấy mã
                psHD.setString(3, hoaDon.getNhanVien().getMaNhanVien()); 
                // Lỗi 3: Phải lấy đối tượng KhachHang rồi mới lấy mã
                psHD.setString(4, hoaDon.getKhachHang().getMaKhachHang()); 
                // <<< KẾT THÚC SỬA LỖI >>>
                
                psHD.setDate(5, Date.valueOf(hoaDon.getNgayLap()));
                psHD.setDouble(6, hoaDon.getThueVAT());
                psHD.setDouble(7, hoaDon.getTongTien());
                psHD.setString(8, "Thanh toán trả trước"); // Ghi chú
                
                if (psHD.executeUpdate() == 0) throw new SQLException("Thêm Hóa Đơn thất bại");

                // 6b. Thêm Chi Tiết Thanh Toán (Không đổi)
                String sqlCTTT = "INSERT INTO ChiTietThanhToan (maHoaDon, maHinhThuc, soTien, maGiaoDich) VALUES (?, ?, ?, ?)";
                PreparedStatement psCTTT = transCon.prepareStatement(sqlCTTT);
                for (entity.ChiTietThanhToan cttt : dsThanhToan) {
                    psCTTT.setString(1, hoaDon.getMaHoaDon()); 
                    psCTTT.setString(2, cttt.getHinhThuc().getMaHinhThuc());
                    psCTTT.setDouble(3, cttt.getSoTien());
                    psCTTT.setString(4, cttt.getMaGiaoDich());
                    psCTTT.addBatch();
                }
                psCTTT.executeBatch();
            }

            // 7. Nếu tất cả thành công -> COMMIT (Không đổi)
            transCon.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                transCon.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        } finally {
            try {
                transCon.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}