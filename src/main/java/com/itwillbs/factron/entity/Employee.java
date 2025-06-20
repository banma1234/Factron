package com.itwillbs.factron.entity;


import com.itwillbs.factron.dto.employee.RequestEmployeeUpdateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@SuperBuilder
@Table(name = "employee")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {

    @Id
    private Long id;

    @Column(name = "employ_code", length = 6, nullable = false)
    private String employCode;

    @Column(name = "dept_code", length = 6, nullable = false)
    private String deptCode;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "address", length = 200, nullable = false)
    private String address;

    @Column(name = "position_code", length = 6, nullable = false)
    private String positionCode;

    @Column(name = "joined_date")
    @CreatedDate
    private LocalDate joinedDate;

    @Column(name = "quit_date")
    private LocalDate quitDate;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 100, nullable = false, unique = true)
    private String phone;

    @Column(name = "birth", length = 100, nullable = false)
    private String birth;

    @Column(name = "rrn_back", length =225, nullable = false)
    private String rrnBack;

    @Column(name = "gender", length = 1, nullable = false)
    private String gender;

    @Column(name = "edu_level_code", length = 6, nullable = false)
    private String eduLevelCode;

    public void updatePositionCode(String newPositionCode) {
        this.positionCode = newPositionCode;
    }

    public void updateDeptCode(String newDeptCode) {
        this.deptCode = newDeptCode;
    }

    /**
     * 일반 사원이 본인의 개인정보를 수정합니다.
     * @param reqEmpUpDto {@link RequestEmployeeUpdateDTO}
     */
    public void updateNormEmployeeInfo(RequestEmployeeUpdateDTO reqEmpUpDto) {
        this.name = reqEmpUpDto.getEmpName();
        this.birth = reqEmpUpDto.getBirth();
        this.rrnBack = reqEmpUpDto.getRrnBack();
        this.email = reqEmpUpDto.getEmail();
        this.phone = reqEmpUpDto.getPhone();
        this.address = reqEmpUpDto.getAddress();
    }

    /**
     * 인사 직원이 사원의 정보(퇴사 여부 포함)를 수정합니다.
     * @param reqEmpUpDto {@link RequestEmployeeUpdateDTO}
     */
    public void updateTranfEmployeeInfo(RequestEmployeeUpdateDTO reqEmpUpDto) {
        this.gender = reqEmpUpDto.getGender();
        this.eduLevelCode = reqEmpUpDto.getEduLevelCode();
        if ("N".equals(reqEmpUpDto.getEmpIsActive())) this.quitDate = LocalDate.now();
        this.employCode = reqEmpUpDto.getEmployCode();
    }
}
