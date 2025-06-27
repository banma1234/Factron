package com.itwillbs.factron.service.process;

import com.itwillbs.factron.dto.process.ResponseProcessHistoryInfoDTO;
import com.itwillbs.factron.entity.ProcessHistory;
import com.itwillbs.factron.repository.process.ProcessHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProcessHistoryServiceImpl implements ProcessHistoryService {
    private final ProcessHistoryRepository processHistoryRepository;
    @Override
    public List<ResponseProcessHistoryInfoDTO> getProcessHistoryList(String workOrderId) {
        List<ResponseProcessHistoryInfoDTO> processHistoryList = processHistoryRepository.findProcessHistoriesByWorkOrderId(workOrderId);
        log.info("getProcessHistoryList : {}", processHistoryList);
        return processHistoryList;
    }
}
