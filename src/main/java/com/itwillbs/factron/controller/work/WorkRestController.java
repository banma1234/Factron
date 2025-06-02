package com.itwillbs.factron.controller.work;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.work.RequestWork;
import com.itwillbs.factron.dto.work.ResponseWork;
import com.itwillbs.factron.service.work.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work")
public class WorkRestController {

    private final WorkService workService;

    /*
    * 근무 목록 조회
    * */
    @GetMapping()
    public ResponseDTO<List<ResponseWork>> getWorkList(RequestWork requestWork) {
        try {
            return ResponseDTO.success(workService.getWorkList(requestWork));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "근무 목록 조회에 실패했습니다.", workService.getWorkList(requestWork));
        }
    }
}
