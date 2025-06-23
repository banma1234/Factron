package com.itwillbs.factron.mapper.product;

import com.itwillbs.factron.dto.product.RequestBOMDTO;
import com.itwillbs.factron.dto.product.ResponseBOMDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BOMMapper {

    /*
     * BOM 목록 조회
     * */
    List<ResponseBOMDTO> getBOMList(RequestBOMDTO requestBOMDTO);
}
