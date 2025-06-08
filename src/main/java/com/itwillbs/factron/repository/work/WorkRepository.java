package com.itwillbs.factron.repository.work;

import com.itwillbs.factron.entity.WorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<WorkHistory, Long> {
}
