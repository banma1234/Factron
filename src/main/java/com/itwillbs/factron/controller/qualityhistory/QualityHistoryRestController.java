package com.itwillbs.factron.controller.qualityhistory;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestQualityHistoryInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.ResponseQualityHistoryInfoDTO;
import com.itwillbs.factron.service.qualityhistory.QualityHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quality/history")
@RequiredArgsConstructor
public class QualityHistoryRestController {

    private final QualityHistoryService qualityHistoryService;

    // 품질 이력 목록 조회 API
    @GetMapping()
    public ResponseDTO<List<ResponseQualityHistoryInfoDTO>> getQualityHistoryList(
            @ModelAttribute RequestQualityHistoryInfoDTO requestDto ) {

        return ResponseDTO.success(qualityHistoryService.getQualityHistoryList(requestDto));
    }

}
