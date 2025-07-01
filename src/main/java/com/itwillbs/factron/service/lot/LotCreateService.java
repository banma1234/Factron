package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestInboundLotDTO;
import com.itwillbs.factron.dto.lot.RequestProcessLotDTO;
import com.itwillbs.factron.dto.lot.RequestQualityLotDTO;
import com.itwillbs.factron.entity.Lot;

import java.util.List;

public interface LotCreateService {

    public Void CreateInboundLot(List<RequestInboundLotDTO> reqInbound);

    public Lot CreateProcessLot(RequestProcessLotDTO reqInbound);

    public Lot CreateQualityLot(RequestQualityLotDTO reqInbound);
}
