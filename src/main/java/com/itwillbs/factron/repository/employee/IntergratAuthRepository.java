package com.itwillbs.factron.repository.employee;

import com.itwillbs.factron.entity.IntergratAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntergratAuthRepository extends JpaRepository<IntergratAuth, Integer> {
}
