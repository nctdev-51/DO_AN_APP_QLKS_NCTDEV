-- ============================================================
-- HOTEL MANAGEMENT SYSTEM - MariaDB DATABASE (FIXED VERSION)
-- Các thay đổi so với bản gốc:
--   1. KhachHang.loaiKhachHang: thêm 'KHACH_MOI', 'KHACH_THUONG_XUYEN'
--   2. HoaDon: thêm cột tongTienPhong, tongTienDichVu, chietKhau,
--              trangThaiThanhToan, tenPhong để DTO hiển thị đầy đủ
--   3. PhieuDatPhong.trangThai: thêm các giá trị 'Nhận Phòng', 'Trả Phòng'
--              được dùng trong QuanLyPhieuDatTraPhongController
--   4. Dữ liệu mẫu HoaDon cập nhật cột mới
--   5. Thêm 5 phiếu trạng thái DA_NHAN_PHONG để TraPhongController hiển thị
-- ============================================================

DROP DATABASE IF EXISTS qlkhachsanTATP_db;
CREATE DATABASE qlkhachsanTATP_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qlkhachsanTATP_db;

-- ============================================================
-- TABLE CREATION
-- ============================================================

CREATE TABLE LoaiPhong (
                           maLoaiPhong VARCHAR(20) PRIMARY KEY,
                           tenLoaiPhong VARCHAR(50) NOT NULL,
                           moTa VARCHAR(100) NOT NULL
);

CREATE TABLE NhanVien (
                          maNhanVien CHAR(5) PRIMARY KEY,
                          hoTen VARCHAR(50) NOT NULL,
                          ngaySinh DATE NOT NULL,
                          gioiTinh TINYINT(1) NOT NULL,
                          CCCD CHAR(12) NOT NULL UNIQUE,
                          soDienThoai VARCHAR(15) NOT NULL UNIQUE,
                          trangThai TINYINT(1) NOT NULL,
                          loaiNhanVien VARCHAR(50) NOT NULL
                              CHECK (loaiNhanVien IN ('NHAN_VIEN_LE_TAN', 'NHAN_VIEN_QUAN_LY')),
                          ngayVaoLam DATE NOT NULL,
                          queQuan VARCHAR(50) NOT NULL
);

CREATE TABLE TaiKhoan (
                          maNhanVien CHAR(5) PRIMARY KEY,
                          tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
                          matKhau VARCHAR(255) NOT NULL,
                          trangThaiTK TINYINT(1) NOT NULL,
                          CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNhanVien)
                              REFERENCES NhanVien(maNhanVien)
                              ON DELETE CASCADE
);

-- FIX #1: Mở rộng CHECK để khớp tất cả loại KH dùng trong Java code
CREATE TABLE KhachHang (
                           maKhachHang CHAR(5) PRIMARY KEY,
                           hoTen VARCHAR(50) NOT NULL,
                           soDienThoai VARCHAR(15) NOT NULL UNIQUE,
                           ngaySinh DATE NOT NULL,
                           loaiKhachHang VARCHAR(50) NOT NULL
                               CHECK (loaiKhachHang IN (
                                                        'KHACH_VANG_LAI',
                                                        'KHACH_HOI_VIEN',
                                                        'KHACH_MOI',
                                                        'KHACH_THUONG_XUYEN'
                                   ))
);

CREATE TABLE KhuyenMai (
                           maKhuyenMai CHAR(5) PRIMARY KEY,
                           tenKhuyenMai VARCHAR(50) NOT NULL,
                           ngayBatDau DATE NOT NULL DEFAULT CURRENT_DATE,
                           ngayKetThuc DATE NOT NULL,
                           loaiKhuyenMai VARCHAR(50) NOT NULL
                               CHECK (loaiKhuyenMai IN ('THEO_KHACH_HANG', 'THEO_PHONG')),
                           chietKhau DECIMAL(5,2) NOT NULL,
                           CONSTRAINT CHK_KhuyenMai_Ngay CHECK (ngayKetThuc > ngayBatDau)
);

