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
@Table(name = "process")
public class Process extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 공정 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", referencedColumnName = "id", nullable = false)
    private Line line; // 공정이 속한 생산 라인

    @Column(name = "name", length = 255, nullable = false)
    private String name; // 공정 이름

    @Column(name = "desc", length = 255)
    private String desc; // 공정 설명

    @Column(name = "type_code", length = 6, nullable = false)
    private String typeCode; // 공정 유형 코드 (예: 도색, 조립 등)

    @Column(name = "standard_time", nullable = false)
    private Long standardTime; // 공정 기준 시간

    @Column(name = "has_machine", length = 1, nullable = false)
    private String hasMachine; // 설비 여부 (Y/N)
}
