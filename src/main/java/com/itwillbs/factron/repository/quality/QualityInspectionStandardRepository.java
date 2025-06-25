package com.itwillbs.factron.repository.quality;

import com.itwillbs.factron.entity.QualityInspection;
import com.itwillbs.factron.entity.QualityInspectionStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualityInspectionStandardRepository extends JpaRepository<QualityInspectionStandard, Long> {
    // 해당 품질 검사의 품질 검사 기준 리스트 검색
    List<QualityInspectionStandard> findByQualityInspectionId(Long qualityInspectionId);

    // 해당 제품의 같은 품질 검사 존재 여부
    boolean existsByItemIdAndQualityInspectionId(String itemId, Long qualityInspectionId);
}