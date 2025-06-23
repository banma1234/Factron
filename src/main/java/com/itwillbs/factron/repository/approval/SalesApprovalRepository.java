package com.itwillbs.factron.repository.approval;

import com.itwillbs.factron.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesApprovalRepository extends JpaRepository<Approval, Long> {
}
