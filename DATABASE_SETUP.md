# 🗄️ Hướng Dẫn Cấu Hình Database MariaDB

## 📋 Yêu Cầu

- MariaDB Server 10.5 hoặc cao hơn
- MySQL Workbench (tùy chọn, để quản lý DB)
- MariaDB JDBC Driver (đã include trong pom.xml)

## 🚀 Các Bước Setup

### 1. Cài Đặt MariaDB

#### Windows:
```bash
# Download từ: https://mariadb.org/download/
# Hoặc dùng Chocolatey
choco install mariadb-server
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install mariadb-server
sudo mysql_secure_installation
```

#### macOS:
```bash
brew install mariadb
brew services start mariadb
```

### 2. Khởi Động MariaDB

```bash
# Windows (Command Prompt as Admin)
net start MySQL80

# Linux/macOS
sudo service mysql start
# hoặc
sudo systemctl start mariadb
```

### 3. Đăng Nhập vào MariaDB

```bash
mysql -u root -p
# Nhập password (mặc định trống nếu vừa cài)
```

### 4. Tạo Database

```sql
-- Tạo database mới
CREATE DATABASE qlkhachsan_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Kiểm tra database được tạo
SHOW DATABASES;

-- Chọn database để sử dụng
USE qlkhachsan_db;
```

### 5. Tạo User cho Ứng Dụng (Tùy Chọn - Bảo Mật)

```sql
-- Tạo user mới (thay 'app_user' và 'secure_password' theo ý)
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'secure_password';

-- Cấp quyền
GRANT ALL PRIVILEGES ON qlkhachsan_db.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;

-- Kiểm tra
SELECT User, Host FROM mysql.user;
```

### 6. Cấu Hình Ứng Dụng

Chỉnh sửa file `src/main/resources/META-INF/persistence.xml`:

```xml
<persistence-unit name="qlkhachsan-pu" transaction-type="RESOURCE_LOCAL">
    <!-- ... other config ... -->
    
    <properties>
        <!-- Database Connection -->
        <property name="jakarta.persistence.jdbc.driver" value="org.mariadb.jdbc.Driver"/>
        <property name="jakarta.persistence.jdbc.url" 
                  value="jdbc:mariadb://localhost:3306/qlkhachsan_db?useUnicode=true&characterEncoding=utf-8"/>
        <property name="jakarta.persistence.jdbc.user" value="root"/>
        <property name="jakarta.persistence.jdbc.password" value=""/>
        
        <!-- Hoặc nếu dùng user khác -->
        <!-- <property name="jakarta.persistence.jdbc.user" value="app_user"/>
        <property name="jakarta.persistence.jdbc.password" value="secure_password"/> -->
    </properties>
</persistence-unit>
```

### 7. Chạy Ứng Dụng

```bash
mvn clean install
mvn javafx:run
```

Khi chạy lần đầu:
- Hibernate sẽ tự động **tạo tables** từ @Entity classes (do `hibernate.hbm2ddl.auto=update`)
- Tables sẽ được tạo với tên như: `nhan_vien`, `khach_hang`, `tai_khoan`, v.v.
- Foreign keys sẽ được tự động tạo

## 📊 Xem Dữ Liệu trong Database

```bash
# Đăng nhập MySQL
mysql -u root -p qlkhachsan_db

# Xem tất cả tables
SHOW TABLES;

# Xem cấu trúc table
DESCRIBE nhan_vien;
DESCRIBE khach_hang;
DESCRIBE tai_khoan;

# Xem dữ liệu
SELECT * FROM nhan_vien;
SELECT * FROM khach_hang;
SELECT * FROM tai_khoan;
```

## 🔍 Troubleshooting

### Lỗi: "Can't connect to MySQL server"
```
❌ Nguyên nhân: MariaDB service chưa khởi động
✅ Giải pháp: 
   - Windows: net start MySQL80
   - Linux: sudo systemctl start mariadb
   - macOS: brew services start mariadb
```

### Lỗi: "Access denied for user 'root'@'localhost'"
```
❌ Nguyên nhân: Password sai
✅ Giải pháp:
   - Kiểm tra lại password trong persistence.xml
   - Hoặc reset password:
     mysql -u root
     ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
```

### Lỗi: "Unknown database 'qlkhachsan_db'"
```
❌ Nguyên nhân: Database chưa được tạo
✅ Giải pháp:
   mysql -u root -p
   CREATE DATABASE qlkhachsan_db;
```

### Lỗi: "Class 'org.mariadb.jdbc.Driver' not found"
```
❌ Nguyên nhân: MariaDB JDBC driver chưa được load
✅ Giải pháp:
   - Chạy: mvn dependency:resolve
   - Kiểm tra file pom.xml có dependency mariadb-java-client
```

## 📝 SQL Script Tạo Dữ Liệu Mẫu (Tùy Chọn)

```sql
-- Chỉ chạy AFTER Hibernate tạo tables (lần đầu chạy app)

USE qlkhachsan_db;

-- Thêm nhân viên
INSERT INTO nhan_vien (ma_nhan_vien, ho_ten, ngay_sinh, gioi_tinh, cccd, so_dien_thoai, trang_thai, loai_nhan_vien, ngay_vao_lam, que_quan)
VALUES 
('NV001', 'Nguyễn Văn A', '1990-05-15', true, '123456789', '0912345678', true, 'NHAN_VIEN_LE_TAN', '2023-01-01', 'Hà Nội'),
('NV002', 'Trần Thị B', '1992-03-20', false, '987654321', '0987654321', true, 'QUAN_LY', '2023-02-01', 'TP.HCM');

-- Thêm tài khoản
INSERT INTO tai_khoan (tai_khoan, mat_khau, ma_nhan_vien)
VALUES 
('admin', 'admin123', 'NV001'),
('manager', 'manager123', 'NV002');

-- Thêm khách hàng
INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, ngay_sinh, loai_khach_hang)
VALUES 
('KH001', 'Phạm Văn C', '0911111111', '1985-07-10', 'KHACH_THUONG_XUYÊN'),
('KH002', 'Lê Thị D', '0922222222', '1995-12-25', 'KHACH_MOI');

-- Xem dữ liệu
SELECT * FROM nhan_vien;
SELECT * FROM tai_khoan;
SELECT * FROM khach_hang;
```

## 🔐 Bảo Mật Database

### Đổi Password Root
```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'strong_password_123!@#';
FLUSH PRIVILEGES;
```

### Xóa Anonymous Users (Production)
```sql
DELETE FROM mysql.user WHERE User = '';
FLUSH PRIVILEGES;
```

### Backup Database
```bash
# Windows
mysqldump -u root -p qlkhachsan_db > backup.sql

# Restore
mysql -u root -p qlkhachsan_db < backup.sql
```

---

**Lưu ý**: Persistence.xml được cấu hình để tự động **update schema** (`hibernate.hbm2ddl.auto=update`), nên Hibernate sẽ tự tạo tables lần đầu. Nếu muốn control tốt hơn, có thể đổi thành `create` (tạo lại mỗi lần) hoặc `validate` (chỉ kiểm tra).


