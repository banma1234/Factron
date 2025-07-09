package com.itwillbs.factron.dto.process;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class RequestProcessHistStatDTO {
    @NotNull(message = "검색 내용이 없습니다.")
    String processNameOrId;
    
    // 시작 날짜 (기본값: 30일 전)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startDate;
    
    // 종료 날짜 (기본값: 현재 시간)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endDate;
    
    /**
     * 시작 날짜가 설정되지 않은 경우 기본값 설정 (30일 전)
     */
    public LocalDateTime getStartDate() {
        if (startDate == null) {
            return LocalDateTime.now().minusDays(30);
        }
        return startDate;
    }
    
    /**
     * 종료 날짜가 설정되지 않은 경우 기본값 설정 (현재 시간)
     */
    public LocalDateTime getEndDate() {
        if (endDate == null) {
            return LocalDateTime.now();
        }
        return endDate;
    }
}