CREATE TABLE Phong (
                       maPhong CHAR(4) PRIMARY KEY,
                       tenPhong VARCHAR(100),
                       giaPhong DECIMAL(18, 2) NOT NULL,
                       maLoaiPhong VARCHAR(20) NOT NULL,
    -- FIX #3: Thêm giá trị 'Đang ở' (đúng với code cập nhật phòng khi check-in)
                       tinhTrang VARCHAR(50) NOT NULL DEFAULT 'Trống',
                       CONSTRAINT FK_Phong_LoaiPhong FOREIGN KEY (maLoaiPhong)
                           REFERENCES LoaiPhong(maLoaiPhong),
                       CONSTRAINT CHK_Phong_TinhTrang CHECK (tinhTrang IN ('Trống', 'Đã Đặt', 'Đang ở', 'Bảo Trì'))
);

CREATE TABLE DichVu (
                        maDichVu VARCHAR(20) PRIMARY KEY,
                        tenDichVu VARCHAR(100) NOT NULL,
                        giaTien DECIMAL(18, 2) NOT NULL,
                        moTa VARCHAR(255)
);

-- FIX #2: Thêm các giá trị trangThai phù hợp với controller
CREATE TABLE PhieuDatPhong (
                               maPhieu VARCHAR(20) PRIMARY KEY,
                               maKhachHang CHAR(5),
                               maPhong CHAR(4),
                               ngayDat DATE,
                               ngayNhan DATE,
                               ngayTra DATE,
                               tongTien DECIMAL(18, 2),
                               trangThai VARCHAR(30) NOT NULL DEFAULT 'CHO_NHAN_PHONG'
                                   CHECK (trangThai IN (
                                                        'CHO_NHAN_PHONG',
                                                        'DA_NHAN_PHONG',
                                                        'DA_TRA_PHONG',
                                                        'DA_HUY',
                                                        'Nhận Phòng',   -- dùng trong QuanLyPhieuDatTraPhongController.processCheckIn()
                                                        'Trả Phòng',    -- dùng trong processCheckoutReal()
                                                        'Đang chờ',     -- dùng trong filter stat cards
                                                        'Đã xác nhận',
                                                        'Đã checkin',
                                                        'Đã checkout'
                                       )),
                               maNhanVien CHAR(5) NULL,
                               FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
                               FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
                               FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

-- FIX #2: Bổ sung cột cần thiết cho HoaDonDTO được dùng trong các controller
CREATE TABLE HoaDon (
                        maHoaDon VARCHAR(10) PRIMARY KEY,
                        maNhanVien CHAR(5),
                        maKhachHang CHAR(5),
                        ngayLap DATE,
                        thueVAT FLOAT DEFAULT 0,
                        maKhuyenMai CHAR(5),
                        maPhongDat CHAR(4),
                        tenPhong VARCHAR(100),           -- NEW: tên phòng (dùng trong processCheckoutReal)
                        ghiChu VARCHAR(500),
                        tongTienPhong DECIMAL(18, 2) DEFAULT 0,  -- NEW: tiền phòng riêng
                        tongTienDichVu DECIMAL(18, 2) DEFAULT 0, -- NEW: tiền dịch vụ riêng
                        chietKhau DECIMAL(18, 2) DEFAULT 0,      -- NEW: chiết khấu
                        tongTien DECIMAL(18, 2),
                        trangThaiThanhToan VARCHAR(50) DEFAULT 'Chưa Thanh Toán',  -- NEW: trạng thái TT
                        FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
                        FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
                        FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai),
                        FOREIGN KEY (maPhongDat) REFERENCES Phong(maPhong)
);

