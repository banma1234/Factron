package com.itwillbs.factron.controller.process;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;
import com.itwillbs.factron.dto.process.RequestAddProcessDTO;
import com.itwillbs.factron.dto.process.RequestProcessInfoDTO;
import com.itwillbs.factron.dto.process.RequestUpdateProcessDTO;
import com.itwillbs.factron.dto.process.ResponseProcessInfoDTO;
import com.itwillbs.factron.service.process.ProcessService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessRestController {

    private final ProcessService processService;

    // 공정 추가 API
    @PostMapping()
    public ResponseDTO<Void> addProcess(@RequestHeader("empId") Long empId,
                                        @RequestBody @Valid RequestAddProcessDTO requestDto) {

        processService.addProcess(requestDto, empId);

        return ResponseDTO.success("공정을 추가하였습니다", null);
    }

    // 공정 수정 API
    @PutMapping()
    public ResponseDTO<Void> updateProcess(@RequestHeader("empId") Long empId,
                                           @RequestBody @Valid RequestUpdateProcessDTO requestDto) {

        try {

            processService.updateProcess(requestDto, empId);

            return ResponseDTO.success("공정을 수정하였습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    // 공정 목록 조회 API
    @GetMapping()
    public ResponseDTO<List<ResponseProcessInfoDTO>> getLineList(
            @ModelAttribute RequestProcessInfoDTO requestDto) {

        return ResponseDTO.success(processService.getProcessList(requestDto));
    }
}
