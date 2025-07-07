package com.itwillbs.factron.service.process;


import com.itwillbs.factron.dto.process.RequestProcessHistDTO;
import com.itwillbs.factron.dto.process.RequestProcessHistStatDTO;
import com.itwillbs.factron.dto.process.ResponseProcessHistoryInfoDTO;
import com.itwillbs.factron.dto.process.ResponseProcessHistoryStatDTO;

import java.util.List;

public interface ProcessHistoryService {
    /**
     * 해당 작업지시의 공정 이력을 조회합니다.
     * @param workOrderId
     * @return List<ResponseProcessHistoryInfoDTO> {@link ResponseProcessHistoryInfoDTO}
     */
    List<ResponseProcessHistoryInfoDTO> getProcessHistoryList(String workOrderId);

    /**
     * 공정이력 목록들의 결과를 저장합니다.
     * @param requestDTO {@link RequestProcessHistDTO}
     */
    void updateProcessHistory(RequestProcessHistDTO requestDTO);

    /**
     * 해당 공정의 이동표준편차, 이동평균을 구합니다.
     * @param requestDTO {@link RequestProcessHistStatDTO}
     */
    List<ResponseProcessHistoryStatDTO> getProcessStat(RequestProcessHistStatDTO requestDTO);
}
