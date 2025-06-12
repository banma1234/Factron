package com.itwillbs.factron.repository.syscode;

import com.itwillbs.factron.dto.sys.RequestSysDetailDTO;
import com.itwillbs.factron.entity.DetailSysCode;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetailSysCodeRepository extends JpaRepository<DetailSysCode, Long> {
    Optional<List<DetailSysCode>> findByMainCode(String mainCode);

    Optional<DetailSysCode> findByDetailCode(String detailCode);
}
