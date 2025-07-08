package com.itwillbs.factron.repository.employee;

import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.IntergratAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntergratAuthRepository extends JpaRepository<IntergratAuth, Long> {
    Optional<IntergratAuth> findByEmployee(Employee targetEmp);
    Optional<IntergratAuth> findByEmployeeId(Long id);
}
