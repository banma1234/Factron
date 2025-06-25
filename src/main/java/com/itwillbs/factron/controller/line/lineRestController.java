package com.itwillbs.factron.controller.line;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.line.*;
import com.itwillbs.factron.service.line.lineService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/line")
@RequiredArgsConstructor
public class lineRestController {

    private final lineService lineService;

    // 라인 목록 조회 API
    @GetMapping()
    public ResponseDTO<List<ResponseLineInfoDTO>> getLineList(
            @ModelAttribute RequestLineInfoDTO requestDto) {

        return ResponseDTO.success(lineService.getLineList(requestDto));
    }

    // 라인 추가 API
    @PostMapping()
    public ResponseDTO<Void> addLine(@RequestHeader("empId") Long empId,
                                     @RequestBody @Valid RequestAddLineDTO requestDto) {

        try {
            lineService.addLine(requestDto, empId);

            return ResponseDTO.success("라인을 추가하였습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, e.getMessage(), null);
        } catch (IllegalArgumentException e) {

            return ResponseDTO.fail(801, e.getMessage(), null);
        } catch (IllegalStateException e) {

            return ResponseDTO.fail(802, e.getMessage(), null);
        }
    }

    // 라인 수정 API
    @PutMapping()
    public ResponseDTO<Void> updateLine(@RequestHeader("empId") Long empId,
                                        @RequestBody @Valid RequestUpdateLineDTO requestDto) {

        try {

            lineService.updateLine(requestDto, empId);

            return ResponseDTO.success("라인을 수정하였습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    // 라인에 공정 연결 API
    @PutMapping("/connect-process")
    public ResponseDTO<Void> connectProcessesToLine(@RequestHeader("empId") Long empId,
                                                  @RequestBody RequestConnectProcessesToLineDTO requestDto) {
        try {

            lineService.connectProcessesToLine(requestDto, empId);

            return ResponseDTO.success("공정을 라인에 연결하였습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, e.getMessage(), null);
        } catch (IllegalArgumentException e) {

            return ResponseDTO.fail(801, e.getMessage(), null);
        } catch (IllegalStateException e) {

            return ResponseDTO.fail(802, e.getMessage(), null);
        }
    }

    // 라인에서 공정 연결 해제 API
    @PutMapping("/disconnect-process")
    public ResponseDTO<Void> disconnectProcessesFromLine(@RequestHeader("empId") Long empId,
                                                       @RequestBody RequestDisconnectProcessesFromLineDTO requestDto) {
        try {

            lineService.disconnectProcessesFromLine(requestDto, empId);

            return ResponseDTO.success("공정을 라인에서 연결 해제하였습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, e.getMessage(), null);
        } catch (IllegalStateException e) {

            return ResponseDTO.fail(801, e.getMessage(), null);
        }
    }
}
