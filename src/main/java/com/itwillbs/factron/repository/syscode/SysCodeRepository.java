package com.itwillbs.factron.repository.syscode;

import com.itwillbs.factron.entity.SysCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysCodeRepository extends JpaRepository<SysCode, Integer> {
    SysCode findByMainCode(String mainCode);
}
