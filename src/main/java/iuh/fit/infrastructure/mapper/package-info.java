/**
 * Package: iuh.fit.infrastructure.mapper
 * 
 * TẦNG: INFRASTRUCTURE - Mapper/Converter
 * 
 * TRÁCH NHIỆM:
 * - Chuyển đổi giữa Entity và DTO (bidirectional)
 * - Tập trung logic transformation ở một chỗ
 * - Handle field mapping (including nested objects)
 * - Provide utility methods để convert collections
 * 
 * NGUYÊN TẮC:
 * ✅ Stateless utility classes
 * ✅ Static methods (không cần instantiate)
 * ✅ Null-safe conversions
 * ✅ Handle Enum conversions (String ↔ Enum)
 * ✅ Handle nested objects (transform ID, not full object)
 * ❌ KHÔNG có business logic
 * ❌ KHÔNG có state/fields
 * ❌ KHÔNG have side effects
 * 
 * LUỒNG SỬ DỤNG:
 * 
 * (1) SERVICE → INFRASTRUCTURE:
 * Service.addEntity(dto) → Mapper.dtoToEntity(dto) → RepositoryImpl.save()
 * 
 * (2) INFRASTRUCTURE → SERVICE:
 * RepositoryImpl.findById() → Entity → Mapper.entityToDTO(entity) → Service
 * 
 * (3) SERVICE → PRESENTATION:
 * Service.getDTO() → DTO → Controller → UI
 * 
 * IMPORTANT NOTES:
 * 
 * Entity vs DTO:
 * ├─ Entity: Full object graph (relationships as objects)
 * ├─ DTO: Flattened (relationships as IDs or primitive values)
 * └─ Mapper: Bridge between them
 * 
 * VÍ DỤ - TAИКHOAN:
 * 
 * Entity:
 *   - taiKhoan: String
 *   - matKhau: String
 *   - nhanVien: NhanVien (full object)
 * 
 * DTO:
 *   - taiKhoan: String
 *   - matKhau: String (or omitted for security)
 *   - maNhanVien: String (ID only)
 *   - hoTenNhanVien: String (for display)
 * 
 * Mapper.entityToDTO(entity):
 *   ├─ dto.taiKhoan = entity.taiKhoan
 *   ├─ dto.matKhau = (omit for security)
 *   ├─ if (entity.nhanVien != null):
 *   │   ├─ dto.maNhanVien = entity.nhanVien.maNhanVien (extract ID)
 *   │   └─ dto.hoTenNhanVien = entity.nhanVien.hoTen (for UI display)
 *   └─ return dto
 * 
 * CÁC FILES:
 * - TaiKhoanMapper.java
 *   ├─ entityToDTO(TaiKhoan): TaiKhoanDTO
 *   └─ dtoToEntity(TaiKhoanDTO): TaiKhoan
 * 
 * - KhachHangMapper.java
 *   ├─ entityToDTO(KhachHang): KhachHangDTO
 *   └─ dtoToEntity(KhachHangDTO): KhachHang
 * 
 * COLLECTION MAPPING:
 * 
 * // Map list of entities to list of DTOs
 * List<TaiKhoanDTO> dtos = entities.stream()
 *     .map(TaiKhoanMapper::entityToDTO)
 *     .collect(Collectors.toList());
 * 
 * BEST PRACTICES:
 * - Always check null before converting
 * - Handle Enum conversions safely (try-catch)
 * - Document complex mappings
 * - Test mapping logic (especially nested objects)
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.infrastructure.mapper;

