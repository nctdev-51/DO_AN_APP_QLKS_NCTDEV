package gui;

import dao.Phong_DAO;
import entity.LoaiPhong;
import entity.Phong;
import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ChonPhong_Gui extends JFrame {

    // ============================ KHAI BÁO BIẾN UI ================================
    private DefaultListModel<Booking> bookingListModel;
    private JList<Booking> lstBookings;
    private JPanel pnlRooms;

    // Bộ lọc
    private JDateChooser dcNgayNhan, dcNgayTra;
    private JComboBox<String> cmbLoai;
    private JComboBox<String> cmbPriceTier;
    private JFormattedTextField txtGiaMax; // Đổi tên từ txtGiaDen thành txtGiaMax cho rõ nghĩa
    private JSlider sliderPrice;

    private JButton btnTim, btnTaoPhieu, btnXacNhan, btnHuyPhieu;

    // ============================ KHAI BÁO BIẾN DỮ LIỆU ===========================
    private Phong_DAO phongDAO;
    private NhanVien nhanVien;
    private Set<String> selectedRoomIds = new HashSet<>();

    // Biến giá động
    private int PRICE_MIN_DB = 0;
    private int PRICE_MAX_DB = 10000000;

    // Hằng số phân khúc giá
    private final int TIER_LOW_MAX = 500_000;
    private final int TIER_MED_MAX = 2_000_000;
    private final int TIER_HIGH_MIN = TIER_MED_MAX + 1;
    private final boolean[] priceSyncing = {false};

    // ============================ CONSTRUCTOR =====================================
    public ChonPhong_Gui(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        this.phongDAO = new Phong_DAO();

        // Lấy giá min/max thực tế từ CSDL
        try {
             double[] minMax = this.phongDAO.getMinMaxGiaPhong();
             this.PRICE_MIN_DB = (int) minMax[0];
             this.PRICE_MAX_DB = (int) minMax[1];
        } catch (Exception e) {
             System.err.println("⚠️ Cảnh báo: Không lấy được Min/Max giá từ CSDL, dùng mặc định.");
        }

        setTitle("Chọn phòng khách sạn");
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
    }

    // ============================ KHỞI TẠO GIAO DIỆN ==============================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(Color.WHITE);
        setContentPane(root);

        root.add(initLeftPanel(), BorderLayout.WEST);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBackground(Color.WHITE);
        pnlCenter.add(initFilterPanel(), BorderLayout.NORTH);

        pnlRooms = new JPanel();
        pnlRooms.setLayout(new BoxLayout(pnlRooms, BoxLayout.Y_AXIS));
        pnlRooms.setBackground(Color.WHITE);
        
        JScrollPane scrRooms = new JScrollPane(pnlRooms);
        scrRooms.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Danh sách phòng trống (Nhấn để chọn)"));
        scrRooms.getVerticalScrollBar().setUnitIncrement(16);
        pnlCenter.add(scrRooms, BorderLayout.CENTER);

        root.add(pnlCenter, BorderLayout.CENTER);

        registerEvents();
        
        dcNgayNhan.setDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        dcNgayTra.setDate(cal.getTime());

        doSearchAndRender();
    }

    private JPanel initLeftPanel() {
        JPanel pnlLeft = new JPanel(new BorderLayout(5, 5));
        pnlLeft.setPreferredSize(new Dimension(320, 0));
        pnlLeft.setBackground(new Color(250, 250, 250));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Phiếu đặt phòng tạm"));

        bookingListModel = new DefaultListModel<>();
        lstBookings = new JList<>(bookingListModel);
        lstBookings.setCellRenderer(new BookingRenderer());
        lstBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pnlLeft.add(new JScrollPane(lstBookings), BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlBtns.setOpaque(false);
        pnlBtns.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        btnXacNhan = new JButton("✅ Xác nhận & Nhập thông tin");
        btnXacNhan.setBackground(new Color(0, 120, 215)); btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnHuyPhieu = new JButton("❌ Hủy phiếu tạm");
        btnHuyPhieu.setBackground(new Color(220, 53, 69)); btnHuyPhieu.setForeground(Color.WHITE);
        btnHuyPhieu.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnXoaHet = new JButton("🔄 Xóa tất cả");
        btnXoaHet.setBackground(Color.WHITE);
        btnXoaHet.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pnlBtns.add(btnXacNhan); pnlBtns.add(btnHuyPhieu); pnlBtns.add(btnXoaHet);
        
        btnXoaHet.addActionListener(e -> {
            if (!bookingListModel.isEmpty() && JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa HẾT phiếu tạm?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                bookingListModel.clear();
                doSearchAndRender();
            }
        });

        pnlLeft.add(pnlBtns, BorderLayout.SOUTH);
        return pnlLeft;
    }

    private JPanel initFilterPanel() {
        JPanel pnlFilter = new JPanel(new GridBagLayout());
        pnlFilter.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Bộ lọc tìm kiếm"), new EmptyBorder(5, 10, 10, 10)));
        pnlFilter.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // === CỘT 1: NGÀY NHẬN & TRẢ ===
        JPanel pnlDate = new JPanel(new GridBagLayout());
        pnlDate.setOpaque(false);
        GridBagConstraints gbcD = new GridBagConstraints();
        gbcD.insets = new Insets(2, 0, 5, 5); gbcD.fill = GridBagConstraints.HORIZONTAL;
        
        gbcD.gridx=0; gbcD.gridy=0; pnlDate.add(new JLabel("Ngày nhận:", JLabel.RIGHT), gbcD);
        dcNgayNhan = new JDateChooser(); dcNgayNhan.setDateFormatString("dd/MM/yyyy"); dcNgayNhan.setPreferredSize(new Dimension(130, 28));
        gbcD.gridx=1; pnlDate.add(dcNgayNhan, gbcD);

        gbcD.gridx=0; gbcD.gridy=1; pnlDate.add(new JLabel("Ngày trả:", JLabel.RIGHT), gbcD);
        dcNgayTra = new JDateChooser(); dcNgayTra.setDateFormatString("dd/MM/yyyy"); dcNgayTra.setPreferredSize(new Dimension(130, 28));
        gbcD.gridx=1; pnlDate.add(dcNgayTra, gbcD);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlFilter.add(pnlDate, gbc);

        // === CỘT 2: LOẠI PHÒNG ===
        JPanel pnlType = new JPanel(new BorderLayout(0, 5));
        pnlType.setOpaque(false);
        pnlType.add(new JLabel("Loại phòng:"), BorderLayout.NORTH);
        cmbLoai = new JComboBox<>(new String[]{"Tất cả"});
        for (LoaiPhong lp : LoaiPhong.values()) cmbLoai.addItem(lp.name());
        pnlType.add(cmbLoai, BorderLayout.CENTER);

        gbc.gridx = 1; gbc.weightx = 0.2;
        pnlFilter.add(pnlType, gbc);

        // === CỘT 3: KHOẢNG GIÁ (CHỈ CÒN 1 Ô "TỐI ĐA") ===
        JPanel pnlPrice = new JPanel(new BorderLayout(0, 5));
        pnlPrice.setOpaque(false);
        pnlPrice.setBorder(BorderFactory.createTitledBorder("Giá tối đa (VNĐ)"));

        sliderPrice = new JSlider(JSlider.HORIZONTAL, PRICE_MIN_DB, PRICE_MAX_DB, PRICE_MAX_DB);
        sliderPrice.setOpaque(false);
        pnlPrice.add(sliderPrice, BorderLayout.NORTH);

        JPanel pnlPriceInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlPriceInputs.setOpaque(false);
        
        cmbPriceTier = new JComboBox<>(new String[]{"Tất cả", "Dưới 500k", "500k - 2 triệu", "Trên 2 triệu", "Tùy chỉnh"});
        
        NumberFormat numFormat = NumberFormat.getIntegerInstance();
        numFormat.setGroupingUsed(true);
        NumberFormatter formatter = new NumberFormatter(numFormat);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        // Chỉ còn 1 text field cho giá tối đa
        txtGiaMax = new JFormattedTextField(formatter); 
        txtGiaMax.setColumns(9); 
        txtGiaMax.setValue(PRICE_MAX_DB);
        txtGiaMax.setFont(new Font("Segoe UI", Font.BOLD, 13));

        pnlPriceInputs.add(cmbPriceTier);
        pnlPriceInputs.add(Box.createHorizontalStrut(10));
        pnlPriceInputs.add(new JLabel("Tối đa:")); 
        pnlPriceInputs.add(txtGiaMax);
        
        pnlPrice.add(pnlPriceInputs, BorderLayout.CENTER);

        gbc.gridx = 2; gbc.weightx = 1.0;
        pnlFilter.add(pnlPrice, gbc);

        // === CỘT 4: NÚT TÁC VỤ ===
        JPanel pnlAction = new JPanel(new GridLayout(2, 1, 5, 10));
        pnlAction.setOpaque(false);
        btnTim = new JButton("🔍 Tìm phòng");
        btnTim.setBackground(new Color(0, 120, 215)); btnTim.setForeground(Color.WHITE);
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        btnTaoPhieu = new JButton("➕ Tạo phiếu tạm");
        btnTaoPhieu.setBackground(new Color(40, 167, 69)); btnTaoPhieu.setForeground(Color.WHITE);
        btnTaoPhieu.setFont(new Font("Segoe UI", Font.BOLD, 13));

        pnlAction.add(btnTim); pnlAction.add(btnTaoPhieu);

        gbc.gridx = 3; gbc.weightx = 0.1;
        pnlFilter.add(pnlAction, gbc);

        return pnlFilter;
    }

    // ==============================================================================
    // ============================ LOGIC & SỰ KIỆN =================================
    // ==============================================================================

    public void refreshData() {
        doSearchAndRender();
    }

    private void doSearchAndRender() {
        pnlRooms.removeAll();
        selectedRoomIds.clear();

        Date ngayNhan = dcNgayNhan.getDate();
        Date ngayTra = dcNgayTra.getDate();
        
        if (ngayNhan == null || ngayTra == null) return;
        if (!ngayTra.after(ngayNhan)) {
             JOptionPane.showMessageDialog(this, "Ngày trả phải sau Ngày nhận!", "Lỗi ngày tháng", JOptionPane.WARNING_MESSAGE);
             return;
        }

        // Lấy giá trị từ các bộ lọc
        // Min luôn là PRICE_MIN_DB, Max lấy từ ô nhập liệu
        double min = PRICE_MIN_DB;
        double max = ((Number)txtGiaMax.getValue()).doubleValue();
        String loaiSel = (String) cmbLoai.getSelectedItem();

        // 1. LẤY TỪ CSDL
        List<Phong> availableRooms = phongDAO.getPhongTheoTieuChi(ngayNhan, ngayTra, min, max);

        // 2. LỌC TIẾP TRÊN RAM
        Map<Integer, List<Phong>> roomsByFloor = new TreeMap<>();
        for (Phong p : availableRooms) {
            if (!"Tất cả".equals(loaiSel) && !p.getLoaiPhong().name().equals(loaiSel)) {
                continue;
            }

            boolean isConflict = false;
            for (int i = 0; i < bookingListModel.size(); i++) {
                Booking b = bookingListModel.get(i);
                if (b.rooms.stream().anyMatch(r -> r.getMaPhong().equals(p.getMaPhong()))) {
                    if (ngayNhan.before(b.to) && ngayTra.after(b.from)) {
                        isConflict = true;
                        break;
                    }
                }
            }

            if (!isConflict) {
                roomsByFloor.computeIfAbsent(deriveFloor(p.getMaPhong()), k -> new ArrayList<>()).add(p);
            }
        }

        // 3. RENDER
        if (roomsByFloor.isEmpty()) {
            JLabel lblEmpty = new JLabel("Không tìm thấy phòng trống phù hợp.", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 16)); lblEmpty.setForeground(Color.GRAY);
            pnlRooms.add(Box.createVerticalGlue()); pnlRooms.add(lblEmpty); pnlRooms.add(Box.createVerticalGlue());
        } else {
            roomsByFloor.forEach((floor, list) -> {
                JPanel pnlF = new JPanel(new BorderLayout());
                pnlF.setBackground(Color.WHITE);
                pnlF.setBorder(BorderFactory.createTitledBorder("Tầng " + floor));
                
                JPanel pnlG = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
                pnlG.setBackground(new Color(250, 250, 250));
                list.forEach(p -> pnlG.add(createRoomBtn(p)));
                
                pnlF.add(pnlG, BorderLayout.CENTER);
                pnlRooms.add(pnlF);
                pnlRooms.add(Box.createVerticalStrut(15));
            });
        }
        pnlRooms.revalidate(); pnlRooms.repaint();
    }

    private JButton createRoomBtn(Phong p) {
        JButton btn = new JButton("<html><center><b style='font-size:12px'>" + p.getMaPhong() + "</b><br/>" 
                + "<span style='font-size:10px'>" + p.getLoaiPhong().name() + "</span><br/>" 
                + "<b style='color:yellow'>" + String.format("%,.0f", p.getGiaPhong()) + "</b></center></html>");
        btn.setPreferredSize(new Dimension(110, 80));
        btn.setBackground(new Color(40, 167, 69)); // Xanh lá
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("maPhong", p.getMaPhong());

        btn.addActionListener(e -> {
            String id = (String) btn.getClientProperty("maPhong");
            if (selectedRoomIds.contains(id)) {
                selectedRoomIds.remove(id);
                btn.setBackground(new Color(40, 167, 69));
            } else {
                selectedRoomIds.add(id);
                btn.setBackground(new Color(255, 193, 7)); // Vàng cam khi chọn
            }
        });
        return btn;
    }

    private int deriveFloor(String ma) {
        try {
            if (ma != null && ma.length() >= 2 && Character.isDigit(ma.charAt(1))) {
                return Character.getNumericValue(ma.charAt(1));
            }
        } catch (Exception ignored) { }
        return 1;
    }

    private void registerEvents() {
        btnTim.addActionListener(e -> doSearchAndRender());

        btnTaoPhieu.addActionListener(e -> {
            if (selectedRoomIds.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 phòng!", "Thông báo", JOptionPane.WARNING_MESSAGE); return;
            }
            Date n = dcNgayNhan.getDate(), t = dcNgayTra.getDate();
            if (n == null || t == null || !t.after(n)) {
                 JOptionPane.showMessageDialog(this, "Kiểm tra lại ngày nhận/trả!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }
            List<Phong> rooms = new ArrayList<>();
            selectedRoomIds.forEach(id -> rooms.add(phongDAO.timPhongTheoMa(id)));
            
            String maPhieu = "TẠM-" + System.currentTimeMillis()%100000;
            bookingListModel.addElement(new Booking(maPhieu, n, t, rooms));
            
            JOptionPane.showMessageDialog(this, "Đã tạo phiếu tạm: " + maPhieu + "\n(" + rooms.size() + " phòng)");
            selectedRoomIds.clear();
            doSearchAndRender();
        });

        btnHuyPhieu.addActionListener(e -> {
            Booking b = lstBookings.getSelectedValue();
            if (b != null && JOptionPane.showConfirmDialog(this, "Hủy phiếu tạm " + b.ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                bookingListModel.removeElement(b);
                doSearchAndRender();
            }
        });

        btnXacNhan.addActionListener(e -> {
            // 1. Lấy phiếu tạm đang được chọn
            Booking b = lstBookings.getSelectedValue();
            if (b == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu để tiếp tục!");
                return;
            }
            
            // 2. Lấy danh sách mã phòng từ phiếu tạm
            List<String> ids = new ArrayList<>();
            b.rooms.forEach(p -> ids.add(p.getMaPhong()));

            // 3. Lấy các tham số cần thiết
            NhanVien nvHienTai = this.nhanVien;
            ChonPhong_Gui frameHienTai = this;
            Date ngayNhan = b.from;
            Date ngayTra = b.to;

            // 4. (ĐÃ SỬA) Tạo và hiển thị màn hình Đặt phòng mới
            // Bỏ dòng JOptionPane cũ và kích hoạt dòng new DatPhong_Gui
            new DatPhong_Gui(ids, nvHienTai, frameHienTai, ngayNhan, ngayTra).setVisible(true);

            // 5. Xóa phiếu tạm khỏi danh sách sau khi đã chuyển sang màn hình mới
            bookingListModel.removeElement(b);
            
            // (Tùy chọn: Nếu bạn muốn đóng cửa sổ ChonPhong_Gui sau khi xác nhận,
            // hãy thêm dòng "this.dispose();" ở đây)
        });

        // --- REAL-TIME SLIDER ---
        sliderPrice.addChangeListener(e -> {
            if (!priceSyncing[0]) {
                priceSyncing[0] = true;
                txtGiaMax.setValue(sliderPrice.getValue());
                cmbPriceTier.setSelectedItem("Tùy chỉnh");
                priceSyncing[0] = false;
            }
        });

        // --- INPUT SYNC (Chỉ còn 1 ô txtGiaMax) ---
        txtGiaMax.addPropertyChangeListener("value", evt -> {
            if (priceSyncing[0]) return;
            priceSyncing[0] = true;
            try {
                int val = ((Number)txtGiaMax.getValue()).intValue();
                // Ràng buộc giá trị trong khoảng DB
                if (val < PRICE_MIN_DB) val = PRICE_MIN_DB;
                if (val > PRICE_MAX_DB) val = PRICE_MAX_DB;
                
                txtGiaMax.setValue(val);
                sliderPrice.setValue(val);
                cmbPriceTier.setSelectedItem("Tùy chỉnh");
            } finally { priceSyncing[0] = false; }
        });

        // --- COMBOBOX GIÁ ---
        cmbPriceTier.addActionListener(e -> {
            String s = (String)cmbPriceTier.getSelectedItem();
            if(s==null || "Tùy chỉnh".equals(s)) return;
            priceSyncing[0] = true;
            int val = PRICE_MAX_DB;
            if(s.contains("Dưới 500k")) val = TIER_LOW_MAX;
            else if(s.contains("500k - 2 triệu")) val = TIER_MED_MAX;
            // "Trên 2 triệu" và "Tất cả" đều set về MAX_DB
            
            txtGiaMax.setValue(val);
            sliderPrice.setValue(val);
            priceSyncing[0] = false;
        });
    }

    // ============================ INNER CLASSES ===================================
    private static class Booking {
        String ma; Date from, to; List<Phong> rooms;
        Booking(String m, Date f, Date t, List<Phong> r) { ma=m; from=f; to=t; rooms=r; }
        @Override public String toString() { return ma; }
    }
    private static class BookingRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
            super.getListCellRendererComponent(l, v, i, s, f); 
            setBorder(new EmptyBorder(8, 8, 8, 8));
            if(v instanceof Booking) {
                Booking b = (Booking)v;
                setText("<html><b style='color:#007bff'>" + b.ma + "</b><br/>" 
                        + new SimpleDateFormat("dd/MM").format(b.from) + " ⮕ " + new SimpleDateFormat("dd/MM").format(b.to) 
                        + "<br/>(" + b.rooms.size() + " phòng)</html>");
                StringBuilder tip = new StringBuilder("<html>Phòng: ");
                b.rooms.forEach(p -> tip.append(p.getMaPhong()).append(", "));
                setToolTipText(tip.substring(0, tip.length()-2) + "</html>");
            }
            return this;
        }
    }
}