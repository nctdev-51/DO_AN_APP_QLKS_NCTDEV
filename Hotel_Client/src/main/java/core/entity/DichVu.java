package core.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DichVu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DichVu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 20)
    private String maDichVu;

    @Column(nullable = false, length = 100)
    private String tenDichVu;

    @Column(nullable = false)
    private Double giaTien;

    @Column(length = 255)
    private String moTa;
}
