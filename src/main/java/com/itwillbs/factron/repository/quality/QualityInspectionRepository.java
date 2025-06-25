package com.itwillbs.factron.repository.quality;

import com.itwillbs.factron.dto.quality.ResponseQualityStandardDTO;
import com.itwillbs.factron.entity.QualityInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualityInspectionRepository extends JpaRepository<QualityInspection, Long> {
    // 이름 또는 ID로 검색
    @Query("SELECT q FROM QualityInspection q WHERE q.name LIKE %:searchTerm% OR CAST(q.id AS string) = :searchTerm")
    List<QualityInspection> findByNameOrIdContaining(@Param("searchTerm") String searchTerm);
}
