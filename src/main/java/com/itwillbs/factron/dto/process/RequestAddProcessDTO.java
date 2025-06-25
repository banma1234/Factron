package com.itwillbs.factron.dto.process;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddProcessDTO {

    @NotBlank(message = "공정명은 필수입니다.")
    @Size(max = 70, message = "공정명은 70자 이내로 입력해주세요.")
    private String processName; // 공정명

    @Size(max = 70, message = "설명은 70자 이내로 입력해주세요.")
    private String description; // 공정 설명 (선택)

    @NotBlank(message = "공정 유형 코드는 필수입니다.")
    private String processTypeCode; // 공정 유형 코드

    @NotNull(message = "공정 기준 시간은 필수입니다.")
    private Long standardTime; // 공정 기준 시간 (분 단위)
}