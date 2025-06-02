package com.itwillbs.factron.service.work;

import com.itwillbs.factron.dto.work.RequestWork;
import com.itwillbs.factron.dto.work.ResponseWork;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkServiceImpl implements WorkService {

    /*
    * 근무 목록 조회
    * */
    @Override
    public List<ResponseWork> getWorkList(RequestWork requestWork) {
        return List.of();
    }
}
