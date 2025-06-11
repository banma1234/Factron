package com.itwillbs.factron.controller.vacation;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.vacation.VacationRequestDTO;
import com.itwillbs.factron.dto.vacation.VacationResponseDTO;
import com.itwillbs.factron.service.vacation.VacationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Log4j2
@RestController
@RequestMapping("/api/vacation")
@RequiredArgsConstructor
public class VacationRestController {

    private final VacationService vacationService;

    //날짜 검색
    @GetMapping("/{startDate}&{endDate}")
    public ResponseDTO<List<VacationResponseDTO>> getMyVacations(
            @RequestHeader("empId") Long empId,
            @PathVariable LocalDate startDate,
            @PathVariable LocalDate endDate) {

        log.info("날짜 데이터 확인 시작={}, 끝={}", startDate, endDate);
        try {
            return ResponseDTO.success(vacationService.getMyVacations(empId, startDate, endDate));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "근무 목록 조회에 실패했습니다.", vacationService.getMyVacations(empId, startDate, endDate));
        }
    }

    //휴가 신청
    @PostMapping
    public ResponseDTO<Void> requestVacation(
            @RequestHeader("empId") Long empId,
            @RequestBody VacationRequestDTO dto) {

        try {
            log.info("💡 휴가 신청 요청: empId={}, start={}, end={}", empId, dto.getVacationStartDate(), dto.getVacationEndDate());
            return ResponseDTO.success("휴가 결재 신청이 완료되었습니다!", vacationService.registVacation(empId, dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (IllegalStateException ise) {
            return ResponseDTO.fail(802, ise.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "휴가 등록에 실패했습니다.", null);
        }
        }
    }


