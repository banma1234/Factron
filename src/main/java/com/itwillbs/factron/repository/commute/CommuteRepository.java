package com.itwillbs.factron.repository.commute;

import com.itwillbs.factron.entity.CommuteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommuteRepository extends JpaRepository<CommuteHistory, Long> {


}
