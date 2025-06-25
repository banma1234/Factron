package com.itwillbs.factron.dto.machine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateMachineDTO {

    private Long machineId; // 설비 ID

    @NotBlank(message = "설비명은 필수입니다.")
    @Size(max = 20, message = "설비명은 30자 이내로 입력해주세요.")
    private String machineName; // 설비명

    @NotBlank(message = "제조사명은 필수입니다.")
    @Size(max = 70, message = "제조사명은 30자 이내로 입력해주세요.")
    private String manufacturer;

    @NotNull(message = "구입일자는 필수입니다.")
    private LocalDate buyDate;
}