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
public class RequestDisconnectProcessesFromLineDTO {

    @NotNull(message = "공정 ID 목록은 필수입니다")
    private List<Long> processIds; // 해당 공정의 lineId를 null로 변경하면 되기에 processId 리스트만 필요
}
