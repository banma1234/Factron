package com.itwillbs.factron.dto.process;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestProcessHistStatDTO {
    @NotNull(message = "검색 내용이 없습니다.")
    String processNameOrId;
}
