package com.itwillbs.factron.dto.work;

import lombok.Data;

@Data
public class RequestWork {

    private String srhIdOrName;
    private String srhStrDate;
    private String srhEndDate;
    private String srhDeptCode;
    private String srhWorkCode;
}
