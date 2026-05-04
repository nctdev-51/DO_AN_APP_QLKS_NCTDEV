package services;

import dao.Phong_DAO;
import dto.PhongFilterCriteria;
import dto.PhongFilterResult;
import entity.Phong;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhongFilterService {

    private Phong_DAO phongDAO;

    public PhongFilterService() {
        this.phongDAO = new Phong_DAO();
    }

    /**
     * Hàm logic chính để lọc phòng dựa trên tiêu chí.
     * @param criteria Tiêu chí lọc từ GUI
     * @return một đối tượng PhongFilterResult chứa kết quả
     */
    public PhongFilterResult filterPhong(PhongFilterCriteria criteria) {
        
        // 1. Xác thực đầu vào
        if (criteria.getCheckInDate() == null || criteria.getCheckOutDate() == null) {
            return new PhongFilterResult(PhongFilterResult.ResultType.ERROR, "Vui lòng chọn ngày nhận và ngày trả phòng.", null);
        }
        if (!criteria.getCheckOutDate().after(criteria.getCheckInDate())) {
            return new PhongFilterResult(PhongFilterResult.ResultType.ERROR, "Ngày trả phòng phải sau ngày nhận phòng.", null);
        }

        int totalGuests = criteria.getTotalGuests();
        int maxRoomsWanted = criteria.getMaxRoomsWanted();
        
        // 2. Lấy danh sách phòng có sẵn theo ngày + giá (từ DAO)
        List<Phong> available = phongDAO.getPhongTheoTieuChi(
            criteria.getCheckInDate(),
            criteria.getCheckOutDate(),
            criteria.getPriceMin(),
            criteria.getPriceMax()
        );

        if (available.isEmpty()) {
            return new PhongFilterResult(PhongFilterResult.ResultType.NO_ROOMS_FOUND, "Không tìm thấy phòng nào phù hợp với tiêu chí ngày và giá.", null);
        }

        // 3. Tìm các lựa chọn phòng đơn
        List<List<Phong>> singleCandidates = new ArrayList<>();
        for (Phong r : available) {
            if (r.getSucChua() >= totalGuests) {
                List<Phong> one = new ArrayList<>();
                one.add(r);
                singleCandidates.add(one);
            }
        }
        
        if (!singleCandidates.isEmpty()) {
            // Sắp xếp theo giá tăng dần
            singleCandidates.sort((a, b) -> Double.compare(a.get(0).getGiaPhong(), b.get(0).getGiaPhong()));
            return new PhongFilterResult(PhongFilterResult.ResultType.SINGLE_OPTIONS, "Có các phòng đơn phù hợp, chọn 1 phòng:", singleCandidates);
        }

        // 4. Không có phòng đơn -> tìm tổ hợp (số phòng <= maxRoomsWanted)
        int maxRoomsLimit = Math.max(1, maxRoomsWanted);
        if (maxRoomsLimit > 8) maxRoomsLimit = 8; // Giới hạn an toàn

        List<List<Phong>> combos = findCombinations(new ArrayList<>(available), totalGuests, maxRoomsLimit);

        if (combos.isEmpty()) {
            // Thử lại với giới hạn rộng hơn
            combos = findCombinations(new ArrayList<>(available), totalGuests, 6);
        }

        if (combos.isEmpty()) {
            return new PhongFilterResult(PhongFilterResult.ResultType.NO_ROOMS_FOUND, "Không tìm được tổ hợp phòng để đáp ứng số khách với hạn mức phòng hiện tại.", null);
        }

        // 5. Trả về các tổ hợp tìm được
        return new PhongFilterResult(PhongFilterResult.ResultType.COMBO_OPTIONS, "Các tổ hợp phù hợp (ít phòng -> rẻ):\nChọn 1 tổ hợp để hiển thị", combos);
    }

    /**
     * [ĐÃ DI CHUYỂN TỪ TrangChu_Gui]
     * Helper: tìm tất cả tổ hợp (không vượt quá maxRooms) sao cho tổng sucChua >= totalGuests
     */
    private List<List<Phong>> findCombinations(List<Phong> rooms, int totalGuests, int maxRooms) {
        List<List<Phong>> results = new ArrayList<>();
        if (rooms == null || rooms.isEmpty()) return results;

        final int MAX_TO_CONSIDER = 16;
        List<Phong> candidate = rooms.stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getSucChua(), a.getSucChua()); // capacity desc
                    if (cmp != 0) return cmp;
                    return Double.compare(a.getGiaPhong(), b.getGiaPhong()); // price asc
                })
                .limit(MAX_TO_CONSIDER)
                .collect(Collectors.toList());

        List<Phong> cur = new ArrayList<>();
        final int N = candidate.size();

        java.util.function.BiConsumer<Integer, Void> dfs = new java.util.function.BiConsumer<Integer, Void>() {
            @Override
            public void accept(Integer start, Void __) {
                int capSum = 0;
                for (Phong p : cur) {
                    capSum += p.getSucChua();
                }
                if (capSum >= totalGuests && !cur.isEmpty()) {
                    results.add(new ArrayList<>(cur));
                }
                if (cur.size() >= maxRooms) return;
                for (int i = start; i < N; i++) {
                    cur.add(candidate.get(i));
                    this.accept(i + 1, null);
                    cur.remove(cur.size() - 1);
                }
            }
        };

        dfs.accept(0, null);

        // Sắp xếp kết quả: (1) ít phòng trước, (2) tổng giá nhỏ trước
        results.sort((a, b) -> {
            int c = Integer.compare(a.size(), b.size());
            if (c != 0) return c;
            double pa = a.stream().mapToDouble(Phong::getGiaPhong).sum();
            double pb = b.stream().mapToDouble(Phong::getGiaPhong).sum();
            return Double.compare(pa, pb);
        });

        return results;
    }
}