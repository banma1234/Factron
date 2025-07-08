package com.itwillbs.factron.repository.storage;

import com.itwillbs.factron.entity.Outbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundRepository extends JpaRepository<Outbound, Long> {
    boolean existsByContractIdAndStatusCodeNot(Long ContractId, String statusCode);
}
