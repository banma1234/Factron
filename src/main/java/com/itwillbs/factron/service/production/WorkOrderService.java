package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.RequestWorkOrderDTO;
import com.itwillbs.factron.dto.production.ResponseWorkOrderDTO;

import java.util.List;

public interface WorkOrderService {
    List<ResponseWorkOrderDTO> getWorkOrderList(RequestWorkOrderDTO requestWorkOrderDTO);

    Void registWorkOrder(RequestWorkOrderDTO requestWorkOrderDTO);
}
