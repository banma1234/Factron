package com.itwillbs.factron.dto.process;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseProcessHistoryInfoDTO {
    String processHistoryId;
    String processId;
    String processName;
    Double inputQuantity;
    Double outputQuantity;
    String lotId;
    String workOrderId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Double costTime;
    String processStatusCode;
    String processStatusName;
    String unit;
    String itemName;
    Long quantity;
    String processTypeCode;
    String processTypeName;

    public ResponseProcessHistoryInfoDTO(
            String processHistoryId,
            String processId,
            String processName,
            Double inputQuantity,
            Double outputQuantity,
            String lotId,
            String workOrderId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Double costTime,
            String processStatusCode,
            String processStatusName,
            String unit,
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
        this.startTime = startTime;
        this.endTime = startTime != null ? startTime.plusMinutes(costTime != null ? costTime.longValue() : 0) : null;
        this.costTime = costTime;
        this.processStatusCode = processStatusCode;
        this.processStatusName = processStatusName;
        this.unit = unit;
        this.itemName = itemName;
        this.quantity = quantity;
        this.processTypeCode = processTypeCode;
        this.processTypeName = processTypeName;
    }
}
