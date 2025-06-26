package com.itwillbs.factron.dto.production;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePrdctPlanDTO {

    private String id;
    private String itemId;
    private String itemName;
    private Long quantity;
    private String unit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long empId;
    private String empName;
}
