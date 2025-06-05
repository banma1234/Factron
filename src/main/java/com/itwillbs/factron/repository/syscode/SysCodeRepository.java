package com.itwillbs.factron.repository.syscode;

import com.itwillbs.factron.entity.SysCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysCodeRepository extends JpaRepository<SysCode, Long> {
    List<SysCode> findByMainCode(String mainCode);
}
