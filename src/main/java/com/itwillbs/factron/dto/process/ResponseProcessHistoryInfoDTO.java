package com.itwillbs.factron.dto.process;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ResponseProcessHistoryInfoDTO {
    String processHistoryId;
    String processId;
    String processName;
    Long inputQuantity;
    Long outputQuantity;
    String lotId;
    String workOrderId;
    LocalDate workOrderStartDate;
    String startTime;
    String endTime;
    Long costTime;
    String processStatusCode;
    String processStatusName;
    String unit;
    String unitName;
    String itemName;
    Long quantity;
    String processTypeCode;
    String processTypeName;

    public ResponseProcessHistoryInfoDTO(
            String processHistoryId,
            String processId,
            String processName,
            Long inputQuantity,
            Long outputQuantity,
            String lotId,
            String workOrderId,
            LocalDate workOrderStartDate,
            LocalDateTime startTime,
            Long costTime,
            String processStatusCode,
            String processStatusName,
            String unit,
            String unitName,
            String itemName,
            Long quantity,
            String processTypeCode,
            String processTypeName
    ) {
        this.processHistoryId = processHistoryId;
        this.processId = processId;
        this.processName = processName;
        this.inputQuantity = inputQuantity;
        this.outputQuantity = outputQuantity;
        this.lotId = lotId;
        this.workOrderId = workOrderId;
        this.startTime = getFormattedStartTime(startTime);
        this.endTime = getFormattedStartTime(startTime != null ? startTime.plusMinutes(costTime != null ? costTime.longValue() : 0) : null);
        this.workOrderStartDate = workOrderStartDate;
        this.costTime = costTime;
        this.processStatusCode = processStatusCode;
        this.processStatusName = processStatusName;
        this.unit = unit;
        this.unitName = unitName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.processTypeCode = processTypeCode;
        this.processTypeName = processTypeName;
    }

    /**
     * startTime을 "yyyy-MM-dd hh:mm a" 형식으로 변환
     * 예: 2025-07-01T15:00:00 → 2025-07-01 03:00 PM
     * @return 포맷된 날짜 문자열, startTime이 null이면 null 반환
     */
    private String getFormattedStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        return startTime.format(formatter);
    }
}
