package com.itwillbs.factron.controller.production;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.production.ResponseWorkerDTO;
import com.itwillbs.factron.service.production.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workDtl")
public class WorkerRestController {

    private final WorkerService workerService;

    /*
    * 작업자 목록 조회
    * */
    @GetMapping()
    public ResponseDTO<List<ResponseWorkerDTO>> getWorkerList(Long orderId) {
        try {
            return ResponseDTO.success(workerService.getWorkerList(orderId));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "작업자 목록 조회에 실패했습니다.", workerService.getWorkerList(orderId));
        }
    }
}
