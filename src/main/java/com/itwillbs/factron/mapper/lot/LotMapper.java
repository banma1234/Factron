package com.itwillbs.factron.mapper.lot;

import com.itwillbs.factron.dto.lot.LotTreeDTO;
import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.entity.Lot;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LotMapper {

    /**
     * LOT id 생성에 필요한 sequence 계산
     * */
    Long getLotSequence(Map<String, String> map);

    /**
     * (입고) id로 LOT 검색
     * */
    List<Lot> getInboundLotById(RequestLotUpdateDTO target);

    /**
     * 대상 LOT quantity 업데이트
     * */
    void updateLotQuantity(Lot lot);

    /**
     * 대상 LOT 트리 검색 (LOT + LOT_STRUCTURE)
     * */
    List<LotTreeDTO> getDetailLotTreeById(String lotId);
}
