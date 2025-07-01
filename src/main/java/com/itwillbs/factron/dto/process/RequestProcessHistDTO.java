package com.itwillbs.factron.dto.process;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Data
public class RequestProcessHistDTO {
    List<ProcessDTO> processList;
    String workOrderId;



//    public LocalDate getStartDate() {
//        return startTime.toLocalDate();
//    }
    @Data
    public static class ProcessDTO {
        Long processHistId;
        Long outputQty;
        @NotNull(message = "공정 시작시간과 종료시간은 필수입니다.")
        LocalDateTime startTime;
        @NotNull(message = "공정 시작시간과 종료시간은 필수입니다.")
        LocalDateTime endTime;
        String processTypeCode;

        /**
         * 공정 소요 시간을 분 단위로 계산
         * @return 소요 시간(분), 계산 불가능한 경우 null
         */
        public Long getCostTime() {
            // endTime이 startTime보다 이전인 경우 처리
            if (endTime.isBefore(startTime)) {
                throw new IllegalArgumentException("공정 시작 시간은 종료시간보다 빨라야합니다.");
            }

            // Duration을 사용하여 정확한 시간 차이 계산
            Duration duration = Duration.between(startTime, endTime);
            return duration.toMinutes();
        }
    }
}
