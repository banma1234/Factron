package com.itwillbs.factron.repository.storage;

import com.itwillbs.factron.entity.Inbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundRepository extends JpaRepository<Inbound, Long> {
    boolean existsByPurchaseIdAndStatusCodeNot(Long purchaseId, String statusCode);
}
