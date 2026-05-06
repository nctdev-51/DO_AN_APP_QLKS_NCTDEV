# Hoàn Thiện Chức Năng Đặt Phòng & Thanh Toán

## Tóm Tắt Cải Tiến

Các chức năng sau đây đã được **hoàn thiện** với logic thực tế:

### 1. **Đặt Phòng Mới** (Phòng Trống)
**Tệp:** `MainController.java`, `QuanLyPhieuDatTraPhongController.java`

- **Giao diện:** Dialog popup để chọn khách, ngày nhận, ngày trả
- **Logic:**
  - `showBookingDialog()` - Hiển thị form đặt phòng
  - `processBooking()` - Xử lý logic đặt phòng
  - Tạo phiếu đặt phòng mới trong database
  - Cập nhật trạng thái phòng từ "Trống" → "Đã Đặt"
  - Tính tổng tiền = số ngày × giá phòng

**Quy trình:**
1. Click phòng "Trống" → Hiển thị modal
2. Click "🏨 Bắt Đầu Đặt Phòng" → Mở dialog
3. Chọn khách hàng, ngày nhận/trả
4. Click "✅ Đặt Phòng"
5. Hệ thống tạo phiếu và cập nhật trạng thái

### 2. **Nhận Phòng (Check-in)** (Phòng Đã Đặt)
**Tệp:** `MainController.java`, `QuanLyPhieuDatTraPhongController.java`

- **Logic:**
  - `processCheckIn()` - Xử lý nhận phòng
  - Cập nhật trạng thái phiếu đặt từ "Đặt Phòng" → "Nhận Phòng"
  - Cập nhật trạng thái phòng từ "Đã Đặt" → "Đang ở"

**Quy trình:**
1. Click phòng "Đã Đặt" → Hiển thị modal
2. Click "🔑 Nhận Phòng (Check-in)"
3. Hệ thống xác nhận khách đã nhận phòng

### 3. **Trả Phòng & Thanh Toán (Check-out)** (Phòng Đang Ở)
**Tệp:** `MainController.java`, `QuanLyPhieuDatTraPhongController.java`

- **Logic:**
  - `processCheckoutReal()` - Xử lý trả phòng
  - Tính toán hóa đơn:
    ```
    Tiền phòng = số ngày × giá phòng
    Tiền VAT = tiền phòng × (tỷ lệ VAT%)
    Tổng tiền = tiền phòng + dịch vụ + VAT - chiết khấu
    ```
  - Tạo hóa đơn mới
  - Cập nhật trạng thái phiếu đặt → "Trả Phòng"
  - Cập nhật trạng thái phòng → "Trống"

**Quy trình:**
1. Click phòng "Đang ở" → Hiển thị modal
2. Click "💳 Trả Phòng"
3. Xác nhận trong dialog
4. Hệ thống tính toán và lưu hóa đơn

### 4. **Hủy Đặt Phòng** (Phòng Đã Đặt)
**Tệp:** `MainController.java`, `QuanLyPhieuDatTraPhongController.java`

- **Logic:**
  - `cancelBooking()` - Hủy phiếu đặt
  - Cập nhật trạng thái phiếu → "Hủy"
  - Cập nhật trạng thái phòng → "Trống"

### 5. **Hoàn Thành Bảo Trì** (Phòng Bảo Trì)
**Tệp:** `MainController.java`, `QuanLyPhieuDatTraPhongController.java`

- **Logic:**
  - `completeMaintenance()` - Hoàn thành bảo trì
  - Cập nhật trạng thái phòng từ "Bảo Trì" → "Trống"

## Các Phương Thức Mới Được Thêm

### Trong `QuanLyPhieuDatTraPhongController.java`:

```java
// Xử lý trả phòng và thanh toán
private void processCheckoutReal()

// Nhận phòng từ phiếu đã đặt
public boolean processCheckIn(String maPhieu)

// Đặt phòng mới
public boolean processBooking(String maKhachHang, String maPhong, LocalDate ngayNhan, LocalDate ngayTra)

// Hủy phiếu đặt phòng
public boolean cancelBooking(String maPhieu)

// Hoàn thành bảo trì
public boolean completeMaintenance(String maPhong)
```

### Trong `MainController.java`:

```java
// Hiển thị dialog đặt phòng mới
private void showBookingDialog(PhongDTO phong, Stage parentStage)

// Và các method hỗ trợ:
private VBox createInfoBox(String[] labels, String[] values)
private VBox createServiceList(List<HoaDonDTO> hoaDons)
private Button createActionButton(String text, String colorHex)
private void showMessage(String title, String message)
```

## Trạng Thái Trở Lại Dashboard

- Sau mỗi hành động (đặt, nhận, trả, hủy, hoàn thành bảo trì), modal tự động đóng
- Dashboard tự động làm mới để hiển thị dữ liệu mới nhất
- Trạng thái phòng được cập nhật ngay lập tức

## Kiểm Tra Hoạt Động

### Phòng Trống:
1. ✅ Hiển thị "Đặt Phòng Mới"
2. ✅ Nút "🏨 Bắt Đầu Đặt Phòng" → mở dialog
3. ✅ Tạo phiếu đặt → cập nhật trạng thái phòng

### Phòng Đã Đặt:
1. ✅ Hiển thị "Thông Tin Đặt Chỗ"
2. ✅ Nút "🔑 Nhận Phòng" → Check-in
3. ✅ Nút "❌ Hủy Đặt Phòng" → Hủy phiếu

### Phòng Đang Ở:
1. ✅ Hiển thị "Chi Tiết Khách Đang Ở"
2. ✅ Nút "💳 Trả Phòng" → Thanh toán & check-out
3. ✅ Tính toán hóa đơn tự động

### Phòng Bảo Trì:
1. ✅ Hiển thị "Thông Tin Bảo Trì"
2. ✅ Nút "✅ Hoàn Thành Bảo Trì" → Chuyển về Trống

## Ghi Chú

- Tất cả logic đều xử lý exception và hiển thị thông báo lỗi
- Database được cập nhật ngay lập tức
- Trạng thái UI luôn đồng bộ với database
- Các phương thức đều có xác nhận trước khi thực hiện

