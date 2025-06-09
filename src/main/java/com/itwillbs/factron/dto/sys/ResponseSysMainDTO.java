package com.itwillbs.factron.dto.sys;

import com.itwillbs.factron.entity.SysCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseSysMainDTO {

    private Long id;
    private String main_code;
    private String name;
    private String is_active;
    private Long created_by;
    private LocalDateTime created_at;

    @Builder
    public ResponseSysMainDTO(Long id, String main_code, String name, String is_active, Long created_by, LocalDateTime created_at) {
        this.id = id;
        this.main_code = main_code;
        this.name = name;
        this.is_active = is_active;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    /**
     * Entity -> DTO 변환
     * @param entity 엔티티
     * @return responseMainDTO 반환 DTO
     * */
    public static ResponseSysMainDTO fromEntity(SysCode entity) {
        return ResponseSysMainDTO.builder()
                .id(entity.getId())
                .main_code(entity.getMainCode())
                .name(entity.getName())
                .is_active(entity.getIsActive())
                .created_by(entity.getCreatedBy())
                .created_at(entity.getCreatedAt())
                .build();
    }

}
