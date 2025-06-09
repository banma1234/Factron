package com.itwillbs.factron.dto.sys;

import com.itwillbs.factron.entity.DetailSysCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseSysDetailDTO {

    @Positive
    @NotBlank(message = "상세코드 id는 필수값입니다.")
    private Long id;

    @NotBlank(message = "메인코드는 필수값입니다.")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "메인코드는 3자리 알파벳만 사용 가능합니다.")
    private String main_code;

    @Positive
    @NotBlank(message = "상세코드는 필수값입니다.")
    @Pattern(regexp = "^\\d{3}$", message = "상세코드는 3자리 숫자만 사용 가능합니다.")
    private String detail_code;

    @NotBlank(message = "구분명은 필수값입니다.")
    private String name;

    @NotBlank(message = "사용여부는 데이터를 불러올 시에는 필수값입니다.")
    @Pattern(regexp = "^[YN]$", message = "사용여부는 Y 또는 N만 사용 가능합니다.")
    private String is_active;

    @Positive
    @Pattern(regexp = "^\\d{8}$", message = "사원번호는 8자리 숫자만 사용 가능합니다.")
    private Long created_by;

    private LocalDateTime created_at;

    @Builder
    public ResponseSysDetailDTO(Long id, String main_code, String detail_code, String name, String is_active, Long created_by, LocalDateTime created_at) {
        this.id = id;
        this.main_code = main_code;
        this.detail_code = detail_code;
        this.name = name;
        this.is_active = is_active;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    /**
     * Entity → DTO 변환
     * @param entity 엔티티
     * @return responseDetailDTO 반환 DTO
     * */
    public static ResponseSysDetailDTO fromEntity(DetailSysCode entity) {
        return ResponseSysDetailDTO.builder()
                .id(entity.getId())
                .main_code(entity.getMainCode())
                .detail_code(entity.getDetailCode())
                .name(entity.getName())
                .is_active(entity.getIsActive())
                .created_by(entity.getCreatedBy())
                .created_at(entity.getCreatedAt())
                .build();
    }
}
