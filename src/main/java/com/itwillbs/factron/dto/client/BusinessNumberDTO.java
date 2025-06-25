package com.itwillbs.factron.dto.client;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 사업자등록번호 요청 DTO
 * */
@Data
public class BusinessNumberDTO {

    @NotNull(message = "사업자등록번호는 필수 입력값입니다.")
    private List<String> b_no;
}
