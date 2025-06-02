package com.itwillbs.factron.dto.commute;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommuteRequestDto {

    private String startDate;
    private String endDate;
    private String nameOrId;
    private String dept;
}
