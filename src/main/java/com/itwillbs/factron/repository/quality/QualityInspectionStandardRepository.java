package com.itwillbs.factron.repository.quality;

import com.itwillbs.factron.entity.QualityInspectionStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityInspectionStandardRepository extends JpaRepository<QualityInspectionStandard, Long> {
}
