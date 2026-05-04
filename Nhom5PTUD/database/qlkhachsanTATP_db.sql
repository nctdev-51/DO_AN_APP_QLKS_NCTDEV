USE master;
GO

-- Kiểm tra xem database có tồn tại không, nếu có thì xóa
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'qlkhachsanTATP_db')
BEGIN
    ALTER DATABASE qlkhachsanTATP_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE qlkhachsanTATP_db;
END
GO

PRINT N'Tạo database qlkhachsanTATP_db...';
CREATE DATABASE qlkhachsanTATP_db;
GO

USE qlkhachsanTATP_db;
GO

----------------------------------------------------
-- 1. TẠO CẤU TRÚC BẢNG (DDL)
-- (Đã sửa lỗi kiểu dữ liệu PK/FK)
----------------------------------------------------

PRINT N'Tạo bảng NhanVien...';
CREATE TABLE NhanVien (
    maNhanVien CHAR(5) PRIMARY KEY,
    hoTen NVARCHAR(50) NOT NULL,
    ngaySinh DATE NOT NULL
        CONSTRAINT CHK_NhanVien_NgaySinh CHECK (ngaySinh <= DATEADD(YEAR, -18, GETDATE())),
    gioiTinh BIT NOT NULL,
    CCCD CHAR(12) NOT NULL UNIQUE,
    soDienThoai VARCHAR(15) NOT NULL UNIQUE,
    trangThai BIT NOT NULL,
    loaiNhanVien VARCHAR(50) NOT NULL
        CONSTRAINT CHK_NhanVien_Loai CHECK (loaiNhanVien IN ('NHAN_VIEN_LE_TAN', 'NHAN_VIEN_QUAN_LY')),
    ngayVaoLam DATE NOT NULL
        CONSTRAINT CHK_NhanVien_NgayVaoLam CHECK (ngayVaoLam <= GETDATE()),
    queQuan NVARCHAR(50) NOT NULL
);
GO

PRINT N'Tạo bảng TaiKhoan...';
CREATE TABLE TaiKhoan (
    maNhanVien CHAR(5) PRIMARY KEY,
    tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    matKhau VARCHAR(255) NOT NULL,
    trangThaiTK BIT NOT NULL,
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNhanVien)
        REFERENCES NhanVien(maNhanVien)
        ON DELETE CASCADE
);
GO

PRINT N'Tạo bảng KhachHang...';
CREATE TABLE KhachHang (
    maKhachHang CHAR(5) PRIMARY KEY,
    hoTen NVARCHAR(50) NOT NULL,
    soDienThoai VARCHAR(10) NOT NULL UNIQUE,
    ngaySinh DATE NOT NULL
        CONSTRAINT CHK_KhachHang_NgaySinh CHECK (ngaySinh <= DATEADD(YEAR, -18, GETDATE())),
    loaiKhachHang VARCHAR(50) NOT NULL
        CONSTRAINT CHK_KhachHang_Loai CHECK (loaiKhachHang IN ('KHACH_VANG_LAI', 'KHACH_HOI_VIEN'))
);
GO

PRINT N'Tạo bảng KhuyenMai...';
CREATE TABLE KhuyenMai (
    maKhuyenMai CHAR(5) PRIMARY KEY,
    tenKhuyenMai NVARCHAR(50) NOT NULL,
    ngayBatDau DATE NOT NULL DEFAULT GETDATE(),
    ngayKetThuc DATE NOT NULL,
    loaiKhuyenMai VARCHAR(50) NOT NULL
        CONSTRAINT CHK_KhuyenMai_Loai CHECK (loaiKhuyenMai IN ('THEO_KHACH_HANG', 'THEO_PHONG')),
    chietKhau DECIMAL(5,2) NOT NULL,
    CONSTRAINT CHK_KhuyenMai_Ngay CHECK (ngayKetThuc > ngayBatDau)
);
GO

PRINT N'Tạo bảng LoaiPhong...';
CREATE TABLE LoaiPhong (
    maLoaiPhong NVARCHAR(20) PRIMARY KEY,
    tenLoaiPhong NVARCHAR(50) NOT NULL,
    moTa NVARCHAR(50) NOT NULL
);
GO

PRINT N'Tạo bảng Phong...';
CREATE TABLE Phong (
    maPhong CHAR(4) PRIMARY KEY,
    tenPhong NVARCHAR(100),
    giaPhong DECIMAL(18, 2) NOT NULL,
    maLoaiPhong NVARCHAR(20) NOT NULL, -- Kiểu dữ liệu khớp với LoaiPhong.maLoaiPhong
    tinhTrang NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_Phong_LoaiPhong FOREIGN KEY (maLoaiPhong)
        REFERENCES LoaiPhong(maLoaiPhong),
    CONSTRAINT CHK_Phong_TinhTrang CHECK (tinhTrang IN (N'Trống', N'Đã Đặt', N'Bảo Trì'))
);
GO

