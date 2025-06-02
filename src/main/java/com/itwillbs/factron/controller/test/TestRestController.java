package com.itwillbs.factron.controller.test;

import com.itwillbs.factron.dto.test.RequestTest;
import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.test.Test;
import com.itwillbs.factron.service.test.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/testList")
@RequiredArgsConstructor
public class TestRestController {

    private final TestService testService;

    /*
    * 테스트 목록 조회
    */
    @GetMapping()
    public ResponseDTO<List<Test>> getTestList(RequestTest srhTest) {
        try {
            return ResponseDTO.success(testService.getTestList(srhTest));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "퇴사 처리된 사원입니다.", testService.getTestList(srhTest));
        }
    }

    /*
     * 테스트 저장
     */
    @PostMapping()
    public ResponseDTO<Void> registTest(@RequestBody Test test) {
        log.info("registTest: {}", test);
        try {
            testService.registTest(test);
            return ResponseDTO.success("저장이 완료되었습니다!", null);
        } catch (Exception e) {
            return ResponseDTO.fail(800, "저장에 실패했습니다. 다시 시도해주세요.", null);
        }
    }
}
