package com.itwillbs.factron.repository.employee;

import com.itwillbs.factron.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    /**
     * 이번달 입사원수
     * @param yearMonth
     * @return long
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE FUNCTION('TO_CHAR', e.joinedDate, 'YYYY-MM') = :yearMonth")
    long countByYearMonth(@Param("yearMonth") String yearMonth);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
