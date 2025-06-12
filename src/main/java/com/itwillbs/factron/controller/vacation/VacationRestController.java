package com.itwillbs.factron.controller.vacation;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.vacation.VacationRequestDTO;
import com.itwillbs.factron.dto.vacation.VacationResponseDTO;
import com.itwillbs.factron.service.vacation.VacationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@RestController
@RequestMapping("/api/vacation")
@RequiredArgsConstructor
public class VacationRestController {

    private final VacationService vacationService;

    //조건 검색
    @GetMapping()
    public ResponseDTO<List<VacationResponseDTO>> getMyVacations(VacationRequestDTO dto) {
        log.info("srhIdOrName 확인 :{}", dto.getSrhIdOrName());
        try {
            return ResponseDTO.success(vacationService.getMyVacations(dto));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "휴가 목록 조회에 실패했습니다.", vacationService.getMyVacations(dto));
        }
    }

    //휴가 신청
    @PostMapping
    public ResponseDTO<Void> requestVacation(@RequestBody VacationRequestDTO dto) {
        try {
            return ResponseDTO.success("휴가 결재 신청이 완료되었습니다!", vacationService.registVacation(dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (IllegalStateException ise) {
            return ResponseDTO.fail(802, ise.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "휴가 등록에 실패했습니다.", null);
        }
        }
    }


