package com.itwillbs.factron.mapper.outbound;

import com.itwillbs.factron.dto.outbound.RequestOutboundCompleteDTO;
import com.itwillbs.factron.dto.outbound.RequestSearchOutboundDTO;
import com.itwillbs.factron.dto.outbound.ResponseSearchOutboundDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OutboundMapper {

    // 출고 전체 조회
    List<ResponseSearchOutboundDTO> getOutboundsList(RequestSearchOutboundDTO requestSearchOutboundDTO);

    // 출고 처리
    void updateOutbound(RequestOutboundCompleteDTO requestOutboundCompleteDTO);
}
