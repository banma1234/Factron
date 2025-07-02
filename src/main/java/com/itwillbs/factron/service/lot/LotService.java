package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.dto.lot.ResponseLotDTO;

import java.util.List;
import java.util.Map;

public interface LotService {

    public Long getLotSequence (Map<String, String> map);

    public List<ResponseLotDTO> getLotById(String lotId);

    public Void updateLotQuantity (List<RequestLotUpdateDTO> materialList);
}
