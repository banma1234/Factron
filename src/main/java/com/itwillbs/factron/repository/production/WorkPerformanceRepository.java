package com.itwillbs.factron.repository.production;

import com.itwillbs.factron.entity.WorkPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkPerformanceRepository extends JpaRepository<WorkPerformance, Long> {

    Optional<WorkPerformance> findByWorkOrderId(String workOrderId);
}
