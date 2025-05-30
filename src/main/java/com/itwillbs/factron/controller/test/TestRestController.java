package com.itwillbs.factron.controller.test;

import com.itwillbs.factron.dto.test.RequestTest;
import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.test.Test;
import com.itwillbs.factron.service.test.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestRestController {

    private final TestService testService;

    /*
    * 테스트 목록 조회
    */
    @GetMapping("/testList")
    public ResponseDTO<List<Test>> getTestList(RequestTest srhTest) {
        try {
            return ResponseDTO.success(testService.getTestList(srhTest));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "퇴사 처리된 사원입니다.", testService.getTestList(srhTest));
        }
    }
}
