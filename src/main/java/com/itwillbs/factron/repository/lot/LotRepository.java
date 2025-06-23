package com.itwillbs.factron.repository.lot;

import com.itwillbs.factron.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotRepository extends JpaRepository<Lot, String> {
}
