package com.itwillbs.factron.repository.contract;

import com.itwillbs.factron.entity.Approval;
import com.itwillbs.factron.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByApproval(Approval approval);
}
