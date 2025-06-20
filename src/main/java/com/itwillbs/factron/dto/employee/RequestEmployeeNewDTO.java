package com.itwillbs.factron.dto.employee;

import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.IntergratAuth;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestEmployeeNewDTO {

    @NotBlank(message = "이름을 입력해주세요.")
    @NotNull(message = "이름은 필수 입력 항목입니다.")
    @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 입력 가능합니다.")
    private String empName; // 사원 이름

    @NotBlank(message = "생년월일을 입력해주세요.")
    @NotNull(message = "생년월일은 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{6}$", message = "생년월일은 6자리 숫자여야 합니다.")
    private String birth; // 주민번호

    @NotBlank(message = "주민번호 뒷자리를 입력해주세요.")
    @NotNull(message = "주민번호 뒷자리는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{7}$", message = "주민번호 뒷자리는 7자리 숫자여야 합니다.")
    private String rrnBack; // 주민번호

    @NotBlank(message = "이메일을 입력해주세요.")
    @NotNull(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email; // 이메일

    @NotBlank(message = "성별을 입력해주세요.")
    @NotNull(message = "성별은 필수 입력 항목입니다.")
    @Pattern(regexp = "^(M|F)$", message = "성별은 M 또는 F만 입력 가능합니다.")
    private String gender; // 성별

    @NotBlank(message = "전화번호를 입력해주세요.")
    @NotNull(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다.")
    private String phone; // 전화번호

    @NotNull(message = "주소는 필수 입력 항목입니다.")
    private String address; // 주소

    @NotBlank(message = "입사일을 입력해주세요.")
    @NotNull(message = "입사일은 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "입사일은 YYYY-MM-DD 형식이어야 합니다.")
    private String joinedDate; // 입사일

    @NotBlank(message = "학력 코드를 입력해주세요.")
    @NotNull(message = "학력 코드는 필수 입력 항목입니다.")
    @Pattern(regexp = "^[A-Z]{3}\\d{3}$", message = "학력 코드는 대문자 3글자와 숫자 3자리 조합이어야 합니다.")
    private String eduLevelCode; // 학력 코드

    @NotBlank(message = "직급 코드를 입력해주세요.")
    @NotNull(message = "직급 코드는 필수 입력 항목입니다.")
    @Pattern(regexp = "^[A-Z]{3}\\d{3}$", message = "직급 코드는 대문자 3글자와 숫자 3자리 조합이어야 합니다.")
    private String positionCode; // 직급 코드

    @NotBlank(message = "부서 코드를 입력해주세요.")
    @NotNull(message = "부서 코드는 필수 입력 항목입니다.")
    @Pattern(regexp = "^[A-Z]{3}\\d{3}$", message = "부서 코드는 대문자 3글자와 숫자 3자리 조합이어야 합니다.")
    private String deptCode; // 부서 코드

    @NotBlank(message = "고용 코드를 입력해주세요.")
    @NotNull(message = "고용 코드는 필수 입력 항목입니다.")
    @Pattern(regexp = "^[A-Z]{3}\\d{3}$", message = "고용 코드는 대문자 3글자와 숫자 3자리 조합이어야 합니다.")
    private String employCode; //입사 유형 코드

    @NotBlank(message = "재직여부를 입력해주세요.")
    @NotNull(message = "재직여부는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(Y|N)$", message = "재직여부는 Y 또는 N만 입력 가능합니다.")
    private String isActive; // 재직 상태

    public Employee toEntity(Long newId, Long addBy) {
        return Employee.builder()
                .id(newId)
                .employCode(employCode)
                .deptCode(deptCode)
                .positionCode(positionCode)
                .eduLevelCode(eduLevelCode)
                .name(empName)
                .address(address)
                .email(email)
                .phone(phone)
                .birth(birth)
                .rrnBack(rrnBack)
                .gender(gender)
                .joinedDate(LocalDate.parse(joinedDate))
                .createdBy(addBy)
                .build();
    }

    public IntergratAuth toIntergratAuth(Employee employee) {
        return IntergratAuth.builder()
                .password(phone)
                .authCode(positionCode)
                .isActive(isActive)
                .employee(employee)
                .build();
    }
}
