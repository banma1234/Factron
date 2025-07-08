package com.itwillbs.factron.repository.lot;

import com.itwillbs.factron.entity.LotStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotStructureRepository extends JpaRepository<LotStructure, Long> {
}
