package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.RequestWorkOrderDTO;
import com.itwillbs.factron.dto.production.RequestWorkProdDTO;
import com.itwillbs.factron.dto.production.ResponseWorkOrderDTO;
import com.itwillbs.factron.dto.production.ResponseWorkProdDTO;

import java.util.List;

public interface WorkOrderService {
    List<ResponseWorkOrderDTO> getWorkOrderList(RequestWorkOrderDTO requestWorkOrderDTO);

    List<ResponseWorkProdDTO> getWorkItemList(RequestWorkProdDTO requestWorkProdDTO);

    List<ResponseWorkProdDTO> getInputProdList(RequestWorkProdDTO requestWorkProdDTO);

    Void registWorkOrder(RequestWorkOrderDTO requestWorkOrderDTO);
}
