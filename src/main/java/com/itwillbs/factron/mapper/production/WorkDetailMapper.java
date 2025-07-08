package com.itwillbs.factron.mapper.production;

import com.itwillbs.factron.dto.production.ResponseWorkProdDTO;
import com.itwillbs.factron.dto.production.ResponseWorkerDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkDetailMapper {

    /*
     * 작업자 목록 조회
     * */
    List<ResponseWorkerDTO> getWorkerList(String orderId);

    /*
     * 투입된 품목 목록 조회
     * */
    List<ResponseWorkProdDTO> getWorkProdList(String orderId);
}
