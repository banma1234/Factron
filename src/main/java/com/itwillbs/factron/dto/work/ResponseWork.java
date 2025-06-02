package com.itwillbs.factron.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseWork {

    private Long id;
    private LocalDate workDate;
    private String startTime;
    private String endTime;
    private String workCode;
    private String workName;
    private Long empId;
    private String empName;
}
