package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.BaseEntity;
import com.itwillbs.factron.entity.Line;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "process")
public class Process extends com.itwillbs.factron.entity.BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "process_seq")
    @SequenceGenerator(name = "process_seq", sequenceName = "process_seq", allocationSize = 1)
    private Long id; // 공정 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", referencedColumnName = "id")
    private Line line; // 공정이 속한 생산 라인

    @Column(name = "name", length = 255, nullable = false)
    private String name; // 공정 이름

    @Column(name = "description", length = 255)
    private String description; // 공정 설명

    @Column(name = "type_code", length = 6, nullable = false)
    private String typeCode; // 공정 유형 코드 (예: 도색, 조립 등)

    @Column(name = "standard_time", nullable = false)
    private Long standardTime; // 공정 기준 시간 (분 단위)

    @Column(name = "has_machine", length = 1, nullable = false)
    private String hasMachine; // 설비 여부 (Y/N), 기본값은 'N'

    // 공정 정보 수정 메소드
    public void updateProcessInfo(String name, String description, String typeCode, Long standardTime) {
        this.name = name;
        this.description = description;
        this.typeCode = typeCode;
        this.standardTime = standardTime;
    }

    // 공정의 설비 여부 수정 메소드
    public void updateHasMachine(String hasMachine) {
        this.hasMachine = hasMachine;
    }

    // 공정의 라인 정보 수정 메소드
    public void updateLine(Line line) {
        this.line = line;
    }
}
