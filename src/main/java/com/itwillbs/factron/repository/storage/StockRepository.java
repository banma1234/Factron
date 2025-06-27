package com.itwillbs.factron.repository.storage;

import com.itwillbs.factron.dto.stock.ResponseStockSrhDTO;
import com.itwillbs.factron.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    // 창고 번호, 제품 번호, 제품명, 제품 종류로 검색
    @Query(value = """
        SELECT 
            new com.itwillbs.factron.dto.stock.ResponseStockSrhDTO(
                s.id,
                s.quantity,
                s.storage.id,
                st.name,
                m.id,
                m.name,
                m.unit,
                'ITP001',
                cd.name
            )
        FROM Stock s
        JOIN s.material m
        JOIN s.storage st
        JOIN DetailSysCode cd ON 'ITP001' = cd.detailCode
        WHERE s.material IS NOT NULL
        AND (:srhIdOrName IS NULL OR m.id LIKE %:srhIdOrName% OR m.name LIKE %:srhIdOrName%)
        AND (:storageId IS NULL OR s.storage.id = :storageId)
        AND (:productTypeCode IS NULL OR 'ITP001' = :productTypeCode)
        
        UNION ALL
        
        SELECT 
            new com.itwillbs.factron.dto.stock.ResponseStockSrhDTO(
                s.id,
                s.quantity,
                s.storage.id,
                st.name,
                i.id,
                i.name,
                i.unit,
                i.typeCode,
                cd.name
            )
        FROM Stock s
        JOIN s.item i
        JOIN s.storage st
        JOIN DetailSysCode cd ON i.typeCode = cd.detailCode
        WHERE s.item IS NOT NULL
        AND (:srhIdOrName IS NULL OR i.id LIKE %:srhIdOrName% OR i.name LIKE %:srhIdOrName%)
        AND (:storageId IS NULL OR s.storage.id = :storageId)
        AND (:productTypeCode IS NULL OR i.typeCode = :productTypeCode)
        """)
    List<ResponseStockSrhDTO> findStockWithProductInfo(@Param("srhIdOrName") String srhIdOrName, 
                                                       @Param("storageId") Long storageId, 
                                                       @Param("productTypeCode") String productTypeCode);

    //제품 번호로 품목 검색
    @Query("""
        SELECT 
            new com.itwillbs.factron.dto.stock.ResponseStockSrhDTO(
                s.id,
                s.quantity,
                s.storage.id,
                st.name,
                m.id,
                m.name,
                m.unit,
                'ITP001',
                cd.name
            )
        FROM Stock s
        JOIN s.material m
        JOIN s.storage st
        JOIN DetailSysCode cd ON 'ITP001' = cd.detailCode
        WHERE s.material IS NOT NULL
        AND (m.id = :productId)
        
        UNION ALL
        
        SELECT 
            new com.itwillbs.factron.dto.stock.ResponseStockSrhDTO(
                s.id,
                s.quantity,
                s.storage.id,
                st.name,
                i.id,
                i.name,
                i.unit,
                i.typeCode,
                cd.name
            )
        FROM Stock s
        JOIN s.item i
        JOIN s.storage st
        JOIN DetailSysCode cd ON i.typeCode = cd.detailCode
        WHERE s.item IS NOT NULL
        AND (i.id = :productId)
    """)
    List<ResponseStockSrhDTO> findSingleStock(@Param("productId") String productId);
}