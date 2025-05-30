package com.itwillbs.factron.controller.commute;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.service.commute.CommuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commute")
public class CommuteRestController {

    private final CommuteService commuteService;

    // 출근
    @PostMapping()
    public ResponseDTO<Void> commuteIn(@RequestHeader("empId") String employeeId) {

        commuteService.commuteIn(employeeId);

//        try {
//            return ResponseDTO.success(null);
//        } catch (Exception e) {
//            return ResponseDTO.fail(800, "퇴사 처리된 사원입니다.", testService.getTestList(srhTest));
//        }

        return ResponseDTO.success("출근이 완료되었습니다", null);
    }
}
