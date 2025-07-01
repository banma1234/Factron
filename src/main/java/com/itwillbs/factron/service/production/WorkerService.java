package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.ResponseWorkerDTO;

import java.util.List;

public interface WorkerService {

    List<ResponseWorkerDTO> getWorkerList(Long orderId);
}
