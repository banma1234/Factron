package com.itwillbs.factron.repository.process;

import com.itwillbs.factron.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {
}
