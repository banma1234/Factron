package com.itwillbs.factron.dto.sys;

import com.itwillbs.factron.entity.DetailSysCode;
import com.itwillbs.factron.entity.SysCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
public class RequestSysDetailDTO {

    @NotBlank(message = "메인코드는 필수 입력값입니다.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "메인코드는 3자리의 대문자 알파벳만 사용 가능합니다.")
    private String main_code;

    @NotBlank(message = "상세코드는 필수 입력값입니다.")
    @Pattern(regexp = "^[A-Z]{3}[0-9]{3}$", message = "상세코드는 3자리의 대문자 알파벳과 3자리 숫자의 조합만 허용됩니다.")
    private String detail_code;

    @NotBlank(message = "구분명은 필수 입력값입니다.")
    private String name;

    @Pattern(regexp = "^[YN]$", message = "사용여부는 Y 또는 N만 입력 가능합니다.")
    private String is_active;

    @Builder
    public RequestSysDetailDTO(String main_code, String detail_code, String name, String is_active) {
        this.main_code = main_code;
        this.detail_code = detail_code;
        this.name = name;
        this.is_active = is_active;
    }

    /**
     * DTO -> Entity 변환
     * @param DTO 요청 DTO
     * @param sysCode 부모 sysCode
     * @return detailSysCode 엔티티
     * */
    public static DetailSysCode toEntity(RequestSysDetailDTO DTO, SysCode sysCode) {
        return DetailSysCode.builder()
                .sysCode(sysCode)
                .mainCode(DTO.getMain_code())
                .detailCode(DTO.getDetail_code())
                .name(DTO.getName())
                .isActive(DTO.getIs_active())
                .createdBy(20180924L)
                .build();
    }
    
}