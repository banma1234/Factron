package com.itwillbs.factron.controller.work;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.work.RequestWorkDTO;
import com.itwillbs.factron.dto.work.ResponseWorkDTO;
import com.itwillbs.factron.service.work.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work")
public class WorkRestController {

    private final WorkService workService;

    /*
    * 근무 목록 조회
    * */
    @GetMapping()
    public ResponseDTO<List<ResponseWorkDTO>> getWorkList(RequestWorkDTO requestWorkDTO) {
        try {
            return ResponseDTO.success(workService.getWorkList(requestWorkDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "근무 목록 조회에 실패했습니다.", workService.getWorkList(requestWorkDTO));
        }
    }

    /*
     * 근무 등록 (결재 추가)
     * */
    @PostMapping()
    public ResponseDTO<Void> registWork(@RequestBody RequestWorkDTO requestWorkDTO) {
        try {
            return ResponseDTO.success("근무 등록 결재 신청이 완료되었습니다!", workService.registWork(requestWorkDTO));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (IllegalArgumentException iae) {
            return ResponseDTO.fail(801, iae.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(802, "근무 등록에 실패했습니다.", null);
        }
    }
}
