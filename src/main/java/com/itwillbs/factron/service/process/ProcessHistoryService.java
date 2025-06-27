package com.itwillbs.factron.service.process;


import com.itwillbs.factron.dto.process.ResponseProcessHistoryInfoDTO;

import java.util.List;

public interface ProcessHistoryService {
    List<ResponseProcessHistoryInfoDTO> getProcessHistoryList(String workOrderId);
}
