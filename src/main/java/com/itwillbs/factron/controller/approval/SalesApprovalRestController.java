package com.itwillbs.factron.controller.approval;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;
import com.itwillbs.factron.service.approval.SalesApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
