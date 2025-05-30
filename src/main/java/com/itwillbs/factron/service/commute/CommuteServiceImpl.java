package com.itwillbs.factron.service.commute;

import com.itwillbs.factron.repository.commute.CommuteRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommuteServiceImpl implements CommuteService {

    private final CommuteRepository commuteRepository;

    @Override
    @Transactional
    public void commuteIn(String employeeId) {
        // 출근 처리 로직 구현
        log.info("출근 처리: employeeId={}", employeeId);

        commuteRepository
            .findById(Long.parseLong(employeeId)) // Assuming employeeId is convertible to Long
            .ifPresentOrElse(
                commuteHistory -> {
                    // 출근 처리 로직
                    log.info("출근 기록이 이미 존재합니다: {}", commuteHistory);
                },
                () -> {
                    // 새로운 출근 기록 생성 로직
                    log.info("새로운 출근 기록 생성: employeeId={}", employeeId);
                    // 예시로 CommuteHistory 엔티티를 생성하고 저장하는 로직을 추가할 수 있습니다.
                }
            );


    }
}
