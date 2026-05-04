package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import dao.LichSuCaLamViec_DAO;
import entity.LichSuCaLamViec;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LichSuCaLamViec_Gui extends JPanel {

    private JTable tableLichSu;
    private DefaultTableModel modelLichSu;
    private LichSuCaLamViec_DAO ls_dao;
    private JButton btnTaiLai;

    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat MONEY_FORMATTER = new DecimalFormat("#,##0 VND");

    public LichSuCaLamViec_Gui() {
        ls_dao = new LichSuCaLamViec_DAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.setBackground(Color.WHITE);
        btnTaiLai = new JButton("Tải lại danh sách");
        pnlTop.add(btnTaiLai);
        add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Mã LS", "Ngày làm", "Nhân viên", "Ca", "Nhận ca", "Bàn giao", "Tiền đầu ca", "Tiền cuối ca", "Trạng thái"};
        modelLichSu = new DefaultTableModel(cols, 0);
        tableLichSu = new JTable(modelLichSu);
        tableLichSu.setRowHeight(25);
        
        JScrollPane scroll = new JScrollPane(tableLichSu);
        scroll.setBorder(BorderFactory.createTitledBorder("Toàn bộ lịch sử ca làm việc"));
        add(scroll, BorderLayout.CENTER);
        
        loadDataToTable();
        
        btnTaiLai.addActionListener(e -> loadDataToTable());
    }

    private void loadDataToTable() {
        modelLichSu.setRowCount(0);
        List<LichSuCaLamViec> dsLS = ls_dao.getAllLichSu();
        for (LichSuCaLamViec ls : dsLS) {
            modelLichSu.addRow(new Object[]{
                ls.getMaLichSu(),
                ls.getNgayLamViec() != null ? ls.getNgayLamViec().format(DATE_FORMATTER) : "N/A",
                ls.getNhanVien() != null ? ls.getNhanVien().getHoTen() : "N/A",
                ls.getCaLamViec() != null ? ls.getCaLamViec().getTenCa() : "N/A",
                ls.getThoiGianNhanCa() != null ? ls.getThoiGianNhanCa().format(DATETIME_FORMATTER) : "N/A",
                ls.getThoiGianBanGiao() != null ? ls.getThoiGianBanGiao().format(DATETIME_FORMATTER) : "N/A",
                MONEY_FORMATTER.format(ls.getTienMatDauCa()),
                MONEY_FORMATTER.format(ls.getTienMatCuoiCa()),
                ls.getTrangThai()
            });
        }
    }
}