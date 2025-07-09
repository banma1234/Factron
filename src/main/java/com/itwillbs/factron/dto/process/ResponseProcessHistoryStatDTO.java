package com.itwillbs.factron.dto.process;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProcessHistoryStatDTO {
    private Long processId;
    private LocalDate workDate;
    private double yieldRate;
    private double movingAverageYieldRate;
    private double movingStddevYieldRate;
    private Long inputQty;
    private Long outputQty;
    private String processName;
}
