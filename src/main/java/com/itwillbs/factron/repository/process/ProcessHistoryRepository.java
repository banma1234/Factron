package com.itwillbs.factron.repository.process;

import com.itwillbs.factron.entity.ProcessHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessHistoryRepository extends JpaRepository<ProcessHistory, Long> {
}
