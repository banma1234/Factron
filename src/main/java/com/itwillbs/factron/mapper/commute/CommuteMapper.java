package com.itwillbs.factron.mapper.commute;

import com.itwillbs.factron.dto.commute.CommuteResponseDto;
import com.itwillbs.factron.dto.test.RequestTest;
import com.itwillbs.factron.dto.test.Test;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommuteMapper {

    // 출퇴근 기록을 조회하는 메서드
    List<CommuteResponseDto> selectCommuteHistories(Map<String, Object> params);


}
