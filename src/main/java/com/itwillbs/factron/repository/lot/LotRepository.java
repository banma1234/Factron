package com.itwillbs.factron.repository.lot;

import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, String> {
    // Item 기준 LOT FIFO 조회
    List<Lot> findByItemAndQuantityGreaterThanOrderByCreatedAtAsc(Item item, Long quantity);

    // Material 기준 LOT FIFO 조회
    List<Lot> findByMaterialAndQuantityGreaterThanOrderByCreatedAtAsc(Material material, Long quantity);

    Optional<List<Lot>> findByIdContaining(String lotId);

    Optional<List<Lot>> findByEventTypeOrderByCreatedAtDesc(String isp);
    
    // 날짜와 이벤트 타입으로 LOT 개수 조회
    @Query("SELECT COUNT(l) FROM Lot l WHERE l.id LIKE :dateToday || '-' || :eventType || '-%'")
    Long countByDateAndEventType(@Param("dateToday") String dateToday, 
                                @Param("eventType") String eventType);
}
