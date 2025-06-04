package com.itwillbs.factron.dto.sys;

import com.itwillbs.factron.entity.DetailSysCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseSysDetailDTO {

    private Long id;
    private String main_code;
    private String detail_code;
    private String name;
    private String is_active;
    private Long created_by;
    private LocalDateTime created_at;

    @Builder
    public ResponseSysDetailDTO(Long id, String main_code, String detail_code, String name, String is_active, Long created_by, LocalDateTime created_at) {
        this.id = id;
        this.main_code = main_code;
        this.detail_code = detail_code;
        this.name = name;
        this.is_active = is_active;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    // Entity → DTO
    public static ResponseSysDetailDTO fromEntity(DetailSysCode entity) {
        return ResponseSysDetailDTO.builder()
                .id(entity.getId())
                .main_code(entity.getMainCode())
                .detail_code(entity.getDetailCode())
                .name(entity.getName())
                .is_active(entity.getIsActive())
                .created_by(entity.getCreatedBy())
                .created_at(entity.getCreatedAt())
                .build();
    }
}
