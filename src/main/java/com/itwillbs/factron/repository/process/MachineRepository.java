package com.itwillbs.factron.repository.process;

import com.itwillbs.factron.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
}
