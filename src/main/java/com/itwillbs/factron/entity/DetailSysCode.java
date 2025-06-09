package com.itwillbs.factron.entity;

import com.itwillbs.factron.dto.sys.RequestSysDetailDTO;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "detail_sys_code")
public class DetailSysCode extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_code_id", referencedColumnName = "id")
    private SysCode sysCode;

    @Column(name = "main_code", length = 3, nullable = false)
    private String mainCode;

    @Column(name = "detail_code", length = 6, nullable = false, unique = true)
    private String detailCode;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "is_active", length = 1, nullable = false)
    private String isActive;

    public void updateSysCode(@Valid RequestSysDetailDTO dto) {
        this.mainCode = dto.getMain_code();
        this.name = dto.getName();
        this.isActive = dto.getIs_active();
    }
}
