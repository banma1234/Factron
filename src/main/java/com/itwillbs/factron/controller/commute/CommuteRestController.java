package com.itwillbs.factron.controller.commute;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.service.commute.CommuteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commute")
public class CommuteRestController {

    private final CommuteService commuteService;

    // 출근
    @PostMapping()
    public ResponseDTO<Void> commuteIn(@RequestHeader("empId") String employeeId) {

        try {

            commuteService.commuteIn(employeeId);

            return ResponseDTO.success("출근이 완료되었습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, "해당 사원이 없습니다", null);
        } catch (IllegalArgumentException e) {

            return ResponseDTO.fail(801, "이미 출근한 상태입니다", null);
        }
    }

    // 퇴근
    @PutMapping()
    public ResponseDTO<Void> commuteOut(@RequestHeader("empId") String employeeId) {

        try {

            commuteService.commuteOut(employeeId);

            return ResponseDTO.success("퇴근이 완료되었습니다", null);
        } catch (EntityNotFoundException e) {

            return ResponseDTO.fail(800, "해당 사원이 없습니다", null);
        } catch (NoSuchElementException e) {

            return ResponseDTO.fail(801, "오늘 출근 기록이 없습니다", null);
        } catch (IllegalArgumentException e) {

            return ResponseDTO.fail(802, "이미 퇴근한 상태입니다", null);
        }
    }
}
