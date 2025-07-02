package com.itwillbs.factron.repository.purchase;

import com.itwillbs.factron.entity.Approval;
import com.itwillbs.factron.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByApproval(Approval approval);
}
