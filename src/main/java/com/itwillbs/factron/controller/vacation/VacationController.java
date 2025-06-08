package com.itwillbs.factron.controller.vacation;

import com.itwillbs.factron.dto.vacation.VacationResponseDTO;
import com.itwillbs.factron.service.vacation.VacationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/vacation")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;

    @GetMapping("")
    public String vacationGrid(
            Model model) {

        Long empId = 10001L; // 더미테스트
        LocalDate endDate = LocalDate.now(); // 오늘
        LocalDate startDate = endDate.minusYears(1); // 1년 전


        log.info("💡 휴가 목록 호출: empId={}, start={}, end={}", empId, startDate, endDate);

        List<VacationResponseDTO> vacations = vacationService.getMyVacations(empId, startDate, endDate);
        log.info("💡 휴가 목록 호출 결과: {}", vacations);
        model.addAttribute("vacations", vacations);
        model.addAttribute("empId", empId);

        return "vacation/vacation";
    }



    @GetMapping("/save")
    public String showVacationApply(Model model) {

        Long empId = 10001L;
        String empName = "홍길동";

        model.addAttribute("empId", empId);
        model.addAttribute("empName", empName);

        return "vacation/vacation-form";
    }
}