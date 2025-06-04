package com.itwillbs.factron.dto.sys;

import com.itwillbs.factron.entity.DetailSysCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SysDetailDTO {

    private Long id;
    private Long sys_code_id;
    private String main_code;
    private String detail_code;
    private String name;
    private String is_active;
    private Long created_by;
    private LocalDateTime created_at;

    @Builder
    public SysDetailDTO(Long id, Long sys_code_id, String main_code, String detail_code, String name, String is_active, Long created_by, LocalDateTime created_at) {
        this.id = id;
        this.sys_code_id = sys_code_id;
        this.main_code = main_code;
        this.detail_code = detail_code;
        this.name = name;
        this.is_active = is_active;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    // Entity → DTO
    public static SysDetailDTO fromEntity(DetailSysCode detailSysCode) {
        return SysDetailDTO.builder()
                .id(detailSysCode.getId())
                .sys_code_id(detailSysCode.getSysCode().getId())
                .main_code(detailSysCode.getMainCode())
                .detail_code(detailSysCode.getDetailCode())
                .name(detailSysCode.getName())
                .is_active(detailSysCode.getIsActive())
                .created_by(detailSysCode.getCreatedBy())
                .created_at(detailSysCode.getCreatedAt())
                .build();
    }
}
