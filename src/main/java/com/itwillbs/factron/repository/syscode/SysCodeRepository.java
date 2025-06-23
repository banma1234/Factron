package com.itwillbs.factron.repository.syscode;

import com.itwillbs.factron.entity.SysCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysCodeRepository extends JpaRepository<SysCode, Long> {
    Optional<List<SysCode>> findByMainCode(String mainCode);

    Optional<List<SysCode>> findByMainCodeContaining(String mainCode);
}
