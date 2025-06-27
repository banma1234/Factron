package com.itwillbs.factron.controller.qualityhistory;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestQualityHistoryInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestUpdateQualityHistoryListDTO;
import com.itwillbs.factron.dto.qualityhistory.ResponseQualityHistoryInfoDTO;
import com.itwillbs.factron.service.qualityhistory.QualityHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quality/history")
@RequiredArgsConstructor
public class QualityHistoryRestController {

    private final QualityHistoryService qualityHistoryService;

    // 작업 지시 별 품질검사 이력 목록 조회 API
    @GetMapping()
    public ResponseDTO<List<ResponseQualityHistoryInfoDTO>> getQualityHistoryList(
            @ModelAttribute RequestQualityHistoryInfoDTO requestDto) {

        return ResponseDTO.success(qualityHistoryService.getQualityHistoryList(requestDto));
    }

    // 작업 지시 별 품질검사 이력 결과 저장 API
    @PutMapping()
    public ResponseDTO<Void> updateQualityHistoryList(
            @RequestBody List<RequestUpdateQualityHistoryListDTO> requestDto) {

        qualityHistoryService.updateQualityHistoryList(requestDto);

        return ResponseDTO.success("품질 이력 검사 결과를 저장하였습니다", null);
    }
}
