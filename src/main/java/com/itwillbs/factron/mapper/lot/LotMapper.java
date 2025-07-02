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

    List<Lot> getInboundLotById(RequestLotUpdateDTO target);

    void updateLotQuantity(Lot lot);

    List<LotTreeDTO> getDetailLotTreeById(String lotId);
}
