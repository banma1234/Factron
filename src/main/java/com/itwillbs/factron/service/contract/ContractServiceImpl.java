package com.itwillbs.factron.service.contract;

import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;
import com.itwillbs.factron.mapper.contract.ContractMapper;
import com.itwillbs.factron.repository.contract.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Override
    public List<ResponseSearchContractDTO> getContractsList(RequestSearchContractDTO requestSearchContractDTO){
        return contractMapper.getContractsList(requestSearchContractDTO);
    }
}
