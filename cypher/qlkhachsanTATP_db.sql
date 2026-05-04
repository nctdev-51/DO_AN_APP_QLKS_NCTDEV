-- ============================================================
-- HOTEL MANAGEMENT DATABASE - MARIADB MIGRATION (FIXED)
-- ============================================================

DROP DATABASE IF EXISTS qlkhachsan_db;
CREATE DATABASE qlkhachsan_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qlkhachsan_db;

-- ============================================================
-- 1. TABLE CREATION (DDL)
-- ============================================================

CREATE TABLE NhanVien (
                          maNhanVien CHAR(5) PRIMARY KEY,
                          hoTen VARCHAR(50) NOT NULL,
                          ngaySinh DATE NOT NULL,
                          gioiTinh TINYINT(1) NOT NULL,
                          CCCD CHAR(12) NOT NULL UNIQUE,
                          soDienThoai VARCHAR(15) NOT NULL UNIQUE,
                          trangThai TINYINT(1) NOT NULL,
                          loaiNhanVien VARCHAR(50) NOT NULL,
                          ngayVaoLam DATE NOT NULL,
                          queQuan VARCHAR(50) NOT NULL,
                          CONSTRAINT CHK_NhanVien_Loai CHECK (loaiNhanVien IN ('NHAN_VIEN_LE_TAN', 'NHAN_VIEN_QUAN_LY'))
);

CREATE TABLE TaiKhoan (
                          maNhanVien CHAR(5) PRIMARY KEY,
                          tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
                          matKhau VARCHAR(255) NOT NULL,
                          trangThaiTK TINYINT(1) NOT NULL,
                          CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE
);

CREATE TABLE KhachHang (
                           maKhachHang CHAR(5) PRIMARY KEY,
                           hoTen VARCHAR(50) NOT NULL,
                           soDienThoai VARCHAR(10) NOT NULL UNIQUE,
                           ngaySinh DATE NOT NULL,
                           loaiKhachHang VARCHAR(50) NOT NULL,
                           CONSTRAINT CHK_KhachHang_Loai CHECK (loaiKhachHang IN ('KHACH_VANG_LAI', 'KHACH_HOI_VIEN'))
);

CREATE TABLE KhuyenMai (
                           maKhuyenMai CHAR(5) PRIMARY KEY,
                           tenKhuyenMai VARCHAR(50) NOT NULL,
                           ngayBatDau DATE NOT NULL DEFAULT CURRENT_DATE,
                           ngayKetThuc DATE NOT NULL,
                           loaiKhuyenMai VARCHAR(50) NOT NULL,
                           chietKhau DECIMAL(5,2) NOT NULL,
                           CONSTRAINT CHK_KhuyenMai_Loai CHECK (loaiKhuyenMai IN ('THEO_KHACH_HANG', 'THEO_PHONG')),
                           CONSTRAINT CHK_KhuyenMai_Ngay CHECK (ngayKetThuc > ngayBatDau)
);

CREATE TABLE LoaiPhong (
                           maLoaiPhong VARCHAR(20) PRIMARY KEY,
                           tenLoaiPhong VARCHAR(50) NOT NULL,
                           moTa VARCHAR(50) NOT NULL
);

CREATE TABLE Phong (
                       maPhong CHAR(4) PRIMARY KEY,
                       tenPhong VARCHAR(100),
                       giaPhong DECIMAL(18, 2) NOT NULL,
                       maLoaiPhong VARCHAR(20) NOT NULL,
                       tinhTrang VARCHAR(50) NOT NULL,
                       CONSTRAINT FK_Phong_LoaiPhong FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong),
                       CONSTRAINT CHK_Phong_TinhTrang CHECK (tinhTrang IN ('Trống', 'Đã Đặt', 'Bảo Trì'))
);

CREATE TABLE DichVu (
                        maDichVu VARCHAR(20) PRIMARY KEY,
                        tenDichVu VARCHAR(100) NOT NULL,
                        giaTien DECIMAL(18, 2) NOT NULL,
                        moTa VARCHAR(255)
);

CREATE TABLE PhieuDatPhong (
                               maPhieu VARCHAR(10) PRIMARY KEY,
                               maKhachHang CHAR(5),
                               maPhong CHAR(4),
                               ngayDat DATE,
                               ngayNhan DATE,
                               ngayTra DATE,
                               tongTien DECIMAL(18, 2),
                               FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
                               FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
);

CREATE TABLE HoaDon (
                        maHoaDon VARCHAR(10) PRIMARY KEY,
                        maNhanVien CHAR(5),
                        maKhachHang CHAR(5),
                        ngayLap DATE,
                        thueVAT FLOAT,
                        maKhuyenMai CHAR(5),
                        maPhongDat CHAR(4),
                        ghiChu VARCHAR(500),
                        tongTien DECIMAL(18, 2),
                        FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
                        FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
                        FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai),
                        FOREIGN KEY (maPhongDat) REFERENCES Phong(maPhong)
);

CREATE TABLE ChiTietPhieuDatPhong (
                                      maPhieu VARCHAR(10),
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
-- 2. INSERT SAMPLE DATA (DML)
-- ============================================================

INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, moTa) VALUES
                                                            ('DON', 'Phòng đơn', 'Phòng đơn'),
                                                            ('DOI', 'Phòng đôi', 'Phòng đôi'),
                                                            ('GIADINH', 'Phòng gia đình', 'Phòng gia đình'),
                                                            ('VIP', 'Phòng VIP', 'Phòng VIP');

