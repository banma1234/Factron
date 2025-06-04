package com.itwillbs.factron.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSearchDTO {
    private String name;          // 이름
    private String deptCode;      // 부서 코드
    private String positionCode;  // 직급 코드
    private String isActive;      // 통합 인증 여부
    private Long employeeId;      // 사원 번호

    public EmployeeSearchDTO(RequestEmployeeDTO requestEmployeeDTO) {
        // name 또는 사원번호 판별
        String rawName = safeTrim(requestEmployeeDTO.getName());
        if (isNumeric(rawName)) {
            this.employeeId = Long.parseLong(rawName);
            this.name = null;
        } else {
            this.employeeId = null;
            this.name = rawName;
        }

        // 코드 유효성 검사
        this.deptCode = validateCode(requestEmployeeDTO.getDept());
        this.positionCode = validateCode(requestEmployeeDTO.getPosition());
        this.isActive = safeTrim(requestEmployeeDTO.getEmpIsActive());
    }

    // 공백 제거
    private String safeTrim(String input) {
        return (input == null) ? "" : input.trim();
    }

    // 재직여부 확인
    private String validActive(String str){
        return (str != null && (str.equals("y")  || str.equals("n")) ) ?  str.trim() : "";
    }

    // 문자열이 숫자로만 이루어 졌는지 확인
    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Code 유효성 검사
    private String validateCode(String code) {
        String trimmed = safeTrim(code);
        if (trimmed.matches("^[A-Z]{3}\\d{3}$")) {
            return trimmed;
        }
        return "";
    }

}
