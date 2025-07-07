package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.RequestPrdctPlanDTO;
import com.itwillbs.factron.dto.production.ResponsePrdctPlanDTO;

import java.util.List;

public interface PrdctPlanService {
    List<ResponsePrdctPlanDTO> getPrdctPlanList(RequestPrdctPlanDTO requestPrdctPlanDTO);

    Void registPrdctPlan(RequestPrdctPlanDTO requestPrdctPlanDTO);
}
