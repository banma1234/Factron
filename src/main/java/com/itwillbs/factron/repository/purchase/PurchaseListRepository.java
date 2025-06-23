package com.itwillbs.factron.repository.purchase;

import com.itwillbs.factron.entity.PurchaseList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseListRepository extends JpaRepository<PurchaseList, Long> {
}
