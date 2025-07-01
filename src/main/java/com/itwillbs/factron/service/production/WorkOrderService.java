package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.*;

import java.util.List;

public interface WorkOrderService {
    List<ResponseWorkOrderDTO> getWorkOrderList(RequestWorkOrderDTO requestWorkOrderDTO);

    List<ResponseWorkProdDTO> getWorkItemList(RequestWorkProdDTO requestWorkProdDTO);

    List<ResponseWorkProdDTO> getInputProdList(RequestWorkProdDTO requestWorkProdDTO);

    List<ResponseWorkerDTO> getPossibleWorkerList();

    Void registWorkOrder(RequestWorkOrderDTO requestWorkOrderDTO);
}
