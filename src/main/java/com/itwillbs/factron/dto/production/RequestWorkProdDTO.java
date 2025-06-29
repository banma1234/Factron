package com.itwillbs.factron.dto.production;

import lombok.Data;

@Data
public class RequestWorkProdDTO {

    private String parentItemId;
    private String planId;
    private String quantity;
}
