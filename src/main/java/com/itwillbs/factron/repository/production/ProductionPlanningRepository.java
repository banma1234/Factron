package com.itwillbs.factron.repository.production;

import com.itwillbs.factron.entity.ProductionPlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionPlanningRepository extends JpaRepository<ProductionPlanning, String> {
}
