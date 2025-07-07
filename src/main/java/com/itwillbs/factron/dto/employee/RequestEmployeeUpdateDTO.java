package com.itwillbs.factron.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEmployeeUpdateDTO {
    @NotNull(message = "사원 ID는 필수입니다.")
    private Long empId;
    
    @NotNull(message = "이름은 필수입니다.")
    @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 입력 가능합니다.")
    private String empName;
    
    @NotNull(message = "생년월일은 필수입니다.")
    @Pattern(regexp = "^\\d{6}$", message = "생년월일은 6자리 숫자여야 합니다.")
    private String birth;
    
    @NotNull(message = "주민번호 뒷자리는 필수입니다.")
    @Pattern(regexp = "^\\d{7}$", message = "주민번호 뒷자리는 7자리 숫자여야 합니다.")
    private String rrnBack;

    @NotNull(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    @NotNull(message = "성별은 필수입니다.")
    @Pattern(regexp = "^(M|F)$", message = "성별은 M 또는 F만 입력 가능합니다.")
    private String gender;
    
    @NotNull(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다.")
    private String phone;
    
    @NotNull(message = "주소는 필수입니다.")
    private String address;
    
    @NotNull(message = "재직여부는 필수입니다.")
    @Pattern(regexp = "^(Y|N)$", message = "재직여부는 Y 또는 N만 입력 가능합니다.")
    private String empIsActive;

    @NotNull(message = "학력 코드는 필수입니다.")
    @Pattern(regexp = "^[A-Z]{3}\\d{3}$", message = "학력 코드는 대문자 3글자와 숫자 3자리 조합이어야 합니다.")
    private String eduLevelCode;
    
    @NotNull(message = "고용 코드는 필수입니다.")
    @Pattern(regexp = "^[A-Z]{3}\\d{3}$", message = "고용 코드는 대문자 3글자와 숫자 3자리 조합이어야 합니다.")
    private String employCode;
}
