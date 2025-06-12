package com.itwillbs.factron.service.vacation;


import com.itwillbs.factron.dto.vacation.VacationRequestDTO;
import com.itwillbs.factron.dto.vacation.VacationResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface VacationService {
    List<VacationResponseDTO> getMyVacations(VacationRequestDTO dto);
    Void registVacation(VacationRequestDTO dto);
}

