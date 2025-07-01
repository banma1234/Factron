package com.itwillbs.factron.repository.process;

import com.itwillbs.factron.entity.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {


    List<Process> findByLineId(Long lineId);
}
