package com.itwillbs.factron.mapper.contract;

import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseContractItemDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContractMapper {

    // 수주 전체 조회
    List<ResponseSearchContractDTO> getContractsList(RequestSearchContractDTO requestSearchContractDTO);

    // 수주 품목 목록 조회
    List<ResponseContractItemDTO> getContractItemsByContractId(Long contractId);

    // 수주 상세 조회
    ResponseSearchContractDTO getContractDetailByContractId(Long contractId);
}
