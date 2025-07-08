package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "line")
public class Line extends com.itwillbs.factron.entity.BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "line_seq")
    @SequenceGenerator(name = "line_seq", sequenceName = "line_seq", allocationSize = 1)
    private Long id; // 라인 ID

    @Column(name = "name", length = 100, nullable = false)
    private String name; // 라인 이름

    @Column(name = "status_code", length = 6, nullable = false)
    private String statusCode; // 라인 상태 코드 (예: 운영, 정지)

    @Column(name = "description", length = 255)
    private String description; // 라인 설명

    // 라인 정보 수정
    public void updateLineInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // 라인 상태 코드 수정
    public void updateStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
