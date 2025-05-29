package com.itwillbs.factron.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_code")
public class SysCode extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "main_code", length = 3, nullable = false)
    private String mainCode;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "is_active", length = 1, nullable = false)
    private String isActive;
}


