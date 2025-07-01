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
public class ResponseWorkOrderDTO {

    private String productionId;
    private String id;
    private String itemId;
    private String itemName;
    private Long quantity;
    private Long fectiveQuantity;
    private String unit;
    private String lineName;
    private LocalDate startDate;
    private String status;
    private Long empId;
    private String empName;
}
