package com.itwillbs.factron.dto.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Test {

    private Long id;
    private String name;
    private boolean chkType;
    private LocalDate birth;
    private String address;
    private String filePath;
    private LocalDateTime regDate;
}
