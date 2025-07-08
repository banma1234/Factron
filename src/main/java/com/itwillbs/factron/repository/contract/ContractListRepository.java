package com.itwillbs.factron.repository.contract;

import com.itwillbs.factron.entity.ContractList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractListRepository extends JpaRepository<ContractList, Long> {
}
