package com.itwillbs.factron.dto.sys;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysMain {

    private Long id;
    private String main_code;
    private String name;
    private String is_active;
    private String created_by;
    private LocalDateTime created_at;
}
