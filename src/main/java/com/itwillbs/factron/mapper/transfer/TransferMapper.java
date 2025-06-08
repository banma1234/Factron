package com.itwillbs.factron.mapper.transfer;

import com.itwillbs.factron.dto.transfer.RequestTransferDTO;
import com.itwillbs.factron.dto.transfer.ResponseTransferDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TransferMapper {

    // 인사발령 목록 조회
    List<ResponseTransferDTO> getTransferList(RequestTransferDTO requestTransferDTO);

    // 인사발령 중복 조회
    boolean existsPendingTransfer(Map<String, Object> params);
}
