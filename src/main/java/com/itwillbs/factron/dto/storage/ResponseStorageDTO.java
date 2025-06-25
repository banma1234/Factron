package com.itwillbs.factron.dto.storage;

import com.itwillbs.factron.dto.sys.ResponseSysMainDTO;
import com.itwillbs.factron.entity.Storage;
import lombok.Builder;
import lombok.Data;

@Data
public class ResponseStorageDTO {

    private Long id;
    private String name;
    private String address;
    private String area;
    private String typeCode;

    @Builder
    public ResponseStorageDTO(Long id, String name, String address, String area, String typeCode) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.area = area;
        this.typeCode = typeCode;
    }

    public static ResponseStorageDTO fromEntity(Storage entity) {
        return ResponseStorageDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .area(entity.getArea())
                .typeCode(entity.getTypeCode())
                .build();
    }
}
