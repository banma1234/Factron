package com.itwillbs.factron.dto.sys;

import com.itwillbs.factron.entity.SysCode;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SysMainDTO {

    private Long id;
    private String main_code;
    private String name;
    private String is_active;
    private Long created_by;
    private LocalDateTime created_at;

    @Builder
    public SysMainDTO(Long id, String main_code, String name, String is_active, Long created_by, LocalDateTime created_at) {
        this.id = id;
        this.main_code = main_code;
        this.name = name;
        this.is_active = is_active;
        this.created_by = created_by;
        this.created_at = created_at;
    }

}
