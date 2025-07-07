package com.itwillbs.factron.dto.line;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestConnectProcessesToLineDTO {

    @NotNull(message = "라인 ID는 필수입니다")
    private Long lineId;

    @NotNull(message = "공정 ID 목록은 필수입니다")
    private List<Long> processIds;
}
