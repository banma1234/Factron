package com.itwillbs.factron.controller.production;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.production.ResponseWorkerDTO;
import com.itwillbs.factron.service.production.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/worker")
public class WorkerRestController {

    private final WorkerService workerService;

    /*
    * 작업 가능한 사원 목록 조회
    * */
    @GetMapping()
    public ResponseDTO<List<ResponseWorkerDTO>> getPossibleWorkerList() {
        try {
            return ResponseDTO.success(workerService.getPossibleWorkerList());
        } catch (Exception e) {
            return ResponseDTO.fail(800, "작업자 목록 조회에 실패했습니다.", workerService.getPossibleWorkerList());
        }
    }
}
