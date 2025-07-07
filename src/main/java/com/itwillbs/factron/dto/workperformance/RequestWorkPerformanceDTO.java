package com.itwillbs.factron.dto.workperformance;

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
public class RequestWorkPerformanceDTO {
    private Long id;                //실적 id
    private String workOrderId;     //작업지시 id
    private LocalDate startDate;    //.                                                                                                                                                                                                                                                                              작업시작일
    private LocalDate endDate;      //종료일
    private String statusCode;      // 상태 코드
    private String quantity;
    private Long fectiveQuantity;   //양품
    private Long defectiveQuantity; //불량품
    private Long employeeId;        //등록자 ID
    private LocalDateTime lastProcessStartTime; // 마지막 공정 시작 시간
}
