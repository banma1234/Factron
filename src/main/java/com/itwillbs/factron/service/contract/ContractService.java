package com.itwillbs.factron.service.contract;

import com.itwillbs.factron.dto.contract.RequestRegisterContractDTO;
import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseContractItemDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;

import java.util.List;

public interface ContractService {

    // 수주 전체 조회
    List<ResponseSearchContractDTO> getContractsList(RequestSearchContractDTO requestSearchContractDTO);

    // 수주 품목 목록 조회
    List<ResponseContractItemDTO> getContractItemsByContractId(Long contractId);

    // 수주 상세 조회
    ResponseSearchContractDTO getContractDetailByContractId(Long contractId);

    // 수주 등록
    void registerContract(RequestRegisterContractDTO dto);

    // 수주 취소
    void cancelContract(Long approvalId);
}
