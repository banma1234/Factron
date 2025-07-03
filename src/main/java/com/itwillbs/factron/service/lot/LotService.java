package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.dto.lot.ResponseLotDTO;
import com.itwillbs.factron.dto.lot.ResponseLotTreeDTO;

import java.util.List;
import java.util.Map;

public interface LotService {

    Long getLotSequence (Map<String, String> map);

    List<ResponseLotDTO> getLotById(String lotId);

    Void updateLotQuantity (List<RequestLotUpdateDTO> materialList);

    ResponseLotTreeDTO getLotTreeById(String lotId);
}
