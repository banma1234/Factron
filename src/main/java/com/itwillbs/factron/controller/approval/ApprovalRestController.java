package com.itwillbs.factron.controller.approval;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchApprovalDTO;
import com.itwillbs.factron.service.approval.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalRestController {
    private final ApprovalService approvalService;

    // 결재 조회
    @GetMapping("")
    public ResponseDTO<List<ResponseSearchApprovalDTO>> getApprovalsList(RequestSearchApprovalDTO requestSearchApprovalDTO) {
        try {
            return ResponseDTO.success(approvalService.getApprovalsList(requestSearchApprovalDTO));
        }
        catch (Exception e) {
            return ResponseDTO.fail(800,"조회된 결재가 없습니다.",approvalService.getApprovalsList(requestSearchApprovalDTO));
        }
    }

    @PutMapping("")
    public ResponseDTO<Void> updateApproval(@RequestBody RequestApprovalDTO requestApprovalDTO) {
        log.info("Received approval update DTO: {}", requestApprovalDTO);
        try {
            approvalService.updateApproval(requestApprovalDTO);
            return ResponseDTO.success("결재 성공",null);
        } catch (Exception e) {
            log.error("Approval update failed", e);
            return ResponseDTO.fail(800, "결재 실패", null);
        }
    }


}
