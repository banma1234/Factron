package com.itwillbs.factron.repository.production;

import com.itwillbs.factron.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, String> {

    /*
     * 작업지시 번호 생성용 카운팅
     * */
    long countByIdStartingWith(String prefix);
}
