package iuh.fit.core.service;

import iuh.fit.core.dto.PhongFilterCriteria;
import iuh.fit.core.dto.PhongFilterResult;
import iuh.fit.core.entity.Phong;
import iuh.fit.core.repository.IPhongRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service: PhongFilterService
 * Mô tả: Xử lý logic lọc phòng và tìm tổ hợp tối ưu (Ít phòng nhất, Giá rẻ nhất)
 */
public class PhongFilterService {

    private final IPhongRepository phongRepository;

    public PhongFilterService(IPhongRepository phongRepository) {
        this.phongRepository = phongRepository;
    }

    /**
     * Hàm logic chính để lọc phòng dựa trên tiêu chí.
     * @param criteria Tiêu chí lọc từ GUI
     * @return một đối tượng PhongFilterResult chứa kết quả
     */
    public PhongFilterResult filterPhong(PhongFilterCriteria criteria) {

        // 1. Xác thực đầu vào
        if (criteria.getCheckInDate() == null || criteria.getCheckOutDate() == null) {
            return new PhongFilterResult(
                    PhongFilterResult.ResultType.ERROR,
                    "Vui lòng chọn ngày nhận và ngày trả phòng.",
                    null
            );
        }
        if (!criteria.getCheckOutDate().isAfter(criteria.getCheckInDate())) {
            return new PhongFilterResult(
                    PhongFilterResult.ResultType.ERROR,
                    "Ngày trả phòng phải sau ngày nhận phòng.",
                    null
            );
        }

        int totalGuests = criteria.getTotalGuests();
        int maxRoomsWanted = criteria.getMaxRoomsWanted();
        if (maxRoomsWanted <= 0) maxRoomsWanted = 5; // Mặc định tìm tối đa 5 phòng

        // 2. Lấy danh sách phòng trống theo ngày + giá
        List<Phong> availableRooms = phongRepository.findAvailableRooms(
                criteria.getCheckInDate(),
                criteria.getCheckOutDate(),
                criteria.getPriceMin(),
                criteria.getPriceMax()
        );

        if (availableRooms.isEmpty()) {
            return new PhongFilterResult(
                    PhongFilterResult.ResultType.NO_ROOMS_FOUND,
                    "Không tìm thấy phòng nào phù hợp với tiêu chí ngày và giá.",
                    null
            );
        }

        // 3. Tìm tất cả các tổ hợp phòng đáp ứng đủ số lượng khách
        List<List<Phong>> allValidCombos = findCombinations(availableRooms, totalGuests, maxRoomsWanted);

        if (allValidCombos.isEmpty()) {
            return new PhongFilterResult(
                    PhongFilterResult.ResultType.NO_ROOMS_FOUND,
                    "Không tìm được tổ hợp phòng để đáp ứng số khách với hạn mức phòng hiện tại.",
                    null
            );
        }

        // 4. Sắp xếp tối ưu: Ưu tiên 1 (Tổng giá rẻ nhất), Ưu tiên 2 (Số lượng phòng ít nhất)
        allValidCombos.sort(Comparator
                .comparingDouble(this::calculateTotalPrice)
                .thenComparingInt(List::size)
        );

        // Giới hạn trả về Top 10 tổ hợp tốt nhất để giao diện không bị quá tải
        List<List<Phong>> topCombos = allValidCombos.stream().limit(10).collect(Collectors.toList());

        // 5. Phân loại kết quả trả về cho UI
        if (topCombos.get(0).size() == 1) {
            return new PhongFilterResult(
                    PhongFilterResult.ResultType.SINGLE_OPTIONS,
                    "Có các phòng đáp ứng đủ số lượng khách. Gợi ý ưu tiên giá tốt nhất:",
                    topCombos
            );
        } else {
            return new PhongFilterResult(
                    PhongFilterResult.ResultType.COMBO_OPTIONS,
                    "Tổ hợp phòng tối ưu (Rẻ nhất & Ít phòng nhất):",
                    topCombos
            );
        }
    }

    /**
     * Hàm helper: Định lượng sức chứa khách tối đa của một phòng dựa trên Loại Phòng
     */
    private int getRoomCapacity(Phong phong) {
        String loai = phong.getMaLoaiPhong();
        if (loai == null) return 2; // Default
        return switch (loai.toUpperCase()) {
            case "DON" -> 2;       // Phòng Đơn (Tối đa 2 người)
            case "DOI" -> 4;       // Phòng Đôi (Tối đa 4 người)
            case "GIADINH" -> 6;   // Phòng Gia Đình (Tối đa 6 người)
            case "VIP" -> 2;       // Phòng VIP (Tối đa 2 người - thoải mái)
            default -> 2;
        };
    }

    /**
     * Hàm helper: Tính tổng tiền của một tổ hợp phòng
     */
    private double calculateTotalPrice(List<Phong> combo) {
        return combo.stream().mapToDouble(Phong::getGiaPhong).sum();
    }

    /**
     * Helper: Tìm tất cả tổ hợp (không vượt quá maxRooms) sao cho tổng khả năng chứa >= totalGuests
     */
    private List<List<Phong>> findCombinations(List<Phong> rooms, int totalGuests, int maxRooms) {
        List<List<Phong>> results = new ArrayList<>();

        // Lọc bớt phòng để tránh nổ tổ hợp (chọn top 20 phòng tốt nhất làm ứng viên)
        List<Phong> candidateRooms = rooms.stream()
                .sorted(Comparator.comparingDouble(Phong::getGiaPhong)) // Ưu tiên phòng rẻ đưa lên trước
                .limit(20)
                .collect(Collectors.toList());

        generateCombinations(candidateRooms, new ArrayList<>(), 0, totalGuests, maxRooms, results);
        return results;
    }

    /**
     * Thuật toán Đệ quy (Backtracking) để duyệt tìm các tổ hợp phòng
     */
    private void generateCombinations(List<Phong> allRooms, List<Phong> currentCombo, int startIdx,
                                      int remainingGuests, int remainingRooms, List<List<Phong>> results) {

        // Điều kiện dừng thành công: Đã đủ sức chứa cho tổng số khách
        if (remainingGuests <= 0) {
            results.add(new ArrayList<>(currentCombo));
            return;
        }

        // Điều kiện dừng thất bại: Hết phòng hoặc quá hạn mức số lượng phòng cho phép
        if (remainingRooms <= 0 || startIdx >= allRooms.size()) {
            return;
        }

        // Nhánh 1: CHỌN phòng tại startIdx
        Phong room = allRooms.get(startIdx);
        int capacity = getRoomCapacity(room);

        currentCombo.add(room);
        // Đệ quy tiếp tục tìm phòng (startIdx + 1 để không chọn lại phòng này)
        generateCombinations(allRooms, currentCombo, startIdx + 1, remainingGuests - capacity, remainingRooms - 1, results);
        // Backtrack (Hoàn tác)
        currentCombo.remove(currentCombo.size() - 1);

        // Nhánh 2: BỎ QUA phòng tại startIdx (Tìm các phòng tiếp theo)
        generateCombinations(allRooms, currentCombo, startIdx + 1, remainingGuests, remainingRooms, results);
    }
}