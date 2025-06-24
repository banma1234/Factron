package com.itwillbs.factron.dto.client;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientDTO {

    private Long id;
    private String name;
    private String business_number;
    private String address;
    private String contact;
    private String ceo;
    private String contact_manager;
    private String remark;

    @Builder
    public ClientDTO(Long id, String name, String business_number, String address, String contact, String ceo, String contact_manager, String remark) {
        this.id = id;
        this.name = name;
        this.business_number = business_number;
        this.address = address;
        this.contact = contact;
        this.ceo = ceo;
        this.contact_manager = contact_manager;
        this.remark = remark;
    }
}
