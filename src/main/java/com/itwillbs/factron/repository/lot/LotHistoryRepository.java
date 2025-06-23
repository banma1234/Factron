package com.itwillbs.factron.repository.lot;

import com.itwillbs.factron.entity.LotHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotHistoryRepository extends JpaRepository<LotHistory, Long> {
}
