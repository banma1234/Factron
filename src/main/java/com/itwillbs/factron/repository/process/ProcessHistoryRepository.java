package com.itwillbs.factron.repository.process;

import com.itwillbs.factron.dto.process.ResponseProcessHistoryInfoDTO;
import com.itwillbs.factron.entity.ProcessHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessHistoryRepository extends JpaRepository<ProcessHistory, Long> {
    @Query("""
        SELECT
           new com.itwillbs.factron.dto.process.ResponseProcessHistoryInfoDTO(
               CAST(ph.id AS string),
               CAST(p.id AS string),
               p.name,
               CAST(ph.inputQuantity AS Long),
               CAST(ph.outputQuantity AS Long),
               CAST(ph.lot.id AS string),
               CAST(wo.id AS string),
               wo.startDate,
               ph.startTime,
               CAST(ph.coastTime AS Long),
               ph.statusCode,
               dsc.name,
               i.unit,
               unit.name,
               i.name,
               wo.quantity,
               dscs.detailCode,
               dscs.name
           )
       FROM ProcessHistory ph
       JOIN ph.workOrder wo
       JOIN wo.item i
       JOIN ph.process p
       JOIN DetailSysCode dsc ON ph.statusCode = dsc.detailCode
       JOIN DetailSysCode dscs ON p.typeCode = dscs.detailCode
       JOIN DetailSysCode unit ON i.unit = unit.detailCode
       WHERE wo.id = :workOrderId
    """)
    List<ResponseProcessHistoryInfoDTO> findProcessHistoriesByWorkOrderId(@Param("workOrderId") String workOrderId);

    @Query("""
        SELECT COUNT(ph)
        FROM ProcessHistory ph
        WHERE ph.workOrder.id = :workOrderId
        AND ph.statusCode = 'STS003'
    """)
    Integer countCompletedProcessHistoriesByWorkOrderId(@Param("workOrderId") String workOrderId);
}