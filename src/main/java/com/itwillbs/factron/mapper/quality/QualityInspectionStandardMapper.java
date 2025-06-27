package com.itwillbs.factron.mapper.quality;

import com.itwillbs.factron.dto.quality.RequestInspStdSrhDTO;
import com.itwillbs.factron.dto.quality.ResponseQualityStandardDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QualityInspectionStandardMapper {
    List<ResponseQualityStandardDTO> getQualityInspectionByIdOrName(RequestInspStdSrhDTO requestDTO);
}
