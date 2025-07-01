package com.itwillbs.factron.controller.contract;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseContractItemDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;
import com.itwillbs.factron.service.contract.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractRestController {
    private final ContractService contractService;

    @GetMapping("")
    public ResponseDTO<List<ResponseSearchContractDTO>> getContractsList(RequestSearchContractDTO requestSearchContractDTO) {
        try{
            return ResponseDTO.success(contractService.getContractsList(requestSearchContractDTO));
        }
        catch (Exception e){
            return ResponseDTO.fail(800,"조회된 결과가 없습니다.",contractService.getContractsList(requestSearchContractDTO));
        }
    }

    @GetMapping("/{contractId}/items")
    public ResponseDTO<List<ResponseContractItemDTO>> getContractItems(@PathVariable Long contractId) {
        try{
            return  ResponseDTO.success(contractService.getContractItemsByContractId(contractId));
        } catch (Exception e) {
            return ResponseDTO.fail(800,"조회된 결과가 없습니다.",contractService.getContractItemsByContractId(contractId));
        }
    }

}
