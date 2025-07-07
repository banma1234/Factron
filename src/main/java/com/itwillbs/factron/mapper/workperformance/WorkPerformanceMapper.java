package com.itwillbs.factron.mapper.workperformance;

import com.itwillbs.factron.dto.workperformance.RequestWorkPerformanceDTO;
import com.itwillbs.factron.dto.workperformance.ResponseWorkPerformanceDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface WorkPerformanceMapper {

    /*
     * 실적 목록 조회
     * */
    List<ResponseWorkPerformanceDTO> getWorkPerformanceList(RequestWorkPerformanceDTO dto);

    /*
     * 전체 공정 종회
     * */
    int countTotalProcess(String workOrderId);

    /*
     * 완료된 공정 조회
     * */
    int countCompletedProcess(String workOrderId);
}
