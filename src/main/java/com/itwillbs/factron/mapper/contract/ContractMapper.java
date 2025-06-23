package com.itwillbs.factron.mapper.contract;

import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContractMapper {
    List<ResponseSearchContractDTO> getContractsList(RequestSearchContractDTO requestSearchContractDTO);
}
