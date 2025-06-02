package com.itwillbs.factron.dto.employee;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id; //사번
    private String name; //이름
    private String rrn; //주민번호
    private String gender; //성별
    private String email; //메일주소
    private String eduLevelCode; //최종학력코드
    private String address; //주소
    private String quitDate; //퇴사일
    private String positionCode; //직급코드
    private String isActive; //재직상태
    private String joinDate; //입사일
    private String employCode; //입사유형코드
    private String phone; //전화번호
    private String deptCode; //부서코드

    public EmployeeDTO(String deptCode, String phone, String employCode, String joinDate, String isActive, String positionCode, String quitDate, String address, String eduLevelCode, String email, String gender, String rrn, String name, Long id) {
        this.deptCode = deptCode;
        this.phone = phone;
        this.employCode = employCode;
        this.joinDate = joinDate;
        this.isActive = isActive;
        this.positionCode = positionCode;
        this.quitDate = quitDate;
        this.address = address;
        this.eduLevelCode = eduLevelCode;
        this.email = email;
        this.gender = gender;
        this.rrn = rrn;
        this.name = name;
        this.id = id;
    }
}
