package com.itwillbs.factron.dto.quality;

import lombok.Data;
import java.util.List;

@Data
public class RequestQualityStandardDeleteDTO {
    private List<DeleteItem> deleteList;
    
    @Data
    public static class DeleteItem {
        private Long inspectionId;
    }
} 