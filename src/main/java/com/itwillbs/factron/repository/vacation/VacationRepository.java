package com.itwillbs.factron.repository.vacation;

import com.itwillbs.factron.entity.VacationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRepository extends JpaRepository<VacationHistory, Integer> {
}
