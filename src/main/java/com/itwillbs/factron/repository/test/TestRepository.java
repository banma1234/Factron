package com.itwillbs.factron.repository.test;

import com.itwillbs.factron.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Employee, Long> {
    // 삽입, 수정 시 사용
}
