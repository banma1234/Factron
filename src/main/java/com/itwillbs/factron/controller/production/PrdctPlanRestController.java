package com.itwillbs.factron.controller.production;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.production.RequestPrdctPlanDTO;
import com.itwillbs.factron.dto.production.ResponsePrdctPlanDTO;
import com.itwillbs.factron.service.production.PrdctPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/production")
public class PrdctPlanRestController {

    private final PrdctPlanService prdctPlanService;

    /*
    * 생산계획 목록 조회
    * */
    @GetMapping()
    public ResponseDTO<List<ResponsePrdctPlanDTO>> getPrdctPlanList(RequestPrdctPlanDTO requestPrdctPlanDTO) {
        try {
            return ResponseDTO.success(prdctPlanService.getPrdctPlanList(requestPrdctPlanDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "생산계획 목록 조회에 실패했습니다.", prdctPlanService.getPrdctPlanList(requestPrdctPlanDTO));
        }
    }

    /*
     * 생산계획 등록
     * */
    @PostMapping()
    public ResponseDTO<Void> registPrdctPlan(@RequestBody RequestPrdctPlanDTO requestPrdctPlanDTO) {
        try {
            return ResponseDTO.success("생산계획 등록이 완료되었습니다!", prdctPlanService.registPrdctPlan(requestPrdctPlanDTO));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "생산계획 등록에 실패했습니다.", null);
        }
    }
}
