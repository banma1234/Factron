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

    // 단일 결재 조회
    @GetMapping("/{approvalId}")
    public ResponseDTO<ResponseSearchApprovalDTO> getApproval(@PathVariable Long approvalId) {
        try {
            ResponseSearchApprovalDTO dto = approvalService.getApprovalById(approvalId);
            if (dto != null) {
                return ResponseDTO.success(dto);
            } else {
                return ResponseDTO.fail(404, "해당 결재를 찾을 수 없습니다.", null);
            }
        } catch (Exception e) {
            log.error("단일 결재 조회 중 오류:", e);
            return ResponseDTO.fail(500, "서버 오류가 발생했습니다.", null);
        }
    }


    // 결재(승인, 반려)
    @PutMapping("")
    public ResponseDTO<Void> updateApproval(@RequestBody RequestApprovalDTO requestApprovalDTO) {
        try {
            approvalService.updateApproval(requestApprovalDTO);
            return ResponseDTO.success("결재가 완료되었습니다!",null);
        } catch (Exception e) {
            return ResponseDTO.fail(800, "결재에 실패했습니다.", null);
        }
    }


}
