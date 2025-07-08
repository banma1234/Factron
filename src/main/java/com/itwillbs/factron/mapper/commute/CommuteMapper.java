package com.itwillbs.factron.mapper.commute;

import com.itwillbs.factron.dto.commute.RequestCommuteDTO;
import com.itwillbs.factron.dto.commute.ResponseCommuteDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommuteMapper {

    // 출퇴근 기록을 조회하는 메서드
    List<ResponseCommuteDTO> selectCommuteHistories(RequestCommuteDTO requestDto);
}