PRINT N'Tạo bảng DichVu...';
CREATE TABLE DichVu (
    maDichVu VARCHAR(20) PRIMARY KEY,
    tenDichVu NVARCHAR(100) NOT NULL,
    giaTien DECIMAL(18, 2) NOT NULL,
    moTa NVARCHAR(255)
);
GO

PRINT N'Tạo bảng PhieuDatPhong...';
CREATE TABLE PhieuDatPhong (
    maPhieu VARCHAR(10) PRIMARY KEY,
    maKhachHang CHAR(5), -- Khớp với KhachHang.maKhachHang
    maPhong CHAR(4), -- Khớp với Phong.maPhong
    ngayDat DATE,
    ngayNhan DATE,
    ngayTra DATE,
    tongTien DECIMAL(18, 2),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
);
GO

PRINT N'Tạo bảng HoaDon...';
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(10) PRIMARY KEY,
    maNhanVien CHAR(5), -- Khớp với NhanVien.maNhanVien
    maKhachHang CHAR(5), -- Khớp với KhachHang.maKhachHang
    ngayLap DATE,
    thueVAT FLOAT,
    maKhuyenMai CHAR(5), -- Khớp với KhuyenMai.maKhuyenMai
    maPhongDat CHAR(4), -- Khớp với Phong.maPhong
    ghiChu NVARCHAR(500),
    tongTien DECIMAL(18, 2),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai),
    FOREIGN KEY (maPhongDat) REFERENCES Phong(maPhong)
);
GO

PRINT N'Tạo bảng ChiTietPhieuDatPhong...';
CREATE TABLE ChiTietPhieuDatPhong (
    maPhieu VARCHAR(10), -- Khớp với PhieuDatPhong.maPhieu
    maDichVu VARCHAR(20), -- Khớp với DichVu.maDichVu
    soLuong INT,
    ghiChu NVARCHAR(500),
    PRIMARY KEY (maPhieu, maDichVu),
    FOREIGN KEY (maPhieu) REFERENCES PhieuDatPhong(maPhieu),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);
GO

PRINT N'Tạo bảng ChiTietHoaDon...';
CREATE TABLE ChiTietHoaDon (
    maHoaDon VARCHAR(10), -- Khớp với HoaDon.maHoaDon
    maDichVu VARCHAR(20), -- Khớp với DichVu.maDichVu
    soLuong INT,
    PRIMARY KEY (maHoaDon, maDichVu),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);
GO

PRINT N'*** Đã tạo xong cấu trúc bảng (DDL) ***';
GO

----------------------------------------------------
-- 2. CHÈN DỮ LIỆU MẪU (DML)
-- (Đã cập nhật theo Enums và sửa lỗi Tầng)
----------------------------------------------------

PRINT N'Chèn dữ liệu cho LoaiPhong (theo Enum)...';
INSERT INTO LoaiPhong (maLoaiPhong, tenLoaiPhong, moTa) VALUES
(N'DON', N'Phòng đơn', N'Phòng đơn'),
(N'DOI', N'Phòng đôi', N'Phòng đôi'),
(N'GIADINH', N'Phòng gia đình', N'Phòng gia đình'),
(N'VIP', N'Phòng VIP', N'Phòng VIP');
GO

PRINT N'Chèn dữ liệu cho NhanVien...';
INSERT INTO NhanVien (maNhanVien, hoTen, ngaySinh, gioiTinh, CCCD, soDienThoai, trangThai, loaiNhanVien, ngayVaoLam, queQuan) VALUES
('NV001', N'Nguyễn Văn An', '1995-01-15', 1, '001195000123', '0905111222', 1, 'NHAN_VIEN_QUAN_LY', '2020-05-01', N'Hà Nội'),
('NV002', N'Trần Thị Bích', '2000-07-22', 0, '001200000456', '0905333444', 1, 'NHAN_VIEN_LE_TAN', '2023-01-10', N'Đà Nẵng'),
('NV003', N'Lê Văn Cường', '2002-03-10', 1, '001202000789', '0988111222', 0, 'NHAN_VIEN_LE_TAN', '2024-02-11', N'TP. HCM');
GO

PRINT N'Chèn dữ liệu cho KhachHang (theo Enum)...';
INSERT INTO KhachHang (maKhachHang, hoTen, soDienThoai, ngaySinh, loaiKhachHang) VALUES
('KH001', N'Trần Hùng Dũng', '0912345678', '1990-11-20', 'KHACH_HOI_VIEN'),
('KH002', N'Phạm Thị Mai', '0987654321', '1985-05-10', 'KHACH_HOI_VIEN'),
('KH003', N'Ngô Gia Bảo', '0334455667', '2001-08-01', 'KHACH_VANG_LAI');
GO

