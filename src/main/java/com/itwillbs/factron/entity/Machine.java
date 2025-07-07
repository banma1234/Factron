package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.Process;
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
@Table(name = "machine")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "machine_seq")
    @SequenceGenerator(name = "machine_seq", sequenceName = "machine_seq", allocationSize = 1)
    private Long id; // 설비 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", referencedColumnName = "id", nullable = false)
    private Process process; // 연관된 공정 정보

    @Column(name = "name", length = 100, nullable= false)
    private String name; // 설비 이름

    @Column(name = "manufacturer", length = 100, nullable = false)
    private String manufacturer; // 제조사

    @Column(name = "buy_date", nullable = false)
    private LocalDate buyDate; // 구입일

    // 설비 정보 수정 메소드
    public void updateMachineInfo(String name, String manufacturer, LocalDate buyDate) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.buyDate = buyDate;
    }
}
