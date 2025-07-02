package com.itwillbs.factron.controller.process;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.process.RequestProcessHistDTO;
import com.itwillbs.factron.dto.process.ResponseProcessHistoryInfoDTO;
import com.itwillbs.factron.service.process.ProcessHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/process/history")
@RequiredArgsConstructor
@Log4j2
public class ProcessHistoryRestController {
    private final ProcessHistoryService processHistoryService;

    @GetMapping("/{workOrderId}")
    public ResponseDTO<List<ResponseProcessHistoryInfoDTO>> getProcessHistoryList(@PathVariable("workOrderId") String workOrderId) {
        log.info("Received workOrderId: {}", workOrderId);
        try{
            return ResponseDTO.success(processHistoryService.getProcessHistoryList(workOrderId));
        }catch (Exception e){
            return ResponseDTO.fail(800,e.getMessage(),null);
        }
    }

//    // 공정 이력 수정?
    @PutMapping("")
    public ResponseDTO<Void> updateProcessHistory(@RequestBody RequestProcessHistDTO requestDTO){
        log.info("Received updateProcessHistory DTO: {}", requestDTO);
        try{
            processHistoryService.updateProcessHistory(requestDTO);
            return ResponseDTO.success(null);
        }catch (Exception e){
            return ResponseDTO.fail(800,e.getMessage(),null);
        }
    }
}