PRINT N'Chèn dữ liệu cho KhuyenMai (theo Enum)...';
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, loaiKhuyenMai, chietKhau) VALUES
('KM001', N'Giảm 10% khách hội viên', '2025-10-01', '2025-12-31', 'THEO_KHACH_HANG', 0.10),
('KM002', N'Giảm 20% phòng VIP', '2025-11-01', '2025-11-30', 'THEO_PHONG', 0.20),
('KM003', N'Giảm 5% cuối tuần', '2025-01-01', '2025-12-31', 'THEO_PHONG', 0.05);
GO

PRINT N'Chèn dữ liệu cho DichVu...';
INSERT INTO DichVu (maDichVu, tenDichVu, giaTien, moTa) VALUES
('DV001', N'Nước suối', 15000.00, N'Nước suối Aquafina 500ml'),
('DV002', N'Giặt ủi', 50000.00, N'Giặt ủi nhanh trong ngày'),
('DV003', N'Ăn sáng buffet', 150000.00, N'Buffet sáng tại nhà hàng'),
('DV004', N'Coca-Cola', 20000.00, N'Nước ngọt Coca-Cola lon');
GO

PRINT N'Chèn dữ liệu cho Phong (theo Enum VÀ SỬA TẦNG)...';
INSERT INTO Phong (maPhong, tenPhong, giaPhong, maLoaiPhong, tinhTrang) VALUES
('P101', N'Phòng 101 - Hướng Vườn', 500000.00, N'DON', N'Trống'),
('P102', N'Phòng 102 - Hướng Phố', 450000.00, N'DON', N'Trống'),
('P201', N'Phòng 201 - Hướng Biển', 800000.00, N'DOI', N'Đã Đặt'),
('P202', N'Phòng 202 - Hướng Vườn', 750000.00, N'GIADINH', N'Bảo Trì'),
('P203', N'Phòng 203 - VIP Hướng Biển', 2000000.00, N'VIP', N'Trống'); -- SỬA: P301 -> P203
GO

PRINT N'Chèn dữ liệu cho TaiKhoan...';
INSERT INTO TaiKhoan (maNhanVien, tenDangNhap, matKhau, trangThaiTK) VALUES
('NV001', 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1), -- Mật khẩu là '123456' (MD5)
('NV002', 'letan01', 'e10adc3949ba59abbe56e057f20f883e', 1), -- Mật khẩu là '123456' (MD5)
('NV003', 'letan02', 'e10adc3949ba59abbe56e057f20f883e', 0); -- Tài khoản bị vô hiệu hóa
GO

PRINT N'Chèn dữ liệu cho PhieuDatPhong (SỬA TẦNG)...';
INSERT INTO PhieuDatPhong (maPhieu, maKhachHang, maPhong, ngayDat, ngayNhan, ngayTra, tongTien) VALUES
('PDP001', 'KH001', 'P201', '2025-10-30', '2025-11-05', '2025-11-08', NULL),
('PDP002', 'KH003', 'P101', '2025-11-01', '2025-11-02', '2025-11-04', NULL),
('PDP003', 'KH002', 'P203', '2025-11-01', '2025-11-10', '2025-11-15', NULL); -- SỬA: P301 -> P203
GO

PRINT N'Chèn dữ liệu cho HoaDon...';
INSERT INTO HoaDon (maHoaDon, maNhanVien, maKhachHang, ngayLap, thueVAT, maKhuyenMai, maPhongDat, ghiChu, tongTien) VALUES
('HD001', 'NV002', 'KH003', '2025-11-02', 0.08, NULL, 'P101', N'Thanh toán khi trả phòng (PDP002)', 1050000.00),
('HD002', 'NV002', 'KH001', '2025-11-02', 0.08, 'KM001', 'P201', N'Thanh toán cho PDP001 (chưa ở)', 2400000.00);
GO

PRINT N'Chèn dữ liệu cho ChiTietPhieuDatPhong (SỬA TẦNG)...';
INSERT INTO ChiTietPhieuDatPhong (maPhieu, maDichVu, soLuong, ghiChu) VALUES
('PDP001', 'DV001', 4, N'2 chai mỗi ngày'),
('PDP001', 'DV002', 1, N'Giặt 1 bộ vest'),
('PDP002', 'DV003', 2, N'Ăn sáng cho 2 người'),
('PDP003', 'DV001', 10, N'Mang lên phòng ngày 10/11'), -- Tham chiếu đến PDP003 (đã sửa)
('PDP003', 'DV004', 5, N'Mang lên phòng ngày 10/11'); -- Tham chiếu đến PDP003 (đã sửa)
GO

PRINT N'Chèn dữ liệu cho ChiTietHoaDon...';
INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, soLuong) VALUES
('HD001', 'DV003', 2), -- Dịch vụ từ PDP002
('HD002', 'DV001', 4), -- Dịch vụ từ PDP001
('HD002', 'DV002', 1); -- Dịch vụ từ PDP001
GO

PRINT N'*** ĐÃ HOÀN TẤT VIỆC TẠO VÀ CHÈN DỮ LIỆU! ***';
GO