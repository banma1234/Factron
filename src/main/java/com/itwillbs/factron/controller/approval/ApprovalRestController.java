package com.itwillbs.factron.controller.approval;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.approval.ResponseApproval;
import com.itwillbs.factron.dto.approval.RequestApproval;
import com.itwillbs.factron.service.approval.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalRestController {
    private final ApprovalService approvalService;

    // 결재 조회
    @GetMapping("")
    public ResponseDTO<List<ResponseApproval>> getApprovalsList(RequestApproval requestApproval) {
        try {
            return ResponseDTO.success(approvalService.getApprovalsList(requestApproval));
        }
        catch (Exception e) {
            return ResponseDTO.fail(800,"조회된 결재가 없습니다.",approvalService.getApprovalsList(requestApproval));
        }
    }


}
