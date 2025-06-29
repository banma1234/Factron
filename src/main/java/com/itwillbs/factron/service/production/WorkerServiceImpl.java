package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.ResponseWorkerDTO;
import com.itwillbs.factron.mapper.production.WorkerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkerServiceImpl implements WorkerService {

    private final WorkerMapper workerMapper;

    /*
    * 작업 가능한 사원 목록 조회
    * */
    @Override
    public List<ResponseWorkerDTO> getPossibleWorkerList() {
        return workerMapper.getPossibleWorkerList();
    }
}
