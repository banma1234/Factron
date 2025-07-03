package com.itwillbs.factron.mapper.production;

import com.itwillbs.factron.dto.production.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkOrderMapper {

    /*
     * 작업지시 목록 조회
     * */
    List<ResponseWorkOrderDTO> getWorkOrderList(RequestWorkOrderDTO requestWorkOrderDTO);

    /*
     * 작업지시 내릴 수 있는 제품 목록 조회
     * */
    List<ResponseWorkProdDTO> getWorkItemList(RequestWorkProdDTO requestWorkProdDTO);

    /*
     * 투입할 품목 목록 조회
     * */
    List<ResponseWorkProdDTO> getPossibleInputList(RequestWorkProdDTO requestWorkProdDTO);

    /*
     * 작업 가능한 사원 목록 조회
     * */
    List<ResponseWorkerDTO> getPossibleWorkerList();
}
