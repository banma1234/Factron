package com.itwillbs.factron.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "commute_history")
public class CommuteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "commute_in", nullable = false)
    private LocalDateTime commuteIn;

    @Column(name = "commute_out")
    private LocalDateTime commuteOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    /**
     *  퇴근 시간을 변경하는 메소드
     *  @param commuteOut 현재 퇴근 시간
     */
    public void changeCommuteOut(LocalDateTime commuteOut) {

        this.commuteOut = commuteOut;
    }
}
