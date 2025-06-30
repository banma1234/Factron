package com.itwillbs.factron.repository.storage;

import com.itwillbs.factron.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    Optional<List<Storage>> findByName(String name);

    Optional<Storage> findByTypeCode(String typeCode);
}
