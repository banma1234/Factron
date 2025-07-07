package com.itwillbs.factron.repository.storage;

import com.itwillbs.factron.dto.storage.ResponseStorageDTO;
import com.itwillbs.factron.entity.Storage;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    Optional<Storage> findByTypeCode(String typeCode);

    @Query("""
    SELECT new com.itwillbs.factron.dto.storage.ResponseStorageDTO(
        s.id, s.name, s.address, s.area, d.name
    )
    FROM Storage s
    JOIN DetailSysCode d ON s.typeCode = d.detailCode
    WHERE s.name LIKE %:name%
    """)
    List<ResponseStorageDTO> findByName(@Param("name") String name);
}
