package com.itwillbs.factron.repository.production;

import com.itwillbs.factron.entity.ProductionPlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionPlanningRepository extends JpaRepository<ProductionPlanning, String> {

    /*
    * 생산계획 번호 생성용 카운팅
    * */
    long countByIdStartingWith(String prefix);
}
