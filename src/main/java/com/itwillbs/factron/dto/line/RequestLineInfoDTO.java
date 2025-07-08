package com.itwillbs.factron.dto.line;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLineInfoDTO {

    private String lineName; // 라인 이름
    private String lineStatusCode; // 라인 상태 코드
}
