package com.itwillbs.factron.dto.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseStorageDTO {

    private Long id;
    private String name;
    private String address;
    private String area;
    private String typeCode;
}
