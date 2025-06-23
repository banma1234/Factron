package com.itwillbs.factron.service.contract;

import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;

import java.util.List;

public interface ContractService {
    List<ResponseSearchContractDTO> getContractsList(RequestSearchContractDTO requestSearchContractDTO);
}
