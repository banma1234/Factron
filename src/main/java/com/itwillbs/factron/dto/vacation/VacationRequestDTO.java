package com.itwillbs.factron.dto.vacation;

import com.itwillbs.factron.entity.Approval;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.VacationHistory;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
@Getter
@Builder
public class VacationRequestDTO {
    private String vacationStartDate;
    private String vacationEndDate;
    private String remark;

    public VacationHistory toEntity(Employee employee, Approval approval) {
        return VacationHistory.builder()
                .employee(employee)
                .startDate(LocalDate.parse(this.vacationStartDate))
                .endDate(LocalDate.parse(this.vacationEndDate))
                .remark(this.remark)
                .approval(approval)
                .build();
    }

}