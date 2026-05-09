CREATE DATABASE IF NOT EXISTS qlkhachsan_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SHOW DATABASES LIKE 'qlkhachsan_db';
USE qlkhachsan_db;

ALTER TABLE phan_cong_ca_lam_viec ADD COLUMN trang_thai VARCHAR(50) DEFAULT 'CHUA_LAM';
ALTER TABLE phan_cong_ca_lam_viec ADD COLUMN ghi_chu VARCHAR(255);