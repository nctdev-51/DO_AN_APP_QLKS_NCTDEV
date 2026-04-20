package core.repository;

import core.entity.NhanVien;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TaiKhoanRepository extends BaseRepository {

    public NhanVien findNhanVienByCredentials(String username, String password) {
        EntityManager em = createEntityManager();
        try {
            List<NhanVien> result = em.createQuery("""
                            SELECT tk.nhanVien
                            FROM TaiKhoan tk
                            WHERE tk.tenDangNhap = :username
                              AND tk.matKhau = :password
                              AND tk.trangThaiTk = true
                            """, NhanVien.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .setMaxResults(1)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }
}
