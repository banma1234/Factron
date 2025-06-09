package com.itwillbs.factron.entity;


import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
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
@Table(name = "sys_code")
public class SysCode extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "main_code", length = 3, nullable = false, unique = true)
    private String mainCode;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "is_active", length = 1, nullable = false)
    private String isActive;

    /**
     * DTO 받아서 엔티티 업데이트
     * @param dto 요청 DTO
     * */
    public void updateSysCode(@Valid RequestSysMainDTO dto) {
        this.mainCode = dto.getMain_code();
        this.name = dto.getName();
        this.isActive = dto.getIs_active();
    }
}


