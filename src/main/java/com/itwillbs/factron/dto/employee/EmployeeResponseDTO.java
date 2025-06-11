package com.itwillbs.factron.dto.employee;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private Long empId; //사번
    private String empName; //이름
    private String residentRegistrationNumber; //주민번호
    private String gender; //성별
    private String email; //메일주소
    private String eduLevelCode; //최종학력코드
    private String eduLevelName; //최종학력이름
    private String address; //주소
    private String quitDate; //퇴사일
    private String positionCode; //직급코드
    private String positionName; //직급이름
    private String empIsActive; //재직상태
    private String joinedDate; //입사일
    private String employCode; //입사유형코드
    private String employName; //입사유형이름
    private String phone; //전화번호
    private String deptCode; //부서코드
    private String deptName; //부서이름
}
