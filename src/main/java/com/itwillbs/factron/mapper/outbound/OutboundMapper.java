package com.itwillbs.factron.mapper.outbound;

import com.itwillbs.factron.dto.outbound.RequestOutboundCompleteDTO;
import com.itwillbs.factron.dto.outbound.RequestSearchOutboundDTO;
import com.itwillbs.factron.dto.outbound.ResponseSearchOutboundDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OutboundMapper {
    List<ResponseSearchOutboundDTO> getOutboundsList(RequestSearchOutboundDTO requestSearchOutboundDTO);
    void updateOutbound(RequestOutboundCompleteDTO requestOutboundCompleteDTO);
}
