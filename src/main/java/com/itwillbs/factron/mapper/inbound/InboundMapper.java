package com.itwillbs.factron.mapper.inbound;

import com.itwillbs.factron.dto.inbound.RequestInboundCompleteDTO;
import com.itwillbs.factron.dto.inbound.RequestInboundDTO;
import com.itwillbs.factron.dto.inbound.RequestSearchInboundDTO;
import com.itwillbs.factron.dto.inbound.ResponseSearchInboundDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InboundMapper {
    List<ResponseSearchInboundDTO> getInboundsList(RequestSearchInboundDTO requestSearchInboundDTO);
    void updateInbound(RequestInboundCompleteDTO dto);
}
