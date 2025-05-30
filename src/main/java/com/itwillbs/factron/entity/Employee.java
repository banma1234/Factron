package com.itwillbs.factron.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

    @Column(name = "joined_code", nullable = false)
    private LocalDate joinedCode;

    @Column(name = "quit_code")
    private LocalDate quitCode;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "phone", length = 100, nullable = false)
    private String phone;

    @Column(name = "birth", length = 100, nullable = false)
    private String birth;

    @Column(name = "rrn_back", length =225, nullable = false)
    private String rrnBack;

    @Column(name = "gender", length = 1, nullable = false)
    private String gender;

}
