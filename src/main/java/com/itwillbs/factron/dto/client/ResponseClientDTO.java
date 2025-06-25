package com.itwillbs.factron.dto.client;

import com.itwillbs.factron.entity.Client;
import lombok.Builder;
import lombok.Data;

/**
 * client 반환 DTO
 * */
@Data
public class ResponseClientDTO {

    private Long id;
    private String name;
    private String business_number;
    private String address;
    private String contact;
    private String ceo;
    private String contact_manager;
    private String remark;

    @Builder
    public ResponseClientDTO(Long id, String name, String business_number, String address, String contact, String ceo, String contact_manager, String remark) {
        this.id = id;
        this.name = name;
        this.business_number = business_number;
        this.address = address;
        this.contact = contact;
        this.ceo = ceo;
        this.contact_manager = contact_manager;
        this.remark = remark;
    }

    /**
     * Entity -> DTO 변환
     * @param entity 엔티티
     * @return responseClientDTO 반환 DTO
     * */
    public static ResponseClientDTO fromEntity(Client entity) {
        return ResponseClientDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .business_number(entity.getBusinessNumber())
                .address(entity.getAddress())
                .contact(entity.getContact())
                .ceo(entity.getCeo())
                .contact_manager(entity.getContactManager())
                .remark(entity.getRemark())
                .build();
    }
}
