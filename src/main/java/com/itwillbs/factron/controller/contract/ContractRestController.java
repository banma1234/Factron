package com.itwillbs.factron.controller.contract;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.contract.RequestRegisterContractDTO;
import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseContractItemDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;
import com.itwillbs.factron.service.contract.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractRestController {
    private final ContractService contractService;

    // 수주 전체 조회
    @GetMapping("")
    public ResponseDTO<List<ResponseSearchContractDTO>> getContractsList(RequestSearchContractDTO requestSearchContractDTO) {
        try{
            return ResponseDTO.success(contractService.getContractsList(requestSearchContractDTO));
        }
        catch (Exception e){
            return ResponseDTO.fail(800,"조회된 결과가 없습니다.",contractService.getContractsList(requestSearchContractDTO));
        }
    }

    // 수주 품목 조회
    @GetMapping("/{contractId}/items")
    public ResponseDTO<List<ResponseContractItemDTO>> getContractItems(@PathVariable Long contractId) {
        try{
            return  ResponseDTO.success(contractService.getContractItemsByContractId(contractId));
        } catch (Exception e) {
            return ResponseDTO.fail(800,"조회된 결과가 없습니다.",contractService.getContractItemsByContractId(contractId));
        }
    }

    // 수주 상세 조회
    @GetMapping("/{contractId}")
    public ResponseDTO<ResponseSearchContractDTO> getContractDetails(@PathVariable Long contractId) {
        try {
            ResponseSearchContractDTO dto = contractService.getContractDetailByContractId(contractId);
            return ResponseDTO.success(dto);
        } catch (Exception e) {
            return ResponseDTO.fail(800,"상세 조회에 실패했습니다.", null);
        }
    }

    // 수주 등록
    @PostMapping("")
    public ResponseDTO<Void> registerContract(@RequestBody RequestRegisterContractDTO requestRegisterContractDTO) {
        try {
            contractService.registerContract(requestRegisterContractDTO);
            return ResponseDTO.success("등록이 완료되었습니다.", null);
        } catch (Exception e) {
            return ResponseDTO.fail(800,"수주 등록에 실패했습니다.",null);
        }
    }

    // 수주 취소
    @PutMapping("/cancel")
    public ResponseDTO<Void> cancelContract(@RequestParam Long approvalId) {
        try{
            contractService.cancelContract(approvalId);
            return ResponseDTO.success("수주가 취소되었습니다.", null);
        } catch (Exception e) {
            return ResponseDTO.fail(800,"수주 취소에 실패했습니다.",null);
        }
    }
}
