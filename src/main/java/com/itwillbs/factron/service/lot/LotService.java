package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;

import java.util.List;
import java.util.Map;

public interface LotService {

    public Long getLotSequence (Map<String, String> map);

    public Void updateInboundLotQuantity (List<RequestLotUpdateDTO> materialList);
}
