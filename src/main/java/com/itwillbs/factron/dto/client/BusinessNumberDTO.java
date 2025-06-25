package com.itwillbs.factron.dto.client;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class BusinessNumberDTO {

    @NotNull(message = "사업자등록번호는 필수 입력값입니다.")
    private List<String> b_no;
}
