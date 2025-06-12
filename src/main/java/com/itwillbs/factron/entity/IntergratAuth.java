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
@Table(name = "intergrat_auth")
public class IntergratAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "password", length = 225, nullable = false)
    private String password;

    @Column(name = "auth_code", length = 6, nullable = false)
    private String authCode;

    @Column(name = "is_active", length = 1, nullable = false)
    private String isActive;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee;
}
