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

    /*
     * BOM 등록 가능한 품목 목록 조회
     * */
    List<ResponseBOMDTO> getPossibleProdList(RequestBOMDTO requestBOMDTO);

    /*
     * 삭제할 BOM 모두 조회
     * */
    List<Long> findDelBomList(RequestBOMDTO requestBOMDTO);

    /*
     * BOM 하위 품목까지 모두 삭제
     * */
    void deleteBOM(List<Long> ids);
}
