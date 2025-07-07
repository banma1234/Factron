package com.itwillbs.factron.controller.workperformance;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.workperformance.RequestWorkPerformanceDTO;
import com.itwillbs.factron.dto.workperformance.ResponseWorkPerformanceDTO;
import com.itwillbs.factron.service.workperformance.WorkPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class WorkPerformanceRestController {

    private final WorkPerformanceService workPerformanceService;

    /*
     * 실적 목록 조회
     * */
    @GetMapping
    public ResponseDTO<List<ResponseWorkPerformanceDTO>> getWorkPerformanceList(RequestWorkPerformanceDTO dto) {
        try {
            return ResponseDTO.success(workPerformanceService.getWorkPerformanceList(dto));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "실적 목록 조회에 실패했습니다.", workPerformanceService.getWorkPerformanceList(dto));
        }
    }

    /*
     * 실적 등록
     * */
    @PostMapping
    public ResponseDTO<Void> registerPerformance(@RequestBody RequestWorkPerformanceDTO dto) {
        try {
            return ResponseDTO.success("실적 등록이 완료되었습니다!", workPerformanceService.registerPerformance(dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "실적 등록에 실패했습니다.", null);
        }
    }
}
