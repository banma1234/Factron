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
@Table(name = "machine")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 설비 ID

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "process_id", referencedColumnName = "id", nullable = false)
    private Process process; // 연관된 공정 정보

    @Column(name = "name", length = 100, nullable= false)
    private String name; // 설비 이름

    @Column(name = "manufacturer", length = 100, nullable = false)
    private String manufacturer; // 제조사

    @Column(name = "buy_date", nullable = false)
    private String buyDate; // 구입일
}
