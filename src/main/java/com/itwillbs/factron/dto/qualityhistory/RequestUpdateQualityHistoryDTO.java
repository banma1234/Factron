package com.itwillbs.factron.dto.qualityhistory;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateQualityHistoryDTO {

    private Long qualityHistoryId; // 품질검사 이력 ID

    private Long qualityInspectionId; // 품질검사 ID

    @NotNull(message = "결과값은 필수입니다.")
    @Digits(integer = Integer.MAX_VALUE, fraction = Integer.MAX_VALUE, message = "결과값은 정수 및 실수만 입력 가능합니다.")
    private Double resultValue; // 검사 결과 값
}
