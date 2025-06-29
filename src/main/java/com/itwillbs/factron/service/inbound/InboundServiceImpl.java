package com.itwillbs.factron.service.inbound;

import com.itwillbs.factron.dto.inbound.RequestInboundCompleteDTO;
import com.itwillbs.factron.dto.inbound.RequestSearchInboundDTO;
import com.itwillbs.factron.dto.inbound.ResponseSearchInboundDTO;
import com.itwillbs.factron.dto.lot.RequestInboundLotDTO;
import com.itwillbs.factron.entity.Inbound;
import com.itwillbs.factron.entity.Purchase;
import com.itwillbs.factron.entity.Stock;
import com.itwillbs.factron.entity.enums.LotType;
import com.itwillbs.factron.mapper.inbound.InboundMapper;
import com.itwillbs.factron.repository.lot.LotRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.product.MaterialRepository;
import com.itwillbs.factron.repository.purchase.PurchaseRepository;
import com.itwillbs.factron.repository.storage.InboundRepository;
import com.itwillbs.factron.repository.storage.StockRepository;
import com.itwillbs.factron.service.lot.LotCreateService;
import com.itwillbs.factron.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboundServiceImpl implements InboundService {
    private final InboundMapper inboundMapper;
    private final InboundRepository inboundRepository;
    private final PurchaseRepository purchaseRepository;
    private final StockRepository stockRepository;
    private final LotRepository lotRepository;
    private final MaterialRepository materialRepository;
    private final ItemRepository itemRepository;
    private final StorageService storageService;
    private final LotCreateService lotCreateService;


    @Override
    public List<ResponseSearchInboundDTO> getInboundsList(RequestSearchInboundDTO requestSearchInboundDTO){
        return inboundMapper.getInboundsList(requestSearchInboundDTO);
    }

    @Override
    @Transactional
    public void updateInbound(RequestInboundCompleteDTO dto) {
        List<RequestInboundLotDTO> lotList = new ArrayList<>();

        for (Long inboundId : dto.getInboundIds()) {
            Inbound inbound = inboundRepository.findById(inboundId)
                    .orElseThrow(() -> new IllegalArgumentException("입고 데이터를 찾을 수 없습니다: " + inboundId));

            // 1. 입고 상태 + 입고일자 변경
            inbound.updateStatus();

            // 2. 발주 상태 변경 (조건부)
            if (inbound.getPurchase() != null) {
                Long purchaseId = inbound.getPurchase().getId();

                // 발주ID에 입고 완료가 아닌 데이터가 존재하는지 확인
                boolean existsInboundNotCompleted = inboundRepository.existsByPurchaseIdAndStatusCodeNot(purchaseId, "STS003");

                if (!existsInboundNotCompleted) {
                    // 입고가 모두 완료되었을 때 발주 상태 완료로 변경
                    Purchase purchase = purchaseRepository.findById(purchaseId)
                            .orElseThrow(() -> new IllegalArgumentException("발주 데이터를 찾을 수 없습니다: " + purchaseId));
                    purchase.updateStatus("STP004");  // 발주 완료 상태로 변경
                    purchaseRepository.save(purchase);
                }
            }




            // 3. 재고 수량 추가 또는 신규 생성
            if (inbound.getItem() != null) {
                Stock stock = stockRepository.findByItemAndStorage(inbound.getItem(), inbound.getStorage()).orElse(null);
                if (stock != null) {
                    stock.addQuantity(inbound.getQuantity());
                } else {
                    Stock newStock = Stock.builder()
                            .item(inbound.getItem())
                            .material(null)
                            .storage(inbound.getStorage())
                            .quantity(inbound.getQuantity())
                            .build();
                    stockRepository.save(newStock);
                }
            } else if (inbound.getMaterial() != null) {
                Stock stock = stockRepository.findByMaterialAndStorage(inbound.getMaterial(), inbound.getStorage()).orElse(null);
                if (stock != null) {
                    stock.addQuantity(inbound.getQuantity());
                } else {
                    Stock newStock = Stock.builder()
                            .material(inbound.getMaterial())
                            .item(null)
                            .storage(inbound.getStorage())
                            .quantity(inbound.getQuantity())
                            .build();
                    stockRepository.save(newStock);
                }
            }

            // 4. LOT 리스트에 추가
            RequestInboundLotDTO lotDTO = new RequestInboundLotDTO(
                    inbound.getMaterial(),
                    inbound.getQuantity(),
                    LotType.INBOUND
            );
            lotList.add(lotDTO);
        }

        // 5. LOT 생성 서비스 호출 (한 번에)
        //lotCreateService.CreateInboundLot(lotList);
    }

}
