package com.itwillbs.factron.repository.quality;

import com.itwillbs.factron.entity.QualityInspectionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityInspectionHistoryRepository extends JpaRepository<QualityInspectionHistory, Long> {
}
