package com.itwillbs.factron.mapper.lot;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface LotMapper {

    /**
     * LOT id 생성에 필요한 sequence 계산
     * */
    Long getLotSequence(Map<String,String> map);
}
