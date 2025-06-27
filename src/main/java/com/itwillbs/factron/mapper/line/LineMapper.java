package com.itwillbs.factron.mapper.line;

import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LineMapper {

    // 라인 목록 조회
    List<ResponseLineInfoDTO> selectLineList(RequestLineInfoDTO requestDto);
}
