package com.itwillbs.factron.mapper.production;

import com.itwillbs.factron.dto.production.RequestWorkOrderDTO;
import com.itwillbs.factron.dto.production.ResponseWorkOrderDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkOrderMapper {

    /*
     * 작업지시 목록 조회
     * */
    List<ResponseWorkOrderDTO> getWorkOrderList(RequestWorkOrderDTO requestWorkOrderDTO);
}
