package com.itwillbs.factron.controller.quality;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.quality.*;
import com.itwillbs.factron.service.quality.QualityInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quality")
@Log4j2
@RequiredArgsConstructor
public class QualityInspectionRestController {
    private final QualityInspectionService qualityInspectionService;

    // 품질 검사 조회
    @GetMapping()
    public ResponseDTO<List<ResponseInspSrhDTO>> getInspections(@ModelAttribute RequestInspSrhDTO reqInspSrhDTO) {
        try {
            List<ResponseInspSrhDTO> result = qualityInspectionService.getQualityInspections(reqInspSrhDTO);
            return ResponseDTO.success(result);
        } catch (Exception e) {
            log.error("품질 검사 조회 중 오류 발생: ", e);
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    // 품질 검사 리스트 추가
    @PostMapping()
    public ResponseDTO<Void> addInspection(@RequestBody RequestQualityInspectionListDTO requestDTO) {
        try {
            qualityInspectionService.registQualityInspection(requestDTO.getNewInspection());
            return ResponseDTO.success(null);
        } catch (Exception e) {
            log.error("품질 검사 추가 중 오류 발생: ", e);
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    // 품질 검사 리스트 수정
    @PutMapping()
    public ResponseDTO<Void> updateInspection(@RequestBody RequestQualityInspectionUpdateDTO requestDTO) {
        try {
            qualityInspectionService.updateQualityInspection(requestDTO.getUpdateInspectionList());
            return ResponseDTO.success(null);
        } catch (Exception e) {
            log.error("품질 검사 수정 중 오류 발생: ", e);
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    // (품질 검사 또는 제품의) (아이디 혹은 이름)으로 제품별 품질 검사 기준 조회
    @GetMapping("/standard")
    public ResponseDTO<List<ResponseQualityStandardDTO>> getQualityStandards(@ModelAttribute RequestInspStdSrhDTO requestDTO) {
        try {
            List<ResponseQualityStandardDTO> result = qualityInspectionService.getQualityInspectionStandards(requestDTO);
            return ResponseDTO.success(result);
        } catch (Exception e) {
            log.error("제품별 품질 검사 기준 조회 중 오류 발생: ", e);
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }


    // 품질 검사 아이디로 품질 검사 기준 조회
    @GetMapping("/standard/{inspectionId}")
    public ResponseDTO<List<ResponseQualityStandardDTO>> getQualityStandardByQIId(@PathVariable("inspectionId") String inspectionId) {
        try {
            List<ResponseQualityStandardDTO> result = qualityInspectionService.getQualityInspectionStandardsByInspectionId(inspectionId);
            return ResponseDTO.success(result);
        } catch (Exception e) {
            log.error("제품별 품질 검사 기준 조회 중 오류 발생: ", e);
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }


    // 제품별 품질 검사 기준 리스트 추가
    @PostMapping("/standard")
    public ResponseDTO<Void> addQualityStandard(@RequestBody RequestQualityStandardListDTO requestDTO) {
        try {
            qualityInspectionService.registQualityInspectionStandard(requestDTO.getQualityInspectionStandard());
            return ResponseDTO.success(null);
        } catch (Exception e) {
            log.error("제품별 품질 검사 기준 추가 중 오류 발생: ", e);
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    // 제품별 품질 검사 기준 리스트 수정
    @PutMapping("/standard")
    public ResponseDTO<Void> updateQualityStandard(@RequestBody RequestQualityStandardListDTO requestDTO) {
        try {
            qualityInspectionService.updateQualityInspectionStandard(requestDTO.getQualityInspectionStandard());
            return ResponseDTO.success(null);
        } catch (Exception e) {
            log.error("제품별 품질 검사 기준 수정 중 오류 발생: ", e);
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    // 제품별 품질 검사 기준 리스트 삭제
    @DeleteMapping("/standard")
    public ResponseDTO<Void> deleteQualityStandard(@RequestBody RequestQualityStandardDeleteDTO requestDTO) {
        try {
            qualityInspectionService.deleteQualityInspectionStandard(requestDTO.getDeleteList());
            return ResponseDTO.success(null);
        } catch (Exception e) {
            log.error("제품별 품질 검사 기준 삭제 중 오류 발생: ", e);
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }
}
