package com.itwillbs.factron.service.workperformance;

import com.itwillbs.factron.dto.workperformance.RequestWorkPerformanceDTO;
import com.itwillbs.factron.dto.workperformance.ResponseWorkPerformanceDTO;

import java.util.List;


public interface WorkPerformanceService {

    List<ResponseWorkPerformanceDTO> getWorkPerformanceList(RequestWorkPerformanceDTO dto);

    Void registerPerformance(RequestWorkPerformanceDTO dto);
}
