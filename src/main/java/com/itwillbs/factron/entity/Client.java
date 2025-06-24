package com.itwillbs.factron.entity;

import com.itwillbs.factron.dto.client.RequestClientDTO;
import com.itwillbs.factron.dto.client.RequestPutClientDTO;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 거래처 ID

    @Column(name = "name", length = 100, nullable = false)
    private String name; // 거래처명

    @Column(name = "business_number", length = 50, nullable = false)
    private String businessNumber; // 사업자 등록번호

    @Column(name = "address", length = 255, nullable = false)
    private String address; // 주소

    @Column(name = "contact", length = 50, nullable = false)
    private String contact; // 연락처

    @Column(name = "ceo", length = 50, nullable = false)
    private String ceo; // 대표자명

    @Column(name = "contact_manager", length = 50, nullable = false)
    private String contactManager; // 담당자명

    @Column(name = "remark", length = 255)
    private String remark; // 비고사항

    /**
     * DTO 받아서 엔티티 업데이트
     * @param DTO 요청 DTO
     * */
    public void updateClient(@Valid RequestPutClientDTO DTO) {
        this.name = DTO.getName();
        this.businessNumber = DTO.getBusiness_number();
        this.address = DTO.getAddress();
        this.contact = DTO.getContact();
        this.ceo = DTO.getCeo();
        this.contactManager = DTO.getContact_manager();
        this.remark = DTO.getRemark();
    }
}
