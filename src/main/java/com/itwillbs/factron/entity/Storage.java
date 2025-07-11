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
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "storage_seq")
    @SequenceGenerator(name = "storage_seq", sequenceName = "storage_seq", allocationSize = 1)
    private Long id; // 창고 ID

    @Column(name = "name", length = 150, nullable = false)
    private String name; // 창고 이름

    @Column(name = "address", length = 255, nullable = false)
    private String address; // 창고 주소

    @Column(name = "area", length = 50, nullable = false)
    private String area; // 창고 면적

    @Column(name = "type_code", length = 6, nullable = false)
    private String typeCode; // 창고 유형 코드 (예: 제품 창고, 원자재 창고)
}
