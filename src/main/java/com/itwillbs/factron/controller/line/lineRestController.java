package com.itwillbs.factron.controller.line;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.line.RequestAddLineDTO;
import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.RequestUpdateLineDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;
import com.itwillbs.factron.service.line.lineService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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

        lineService.addLine(requestDto, empId);

        return ResponseDTO.success("라인을 추가하였습니다", null);
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
        } catch (Exception e) {

            return ResponseDTO.fail(500, "라인 수정 중 오류가 발생하였습니다", null);
        }
    }


}
