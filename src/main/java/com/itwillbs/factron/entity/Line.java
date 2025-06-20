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
@Table(name = "line")
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 라인 ID

    @Column(name = "name", length = 100, nullable = false)
    private String name; // 라인 이름

    @Column(name = "status_code", length = 6, nullable = false)
    private String statusCode; // 라인 상태 코드 (예: 운영, 정지)

    @Column(name = "description", length = 255)
    private String description; // 라인 설명
}
