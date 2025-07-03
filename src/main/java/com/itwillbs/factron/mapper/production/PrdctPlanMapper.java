package com.itwillbs.factron.mapper.production;

import com.itwillbs.factron.dto.production.RequestPrdctPlanDTO;
import com.itwillbs.factron.dto.production.ResponsePrdctPlanDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PrdctPlanMapper {

    /*
     * 생산계획 목록 조회
     * */
    List<ResponsePrdctPlanDTO> getPrdctPlanList(RequestPrdctPlanDTO requestPrdctPlanDTO);
}
