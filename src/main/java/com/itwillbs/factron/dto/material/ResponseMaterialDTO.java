package com.itwillbs.factron.dto.material;

import lombok.Data;

@Data
public class ResponseMaterialDTO {
    private String materialId;
    private String name;
    private String unit;
    private String unitName;
    private String info;
    private String spec;
    private String createdAt;
    private String createdBy;
}