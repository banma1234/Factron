package com.itwillbs.factron.controller.approval;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.approval.RequestSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;
import com.itwillbs.factron.service.approval.SalesApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/salesApproval")
@RequiredArgsConstructor
public class SalesApprovalRestController {
    private final SalesApprovalService salesApprovalService;

    @GetMapping("")
    public ResponseDTO<List<ResponseSearchSalesApprovalDTO>> getSalesApprovalsList(RequestSearchSalesApprovalDTO requestSearchSalesApprovalDTO){
        try {
            return ResponseDTO.success(salesApprovalService.getSalesApprovalsList(requestSearchSalesApprovalDTO));
        }
        catch (Exception e) {
            return ResponseDTO.fail(800,"조회된 결재가 없습니다.",salesApprovalService.getSalesApprovalsList(requestSearchSalesApprovalDTO));
        }
    }

    @GetMapping("/{approvalId}")
    public ResponseDTO<ResponseSearchSalesApprovalDTO> getSalesApprovalById(@PathVariable Long approvalId) {
        try {
            ResponseSearchSalesApprovalDTO approval = salesApprovalService.getSalesApprovalById(approvalId);
            if (approval == null) {
                return ResponseDTO.fail(404, "결재를 찾을 수 없습니다.", null);
            }
            return ResponseDTO.success(approval);
        } catch (Exception e) {
            log.error("단건 결재 조회 실패", e);
            return ResponseDTO.fail(500, "서버 오류가 발생했습니다.", null);
        }
    }


    @PutMapping("")
    public ResponseDTO<Void> updateSalesApproval(@RequestBody RequestSalesApprovalDTO requestSalesApprovalDTO){
        try{
            salesApprovalService.updateSalesApproval(requestSalesApprovalDTO);
            return ResponseDTO.success("결재가 완료되었습니다!",null);
        }catch (Exception e){
            return ResponseDTO.fail(800,"결재에 실패했습니다.",null);
        }
    }
}
