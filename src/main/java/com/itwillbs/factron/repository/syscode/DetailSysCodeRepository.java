package com.itwillbs.factron.repository.syscode;

import com.itwillbs.factron.entity.DetailSysCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailSysCodeRepository extends JpaRepository<DetailSysCode, Long> {
    List<DetailSysCode> findBySysCode_Id(Long id);
}
