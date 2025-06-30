package com.itwillbs.factron.repository.lot;

import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, String> {
    // Item 기준 LOT FIFO 조회
    List<Lot> findByItemAndQuantityGreaterThanOrderByCreatedAtAsc(Item item, Long quantity);

    // Material 기준 LOT FIFO 조회
    List<Lot> findByMaterialAndQuantityGreaterThanOrderByCreatedAtAsc(Material material, Long quantity);
}