CREATE TABLE ChiTietPhieuDatPhong (
                                      maPhieu VARCHAR(20),
                                      maDichVu VARCHAR(20),
                                      soLuong INT,
                                      ghiChu VARCHAR(500),
                                      PRIMARY KEY (maPhieu, maDichVu),
                                      FOREIGN KEY (maPhieu) REFERENCES PhieuDatPhong(maPhieu),
                                      FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

CREATE TABLE ChiTietHoaDon (
                               maHoaDon VARCHAR(10),
                               maDichVu VARCHAR(20),
                               soLuong INT,
                               PRIMARY KEY (maHoaDon, maDichVu),
                               FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
                               FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- LOẠI PHÒNG
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, moTa) VALUES
                                                            ('DON',      'Phòng đơn',     'Phòng tiêu chuẩn cho một khách'),
                                                            ('DOI',      'Phòng đôi',     'Phòng cho hai khách với giường đôi'),
                                                            ('GIADINH',  'Phòng gia đình','Phòng rộng cho 3-4 người'),
                                                            ('VIP',      'Phòng VIP',     'Phòng cao cấp với đầy đủ tiện nghi');

-- NHÂN VIÊN
INSERT INTO NhanVien (maNhanVien, hoTen, ngaySinh, gioiTinh, CCCD, soDienThoai, trangThai, loaiNhanVien, ngayVaoLam, queQuan) VALUES
                                                                                                                                  ('NV001', 'Nguyễn Văn An',     '1994-01-15', 1, '001194000001', '0905111001', 1, 'NHAN_VIEN_QUAN_LY', '2020-05-01', 'Hà Nội'),
                                                                                                                                  ('NV002', 'Trần Thị Bích',     '2000-07-22', 0, '001200000002', '0905111002', 1, 'NHAN_VIEN_LE_TAN',  '2023-01-10', 'Đà Nẵng'),
                                                                                                                                  ('NV003', 'Lê Văn Cường',      '2001-03-10', 1, '001201000003', '0905111003', 1, 'NHAN_VIEN_LE_TAN',  '2023-02-15', 'TP. HCM'),
                                                                                                                                  ('NV004', 'Vũ Thị Dung',       '1999-05-20', 0, '001199000004', '0905111004', 1, 'NHAN_VIEN_LE_TAN',  '2023-03-01', 'Hải Phòng'),
                                                                                                                                  ('NV005', 'Phạm Văn Em',       '1998-08-12', 1, '001198000005', '0905111005', 1, 'NHAN_VIEN_QUAN_LY', '2021-06-10', 'Cần Thơ'),
                                                                                                                                  ('NV006', 'Hoàng Thị Phương',  '2001-11-05', 0, '001201000006', '0905111006', 1, 'NHAN_VIEN_LE_TAN',  '2023-07-01', 'Huế'),
                                                                                                                                  ('NV007', 'Đỗ Văn Giang',      '2000-04-18', 1, '001200000007', '0905111007', 1, 'NHAN_VIEN_LE_TAN',  '2023-08-15', 'Nha Trang'),
                                                                                                                                  ('NV008', 'Ngô Thị Hương',     '1999-12-30', 0, '001199000008', '0905111008', 0, 'NHAN_VIEN_LE_TAN',  '2023-09-20', 'Đà Lạt'),
                                                                                                                                  ('NV009', 'Bùi Văn Kiên',      '2002-02-14', 1, '001202000009', '0905111009', 1, 'NHAN_VIEN_LE_TAN',  '2024-01-05', 'Vinh'),
                                                                                                                                  ('NV010', 'Trương Thị Lan',    '2000-09-08', 0, '001200000010', '0905111010', 1, 'NHAN_VIEN_LE_TAN',  '2024-02-01', 'Quảng Ninh'),
                                                                                                                                  ('NV011', 'Võ Văn Minh',       '1997-06-25', 1, '001197000011', '0905111011', 1, 'NHAN_VIEN_QUAN_LY', '2022-03-15', 'Bắc Giang'),
                                                                                                                                  ('NV012', 'Dương Thị Nhi',     '2001-10-12', 0, '001201000012', '0905111012', 1, 'NHAN_VIEN_LE_TAN',  '2024-03-10', 'Phú Yên');

-- TÀI KHOẢN
INSERT INTO TaiKhoan (maNhanVien, tenDangNhap, matKhau, trangThaiTK) VALUES
                                                                         ('NV001', 'admin',      '123', 1),
                                                                         ('NV002', 'letan01',    '123', 1),
                                                                         ('NV003', 'letan02',    '123', 1),
                                                                         ('NV004', 'letan03',    '123', 1),
                                                                         ('NV005', 'manager01',  '123', 1),
                                                                         ('NV006', 'letan04',    '123', 1),
                                                                         ('NV007', 'letan05',    '123', 1),
                                                                         ('NV009', 'letan06',    '123', 1),
                                                                         ('NV010', 'letan07',    '123', 1),
                                                                         ('NV011', 'manager02',  '123', 1);

-- KHÁCH HÀNG (FIX #1: dùng đúng các loại mới)
INSERT INTO KhachHang (maKhachHang, hoTen, soDienThoai, ngaySinh, loaiKhachHang) VALUES
                                                                                     ('KH001', 'Trần Hùng Dũng',   '0912345001', '1990-11-20', 'KHACH_HOI_VIEN'),
                                                                                     ('KH002', 'Phạm Thị Mai',     '0987654002', '1985-05-10', 'KHACH_HOI_VIEN'),
                                                                                     ('KH003', 'Ngô Gia Bảo',      '0934455003', '2001-08-01', 'KHACH_VANG_LAI'),
                                                                                     ('KH004', 'Vũ Minh Tuấn',     '0901234004', '1992-03-15', 'KHACH_HOI_VIEN'),
                                                                                     ('KH005', 'Hoàng Thúy Vân',   '0945678005', '1988-07-22', 'KHACH_VANG_LAI'),
                                                                                     ('KH006', 'Lý Văn Hùng',      '0956789006', '1995-12-05', 'KHACH_HOI_VIEN'),
                                                                                     ('KH007', 'Cao Thị Hương',    '0967890007', '2000-04-18', 'KHACH_THUONG_XUYEN'),
                                                                                     ('KH008', 'Đặng Văn Kiên',    '0978901008', '1993-09-30', 'KHACH_HOI_VIEN'),
                                                                                     ('KH009', 'Phan Thị Liên',    '0989012009', '1991-02-14', 'KHACH_THUONG_XUYEN'),
                                                                                     ('KH010', 'Tô Văn Minh',      '0990123010', '1998-06-25', 'KHACH_HOI_VIEN'),
                                                                                     ('KH011', 'Hà Thị Nhung',     '0901234511', '2002-10-12', 'KHACH_VANG_LAI'),
                                                                                     ('KH012', 'Quốc Hợp',         '0912345512', '1997-01-08', 'KHACH_HOI_VIEN'),
                                                                                     ('KH013', 'Lương Thị Oanh',   '0923456513', '1999-05-20', 'KHACH_MOI'),
                                                                                     ('KH014', 'Mạnh Quyền',       '0934567514', '1996-07-14', 'KHACH_HOI_VIEN'),
                                                                                     ('KH015', 'Tuyền Thị Hồng',   '0945678515', '2001-11-30', 'KHACH_MOI');

-- KHUYẾN MÃI
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, loaiKhuyenMai, chietKhau) VALUES
                                                                                                         ('KM001', 'Giảm 10% khách hội viên', '2025-10-01', '2026-12-31', 'THEO_KHACH_HANG', 0.10),
                                                                                                         ('KM002', 'Giảm 20% phòng VIP',      '2025-11-01', '2026-11-30', 'THEO_PHONG',       0.20),
                                                                                                         ('KM003', 'Giảm 5% cuối tuần',       '2025-01-01', '2026-12-31', 'THEO_PHONG',       0.05),
                                                                                                         ('KM004', 'Giảm 15% nhóm',           '2025-11-05', '2026-12-01', 'THEO_KHACH_HANG',  0.15),
                                                                                                         ('KM005', 'Giảm 12% tháng 11',       '2025-11-01', '2026-11-30', 'THEO_PHONG',       0.12);

-- DỊCH VỤ
INSERT INTO DichVu (maDichVu, tenDichVu, giaTien, moTa) VALUES
                                                            ('DV001', 'Nước suối Aquafina',    15000.00,  'Nước suối Aquafina 500ml'),
                                                            ('DV002', 'Giặt ủi quần áo',       80000.00,  'Giặt ủi nhanh trong ngày'),
                                                            ('DV003', 'Ăn sáng buffet',       150000.00,  'Buffet sáng đa dạng tại nhà hàng'),
                                                            ('DV004', 'Coca-Cola lon',          25000.00,  'Nước ngọt Coca-Cola 330ml'),
                                                            ('DV005', 'Cafe đen đá',            30000.00,  'Cà phê đen đá tươi'),
                                                            ('DV006', 'Ăn tối set menu',       250000.00,  'Set menu tối cao cấp'),
                                                            ('DV007', 'Massage toàn thân',     200000.00,  'Dịch vụ massage thư giãn'),
                                                            ('DV008', 'Spa mặt chuyên sâu',   180000.00,  'Dịch vụ spa chăm sóc da mặt'),
                                                            ('DV009', 'Gym & Fitness',         120000.00,  'Sử dụng phòng gym trong 1 ngày'),
                                                            ('DV010', 'Taxi sân bay',          300000.00,  'Dịch vụ đưa đón sân bay'),
                                                            ('DV011', 'Giặt giày cao cấp',      50000.00,  'Giặt giày da, vải cao cấp'),
                                                            ('DV012', 'Room Service đêm',      100000.00,  'Dịch vụ phòng mở rộng 24/7'),
                                                            ('DV013', 'Internet cao tốc',       35000.00,  'WiFi cao tốc unlimited'),
                                                            ('DV014', 'Trà chiều Anh quốc',    120000.00,  'Trà chiều với bánh nước ngoài'),
                                                            ('DV015', 'Dịch vụ ký gửi',         20000.00,  'Ký gửi hành lý trong 1 ngày');

-- PHÒNG
INSERT INTO Phong (maPhong, tenPhong, giaPhong, maLoaiPhong, tinhTrang) VALUES
-- Tầng 1
('1P01', 'Phòng 101 - Hướng Vườn',  500000.00, 'DON',     'Trống'),
('1P02', 'Phòng 102 - Hướng Phố',   450000.00, 'DON',     'Trống'),
('1P03', 'Phòng 103 - Hướng Vườn',  480000.00, 'DON',     'Đang ở'),
('1P04', 'Phòng 104 - Ngoài sân',   520000.00, 'DON',     'Trống'),
('1P05', 'Phòng 105 - Hướng Biển',  600000.00, 'DOI',     'Đang ở'),
('1P06', 'Phòng 106 - Hướng Phố',   580000.00, 'DOI',     'Đang ở'),
('1P07', 'Phòng 107 - Hướng Vườn',  750000.00, 'GIADINH', 'Trống'),
('1P08', 'Phòng 108 - Gia đình',    720000.00, 'GIADINH', 'Đang ở'),
('1P09', 'Phòng 109 - VIP Biển',   2000000.00, 'VIP',     'Đang ở'),
('1P10', 'Phòng 110 - VIP Garden', 1950000.00, 'VIP',     'Bảo Trì'),
-- Tầng 2
('2P01', 'Phòng 201 - Hướng Biển',  510000.00, 'DON',     'Trống'),
('2P02', 'Phòng 202 - Hướng Phố',   460000.00, 'DON',     'Trống'),
('2P03', 'Phòng 203 - Hướng Vườn',  490000.00, 'DON',     'Đang ở'),
('2P04', 'Phòng 204 - Ngoài sân',   530000.00, 'DON',     'Trống'),
('2P05', 'Phòng 205 - Hướng Biển',  610000.00, 'DOI',     'Trống'),
('2P06', 'Phòng 206 - Hướng Phố',   590000.00, 'DOI',     'Đang ở'),
('2P07', 'Phòng 207 - Gia đình',    760000.00, 'GIADINH', 'Đang ở'),
('2P08', 'Phòng 208 - Gia đình',    730000.00, 'GIADINH', 'Đang ở'),
('2P09', 'Phòng 209 - VIP Biển',   2050000.00, 'VIP',     'Trống'),
('2P10', 'Phòng 210 - VIP Garden', 2000000.00, 'VIP',     'Trống'),
-- Tầng 3
('3P01', 'Phòng 301 - Hướng Biển',  520000.00, 'DON',     'Trống'),
('3P02', 'Phòng 302 - Hướng Phố',   470000.00, 'DON',     'Trống'),
('3P03', 'Phòng 303 - Hướng Vườn',  500000.00, 'DON',     'Đang ở'),
('3P04', 'Phòng 304 - Ngoài sân',   540000.00, 'DON',     'Trống'),
('3P05', 'Phòng 305 - Hướng Biển',  620000.00, 'DOI',     'Trống'),
('3P06', 'Phòng 306 - Hướng Phố',   600000.00, 'DOI',     'Đang ở'),
('3P07', 'Phòng 307 - Gia đình',    770000.00, 'GIADINH', 'Trống'),
('3P08', 'Phòng 308 - Gia đình',    740000.00, 'GIADINH', 'Đang ở'),
('3P09', 'Phòng 309 - VIP Biển',   2100000.00, 'VIP',     'Trống'),
('3P10', 'Phòng 310 - VIP Garden', 2050000.00, 'VIP',     'Bảo Trì');

-- PHIẾU ĐẶT PHÒNG
-- FIX #3: Các phiếu DA_NHAN_PHONG = khách đang ở → TraPhong/GoiDichVu sẽ hiện
INSERT INTO PhieuDatPhong (maPhieu, maKhachHang, maPhong, ngayDat, ngayNhan, ngayTra, tongTien, trangThai, maNhanVien) VALUES
-- Chờ nhận phòng
('PDP001', 'KH001', '1P07', '2025-10-25', '2026-05-10', '2026-05-13', NULL,         'CHO_NHAN_PHONG', 'NV002'),
('PDP002', 'KH004', '2P04', '2025-10-28', '2026-05-10', '2026-05-14', NULL,         'CHO_NHAN_PHONG', 'NV003'),
('PDP003', 'KH006', '3P04', '2025-11-01', '2026-05-11', '2026-05-15', NULL,         'CHO_NHAN_PHONG', 'NV002'),
('PDP009', 'KH002', '2P09', '2025-11-01', '2026-05-12', '2026-05-16', NULL,         'CHO_NHAN_PHONG', 'NV004'),
('PDP010', 'KH007', '3P01', '2025-11-02', '2026-05-11', '2026-05-13', NULL,         'CHO_NHAN_PHONG', 'NV006'),
('PDP011', 'KH009', '3P02', '2025-11-03', '2026-05-13', '2026-05-17', NULL,         'CHO_NHAN_PHONG', 'NV002'),
('PDP012', 'KH011', '2P01', '2025-11-02', '2026-05-14', '2026-05-17', NULL,         'CHO_NHAN_PHONG', 'NV003'),
-- Đã nhận phòng (đang ở) → hiện trong GoiDichVu & TraPhong
('PDP004', 'KH003', '1P05', '2025-10-30', '2026-05-05', '2026-05-09', NULL,         'DA_NHAN_PHONG',  'NV003'),
('PDP005', 'KH005', '2P06', '2025-10-15', '2026-05-04', '2026-05-08', NULL,         'DA_NHAN_PHONG',  'NV002'),
('PDP006', 'KH008', '1P08', '2025-10-20', '2026-05-03', '2026-05-07', NULL,         'DA_NHAN_PHONG',  'NV004'),
('PDP007', 'KH010', '3P06', '2025-10-25', '2026-05-02', '2026-05-06', NULL,         'DA_NHAN_PHONG',  'NV006'),
('PDP008', 'KH012', '2P07', '2025-11-01', '2026-05-01', '2026-05-05', NULL,         'DA_NHAN_PHONG',  'NV003'),
('PDP013', 'KH013', '1P03', '2025-11-04', '2026-05-06', '2026-05-08', NULL,         'DA_NHAN_PHONG',  'NV004'),
('PDP014', 'KH014', '2P03', '2025-11-05', '2026-05-07', '2026-05-11', NULL,         'DA_NHAN_PHONG',  'NV006'),
('PDP015', 'KH015', '3P08', '2025-11-03', '2026-05-06', '2026-05-10', NULL,         'DA_NHAN_PHONG',  'NV002'),
('PDP016', 'KH001', '1P06', '2025-11-06', '2026-05-07', '2026-05-11', NULL,         'DA_NHAN_PHONG',  'NV003'),
('PDP017', 'KH002', '1P09', '2025-11-07', '2026-05-07', '2026-05-11', NULL,         'DA_NHAN_PHONG',  'NV004'),
('PDP018', 'KH004', '3P03', '2025-11-08', '2026-05-06', '2026-05-10', NULL,         'DA_NHAN_PHONG',  'NV002'),
-- Đã trả phòng (lịch sử)
('PDP_H1', 'KH005', '2P05', '2025-10-15', '2025-10-20', '2025-10-25', 3050000.00,  'DA_TRA_PHONG',   'NV002'),
('PDP_H2', 'KH008', '1P07', '2025-10-20', '2025-10-22', '2025-10-28', 4950000.00,  'DA_TRA_PHONG',   'NV004'),
('PDP_H3', 'KH010', '3P05', '2025-10-25', '2025-10-28', '2025-11-02', 3380000.00,  'DA_TRA_PHONG',   'NV006');

-- HÓA ĐƠN (FIX #2: thêm tongTienPhong, tongTienDichVu, chietKhau, trangThaiThanhToan, tenPhong)
INSERT INTO HoaDon (maHoaDon, maNhanVien, maKhachHang, ngayLap, thueVAT, maKhuyenMai, maPhongDat, tenPhong, ghiChu, tongTienPhong, tongTienDichVu, chietKhau, tongTien, trangThaiThanhToan) VALUES
                                                                                                                                                                                                ('HD001', 'NV002', 'KH005', '2025-10-25', 0.08, NULL,   '2P05', 'Phòng 205 - Hướng Biển',  'Thanh toán PDP_H1',          3050000.00, 244400.00, 0.00, 3294400.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD002', 'NV004', 'KH008', '2025-10-28', 0.08, 'KM003','1P07', 'Phòng 107 - Hướng Vườn',  'Thanh toán PDP_H2 - giảm 5%', 4500000.00, 472800.00, 0.00, 5068800.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD003', 'NV003', 'KH003', '2025-11-01', 0.08, NULL,   '1P05', 'Phòng 105 - Hướng Biển',  'Tạm thanh toán PDP004',       2400000.00, 948000.00, 0.00, 3348000.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD004', 'NV006', 'KH010', '2025-10-30', 0.08, 'KM001','3P05', 'Phòng 305 - Hướng Biển',  'Thanh toán PDP_H3 - giảm 10%',3100000.00, 45920.00,  0.00, 3145920.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD005', 'NV002', 'KH001', '2025-11-02', 0.10, 'KM003','1P03', 'Phòng 103 - Hướng Vườn',  'Thanh toán sớm PDP001',       1440000.00, -36000.00, 0.00, 1404000.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD006', 'NV003', 'KH004', '2025-11-01', 0.08, NULL,   '1P06', 'Phòng 106 - Hướng Phố',   'Thanh toán sớm PDP002',       2320000.00, 220000.00, 0.00, 2540000.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD007', 'NV004', 'KH012', '2025-11-04', 0.08, 'KM001','2P07', 'Phòng 207 - Gia đình',    'Tạm TT PDP008',               3040000.00, -420000.00,0.00, 2620000.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD008', 'NV002', 'KH006', '2025-11-02', 0.08, NULL,   '2P03', 'Phòng 203 - Hướng Vườn',  'Tạm TT PDP003',               1960000.00, 705600.00, 0.00, 2665600.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD009', 'NV006', 'KH002', '2025-11-03', 0.10, 'KM002','1P09', 'Phòng 109 - VIP Biển',    'Thanh toán VIP PDP009',       8000000.00,8560000.00, 0.00,16560000.00, 'Đã Thanh Toán'),
                                                                                                                                                                                                ('HD010', 'NV003', 'KH007', '2025-11-04', 0.08, 'KM005','3P03', 'Phòng 303 - Hướng Vườn',  'Thanh toán PDP010',           1000000.00,1284800.00, 0.00, 2284800.00, 'Đã Thanh Toán');

-- CHI TIẾT PHIẾU ĐẶT PHÒNG
INSERT INTO ChiTietPhieuDatPhong (maPhieu, maDichVu, soLuong, ghiChu) VALUES
                                                                          ('PDP004', 'DV001', 8,  'Nước suối'), ('PDP004', 'DV003', 4,  'Ăn sáng'), ('PDP004', 'DV013', 4,  'Internet'),
                                                                          ('PDP005', 'DV001', 8,  'Nước suối'), ('PDP005', 'DV003', 4,  'Ăn sáng'), ('PDP005', 'DV006', 2,  'Ăn tối'),
                                                                          ('PDP006', 'DV001', 8,  'Nước suối'), ('PDP006', 'DV003', 4,  'Ăn sáng'), ('PDP006', 'DV007', 2,  'Massage'),
                                                                          ('PDP007', 'DV001', 8,  'Nước suối'), ('PDP007', 'DV005', 4,  'Cà phê'),  ('PDP007', 'DV006', 3,  'Ăn tối'),
                                                                          ('PDP008', 'DV001', 8,  'Nước suối'), ('PDP008', 'DV005', 4,  'Cà phê'),  ('PDP008', 'DV009', 2,  'Gym'),
                                                                          ('PDP013', 'DV001', 4,  'Nước suối'), ('PDP013', 'DV003', 2,  'Ăn sáng'),
                                                                          ('PDP014', 'DV001', 8,  'Nước suối'), ('PDP014', 'DV006', 3,  'Ăn tối'), ('PDP014', 'DV013', 4,  'Internet'),
                                                                          ('PDP015', 'DV001', 8,  'Nước suối'), ('PDP015', 'DV002', 2,  'Giặt ủi'), ('PDP015', 'DV003', 4,  'Ăn sáng'),
                                                                          ('PDP016', 'DV001', 8,  'Nước suối'), ('PDP016', 'DV007', 2,  'Massage'), ('PDP016', 'DV008', 1,  'Spa mặt'),
                                                                          ('PDP017', 'DV001', 8,  'Nước suối premium'), ('PDP017', 'DV007', 2, 'Massage toàn thân'), ('PDP017', 'DV014', 2, 'Trà chiều'),
                                                                          ('PDP018', 'DV001', 4,  'Nước suối'), ('PDP018', 'DV010', 1,  'Taxi sân bay'),
                                                                          ('PDP_H1', 'DV001', 10, 'Nước suối'), ('PDP_H1', 'DV002', 2,  'Giặt ủi'), ('PDP_H1', 'DV003', 5,  'Ăn sáng'),
                                                                          ('PDP_H2', 'DV001', 12, 'Nước suối'), ('PDP_H2', 'DV003', 6,  'Ăn sáng'), ('PDP_H2', 'DV005', 6,  'Cà phê'), ('PDP_H2', 'DV007', 2, 'Massage'),
                                                                          ('PDP_H3', 'DV001', 10, 'Nước suối'), ('PDP_H3', 'DV003', 5,  'Ăn sáng'), ('PDP_H3', 'DV006', 4,  'Ăn tối');

-- CHI TIẾT HÓA ĐƠN
INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, soLuong) VALUES
                                                            ('HD001', 'DV001', 10), ('HD001', 'DV002', 2),  ('HD001', 'DV003', 5),
                                                            ('HD002', 'DV001', 12), ('HD002', 'DV003', 6),  ('HD002', 'DV005', 6),  ('HD002', 'DV007', 2), ('HD002', 'DV014', 1),
                                                            ('HD003', 'DV001', 8),  ('HD003', 'DV003', 4),  ('HD003', 'DV013', 4),
                                                            ('HD004', 'DV001', 10), ('HD004', 'DV003', 5),  ('HD004', 'DV006', 4),
                                                            ('HD005', 'DV001', 6),  ('HD005', 'DV002', 2),  ('HD005', 'DV003', 3),
                                                            ('HD006', 'DV001', 8),  ('HD006', 'DV005', 4),  ('HD006', 'DV006', 2),
                                                            ('HD007', 'DV001', 8),  ('HD007', 'DV005', 4),  ('HD007', 'DV009', 2),
                                                            ('HD008', 'DV001', 8),  ('HD008', 'DV002', 3),  ('HD008', 'DV003', 4),  ('HD008', 'DV007', 2),
                                                            ('HD009', 'DV001', 8),  ('HD009', 'DV007', 2),  ('HD009', 'DV008', 1),  ('HD009', 'DV014', 2),
                                                            ('HD010', 'DV001', 4),  ('HD010', 'DV010', 1);

-- ============================================================
-- VIEWS tiện ích
-- ============================================================

-- View HoaDon đầy đủ cho RevenueController
CREATE OR REPLACE VIEW v_HoaDonDayDu AS
SELECT
    hd.maHoaDon,
    hd.ngayLap,
    hd.tongTienPhong,
    hd.tongTienDichVu,
    hd.chietKhau,
    hd.thueVAT,
    hd.tongTien,
    hd.trangThaiThanhToan,
    hd.maPhongDat,
    hd.tenPhong,
    hd.ghiChu,
    hd.maNhanVien,
    hd.maKhachHang,
    kh.hoTen AS tenKhachHang,
    nv.hoTen AS tenNhanVien
FROM HoaDon hd
         LEFT JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang
         LEFT JOIN NhanVien nv  ON hd.maNhanVien  = nv.maNhanVien;

-- View PhieuDatPhong kèm tên KH, tên phòng cho hiển thị
CREATE OR REPLACE VIEW v_PhieuDatPhongDayDu AS
SELECT
    pdp.maPhieu,
    pdp.maKhachHang,
    kh.hoTen        AS tenKhachHang,
    kh.soDienThoai  AS sdtKhachHang,
    pdp.maPhong,
    p.tenPhong,
    p.giaPhong,
    pdp.ngayDat,
    pdp.ngayNhan,
    pdp.ngayTra,
    pdp.tongTien,
    pdp.trangThai,
    pdp.maNhanVien,
    nv.hoTen        AS tenNhanVien
FROM PhieuDatPhong pdp
         LEFT JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang
         LEFT JOIN Phong p      ON pdp.maPhong      = p.maPhong
         LEFT JOIN NhanVien nv  ON pdp.maNhanVien   = nv.maNhanVien;

-- ============================================================
-- MIGRATION COMPLETE
-- ============================================================