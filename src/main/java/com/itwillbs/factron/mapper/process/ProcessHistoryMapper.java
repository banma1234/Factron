package com.itwillbs.factron.mapper.process;

import com.itwillbs.factron.dto.process.ResponseProcessHistoryStatDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProcessHistoryMapper {

    // 공정별 통계 조회 (Oracle 윈도우 함수 사용)
    List<ResponseProcessHistoryStatDTO> selectProcessStatByProcessNameOrId(
            @Param("processNameOrId") String processNameOrId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}