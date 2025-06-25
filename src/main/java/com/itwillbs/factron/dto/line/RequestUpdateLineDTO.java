package com.itwillbs.factron.dto.line;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateLineDTO {

    private Long lineId; // 라인 ID

    @NotBlank(message = "라인 이름은 필수입니다.")
    @Size(max = 20, message = "라인 이름은 20자 이내로 입력해주세요.")
    private String lineName; // 라인 이름

    @Size(max = 70, message = "설명은 70자 이내로 입력해주세요.")
    private String description; // 라인 설명 (선택)
}
