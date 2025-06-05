package com.itwillbs.factron.dto.sys;

import com.itwillbs.factron.entity.SysCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
public class RequestSysMainDTO {

    @NotBlank(message = "메인코드는 필수 입력값입니다.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "메인코드는 3자리의 대문자 알파벳만 사용 가능합니다.")
    private String main_code;

    @NotBlank(message = "구분명은 필수 입력값입니다.")
    private String name;

    @Pattern(regexp = "^[YN]$", message = "사용여부는 Y 또는 N만 입력 가능합니다.")
    private String is_active;

    @Builder
    public RequestSysMainDTO(String main_code, String name, String is_active) {
        this.main_code = main_code;
        this.name = name;
        this.is_active = is_active;
    }

    // DTO -> Entity 변환
    public static SysCode toEntity(RequestSysMainDTO requestSysMainDTO) {
        return SysCode.builder()
                .mainCode(requestSysMainDTO.getMain_code())
                .name(requestSysMainDTO.getName())
                // isActive에 default = Y 속성이 있어도 명시적으로 null이 들어가버리면 동작하지 않아서 임의로 Y를 세팅해주는 구문
                .isActive(requestSysMainDTO.getIs_active() != null
                        ? requestSysMainDTO.getIs_active()
                        : "Y")
                .createdBy(20180924L)
                .build();
    }
}
