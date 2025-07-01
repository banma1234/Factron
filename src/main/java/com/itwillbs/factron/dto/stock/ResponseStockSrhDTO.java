package com.itwillbs.factron.dto.stock;

import lombok.Data;

@Data
public class ResponseStockSrhDTO {
    private Long id;           // 재고 ID
    private Long quantity;     // 수량
    private Long storageId;    // 창고 ID
    private String storageName; // 창고 이름
    private String productId;  // 제품/자재 ID
    private String productName; // 제품/자재 이름
    private String unit;       // 단위
    private String productType; // 제품 타입 (Material 또는 Item)
    private String productTypeName;

    // JPA에서 직접 DTO 반환을 위한 생성자
    public ResponseStockSrhDTO(Long id, Long quantity, Long storageId, String storageName, 
                              String productId, String productName, String unit, String productType, String productTypeName) {
        this.id = id;
        this.quantity = quantity;
        this.storageId = storageId;
        this.storageName = storageName;
        this.productId = productId;
        this.productName = productName;
        this.unit = unit;
        this.productType = productType;
        this.productTypeName = productTypeName;
    }

    // 기존 Object[] 변환용 생성자 (하위 호환성 유지)
    public ResponseStockSrhDTO(Object[] row) {
        this.id = ((Long) row[0]);
        this.quantity = ((Long) row[1]);
        this.storageId = ((Long) row[2]);
        this.storageName = ((String) row[3]);
        this.productId = ((String) row[4]);
        this.productName = ((String) row[5]);
        this.unit = ((String) row[6]);
        this.productType = ((String) row[7]);
        this.productTypeName = row.length > 8 ? ((String) row[8]) : "";
    }
}