package com.itwillbs.factron.repository.commute;

import com.itwillbs.factron.entity.CommuteHistory;
import com.itwillbs.factron.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CommuteRepository extends JpaRepository<CommuteHistory, Long> {

    /**
     * 특정 날짜에 해당하는 출퇴근 기록을 조회합니다.
     * @param employee
     * @param start
     * @param end
     * @return
     */
    Optional<CommuteHistory> findByEmployeeAndCommuteInBetween(Employee employee, LocalDateTime start, LocalDateTime end);
}
