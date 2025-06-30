package com.itwillbs.factron.dto.lotStructure;

import com.itwillbs.factron.dto.lot.RequestProcessLotDTO;
import com.itwillbs.factron.entity.Lot;

public interface LotStructureService {

    public Void CreateLotStructure(RequestProcessLotDTO reqProcessLotDTO, Lot lot);
}
