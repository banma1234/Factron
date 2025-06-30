package com.itwillbs.factron.mapper.production;

import com.itwillbs.factron.dto.production.ResponseWorkerDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkerMapper {

    /*
     * 작업자 목록 조회
     * */
    List<ResponseWorkerDTO> getWorkerList(Long orderId);
}
