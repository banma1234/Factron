package com.itwillbs.factron.controller.quality;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.quality.ResponseInspSrhDTO;
import com.itwillbs.factron.service.quality.QualityInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quality")
@Log4j2
@RequiredArgsConstructor
public class QualityInspectionRestController {
    private final QualityInspectionService qualityInspectionService;

    //품질 검사 조회
    @GetMapping()
    public ResponseDTO<List<ResponseInspSrhDTO>> getInspections(@ModelAttribute RequestInspSrhDTO reqInspSrhDTO){
        try{
            return ResponseDTO.success(qualityInspectionService.getQualityInspections());
        } catch (Exception e) {
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }
    //품질 검사 추가

    //품질 검사 수정

    //제품별 품질 검사 조회

    //제품별 품질 검사 추가

    //제품별 품질 검사 수정

    //제품별 품질 검사 삭제
}
