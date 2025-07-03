package com.itwillbs.factron.service.contract;

import com.itwillbs.factron.dto.contract.RequestRegisterContractDTO;
import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseContractItemDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;

import java.util.List;

public interface ContractService {
    List<ResponseSearchContractDTO> getContractsList(RequestSearchContractDTO requestSearchContractDTO);
    // ✅ 수주 ID로 품목 목록 조회
    List<ResponseContractItemDTO> getContractItemsByContractId(Long contractId);

    ResponseSearchContractDTO getContractDetailByContractId(Long contractId);

    void registerContract(RequestRegisterContractDTO dto);

    void cancelContract(Long approvalId);
}
