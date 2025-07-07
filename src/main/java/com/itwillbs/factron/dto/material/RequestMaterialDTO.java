package com.itwillbs.factron.dto.material;


import lombok.Data;

@Data
public class RequestMaterialDTO {
    //추가 수정
    private String materialId;
    private String name;
    private String unit;
    private String unitName;
    private String info;
    private String spec;
    private Long createdBy;

    //조회
    private Long empId;
    private String materialName;

}