INSERT INTO NhanVien (maNhanVien, hoTen, ngaySinh, gioiTinh, CCCD, soDienThoai, trangThai, loaiNhanVien, ngayVaoLam, queQuan) VALUES
                                                                                                                                  ('NV001', 'Nguyễn Văn An', '1995-01-15', 1, '001195000123', '0905111222', 1, 'NHAN_VIEN_QUAN_LY', '2020-05-01', 'Hà Nội'),
                                                                                                                                  ('NV002', 'Trần Thị Bích', '2000-07-22', 0, '001200000456', '0905333444', 1, 'NHAN_VIEN_LE_TAN', '2023-01-10', 'Đà Nẵng'),
                                                                                                                                  ('NV003', 'Lê Văn Cường', '2002-03-10', 1, '001202000789', '0988111222', 0, 'NHAN_VIEN_LE_TAN', '2024-02-11', 'TP. HCM');

INSERT INTO KhachHang (maKhachHang, hoTen, soDienThoai, ngaySinh, loaiKhachHang) VALUES
                                                                                     ('KH001', 'Trần Hùng Dũng', '0912345678', '1990-11-20', 'KHACH_HOI_VIEN'),
                                                                                     ('KH002', 'Phạm Thị Mai', '0987654321', '1985-05-10', 'KHACH_HOI_VIEN'),
                                                                                     ('KH003', 'Ngô Gia Bảo', '0334455667', '2001-08-01', 'KHACH_VANG_LAI');

INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, loaiKhuyenMai, chietKhau) VALUES
                                                                                                         ('KM001', 'Giảm 10% khách hội viên', '2025-10-01', '2025-12-31', 'THEO_KHACH_HANG', 0.10),
                                                                                                         ('KM002', 'Giảm 20% phòng VIP', '2025-11-01', '2025-11-30', 'THEO_PHONG', 0.20),
                                                                                                         ('KM003', 'Giảm 5% cuối tuần', '2025-01-01', '2025-12-31', 'THEO_PHONG', 0.05);

INSERT INTO DichVu (maDichVu, tenDichVu, giaTien, moTa) VALUES
                                                            ('DV001', 'Nước suối', 15000.00, 'Nước suối Aquafina 500ml'),
                                                            ('DV002', 'Giặt ủi', 50000.00, 'Giặt ủi nhanh trong ngày'),
                                                            ('DV003', 'Ăn sáng buffet', 150000.00, 'Buffet sáng tại nhà hàng'),
                                                            ('DV004', 'Coca-Cola', 20000.00, 'Nước ngọt Coca-Cola lon');

INSERT INTO Phong (maPhong, tenPhong, giaPhong, maLoaiPhong, tinhTrang) VALUES
                                                                            ('P101', 'Phòng 101 - Hướng Vườn', 500000.00, 'DON', 'Trống'),
                                                                            ('P102', 'Phòng 102 - Hướng Phố', 450000.00, 'DON', 'Trống'),
                                                                            ('P201', 'Phòng 201 - Hướng Biển', 800000.00, 'DOI', 'Đã Đặt'),
                                                                            ('P202', 'Phòng 202 - Hướng Vườn', 750000.00, 'GIADINH', 'Bảo Trì'),
                                                                            ('P203', 'Phòng 203 - VIP Hướng Biển', 2000000.00, 'VIP', 'Trống');

INSERT INTO TaiKhoan (maNhanVien, tenDangNhap, matKhau, trangThaiTK) VALUES
                                                                         ('NV001', 'admin', '123', 1),
                                                                         ('NV002', 'letan01', '123', 1),
                                                                         ('NV003', 'letan02', '123', 0);

INSERT INTO PhieuDatPhong (maPhieu, maKhachHang, maPhong, ngayDat, ngayNhan, ngayTra, tongTien) VALUES
                                                                                                    ('PDP001', 'KH001', 'P201', '2025-10-30', '2025-11-05', '2025-11-08', NULL),
                                                                                                    ('PDP002', 'KH003', 'P101', '2025-11-01', '2025-11-02', '2025-11-04', NULL),
                                                                                                    ('PDP003', 'KH002', 'P203', '2025-11-01', '2025-11-10', '2025-11-15', NULL);

INSERT INTO HoaDon (maHoaDon, maNhanVien, maKhachHang, ngayLap, thueVAT, maKhuyenMai, maPhongDat, ghiChu, tongTien) VALUES
                                                                                                                        ('HD001', 'NV002', 'KH003', '2025-11-02', 0.08, NULL, 'P101', 'Thanh toán khi trả phòng (PDP002)', 1050000.00),
                                                                                                                        ('HD002', 'NV002', 'KH001', '2025-11-02', 0.08, 'KM001', 'P201', 'Thanh toán cho PDP001 (chưa ở)', 2400000.00);

INSERT INTO ChiTietPhieuDatPhong (maPhieu, maDichVu, soLuong, ghiChu) VALUES
                                                                          ('PDP001', 'DV001', 4, '2 chai mỗi ngày'),
                                                                          ('PDP001', 'DV002', 1, 'Giặt 1 bộ vest'),
                                                                          ('PDP002', 'DV003', 2, 'Ăn sáng cho 2 người'),
                                                                          ('PDP003', 'DV001', 10, 'Mang lên phòng ngày 10/11'),
                                                                          ('PDP003', 'DV004', 5, 'Mang lên phòng ngày 10/11');

INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, soLuong) VALUES
                                                            ('HD001', 'DV003', 2),
                                                            ('HD002', 'DV001', 4),
                                                            ('HD002', 'DV002', 1);

-- ============================================================
-- MIGRATION COMPLETE - DATABASE READY FOR USE
-- ============================================================