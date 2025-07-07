package com.itwillbs.factron.mapper.process;

import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;
import com.itwillbs.factron.dto.process.RequestProcessInfoDTO;
import com.itwillbs.factron.dto.process.ResponseProcessInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcessMapper {

    // 공정 목록 조회
    List<ResponseProcessInfoDTO> selectProcessList(RequestProcessInfoDTO requestProcessInfoDTO);
}
